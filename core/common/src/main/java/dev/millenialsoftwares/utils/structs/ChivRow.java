package dev.millenialsoftwares.utils.structs;

import java.util.Map;

public record ChivRow(
        Map<String, Object> values) {

    public Object get(String key) {
        return values.get(key);
    }

    public String getString(String k) {
        return (String) values.get(k);
    }

    public Integer getInt(String k) {
        return (Integer) values.get(k);
    }

    public Long getLong(String k) {
        return (Long) values.get(k);
    }

    public Double getDouble(String k) {
        return (Double) values.get(k);
    }
}