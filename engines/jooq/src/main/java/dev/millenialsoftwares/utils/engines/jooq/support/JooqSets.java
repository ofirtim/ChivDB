package dev.millenialsoftwares.utils.engines.jooq.support;

import dev.ofirtim.chivdb.policy.InPolicy;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.impl.DSL;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class JooqSets {
    private final InPolicy policy;

    public JooqSets(InPolicy policy) {
        this.policy = policy;
    }

    public <T> Condition in(Field<T> f, Object v, Function<Object, T> coerce) {
        // Normalize to List<T>
        final List<T> vals = (v instanceof Collection<?> c)
                ? c.stream().map(coerce).collect(Collectors.toList())
                : List.of(coerce.apply(v));

        // Policy on empty
        if (vals.isEmpty()) {
            return switch (policy.emptyBehavior()) {
                case ALWAYS_FALSE -> DSL.falseCondition();
                case MATCH_ALL -> DSL.trueCondition();
                case ERROR -> throw new IllegalArgumentException("Empty IN not allowed");
            };
        }

        if (vals.size() == 1) return f.eq(vals.get(0));  // type-safe: T
        return f.in(vals);                                // type-safe: Collection<T>
    }
}
