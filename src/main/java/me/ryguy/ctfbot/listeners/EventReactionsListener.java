package me.ryguy.ctfbot.listeners;

import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.event.domain.message.ReactionRemoveEvent;
import discord4j.core.object.entity.Member;
import me.ryguy.ctfbot.types.Event;
import me.ryguy.ctfbot.util.Util;
import me.ryguy.discordapi.listeners.DiscordEvent;
import me.ryguy.discordapi.listeners.Listener;

import java.util.stream.Collectors;

public class EventReactionsListener implements Listener {
    @DiscordEvent
    public void onReactAdd(ReactionAddEvent event) {
        if (Event.getEvent(event.getMessage().block()) == null) {
            return;
        }
        if (event.getUser().block().isBot()) {
            return;
        }
        if(!event.getEmoji().asUnicodeEmoji().isPresent()) {
            return;
        }

        Event e = Event.getEvent(event.getMessage().block());
        if (event.getEmoji().asUnicodeEmoji().get().getRaw().equalsIgnoreCase(e.getSignUpEmoji())) {
            e.addPlayer(event.getUser().block());
            if (event.getMember().isPresent() && !event.getMember().get().getRoles().collect(Collectors.toList()).block().contains(e.getGiveRole())) {
                try {
                    event.getMember().get().addRole(e.getGiveRole().getId());
                }catch(Exception ex) {
                    ex.printStackTrace();
                    Util.sendErrorMessage(ex, this, event);
                }
            }
        }else if(event.getEmoji().asUnicodeEmoji().get().getRaw().equalsIgnoreCase(e.getRejectEmoji())) {
            e.addNonPlayer(event.getUser().block());
            if (event.getMember().isPresent() && event.getMember().get().getRoles().collect(Collectors.toList()).block().contains(e.getGiveRole())) {
                try {
                    event.getMember().get().removeRole(e.getGiveRole().getId());
                }catch(Exception ex) {
                    ex.printStackTrace();
                    Util.sendErrorMessage(ex, this, event);
                }
            }
        }
    }

    @DiscordEvent
    public void onReactRemove(ReactionRemoveEvent event) {
        if (Event.getEvent(event.getMessage().block()) == null) {
            return;
        }
        if (event.getUser().block().isBot()) {
            return;
        }
        if(!event.getEmoji().asUnicodeEmoji().isPresent()) {
            return;
        }
        Event e = Event.getEvent(event.getMessage().block());

        if(event.getEmoji().asUnicodeEmoji().get().getRaw().equalsIgnoreCase(e.getSignUpEmoji())) {
            e.removePlayer(event.getUser().block());
        }else if(event.getEmoji().asUnicodeEmoji().get().getRaw().equalsIgnoreCase(e.getRejectEmoji())) {
            e.removeNonPlayer(event.getUser().block());
        }

        //why does this event not have a member getter? no one knows
        if (event.getGuild().block().getMemberById(event.getUserId()).blockOptional().isPresent()) {
            Member mem = event.getGuild().block().getMemberById(event.getUserId()).block();
            if(mem.getRoles().collect(Collectors.toList()).block().contains(e.getGiveRole()))
                try {
                    mem.removeRole(e.getGiveRole().getId());
                }catch(Exception ex) {
                    ex.printStackTrace();
                    Util.sendErrorMessage(ex, this, event);
                }
        }
    }
}
