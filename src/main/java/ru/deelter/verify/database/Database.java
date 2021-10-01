package ru.deelter.verify.database;

import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.sql.SQLException;

public class Database {

	public static void setup(Plugin plugin) {
		Database database = new SQLite(plugin.getDataFolder());
		database.setupTables();
	}

	public void setupTables() {
		Connection con = Database.openConnection();
		try {
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

	public static Connection openConnection() {
		return SQLite.getConnection();
	}
}
