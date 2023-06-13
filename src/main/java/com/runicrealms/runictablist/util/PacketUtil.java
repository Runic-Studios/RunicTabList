package com.runicrealms.runictablist.util;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.runicrealms.runictablist.tab.TabElement;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

/**
 * A utility function to manage packets
 */
public final class PacketUtil {
    /**
     * Private constructor to prevent class being used in an OOP way
     */
    private PacketUtil() {

    }

    /**
     * Utility function used to send packets to a player
     *
     * @param player  the player
     * @param packets the packets to send
     */
    public static void send(@NotNull Player player, @Nullable PacketContainer... packets) {
        for (PacketContainer packet : packets) {
            if (packet == null) {
                continue;
            }

            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet, false);
        }
    }

    /**
     * A quick way to build an add player packet from a {@link TabElement}
     *
     * @param elements the elements to build a packet from
     * @return the packet
     */
    @NotNull
    public static PacketContainer getAddPacket(@NotNull List<PlayerInfoData> elements) {
        PacketContainer add = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
        add.getPlayerInfoActions().write(0, EnumSet.of(EnumWrappers.PlayerInfoAction.ADD_PLAYER, EnumWrappers.PlayerInfoAction.UPDATE_DISPLAY_NAME, EnumWrappers.PlayerInfoAction.UPDATE_LATENCY, EnumWrappers.PlayerInfoAction.UPDATE_LISTED));
        add.getPlayerInfoDataLists().write(1, elements);
        return add;
    }

    /**
     * A quick way to build an add player packet from a {@link TabElement}
     *
     * @param elements the elements to build a packet from
     * @return the packet
     */
    @NotNull
    public static PacketContainer getAddPacket(@NotNull TabElement... elements) {
        List<PlayerInfoData> data = new ArrayList<>();

        for (TabElement element : elements) {
            data.add(element.getInfo());
        }

        return PacketUtil.getAddPacket(data);
    }

    /**
     * A quick way to build a remove player packet
     *
     * @param uuids the uuids of players to remove
     * @return the packet
     */
    @NotNull
    public static PacketContainer getRemovePacket(@NotNull List<UUID> uuids) {
        PacketContainer remove = new PacketContainer(PacketType.Play.Server.PLAYER_INFO_REMOVE);
        remove.getUUIDLists().write(0, uuids);
        return remove;
    }

    /**
     * A quick way to build a remove player packet
     *
     * @param uuids the uuids of players to remove
     * @return the packet
     */
    @NotNull
    public static PacketContainer getRemovePacket(@NotNull UUID... uuids) {
        return PacketUtil.getRemovePacket(List.of(uuids));
    }

    /**
     * A quick way to build a header and footer packet
     *
     * @param header the header
     * @param footer the footer
     * @return the packet
     */
    @NotNull
    public static PacketContainer getHeaderAndFooterPacket(@Nullable String header, @Nullable String footer) {
        PacketContainer headerAndFooter = new PacketContainer(PacketType.Play.Server.PLAYER_LIST_HEADER_FOOTER);
        headerAndFooter.getChatComponents().write(0, WrappedChatComponent.fromText(header == null ? "" : header));
        headerAndFooter.getChatComponents().write(1, WrappedChatComponent.fromText(footer == null ? "" : footer));
        return headerAndFooter;
    }
}
