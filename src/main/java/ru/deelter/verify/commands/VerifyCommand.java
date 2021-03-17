package ru.deelter.verify.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.deelter.verify.Config;
import ru.deelter.verify.MyVerify;
import ru.deelter.verify.api.PlayerVerificationEvent;
import ru.deelter.verify.utils.Colors;
import ru.deelter.verify.utils.Console;
import ru.deelter.verify.utils.player.Applications;
import ru.deelter.verify.utils.player.DiscordPlayer;

import java.awt.*;
import java.util.UUID;

public class VerifyCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            //open menu
            return true;
        }

        /* Configuration reload */
        if (args[0].equalsIgnoreCase("RELOAD")) {
            sender.sendMessage(Colors.set("&6Конфигурация&f успешно перезагружена"));
            Config.reload();
        }

        if (!(sender instanceof Player))
            return true;

        Player player = (Player) sender;
        if (args[0].equalsIgnoreCase("UNLINK")) {
            DiscordPlayer discordPlayer = DiscordPlayer.get(player.getUniqueId());
            if (!discordPlayer.isLinked()) {
                sender.sendMessage(Colors.set("&6Ваш дискорд&f не привязан"));
                return true;
            }

            MessageEmbed message = new EmbedBuilder().setDescription("Ваш аккаунт успешно `отвязан` от дискорда \nВы сможете привязать его повторно тем же способом").setColor(Color.red).build();
            discordPlayer.sendMessage(message);
            discordPlayer.unregister(true);
            sender.sendMessage(Colors.set("&6Вы отвязали&f свой дискорд"));
            return true;
        }

        /* Accept application */
        if (args[0].equalsIgnoreCase("ACCEPT")) {
            UUID uuid = player.getUniqueId();
            if (!Applications.has(uuid)) {
                player.sendMessage(Colors.set("&cОшибка:&f заявки отсутствуют"));
                return true;
            }

            String ip = player.getAddress().getHostName();
            long id = Applications.get(uuid), time = System.currentTimeMillis();
            DiscordPlayer dPlayer = DiscordPlayer.get(uuid);
            dPlayer.setTime(time);
            dPlayer.setId(id);
            dPlayer.setIp(ip);
            dPlayer.update();

            Applications.remove(uuid);
            player.sendMessage(Colors.set("&6Вы&f успешно верифицировали свой аккаунт"));

            /* Roles setup */
            if (Config.ROLES_ENABLE) {
                Console.debug("Устанавливаем роль игроку " + player.getName());
                Config.ROLES.forEach(roleId -> dPlayer.setRole(roleId));
            }

            /* Nickname setup */
            if (Config.NICKNAME_ENABLE) {
                Console.debug("Изменяем ник игроку " + player.getName());
                dPlayer.setName(player.getName());
            }

            Console.debug("Игрок " + player.getName() + " верифицирован");

            /* Call event for api */
            Bukkit.getScheduler().scheduleSyncDelayedTask(MyVerify.getInstance(), () -> {
                PlayerVerificationEvent event = new PlayerVerificationEvent(player, ip, id, time);
                Bukkit.getPluginManager().callEvent(event);
            });
        }

        return true;
    }
}
