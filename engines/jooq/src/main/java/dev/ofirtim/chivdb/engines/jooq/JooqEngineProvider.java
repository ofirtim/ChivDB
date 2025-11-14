package dev.ofirtim.chivdb.engines.jooq;

import dev.ofirtim.chivdb.ConnectionHandle;
import dev.ofirtim.chivdb.DataBaseConfiguration;
import dev.ofirtim.chivdb.EngineProvider;
import dev.ofirtim.chivdb.EngineType;

public final class JooqEngineProvider implements EngineProvider {
    @Override
    public EngineType type() {
        return EngineType.POSTGRES;
    }

    @Override
    public boolean supports(EngineType k) {
        return k == EngineType.POSTGRES || k == EngineType.MYSQL || k == EngineType.MARIADB || k == EngineType.SQLITE;
    }

    @Override
    public ConnectionHandle open(DataBaseConfiguration cfg) {
        return JooqHandle.open(cfg);
    }
}