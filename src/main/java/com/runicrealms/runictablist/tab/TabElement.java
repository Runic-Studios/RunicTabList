package com.runicrealms.runictablist.tab;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import com.runicrealms.runictablist.util.TextUtil;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

/**
 * A class that represents an element in a {@link TabList}
 *
 * @author BoBoBalloon
 */
public class TabElement {
    private String text;
    private Ping ping;
    private Skin skin;

    public TabElement(@NotNull String text, @NotNull Ping ping, @NotNull Skin skin) {
        this.text = TextUtil.format(text);
        this.ping = ping;
        this.skin = skin;
    }

    /**
     * A method that returns the text that will be displayed in this element
     *
     * @return the text that will be displayed in this element
     */
    @NotNull
    public final String getText() {
        return this.text;
    }

    /**
     * A method that sets the text that will be displayed in this element
     *
     * @param text the text that will be displayed in this element
     */
    public final void setText(@NotNull String text) {
        this.text = TextUtil.format(text);
    }

    /**
     * A method that returns the ping that will be displayed in this element
     *
     * @return the ping that will be displayed in this element
     */
    @NotNull
    public final Ping getPing() {
        return this.ping;
    }

    /**
     * A method that sets the ping that will be displayed in this element
     *
     * @param ping the ping that will be displayed in this element
     */
    public final void setPing(@NotNull Ping ping) {
        this.ping = ping;
    }

    /**
     * A method that returns the base64 encoded string of the textures json that will be displayed in this element
     *
     * @return the base64 encoded string of the textures json that will be displayed in this element
     */
    @NotNull
    public final Skin getSkin() {
        return this.skin;
    }

    /**
     * A method that sets the base64 encoded string of the textures json that will be displayed in this element
     *
     * @param skin the base64 encoded string of the textures json that will be displayed in this element
     */
    public final void setSkin(@NotNull Skin skin) {
        this.skin = skin;
    }

    /**
     * A method that builds a profile for a {@link TabElement}
     * Method stolen from <a href="https://github.com/thekeenant/tabbed/blob/78cc6d22e7bf1abb6a3f6e1a9bf7af876da40144/core/src/main/java/com/keenant/tabbed/tablist/SimpleTabList.java#LL297C32-L297C46">...</a>
     *
     * @param index the index the element is meant to be
     * @return the player data
     */
    @NotNull
    public PlayerInfoData build(int index) {
        String name = String.format("%03d", index) + "|UpdateMC";
        UUID uuid = UUID.nameUUIDFromBytes(name.getBytes());

        WrappedGameProfile profile = new WrappedGameProfile(uuid, name);
        profile.getProperties().put("textures", new WrappedSignedProperty("textures", this.skin.getValue(), this.skin.getSignature()));

        return new PlayerInfoData(profile, this.ping.getLatency(), EnumWrappers.NativeGameMode.SURVIVAL, WrappedChatComponent.fromText(this.text));
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TabElement element)) {
            return false;
        }

        return this.text.equals(element.getText()) && this.ping.equals(element.getPing()) && this.skin.equals(element.getSkin());
    }

    /**
     * An enum which represents the ping a {@link TabElement} can have
     */
    public enum Ping {
        PERFECT(5, 0),
        GOOD(4, 150),
        OK(3, 300),
        BAD(2, 600),
        VERY_BAD(1, 1000);

        private final int bars;
        private final int latency;

        Ping(int bars, int latency) {
            this.bars = bars;
            this.latency = latency;
        }

        /**
         * A method used to get the amount of bars this connection has
         *
         * @return the amount of bars this connection has
         */
        public int getBars() {
            return this.bars;
        }

        /**
         * A method used to get the minimum latency (in milliseconds) a player can have with each ping
         *
         * @return the maximum latency a player can have with each ping
         */
        public int getLatency() {
            return this.latency;
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
    }

    /**
     * Represents a skin
     * Default implementations stolen from <a href="https://github.com/thekeenant/tabbed/blob/master/core/src/main/java/com/keenant/tabbed/util/Skins.java">...</a>
     */
    public static class Skin {
        private final String value;
        private final String signature;

        public static final Skin BLANK = new Skin("eyJ0aW1lc3RhbXAiOjE0MTEyNjg3OTI3NjUsInByb2ZpbGVJZCI6IjNmYmVjN2RkMGE1ZjQwYmY5ZDExODg1YTU0NTA3MTEyIiwicHJvZmlsZU5hbWUiOiJsYXN0X3VzZXJuYW1lIiwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzg0N2I1Mjc5OTg0NjUxNTRhZDZjMjM4YTFlM2MyZGQzZTMyOTY1MzUyZTNhNjRmMzZlMTZhOTQwNWFiOCJ9fX0=", "u8sG8tlbmiekrfAdQjy4nXIcCfNdnUZzXSx9BE1X5K27NiUvE1dDNIeBBSPdZzQG1kHGijuokuHPdNi/KXHZkQM7OJ4aCu5JiUoOY28uz3wZhW4D+KG3dH4ei5ww2KwvjcqVL7LFKfr/ONU5Hvi7MIIty1eKpoGDYpWj3WjnbN4ye5Zo88I2ZEkP1wBw2eDDN4P3YEDYTumQndcbXFPuRRTntoGdZq3N5EBKfDZxlw4L3pgkcSLU5rWkd5UH4ZUOHAP/VaJ04mpFLsFXzzdU4xNZ5fthCwxwVBNLtHRWO26k/qcVBzvEXtKGFJmxfLGCzXScET/OjUBak/JEkkRG2m+kpmBMgFRNtjyZgQ1w08U6HHnLTiAiio3JswPlW5v56pGWRHQT5XWSkfnrXDalxtSmPnB5LmacpIImKgL8V9wLnWvBzI7SHjlyQbbgd+kUOkLlu7+717ySDEJwsFJekfuR6N/rpcYgNZYrxDwe4w57uDPlwNL6cJPfNUHV7WEbIU1pMgxsxaXe8WSvV87qLsR7H06xocl2C0JFfe2jZR4Zh3k9xzEnfCeFKBgGb4lrOWBu1eDWYgtKV67M2Y+B3W5pjuAjwAxn0waODtEn/3jKPbc/sxbPvljUCw65X+ok0UUN1eOwXV5l2EGzn05t3Yhwq19/GxARg63ISGE8CKw=");
        public static final Skin GOLD = new Skin("eyJ0aW1lc3RhbXAiOjE0NTI0MTc5NDUxMzEsInByb2ZpbGVJZCI6ImU4MjYwM2RmNDE3ZDRhOTViZDFmMTcyMDY0OGJlMGI0IiwicHJvZmlsZU5hbWUiOiJQYWJsZXRlMTIzNCIsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9jOGM0ZDQ0OGIxZTEzYTI1ZGUzODg0ZGIxN2Q5YjBlZDM4ZGY1ZDEyM2RlNTc3M2YzODIyZWJiNTNhNzYxZTMzIn19fQ==", "gdr3b0Zy/uOlU4VdDuAmnPGhKlg9qImK4zj0qoisJZfiVYrPNPeQCocxVvnkAYqsuTsoe7UUo5/oW/G6Z6AKw+2aapkNbUSwxhdCb2vLmnIt8WGhxTxKcd2OEdCnAmCNgtcF8kF062yK8Enoni2eJI2oV+7MektoFWV5pWBkSmhNMBuw5AraYv9S0+zJTYjX0eANTuNXV+VKnfzMCcKuyOBwqXhNMzL9vmvXTMBJAr9bYx/xH3POO5xhGRrW4NyuWSE9SXhV4NngSR+59kImfZmSA6d9kuK26feZrfRrAME/UV2rjbnT4WWumYzvrroZKJBcq++yBEsVluEagkWzs8UXOtOiNYttp4ETg19aYObXdQSLGFRzTeVCVw4cHVSX6Svbiie/Kyr+s/5fQX7/LCs7uVlclPMrabQiam/DzDRre3hbEHXTfiUCLFQLjyqOQ1+gsPqWN2E/HMj0I9gbKL6qrgRVstvTf97UXqXxXudbOC3EthdgAM4n/lR8s6RqmqwzfdkWAyvYGW2c49tImnEtaltwhzeprURNy/dEbLkU3KYfXx2nVHO7+d67WJscwHiffyxDpLwTWkclIrC7bl2SKyfib1cElDqzXNzKYeqZ595PkiHeBBRjL9CWLTlXcLMWuJtdqvquCXt6oJ7EtalcVhKE6DHXdtBDDYDaWpA=");
        public static final Skin GREEN = new Skin("eyJ0aW1lc3RhbXAiOjE0NTI0MTgzMjE5NTAsInByb2ZpbGVJZCI6ImU4MjYwM2RmNDE3ZDRhOTViZDFmMTcyMDY0OGJlMGI0IiwicHJvZmlsZU5hbWUiOiJQYWJsZXRlMTIzNCIsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9jMGQzODkzMjYyMWI5NTIzZTlkODdjODcxMzEyNTk3ZTU0Y2QyMjRjODQ0NmY2NTE0ODY1ODE0Y2RlMzhhMTIifX19", "huZ5VsNJVXyj1jijC2KE3rE/XbF47jfj/dApSKRqMbTmHQhi3AEFUAetgN0TsBJWaynz/bgADVTY84WsDeeZyW6u1FZHQtOyvq6zxVCw2L1tFrjO7Ts0AYXpNvaZawz+r9OuM01Y62z9oK4VwA7e9oFHDSuo4mExcTgd35cqyxJmqNA2k5xMh88BBiKumJNTenzTEqfQaaWesSmRJWnxZ09zZKhZb2E0m4ekymZPKZWPuxxfOenaFWlpyltLnx/2pC7VRkG1v+zoBe/VmfsEtu5qWPPS8UPtg1Fpx+Q3GtxIApGj0Ni/DiKkaOmOY/5HH9uYD4BtJ3NjXEFCWbkQOXEgLrgwdstRVL53opC3+07QZTbnXVNA2Ua76Gu6T8j+KMxpA0+q0nS+FQZCL2TEt2Pm9nsAZx43kvQ/iA3hnM1hZ64jSQ4nj38mUJ7bnmqM2bcZpQMtIDzwMwswMLh9/jpYFZBK9p5tG2TW91RjSne6R0sOyzZyfOBX/T8oNohBCSVokD4+8SGyBGzknUb1VE0YOZ5HOj3N7agGxhWyPB6DrYCUT8hljtezFO+iPBSBLVo2yuX1PMrrQYB9ir+rTc7mjOYWmL6ENrkSN52fBDCd4yLhPZyU0AP4ov+Nl175c32e5f8Ihhz7IRnVhNDzOE12WS55ynn1IofblMSgfPU=");
        public static final Skin YELLOW = new Skin("eyJ0aW1lc3RhbXAiOjE0NTI0MTg0MDE0NzAsInByb2ZpbGVJZCI6ImU4MjYwM2RmNDE3ZDRhOTViZDFmMTcyMDY0OGJlMGI0IiwicHJvZmlsZU5hbWUiOiJQYWJsZXRlMTIzNCIsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8zYzY2ZDFmMDhkZmRmZTY2ZjljZDExYjA1MmY0YzM1YTdkMjk0MWZmMjNhOTRjY2UyM2Y3ZjgyMTg1NzcyZDgifX19", "Rs2GuNrHjFBLk09ymZ/3UpTNSbbjmnZ3WvzA8n0X0gwzNHAx+8u/pNWO2uMEW1TZE5fhmrRAb8krfEpr/D5RdeXl+Se7NRch4mxpWqz4jiipMigFgGnf/s0JY+/dn5amGnoHKzqsktHFx3qwwOjGaz2jj1vhysuzQLGptnurn9wnRgVIueWfR2Cctc0v1pJ9jBVx/gkG3N+2Wznml+50pphxhcYBtfUKtwnMRxHIOz0me1KuRhqmBtmMzoQUArJXiz7cAX8cRlTqUg2ilY4UYLxNsH3cyaJi//tOzpk7EEwo2W1vYT/ZqiHTUvDBeRSu4Or9YZ7TwF/klbSnZJqaC2X0du00QcaoGAvFPY+A9HXZ3QII7k4g0M+aI3huiDUQX24O3p9h4J4dKJpktq1FH0G271uf5m3DMQlAQHTJWP07r3rL/23hAALMtIjE4SvUANd+WBxAeAlgSQamWk2YKQv/TxnNlr6tZMKOz/L1xsXJdn4eATWO786wH5IlxHKwgIQnmEYPSZX3AYsGtIsYuQhttbjmqYiecNdywXy6/WpZwhUHWW2aKuvkczJ8kX5Mcq+viVQRWRJNsCwqwzeXMLEX5tpNGOlQAyRJ/lyGUYJKmEXyqWXIxJstbJG09OR/G9V53ZhNa1hEaaVGv1FwZ9WdH1xHhGyJySz9JtrmKQ0=");

        public Skin(@NotNull String value, @NotNull String signature) {
            this.value = value;
            this.signature = signature;
        }

        /**
         * Returns base64 encoded string of texture json
         *
         * @return base64 encoded string of texture json
         */
        @NotNull
        public String getValue() {
            return this.value;
        }

        /**
         * Returns base64 encoded signature provided by mojang
         *
         * @return base64 encoded signature provided by mojang
         */
        @NotNull
        public String getSignature() {
            return this.signature;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Skin skin)) {
                return false;
            }

            return this.value.equals(skin.getValue()) && this.signature.equals(skin.getSignature());
        }

        /**
         * A method used to get the skin of the provided player
         *
         * @param player the player
         * @return their skin
         */
        @NotNull
        public static Skin getSkin(@NotNull Player player) {
            WrappedGameProfile profile = WrappedGameProfile.fromPlayer(player);
            Optional<WrappedSignedProperty> textures = profile.getProperties().get("textures").stream().findAny();

            if (!textures.isPresent()) {
                throw new IllegalStateException("No textures exist on " + player.getName());
            }

            WrappedSignedProperty texture = textures.get();

            return new Skin(texture.getValue(), texture.getSignature());
        }
    }
}
