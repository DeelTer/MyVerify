package ru.deelter.verify.database;

import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLite extends Database {

	private static final String database = "database";
	private static File dataFolder;

	public SQLite(File folder) {
		dataFolder = new File(folder, database + ".db");
		if (!dataFolder.exists()) {
			try {
				dataFolder.createNewFile();
			} catch (IOException e) {
				Bukkit.getLogger().info("File write error: " + database + ".db");
			}
		}
	}

	public static Connection getConnection() {
		try {
			return DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
