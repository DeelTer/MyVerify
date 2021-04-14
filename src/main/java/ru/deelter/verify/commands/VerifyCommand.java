package ru.deelter.verify.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import ru.deelter.verify.Config;
import ru.deelter.verify.MyVerify;
import ru.deelter.verify.api.PlayerVerificationEvent;
import ru.deelter.verify.utils.Console;
import ru.deelter.verify.managers.ApplicationManager;
import ru.deelter.verify.utils.player.DiscordPlayer;

import java.awt.*;
import java.util.*;
import java.util.List;

public class VerifyCommand implements CommandExecutor, @Nullable TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length < 1) {
            //open menu
            return true;
        }

        /* Configuration reload */
        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.isOp())
                return true;

            sender.sendMessage(Config.MSG_MC_RELOAD);
            Config.reload();
        }

        if (!(sender instanceof Player))
            return true;

        Player player = (Player) sender;
        if (args[0].equalsIgnoreCase("unlink")) {
            DiscordPlayer discordPlayer = DiscordPlayer.get(player.getUniqueId());
            if (!discordPlayer.isLinked()) {
                sender.sendMessage(Config.MSG_MC_NOT_LINKED);
                return true;
            }

            MessageEmbed message = new EmbedBuilder().setDescription(Config.MSG_DS_UNLINKED).setColor(Color.red).build();
            discordPlayer.sendMessage(message);
            discordPlayer.removeFromBase();
            discordPlayer.unregister();

            sender.sendMessage(Config.MSG_MC_SUCCESS_UNLINK);
            return true;
        }

        /* Accept application */
        if (args[0].equalsIgnoreCase("accept")) {
            UUID uuid = player.getUniqueId();
            if (!ApplicationManager.has(uuid)) {
                player.sendMessage(Config.MSG_MC_NO_APPLICATIONS);
                return true;
            }

            String ip = Objects.requireNonNull(player.getAddress()).getHostName();
            long id = ApplicationManager.get(uuid), time = System.currentTimeMillis();
            DiscordPlayer dPlayer = DiscordPlayer.get(uuid);
            dPlayer.setTime(time);
            dPlayer.setId(id);
            dPlayer.setIp(ip);

            ApplicationManager.remove(uuid);
            player.sendMessage(Config.MSG_MC_SUCCESS_LINK);

            /* Roles setup */
            if (Config.ROLES_ENABLE) {
                Console.debug("Устанавливаем роль игроку " + player.getName());
                Config.ROLES.forEach(dPlayer::setRole);
            }

            /* Nickname setup */
            if (Config.NICKNAME_ENABLE) {
                Console.debug("Изменяем ник игроку " + player.getName());
                dPlayer.setName(player.getName());
            }

            /* Call event for api */
            Bukkit.getScheduler().scheduleSyncDelayedTask(MyVerify.getInstance(), () -> {
                PlayerVerificationEvent event = new PlayerVerificationEvent(player, ip, id, time);
                Bukkit.getPluginManager().callEvent(event);
            });

            Console.debug("Игрок " + player.getName() + " верифицирован");
            return true;
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> suggestions = new ArrayList<>();
        if (args.length == 1) {
            if (sender.isOp())
                suggestions.add("reload");

            suggestions.addAll(Arrays.asList("accept", "unlink"));
            return suggestions;
        }

       return null;
    }
}
