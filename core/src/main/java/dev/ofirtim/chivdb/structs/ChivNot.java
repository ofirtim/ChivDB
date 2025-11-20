package dev.ofirtim.chivdb.structs;

public record ChivNot(
        ChivCondition inner)
        implements ChivCondition {}