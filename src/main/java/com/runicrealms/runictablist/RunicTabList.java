package com.runicrealms.runictablist;

import com.runicrealms.runictablist.tab.TabElement;
import com.runicrealms.runictablist.tab.TabList;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

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
        private final Map<UUID, BukkitTask> tasks = new HashMap<>();

        @EventHandler(priority = EventPriority.MONITOR)
        private void onPlayerJoin(PlayerJoinEvent event) {
            TabList tab = new TabList(event.getPlayer(), "    testing    ", null);

            BukkitTask task = Bukkit.getScheduler().runTaskTimerAsynchronously(RunicTabList.getInstance(), () -> {
                int index = ThreadLocalRandom.current().nextInt(80);
                TabElement.Ping ping = TabElement.Ping.values()[ThreadLocalRandom.current().nextInt(TabElement.Ping.values().length)];
                int random = ThreadLocalRandom.current().nextInt(3);
                TabElement.Skin skin = random == 0 ? TabElement.Skin.GREEN : random == 1 ? TabElement.Skin.GOLD : TabElement.Skin.YELLOW;
                tab.set(new TabElement(String.valueOf(index), ping, skin), index);
                tab.update();
            }, 10, 100);


            this.tasks.put(event.getPlayer().getUniqueId(), task);
        }

        @EventHandler(priority = EventPriority.MONITOR)
        private void onPlayerQuit(PlayerQuitEvent event) {
            BukkitTask task = this.tasks.remove(event.getPlayer().getUniqueId());

            if (task != null) {
                task.cancel();
            }
        }
    }
}
