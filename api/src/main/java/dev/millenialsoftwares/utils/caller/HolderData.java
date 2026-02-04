package dev.millenialsoftwares.utils.caller;

import java.util.Map;

public record HolderData(
        String id,
        Object callable,
        String parent,
        Map<String, Object> children) {
}