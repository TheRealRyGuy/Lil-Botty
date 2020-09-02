package me.ryguy.ctfbot.temp;

import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.event.domain.message.ReactionRemoveEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Role;
import me.ryguy.ctfbot.util.Util;
import me.ryguy.discordapi.listeners.DiscordEvent;
import me.ryguy.discordapi.listeners.Listener;

import java.util.stream.Collectors;

public class BasicEventReactionsListener implements Listener {
    @DiscordEvent
    public void onReactAdd(ReactionAddEvent event) {
        if (OverlyBasicEvent.getEvent(event.getMessage().block()) == null) return;
        if (event.getUser().block().isBot()) return;
        OverlyBasicEvent e = OverlyBasicEvent.getEvent(event.getMessage().block());
        if (event.getEmoji().toString().equalsIgnoreCase(e.getEmoji().toString()))
            e.addPlayer(event.getUser().block());

        if (event.getMember().isPresent()) {
            if (Util.getRoleByName("playing", event.getGuild().block()) == null) return;
            event.getMember().get().addRole(Util.getRoleByName("playing", event.getGuild().block()).getId(), "signed up to " + e.getName()).block();
        }
    }

    @DiscordEvent
    public void onReactRemove(ReactionRemoveEvent event) {
        if (OverlyBasicEvent.getEvent(event.getMessage().block()) == null) return;
        OverlyBasicEvent e = OverlyBasicEvent.getEvent(event.getMessage().block());
        if (event.getEmoji().toString().equalsIgnoreCase(e.getEmoji().toString()))
            e.removePlayer(event.getUser().block());

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
