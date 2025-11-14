package dev.ofirtim.chivdb;

public interface EngineProvider {

    EngineType type();

    default boolean supports(EngineType k) {
        return type() == k;
    }

    ConnectionHandle open(DataBaseConfiguration configuration);
}