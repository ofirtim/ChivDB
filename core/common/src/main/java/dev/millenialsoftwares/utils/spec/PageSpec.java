package dev.millenialsoftwares.utils.spec;

public record PageSpec(
        Integer limit,
        Integer offset) {

    public int limitOrMax() {
        return limit == null ? Integer.MAX_VALUE : limit;
    }

    public int offsetOrZero() {
        return offset == null ? 0 : offset;
    }
}