package dev.ofirtim.chivdb.policy;

import java.util.Collection;
import java.util.List;

public interface InPolicy {

    enum EmptyBehavior {
        ALWAYS_FALSE,
        MATCH_ALL,
        ERROR
    }

    EmptyBehavior emptyBehavior();

    default List<?> normalize(Object v) {
        return (v instanceof Collection<?> c) ? List.copyOf(c) : List.of(v);
    }

    static InPolicy defaultFalse() {
        return () -> EmptyBehavior.ALWAYS_FALSE;
    }
}