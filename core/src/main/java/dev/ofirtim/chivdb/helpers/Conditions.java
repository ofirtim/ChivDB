package dev.ofirtim.chivdb.helpers;

import dev.ofirtim.chivdb.operators.*;
import dev.ofirtim.chivdb.structs.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public final class Conditions {
    private Conditions() {
    }

    public static ChivCondition and(ChivCondition... cs) {
        return new ChivJunction(true, Arrays.asList(cs));
    }

    public static ChivCondition or(ChivCondition... cs) {
        return new ChivJunction(false, Arrays.asList(cs));
    }

    public static ChivCondition not(ChivCondition c) {
        return new ChivNot(c);
    }


    public static <T> ChivCondition eq(ChivField<T> f, T v) {
        return new ChivAtom<>(f, ComparisonOp.EQUALS, v);
    }

    public static <T extends Comparable<T>> ChivCondition gt(ChivField<T> f, T v) {
        return new ChivAtom<>(f, ComparisonOp.GREATER_THAN, v);
    }

    public static <T extends Comparable<T>> ChivCondition gte(ChivField<T> f, T v) {
        return new ChivAtom<>(f, ComparisonOp.GREATER_THAN_OR_EQUAL, v);
    }

    public static <T extends Comparable<T>> ChivCondition lt(ChivField<T> f, T v) {
        return new ChivAtom<>(f, ComparisonOp.LESS_THAN, v);
    }

    public static <T extends Comparable<T>> ChivCondition lte(ChivField<T> f, T v) {
        return new ChivAtom<>(f, ComparisonOp.LESS_THAN_OR_EQUAL, v);
    }

    public static <T extends Comparable<T>> ChivCondition betweenInclusive(ChivField<T> f, T low, T high) {
        return new ChivAtom<>(f, ComparisonOp.EQUALS, List.of(low, high)); /* adapter treats specially */
    }


    public static <T> ChivCondition in(ChivField<T> f, Collection<T> values) {
        return new ChivAtom<>(f, SetOp.IN, values);
    }


    public static ChivCondition isNull(ChivField<?> f) {
        return new ChivAtom<>(f, NullOp.IS_NULL, Boolean.TRUE);
    }

    public static ChivCondition exists(ChivField<?> f) {
        return new ChivAtom<>(f, NullOp.EXISTS, Boolean.TRUE);
    }


    public static ChivCondition like(ChivField<String> f, String pattern) {
        return new ChivAtom<>(f, StringOp.LIKE, pattern);
    }

    public static ChivCondition regex(ChivField<String> f, String pattern) {
        return new ChivAtom<>(f, StringOp.MATCHES_REGEX, pattern);
    }

    public static ChivCondition startsWith(ChivField<String> f, String s) {
        return new ChivAtom<>(f, StringOp.STARTS_WITH, s);
    }

    public static ChivCondition endsWith(ChivField<String> f, String s) {
        return new ChivAtom<>(f, StringOp.ENDS_WITH, s);
    }

    public static ChivCondition contains(ChivField<String> f, String s) {
        return new ChivAtom<>(f, StringOp.CONTAINS, s);
    }


    public static <E> ChivCondition arrayContains(ChivField<?> f, E val) {
        return new ChivAtom<>(f, ArrayOp.ARRAY_CONTAINS, val);
    }

    public static <E> ChivCondition arrayContainsAny(ChivField<?> f, Collection<E> vals) {
        return new ChivAtom<>(f, ArrayOp.ARRAY_CONTAINS_ANY, vals);
    }

    public static ChivCondition arraySizeEquals(ChivField<?> f, int n) {
        return new ChivAtom<>(f, ArrayOp.ARRAY_SIZE_EQUALS, n);
    }
}