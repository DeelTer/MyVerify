package ru.deelter.verify.discord;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.jetbrains.annotations.NotNull;
import ru.deelter.verify.Config;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.util.concurrent.TimeUnit;

public class VerifyBot {

    private static JDA discordBot;
    private static Guild guild;

    public static void load() {
        JDABuilder builder = JDABuilder.createDefault(Config.TOKEN)
                .disableCache(CacheFlag.EMOTE, CacheFlag.VOICE_STATE)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setActivity(Activity.playing("vk.com/deelter"))
                .addEventListeners(new VerifyChannelListener())
                .setEnabledIntents(GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS));
        try {
            discordBot = builder.build();
            discordBot.awaitReady();
        } catch (LoginException | InterruptedException exception) {
            exception.printStackTrace();
        }
        guild = discordBot.getGuildById(Config.GUILD_ID);
    }

    public static JDA getJDA() {
        return discordBot;
    }

    public static Guild getGuild() {
        return guild;
    }

    public static void warn(@NotNull MessageChannel channel, String message) {
        MessageEmbed warn = new EmbedBuilder().setDescription(message).setColor(Color.red).build();
        channel.sendMessageEmbeds(warn).queue(msg -> msg.delete().queueAfter(4, TimeUnit.SECONDS));
    }

    public static void notify(@NotNull MessageChannel channel, String message) {
        MessageEmbed notify = new EmbedBuilder().setDescription(Config.MSG_DS_ACCOUNT_EXISTS).setColor(Color.green).build();
        channel.sendMessageEmbeds(notify).queue(msg -> msg.delete().queueAfter(4, TimeUnit.SECONDS));
    }
}
