package ru.deelter.verify.database;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLite extends Database {

    private File dataFolder;
    private final File folder;

    public SQLite(@NotNull String databaseName, @NotNull File folder) {
        super(databaseName);
        this.folder = folder;
        setup();
    }

    public SQLite(@NotNull String databaseName, @NotNull Plugin plugin) {
        this(databaseName, plugin.getDataFolder());
    }

    private void setup() {
        dataFolder = new File(folder, getDatabaseName() + ".db");
        if (!dataFolder.exists()) {
            try {
                if (!dataFolder.createNewFile()) Bukkit.getLogger().info("Could not create a database file!");
            } catch (IOException e) {
                Bukkit.getLogger().info("File write error: " + getDatabaseName() + ".db");
            }
        }
    }

    @Override
    public Connection openConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
    }

    @Override
    public void closeConnection() {
        // Nothing to close
    }
}
