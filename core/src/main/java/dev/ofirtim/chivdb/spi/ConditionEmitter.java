package dev.ofirtim.chivdb.spi;

import dev.ofirtim.chivdb.structs.ChivCondition;

public interface ConditionEmitter<E> {
    /** Emit an engine-native predicate from the core Condition AST. */
    E emit(ChivCondition c);
}