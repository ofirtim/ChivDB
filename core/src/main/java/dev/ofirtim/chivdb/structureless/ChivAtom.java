package dev.ofirtim.chivdb.structureless;

import dev.ofirtim.chivdb.operators.Operator;

public record ChivAtom<T>(
        ChivField<T> chivField,
        Operator op,
        Object value)
        implements ChivCondition {}
