package dev.ofirtim.chivdb.helpers;

import dev.ofirtim.chivdb.structureless.ChivField;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public final class Fields {

    private Fields() {}

    public static <T> ChivField<T> of(String name, Class<T> type) {
        return new BasicChivField<>(name, type);
    }

    public static ChivField<String> string(String name) {
        return of(name, String.class);
    }

    public static ChivField<Integer> int32(String name) {
        return of(name, Integer.class);
    }

    public static ChivField<Long> int64(String name) {
        return of(name, Long.class);
    }

    public static ChivField<Double> dbl(String name) {
        return of(name, Double.class);
    }

    public static ChivField<Boolean> bool(String name) {
        return of(name, Boolean.class);
    }

    public static ChivField<UUID> uuid(String name) {
        return of(name, UUID.class);
    }

    public static ChivField<Instant> instant(String name) {
        return of(name, Instant.class);
    }

    public static ChivField<LocalDate> localDate(String name) {
        return of(name, LocalDate.class);
    }

    private record BasicChivField<T>(
            String name,
            Class<T> type)
            implements ChivField<T> {}
}
