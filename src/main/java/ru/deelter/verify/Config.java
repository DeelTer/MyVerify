package ru.deelter.verify;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import ru.deelter.verify.utils.Colors;

import java.util.ArrayList;
import java.util.List;

public class Config {

    /* Bot settings */
    public static String VERIFY_CHANNEL_ID;
    public static String GUILD_ID;
    public static String TOKEN;

    /* Nickname settings */
    public static boolean NICKNAME_ENABLE;
    public static boolean NICKNAME_UPDATER_ENABLE;

    /* Roles settings */
    public static List<String> ROLES_ID = new ArrayList<>();
    public static boolean ROLES_ENABLE = false;

    /* Messages */
    public static String MSG_DS_PLAYER_OFFLINE;
    public static String MSG_DS_ACCOUNT_EXISTS;
    public static String MSG_DS_APPLICATION_EXISTS;
    public static String MSG_DS_APPLICATION_CREATED;
    public static String MSG_DS_UNLINKED;

    public static String MSG_MC_NO_PERM;
    public static String MSG_MC_RELOAD;
    public static String MSG_MC_NOT_LINKED;
    public static String MSG_MC_SUCCESS_UNLINK;
    public static String MSG_MC_NO_APPLICATIONS;
    public static String MSG_MC_SUCCESS_LINK;

    /* Debug */
    public static boolean DEBUG;

    public static void reload() {
        MyVerify.getInstance().reloadConfig();
        Colors.getColors().clear();
        load();
    }

    public static void load() {
        MyVerify.getInstance().reloadConfig();

        FileConfiguration config = MyVerify.getInstance().getConfig();
        DEBUG = config.getBoolean("debug");

        /* Custom colors */
        ConfigurationSection colors = config.getConfigurationSection("custom-colors");
        if (colors == null) return;

        colors.getKeys(false).forEach(id -> Colors.register(id, colors.getString(id)));

        /* Settings */
        ConfigurationSection settings = config.getConfigurationSection("settings");
        if (settings == null) return;

        ConfigurationSection bot = settings.getConfigurationSection("bot");
        if (bot == null) return;

        GUILD_ID = bot.getString("guild");
        TOKEN = bot.getString("token");

        ConfigurationSection channels = settings.getConfigurationSection("channels");
        if (channels == null) return;

        VERIFY_CHANNEL_ID = channels.getString("verify");

        /* Nickname settings */
        ConfigurationSection nickname = settings.getConfigurationSection("nickname");
        if (nickname == null) return;

        NICKNAME_ENABLE = nickname.getBoolean("enable");
        NICKNAME_UPDATER_ENABLE = nickname.getBoolean("updater-enable");

        /* Roles settings */
        ConfigurationSection roles = settings.getConfigurationSection("roles");
        if (roles == null) return;

        ROLES_ENABLE = roles.getBoolean("enable");
        Config.ROLES_ID = roles.getStringList("id");

        /* Messages */
        ConfigurationSection messages = config.getConfigurationSection("messages");
        if (messages == null) return;

        /* Discord messages */
        ConfigurationSection discord = messages.getConfigurationSection("discord");
        if (discord == null) return;

        MSG_DS_PLAYER_OFFLINE = discord.getString("player-offline");
        MSG_DS_ACCOUNT_EXISTS = discord.getString("account-exists");
        MSG_DS_APPLICATION_EXISTS = discord.getString("application-exists");
        MSG_DS_APPLICATION_CREATED = discord.getString("application-created");
        MSG_DS_UNLINKED = discord.getString("unlink");

        /* Minecraft messages */
        ConfigurationSection minecraft = messages.getConfigurationSection("minecraft");
        if (minecraft == null) return;

        MSG_MC_NO_PERM = Colors.set(minecraft.getString("no-perm"));
        MSG_MC_RELOAD = Colors.set(minecraft.getString("reload"));
        MSG_MC_NOT_LINKED = Colors.set(minecraft.getString("not-linked"));
        MSG_MC_SUCCESS_UNLINK = Colors.set(minecraft.getString("success-unlink"));
        MSG_MC_NO_APPLICATIONS = Colors.set(minecraft.getString("no-applications"));
        MSG_MC_SUCCESS_LINK = Colors.set(minecraft.getString("success-link"));
    }
}