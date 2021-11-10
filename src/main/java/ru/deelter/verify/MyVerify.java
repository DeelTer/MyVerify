package ru.deelter.verify;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import ru.deelter.verify.commands.VerifyCommand;
import ru.deelter.verify.discord.DiscordBot;
import ru.deelter.verify.player.DiscordPlayerAuth;
import ru.deelter.verify.database.DiscordDatabase;

public final class MyVerify extends JavaPlugin {

    private static MyVerify instance;

    @Override
    public void onLoad() {
        instance = this;
        saveDefaultConfig();
        DiscordDatabase.setupTables();
    }

    @Override
    public void onEnable() {
        Config.load();
        DiscordBot.load();

        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new DiscordPlayerAuth(), this);

        /* Commands register */
        PluginCommand verifyCommand = getCommand("discordverify");
        if (verifyCommand != null) {
            verifyCommand.setExecutor(new VerifyCommand());
            verifyCommand.setTabCompleter(new VerifyCommand());
        }
    }

    @Override
    public void onDisable() {

    }

    public static MyVerify getInstance() {
        return instance;
    }
}
