package me.ryguy.ctfbot.temp;

import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.event.domain.message.ReactionRemoveEvent;
import me.ryguy.discordapi.listeners.DiscordEvent;
import me.ryguy.discordapi.listeners.Listener;

public class BasicEventReactionsListener implements Listener {
    @DiscordEvent
    public void onReactAdd(ReactionAddEvent event) {
        if(OverlyBasicEvent.getEvent(event.getMessage().block()) == null) return;
        OverlyBasicEvent e = OverlyBasicEvent.getEvent(event.getMessage().block());
        if(event.getEmoji().toString().equalsIgnoreCase(e.getEmoji().toString()))
            e.addPlayer(event.getUser().block());
    }

    @DiscordEvent
    public void onReactRemove(ReactionRemoveEvent event) {
        if(OverlyBasicEvent.getEvent(event.getMessage().block()) == null) return;
        OverlyBasicEvent e = OverlyBasicEvent.getEvent(event.getMessage().block());
        if(event.getEmoji().toString().equalsIgnoreCase(e.getEmoji().toString()))
            e.removePlayer(event.getUser().block());
    }
}
