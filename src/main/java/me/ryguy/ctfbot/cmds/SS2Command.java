package me.ryguy.ctfbot.cmds;

import discord4j.core.object.entity.Message;
import me.ryguy.discordapi.command.Command;
import reactor.core.publisher.Mono;

public class SS2Command extends Command {
    public SS2Command() {
        super("spreadsheet2", "ss2");
    }

    @Override
    public Mono<Void> execute(Message message, String s, String[] strings) {
        return null;
    }
}
