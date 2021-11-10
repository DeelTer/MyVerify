package ru.deelter.verify.database;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class Database {

    private final String databaseName;

    public Database(@NotNull String name) {
        this.databaseName = name;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public Connection getConnection() throws SQLException {
        return openConnection();
    }

    public abstract Connection openConnection() throws SQLException;

    public abstract void closeConnection();

    /**
     * Setup MySQL or SQLite database
     *
     * @param databaseName name of database
     * @param plugin bukkit plugin
     * @param mySQL is MySQL database
     * @return database
     */
    @NotNull
    public static Database setupDatabase(@NotNull String databaseName, @NotNull Plugin plugin, boolean mySQL) {
        return mySQL ? new MySQL(databaseName, plugin.getConfig()) : new SQLite(databaseName, plugin);
    }

    /**
     * Setup SQLite database in specified plugin directory
     *
     * @param databaseName name of database
     * @param plugin bukkit plugin
     * @return SQLite database
     */
    @NotNull
    public static Database setupDatabase(@NotNull String databaseName, @NotNull Plugin plugin) {
        return setupDatabase(databaseName, plugin, false);
    }

    @NotNull
    public static Database setupDatabase(@NotNull Plugin plugin, boolean mySQL) {
        return setupDatabase("database", plugin, mySQL);
    }
}
