package dev.ofirtim.chivdb.structs;

public interface ChivField<T> {

    String name();
    Class<T> type();
}
