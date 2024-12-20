package com.runicrealms.plugin.runictablist;

import com.runicrealms.plugin.RunicCore;
import com.runicrealms.plugin.api.event.PlayerVanishEvent;
import com.runicrealms.plugin.events.EnvironmentDamageEvent;
import com.runicrealms.plugin.events.HealthRegenEvent;
import com.runicrealms.plugin.events.MagicDamageEvent;
import com.runicrealms.plugin.events.PhysicalDamageEvent;
import com.runicrealms.plugin.events.RangedDamageEvent;
import com.runicrealms.plugin.events.RunicDamageEvent;
import com.runicrealms.plugin.party.Party;
import com.runicrealms.plugin.party.event.PartyEvent;
import com.runicrealms.plugin.party.event.PartyJoinEvent;
import com.runicrealms.plugin.party.event.PartyLeaveEvent;
import com.runicrealms.plugin.rdb.event.CharacterLoadedEvent;
import com.runicrealms.plugin.rdb.event.CharacterSelectEvent;
import com.runicrealms.plugin.runicguilds.RunicGuilds;
import com.runicrealms.plugin.runicguilds.api.event.GuildCreationEvent;
import com.runicrealms.plugin.runicguilds.api.event.GuildDisbandEvent;
import com.runicrealms.plugin.runicguilds.api.event.GuildInvitationAcceptedEvent;
import com.runicrealms.plugin.runicguilds.api.event.GuildMemberKickedEvent;
import com.runicrealms.plugin.runicguilds.api.event.GuildMemberLeaveEvent;
import com.runicrealms.plugin.runicguilds.model.GuildInfo;
import com.runicrealms.plugin.runictablist.tab.RunicRealmsTabList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * A class that keeps track of the tablist open for all players online
 *
 * @author BoBoBalloon
 */
public final class TabListManger implements Listener {
    private final Map<UUID, RunicRealmsTabList> tabLists;

    public TabListManger() {
        this.tabLists = new HashMap<>();
    }

    /**
     * A method used to add a player to the tab list and to send the necessary update packets
     *
     * @param player the user to add to the cache
     */
    public void addUser(@NotNull Player player) {
        if (this.tabLists.containsKey(player.getUniqueId())) {
            return;
        }

        RunicRealmsTabList tabList = new RunicRealmsTabList(player);
        this.tabLists.put(player.getUniqueId(), tabList);

        this.update(player, 0);
    }

    /**
     * A method that updates the tablist for the given player
     *
     * @param player the player to update
     * @param delay  the delay in ticks before the update packets are sent
     */
    public void update(@Nullable Player player, long delay) {
        if (player == null) {
            return;
        }

        RunicRealmsTabList tabList = this.tabLists.get(player.getUniqueId());

        if (tabList == null) {
            return;
        }

        Bukkit.getScheduler().runTaskLaterAsynchronously(RunicTabList.getInstance(), tabList::update, delay);
    }

    /**
     * A method that updates the tablist for the given player
     *
     * @param player the player to update
     */
    public void update(@Nullable Player player) {
        this.update(player, 1);
    }

    /**
     * A method used to remove a player from the tab list cache
     *
     * @param player the player to remove from the tab list cache
     */
    public void removeUser(@NotNull Player player) {
        RunicRealmsTabList list = this.tabLists.remove(player.getUniqueId());

        if (list != null && player.isOnline()) {
            Bukkit.getScheduler().runTaskLaterAsynchronously(RunicTabList.getInstance(), list::reset, 1);
        }
    }

    /**
     * Refreshes the tablist for all users
     */
    public void refreshAllTabLists() {
        RunicCore.getInstance().getServer().getScheduler().runTaskLaterAsynchronously(RunicTabList.getInstance(), () -> {
            for (RunicRealmsTabList tabList : this.tabLists.values()) {
                tabList.update();
            }
        }, 1);
    }

    @EventHandler(priority = EventPriority.LOWEST) //I just want to fire this code first
    private void onCharacterSelect(CharacterSelectEvent event) {
        this.addUser(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onCharacterLoaded(CharacterLoadedEvent event) {
        refreshAllTabLists();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onPlayerQuit(PlayerQuitEvent event) {
        this.removeUser(event.getPlayer());
        refreshAllTabLists();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onPlayerVanish(PlayerVanishEvent event) {
        refreshAllTabLists();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onParty(PartyEvent event) {
        this.partyUpdate(event.getParty());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onPartyJoin(PartyJoinEvent event) {
        this.partyUpdate(event.getParty());
        this.update(event.getJoining());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onPartyLeave(PartyLeaveEvent event) {
        this.partyUpdate(event.getParty());
        this.partyUpdate(event.getLeaver());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onGuildCreate(GuildCreationEvent event) {
        this.update(Bukkit.getPlayer(event.getUuid()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onGuildDisband(GuildDisbandEvent event) {
        this.guildUpdate(event.getUUID(), 10);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onGuildJoin(GuildInvitationAcceptedEvent event) {
        this.guildUpdate(event.getUUID());
        this.update(Bukkit.getPlayer(event.getInvited()), 10);
        //player is not added to guild until after event is called, therefore we have a 10 tick delay
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onGuildLeave(GuildMemberLeaveEvent event) {
        this.guildUpdate(event.getUUID());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onGuildKick(GuildMemberKickedEvent event) {
        this.guildUpdate(RunicGuilds.getDataAPI().getGuildInfo(Bukkit.getPlayer(event.getKicker())).getUUID());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onEnvironmentDamage(EnvironmentDamageEvent event) {
        this.onDamage(event);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onMagicDamage(MagicDamageEvent event) {
        this.onDamage(event);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onPhysicalDamage(PhysicalDamageEvent event) {
        this.onDamage(event);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onRangedDamage(RangedDamageEvent event) {
        this.onDamage(event);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onRunicHeal(HealthRegenEvent event) {
        this.partyUpdate(event.getPlayer());
    }

    private void onDamage(@NotNull RunicDamageEvent event) {
        if (!(event.getVictim() instanceof Player player)) {
            return;
        }

        this.partyUpdate(player);
    }

    /**
     * A method used to update the tablist for all players in the player's party if they are in one
     *
     * @param player the player
     */
    private void partyUpdate(@NotNull Player player) {
        Party party = RunicCore.getPartyAPI().getParty(player.getUniqueId());

        if (party == null) {
            return;
        }

        this.partyUpdate(party);
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
     * @param delay     the delay before the update packets are sent
     */
    private void guildUpdate(@NotNull UUID guildUUID, long delay) {
        GuildInfo guild = RunicGuilds.getDataAPI().getGuildInfo(guildUUID);

        guild.getMembersUuids().stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .forEach(player -> this.update(player, delay));
    }

    /**
     * A method used to update the tablists of all guild members
     *
     * @param guildUUID the uuid of the guild
     */
    private void guildUpdate(@NotNull UUID guildUUID) {
        this.guildUpdate(guildUUID, 1);
    }
}
