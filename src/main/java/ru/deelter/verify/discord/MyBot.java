package ru.deelter.verify.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import ru.deelter.verify.Config;
import ru.deelter.verify.discord.events.ReconnectListener;
import ru.deelter.verify.discord.events.VerifyListener;
import ru.deelter.verify.utils.Console;

import javax.security.auth.login.LoginException;

public class MyBot {

    private static JDA bot;
    private static Guild guild;

    public static void load() {
        JDABuilder builder = JDABuilder.createDefault(Config.TOKEN)
                .disableCache(CacheFlag.EMOTE, CacheFlag.VOICE_STATE)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setActivity(Activity.playing("vk.com/deelter"))
                .addEventListeners(new VerifyListener(), new ReconnectListener())
                .setEnabledIntents(GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS));
        try {
            bot = builder.build();
            bot.awaitReady();
        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
        }
        guild = bot.getGuildById(Config.GUILD_ID);
        Console.debug("" + getGuild().loadMembers().isStarted());
    }

    public static void unload() {
        bot.shutdown();
    }

    public static JDA getBot() {
        return bot;
    }

    public static Guild getGuild() {
        return guild;
    }
}
