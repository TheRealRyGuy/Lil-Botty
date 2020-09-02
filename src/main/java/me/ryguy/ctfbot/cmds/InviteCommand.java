package me.ryguy.ctfbot.cmds;

import discord4j.core.object.entity.Message;
import me.ryguy.discordapi.command.Command;
import reactor.core.publisher.Mono;

public class InviteCommand extends Command {
    public InviteCommand() {
        super("invite", "inviteme");
    }

    @Override
    public Mono<Void> execute(Message message, String s, String[] strings) {
        message.getChannel().block().createMessage("Invite me to your discord through <https://discord.com/oauth2/authorize?client_id=749700983105257482&scope=bot&permissions=2146958847>! \n \n " +
                "Keep in mind, the bot is still heavily in beta, so expect bugs / missing features \n " +
                "Feel free to contribute at <https://github.com/TheRealRyGuy/CTF-Community-Discord-Bot>").block();
        return null;
    }
}
