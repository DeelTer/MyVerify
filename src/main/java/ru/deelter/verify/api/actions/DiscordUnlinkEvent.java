package ru.deelter.verify.api.actions;

import org.jetbrains.annotations.NotNull;
import ru.deelter.verify.api.DiscordEvent;
import ru.deelter.verify.utils.player.DiscordPlayer;

public class DiscordUnlinkEvent extends DiscordEvent {

    public DiscordUnlinkEvent(@NotNull DiscordPlayer player) {
        super(player);
    }
}
