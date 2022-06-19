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
import ru.deelter.verify.api.actions.DiscordUnlinkEvent;
import ru.deelter.verify.api.actions.DiscordVerificationEvent;
import ru.deelter.verify.player.DiscordDatabase;
import ru.deelter.verify.player.DiscordPlayerManager;
import ru.deelter.verify.player.PlayerApplicationManager;
import ru.deelter.verify.utils.Console;
import ru.deelter.verify.player.DiscordPlayer;

import java.awt.*;
import java.util.List;
import java.util.*;

public class VerifyCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length < 1) {
            //open menu
            return true;
        }

        /* Configuration reload */
        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.isOp()) return true;

            sender.sendMessage(Config.MSG_MC_RELOAD);
            Config.reload();
            return true;
        }

        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;
        if (args[0].equalsIgnoreCase("unlink")) {
            DiscordPlayer discordPlayer = DiscordPlayerManager.getByPlayer(player);
            if (!discordPlayer.isLinked()) {
                sender.sendMessage(Config.MSG_MC_NOT_LINKED);
                return true;
            }
            //Remove roles
            Config.ROLES_ID.forEach(discordPlayer::removeRole);

            MessageEmbed message = new EmbedBuilder().setDescription(Config.MSG_DS_UNLINKED).setColor(Color.red).build();
            discordPlayer.sendMessage(message);
            discordPlayer.unlink();

            DiscordDatabase.delete(discordPlayer);

            sender.sendMessage(Config.MSG_MC_SUCCESS_UNLINK);
            Bukkit.getScheduler().scheduleSyncDelayedTask(MyVerify.getInstance(), () -> new DiscordUnlinkEvent(discordPlayer).callEvent());
        }

        else if (args[0].equalsIgnoreCase("check")) {
            if (args.length < 2 || !sender.isOp()) return true;

            Player target = Bukkit.getPlayer(args[1]);
            if (target == null || !target.isOnline()) return true;

            DiscordPlayer discordPlayer = DiscordPlayerManager.getByPlayer(target);
            player.sendMessage("NAME: " + target.getName()
                    + ", LINKED: " + discordPlayer.isLinked()
                    + ", IP: " + discordPlayer.getIp()
                    + ", ID: " + discordPlayer.getDiscordId());
        }

        /* Accept application */
        else if (args[0].equalsIgnoreCase("accept")) {
            UUID uuid = player.getUniqueId();
            if (!PlayerApplicationManager.has(uuid)) {
                player.sendMessage(Config.MSG_MC_NO_APPLICATIONS);
                return true;
            }

            String ip = Objects.requireNonNull(player.getAddress()).getHostName();
            long id = PlayerApplicationManager.get(uuid);
            long time = System.currentTimeMillis();

            DiscordPlayer dPlayer = DiscordPlayerManager.getByUuid(uuid);
            dPlayer.setTime(time);
            dPlayer.setId(id);
            dPlayer.setIp(ip);

            PlayerApplicationManager.remove(uuid);
            player.sendMessage(Config.MSG_MC_SUCCESS_LINK);

            /* Roles setup */
            if (Config.ROLES_ENABLE) {
                Console.debug("Устанавливаем роль игроку " + player.getName());
                Config.ROLES_ID.forEach(dPlayer::addRole);
            }

            /* Nickname setup */
            if (Config.NICKNAME_ENABLE) {
                Console.debug("Изменяем ник игроку " + player.getName());
                dPlayer.setName(player.getName());
            }

            /* Call event for api */
            Bukkit.getScheduler().scheduleSyncDelayedTask(MyVerify.getInstance(), () -> new DiscordVerificationEvent(dPlayer).callEvent());
            Console.debug("Игрок " + player.getName() + " верифицирован");
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String @NotNull [] args) {
        List<String> suggestions = new ArrayList<>();
        if (args.length == 1) {
            if (sender.isOp()) suggestions.add("reload");

            suggestions.addAll(Arrays.asList("accept", "unlink", "check"));
            return suggestions;
        }

       return null;
    }
}
