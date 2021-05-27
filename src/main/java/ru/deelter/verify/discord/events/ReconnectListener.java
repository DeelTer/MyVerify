package ru.deelter.verify.discord.events;

import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ReconnectListener extends ListenerAdapter {

//    @Override
//    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
//        Member member = event.getMember();
//        boolean isLinked = DiscordPlayer.contains(member.getId());
//        Console.debug("&fИгрок привязан: " + isLinked);
//        if (!isLinked)
//            return;
//
//        for (String roleId : Config.ROLES) {
//            Guild guild = event.getGuild();
//            Role role = guild.getRoleById(roleId);
//            guild.addRoleToMember(member.getId(), role).queue();
//        }
//    }
}
