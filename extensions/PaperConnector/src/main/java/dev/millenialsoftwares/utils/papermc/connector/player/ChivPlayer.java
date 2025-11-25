package dev.millenialsoftwares.utils.papermc.connector.player;

import dev.millenialsoftwares.utils.commons.DataPointer;

import java.util.Optional;
import java.util.UUID;

public interface ChivPlayer {

    public <T> T get(DataPointer<?, ?> pointer, T type);

    public <T> T get(String field, T type);

    public <T> T getDefault(DataPointer<?, ?> pointer, T type, Class<?> defaultType);

    public Optional<UUID> getUniqueIdByName(String name);

    public Optional<String> getName(UUID playerUniqueId);

}
