package ru.deelter.verify.utils;

import net.md_5.bungee.api.ChatColor;

import java.util.HashMap;
import java.util.Map;

public class Colors {

    private static final Map<String, ChatColor> colors = new HashMap<>();

    /** @return Color by ID */
    public static ChatColor get(String id) {
        return colors.get(id);
    }

    /** @return Colorized String with color placeholders */
    public static String set(String s) {
        for (Map.Entry<String, ChatColor> entry : colors.entrySet()) {
            s = s.replace("%" + entry.getKey() + "%", "" + entry.getValue());
        }
        return ChatColor.translateAlternateColorCodes('&', s);
    }
    /** Register custom color */
    public static void register(String id, String hex) {
        ChatColor color = ChatColor.of(hex);
        colors.putIfAbsent(id, color);
    }

    public static ChatColor from(String hex) {
        return ChatColor.of(hex);
    }

    /** @return Stripped String */
    public static String strip(String s) {
        return ChatColor.stripColor(s);
    }

    public static Map<String, ChatColor> getColors() {
        return colors;
    }
}
