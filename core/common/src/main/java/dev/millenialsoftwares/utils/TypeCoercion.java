package dev.millenialsoftwares.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.regex.Pattern;

public final class TypeCoercion {
    private final Map<Class<?>, BiFunction<Object, Class<?>, Object>> adapters = new HashMap<>();
    private final boolean caseInsensitiveEnums;


    private TypeCoercion(boolean caseInsensitiveEnums) {
        this.caseInsensitiveEnums = caseInsensitiveEnums;
        register(Object.class, (v, t) -> v);
        register(String.class, (v, t) -> v == null ? null : v.toString());
        register(Integer.class, (v, t) -> v instanceof Number n ? n.intValue() : Integer.parseInt(v.toString()));
        register(Long.class, (v, t) -> v instanceof Number n ? n.longValue() : Long.parseLong(v.toString()));
        register(Short.class, (v, t) -> v instanceof Number n ? n.shortValue() : Short.parseShort(v.toString()));
        register(Byte.class, (v, t) -> v instanceof Number n ? n.byteValue() : Byte.parseByte(v.toString()));
        register(Double.class, (v, t) -> v instanceof Number n ? n.doubleValue() : Double.parseDouble(v.toString()));
        register(Float.class, (v, t) -> v instanceof Number n ? n.floatValue() : Float.parseFloat(v.toString()));
        register(BigDecimal.class, (v, t) -> v instanceof BigDecimal b ? b : new BigDecimal(v.toString()));
        register(BigInteger.class, (v, t) -> v instanceof BigInteger b ? b : new BigInteger(v.toString()));
        register(Boolean.class, (v, t) -> v instanceof Boolean b ? b : Boolean.valueOf(v.toString()));
        register(UUID.class, (v, t) -> {
            if (v instanceof UUID u) return u;
            if (v instanceof byte[] b && b.length == 16) {
                var bb = java.nio.ByteBuffer.wrap(b);
                return new UUID(bb.getLong(), bb.getLong());
            }
            return UUID.fromString(v.toString());
        });

        register(Instant.class, (v, t) -> {
            if (v instanceof Instant i) return i;
            if (v instanceof java.sql.Timestamp ts) return ts.toInstant();
            if (v instanceof java.util.Date d) return d.toInstant();
            if (v instanceof Number n) return Instant.ofEpochMilli(n.longValue());
            return Instant.parse(v.toString());
        });

        register(LocalDate.class, (v, t) -> {
            if (v instanceof LocalDate d) return d;
            if (v instanceof java.sql.Date d) return d.toLocalDate();
            return LocalDate.parse(v.toString());
        });

        register(Pattern.class, (v, t) -> v instanceof Pattern p ? p : Pattern.compile(v.toString()));
    }

    public static Builder builder() {
        return new Builder();
    }

    public <T> T coerce(Object value, Class<T> target) {
        if (value == null) return null;
        if (target.isInstance(value)) return target.cast(value);
        if (target.isEnum()) {
            String s = value.toString();
            for (Object c : target.getEnumConstants()) {
                var name = ((Enum<?>) c).name();
                if (caseInsensitiveEnums ? name.equalsIgnoreCase(s) : name.equals(s)) {
                    return target.cast(c);
                }
            }
            throw new IllegalArgumentException("Cannot coerce '" + s + "' to enum " + target.getName());
        }
        var adapter = adapters.get(target);
        if (adapter != null) return target.cast(adapter.apply(value, target));
        throw new IllegalArgumentException("No coercion from " + value.getClass().getName() + " to " + target.getName());
    }

    private <T> void register(Class<T> type, BiFunction<Object, Class<?>, Object> fn) {
        adapters.put(type, fn);
    }

    public static final class Builder {
        private boolean caseInsensitiveEnums = true;

        public Builder caseInsensitiveEnums(boolean b) {
            this.caseInsensitiveEnums = b;
            return this;
        }

        public TypeCoercion build() {
            return new TypeCoercion(caseInsensitiveEnums);
        }
    }
}