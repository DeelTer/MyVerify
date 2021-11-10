package ru.deelter.verify.api.actions;

import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import ru.deelter.verify.api.DiscordEvent;
import ru.deelter.verify.player.DiscordPlayer;

public class DiscordUnlinkEvent extends DiscordEvent {

    public DiscordUnlinkEvent(@NotNull DiscordPlayer player) {
        super(player);
    }

    private static final HandlerList HANDLERS = new HandlerList();

    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
