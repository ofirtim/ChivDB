package dev.ofirtim.chivdb.engines.jooq;


import dev.ofirtim.chivdb.PojoMapper;
import dev.ofirtim.chivdb.TypeCoercion;
import dev.ofirtim.chivdb.helpers.Normalizer;
import dev.ofirtim.chivdb.helpers.queries.RelationalApi;
import dev.ofirtim.chivdb.operators.*;
import dev.ofirtim.chivdb.structureless.*;
import org.jooq.*;
import org.jooq.Record;
import org.jooq.impl.DSL;

import java.util.*;
import java.util.stream.Collectors;

/**
 * RelationalApi implementation on top of jOOQ, using dynamic table/field names.
 */
public final class JooqRelationalApi implements RelationalApi {

    private final DSLContext ctx;
    private final PojoMapper mapper;
    private final TypeCoercion coercion;

    public JooqRelationalApi(DSLContext ctx, PojoMapper mapper, TypeCoercion coercion) {
        this.ctx = ctx;
        this.mapper = mapper;
        this.coercion = coercion;
    }

    @Override
    public RelQuery fetch(String table) {
        return new Q(ctx, table);
    }

    @Override
    public int insert(String table, Map<String, Object> values) {
        Table<Record> t = DSL.table(DSL.name(table));
        var entries = values
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .toList();
        List<Field<?>> fields = entries
                .stream()
                .map(e -> DSL.field(DSL.name(e.getKey())))
                .collect(Collectors.toUnmodifiableList());
        List<Object> vals = entries.stream().map(Map.Entry::getValue).toList();
        return ctx.insertInto(t).columns(fields).values(vals).execute();
    }

    @Override
    public int update(String table, Map<String, Object> values, ChivCondition where) {
        if (values == null || values.isEmpty()) throw new IllegalArgumentException("No values to update");
        Table<Record> t = DSL.table(DSL.name(table));
        Map<Field<Object>, Object> setMap = new LinkedHashMap<>();
        values.forEach((k, v) -> setMap.put(DSL.field(DSL.name(k)), v));
        return ctx.update(t).set(setMap).where(toJooq(where)).execute();
    }

    @Override
    public int delete(String table, ChivCondition where) {
        Table<Record> t = DSL.table(DSL.name(table));
        return ctx.deleteFrom(t).where(toJooq(where)).execute();
    }

    private ChivRow rowFrom(Record r) {
        Map<String, Object> m = new LinkedHashMap<>();
        for (int i = 0; i < r.size(); i++) m.put(r.field(i).getName(), r.get(i));
        return new ChivRow(m);
    }

    private Field<Object> f(Field<?> coreField) {
        return DSL.field(DSL.name(coreField.getName()));
    }

    private Field<Object> f(String name) {
        return DSL.field(DSL.name(name));
    }

    private Condition toJooq(ChivCondition c) {
        c = Normalizer.toNNF(c);
        if (c == null) return DSL.noCondition();
        if (c instanceof ChivAtom<?> a) return atomToJooq(a);
        if (c instanceof ChivJunction(boolean and, List<ChivCondition> parts)) {
            var parts = parts.stream().map(this::toJooq).toArray(Condition[]::new);
            return and ? DSL.and(parts) : DSL.or(parts);
        }
        if (c instanceof ChivNot(ChivCondition inner)) return DSL.not(toJooq(inner));
        throw new IllegalArgumentException("Unknown condition: " + c);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private Condition atomToJooq(ChivAtom<?> a) {
        ChivField<?> cf = a.chivField();
        Field f = DSL.field(DSL.name(cf.name()), cf.type());
        Object v = coercion.coerce(a.value(), f.getType());
        var op = a.op();

        if (op instanceof ComparisonOp cmp) {
            switch (cmp) {
                case EQUALS -> {
                    return f.eq(v);
                }
                case GREATER_THAN -> {
                    return f.gt(v);
                }
                case GREATER_THAN_OR_EQUAL -> {
                    return f.ge(v);
                }
                case LESS_THAN -> {
                    return f.lt(v);
                }
                case LESS_THAN_OR_EQUAL -> {
                    return f.le(v);
                }
            }
        }

        if (op instanceof SetOp set) {
            Collection<?> col = (v instanceof Collection<?> c) ? c : List.of(v);
            List<?> vals = col.stream().map(x -> coercion.coerce(x, f.getType())).toList();
            return switch (set) {
                case IN -> {
                    if (vals.isEmpty())      yield DSL.falseCondition();
                    if (vals.size() == 1)    yield f.eq(vals.get(0));
                    yield f.in(vals);
                }
            };
        }


        if (op instanceof NullOp n) {
            return switch (n) {
                case IS_NULL -> ((Field<?>) f).isNull();
                case EXISTS -> ((Field<?>) f).isNotNull();
            };
        }

        if (op instanceof StringOp s) {
            String sVal = Objects.toString(v, "");
            String esc = sVal.replace("\\","\\\\").replace("%","\\%").replace("_","\\_");
            return switch (s) {
                case LIKE -> ((Field<String>) f).like(DSL.val(esc)).escape('\\');
                case STARTS_WITH -> ((Field<String>) f).like(DSL.val(esc + "%")).escape('\\');
                case ENDS_WITH -> ((Field<String>) f).like(DSL.val("%" + esc)).escape('\\');
                case CONTAINS -> ((Field<String>) f).like(DSL.val("%" + esc + "%")).escape('\\');
                case MATCHES_REGEX -> regexCondition((Field<String>) f, sVal);
            };
        }

        if (op instanceof ArrayOp arr) {
            SQLDialect fam = ctx.dialect();
            boolean isPg = fam.family() == SQLDialect.POSTGRES.family();
            return switch (arr) {
                case ARRAY_CONTAINS -> {
                    if (isPg) {
                        Object ArrObj = (v instanceof Collection<?> c) ? c.toArray() : v;
                        yield DSL.condition("{0} @> {1}", f, DSL.val(ArrObj));
                    } else throw new UnsupportedOperationException("ARRAY_CONTAINS not supported on " + fam);
                }
                case ARRAY_CONTAINS_ANY -> {
                    if (isPg) {
                        Object ArrObj = (v instanceof Collection<?> c) ? c.toArray() : new Object[]{v};
                        yield DSL.condition("{0} && {1}", f, DSL.val(ArrObj));
                    }
                    throw new UnsupportedOperationException("ARRAY_CONTAINS_ANY not supported on " + fam);
                }
                case ARRAY_SIZE_EQUALS -> {
                    if (isPg && v instanceof Number n)
                        yield DSL.condition("cardinality({0}) = {1}", f, DSL.val(n.intValue()));
                    throw new UnsupportedOperationException("ARRAY_SIZE_EQUALS not supported on " + fam);
                }
            };
        }

        if (op instanceof GeoOp) {
            throw new UnsupportedOperationException("Geospatial ops are not supported in jOOQ adapter yet");
        }

        throw new IllegalArgumentException("Unhandled operator: " + op);
    }

    private Condition regexCondition(Field<String> field, String pattern) {
        SQLDialect fam = ctx.dialect();
        return switch (fam.family()) {
            case POSTGRES -> DSL.condition("{0} ~ {1}", field, DSL.inline(pattern));
            case MYSQL -> DSL.condition("{0} REGEXP {1}", field, DSL.inline(pattern));
            case SQLITE -> throw new UnsupportedOperationException("REGEXP not supported by default on SQLite");
            default -> throw new UnsupportedOperationException("Regex not supported for dialect: " + fam);
        };
    }

    @SuppressWarnings("unchecked")
    private Object coerceToFieldType(Field<?> jf, Object raw) {
        Class<Object> t = (Class<Object>) jf.getType();
        return coercion.coerce(raw, t);
    }

    @SuppressWarnings("unchecked")
    private <T> Field<T> jf(ChivField<T> cf) {
        return DSL.field(DSL.name(cf.name()), cf.type());
    }

    private final class Q implements RelationalApi.RelQuery {
        private final DSLContext ctx;
        private final String table;
        private final List<String> cols = new ArrayList<>();
        private ChivCondition where;
        private String orderCol;
        private boolean orderAsc = true;
        private Integer limit;
        private Integer offset;

        Q(DSLContext ctx, String table) {
            this.ctx = ctx;
            this.table = table;
        }

        @Override
        public RelationalApi.RelQuery select(ChivField<?>... fields) {
            for (ChivField<?> f : fields) cols.add(f.name());
            return this;
        }

        @Override
        public RelationalApi.RelQuery where(ChivCondition c) {
            this.where = c;
            return this;
        }

        @Override
        public RelationalApi.RelQuery orderBy(ChivField<?> column, boolean asc) {
            return orderBy(column.name(), asc);
        }

        @Override
        public RelationalApi.RelQuery limit(int n) {
            this.limit = n;
            return this;
        }

        @Override
        public RelationalApi.RelQuery offset(int n) {
            this.offset = n;
            return this;
        }

        @Override
        public List<ChivRow> list() {
            Table<Record> t = DSL.table(DSL.name(table));

            SelectSelectStep<? extends Record> sel = cols.isEmpty()
                    ? ctx.select()
                    : ctx.select(cols.stream()
                    .map(c -> DSL.field(DSL.name(c)))
                    .toArray(Field[]::new));

            Condition cond = (where != null)
                    ? JooqRelationalApi.this.toJooq(where)
                    : DSL.noCondition();

            List<SortField<?>> sorts = new ArrayList<>(1);
            if (orderCol != null) {
                sorts.add(orderAsc
                        ? DSL.field(DSL.name(orderCol)).asc()
                        : DSL.field(DSL.name(orderCol)).desc());
            }

            int lim = (limit != null) ? limit : Integer.MAX_VALUE;

            var result = sel
                    .from(t)
                    .where(cond)
                    .orderBy(sorts)
                    .limit(lim)
                    .fetch();

            return result.map(JooqRelationalApi.this::rowFrom);
        }


        @Override
        public Optional<ChivRow> one() {
            var rows = limit(1).list();
            return rows.isEmpty() ? Optional.empty() : Optional.of(rows.get(0));
        }

        @Override
        public <T> List<T> columnAs(String column, Class<T> type) {
            Table<Record> t = DSL.table(DSL.name(table));
            int off = (offset != null) ? offset : 0;
            Field<Object> col = DSL.field(DSL.name(column));

            Condition cond = (where != null) ? JooqRelationalApi.this.toJooq(where) : DSL.noCondition();

            List<SortField<?>> sorts = new ArrayList<>(1);
            if (orderCol != null) {
                sorts.add(orderAsc
                        ? DSL.field(DSL.name(orderCol)).asc()
                        : DSL.field(DSL.name(orderCol)).desc());
            }

            int lim = (limit != null) ? limit : Integer.MAX_VALUE;

            var result = ctx
                    .select(col)
                    .from(t)
                    .where(cond)
                    .orderBy(sorts)
                    .limit(lim)
                    .offset(off)
                    .fetch();

            return result.map(rec -> coercion.coerce(rec.value1(), type));
        }

        @Override
        public <T> List<T> columnAs(ChivField<T> column, Class<T> type) {
            return columnAs(column.name(), type);
        }

        @Override
        public <T> Optional<T> columnOneAs(String column, Class<T> type) {
            Table<Record> t = DSL.table(DSL.name(table));
            int off = (offset != null) ? offset : 0;
            Field<Object> col = DSL.field(DSL.name(column));

            Condition cond = (where != null) ? JooqRelationalApi.this.toJooq(where) : DSL.noCondition();

            List<SortField<?>> sorts = new ArrayList<>(1);
            if (orderCol != null) {
                sorts.add(orderAsc
                        ? DSL.field(DSL.name(orderCol)).asc()
                        : DSL.field(DSL.name(orderCol)).desc());
            }

            var rec = ctx
                    .select(col)
                    .from(t)
                    .where(cond)
                    .orderBy(sorts)
                    .limit(1)
                    .offset(off)
                    .fetchOne();

            return Optional.ofNullable(rec == null ? null : coercion.coerce(rec.value1(), type));
        }

        @Override
        public <T> Optional<T> columnOneAs(ChivField<T> column, Class<T> type) {
            return columnOneAs(column.name(), type);
        }

        @Override
        public <T> List<T> listAs(Class<T> type) {
            return list().stream().map(r -> mapper.map(r.values(), type)).toList();
        }

        @Override
        public <T> Optional<T> oneAs(Class<T> type) {
            return one().map(r -> mapper.map(r.values(), type));
        }
    }
}