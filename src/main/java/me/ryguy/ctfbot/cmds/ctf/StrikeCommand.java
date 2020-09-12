package me.ryguy.ctfbot.cmds.ctf;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.Role;
import discord4j.rest.util.Color;
import me.ryguy.ctfbot.CTFDiscordBot;
import me.ryguy.ctfbot.types.CTFDiscordOnlyCommand;
import me.ryguy.ctfbot.util.Util;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

public class StrikeCommand extends CTFDiscordOnlyCommand {

    public StrikeCommand() {
        super("strike");
    }

    @Override
    public Mono<Void> execute(Message message, String alias, String[] args) {
        if(args.length != 0) {
            message.getChannel().block().createEmbed(em -> {
                em.setColor(Color.RED);
                em.setTitle(":x: You need to have some arguments!");
                em.setDescription("Possible Arguments: \n`!strike @user [reason]`\n`!strike list`\n`!strike info @user`\n`!strike remove [id]");
            }).block();
        }else {
            if(args[0].equalsIgnoreCase("info")) {

            }else if(args[0].equalsIgnoreCase("list")) {

            }else if(args[0].equalsIgnoreCase("remove")) {

            }else if(Util.isValidMention(args[0], message.getGuild().block())) {

            }else {
                message.getChannel().block().createEmbed(em -> {
                    em.setColor(Color.RED);
                    em.setTitle(":x: Invalid Arguments!");
                    em.setDescription("Possible Arguments: \n`!strike @user [reason]`\n`!strike list`\n`!strike info @user`\n`!strike remove [id]");
                }).block();
            }
        }
        return null;
    }

    @Override
    public boolean canExecute(Message e, boolean shouldSend) {
        if (e.getGuildId().isPresent()) {
            if (e.getGuildId().get().asLong() == CTFDiscordBot.CTF_DISCORD_ID) {
                if (e.getAuthorAsMember().blockOptional().isPresent()) {
                    if (e.getAuthorAsMember().block().getRoles().map(Role::getName).collect(Collectors.toList()).block().contains("PPM Host")) {
                        return true;
                    } else {
                        if (shouldSend) {
                            e.getChannel().block().createEmbed(em -> {
                                em.setColor(Color.RED);
                                em.setDescription(":x: You need to have the role `PPM Host` to use this command!");
                            }).block();
                        }
                        return false;
                    }
                }
            }
        }
        return false;
    }
}
