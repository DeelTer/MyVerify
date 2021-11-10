package ru.deelter.verify.api.actions;

import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import ru.deelter.verify.api.DiscordEvent;
import ru.deelter.verify.player.DiscordPlayer;

/**
 * @author DeelTer
 */
public class DiscordNameChangeEvent extends DiscordEvent {

    private final String oldName, newName;

    public DiscordNameChangeEvent(DiscordPlayer player, String oldName, String newName) {
        super(player);
        this.oldName = oldName;
        this.newName = newName;
    }

    public String getOldName() {
        return oldName;
    }

    public String getNewName() {
        return newName;
    }

    private static final HandlerList HANDLERS = new HandlerList();

    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
