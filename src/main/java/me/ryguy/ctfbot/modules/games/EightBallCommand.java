package me.ryguy.ctfbot.modules.games;

import discord4j.core.object.entity.Message;
import discord4j.rest.util.Color;
import me.ryguy.discordapi.DiscordBot;
import me.ryguy.discordapi.command.Command;
import reactor.core.publisher.Mono;

import java.util.*;

public class EightBallCommand extends Command {

    private static final Map<String, BallResult> possibilities = new HashMap<>();

    static {
        possibilities.put("It is certain", BallResult.GOOD);
        possibilities.put("It is decidedly so", BallResult.GOOD);
        possibilities.put("Yes definitely", BallResult.GOOD);
        possibilities.put("You may rely on it", BallResult.GOOD);
        possibilities.put("As I see it, yes", BallResult.GOOD);
        possibilities.put("Most likely", BallResult.GOOD);
        possibilities.put("Outlook good", BallResult.GOOD);
        possibilities.put("Yes", BallResult.GOOD);
        possibilities.put("Signs point to yes", BallResult.GOOD);
        possibilities.put("Reply hazy, try again", BallResult.OKAY);
        possibilities.put("Ask again later", BallResult.OKAY);
        possibilities.put("Better not to tell you now", BallResult.OKAY);
        possibilities.put("Cannot predict now", BallResult.OKAY);
        possibilities.put("Concentrate and ask again", BallResult.OKAY);
        possibilities.put("Don't count on it", BallResult.BAD);
        possibilities.put("My reply is no", BallResult.BAD);
        possibilities.put("My sources say no", BallResult.BAD);
        possibilities.put("Outlook not so good", BallResult.BAD);
        possibilities.put("My sources say no", BallResult.BAD);
        possibilities.put("Outlook not so good", BallResult.BAD);
        possibilities.put("Very doubtful", BallResult.BAD);
    }

    public EightBallCommand() {
        super("8ball");
    }

    @Override
    public Mono<Void> execute(Message message, String alias, String[] args) {
        if (args.length == 0) {
            message.getChannel().block().createEmbed(em -> {
                em.setColor(Color.RED);
                em.setTitle("8Ball :8ball:");
                em.setDescription(":x: You need to ask the 8ball something!");
            }).block();
        } else {
            List<String> toGrab = new ArrayList<>(possibilities.keySet());
            Collections.shuffle(toGrab);
            String res = toGrab.get(0);
            message.getChannel().block().createEmbed(em -> {
                em.setTitle("8Ball :8ball:");
                em.addField("You asked: ", String.join(" ", args), false);
                em.addField("Result", res, false);
                em.setColor(possibilities.get(res).getColor());
                em.setThumbnail(message.getAuthorAsMember().block().getAvatarUrl());
                em.setFooter("hugs and pugs <3", DiscordBot.getBot().getGateway().getSelf().block().getAvatarUrl());
            }).block();
        }
        return null;
    }

    private enum BallResult {
        GOOD, OKAY, BAD;

        private Color getColor() {
            switch (this) {
                case GOOD:
                    return Color.GREEN;
                case OKAY:
                    return Color.of(255, 215, 0);
                case BAD:
                    return Color.RED;
                default:
                    return Color.TAHITI_GOLD;
            }
        }
    }
}
