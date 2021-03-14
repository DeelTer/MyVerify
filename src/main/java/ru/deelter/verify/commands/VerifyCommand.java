package ru.deelter.verify.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
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

import java.awt.*;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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
        /* sending message */
        if (args[0].equalsIgnoreCase("UNLINK")) {
            MessageEmbed message = new EmbedBuilder().setDescription("Ваш аккаунт успешно `отвязан`").setColor(Color.red).build();
            DiscordPlayer discordPlayer = DiscordPlayer.get(player.getUniqueId());
            discordPlayer.sendMessage(message);
            discordPlayer.unregister();
            return true;
        }

        if (args[0].equalsIgnoreCase("ACCEPT")) {
            UUID uuid = player.getUniqueId();
            if (!Applications.has(uuid)) {
                player.sendMessage(Colors.set("&cОшибка:&f заявки отсутствуют"));
                return true;
            }

            String ip = player.getAddress().getHostName();
            long id = Applications.get(uuid), time = System.currentTimeMillis();
            DiscordPlayer dPlayer = DiscordPlayer.get(player);
            dPlayer.setTime(time);
            dPlayer.setId(id);
            dPlayer.setIp(ip);
            dPlayer.update();

            Applications.remove(uuid);
            player.sendMessage(Colors.set("&6Вы&f успешно верифицировали свой аккаунт"));

            /* Roles setup */
            if (Config.ROLES_ENABLE) {
                for (String roleId : Config.ROLES) {
                    Console.debug("Устанавливаем роль игроку " + player.getName());
                    dPlayer.setRole(roleId);
                }
            }

            /* Nickname setup */
            if (Config.NICKNAME_ENABLE) {
                Console.debug("Изменяем ник игроку " + player.getName());
                dPlayer.getMember().modifyNickname(player.getName());
            }

            Console.debug("Игрок " + player.getName() + " верифицирован");

            /* Call event for api */
            Bukkit.getScheduler().scheduleSyncDelayedTask(VerifyReload.getInstance(), () -> {
                PlayerVerificationEvent event = new PlayerVerificationEvent(player, ip, id, time);
                Bukkit.getPluginManager().callEvent(event);
            });
        }

        return true;
    }
}
