package me.ryguy.ctfbot.modules.games;

import discord4j.core.object.entity.Message;
import discord4j.rest.util.Color;
import me.ryguy.discordapi.DiscordBot;
import me.ryguy.discordapi.command.Command;
import reactor.core.publisher.Mono;

import java.util.Random;

public class CoinFlipCommand extends Command {
    public CoinFlipCommand() {
        super("coinflip", "flipcoin", "flip", "flipacoin");
    }

    @Override
    public Mono<Void> execute(Message message, String s, String[] strings) {
        if(new Random().nextInt(2) == 0) {
            message.getChannel().block().createEmbed(em -> {
                em.setTitle("Coinflip :moneybag:");
                em.setColor(Color.TAHITI_GOLD);
                em.setDescription("You flipped heads!");
                em.setFooter("excommunicate mutes", DiscordBot.getBot().getGateway().getSelf().block().getAvatarUrl());
            }).block();
        }else {
            message.getChannel().block().createEmbed(em -> {
                em.setTitle("Coinflip :moneybag:");
                em.setColor(Color.TAHITI_GOLD);
                em.setDescription("You flipped tails!");
                em.setFooter("excommunicate mutes", DiscordBot.getBot().getGateway().getSelf().block().getAvatarUrl());
            }).block();
        }
        return null;
    }
}
