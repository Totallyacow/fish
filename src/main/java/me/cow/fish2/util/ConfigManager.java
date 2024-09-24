package me.cow.fish2.util;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigManager {
    private final JavaPlugin plugin;
    private FileConfiguration config;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    private void loadConfig() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        config = plugin.getConfig();
    }

    public int getGemCost(int slotsUnlocked) {
        return config.getInt("gem_cost_multiplier", 2) * slotsUnlocked;
    }

    public int getInventorySize(String menuType) {
        return config.getInt("inventory_sizes." + menuType, 27);
    }

    // Add more methods to retrieve other configuration values
}
