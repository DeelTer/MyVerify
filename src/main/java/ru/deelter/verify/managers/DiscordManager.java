package ru.deelter.verify.managers;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import ru.deelter.verify.database.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class DiscordManager {

    public static UUID getUUID(String id) {
        String sql = "SELECT * FROM ACCOUNTS WHERE ID = '" + id + "';";
        try (Connection con = Database.openConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            String stringUUID = rs.getString("UUID");
            return UUID.fromString(stringUUID);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static OfflinePlayer getOfflinePlayer(String id) {
        UUID uuid = getUUID(id);
        if (uuid == null)
            return null;

        return Bukkit.getOfflinePlayer(uuid);
    }

    public static long getId(UUID uuid) {
        String sql = "SELECT * FROM ACCOUNTS WHERE UUID = '" + uuid.toString() + "';";
        try (Connection con = Database.openConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            return rs.getLong("ID");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return -1;
    }
}
