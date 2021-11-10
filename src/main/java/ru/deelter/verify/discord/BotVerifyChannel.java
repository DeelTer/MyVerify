package ru.deelter.verify.discord;

import net.dv8tion.jda.api.entities.MessageChannel;
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
import ru.deelter.verify.player.PlayerApplicationManager;
import ru.deelter.verify.utils.Colors;
import ru.deelter.verify.utils.Console;
import ru.deelter.verify.player.DiscordPlayer;

public class BotVerifyChannel extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        User author = event.getAuthor();
        if (author.isBot()) return;

        MessageChannel channel = event.getChannel();
        if (!channel.getId().equals(Config.VERIFY_CHANNEL_ID)) return;

        event.getMessage().delete().queue();
        String name = event.getMessage().getContentRaw();

        Player player = Bukkit.getPlayerExact(name);
        if (player == null || !player.isOnline()) {
            DiscordBot.warn(channel, Config.MSG_DS_PLAYER_OFFLINE);
            return;
        }

        DiscordPlayer dPlayer = DiscordPlayer.get(player);
        if (dPlayer.isLinked()) {
            DiscordBot.warn(channel, Config.MSG_DS_ACCOUNT_EXISTS);
            return;
        }

        if (PlayerApplicationManager.has(player.getUniqueId())) {
            DiscordBot.warn(channel, Config.MSG_DS_APPLICATION_EXISTS);
            return;
        }

        PlayerApplicationManager.add(player.getUniqueId(), author.getIdLong());

        // Minecraft part
        TextComponent text = new TextComponent(Colors.set("\n&6# &n" + author.getAsTag() + "&f создал заявку на\nверификацию вашего аккаунта: "));
        TextComponent button = new TextComponent(Colors.set("&8[&6Подтвердить&8]\n"));
        button.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/discordverify accept"));
        button.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(Colors.set("&6Кликните,&f чтобы привязать\nваш аккаунт к дискорду"))));

        text.addExtra(button);
        player.sendMessage(text);

        DiscordBot.notify(channel, Config.MSG_DS_APPLICATION_CREATED);
        Console.debug("Игрок " + player.getName() + " создал заявку на верификацию");
    }
}
