package ru.deelter.verify.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import ru.deelter.verify.Config;
import ru.deelter.verify.discord.events.ReactionListener;
import ru.deelter.verify.discord.events.ReconnectListener;
import ru.deelter.verify.discord.events.VerifyListener;

import javax.security.auth.login.LoginException;

public class MyBot {

    private static JDA bot;

    public static void load() {

        JDABuilder builder = JDABuilder.createDefault(Config.TOKEN);
        builder.setActivity(Activity.playing("vk.com/deelter"));
        builder.addEventListeners(new VerifyListener(), new ReconnectListener());
        try {
            bot = builder.build();
            bot.awaitReady();
        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void unload() {
        bot.shutdown();
    }

    public static JDA get() {
        return bot;
    }
}
