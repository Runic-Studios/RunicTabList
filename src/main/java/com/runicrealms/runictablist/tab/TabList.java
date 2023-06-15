package com.runicrealms.runictablist.tab;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import com.runicrealms.runictablist.RunicTabList;
import com.runicrealms.runictablist.util.PacketUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A class that represents a tab list sent to a player
 */
public class TabList {
    private final Player player;
    private final Map<Integer, TabElement> elements;
    private final Map<Integer, TabElement> clientIcons;
    private String header;
    private String footer;

    public static final int MAXIMUM_ITEMS = 4 * 20; //client maximum is 4x20 (4 columns, 20 rows)
    private static final List<PlayerInfoData> BLANKS = IntStream.range(0, TabList.MAXIMUM_ITEMS).mapToObj(i -> TabList.build(TabElement.BLANK, i)).collect(Collectors.toUnmodifiableList());

    public TabList(@NotNull Player player, @Nullable String header, @Nullable String footer) {
        this.player = player;
        this.elements = new HashMap<>();
        this.clientIcons = new HashMap<>();
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
     * A method that sets the given index to a tab element
     *
     * @param element the tab element
     * @param index   the index to set it to
     */
    public void set(@NotNull TabElement element, int index) {
        if (index < 0 || index >= TabList.MAXIMUM_ITEMS) {
            return;
        }

        this.elements.put(index, Objects.requireNonNull(element));
    }

    /**
     * A method that sets the given index to a tab element
     *
     * @param index the index to set it to
     */
    public void remove(int index) {
        this.elements.remove(index);
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
        Bukkit.getScheduler().runTaskAsynchronously(RunicTabList.getInstance(), () -> {
            PacketContainer headerAndFooter = PacketUtil.getHeaderAndFooterPacket(this.header, this.footer);

            if (this.elements.equals(this.clientIcons) && !this.clientIcons.isEmpty()) {
                PacketUtil.send(this.player, headerAndFooter);
                return;
            }

            List<PlayerInfoData> newPlayers = new ArrayList<>();
            List<UUID> removePlayers = new ArrayList<>();
            List<PlayerInfoData> updateName = new ArrayList<>();
            List<PlayerInfoData> updatePing = new ArrayList<>();

            for (int i = 0; i < TabList.MAXIMUM_ITEMS; i++) {
                TabElement push = this.elements.get(i);
                TabElement current = this.clientIcons.get(i);

                if (i == 0) Bukkit.broadcastMessage("---------------------");
                if (i == 0)
                    Bukkit.broadcastMessage(push == null ? "false" : String.valueOf(push.getSkin().equals(TabElement.Skin.GOLD)));

                //if something does not exist for the first time, add a blank
                if (push == null && current == null && this.clientIcons.isEmpty()) {
                    newPlayers.add(TabList.BLANKS.get(i));
                    if (i == 0) Bukkit.broadcastMessage("1"); //REMOVE
                    continue;
                }

                //if something exists but both are blank, the client already knows this, do nothing
                if (push == null && current == null) {
                    if (i == 0) Bukkit.broadcastMessage("2");
                    continue;
                }

                //this must mean that current != null, therefore we need to delete what the client has here
                if (push == null) {
                    removePlayers.add(UUID.nameUUIDFromBytes(TabList.getFakeName(i).getBytes()));
                    newPlayers.add(TabList.BLANKS.get(i));
                    if (i == 0) Bukkit.broadcastMessage("3");
                    continue;
                }

                //this must mean that push != null, therefore we need to update the client to what is in elements
                if (current == null) {
                    removePlayers.add(UUID.nameUUIDFromBytes(TabList.getFakeName(i).getBytes()));
                    newPlayers.add(TabList.build(push, i));
                    if (i == 0) {
                        Bukkit.broadcastMessage("4");
                    }
                    continue;
                }

                //if they are both not null, and both the same that means the client already knows this, do nothing
                if (push.equals(current)) { //push != null && current != null always true
                    if (i == 0) Bukkit.broadcastMessage("5");
                    continue;
                }

                PlayerInfoData data = TabList.build(push, i);

                //if skin is not the same it must be updated so the player needs to be removed and added again
                if ((push.getSkin() == null) != (current.getSkin() == null) || !push.getSkin().equals(current.getSkin())) {
                    removePlayers.add(UUID.nameUUIDFromBytes(TabList.getFakeName(i).getBytes()));
                    newPlayers.add(data);
                    if (i == 0) Bukkit.broadcastMessage("6");
                    continue; //this process covers all the bases so the next two are just not necessary
                }

                //if the text is not the same, update text
                if (!push.getText().equals(current.getText())) {
                    if (i == 0) Bukkit.broadcastMessage("7");
                    updateName.add(data);
                }

                //if ping is not the same, update ping
                if (push.getPing() != current.getPing()) {
                    if (i == 0) Bukkit.broadcastMessage("8");
                    updatePing.add(data);
                }
            }

            PacketContainer removeIcons = !this.clientIcons.isEmpty() ? PacketUtil.getRemovePacket(removePlayers) : null;
            PacketContainer addIcons = PacketUtil.getAddPacket(newPlayers);
            PacketContainer updateNames = !updateName.isEmpty() ? PacketUtil.getAddPacket(updateName, EnumSet.of(EnumWrappers.PlayerInfoAction.UPDATE_DISPLAY_NAME)) : null;
            PacketContainer updatePings = !updatePing.isEmpty() ? PacketUtil.getAddPacket(updatePing, EnumSet.of(EnumWrappers.PlayerInfoAction.UPDATE_LATENCY)) : null;

            PacketUtil.send(this.player, headerAndFooter, removeIcons, addIcons, updateNames, updatePings);

            this.clientIcons.clear();
            this.clientIcons.putAll(this.elements);
        });
    }

    /**
     * A method that builds a profile for a {@link TabElement}
     * Method stolen from <a href="https://github.com/thekeenant/tabbed/blob/78cc6d22e7bf1abb6a3f6e1a9bf7af876da40144/core/src/main/java/com/keenant/tabbed/tablist/SimpleTabList.java#LL297C32-L297C46">...</a>
     *
     * @param element the element to build data for
     * @param index   the index the element is meant to be
     * @return the player data
     */
    @NotNull
    private static PlayerInfoData build(@NotNull TabElement element, int index) {
        String name = TabList.getFakeName(index);
        UUID uuid = UUID.nameUUIDFromBytes(name.getBytes());

        WrappedGameProfile profile = new WrappedGameProfile(uuid, name);
        if (element.getSkin() != null) {
            profile.getProperties().put("textures", new WrappedSignedProperty("textures", element.getSkin().getValue(), element.getSkin().getSignature()));
        }

        return new PlayerInfoData(profile, element.getPing().getLatency(), EnumWrappers.NativeGameMode.SURVIVAL, WrappedChatComponent.fromText(element.getText()));
    }

    /**
     * Get the fake name of the icon
     *
     * @param index the index the icon should have
     * @return the name of the game profile
     */
    @NotNull
    private static String getFakeName(int index) {
        return String.format("%03d", index) + "|UpdateMC";
    }
}
