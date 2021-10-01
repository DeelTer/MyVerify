package ru.deelter.verify.api.actions;

import ru.deelter.verify.api.DiscordEvent;
import ru.deelter.verify.utils.player.DiscordPlayer;

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
}
