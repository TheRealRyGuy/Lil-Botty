package me.ryguy.ctfbot.cmds;

import discord4j.core.object.entity.Message;
import discord4j.rest.util.Color;
import me.ryguy.ctfbot.CTFDiscordBot;
import me.ryguy.discordapi.DiscordBot;
import me.ryguy.discordapi.command.Command;
import reactor.core.publisher.Mono;

public class RestartCommand extends Command {
    public RestartCommand() {
        super("restart");
    }

    @Override
    public Mono<Void> execute(Message message, String s, String[] strings) {
        message.getChannel().block().createEmbed(em -> {
            em.setColor(Color.TAHITI_GOLD);
            em.setDescription(":wave: Goodbye! Restarting in 5 seconds");
            em.setFooter("!findmap BOSTON", DiscordBot.getBot().getGateway().getSelf().block().getAvatarUrl());
        }).block();
        System.exit(0);
        return null;
    }

    @Override
    public boolean canExecute(Message e, boolean shouldSendMessage) {
        if (e.getAuthor().isPresent()) {
            return e.getAuthor().get().getId().asLong() == CTFDiscordBot.BOT_OWNER || e.getAuthor().get().getId().asLong() == 200299141438504960L;
        }
        return false;
    }
}
