package ru.deelter.verify.player;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import ru.deelter.verify.Config;
import ru.deelter.verify.MyVerify;
import ru.deelter.verify.utils.Console;

public class DiscordPlayerAuthListener implements Listener {

	@EventHandler
	public void onLogin(PlayerJoinEvent event) {
		Bukkit.getScheduler().runTaskAsynchronously(MyVerify.getInstance(), () -> {
			DiscordPlayer player = DiscordPlayerManager.getByPlayer(event.getPlayer());
			if (!player.isLinked()) return;
			if (!Config.NICKNAME_UPDATER_ENABLE) {
				// Sync discord name with minecraft
				String name = event.getPlayer().getName();
				player.setName(name);
				Console.debug("Update user nickname " + name);
			}
		});
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onQuit(PlayerQuitEvent event) {
		Bukkit.getScheduler().runTaskAsynchronously(MyVerify.getInstance(), () -> {
			DiscordPlayer player = DiscordPlayerManager.getByPlayer(event.getPlayer());
			if (player.isLinked()) DiscordDatabase.updateInfo(player);
		});
	}
}
