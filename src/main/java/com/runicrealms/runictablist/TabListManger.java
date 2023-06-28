package com.runicrealms.runictablist;

import com.runicrealms.plugin.RunicCore;
import com.runicrealms.plugin.api.event.PlayerVanishEvent;
import com.runicrealms.plugin.party.Party;
import com.runicrealms.plugin.party.event.PartyEvent;
import com.runicrealms.plugin.party.event.PartyJoinEvent;
import com.runicrealms.runicguilds.RunicGuilds;
import com.runicrealms.runicguilds.api.event.GuildCreationEvent;
import com.runicrealms.runicguilds.api.event.GuildDisbandEvent;
import com.runicrealms.runicguilds.api.event.GuildInvitationAcceptedEvent;
import com.runicrealms.runicguilds.api.event.GuildMemberKickedEvent;
import com.runicrealms.runicguilds.api.event.GuildMemberLeaveEvent;
import com.runicrealms.runicguilds.model.GuildInfo;
import com.runicrealms.runictablist.tab.RunicRealmsTabList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class TabListManger implements Listener {
    private final Map<UUID, RunicRealmsTabList> tabLists;

    public TabListManger() {
        this.tabLists = new HashMap<>();
    }

    /**
     * A method used to add a player to the tab list
     *
     * @param player the user to add to the cache
     */
    public void addUser(@NotNull Player player) {
        if (!this.tabLists.containsKey(player.getUniqueId())) {
            RunicRealmsTabList tabList = new RunicRealmsTabList(player);
            this.tabLists.put(player.getUniqueId(), tabList);
            RunicCore.getInstance().getServer().getScheduler().runTaskAsynchronously(RunicTabList.getInstance(), tabList::update);
        }
    }

    /**
     * A method that updates the tablist for the given player
     *
     * @param player the player to update
     */
    public void update(@Nullable Player player) {
        if (player == null) {
            return;
        }

        RunicRealmsTabList tabList = this.tabLists.get(player.getUniqueId());

        if (tabList == null) {
            return;
        }

        RunicCore.getInstance().getServer().getScheduler().runTaskAsynchronously(RunicTabList.getInstance(), tabList::update);
    }

    /**
     * A method used to remove a player from the tab list cache
     *
     * @param player the player to remove from the tab list cache
     */
    public void removeUser(@NotNull Player player) {
        RunicRealmsTabList list = this.tabLists.remove(player.getUniqueId());

        if (list != null && player.isOnline()) {
            Bukkit.getScheduler().runTaskAsynchronously(RunicTabList.getInstance(), list::reset);
        }
    }

    /**
     * Refreshes the tablist for all users
     */
    public void refreshAllTabLists() {
        for (RunicRealmsTabList tabList : this.tabLists.values()) {
            RunicCore.getInstance().getServer().getScheduler().runTaskLaterAsynchronously(RunicTabList.getInstance(), tabList::update, 1);
        }
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        this.addUser(event.getPlayer());
        refreshAllTabLists();
    }


    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent event) {
        this.removeUser(event.getPlayer());
        refreshAllTabLists();
    }

    @EventHandler
    private void onPlayerVanish(PlayerVanishEvent event) {
        refreshAllTabLists();
    }

    @EventHandler
    private void onParty(PartyEvent event) {
        this.partyUpdate(event.getParty());
    }

    @EventHandler
    private void onPartyJoin(PartyJoinEvent event) {
        this.partyUpdate(event.getParty());
    }

    @EventHandler
    private void onPartyLeave(PartyJoinEvent event) {
        this.partyUpdate(event.getParty());
    }

    @EventHandler
    private void onGuildCreate(GuildCreationEvent event) {
        this.update(Bukkit.getPlayer(event.getUuid()));
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onGuildDisband(GuildDisbandEvent event) {
        this.guildUpdate(event.getUUID());
    }

    @EventHandler
    private void onGuildJoin(GuildInvitationAcceptedEvent event) {
        this.guildUpdate(event.getUUID());
    }

    @EventHandler
    private void onGuildLeave(GuildMemberLeaveEvent event) {
        this.guildUpdate(event.getUUID());
    }

    @EventHandler
    private void onGuildKick(GuildMemberKickedEvent event) {
        this.guildUpdate(RunicGuilds.getDataAPI().getGuildInfo(Bukkit.getPlayer(event.getKicker())).getUUID());
        this.update(Bukkit.getPlayer(event.getKicked()));
    }

    /**
     * A method used to update the tablists of all party members
     *
     * @param party the party to be updated
     */
    private void partyUpdate(@NotNull Party party) {
        for (Player player : party.getMembersWithLeader()) {
            this.update(player);
        }
    }

    /**
     * A method used to update the tablists of all guild members
     *
     * @param guildUUID the uuid of the guild
     */
    private void guildUpdate(@NotNull UUID guildUUID) {
        GuildInfo guild = RunicGuilds.getDataAPI().getGuildInfo(guildUUID);

        guild.getMembersUuids().stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .forEach(this::update);
    }
}
