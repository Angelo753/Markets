package cz.angelo.angelmarkets.utils;

import org.bukkit.ChatColor;

public class Color {
    public static String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

}
