package ru.deelter.verify;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import ru.deelter.verify.commands.VerifyCommand;
import ru.deelter.verify.database.Database;
import ru.deelter.verify.discord.Bot;
import ru.deelter.verify.listeners.PlayerAuthListener;

public final class MyVerify extends JavaPlugin {

    private static MyVerify instance;

    @Override
    public void onLoad() {
        instance = this;
        saveDefaultConfig();
        Database.setupDatabase(this);
    }

    @Override
    public void onEnable() {
        Config.load();
        Bot.load();

        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new PlayerAuthListener(), this);

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
