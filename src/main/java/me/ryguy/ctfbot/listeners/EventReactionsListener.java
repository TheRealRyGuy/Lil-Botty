package me.ryguy.ctfbot.listeners;

import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.event.domain.message.ReactionRemoveEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Role;
import me.ryguy.ctfbot.types.Event;
import me.ryguy.ctfbot.util.Util;
import me.ryguy.discordapi.listeners.DiscordEvent;
import me.ryguy.discordapi.listeners.Listener;

import java.util.stream.Collectors;

public class EventReactionsListener implements Listener {
    @DiscordEvent
    public void onReactAdd(ReactionAddEvent event) {
        System.out.println("Add");
        if (Event.getEvent(event.getMessage().block()) == null) {
            System.out.println("Null event");
            return;
        }
        if (event.getUser().block().isBot()) {
            System.out.println("Bot User");
            return;
        }
        if(!event.getEmoji().asUnicodeEmoji().isPresent()) {
            System.out.println("Unicode emoji not present");
            return;
        }

        Event e = Event.getEvent(event.getMessage().block());
        if (event.getEmoji().asUnicodeEmoji().get().getRaw().equalsIgnoreCase(e.getSignUpEmoji())) {
            e.addPlayer(event.getUser().block());
            if (event.getMember().isPresent()) {
                if (Util.getRoleByName("playing", event.getGuild().block()) == null) return;
                event.getMember().get().addRole(Util.getRoleByName("playing", event.getGuild().block()).getId(), "signed up to " + e.getName()).block();
            }
        }else if(event.getEmoji().asUnicodeEmoji().get().getRaw().equalsIgnoreCase(e.getRejectEmoji())) {
            e.addNonPlayer(event.getUser().block());
            if (event.getMember().isPresent()) {
                if (Util.getRoleByName("playing", event.getGuild().block()) == null) return;
                event.getMember().get().removeRole(Util.getRoleByName("playing", event.getGuild().block()).getId(), "rejected event " + e.getName()).block();
            }
        }
    }

    @DiscordEvent
    public void onReactRemove(ReactionRemoveEvent event) {
        System.out.println("Remove");
        if (Event.getEvent(event.getMessage().block()) == null) {
            System.out.println("Null event");
            return;
        }
        if (event.getUser().block().isBot()) {
            System.out.println("Bot User");
            return;
        }
        if(!event.getEmoji().asUnicodeEmoji().isPresent()) {
            System.out.println("Unicode emoji not present");
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
            if (Util.getRoleByName("playing", event.getGuild().block()) == null) return;
            Member mem = event.getGuild().block().getMemberById(event.getUserId()).block();
            if (mem.getRoles().map(Role::getName).collect(Collectors.toList()).block().contains("playing")) {
                Role r = Util.getRoleByName("playing", event.getGuild().block());
                mem.removeRole(r.getId(), "unsigned up from " + e.getName()).block();
            }
        }
    }
}
