package ru.deelter.verify.player;

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
import ru.deelter.verify.database.DiscordDatabase;
import ru.deelter.verify.discord.DiscordBot;

import java.util.*;

public class DiscordPlayer {

	private static final Guild GUILD = DiscordBot.getGuild();

	private static final Map<UUID, DiscordPlayer> PLAYERS = new HashMap<>();
	private final UUID uuid;
	private String ip;

	private long id, time;

	public DiscordPlayer(@NotNull UUID uuid) {
		this.uuid = uuid;
	}

	public DiscordPlayer(@NotNull Player player) {
		this(player.getUniqueId());
	}

	public DiscordPlayer(@NotNull UUID uuid, long id, String ip, long time) {
		this.uuid = uuid;
		this.id = id;
		this.ip = ip;
		this.time = time;
	}

	@NotNull
	public UUID getUUID() {
		return uuid;
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

	public void unlink() {
		DiscordDatabase.deletePlayer(uuid);
		this.unregister();
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
	 * Add role to the player
	 * @param roleId Role id
	 */
	public void addRole(@NotNull String roleId) {
		setRole(roleId, true);
	}

	/**
	 * Remove player role
	 * @param roleId Role id
	 */
	public void removeRole(@NotNull String roleId) {
		setRole(roleId, false);
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
		DiscordBot.getDiscordBot().openPrivateChannelById(id).queue(chat -> chat.sendMessage(message).queue());
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
		PLAYERS.putIfAbsent(uuid, this);
		return this;
	}

	public void unregister() {
		PLAYERS.remove(uuid);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		DiscordPlayer that = (DiscordPlayer) o;
		return Objects.equals(uuid, that.uuid);
	}

	@Override
	public int hashCode() {
		return Objects.hash(uuid);
	}

	@Override
	public String toString() {
		return "DiscordPlayer{" +
				"uuid=" + uuid +
				", ip='" + ip + '\'' +
				", id=" + id + '\'' +
				", linked=" + isLinked() +
				'}';
	}
}
