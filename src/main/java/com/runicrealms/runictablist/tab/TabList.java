package com.runicrealms.runictablist.tab;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumSet;
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

        PacketContainer headerAndFooter = new PacketContainer(PacketType.Play.Server.PLAYER_LIST_HEADER_FOOTER);
        headerAndFooter.getChatComponents().write(0, WrappedChatComponent.fromText(this.header == null ? "" : this.header));
        headerAndFooter.getChatComponents().write(1, WrappedChatComponent.fromText(this.footer == null ? "" : this.footer));

        /*
        //If just removing all of it and readding is heavy we can do this, harder to maintain
        List<UUID> remove = this.pastUpdate != null ? this.pastUpdate.stream().filter(element -> !this.elements.contains(element)).map(TabElement::getUUID).collect(Collectors.toList()) : null;

        PacketContainer removePlayers = remove != null && !remove.isEmpty() ? new PacketContainer(PacketType.Play.Server.PLAYER_INFO_REMOVE) : null;
        if (removePlayers != null) {
            removePlayers.getIntegers().write(0, remove.size());
            removePlayers.getUUIDLists().write(1, remove);
        }
         */
        PacketContainer removePlayers = this.pastUpdate != null ? new PacketContainer(PacketType.Play.Server.PLAYER_INFO_REMOVE) : null;
        if (removePlayers != null) {
            removePlayers.getIntegers().write(0, this.pastUpdate.size());
            removePlayers.getUUIDLists().write(0, this.pastUpdate); //maybe field index of 1?
        }

        PacketContainer addPlayers = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
        addPlayers.getPlayerInfoActions().write(0, EnumSet.of(EnumWrappers.PlayerInfoAction.ADD_PLAYER));
        addPlayers.getPlayerInfoDataLists().write(0, this.elements.stream().map(element -> new PlayerInfoData(element.getProfile(), element.getPing().getBars(), EnumWrappers.NativeGameMode.SURVIVAL, WrappedChatComponent.fromText(element.getText()))).toList()); //maybe fieldIndex of 1?

        if (this.pastUpdate == null) {
            this.pastUpdate = new ArrayList<>();
        }

        this.pastUpdate.clear();
        Iterator<UUID> iterator = this.elements.stream().map(TabElement::getUUID).iterator();
        while (iterator.hasNext()) {
            this.pastUpdate.add(iterator.next());
        }
    }
}
