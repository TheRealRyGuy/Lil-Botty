package me.ryguy.ctfbot.modules.quotes;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.rest.util.Permission;
import me.ryguy.ctfbot.modules.ModuleListener;
import me.ryguy.ctfbot.modules.Modules;
import me.ryguy.discordapi.listeners.DiscordEvent;
import me.ryguy.discordapi.listeners.Listener;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@ModuleListener(module = Modules.QUOTES)
public class QuoteListener implements Listener {
    public static final long COOLDOWN = 15 * 1000; // 15 seconds in millis
    private final Map<Snowflake, Long> cooldowns = new HashMap<>();

    @DiscordEvent
    public void onReactAdd(ReactionAddEvent event) {
        if (!(event.getChannel().block() instanceof GuildMessageChannel)) return;

        GuildMessageChannel chan = (GuildMessageChannel) event.getChannel().block();

        if (!chan.getMembers().map(Member::getId).collect(Collectors.toList()).block().contains(event.getUser().block().getId()))
            return;
        if (!chan.getEffectivePermissions(event.getUserId()).block().contains(Permission.SEND_MESSAGES)) return;
        if (!isQuoteEmoji(event.getEmoji())) return;
        if (!event.getMember().isPresent()) return;
        if (event.getMessage().block().getContent() == null) return;

        String content = event.getMessage().block().getContent().trim();

        if (content.isEmpty() || content.equalsIgnoreCase("")) return;

        Member member = event.getMember().get(); // person who quoted
        Member author = event.getMessage().block().getAuthorAsMember().block(); // original message author

        if (cooldowns.containsKey(member.getId())) {
            if (System.currentTimeMillis() - cooldowns.get(member.getId()) <= COOLDOWN) {
                removeReactionEmoji(event.getMessage().block(), event.getEmoji(), member.getId());
                return;
            }

            cooldowns.put(member.getId(), System.currentTimeMillis());
        }

        removeReactionEmoji(event.getMessage().block(), event.getEmoji(), member.getId());
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
        if (e.asUnicodeEmoji().isPresent()) {
            return unicodeQuotes.contains(e.asUnicodeEmoji().get().getRaw());
        } else if (e.asCustomEmoji().isPresent()) {
            return e.asCustomEmoji().get().getName().equalsIgnoreCase("quote");
        }
        return false;
    }

    private void removeReactionEmoji(Message message, ReactionEmoji emoji, Snowflake memberId) {
        message.removeReaction(emoji, memberId).block();
    }
}
