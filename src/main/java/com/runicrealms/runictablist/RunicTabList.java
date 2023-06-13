package com.runicrealms.runictablist;

import com.runicrealms.runictablist.tab.TabElement;
import com.runicrealms.runictablist.tab.TabList;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class RunicTabList extends JavaPlugin {
    private static RunicTabList plugin;

    /*
    TODO:
    - fix default skins
    - find way to order tab list (order in sent list is not maintained on client)
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
        @EventHandler
        private void onPlayerJoin(PlayerJoinEvent event) {
            List<TabElement> elements = new ArrayList<>();
            TabList tab = new TabList(event.getPlayer(), elements, null, null);
            tab.setHeader("0");
            tab.update();

            Bukkit.getScheduler().runTaskTimerAsynchronously(RunicTabList.getInstance(), () -> {
                String text = String.valueOf(Integer.parseInt(tab.getHeader()) + 1);
                tab.setHeader(text);
                elements.add(new TabElement(text, TabElement.Ping.BAD, TabElement.Skin.BLANK));
                tab.update();
            }, 40, 100);
        }
    }
}
