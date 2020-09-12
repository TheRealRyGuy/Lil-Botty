package me.ryguy.ctfbot.cmds.games;

import discord4j.core.object.entity.Message;
import discord4j.rest.util.Color;
import discord4j.rest.util.Image;
import lombok.Getter;
import me.ryguy.discordapi.DiscordBot;
import me.ryguy.discordapi.command.Command;
import org.apache.commons.lang3.EnumUtils;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RockPaperScissorsCommand extends Command {
    public RockPaperScissorsCommand() {
        super("rps", "rockpaperscissors");
    }

    @Override
    public Mono<Void> execute(Message message, String alias, String[] args) {
        if(args.length != 1) {
            message.getChannel().block().createEmbed(em -> {
                em.setColor(Color.RED);
                em.setTitle("Rock Paper Scissors");
                em.setDescription(":x: You need to play something!");
            }).block();
        }else {
            if(!EnumUtils.isValidEnumIgnoreCase(RPS.class, args[0])) {
                message.getChannel().block().createEmbed(em -> {
                    em.setColor(Color.RED);
                    em.setTitle("Rock Paper Scissors");
                    em.setDescription(":x: You need to play a proper value! \nPossible values: `" + Arrays.asList(RPS.values()).toString().toLowerCase() + "`");
                    em.setFooter(":wc:", DiscordBot.getBot().getGateway().getSelf().block().getAvatarUrl());
                }).block();
            }else {
                List<RPS> rps = Arrays.asList(RPS.values());
                Collections.shuffle(rps);
                RPS consoleValue = rps.get(0);
                RPS playerValue = RPS.valueOf(args[0].toUpperCase());
                if(consoleValue.equals(playerValue)) {
                    message.getChannel().block().createEmbed(em -> {
                        em.setColor(Color.of(255, 215, 0));
                        em.setTitle("Rock Paper Scissors");
                        em.setDescription("We tied! I played " + consoleValue.getEmoji() + " and you played " + playerValue.getEmoji() + "!");
                        if(message.getGuild().block().getIconUrl(Image.Format.JPEG).isPresent()) {
                            em.setThumbnail(message.getGuild().block().getIconUrl(Image.Format.JPEG).get());
                        }
                        em.setFooter("replay map, start from overtime", DiscordBot.getBot().getGateway().getSelf().block().getAvatarUrl());
                    }).block();
                }else if(playerValue.isWin(consoleValue)) {
                    message.getChannel().block().createEmbed(em -> {
                        em.setColor(Color.GREEN);
                        em.setTitle("Rock Paper Scissors");
                        em.setDescription("You won! I played " + consoleValue.getEmoji() + " and you played " + playerValue.getEmoji() + "!");
                        em.setThumbnail(message.getAuthorAsMember().block().getAvatarUrl());
                        em.setFooter("replay map, start from overtime", DiscordBot.getBot().getGateway().getSelf().block().getAvatarUrl());
                    }).block();
                }else if(!playerValue.isWin(consoleValue)) {
                    message.getChannel().block().createEmbed(em -> {
                        em.setColor(Color.RED);
                        em.setTitle("Rock Paper Scissors");
                        em.setDescription("I won! I played " + consoleValue.getEmoji() + " and you played " + playerValue.getEmoji() + "!");
                        em.setThumbnail(DiscordBot.getBot().getGateway().getSelf().block().getAvatarUrl());
                        em.setFooter("replay map, start from overtime", DiscordBot.getBot().getGateway().getSelf().block().getAvatarUrl());
                    }).block();
                }else {
                    message.getChannel().block().createEmbed(em -> {
                        em.setColor(Color.RED);
                        em.setTitle("Rock Paper Scissors");
                        em.setDescription(":x: Something went wrong in your Rock Paper Scissors Game, there is no winner??");
                        if(message.getGuild().block().getIconUrl(Image.Format.JPEG).isPresent()) {
                            em.setThumbnail(message.getGuild().block().getIconUrl(Image.Format.JPEG).get());
                        }
                        em.setFooter("replay map, start from overtime", DiscordBot.getBot().getGateway().getSelf().block().getAvatarUrl());
                    }).block();
                }
            }
        }
        return null;
    }
    @Getter
    private enum Result {
        ROCK(RPS.ROCK, RPS.PAPER),
        PAPER(RPS.PAPER, RPS.SCISSORS),
        SCISSORS(RPS.SCISSORS, RPS.ROCK);
        Result(RPS type, RPS killer) {
            this.type = type;
            this.killer = killer;
        }
        private RPS type;
        private RPS killer;
    }
    private enum RPS {
        ROCK, PAPER, SCISSORS;
        private boolean isWin(RPS attack) {
            return Result.valueOf(this.name()).getKiller().equals(attack);
        }
        private String getEmoji() {
            switch(this) {
                case ROCK:
                    return ":curling_stone:";
                case PAPER:
                    return ":newspaper:";
                case SCISSORS:
                    return ":scissors:";
                default:
                    return ":scales:";
            }
        }
    }
}
