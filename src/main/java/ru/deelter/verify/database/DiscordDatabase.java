package ru.deelter.verify.database;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.deelter.verify.MyVerify;
import ru.deelter.verify.player.DiscordPlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class DiscordDatabase {

    private static final Database DATABASE = Database.setupDatabase("database", MyVerify.getInstance());

    @NotNull
    public static Database getDatabase() {
        return DATABASE;
    }

    public static void setupTables() {
        try (Connection con = DATABASE.openConnection()){
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

    /**
     * Get minecraft UUID by discord ID
     *
     * @param discordId discord id
     * @return null if error
     */
    @Nullable
    public static UUID getUUID(@NotNull String discordId) {
        synchronized (MyVerify.getInstance()) {
            String sql = "SELECT * FROM ACCOUNTS WHERE ID = '" + discordId + "';";
            try (Connection con = DATABASE.openConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
                ResultSet rs = ps.executeQuery();
                String stringUUID = rs.getString("UUID");
                return UUID.fromString(stringUUID);
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }

    @Nullable
    public static OfflinePlayer getOfflinePlayer(@NotNull String discordId) {
        UUID uuid = getUUID(discordId);
        return uuid != null ? Bukkit.getOfflinePlayer(uuid) : null;
    }

    /**
     * Get discord ID by minecraft UUID
     *
     * @param uuid minecraft uuid
     * @return -1 if error
     */
    public static long getDiscordId(@NotNull UUID uuid) {
        synchronized (MyVerify.getInstance()) {
            String sql = "SELECT * FROM ACCOUNTS WHERE UUID = '" + uuid + "';";
            try (Connection con = DATABASE.openConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
                ResultSet rs = ps.executeQuery();
                return rs.getLong("ID");
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }
        return -1;
    }

    /**
     * Update information about player
     * into database
     *
     * @param player discord player object
     */
    public static void updateInfo(@NotNull DiscordPlayer player) {
        synchronized (MyVerify.getInstance()) {
            Bukkit.getScheduler().runTaskAsynchronously(MyVerify.getInstance(), () -> {
                String sql = "INSERT OR REPLACE INTO ACCOUNTS(UUID,ID,IP,TIME) VALUES(?,?,?,?);";
                try (Connection con = DATABASE.openConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setString(1, player.getUUID().toString());
                    ps.setLong(2, player.getId());
                    ps.setString(3, player.getIp());
                    ps.setLong(4, player.getTime());
                    ps.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    /**
     * Remove player from database
     *
     * @param uuid player uuid
     */
    public static void deletePlayer(@NotNull UUID uuid) {
        synchronized (MyVerify.getInstance()) {
            Bukkit.getScheduler().runTaskAsynchronously(MyVerify.getInstance(), () -> {
                String sql = "DELETE FROM ACCOUNTS WHERE UUID = '" + uuid + "';";
                try (Connection con = DATABASE.openConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public static void deletePlayer(DiscordPlayer discordPlayer) {
        deletePlayer(discordPlayer.getUUID());
    }

    @NotNull
    public static DiscordPlayer exportPlayer(@NotNull UUID uuid) {
        synchronized (MyVerify.getInstance()) {
                String sql = "SELECT * FROM ACCOUNTS WHERE UUID = '" + uuid + "';";
                try (Connection con = DATABASE.openConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
                    ResultSet rs = ps.executeQuery();
                    if (!rs.next()) return new DiscordPlayer(uuid);

                    long id = rs.getLong("ID");
                    String ip = rs.getString("IP");
                    long time = rs.getLong("TIME");
                    return new DiscordPlayer(uuid, id, ip, time).register();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return new DiscordPlayer(uuid);
    }

    public static DiscordPlayer exportPlayer(Player player) {
        return exportPlayer(player.getUniqueId());
    }
}
