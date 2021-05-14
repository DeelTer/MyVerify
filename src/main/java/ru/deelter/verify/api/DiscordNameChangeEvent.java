package ru.deelter.verify.api;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import ru.deelter.verify.utils.player.DiscordPlayer;

/**
 * @author DeelTer
 */
public class DiscordNameChangeEvent extends Event {

    private final DiscordPlayer player;
    private final String oldName, newName;

    public DiscordNameChangeEvent(DiscordPlayer player, String oldName, String newName) {
        this.player = player;
        this.oldName = oldName;
        this.newName = newName;
    }

    /**
     * Get a player who has successfully verified
     * @return Player
     */
    public DiscordPlayer getPlayer() {
        return player;
    }

    public String getOldName() {
        return oldName;
    }

    public String getNewName() {
        return newName;
    }

    private static final HandlerList HANDLERS = new HandlerList();

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
