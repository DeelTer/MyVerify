package ru.deelter.verify.database;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class Database {

	private static Database database;

	public static void setupDatabase(Plugin plugin) {
		if (plugin.getConfig().getBoolean("database.use-mysql")) {
			try {
				database = new MySQL(plugin.getConfig());
				return;
			} catch (Exception ex) {
				Bukkit.getLogger().info("Couldn't connect to the database! Using SQLite instead.");
			}
		}
		database = new SQLite(plugin.getDataFolder());
	}

	public static void closeDatabase() {
		database.close();
	}

	public void setupTables() {
		try (Connection con = Database.openConnection()){
			con.prepareStatement("CREATE TABLE IF NOT EXISTS `ACCOUNTS`("
					+ "`UUID` varchar(64) PRIMARY KEY,"
					+ "`ID` BIGINT NOT NULL,"
					+ "`IP` TEXT NOT NULL,"
					+ "`TIME` BIGINT NOT NULL);")
					.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static Connection openConnection() throws SQLException {
		return database.getConnection();
	}

	public abstract Connection getConnection() throws SQLException;

	public abstract void close();
}
