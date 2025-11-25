package dev.millenialsoftwares.utils.helpers.queries;

import dev.millenialsoftwares.utils.structs.ChivRow;
import dev.millenialsoftwares.utils.structs.ChivCondition;
import dev.millenialsoftwares.utils.structs.ChivField;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface RelationalApi {
    RelQuery fetch(String table);

    int insert(String table, Map<String, Object> values);

    int update(String table, Map<String, Object> values, ChivCondition where);

    int delete(String table, ChivCondition where);

    interface RelQuery {

        RelQuery select(String... columns);
        RelQuery select(ChivField<?>... fields);

        RelQuery where(ChivCondition c);

        RelQuery orderBy(String column, boolean asc);
        RelQuery orderBy(ChivField<?> column, boolean asc);

        RelQuery limit(int n);
        RelQuery offset(int n);

        List<ChivRow> list();
        Optional<ChivRow> one();

        <T> List<T> columnAs(String column, Class<T> type);
        <T> List<T> columnAs(ChivField<T> column, Class<T> type);

        <T> Optional<T> columnOneAs(String column, Class<T> type);
        <T> Optional<T> columnOneAs(ChivField<T> column, Class<T> type);

        <T> List<T> listAs(Class<T> type);
        <T> Optional<T> oneAs(Class<T> type);
    }
}
