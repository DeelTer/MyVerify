package ru.deelter.verify;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import ru.deelter.verify.commands.VerifyCommand;
import ru.deelter.verify.discord.VerifyBot;
import ru.deelter.verify.player.DiscordPlayerAuthListener;
import ru.deelter.verify.player.DiscordDatabase;
import ru.deelter.verify.player.DiscordPlayerManager;

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
        VerifyBot.load();

        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new DiscordPlayerAuthListener(), this);

        /* Commands register */
        PluginCommand verifyCommand = getCommand("discordverify");
        if (verifyCommand != null) {
            verifyCommand.setExecutor(new VerifyCommand());
            verifyCommand.setTabCompleter(new VerifyCommand());
        }
    }

    @Override
    public void onDisable() {
        DiscordPlayerManager.saveCacheToDatabase();
    }

    public static MyVerify getInstance() {
        return instance;
    }
}
