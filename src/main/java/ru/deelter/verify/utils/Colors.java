package ru.deelter.verify.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
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

    /** @return Colorized Component with color placeholders */
    public static Component component(String s) {
        return LegacyComponentSerializer.legacySection().deserialize(set(s));
    }

    private static boolean hasCustomColors(String s) {
        for (Map.Entry<String, ChatColor> entry : colors.entrySet()) {
            if (s.contains("%" + entry.getKey() + "%"))
                return true;
        }
        return false;
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
