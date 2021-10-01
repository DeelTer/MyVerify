package ru.deelter.verify.database;

import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLite extends Database {

	private static final String DATABASE = "database";
	private final File dataFolder;

	public SQLite(File folder) {
		dataFolder = new File(folder, DATABASE + ".db");
		if (!dataFolder.exists()) {
			try {
				if (!dataFolder.createNewFile())
					Bukkit.getLogger().info("Could not create a database file!");
			} catch (IOException e) {
				Bukkit.getLogger().info("File write error: " + DATABASE + ".db");
			}
		}
	}

	@Override
	public Connection getConnection() throws SQLException {
		return DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
	}

	@Override
	public void close() {
		// We have nothing to close
	}

}
