package me.ryguy.ctfbot.modules.roles;

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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SetRolesCommand extends Command {
    public SetRolesCommand() {
        super("setroles", "roles");
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
        Role currentRole = null;
        List<String> skippedLines = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        if (message.getContent().split("\n").length == 0) {
            message.getChannel().block().createMessage(":x: You need to specify roles or users to set!").block();
            return null;
        }

        Message toEdit = message.getChannel().block().createMessage(msg -> {
            msg.setEmbed(embed -> {
                embed.setColor(Color.TAHITI_GOLD);
                embed.setDescription(":repeat: Setting Roles");
            });
        }).block();
        int rolesSet = 0;
        for (String s : message.getContent().split("\n")) {
            if (s.trim().equalsIgnoreCase(DiscordBot.getBot().getPrefix() + "setroles")) continue;
            if (s.trim() == "" || s.trim().isEmpty() || s == null) {
                skippedLines.add(s);
                continue;
            }
            if (Util.parseMention(s) == null) {
                skippedLines.add(s);
                continue;
            }
            if (message.getGuild().block().getRoleIds().contains(Snowflake.of(Long.valueOf(Util.parseMention(s))))) {
                currentRole = message.getGuild().block().getRoleById(Snowflake.of(Long.valueOf(Util.parseMention(s)))).block();
            } else if (message.getGuild().block().getMembers().map(Member::getId).collect(Collectors.toList()).block().contains(Snowflake.of(Util.parseMention(s)))) {
                if (currentRole == null) {
                    skippedLines.add(s);
                    continue;
                } else {
                    try { //Catches permission errors
                        Member mem = message.getGuild().block().getMemberById(Snowflake.of(Util.parseMention(s))).block();
                        mem.addRole(currentRole.getId(), "Setroles done by " + message.getAuthorAsMember().block().getDisplayName() + " in " +
                                message.getChannel().block().getRestChannel().getData().block().name().get()).block();
                        rolesSet++;
                    } catch (Exception e) {
                        Util.sendErrorMessage(e, message);
                        errors.add(s);
                        continue;
                    }
                }
            } else {
                skippedLines.add(s);
            }
        }

        int finalRolesSet = rolesSet;
        toEdit.edit(msg -> {
            msg.setEmbed(embed -> {
                embed.setColor(Color.GREEN);
                embed.setDescription(String.format(":white_check_mark: %s roles set!", finalRolesSet));
                if (skippedLines.size() != 0) {
                    embed.addField("Skipped Lines", skippedLines.toString(), false);
                }
                if (errors.size() != 0) {
                    embed.addField(":x: Errors", errors.toString(), false);
                    embed.setFooter("Errors are probably due to the bot missing permissions", null);
                }
            });
        }).block();

        return null;
    }
}
