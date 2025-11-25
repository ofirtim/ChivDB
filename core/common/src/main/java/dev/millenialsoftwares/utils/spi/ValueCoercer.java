package dev.millenialsoftwares.utils.spi;

public interface ValueCoercer {

    Object coerce(Object value, Class<?> targetType);

}