package dev.millenialsoftwares.utils.structs;

import dev.millenialsoftwares.utils.operators.Operator;

public record ChivAtom<T>(
        ChivField<T> chivField,
        Operator op,
        Object value)
        implements ChivCondition {}
