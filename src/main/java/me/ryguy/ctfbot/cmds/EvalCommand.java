package me.ryguy.ctfbot.cmds;

import discord4j.core.object.entity.Message;
import discord4j.rest.util.Color;
import me.ryguy.ctfbot.CTFDiscordBot;
import me.ryguy.ctfbot.util.Util;
import me.ryguy.discordapi.command.Command;
import reactor.core.publisher.Mono;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;

public class EvalCommand extends Command {

    private ScriptEngine engine;

    public EvalCommand() {
        super("eval");

        this.engine = new ScriptEngineManager().getEngineByName("nashorn");

        this.engine.put("bot", CTFDiscordBot.getBot());
        this.engine.put("gateway", CTFDiscordBot.getBot().getGateway());
        this.engine.put("client", CTFDiscordBot.getBot().getClient());
        this.engine.put("dir", new File(System.getProperty("user.dir")));
    }
    @Override
    public Mono<Void> execute(Message message, String alias, String[] args) {
        try {
            Object res = engine.eval(Util.connectArray(args, 0));
            message.getChannel().block().createEmbed(em -> {
                em.setTitle("Eval");
                em.setColor(Color.GREEN);
                em.addField(":pencil: Input", Util.connectArray(args, 0), false);
                em.addField(":pencil: Output", res.toString(), false);
            }).block();
        } catch (ScriptException e) {
            message.getChannel().block().createEmbed(em -> {
                em.setTitle("Eval - " + e.getClass().getSimpleName());
                em.setColor(Color.RED);
                em.addField(":pencil: Input", Util.connectArray(args, 0), false);
                em.addField(":pencil: Error", e.getMessage(), false);
            }).block();
            e.printStackTrace();
        }
        return null;
    }
    @Override
    public boolean canExecute(Message e, boolean asdf) {
        if(e.getAuthor().isPresent()) {
            return e.getAuthor().get().getId().asLong() == CTFDiscordBot.BOT_OWNER;
        }
        return false;
    }
}
