package ru.deelter.verify.utils.player;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.deelter.verify.Config;
import ru.deelter.verify.VerifyReload;
import ru.deelter.verify.database.Database;
import ru.deelter.verify.discord.MyBot;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DiscordPlayer {

	private static final Map<UUID, DiscordPlayer> players = new HashMap<>();
	private final UUID uuid;
	private String ip;

	private long id;
	private long time;

	public DiscordPlayer(UUID uuid) {
		this.uuid = uuid;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public boolean isLinked() {
		return id != 0;
	}

	public List<Role> getRoles() {
		Guild guild = MyBot.get().getGuildById(Config.GUILD_ID);
		return guild.getMemberById(id).getRoles();
	}

	public void setRole(String roleId) {
		Guild guild = MyBot.get().getGuildById(Config.GUILD_ID);
		Role role = guild.getRoleById(roleId);
		guild.addRoleToMember(id, role).queue();
	}

	public static DiscordPlayer get(Player player) {
		return get(player.getUniqueId());
	}

	public static DiscordPlayer get(UUID uuid) {
		if (!players.containsKey(uuid)) {
			DiscordPlayer dPlayer = new DiscordPlayer(uuid);
			dPlayer.register();
			return dPlayer;
		}
		return players.get(uuid);
	}

	public void register() {
		Bukkit.getScheduler().runTaskAsynchronously(VerifyReload.getInstance(), () -> {
			String sql = "SELECT * FROM ACCOUNTS WHERE UUID = '" + uuid + "';";
			try (Connection con = Database.openConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
				ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					this.id = rs.getLong("ID");
					this.time = rs.getLong("TIME");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		});
		players.putIfAbsent(uuid, this);
	}

	public void unregister() {
		players.remove(uuid);
	}

	/**
	 * Update player statistic in Database
	 */
	public void update() {
		Bukkit.getScheduler().runTaskAsynchronously(VerifyReload.getInstance(), () -> {
			String sql = "INSERT OR REPLACE INTO ACCOUNTS(UUID,ID,IP,TIME) VALUES(?,?,?,?);";
			try (Connection con = Database.openConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
				ps.setString(1, uuid.toString());
				ps.setLong(2, id);
				ps.setString(3, ip);
				ps.setLong(4, time);
				ps.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		});
	}

	public static boolean containsDiscord(String id) {
		String sql = "SELECT 1 FROM ACCOUNTS WHERE ID = `" + id + "`;";
		try (Connection con = Database.openConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
			ResultSet rs = ps.executeQuery();
			return rs.next();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
}
