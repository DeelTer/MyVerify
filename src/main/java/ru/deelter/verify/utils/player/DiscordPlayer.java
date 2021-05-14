package ru.deelter.verify.utils.player;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.deelter.verify.MyVerify;
import ru.deelter.verify.api.DiscordNameChangeEvent;
import ru.deelter.verify.database.Database;
import ru.deelter.verify.discord.MyBot;
import ru.deelter.verify.utils.Console;

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

	private long id, time;

	public DiscordPlayer(UUID uuid) {
		this.uuid = uuid;
	}

	public DiscordPlayer(UUID uuid, long id, String ip, long time) {
		this.uuid = uuid;
		this.id = id;
		this.ip = ip;
		this.time = time;
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

	/** Is player linked */
	public boolean isLinked() {
		return id != 0;
	}

	/** Get online player */
	public Player getPlayer() {
		return Bukkit.getPlayer(uuid);
	}

	/** Get Discord member */
	public Member getMember() {
		return MyBot.getGuild().getMemberById(id);
	}

	/** Get player roles in Discord */
	public List<Role> getRoles() {
		return getMember().getRoles();
	}

	/** Set player role in Discord */
	public void setRole(String roleId, boolean needSet) {
		if (getMember() == null || getMember().isOwner())
			return;

		Guild guild = MyBot.getGuild();
		Role role = guild.getRoleById(roleId);
		if (role == null)
			return;

		if (needSet)
			guild.addRoleToMember(id, role).queue();
		else
			guild.removeRoleFromMember(id, role).queue();
	}

	/** Set Discord nickname to ... */
	public void setName(String name) {
		if (getMember() == null)
			return;

		if (getMember().isOwner())
			return;

		//modify nickname
		getMember().modifyNickname(name).queue();

		/* Call event for api */
		Bukkit.getScheduler().scheduleSyncDelayedTask(MyVerify.getInstance(), () -> {
			DiscordNameChangeEvent event = new DiscordNameChangeEvent(this, getMember().getNickname(), name);
			Bukkit.getPluginManager().callEvent(event);
		});
	}

	/** Send message in Discord */
	public void sendMessage(String message) {
		sendMessage(new EmbedBuilder().setDescription(message).build());
	}

	/** Send Embed message in Discord */
	public void sendMessage(MessageEmbed message) {
		Console.debug("Отправляем сообщение игроку " + getPlayer().getName());
		MyBot.getBot().openPrivateChannelById(id).queue(chat -> chat.sendMessage(message).queue());
	}

	/** Ban player on Discord server */
	public void ban(int delDays, String reason) {
		Console.debug("&fБаним игрока" + getPlayer().getName());
		getMember().ban(delDays, reason).queue();
	}

	/** Get Discord player by PLAYER */
	public static DiscordPlayer get(Player player) {
		return get(player.getUniqueId());
	}

	/** Get Discord player by UUID */
	public static DiscordPlayer get(UUID uuid) {
		return players.containsKey(uuid) ? players.get(uuid) : new DiscordPlayer(uuid).register();
	}

	/** Register Discord player */
	public DiscordPlayer register() {
		Bukkit.getScheduler().runTaskAsynchronously(MyVerify.getInstance(), () -> {
			String sql = "SELECT * FROM ACCOUNTS WHERE UUID = '" + uuid + "';";
			try (Connection con = Database.openConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
				ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					this.id = rs.getLong("ID");
					this.ip = rs.getString("IP");
					this.time = rs.getLong("TIME");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		});
		players.putIfAbsent(uuid, this);
		return this;
	}

	/** Unregister player from RAM */
	public void unregister() {
		players.remove(uuid);
	}

	/** Update player statistic in Database */
	public DiscordPlayer update() {
		Bukkit.getScheduler().runTaskAsynchronously(MyVerify.getInstance(), () -> {
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
		return this;
	}

	/** Remove player from Database */
	public void removeFromBase() {
		Bukkit.getScheduler().runTaskAsynchronously(MyVerify.getInstance(), () -> {
			String sql = "DELETE FROM ACCOUNTS WHERE UUID = '" + uuid + "';";
			try (Connection con = Database.openConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
				ps.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace(); }
		});
	}

	public static boolean contains(String id) {
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
