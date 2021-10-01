package ru.deelter.verify.managers.discord;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import ru.deelter.verify.MyVerify;
import ru.deelter.verify.database.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class DiscordDatabaseManager {

    public static UUID getUUID(String id) {
        synchronized (MyVerify.getInstance()) {
            String sql = "SELECT * FROM ACCOUNTS WHERE ID = '" + id + "';";
            try (Connection con = Database.openConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
                ResultSet rs = ps.executeQuery();
                String stringUUID = rs.getString("UUID");
                return UUID.fromString(stringUUID);
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }

    public static OfflinePlayer getOfflinePlayer(String id) {
        UUID uuid = getUUID(id);
        return uuid != null ? Bukkit.getOfflinePlayer(uuid) : null;
    }

    public static long getId(UUID uuid) {
        synchronized (MyVerify.getInstance()) {
            String sql = "SELECT * FROM ACCOUNTS WHERE UUID = '" + uuid.toString() + "';";
            try (Connection con = Database.openConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
                ResultSet rs = ps.executeQuery();
                return rs.getLong("ID");
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }
        return -1;
    }
}
