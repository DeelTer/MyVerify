package ru.deelter.verify.utils.player;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.deelter.verify.MyVerify;
import ru.deelter.verify.api.actions.DiscordBanEvent;
import ru.deelter.verify.api.actions.DiscordNameChangeEvent;
import ru.deelter.verify.database.Database;
import ru.deelter.verify.discord.Bot;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class DiscordPlayer {

	private static final Guild GUILD = Bot.getGuild();

	private static final Map<UUID, DiscordPlayer> PLAYERS = new HashMap<>();
	private final UUID uuid;
	private String ip;

	private long id, time;

	public DiscordPlayer(@NotNull UUID uuid) {
		this.uuid = uuid;
	}

	public DiscordPlayer(@NotNull UUID uuid, long id, String ip, long time) {
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

	public void setIp(@NotNull String ip) {
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

	/**
	 * Get discord member
	 * @return Member (can be null)
	 */
	@Nullable
	public Member getMember() {
		return GUILD.getMemberById(id);
	}

	/**
	 * Get all player roles
	 * @return Roles list
	 */
	@NotNull
	public List<Role> getRoles() {
		Member member = getMember();
		return member == null ? Collections.emptyList() : member.getRoles();
	}

	/**
	 * Get all player role ids
	 * @return Role ids list
	 */
	@NotNull
	public List<String> getRoleIds() {
		List<String> ids = new ArrayList<>();
		getRoles().forEach(role -> ids.add(role.getId()));
		return ids;
	}

	/**
	 * Add role to the player
	 * @param roleId Role id
	 */
	public boolean hasRole(@NotNull String roleId) {
		return getRoleIds().contains(roleId);
	}

	/**
	 * Remove player role
	 * @param roleId Role id
	 */
	public void removeRole(@NotNull String roleId) {
		setRole(roleId, false);
	}

	/**
	 * Add role to the player
	 * @param roleId Role id
	 */
	public void addRole(@NotNull String roleId) {
		setRole(roleId, true);
	}

	public void setRole(@NotNull String roleId, boolean add) {
		if (!isValid(getMember())) return;

		Role role = GUILD.getRoleById(roleId);
		if (role == null) return;

		if (add) GUILD.addRoleToMember(id, role).queue();
		else GUILD.removeRoleFromMember(id, role).queue();
	}

	/**
	 * Set name in discord
	 * @param name New discord name
	 */
	public void setName(@NotNull String name) {
		Member member = getMember();
		if (!isValid(member)) return;

		member.modifyNickname(name).queue(); //modify nickname
		Bukkit.getScheduler().scheduleSyncDelayedTask(MyVerify.getInstance(), () -> new DiscordNameChangeEvent(this, getMember().getNickname(), name).callEvent());
	}

	/**
	 * Send message to player discord
	 * @param message Message
	 */
	public void sendMessage(@NotNull String message) {
		sendMessage(new EmbedBuilder().setDescription(message).build());
	}

	/**
	 * Send message to player discord
	 * @param message Message
	 */
	public void sendMessage(@NotNull MessageEmbed message) {
		Bot.getDiscordBot().openPrivateChannelById(id).queue(chat -> chat.sendMessage(message).queue());
	}

	/**
	 * Ban player in discord
	 * @param delDays Days
	 * @param reason Reason
	 */
	public void ban(int delDays, @NotNull String reason) {
		Member member = getMember();
		if (isValid(member)) member.ban(delDays, reason).queue();
		Bukkit.getScheduler().scheduleSyncDelayedTask(MyVerify.getInstance(), () -> new DiscordBanEvent(this).callEvent());
	}

	private boolean isValid(@Nullable Member member) {
		return member != null && !member.isOwner();
	}

	/** Get Discord player by PLAYER */
	public static DiscordPlayer get(@NotNull Player player) {
		return get(player.getUniqueId());
	}

	/** Get Discord player by UUID */
	public static DiscordPlayer get(@NotNull UUID uuid) {
		return PLAYERS.containsKey(uuid) ? PLAYERS.get(uuid) : new DiscordPlayer(uuid).register();
	}

	/** Register Discord player */
	public DiscordPlayer register() {
		synchronized (MyVerify.getInstance()) {
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
			PLAYERS.putIfAbsent(uuid, this);
			return this;
		}
	}

	/** Unregister player from RAM */
	public void unregister() {
		PLAYERS.remove(uuid);
	}

	/** Update player statistic in Database */
	public void update() {
		synchronized (MyVerify.getInstance()) {
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
		}
	}

	/** Remove player from Database */
	public void removeFromBase() {
		synchronized (MyVerify.getInstance()) {
			Bukkit.getScheduler().runTaskAsynchronously(MyVerify.getInstance(), () -> {
				String sql = "DELETE FROM ACCOUNTS WHERE UUID = '" + uuid + "';";
				try (Connection con = Database.openConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
					ps.executeUpdate();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			});
		}
	}
}
