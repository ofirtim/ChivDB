package dev.millenialsoftwares.utils.structs;

import java.util.Map;

public record ChivDocument(
        Map<String, Object> values) {

    public Object get(String key) {
        return values.get(key);
    }

    public String getString(String k) {
        return (String) values.get(k);
    }
}