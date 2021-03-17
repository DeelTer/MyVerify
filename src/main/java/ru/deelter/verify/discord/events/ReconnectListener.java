package ru.deelter.verify.discord.events;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import ru.deelter.verify.Config;
import ru.deelter.verify.utils.Console;
import ru.deelter.verify.utils.player.DiscordPlayer;

public class ReconnectListener extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        Member member = event.getMember();
        boolean isLinked = DiscordPlayer.contains(member.getId());
        Console.debug("&fИгрок привязан: " + isLinked);
        if (!isLinked)
            return;

        for (String roleId : Config.ROLES) {
            Guild guild = event.getGuild();
            Role role = guild.getRoleById(roleId);
            guild.addRoleToMember(member.getId(), role).queue();
        }
    }
}
