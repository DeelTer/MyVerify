package ru.deelter.verify.api.actions;

import org.jetbrains.annotations.NotNull;
import ru.deelter.verify.api.DiscordEvent;
import ru.deelter.verify.utils.player.DiscordPlayer;

public class DiscordVerificationEvent extends DiscordEvent {

    public DiscordVerificationEvent(@NotNull DiscordPlayer player) {
        super(player);
    }
}
