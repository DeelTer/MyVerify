package ru.deelter.verify.api;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author DeelTer
 */
public class PlayerVerificationEvent extends Event {

    private final Player player;
    private final String ip;

    private final long id;
    private final long time;

    public PlayerVerificationEvent(Player player, String ip, long id, long time) {
        this.player = player;
        this.ip = ip;
        this.id = id;
        this.time = time;
    }

    /**
     * Get a player who has successfully verified
     * @return Player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Get the player's Discord ID
     * @return Discord ID
     */
    public long getId() {
        return id;
    }

    /**
     * The time of verification of the player
     * @return Time in mills
     */
    public long getFirstTime() {
        return time;
    }

    private static final HandlerList HANDLERS = new HandlerList();

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
