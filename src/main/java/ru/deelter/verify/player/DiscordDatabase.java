package ru.deelter.verify.player;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.deelter.verify.MyVerify;
import ru.deelter.verify.database.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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

    public static synchronized @NotNull DiscordPlayer load(UUID uuid) {
        String sql = "SELECT * FROM ACCOUNTS WHERE UUID = '" + uuid + "';";
        try (Connection con = DATABASE.openConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                long id = rs.getLong("ID");
                String ip = rs.getString("IP");
                long time = rs.getLong("TIME");
                return new DiscordPlayer(uuid, id, ip, time);
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return new DiscordPlayer(uuid);
    }

    public static synchronized void save(Collection<DiscordPlayer> players) {
        if (players.isEmpty()) return;
        List<String> requests = new ArrayList<>();
        for (DiscordPlayer player : players) {
            String sql = String.format("INSERT OR REPLACE INTO ACCOUNTS (" +
                            "UUID, " +
                            "ID, " +
                            "IP," +
                            "TIME" +
                            ") VALUES ('%s', '%s', '%s', '%s');",
                    player.getMinecraftId(),
                    player.getDiscordId(),
                    player.getIp(),
                    player.getTime()
            );
            requests.add(sql);
        }
        try (Connection con = DATABASE.openConnection()) {
            for (String request : requests) {
                try (PreparedStatement ps = con.prepareStatement(request)) {
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static synchronized void save(DiscordPlayer player) {
        Bukkit.getScheduler().runTaskAsynchronously(MyVerify.getInstance(), () -> {
            String sql = "INSERT OR REPLACE INTO ACCOUNTS(UUID,ID,IP,TIME) VALUES(?,?,?,?);";
            try (Connection con = DATABASE.openConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, player.getMinecraftId().toString());
                ps.setLong(2, player.getDiscordId());
                ps.setString(3, player.getIp());
                ps.setLong(4, player.getTime());
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public static synchronized @Nullable DiscordPlayer load(long discordId) {
        String sql = "SELECT * FROM ACCOUNTS WHERE ID = '" + discordId + "';";
        try (Connection con = DATABASE.openConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("UUID"));
                String ip = rs.getString("IP");
                long time = rs.getLong("TIME");
                return new DiscordPlayer(uuid, discordId, ip, time);
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public static synchronized void delete(@NotNull UUID uuid) {
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

    public static synchronized void delete(@NotNull DiscordPlayer player) {
        delete(player.getMinecraftId());
    }
}
