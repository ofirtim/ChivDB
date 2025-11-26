package dev.millenialsoftwares.utils.structs;

import java.util.Optional;

public interface ChivField<T> {

    String name();

    Class<T> type();

    Optional<Class<T>> getDefault();
}
