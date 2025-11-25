package dev.millenialsoftwares.utils;

import dev.millenialsoftwares.utils.helpers.queries.DocumentApi;
import dev.millenialsoftwares.utils.helpers.queries.RelationalApi;

import java.io.Closeable;
import java.util.Optional;

public interface ConnectionHandle extends Closeable {

    EngineType type();

    Optional<RelationalApi> relational();

    Optional<DocumentApi> document();

    @Override
    void close();
}