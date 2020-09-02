package me.ryguy.ctfbot.temp;

import discord4j.core.object.entity.Message;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.rest.util.Color;
import me.ryguy.ctfbot.types.CTFDiscordOnlyCommand;
import me.ryguy.ctfbot.util.Util;
import reactor.core.publisher.Mono;

public class BasicSignUpCommand extends CTFDiscordOnlyCommand {
    public BasicSignUpCommand() {
        super("ppm", "ppm");
    }

    @Override
    public Mono<Void> execute(Message message, String s, String[] args) {

        if (!Util.isPpmHost(message.getAuthorAsMember().block())) {
            return null;
        }
        String name;
        if(args.length == 0) {
            name = message.getAuthorAsMember().block().getDisplayName() + " PPM!";
        }else {
            name = String.join(" ");
        }
        OverlyBasicEvent obe = new OverlyBasicEvent(String.format("**%s**", name), ReactionEmoji.unicode("✅"));
        obe.init();
        obe.setupSignups();

        message.getChannel().block().createMessage(m -> {
            m.setEmbed(e -> {
                e.setColor(Color.TAHITI_GOLD);
                e.setDescription(":white_check_mark: Did ppm :D");
            });
        }).block();
        return null;
    }
}
