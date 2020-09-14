package me.ryguy.ctfbot.cmds.ctf;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.Role;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.rest.util.Color;
import me.ryguy.ctfbot.CTFDiscordBot;
import me.ryguy.ctfbot.types.CTFDiscordOnlyCommand;
import me.ryguy.ctfbot.types.PPMStrike;
import me.ryguy.ctfbot.util.DiscordUtil;
import me.ryguy.ctfbot.util.StrikeUtil;
import me.ryguy.ctfbot.util.Util;
import me.ryguy.discordapi.DiscordBot;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
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
                em.setDescription("Possible Arguments: \n`!strike @user [reason]`\n`!strike listactive`\n`!strike listexpired`\n`!strike info @user`\n`!strike remove [id]");
            }).block();
        }else {
            if(args[0].equalsIgnoreCase("info")) {
                if(args.length != 2) {
                    message.getChannel().block().createEmbed(em -> {
                        em.setColor(Color.RED);
                        em.setTitle(":x: Invalid Usage!");
                        em.setDescription("Proper Usage: `!strike info @User`");
                    }).block();
                    return null;
                }
                if(DiscordUtil.isValidUserMention(args[1], message.getGuild().block())) {
                    message.getChannel().block().createEmbed(em -> {
                        em.setColor(Color.RED);
                        em.setDescription(":x: You need to use a valid user mention!");
                    }).block();
                    return null;
                }
                List<PPMStrike> strikes = StrikeUtil.getStrikes(message.getAuthor().get());
                String id = DiscordUtil.parseMention(args[0]);
                if(strikes.size() == 0) {
                    message.getChannel().block().createEmbed(em -> {
                        em.setTitle(":zap: Strike Information");
                        em.setDescription("Strike list for user " + DiscordUtil.getUserTag(id));
                        em.addField("Strikes", "This user has never been striked!", false);
                        em.setColor(Color.TAHITI_GOLD);
                    });
                }else {
                    List<PPMStrike> active = strikes.parallelStream().filter(PPMStrike::isActive).collect(Collectors.toList());
                    List<PPMStrike> inactive = strikes.parallelStream().filter(s -> !s.isActive()).collect(Collectors.toList());
                    message.getChannel().block().createEmbed(em -> {
                        em.setTitle(":zap: Strike Information");
                        em.setDescription("Strike list for user " + DiscordUtil.getUserTag(id));
                        if(active.size() == 0) {
                            em.addField("Active Strikes", "This user has no active strikes!", false);
                        }else {
                            em.addField("Active Strikes", StrikeUtil.buildStrikeList(active), false);
                        }
                        if(inactive.size() == 0) {
                            em.addField("Expired Strikes", "This user has no expired strikes!", false);
                        }else {
                            em.addField("Expired Strikes", StrikeUtil.buildStrikeList(inactive), false);
                        }
                    }).block();
                }
            }else if(args[0].equalsIgnoreCase("listactive")) {
                message.getChannel().block().createEmbed(em -> {
                    em.setTitle(":zap: Active Strikes");
                    em.setColor(Color.TAHITI_GOLD);
                    if(StrikeUtil.getActiveStrikes().size() == 0) {
                        em.addField("Active Strikes", "There are no active strikes!", false);
                    }else {
                        em.addField("Active Strikes", StrikeUtil.buildStrikeList(StrikeUtil.getActiveStrikes()), false);
                    }
                }).block();
            }else if(args[0].equalsIgnoreCase("listexpired")) {
                message.getChannel().block().createEmbed(em -> {
                    em.setTitle(":zap: Expired Strikes");
                    em.setColor(Color.TAHITI_GOLD);
                    if(StrikeUtil.getExpiredStrikes().size() == 0) {
                        em.addField("Expired Strikes", "There are no Expired strikes!", false);
                    }else {
                        StringBuilder sb = new StringBuilder();
                        for(PPMStrike strike : StrikeUtil.getExpiredStrikes()) {
                            sb.append(strike.getTier().getEmoji() + DiscordUtil.getUserTag(strike.getStriked()) + "was striked by " + DiscordUtil.getUserTag(strike.getStrikedBy()) + "\n");
                        }
                        em.addField("Expired Strikes", sb.toString(), false);
                    }
                }).block();
            }else if(args[0].equalsIgnoreCase("remove")) {
                if(DiscordUtil.isValidUserMention(args[0], message.getGuild().block())) {
                    List<PPMStrike> strikes = StrikeUtil.getStrikes(DiscordUtil.getUserByMention(args[0])).parallelStream().filter(PPMStrike::isActive).collect(Collectors.toList());
                    if(strikes.size() == 0) {
                        message.getChannel().block().createEmbed(em -> {
                            em.setColor(Color.RED);
                            em.setTitle(":x: Invalid User!");
                            em.setDescription("Invalid: You cannot remove strikes from this user, as they currently are not striked!`");
                        }).block();
                    }else {
                        try {
                            CTFDiscordBot.data.strikes.removeAll(strikes);
                            CTFDiscordBot.data.save(CTFDiscordBot.DATA_FILE);
                            message.getChannel().block().createEmbed(em -> {
                                em.setTitle(":zap: Strikes Removed");
                                em.setDescription("Cleared these active strikes for " + args[0] + "!");
                                int i = 0;
                                for(PPMStrike strike : strikes) {
                                    em.addField("Strike " + i, strike.toString(), false);
                                    i++;
                                }
                                em.setTimestamp(Instant.now());
                            }).block();
                        }catch(IOException e) {
                            message.getChannel().block().createEmbed(em -> {
                                em.setColor(Color.RED);
                                em.setDescription("yo something broke my bad");
                            }).block();
                            e.printStackTrace();
                        }
                    }
                }
            }else if(DiscordUtil.isValidUserMention(args[0], message.getGuild().block())) {
                if(args.length == 1) {
                    message.getChannel().block().createEmbed(em -> {
                        em.setColor(Color.RED);
                        em.setTitle(":x: Invalid Usage!");
                        em.setDescription("Invalid: You need a reason! \n Proper Usage: `!strike @User reason`");
                    }).block();
                    return null;
                }
                PPMStrike strike = new PPMStrike(StrikeUtil.getNextTier(DiscordUtil.getUserByMention(args[0])),
                        Util.connectArray(args, 1), DiscordUtil.getUserByMention(args[0]).getId().asLong(),
                        message.getAuthor().get().getId().asLong());
                if(StrikeUtil.strikeUser(strike)) {
                    ((MessageChannel) DiscordBot.getBot().getGateway().getChannelById(Snowflake.of(276518289289773067L)).block()).createEmbed(em -> {
                        em.setColor(Color.TAHITI_GOLD);
                        em.setTitle(":zap: Player Striked!");
                        em.setDescription(String.format("%s has been striked\n Reason: %s\nThis is their %s strike", args[0], String.join(" ", Arrays.asList(args).remove(0)), strike.getTier().name().toLowerCase()));
                        em.setTimestamp(Instant.now());
                    }).block();
                }else {
                    message.getChannel().block().createEmbed(em -> {
                        em.setColor(Color.RED);
                        em.setDescription("ERROR! Do me a favor and @ RyGuy rn");
                        em.setTimestamp(Instant.now());
                    }).block();
                }
            }else {
                message.getChannel().block().createEmbed(em -> {
                    em.setColor(Color.RED);
                    em.setTitle(":x: Invalid Arguments!");
                    em.setDescription("Possible Arguments: \n`!strike @user [reason]`\n`!strike listactive`\n`!strike listexpired`\n`!strike info @user`\n`!strike remove [id]");
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
