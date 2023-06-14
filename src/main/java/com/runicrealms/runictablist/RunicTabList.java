package com.runicrealms.runictablist;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class RunicTabList extends JavaPlugin {
    private static RunicTabList plugin;

    /*
    TODO:
    - find way to order tab list (order in sent list is not maintained on client)
    - DO NOT REMOVE REAL PLAYERS FROM TAB LIST EVERRRRRRRR
     */

    @Override
    public void onEnable() {
        RunicTabList.plugin = this;

        Bukkit.getPluginManager().registerEvents(new Listener(), this);
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
     * TESTING ONLY PLEASE DO NOT GET MAD AT ME xD
     */
    private static class Listener implements org.bukkit.event.Listener {
        @EventHandler(priority = EventPriority.MONITOR)
        private void onPlayerJoin(PlayerJoinEvent event) {
            /*
            List<TabElement> elements = new ArrayList<>();
            TabList tab = new TabList(event.getPlayer(), elements, null, null);
            tab.setHeader("    0    ");

            Bukkit.getScheduler().runTaskTimerAsynchronously(RunicTabList.getInstance(), () -> {
                String text = "    " + (Integer.parseInt(tab.getHeader().trim()) + 1) + "    ";
                tab.setHeader(text);
                elements.add(new TabElement(text, TabElement.Ping.VERY_BAD, TabElement.Skin.YELLOW));
                tab.update();
            }, 100, 100);

             */
        }
    }
}
