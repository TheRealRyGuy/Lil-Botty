package me.ryguy.ctfbot.listeners;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import discord4j.core.event.domain.guild.GuildDeleteEvent;
import discord4j.core.event.domain.guild.MemberLeaveEvent;
import discord4j.core.event.domain.message.MessageDeleteEvent;
import me.ryguy.ctfbot.CTFDiscordBot;
import me.ryguy.ctfbot.modules.events.Event;
import me.ryguy.ctfbot.modules.poll.Poll;
import me.ryguy.discordapi.listeners.DiscordEvent;
import me.ryguy.discordapi.listeners.Listener;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataCleaner implements Listener {

    //TODO: actually do this
    private static final ExecutorService threadPool = Executors.newCachedThreadPool(new ThreadFactoryBuilder().setNameFormat("DataCleaner").build());
    //Listeners
    @DiscordEvent
    public void onMessageDelete(MessageDeleteEvent e) {

    }
    @DiscordEvent
    public void onGuildDelete(GuildDeleteEvent e) {

    }
    @DiscordEvent
    public void onMemberLeave(MemberLeaveEvent e) {

    }
    //Cleaners
    private boolean cleanEvents() {
        for(Event ev : CTFDiscordBot.data.events) {
            if (ev.getAnnouncementMessage() == null || ev.getListMessage() == null) {
                CTFDiscordBot.data.events.remove(ev);
                return true;
            }
        }
        return false;
    }
    private boolean cleanPolls() {
        for(Poll p : CTFDiscordBot.data.polls) {
            if (p.getMessage() == null) {
                CTFDiscordBot.data.polls.remove(p);
                return true;
            }
        }
        return false;
    }
}
