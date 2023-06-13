package com.runicrealms.runictablist.tab;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.runicrealms.runictablist.RunicTabList;
import com.runicrealms.runictablist.util.PacketUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * A class that represents a tab list sent to a player
 */
public class TabList {
    public static int MAXIMUM_ITEMS = 4 * 20; //client maximum is 4x20 (4 columns, 20 rows)
    private final Player player;
    private final List<TabElement> elements;
    private String header;
    private String footer;
    private List<UUID> pastUpdate;

    public TabList(@NotNull Player player, @NotNull List<TabElement> elements, @Nullable String header, @Nullable String footer) {
        if (elements.size() >= TabList.MAXIMUM_ITEMS) {
            throw new IllegalStateException("The elements size is too large! It must be less than or equal to " + TabList.MAXIMUM_ITEMS);
        }

        this.player = player;
        this.elements = elements;
        this.header = header;
        this.footer = footer;
    }

    /**
     * A method that gets the player this tab list is being sent to
     *
     * @return the player this tab list is being sent to
     */
    @NotNull
    public Player getPlayer() {
        return this.player;
    }

    /**
     * A method that returns the elements displayed in this tab list
     *
     * @return the elements displayed in this tab list
     */
    @NotNull
    public List<TabElement> getElements() {
        return this.elements;
    }

    /**
     * A method used to get the header of this tab list
     *
     * @return the header of this tab list (null if no header should exist)
     */
    @Nullable
    public String getHeader() {
        return this.header;
    }

    /**
     * A method used to set the header of this tab list
     *
     * @param header the header of this tab list (null if no header should exist)
     */
    public void setHeader(@Nullable String header) {
        this.header = header;
    }

    /**
     * A method used to get the footer of this tab list
     *
     * @return the footer of this tab list (null if no footer should exist)
     */
    @Nullable
    public String getFooter() {
        return this.footer;
    }

    /**
     * A method used to set the footer of this tab list
     *
     * @param footer the footer of this tab list (null if no footer should exist)
     */
    public void setFooter(@Nullable String footer) {
        this.footer = footer;
    }

    /**
     * A method used to send this tab list to a player
     */
    public void update() {
        if (this.elements.size() >= TabList.MAXIMUM_ITEMS) {
            throw new IllegalStateException("The elements size is too large! It must be less than or equal to " + TabList.MAXIMUM_ITEMS);
        }

        Bukkit.getScheduler().runTaskAsynchronously(RunicTabList.getInstance(), () -> {
            if (this.pastUpdate == null) {
                PacketContainer wipe = PacketUtil.getRemovePacket(Bukkit.getOnlinePlayers().stream().map(Player::getUniqueId).toList());
                PacketUtil.send(this.player, wipe);
            }

            PacketContainer headerAndFooter = PacketUtil.getHeaderAndFooterPacket(this.header, this.footer);

            PacketContainer removePlayers = this.pastUpdate != null ? new PacketContainer(PacketType.Play.Server.PLAYER_INFO_REMOVE) : null;
            if (removePlayers != null) {
                removePlayers.getUUIDLists().write(0, this.pastUpdate);
            }

            PacketContainer addPlayers = PacketUtil.getAddPacket(this.elements.stream().map(TabElement::getInfo).toList());

            if (this.pastUpdate == null) {
                this.pastUpdate = new ArrayList<>();
            }

            this.pastUpdate.clear();
            Iterator<UUID> iterator = this.elements.stream().map(TabElement::getUUID).iterator();
            while (iterator.hasNext()) {
                this.pastUpdate.add(iterator.next());
            }

            PacketUtil.send(this.player, headerAndFooter, removePlayers, addPlayers);
        });
    }
}
