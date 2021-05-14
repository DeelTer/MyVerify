package ru.deelter.verify.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import ru.deelter.verify.Config;
import ru.deelter.verify.MyVerify;
import ru.deelter.verify.utils.Console;
import ru.deelter.verify.utils.player.DiscordPlayer;

import java.util.UUID;

public class PlayerAuthListener implements Listener {

	@EventHandler
	public void onPreJoin(AsyncPlayerPreLoginEvent e) {
		UUID uuid = e.getUniqueId();
		new DiscordPlayer(uuid).register();
	}

	@EventHandler
	public void onLogin(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		DiscordPlayer dPlayer = DiscordPlayer.get(player.getUniqueId());
		if (!dPlayer.isLinked())
			return;

		Console.debug("Информация: " + ", " + dPlayer.getIp() + ", " + dPlayer.getId());
		if (!Config.NICKNAME_UPDATER_ENABLE)
			return;

		/* Set discord name to current */
		dPlayer.setName(player.getName());
		Console.debug("Обновляем ник у " + player.getName());
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Bukkit.getScheduler().runTaskAsynchronously(MyVerify.getInstance(), () -> {
			DiscordPlayer discordPlayer = DiscordPlayer.get(e.getPlayer());
			if (discordPlayer.isLinked())
				discordPlayer.update();
		});
	}
}
