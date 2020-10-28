package me.ryguy.ctfbot.modules.meetings;

import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.event.domain.message.ReactionRemoveEvent;
import me.ryguy.ctfbot.CTFDiscordBot;
import me.ryguy.ctfbot.module.ModuleListener;
import me.ryguy.ctfbot.module.Modules;
import me.ryguy.discordapi.listeners.DiscordEvent;
import me.ryguy.discordapi.listeners.Listener;

@ModuleListener(module = Modules.MEETINGS)
public class MeetingListeners implements Listener {
    @DiscordEvent
    public void onReactAdd(ReactionAddEvent event) {
        if (Meeting.getMeeting(event.getMessage().block()) == null) {
            return;
        }
        if (event.getUser().block().isBot()) {
            return;
        }
        if (!event.getEmoji().asUnicodeEmoji().isPresent()) {
            return;
        }

        Meeting e = Meeting.getMeeting(event.getMessage().block());
        boolean shouldHandle = false;
        if (event.getEmoji().asUnicodeEmoji().get().getRaw().equalsIgnoreCase("✅")) {
            e.getTimeEntry(event.getMessage().block()).getSignups().add(event.getUser().block());
            shouldHandle = true;
        }
        if (shouldHandle) {
            e.handleReaction();
        }
        CTFDiscordBot.save();
    }

    @DiscordEvent
    public void onReactRemove(ReactionRemoveEvent event) {
        if (Meeting.getMeeting(event.getMessage().block()) == null) {
            return;
        }
        if (event.getUser().block().isBot()) {
            return;
        }
        if (!event.getEmoji().asUnicodeEmoji().isPresent()) {
            return;
        }

        Meeting e = Meeting.getMeeting(event.getMessage().block());
        boolean shouldHandle = false;
        if (event.getEmoji().asUnicodeEmoji().get().getRaw().equalsIgnoreCase("✅")) {
            e.getTimeEntry(event.getMessage().block()).getSignups().remove(event.getUser().block());
            shouldHandle = true;
        }
        if (shouldHandle) {
            e.handleReaction();
        }
        CTFDiscordBot.save();
    }
}
