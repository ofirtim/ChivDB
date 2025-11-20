package dev.millenialsoftwares.utils.papermc.connector;

import org.bukkit.plugin.java.JavaPlugin;

public class PaperAppLoader extends JavaPlugin {

    private static PaperAppLoader instance;

    @Override
    public void onEnable() {
        instance = this;
        getLogger().info("[ChivDB] ChivDB tester is now enabled");
    }

    public static PaperAppLoader getInstance() {
        return instance;
    }
}
