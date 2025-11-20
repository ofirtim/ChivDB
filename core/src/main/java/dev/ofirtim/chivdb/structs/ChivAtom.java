package dev.ofirtim.chivdb.structs;

import dev.ofirtim.chivdb.operators.Operator;

public record ChivAtom<T>(
        ChivField<T> chivField,
        Operator op,
        Object value)
        implements ChivCondition {}
