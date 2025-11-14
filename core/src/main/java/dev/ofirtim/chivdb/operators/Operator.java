package dev.ofirtim.chivdb.operators;

public sealed interface Operator permits ArrayOp, ComparisonOp, GeoOp, NullOp, SetOp, StringOp {
}
