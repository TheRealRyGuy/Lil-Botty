package me.ryguy.ctfbot.modules.games;

import discord4j.core.object.entity.Message;
import discord4j.rest.util.Color;
import me.ryguy.ctfbot.module.ModuleCommand;
import me.ryguy.ctfbot.module.Modules;
import me.ryguy.discordapi.DiscordBot;
import me.ryguy.discordapi.command.Command;
import reactor.core.publisher.Mono;

import java.util.Random;

@ModuleCommand(module = Modules.GAMES)
public class DiceRollCommand extends Command {
    public DiceRollCommand() {
        super("roll", "rolldice", "diceroll");
    }

    @Override
    public Mono<Void> execute(Message message, String s, String[] strings) {
        message.getChannel().block().createEmbed(em -> {
            em.setTitle(":game_die: Dice Roll");
            em.setDescription(String.format("You rolled a %s!", new Random().nextInt(6) + 1));
            em.setColor(Color.TAHITI_GOLD);
            em.setFooter("love and waffles :3", DiscordBot.getBot().getGateway().getSelf().block().getAvatarUrl());
        }).block();
        return Mono.empty();
    }
}
