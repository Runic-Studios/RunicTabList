package com.runicrealms.runictablist.util;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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
    public static void send(@NotNull Player player, @NotNull PacketContainer... packets) {
        for (PacketContainer packet : packets) {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet, false);
        }
    }
}
