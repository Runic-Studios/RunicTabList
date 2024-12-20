package com.runicrealms.plugin.runictablist.tab;

import com.runicrealms.plugin.RunicCore;
import com.runicrealms.plugin.common.util.ColorUtil;
import com.runicrealms.plugin.common.util.Pair;
import com.runicrealms.plugin.party.Party;
import com.runicrealms.plugin.rdb.RunicDatabase;
import com.runicrealms.plugin.runicguilds.RunicGuilds;
import com.runicrealms.plugin.runicguilds.model.GuildInfo;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A tab list for a user on runic realms
 *
 * @author BoBoBalloon
 */
public final class RunicRealmsTabList extends TabList {
    private static final List<String> RANK_COLOR_ORDER = List.of(
            ChatColor.DARK_RED.toString(),
            ChatColor.RED.toString(),
            ChatColor.LIGHT_PURPLE.toString(),
            ChatColor.DARK_GREEN.toString(),
            ChatColor.GREEN.toString(),
            ChatColor.DARK_PURPLE.toString(),
            ChatColor.YELLOW.toString(),
            ChatColor.BLUE.toString(),
            ChatColor.AQUA.toString(),
            ChatColor.GOLD.toString(),
            ChatColor.WHITE.toString(),
            ChatColor.GRAY.toString()
    );
    private static final TabElement EMPTY_PARTY = new TabElement("&a&l  Party [0]", TabElement.Ping.PERFECT, TabElement.Skin.GREEN);
    private static final TabElement EMPTY_GUILD = new TabElement("&6&l  Guild [0]", TabElement.Ping.PERFECT, TabElement.Skin.GOLD);

    public RunicRealmsTabList(@NotNull Player player) {
        super(player, "&r&d&lRunic Realms&r " + RunicCore.VERSION_NUMBER + "\n&r" + RunicCore.VERSION_TITLE,
                "&2Our Website: &awww.runicrealms.com\n"
                        + "&5Our Discord: &5discord.gg/5FjVVd4");
    }

    /**
     * A method that returns the color of the user's health in the tab display while in a party
     *
     * @param player the user to get health from
     * @return the color of the user's health in the tab display while in a party
     */
    @NotNull
    private static ChatColor getHealthChatColor(@NotNull Player player) {
        int healthToDisplay = (int) (player.getHealth());
        int maxHealth = (int) player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        double healthPercent = (double) healthToDisplay / maxHealth;
        ChatColor chatColor;
        if (healthPercent >= .75) {
            chatColor = ChatColor.GREEN;
        } else if (healthPercent >= .5) {
            chatColor = ChatColor.YELLOW;
        } else if (healthPercent >= .25) {
            chatColor = ChatColor.RED;
        } else {
            chatColor = ChatColor.DARK_RED;
        }
        return chatColor;
    }

    /**
     * A method that returns the color of the user's name
     *
     * @param player the user
     * @return the color of the user's name
     */
    @NotNull
    private static String getTablistNameColor(@NotNull Player player) {
        User lpUser = LuckPermsProvider.get().getUserManager().getUser(player.getUniqueId());

        String nameColor;
        if (lpUser != null) {
            String color = lpUser.getCachedData().getMetaData().getMetaValue("name_color");
            nameColor = color != null ? ColorUtil.format(color) : ChatColor.WHITE.toString();
        } else {
            nameColor = ChatColor.WHITE.toString();
        }

        if (nameColor.equalsIgnoreCase(ChatColor.GRAY.toString())) {
            nameColor = ChatColor.WHITE.toString();
        }

        return nameColor;
    }

    /**
     * A method that returns the collection of players in sorted order
     *
     * @param players the players to be sorted
     * @return the collection of players in sorted order
     */
    @NotNull
    private static List<Pair<? extends Player, String>> sortPlayersByRank(@NotNull Collection<? extends Player> players) {
        Map<Player, String> playerRankColors = new HashMap<>();
        for (Player player : players) {
            playerRankColors.put(player, getTablistNameColor(player));
        }

        List<? extends Player> playersList = new ArrayList<>(players);
        playersList.sort((playerOne, playerTwo) -> {
            int indexOne = RANK_COLOR_ORDER.indexOf(playerRankColors.get(playerOne));
            int indexTwo = RANK_COLOR_ORDER.indexOf(playerRankColors.get(playerTwo));
            if (indexOne == -1) indexOne = Integer.MAX_VALUE;
            if (indexTwo == -1) indexTwo = Integer.MAX_VALUE;

            int compare = Integer.compare(indexOne, indexTwo);

            return compare != 0 ? compare : playerOne.getName().compareTo(playerTwo.getName());
        });

        for (Player player : playerRankColors.keySet()) {
            if (RunicCore.getVanishAPI().getVanishedPlayers().contains(player)) {
                playerRankColors.put(player, playerRankColors.get(player) + ChatColor.ITALIC);
            }
        }

        List<Pair<? extends Player, String>> finalList = new ArrayList<>(players.size());
        for (Player player : playersList) {
            finalList.add(new Pair<>(player, playerRankColors.get(player) + player.getName()));
        }

        return finalList;
    }

    /**
     * A method that checks if the player has selected a character
     *
     * @param player the player
     * @return if the player has selected a character
     */
    private static boolean hasSelectedCharacter(@NotNull Player player) {
        return RunicDatabase.getAPI().getCharacterAPI().getCharacterSlot(player.getUniqueId()) != -1;
    }

    @Override
    public void update() {
        if (!hasSelectedCharacter(this.getPlayer())) {
            IntStream.range(0, 80).forEach(this::remove);
            super.update();
            return;
        }

        //make updates here
        this.set(new TabElement("&e&l  Online [" + Bukkit.getOnlinePlayers().stream().filter(RunicRealmsTabList::hasSelectedCharacter).filter(player -> this.getPlayer().hasPermission("runiccore.vanish") || !RunicCore.getVanishAPI().getVanishedPlayers().contains(player)).count() + "]", TabElement.Ping.PERFECT, TabElement.Skin.YELLOW), 0);

        // Fill column with online players, stop after second column
        try {
            Iterator<Pair<? extends Player, String>> iterator = sortPlayersByRank(
                    Bukkit.getOnlinePlayers()
                            .stream()
                            .filter(RunicRealmsTabList::hasSelectedCharacter)
                            .filter(player ->
                                    this.getPlayer().hasPermission("runiccore.vanish")
                                            || !RunicCore.getVanishAPI().getVanishedPlayers().contains(player)).toList())
                    .iterator();
            for (int j = 0; j < 2; j++) {
                for (int i = j == 0 ? 1 : 0; i < 20; i++) {
                    Pair<? extends Player, String> online = null;
                    while (iterator.hasNext() && online == null) {
                        online = iterator.next();
                    }

                    if (online != null) {
                        this.set(TabElement.fromPlayer(online.first, online.second), j, i);
                    } else {
                        this.remove(j, i);
                    }
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        Party party = RunicCore.getPartyAPI().getParty(this.getPlayer().getUniqueId());
        if (party != null) {
            this.set(new TabElement("&a&l  Party [" + party.getSize() + "]", TabElement.Ping.PERFECT, TabElement.Skin.GREEN), 2, 0);

            List<Pair<? extends Player, String>> sortedParty = sortPlayersByRank(party.getMembersWithLeader());
            for (int i = 0; i < 20; i++) {
                if (i >= sortedParty.size()) {
                    this.remove(2, i + 1);
                    continue;
                }

                Pair<? extends Player, String> sortedMember = sortedParty.get(i);

                Player member = sortedMember.first;
                String memberColoredName = sortedMember.second;
                this.set(TabElement.fromPlayer(member, memberColoredName + " " + getHealthChatColor(member) + (int) member.getHealth() + "❤"), 2, i + 1);
            }
        } else {
            this.set(RunicRealmsTabList.EMPTY_PARTY, 2, 0);
            IntStream.range(1, 20).forEach(i -> this.remove(2, i));
        }

        GuildInfo guild = RunicGuilds.getDataAPI().getGuildInfo(this.getPlayer());

        if (guild == null) {
            this.set(RunicRealmsTabList.EMPTY_GUILD, 3, 0);
            IntStream.range(1, 20).forEach(i -> this.remove(3, i));
            super.update();
            return;
        }

        Set<UUID> members = guild.getMembersUuids();
        Set<Player> onlineMembers = members.stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .filter(RunicRealmsTabList::hasSelectedCharacter)
                .filter(player -> this.getPlayer().hasPermission("runiccore.vanish") || !RunicCore.getVanishAPI().getVanishedPlayers().contains(player))
                .collect(Collectors.toSet());

        this.set(new TabElement("&6&l  Guild [" + onlineMembers.size() + "]", TabElement.Ping.PERFECT, TabElement.Skin.GOLD), 3, 0);

        List<Pair<? extends Player, String>> guildMembers = sortPlayersByRank(onlineMembers);
        for (int i = 0; i < 20; i++) {
            if (i >= guildMembers.size()) {
                this.remove(3, i + 1);
                continue;
            }

            Pair<? extends Player, String> guildMember = guildMembers.get(i);

            this.set(TabElement.fromPlayer(guildMember.first, guildMember.second), 3, i + 1);
        }

        super.update();
    }

}