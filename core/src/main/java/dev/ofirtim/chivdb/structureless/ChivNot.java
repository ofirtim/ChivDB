package dev.ofirtim.chivdb.structureless;

public record ChivNot(
        ChivCondition inner)
        implements ChivCondition {}