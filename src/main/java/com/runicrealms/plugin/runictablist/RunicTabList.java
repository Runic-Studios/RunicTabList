package com.runicrealms.plugin.runictablist;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class RunicTabList extends JavaPlugin {
    private static RunicTabList plugin;
    private TabListManger tabListManger;

    /*
    TODO:

     */

    @Override
    public void onEnable() {
        RunicTabList.plugin = this;
        this.tabListManger = new TabListManger();

        Bukkit.getPluginManager().registerEvents(this.tabListManger, this);
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

    /**
     * A method that returns the instance of the tab list manager
     *
     * @return the instance of the tab list manager
     */
    @NotNull
    public TabListManger getTabListManger() {
        return this.tabListManger;
    }
}
