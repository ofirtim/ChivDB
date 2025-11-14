package dev.ofirtim.chivdb;

import dev.ofirtim.chivdb.helpers.queries.DocumentApi;
import dev.ofirtim.chivdb.helpers.queries.RelationalApi;

import java.io.Closeable;
import java.util.Optional;

public interface ConnectionHandle extends Closeable {

    EngineType type();

    Optional<RelationalApi> relational();

    Optional<DocumentApi> document();

    @Override
    void close();
}