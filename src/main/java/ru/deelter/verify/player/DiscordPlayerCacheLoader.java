package ru.deelter.verify.player;

import com.google.common.cache.CacheLoader;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class DiscordPlayerCacheLoader extends CacheLoader<UUID, DiscordPlayer> {

	@Override
	public @NotNull DiscordPlayer load(@NotNull UUID key) {
		return DiscordDatabase.load(key);
	}
}
