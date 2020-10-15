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
import java.util.Arrays;
import java.util.stream.Collectors;

public class EvalCommand extends Command {

    private final ScriptEngine engine;

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
        String input = Util.connectArray(message.getContent().split(" "), 1);
        try {
            Object res = engine.eval(input);
            message.getChannel().block().createEmbed(em -> {
                em.setTitle("Eval");
                em.setColor(Color.GREEN);
                em.addField(":pencil: Input", "```java\n" + input + "```", false);
                em.addField(":white_check_mark: Output", "```xml\n" + res.toString() + "```", false);
            }).block();
        } catch (ScriptException e) {
            message.getChannel().block().createEmbed(em -> {
                em.setTitle("Eval - " + e.getClass().getSimpleName());
                em.setColor(Color.RED);
                em.addField(":pencil: Input", "```java\n" + input + "```", false);
                em.addField(":x: Error", "```xml\n" + e.getMessage() + "```", false);
            }).block();
        }
        return null;
    }

    @Override
    public boolean canExecute(Message e, boolean asdf) {
        if (e.getAuthor().isPresent()) {
            return e.getAuthor().get().getId().asLong() == CTFDiscordBot.BOT_OWNER;
        }
        return false;
    }
}
