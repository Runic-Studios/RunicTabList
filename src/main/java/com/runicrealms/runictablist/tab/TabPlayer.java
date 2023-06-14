package com.runicrealms.runictablist.tab;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * An element that acts as a model for a real player on the server
 */
public class TabPlayer extends TabElement {
    private final Player player;

    public TabPlayer(@NotNull Player player, @NotNull String text) {
        super(text, TabElement.Ping.getPing(player), TabElement.Skin.getSkin(player));
        this.player = player;
    }

    /**
     * A method that returns the player this element represents
     *
     * @return the player this element represents
     */
    @NotNull
    public Player getPlayer() {
        return this.player;
    }

    @Override
    @NotNull
    public PlayerInfoData build(int index) {
        return new PlayerInfoData(WrappedGameProfile.fromPlayer(this.player), this.getPing().getLatency(), EnumWrappers.NativeGameMode.fromBukkit(this.player.getGameMode()), WrappedChatComponent.fromText(this.getText()));
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TabPlayer element)) {
            return false;
        }

        return this.player.getUniqueId().equals(element.getPlayer().getUniqueId()) && super.equals(element);
    }
}
