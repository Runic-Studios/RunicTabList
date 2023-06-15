package com.runicrealms.runictablist;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class RunicTabList extends JavaPlugin {
    private static RunicTabList plugin;

    //KNOWN BUG: skin for first loaded icon does not update if the update() method is called on another thread with delay, keep everything inside one thread (one task)

    /*
    TODO:
    - Add row and column implementation
     */

    @Override
    public void onEnable() {
        RunicTabList.plugin = this;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    /**
     * A method used to get the singleton instance
     *
     * @return the singleton instance
     */
    @NotNull
    public static RunicTabList getInstance() {
        if (RunicTabList.plugin == null) {
            throw new IllegalStateException("You have tried to access the plugin before it was enabled!");
        }

        return RunicTabList.plugin;
    }
}
