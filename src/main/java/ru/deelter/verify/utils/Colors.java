package ru.deelter.verify.utils;

import net.md_5.bungee.api.ChatColor;

public class Colors {

    public static String set(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static String strip(String s) {
        return ChatColor.stripColor(s);
    }
}
