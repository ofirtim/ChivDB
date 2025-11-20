package dev.ofirtim.chivdb.structs;

import java.util.List;

public record ChivJunction(
        boolean and,
        List<ChivCondition> parts)
        implements ChivCondition {

    public static ChivJunction and(List<ChivCondition> parts) {
        return new ChivJunction(true, parts);
    }

    public static ChivJunction or(List<ChivCondition> parts) {
        return new ChivJunction(false, parts);
    }
}