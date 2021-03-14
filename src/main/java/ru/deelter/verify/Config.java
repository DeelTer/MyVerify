package ru.deelter.verify;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class Config {

    public static List<String> ROLES = new ArrayList<>();

    public static String VERIFY_CHANNEL;
    public static String GUILD_ID;
    public static String TOKEN;

    public static String MSG_PLAYER_OFFLINE;
    public static String MSG_ACCOUNT_EXISTS;
    public static String MSG_APPLICATION_EXISTS;
    public static String MSG_APPLICATION_CREATED;

    public static void reload() {
        VerifyReload.getInstance().reloadConfig();

        FileConfiguration config = VerifyReload.getInstance().getConfig();
        ConfigurationSection settings = config.getConfigurationSection("settings");
        ROLES = settings.getStringList("roles");
        GUILD_ID = settings.getString("guild");

        ConfigurationSection bot = settings.getConfigurationSection("bot");
        TOKEN = bot.getString("token");

        ConfigurationSection channels = settings.getConfigurationSection("channels");
        VERIFY_CHANNEL = channels.getString("verify");

        ConfigurationSection messages = config.getConfigurationSection("messages");
        ConfigurationSection discord = messages.getConfigurationSection("discord");
        MSG_PLAYER_OFFLINE = discord.getString("player-offline");
        MSG_ACCOUNT_EXISTS = discord.getString("account-exists");
        MSG_APPLICATION_EXISTS = discord.getString("application-exists");
        MSG_APPLICATION_CREATED = discord.getString("application-created");
    }
}