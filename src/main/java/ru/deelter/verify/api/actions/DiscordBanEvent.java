package ru.deelter.verify.api.actions;

import ru.deelter.verify.api.DiscordEvent;
import ru.deelter.verify.utils.player.DiscordPlayer;

public class DiscordBanEvent extends DiscordEvent {

    public DiscordBanEvent(DiscordPlayer player) {
        super(player);
    }
}
