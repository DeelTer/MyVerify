package ru.deelter.verify.utils.player;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import ru.deelter.verify.VerifyReload;

import java.util.UUID;

public class PlayerIdentification implements Listener {

	@EventHandler
	public void onJoin(AsyncPlayerPreLoginEvent e) {

		UUID uuid = e.getUniqueId();
		DiscordPlayer aplayer = new DiscordPlayer(uuid);
		aplayer.register();
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Bukkit.getScheduler().runTaskAsynchronously(VerifyReload.getInstance(), () -> {
			DiscordPlayer aplayer = DiscordPlayer.get(e.getPlayer());
			aplayer.unregister();
		});
	}
}
