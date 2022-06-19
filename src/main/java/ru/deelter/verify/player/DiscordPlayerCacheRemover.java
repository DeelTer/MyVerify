package ru.deelter.verify.player;

import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class DiscordPlayerCacheRemover implements RemovalListener<UUID, DiscordPlayer> {
	@Override
	public void onRemoval(@NotNull RemovalNotification<UUID, DiscordPlayer> notification) {
		UUID uuid = notification.getKey();
		DiscordPlayer player = notification.getValue();
		if (uuid == null || player == null) return;
		if (player.isLinked()) DiscordDatabase.save(player);
	}
}
