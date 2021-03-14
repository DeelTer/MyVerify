package ru.deelter.verify.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.deelter.verify.Config;
import ru.deelter.verify.VerifyReload;
import ru.deelter.verify.api.PlayerVerificationEvent;
import ru.deelter.verify.utils.Colors;
import ru.deelter.verify.utils.Console;
import ru.deelter.verify.utils.player.Applications;
import ru.deelter.verify.utils.player.DiscordPlayer;

public class VerifyCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            //open menu
            return true;
        }

        /* Configuration reload */
        if (args[0].equalsIgnoreCase("RELOAD")) {
            Console.log("&6Конфигурация&f успешно перезагружена");
            Config.reload();
        }

        if (!(sender instanceof Player))
            return true;

        Player player = (Player) sender;
        if (args[0].equalsIgnoreCase("ACCEPT")) {
            if (!Applications.has(player.getUniqueId())) {
                player.sendMessage(Colors.set("&cОшибка:&f заявки отсутствуют"));
                return true;
            }

            String ip = player.getAddress().getHostName();
            long id = Applications.get(player.getUniqueId()), time = System.currentTimeMillis();
            DiscordPlayer dPlayer = DiscordPlayer.get(player);
            dPlayer.setTime(time);
            dPlayer.setId(id);
            dPlayer.setIp(ip);
            dPlayer.update();

            Applications.remove(player.getUniqueId());
            player.sendMessage(Colors.set("&6Вы&f успешно верифицировали свой аккаунт"));

            /* Set roles to player */
            for (String roleId : Config.ROLES) {
                dPlayer.setRole(roleId);
            }

            /* Call event for api */
            Bukkit.getScheduler().scheduleSyncDelayedTask(VerifyReload.getInstance(), () -> {
                PlayerVerificationEvent event = new PlayerVerificationEvent(player, ip, id, time);
                Bukkit.getPluginManager().callEvent(event);
            });
        }

        return true;
    }
}
