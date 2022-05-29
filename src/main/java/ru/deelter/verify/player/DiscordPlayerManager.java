package ru.deelter.verify.player;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.deelter.verify.MyVerify;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class DiscordPlayerManager {

	private static final LoadingCache<UUID, DiscordPlayer> PLAYERS = CacheBuilder.newBuilder()
			.maximumSize(1000)
			.expireAfterAccess(15, TimeUnit.MINUTES)
			.removalListener(new DiscordPlayerCacheRemover())
			.build(new DiscordPlayerCacheLoader());

	public static void setup(MyVerify instance) {
		DiscordDatabase.setupTables();
		Bukkit.getScheduler().runTaskTimerAsynchronously(instance, PLAYERS::cleanUp, 5 * 20L, 0L);
		Bukkit.getScheduler().runTaskTimerAsynchronously(instance, DiscordPlayerManager::saveCacheToDatabase, 10 * 60L * 20L, 0L);
	}

	public static @NotNull DiscordPlayer getByUuid(@NotNull UUID uuid) {
		return PLAYERS.getUnchecked(uuid);
	}

	public static @Nullable DiscordPlayer getById(long userId) {
		for (DiscordPlayer value : PLAYERS.asMap().values()) {
			if (value.getDiscordId() == userId)
				return value;
		}
		DiscordPlayer player = DiscordDatabase.load(userId);
		if (player == null) return null;
		PLAYERS.put(player.getMinecraftId(), player);
		return player;
	}

	public static @NotNull DiscordPlayer getByPlayer(@NotNull Player player) {
		return getByUuid(player.getUniqueId());
	}

	public static void saveCacheToDatabase() {
		DiscordDatabase.save(PLAYERS.asMap().values());
	}

	public static void delete(UUID uuid) {
		PLAYERS.invalidate(uuid);
		DiscordDatabase.delete(uuid);
	}
}
