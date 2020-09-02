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
    public Mono<Void> execute(Message message, String s, String[] strings) {

        if (!Util.isPpmHost(message.getAuthorAsMember().block())) {
            return null;
        }
        OverlyBasicEvent obe = new OverlyBasicEvent("**" + message.getAuthorAsMember().block().getDisplayName() + " PPM!**", ReactionEmoji.unicode("✅"));
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
