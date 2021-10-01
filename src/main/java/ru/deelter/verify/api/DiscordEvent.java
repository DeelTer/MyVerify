package ru.deelter.verify.api;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.deelter.verify.utils.player.DiscordPlayer;

public class DiscordEvent extends Event {

    private final DiscordPlayer player;

    public DiscordEvent(@NotNull DiscordPlayer player) {
        this.player = player;
    }

    @NotNull
    public DiscordPlayer getDiscordPlayer() {
        return player;
    }

    @Nullable
    public Player getPlayer() {
        return player.getPlayer();
    }

    /**
     * Get the player's Discord ID
     * @return Discord ID
     */
    public long getId() {
        return player.getId();
    }

    /**
     * Get the player's IP address
     * @return Player IP address
     */
    @Nullable
    public String getIp() {
        return player.getIp();
    }

    /**
     * The time of verification of the player
     * @return Time in mills or -1
     */
    public long getFirstTime() {
        return player.getTime();
    }

    private static final HandlerList HANDLERS = new HandlerList();

    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
