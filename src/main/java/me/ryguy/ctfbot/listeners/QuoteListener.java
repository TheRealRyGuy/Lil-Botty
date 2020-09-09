package me.ryguy.ctfbot.listeners;

import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.rest.util.Permission;
import me.ryguy.discordapi.listeners.DiscordEvent;
import me.ryguy.discordapi.listeners.Listener;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class QuoteListener implements Listener {
    @DiscordEvent
    public void onReactAdd(ReactionAddEvent event) {
        System.out.println("0");
        if(!(event.getChannel().block() instanceof GuildMessageChannel)) return;
        System.out.println("1");
        GuildMessageChannel chan = (GuildMessageChannel) event.getChannel().block();
        if(!chan.getMembers().map(Member::getId).collect(Collectors.toList()).block().contains(event.getUser().block().getId())) return;
        System.out.println("2");
        if(!chan.getEffectivePermissions(event.getUserId()).block().contains(Permission.SEND_MESSAGES)) return;
        System.out.println("3");
        if(!isQuoteEmoji(event.getEmoji())) return;
        System.out.println("4");
        if(!event.getMember().isPresent()) return;
        System.out.println("5");
        Member member = event.getMember().get();
        Member author = event.getMessage().block().getAuthorAsMember().block();
        event.getChannel().block().createEmbed(e -> {
            e.setAuthor(author.getDisplayName(), null, author.getAvatarUrl());
            e.setDescription(event.getMessage().block().getContent());
            e.setTimestamp(Instant.now());
            e.setColor(member.getColor().block());
            e.setFooter("Quoted by " + member.getDisplayName(), member.getAvatarUrl());
        }).block();
    }
    private boolean isQuoteEmoji(ReactionEmoji e) {
        List<String> unicodeQuotes = new ArrayList<>(Arrays.asList("🗨", "🗨️", "💬", "👁️"));
        if(e.asUnicodeEmoji().isPresent()) {
            System.out.println("Unicode: Returning " + unicodeQuotes.contains(e.asUnicodeEmoji().get().getRaw()));
            return unicodeQuotes.contains(e.asUnicodeEmoji().get().getRaw());
        }else if(e.asCustomEmoji().isPresent()) {
            System.out.println("Custom: Returning " + e.asCustomEmoji().get().getName().equalsIgnoreCase("quote"));
            return e.asCustomEmoji().get().getName().equalsIgnoreCase("quote");
        }
        return false;
    }
}
