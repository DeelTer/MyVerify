package ru.deelter.verify;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class Config {

    /* Bot settings */
    public static String VERIFY_CHANNEL;
    public static String GUILD_ID;
    public static String TOKEN;

    /* Nickname settings */
    public static boolean NICKNAME_ENABLE;
    public static boolean NICKNAME_UPDATER_ENABLE;

    /* Roles settings */
    public static List<String> ROLES = new ArrayList<>();
    public static boolean ROLES_ENABLE = false;

    /* Messages */
    public static String MSG_PLAYER_OFFLINE;
    public static String MSG_ACCOUNT_EXISTS;
    public static String MSG_APPLICATION_EXISTS;
    public static String MSG_APPLICATION_CREATED;

    /* Debug */
    public static boolean DEBUG;

    public static void reload() {
        VerifyReload.getInstance().reloadConfig();

        FileConfiguration config = VerifyReload.getInstance().getConfig();
        DEBUG = config.getBoolean("debug");

        ConfigurationSection settings = config.getConfigurationSection("settings");
        GUILD_ID = settings.getString("guild");

        ConfigurationSection bot = settings.getConfigurationSection("bot");
        TOKEN = bot.getString("token");

        ConfigurationSection channels = settings.getConfigurationSection("channels");
        VERIFY_CHANNEL = channels.getString("verify");

        /* Nickname settings */
        ConfigurationSection nickname = settings.getConfigurationSection("nickname");
        NICKNAME_ENABLE = nickname.getBoolean("enable");
        NICKNAME_UPDATER_ENABLE = nickname.getBoolean("updater-enable");

        /* Roles settings */
        ConfigurationSection roles = settings.getConfigurationSection("roles");
        ROLES_ENABLE = roles.getBoolean("enable");
        ROLES = roles.getStringList("id");

        /* Messages */
        ConfigurationSection messages = config.getConfigurationSection("messages");
        ConfigurationSection discord = messages.getConfigurationSection("discord");
        MSG_PLAYER_OFFLINE = discord.getString("player-offline");
        MSG_ACCOUNT_EXISTS = discord.getString("account-exists");
        MSG_APPLICATION_EXISTS = discord.getString("application-exists");
        MSG_APPLICATION_CREATED = discord.getString("application-created");
    }
}