package me.ryguy.ctfbot.cmds;

import discord4j.core.object.entity.Message;
import discord4j.rest.util.Color;
import me.ryguy.discordapi.DiscordBot;
import me.ryguy.discordapi.command.Command;
import me.ryguy.discordapi.command.CommandManager;
import reactor.core.publisher.Mono;

public class HelpCommand extends Command {
    public HelpCommand() {
        super("help");
    }

    @Override
    public Mono<Void> execute(Message message, String alias, String[] args) {
        StringBuilder commands = new StringBuilder();
        for(Command c : CommandManager.getRegisteredCommands()) {
            if(c.canExecute(message, false)) {
                commands.append(c.getName() + "\n");
            }
        }
        message.getChannel().block().createEmbed(e -> {
           e.setTitle("Welcome to WaffleBot2!");
           e.setThumbnail(DiscordBot.getBot().getGateway().getSelf().block().getAvatarUrl());
           e.setColor(Color.TAHITI_GOLD);
           e.setDescription("Welcome to WaffleBot, coded initially by RyGuy#0001, and with a lot of help from 915 and mikye!");
           e.addField("Commands you can use", commands.toString(), false);
           e.addField("Contributing", "To contribute to WaffleBot, you can contribute at <https://github.com/TheRealRyGuy/CTF-Community-Discord-Bot>! Any contributions are welcome!", false);
           e.addField("Inviting", "To invite the bot to your server, use this invite: <https://discord.com/oauth2/authorize?client_id=749700983105257482&scope=bot&permissions=2146958847>", false);
        }).block();
        return null;
    }
}
