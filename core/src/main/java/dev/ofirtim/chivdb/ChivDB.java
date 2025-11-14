package dev.ofirtim.chivdb;

import dev.ofirtim.chivdb.structureless.ChivCondition;

import java.util.ServiceLoader;

public final class ChivDB {
    private ChivDB() {}


    public static ConnectionHandle init(DataBaseConfiguration cfg) {
        ServiceLoader<EngineProvider> loader = ServiceLoader.load(EngineProvider.class);
        for (EngineProvider p : loader) {
            if (p.supports(cfg.type)) {
                return p.open(cfg);
            }
        }
        throw new IllegalStateException("No EngineProvider found for " + cfg.type);
    }
}