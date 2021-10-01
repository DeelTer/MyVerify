package ru.deelter.verify.managers.discord;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.deelter.verify.utils.player.DiscordPlayer;

import java.util.List;

public class DiscordPlayerManager {


    /**
     * Is player discord linked
     * @param player Player
     * @return true or false
     */
    public boolean isLinked(DiscordPlayer player) {
        return player.isLinked();
    }

    public boolean isLinked(Player player) {
        return DiscordPlayer.get(player).isLinked();
    }

    /**
     * Get all player roles
     * @return Roles list
     */
    @NotNull
    public List<Role> getRoles(@NotNull DiscordPlayer player) {
        return player.getRoles();
    }

    @NotNull
    public List<Role> getRoles(@NotNull Player player) {
        return DiscordPlayer.get(player).getRoles();
    }

    /**
     * Get all player role ids
     * @return Role ids list
     */
    @NotNull
    public List<String> getRoleIds(@NotNull DiscordPlayer player) {
        return player.getRoleIds();
    }

    @NotNull
    public List<String> getRoleIds(@NotNull Player player) {
        return DiscordPlayer.get(player).getRoleIds();
    }

    /**
     * Add role to the player
     * @param roleId Role id
     */
    public boolean hasRole(@NotNull DiscordPlayer player, @NotNull String roleId) {
        return player.hasRole(roleId);
    }

    public boolean hasRole(@NotNull Player player, @NotNull String roleId) {
        return DiscordPlayer.get(player).hasRole(roleId);
    }

    /**
     * Remove player role
     * @param roleId Role id
     */
    public void removeRole(@NotNull DiscordPlayer player, @NotNull String roleId) {
        player.removeRole(roleId);
    }

    public void removeRole(@NotNull Player player, @NotNull String roleId) {
        DiscordPlayer.get(player).removeRole(roleId);
    }

    /**
     * Add role to the player
     * @param roleId Role id
     */
    public void addRole(@NotNull DiscordPlayer player, @NotNull String roleId) {
        player.addRole(roleId);
    }

    public void addRole(@NotNull Player player, @NotNull String roleId) {
        DiscordPlayer.get(player).addRole(roleId);
    }

    /**
     * Set name in discord
     * @param name New discord name
     */
    public void setName(@NotNull DiscordPlayer player, @NotNull String name) {
        player.setName(name);
    }

    public void setName(@NotNull Player player, @NotNull String name) {
        DiscordPlayer.get(player).setName(name);
    }

    /**
     * Send message to player discord
     * @param message Message
     */
    public void sendMessage(@NotNull DiscordPlayer player, @NotNull String message) {
        player.sendMessage(message);
    }


    public void sendMessage(@NotNull Player player, @NotNull String message) {
        DiscordPlayer.get(player).sendMessage(message);
    }

    /**
     * Send message to player discord
     * @param message Message
     */
    public void sendMessage(@NotNull DiscordPlayer player, @NotNull MessageEmbed message) {
       player.sendMessage(message);
    }

    public void sendMessage(@NotNull Player player, @NotNull MessageEmbed message) {
        DiscordPlayer.get(player).sendMessage(message);
    }

    /**
     * Ban player in discord
     * @param delDays Days
     * @param reason Reason
     */
    public void ban(@NotNull DiscordPlayer player, int delDays, @NotNull String reason) {
        player.ban(delDays, reason);
    }

    public void ban(@NotNull Player player, int delDays, @NotNull String reason) {
        DiscordPlayer.get(player).ban(delDays, reason);
    }
}
