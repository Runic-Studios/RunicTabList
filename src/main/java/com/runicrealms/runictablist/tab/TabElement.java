package com.runicrealms.runictablist.tab;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * A class that represents an element in a {@link TabList}
 *
 * @author BoBoBalloon
 */
public class TabElement {
    private String text;
    private Ping ping;
    private String skin;

    public TabElement(@NotNull String text, @NotNull Ping ping, @NotNull String skin) {
        this.text = text;
        this.ping = ping;
        this.skin = skin;
    }

    /**
     * A method that returns the text that will be displayed in this element
     *
     * @return the text that will be displayed in this element
     */
    @NotNull
    public String getText() {
        return this.text;
    }

    /**
     * A method that sets the text that will be displayed in this element
     *
     * @param text the text that will be displayed in this element
     */
    public void setText(@NotNull String text) {
        this.text = text;
    }

    /**
     * A method that returns the ping that will be displayed in this element
     *
     * @return the ping that will be displayed in this element
     */
    @NotNull
    public Ping getPing() {
        return this.ping;
    }

    /**
     * A method that sets the ping that will be displayed in this element
     *
     * @param ping the ping that will be displayed in this element
     */
    public void setPing(@NotNull Ping ping) {
        this.ping = ping;
    }

    /**
     * A method that returns the base64 encoded string of the textures json that will be displayed in this element
     *
     * @return the base64 encoded string of the textures json that will be displayed in this element
     */
    @NotNull
    public String getSkin() {
        return this.skin;
    }

    /**
     * A method that sets the base64 encoded string of the textures json that will be displayed in this element
     *
     * @param skin the base64 encoded string of the textures json that will be displayed in this element
     */
    public void setSkin(@NotNull String skin) {
        this.skin = skin;
    }

    /**
     * An enum which represents the ping a {@link TabElement} can have
     */
    public enum Ping {
        PERFECT(5),
        GOOD(4),
        OK(3),
        BAD(2),
        VERY_BAD(1);

        private final int bars;

        Ping(int bars) {
            this.bars = bars;
        }

        /**
         * A method used to get the corresponding ping icon based on packet delay in milliseconds
         * ChatGPT said this was the same conditions that vanilla used so who am I to judge?
         *
         * @param player the player to check the connection of
         * @return the corresponding ping icon based on packet delay in milliseconds
         */
        @NotNull
        public static Ping getPing(@NotNull Player player) {
            int ping = player.getPing();

            if (ping < 150) {
                return Ping.PERFECT;
            } else if (ping < 300) {
                return Ping.GOOD;
            } else if (ping < 600) {
                return Ping.OK;
            } else if (ping < 1000) {
                return Ping.BAD;
            } else {
                return Ping.VERY_BAD;
            }
        }

        /**
         * A method used to get the amount of bars this connection has
         *
         * @return the amount of bars this connection has
         */
        public int getBars() {
            return this.bars;
        }
    }
}
