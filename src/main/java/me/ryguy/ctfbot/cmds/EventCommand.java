package me.ryguy.ctfbot.cmds;

import discord4j.core.object.entity.Message;
import me.ryguy.ctfbot.types.CTFDiscordOnlyCommand;
import me.ryguy.ctfbot.types.Event;
import me.ryguy.ctfbot.util.EmbedPresets;
import me.ryguy.ctfbot.util.Util;
import me.ryguy.discordapi.util.WorkFlow;
import reactor.core.publisher.Mono;

public class EventCommand extends CTFDiscordOnlyCommand {
    public EventCommand() {
        super("event", "ppm");
    }

    @Override
    public Mono<Void> execute(Message message, String s, String[] strings) {
        if(!Util.isPpmHost(message.getAuthorAsMember().block())) return null;
        WorkFlow<Event> flow = new WorkFlow<>(new Event(), message.getChannel().block(), message.getAuthor().get());
        flow.addRule("!cancel", wf -> {
            wf.end();
            message.getChannel().block().createMessage(e -> {
                e.setEmbed(em -> em = EmbedPresets.success().setDescription(":x: Event Cancelled!"));
            });
        });
        return null;
    }
}
