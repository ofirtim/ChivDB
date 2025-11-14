package dev.ofirtim.chivdb.spi;

public interface ValueCoercer {

    Object coerce(Object value, Class<?> targetType);

}