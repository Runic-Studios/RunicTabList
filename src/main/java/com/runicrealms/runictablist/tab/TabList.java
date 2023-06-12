package com.runicrealms.runictablist.tab;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * A class that represents a tab list sent to a player
 */
public class TabList {
    private final List<TabElement> elements;
    private String header;
    private String footer;

    public TabList(@NotNull List<TabElement> elements, @Nullable String header, @Nullable String footer) {
        this.elements = elements;
        this.header = header;
        this.footer = footer;
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
     *
     * @param player the player the tab list should be sent to
     */
    public void update(@NotNull Player player) {
        //write implementation here
    }
}
