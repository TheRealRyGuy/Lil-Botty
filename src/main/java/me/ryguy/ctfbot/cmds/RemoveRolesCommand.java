package me.ryguy.ctfbot.cmds;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.Role;
import discord4j.rest.util.Color;
import discord4j.rest.util.Permission;
import me.ryguy.ctfbot.CTFDiscordBot;
import me.ryguy.ctfbot.util.Util;
import me.ryguy.discordapi.DiscordBot;
import me.ryguy.discordapi.command.Command;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

public class RemoveRolesCommand extends Command {
    public RemoveRolesCommand() {
        super("removeroles", "rr");
    }

    @Override
    public boolean canExecute(Message e, boolean shouldSend) {
        if(e.getGuildId().isPresent()) {
            if(e.getGuildId().get().asLong() == CTFDiscordBot.CTF_DISCORD_ID) {
                if(e.getAuthorAsMember().blockOptional().isPresent()) {
                    if(e.getAuthorAsMember().block().getRoles().map(Role::getName).collect(Collectors.toList()).block().contains("PPM Host")) {
                        return true;
                    }else {
                        if(shouldSend) {
                            e.getChannel().block().createEmbed(em -> {
                                em.setColor(Color.RED);
                                em.setDescription(":x: You need to have the role `PPM Host` to use this command!");
                            }).block();
                        }
                        return false;
                    }
                }
            }else {
                if(e.getAuthorAsMember().blockOptional().isPresent()) {
                    if(e.getAuthorAsMember().block().getBasePermissions().block().contains(Permission.MANAGE_ROLES)) {
                        return true;
                    }else {
                        if(shouldSend) {
                            e.getChannel().block().createEmbed(em -> {
                                em.setColor(Color.RED);
                                em.setDescription(":x: You need to have the permission `MANAGE_ROLES` to use this command!");
                            }).block();
                        }
                        return false;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public Mono<Void> execute(Message message, String alias, String[] args) {
        if (!Util.isPpmHost(message.getAuthorAsMember().block())) return null;
        Map<String, Integer> set = new HashMap<>();
        List<String> skippedLines = new ArrayList<>();
        if(args.length == 0) {
            if(message.getGuildId().get().asLong() == CTFDiscordBot.CTF_DISCORD_ID) { //CTF Discord specific
                Message msg = message.getChannel().block().createMessage(m -> {
                    m.setEmbed(e -> {
                        e.setColor(Color.TAHITI_GOLD);
                        e.setDescription(":arrows_clockwise: Removing Roles!");
                    });
                }).block();
                for (Role role : Util.getRolesToRemove(message.getGuild().block())) {
                    int roles = 0;
                    for (Member m : message.getGuild().block().getMembers().toIterable()) {
                        if (m.getRoleIds().contains(role.getId())) {
                            m.removeRole(role.getId(), DiscordBot.getBot().getPrefix() + "removeroles done by " +
                                    message.getAuthorAsMember().block().getDisplayName() + " in " + message.getChannel().block().getRestChannel().getData().block().name().get()).block();
                            roles++;
                        }
                    }
                    int finalRoles = roles;
                    set.put(role.getName(), finalRoles);
                }
                msg.edit(m -> {
                    m.setEmbed(e -> {
                        e.setColor(Color.GREEN);
                        e.setTitle(":white_check_mark: Success!");
                        StringBuilder sb = new StringBuilder();
                        for(String string : set.keySet()) {
                            sb.append(String.format(" - %s `%s` roles  were removed!\n", set.get(string), string));
                        }
                        e.setDescription(sb.toString());
                        if(!skippedLines.isEmpty()) {
                            e.addField("Skipped Roles", skippedLines.toString(), false);
                        }
                    });
                }).block();
                return null;
            }else {
                message.getChannel().block().createMessage(m -> {
                    m.setEmbed(e -> {
                        e.setColor(Color.RED);
                        e.setDescription(":x: You need to include some arguments!\ni.e. `!removeroles role 1,role 2, role3`");
                    });
                }).block();
                return null;
            }
        }else {
            Message msg = message.getChannel().block().createMessage(m -> {
                m.setEmbed(e -> {
                    e.setColor(Color.TAHITI_GOLD);
                    e.setDescription(":arrows_clockwise: Removing Roles!");
                });
            }).block();
            for(String s : args) {
                if(!s.equalsIgnoreCase("red team") && !s.equalsIgnoreCase("blue team") && !s.equalsIgnoreCase("playing")) {
                    skippedLines.add(s);
                    continue;
                }
                if(message.getGuild().block().getRoles().map(Role::getName).collect(Collectors.toList()).block().contains(s)) {
                    Role role = Util.getRoleByName(s, message.getGuild().block());
                    Set<Snowflake> settest = new HashSet<>(Collections.singleton(role.getId()));
                    if(!message.getAuthorAsMember().block().hasHigherRoles(settest).block()) {
                        skippedLines.add(s);
                        continue;
                    }
                    int roles = 0;
                    for (Member m : message.getGuild().block().getMembers().toIterable()) {
                        if (m.getRoleIds().contains(role.getId())) {
                            m.removeRole(role.getId(), DiscordBot.getBot().getPrefix() + "removeroles done by " +
                                    message.getAuthorAsMember().block().getDisplayName() + " in " + message.getChannel().block().getRestChannel().getData().block().name().get()).block();
                            roles++;
                        }
                    }
                    int finalRoles = roles;
                    set.put(role.getName(), finalRoles);
                }else {
                    skippedLines.add(s);
                }
            }
            msg.edit(m -> {
                m.setEmbed(e -> {
                    e.setColor(Color.GREEN);
                    e.setTitle(":white_check_mark: Success!");
                    StringBuilder sb = new StringBuilder();
                    for(String string : set.keySet()) {
                        sb.append(String.format(" - %s `%s` roles  were removed!\n", set.get(string), string));
                    }
                    e.setDescription(sb.toString());
                    if(!skippedLines.isEmpty()) {
                        e.addField("Skipped Roles", skippedLines.toString(), false);
                    }
                });
            }).block();
            return null;
        }
    }
}
