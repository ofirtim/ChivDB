package dev.ofirtim.chivdb.engines.jooq;

import dev.ofirtim.chivdb.ConnectionHandle;
import dev.ofirtim.chivdb.DataBaseConfiguration;
import dev.ofirtim.chivdb.EngineType;
import dev.ofirtim.chivdb.helpers.queries.DocumentApi;
import dev.ofirtim.chivdb.helpers.queries.RelationalApi;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Optional;

final class JooqHandle implements ConnectionHandle {
    private final Connection conn;
    private final DSLContext ctx;
    private final EngineType engineType;
    private final DataBaseConfiguration cfg;

    private JooqHandle(Connection conn, DSLContext ctx, EngineType type, DataBaseConfiguration cfg) {
        this.conn = conn;
        this.ctx = ctx;
        this.engineType = type;
        this.cfg = cfg;
    }

    static ConnectionHandle open(DataBaseConfiguration cfg) {
        try {
            Connection c = DriverManager.getConnection(cfg.uri, cfg.username, cfg.password);
            SQLDialect dialect = switch (cfg.type) {
                case POSTGRES -> SQLDialect.POSTGRES;
                case MYSQL -> SQLDialect.MYSQL;
                case MARIADB -> SQLDialect.MARIADB;
                case SQLITE -> SQLDialect.SQLITE;
                default ->
                        throw new IllegalArgumentException("Relational adapter supports only Postgres/MySQL/MariaDB/SQLite. Got: " + cfg.type);
            };
            DSLContext dsl = DSL.using(c, dialect);
            return new JooqHandle(c, dsl, cfg.type, cfg);
        } catch (Exception e) {
            throw new RuntimeException("Failed to open jOOQ handle", e);
        }
    }

    @Override
    public EngineType type() {
        return engineType;
    }

    @Override
    public Optional<RelationalApi> relational() {
        return Optional.of(new JooqRelationalApi(ctx, cfg.objectifier, cfg.coercion));
    }

    @Override
    public Optional<DocumentApi> document() {
        return Optional.empty();
    }

    @Override
    public void close() {
        try {
            conn.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
