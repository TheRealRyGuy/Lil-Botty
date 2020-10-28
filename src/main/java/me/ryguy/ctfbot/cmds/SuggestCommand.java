package me.ryguy.ctfbot.cmds;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.entity.channel.PrivateChannel;
import discord4j.rest.util.Color;
import me.ryguy.ctfbot.CTFDiscordBot;
import me.ryguy.ctfbot.util.Util;
import me.ryguy.discordapi.command.Command;
import reactor.core.publisher.Mono;

import java.time.Instant;

public class SuggestCommand extends Command {

    public static final long channelId = 765848261747015680L;

    public SuggestCommand() {
        super("suggest", "suggestion");
    }
    @Override
    public Mono<Void> execute(Message message, String alias, String[] args) {
        if(args.length == 0 || Util.connectArray(args, 0).trim().isEmpty() || !message.getAuthor().isPresent()) {
            message.getChannel().block().createEmbed(em -> {
                em.setColor(Color.RED);
                em.setTitle(":x: Error!");
                em.setDescription("You need to actually type out text to suggest!\ni.e. `!suggest RyGuy should take a shower`");
                em.setTimestamp(Instant.now());
            }).block();
            return Mono.empty();
        }
        String suggestion = Util.connectArray(args, 0);
        ((MessageChannel) CTFDiscordBot.getBot().getGateway().getChannelById(Snowflake.of(channelId)).block()).createEmbed(em -> {
            em.setColor(Color.BLUE);
            em.setTimestamp(Instant.now());
            em.setTitle(String.format("Suggestion from: %s#%s", message.getAuthor().get().getUsername(), message.getAuthor().get().getDiscriminator()));
            em.setDescription("**Suggestion:** " + suggestion);
            if(message.getChannel().block() instanceof PrivateChannel) {
                em.addField("Sent From", "DMs", false);
            }else {
                em.addField("Guild", message.getGuild().block().getName(), true);
                String channelName = message.getChannel().block().getRestChannel().getData().block().name().isAbsent() ? "Inaccessible Name" : message.getChannel().block().getRestChannel().getData().block().name().get();
                em.addField("Channel", channelName, true);
            }
        }).block();
        message.getChannel().block().createEmbed(em -> {
            em.setColor(Color.GREEN);
            em.setTimestamp(Instant.now());
            em.setTitle(":white_check_mark: Success!!");
            em.setDescription(String.format("Suggestion: `%s`", suggestion));
        }).block();
        return Mono.empty();
    }
}
