package com.runicrealms.runictablist.util;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

/**
 * A utility function to format text
 */
public final class TextUtil {
    /**
     * Private constructor to prevent class being used in an OOP way
     */
    private TextUtil() {

    }

    /**
     * A method used to format text
     *
     * @param text the text to format
     * @return formatted string
     */
    @NotNull
    public static String format(@NotNull String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
