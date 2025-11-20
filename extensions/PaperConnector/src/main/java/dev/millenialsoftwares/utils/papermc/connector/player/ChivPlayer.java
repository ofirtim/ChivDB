package dev.millenialsoftwares.utils.papermc.connector.player;

public interface ChivPlayer {

    public <T> T get(T type, Pointer);

    public <T> T getDefault(T type, Class<?> defaultType);
}
