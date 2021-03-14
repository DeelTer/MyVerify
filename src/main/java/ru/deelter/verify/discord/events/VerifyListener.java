package ru.deelter.verify.discord.events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.deelter.verify.Config;
import ru.deelter.verify.utils.Colors;
import ru.deelter.verify.utils.Console;
import ru.deelter.verify.utils.player.Applications;
import ru.deelter.verify.utils.player.DiscordPlayer;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class VerifyListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        User user = event.getAuthor();
        if (user.isBot())
            return;

        final String name = event.getMessage().getContentRaw();
        MessageChannel channel = event.getChannel();

        boolean isPrivate = Config.VERIFY_CHANNEL.equalsIgnoreCase("PRIVATE");
        if (!isPrivate) {
            if (!channel.getId().equals(Config.VERIFY_CHANNEL))
                return;

            event.getMessage().delete().queue();
        } else if (event.getChannelType() != ChannelType.PRIVATE)
            return;

        Player player = Bukkit.getPlayerExact(name);
        if (player == null || !player.isOnline()) {
            MessageEmbed notify = new EmbedBuilder().setDescription(Config.MSG_PLAYER_OFFLINE).setColor(Color.red).build();
            channel.sendMessage(notify).queue(msg -> msg.delete().queueAfter(4, TimeUnit.SECONDS));
            return;
        }

        DiscordPlayer dPlayer = DiscordPlayer.get(player);
        if (dPlayer.isLinked()) {
            MessageEmbed notify = new EmbedBuilder().setDescription(Config.MSG_ACCOUNT_EXISTS).setColor(Color.red).build();
            channel.sendMessage(notify).queue(msg -> msg.delete().queueAfter(4, TimeUnit.SECONDS));
            return;
        }

        if (Applications.has(player.getUniqueId())) {
            MessageEmbed notify = new EmbedBuilder().setDescription(Config.MSG_APPLICATION_EXISTS).setColor(Color.red).build();
            channel.sendMessage(notify).queue(msg -> msg.delete().queueAfter(4, TimeUnit.SECONDS));
            return;
        }

        Applications.add(player.getUniqueId(), user.getIdLong());

        TextComponent text = new TextComponent(Colors.set("\n&6# &n" + user.getAsTag() + "&f создал заявку на\nверификацию вашего аккаунта: "));
        TextComponent button = new TextComponent(Colors.set("&8[&6Подтвердить&8]\n"));
        button.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/verify accept"));
        button.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(Colors.set("&6Кликните,&f чтобы привязать\nваш аккаунт к дискорду"))));
        text.addExtra(button);

        player.sendMessage(text);

        MessageEmbed notify = new EmbedBuilder().setDescription(Config.MSG_APPLICATION_CREATED).setColor(Color.green).build();
        channel.sendMessage(notify).queue(msg -> msg.delete().queueAfter(5, TimeUnit.SECONDS));
        Console.debug("Игрок " + player.getName() + " создал заявку на верификацию");
    }
}
