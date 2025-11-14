package dev.ofirtim.chivdb.operators;

public enum StringOp implements Operator {
    LIKE,
    MATCHES_REGEX,
    STARTS_WITH,
    ENDS_WITH,
    CONTAINS
}