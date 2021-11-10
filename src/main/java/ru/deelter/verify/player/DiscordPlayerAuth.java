package ru.deelter.verify.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import ru.deelter.verify.Config;
import ru.deelter.verify.MyVerify;
import ru.deelter.verify.database.DiscordDatabase;
import ru.deelter.verify.utils.Console;

public class DiscordPlayerAuth implements Listener {

	@EventHandler
	public void onLogin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		DiscordPlayer discordPlayer = DiscordDatabase.exportPlayer(player);
		if (!discordPlayer.isLinked()) return;

		Console.debug("Информация: " + ", " + discordPlayer.getIp() + ", " + discordPlayer.getId());
		if (!Config.NICKNAME_UPDATER_ENABLE) return;

		// Sync discord name with minecraft
		discordPlayer.setName(player.getName());
		Console.debug("Обновляем ник у " + player.getName());
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Bukkit.getScheduler().runTaskAsynchronously(MyVerify.getInstance(), () -> {
			DiscordPlayer player = DiscordPlayer.get(event.getPlayer());
			if (player.isLinked()) DiscordDatabase.updateInfo(player);
		});
	}
}
