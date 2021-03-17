package ru.deelter.verify;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import ru.deelter.verify.commands.VerifyCommand;
import ru.deelter.verify.database.Database;
import ru.deelter.verify.discord.MyBot;
import ru.deelter.verify.utils.Console;
import ru.deelter.verify.utils.player.PlayerIdentification;

import java.io.File;

public final class MyVerify extends JavaPlugin {

    private static JavaPlugin instance;

    public static JavaPlugin getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        File config = new File(instance.getDataFolder().getPath() + "/config.yml");
        if (!config.exists()) {
            Console.log("&6Конфиг&f успешно загружен");
            saveDefaultConfig();
        }

        Database.setup(this);
        Config.reload();
        MyBot.load();

        /* Commands register */
        getCommand("discordverify").setExecutor(new VerifyCommand());

        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new PlayerIdentification(), this);
    }

    @Override
    public void onDisable() {
        MyBot.unload();
    }
}
