package dev.millenialsoftwares.utils.spi;

import dev.millenialsoftwares.utils.structs.ChivCondition;

public interface ConditionEmitter<E> {
    /** Emit an engine-native predicate from the core Condition AST. */
    E emit(ChivCondition c);
}