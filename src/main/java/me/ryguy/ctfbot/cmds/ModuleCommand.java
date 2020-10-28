package me.ryguy.ctfbot.cmds;

import discord4j.core.object.entity.Message;
import me.ryguy.ctfbot.CTFDiscordBot;
import me.ryguy.ctfbot.module.Modules;
import me.ryguy.discordapi.command.Command;
import me.ryguy.discordapi.listeners.Listener;
import reactor.core.publisher.Mono;

public class ModuleCommand extends Command {
    public ModuleCommand() {
        super("modules", "module");
    }
    @Override
    public Mono<Void> execute(Message message, String alias, String[] args) {
        if(args.length == 0) {
            message.getChannel().block().createEmbed(em -> {
                em.setTitle("MODULES!");
                for(Modules m : Modules.values()) {
                    em.addField(m.getModule().name, buildModuleDesc(m), false);
                }
            }).block();
        }
        return Mono.empty();
    }
    @Override
    public boolean canExecute(Message e, boolean asdf) {
        if (e.getAuthor().isPresent()) {
            return e.getAuthor().get().getId().asLong() == CTFDiscordBot.BOT_OWNER;
        }
        return false;
    }

    private String buildModuleDesc(Modules m) {
        StringBuilder sb = new StringBuilder();
        sb.append(m.getModule().description + "\n\nCommands: \n");
        for(Command c : m.getModule().getCommands()) {
            sb.append(c.getName() + "\n");
        }
        sb.append("Listeners: \n");
        for(Listener l : m.getModule().getListeners()) {
            sb.append(l.getClass().getSimpleName() + "\n");
        }
        return sb.toString();
    }
}
