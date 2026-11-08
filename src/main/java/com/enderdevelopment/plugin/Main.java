package com.enderdevelopment.plugin;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new TrapChestLootListener(this), this);
        getLogger().info("Trap chest loot enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("Trap chest loot disabled.");
    }
}
