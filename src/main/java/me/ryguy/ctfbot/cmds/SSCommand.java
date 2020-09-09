package me.ryguy.ctfbot.cmds;

import discord4j.core.object.entity.Message;
import me.ryguy.discordapi.command.Command;
import reactor.core.publisher.Mono;

public class SSCommand extends Command {
    public SSCommand() {
        super("spreadsheet2", "ss2");
    }

    @Override
    public Mono<Void> execute(Message message, String s, String[] strings) {
        return null;
    }
}
