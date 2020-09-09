package me.ryguy.ctfbot.cmds;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.Role;
import discord4j.rest.util.Color;
import discord4j.rest.util.Permission;
import me.ryguy.ctfbot.CTFDiscordBot;
import me.ryguy.ctfbot.types.CTFDiscordOnlyCommand;
import me.ryguy.ctfbot.util.Util;
import me.ryguy.discordapi.DiscordBot;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

public class RemoveRolesCommand extends CTFDiscordOnlyCommand {
    public RemoveRolesCommand() {
        super("removeroles", "rr");
    }

    @Override
    public boolean canExecute(MessageCreateEvent e) {
        if(e.getGuildId().isPresent()) {
            if(e.getGuildId().get().asLong() == CTFDiscordBot.CTF_DISCORD_ID) {
                if(e.getMember().isPresent()) {
                    if(e.getMember().get().getRoles().map(Role::getName).collect(Collectors.toList()).block().contains("PPM Host")) {
                        return true;
                    }else {
                        e.getMessage().getChannel().block().createEmbed(em -> {
                            em.setColor(Color.RED);
                            em.setDescription(":x: You need to have the role `PPM Host` to use this command!");
                        }).block();
                        return false;
                    }
                }
            }else {
                if(e.getMember().isPresent()) {
                    if(e.getMember().get().getBasePermissions().block().contains(Permission.MANAGE_ROLES)) {
                        return true;
                    }else {
                        e.getMessage().getChannel().block().createEmbed(em -> {
                            em.setColor(Color.RED);
                            em.setDescription(":x: You need to have the permission `MANAGE_ROLES` to use this command!");
                        }).block();
                        return false;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public Mono<Void> execute(Message message, String s, String[] strings) {
        if (!Util.isPpmHost(message.getAuthorAsMember().block())) return null;
        int roles = 0;
        Message msg = message.getChannel().block().createMessage(m -> {
            m.setEmbed(e -> {
                e.setColor(Color.TAHITI_GOLD);
                e.setDescription(":arrows_clockwise: Removing Roles!");
            });
        }).block();
        for (Member m : message.getGuild().block().getMembers().toIterable()) {
            for (Role role : Util.getRolesToRemove(message.getGuild().block())) {
                if (m.getRoleIds().contains(role.getId())) {
                    m.removeRole(role.getId(), DiscordBot.getBot().getPrefix() + "removeroles done by " +
                            message.getAuthorAsMember().block().getDisplayName() + " in " + message.getChannel().block().getRestChannel().getData().block().name().get()).block();
                    roles++;
                }
            }
        }
        int finalRoles = roles;
        msg.edit(m -> {
            m.setEmbed(e -> {
                e.setColor(Color.GREEN);
                e.setDescription(String.format(":white_check_mark: %s Roles Removed!", finalRoles));
            });
        }).block();
        return null;
    }
}
