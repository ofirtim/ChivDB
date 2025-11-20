package dev.ofirtim.chivdb.helpers.queries;

import dev.ofirtim.chivdb.structs.ChivCondition;
import dev.ofirtim.chivdb.structs.ChivDocument;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface DocumentApi {

    DocQuery collection(String name);

    interface DocQuery {
        DocQuery where(ChivCondition c);
        DocQuery project(String... fields);
        DocQuery sort(String field, boolean asc);
        DocQuery limit(int n);

        List<ChivDocument> list();
        Optional<ChivDocument> one();

        <T> List<T> listAs(Class<T> type);
        <T> Optional<T> oneAs(Class<T> type);

        String insert(Map<String, Object> doc);
        long update(ChivCondition c, Map<String, Object> set);
        long delete(ChivCondition c);
    }
}