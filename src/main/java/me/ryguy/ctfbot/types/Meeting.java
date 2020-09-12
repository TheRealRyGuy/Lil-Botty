package me.ryguy.ctfbot.types;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.rest.util.Color;
import lombok.Getter;
import lombok.Setter;
import me.ryguy.ctfbot.CTFDiscordBot;
import me.ryguy.ctfbot.util.Util;
import me.ryguy.discordapi.DiscordBot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class Meeting {

    private Long channelToPost;
    private String name;
    private String desc;
    private Map<Message, TimeEntry> entries;
    private List<TimeEntry> times;
    private Message announcement;

    public Meeting() {
        this.channelToPost = null;
        this.name = null;
        this.desc = null;
        this.entries = new HashMap<>();
        this.times = new ArrayList<>();
        this.announcement = null;
    }

    public static Meeting getMeeting(Message toCheck) {
        for (Meeting m : CTFDiscordBot.data.meetings) {
            for (Map.Entry<Message, TimeEntry> entry : m.getEntries().entrySet()) {
                if (entry.getKey().getId().asString().equalsIgnoreCase(toCheck.getId().asString()))
                    return m;
            }
        }
        return null;
    }

    public void init() {
        this.announcement = ((MessageChannel) DiscordBot.getBot().getGateway().getChannelById(Snowflake.of(this.channelToPost)).block())
                .createEmbed(e -> {
                    e.setTitle(this.getName());
                    e.setDescription(this.getDesc());
                    for (TimeEntry t : this.getTimes()) {
                        e.addField(t.getTime(), t.getSignups().isEmpty() ? "No One :sob:" : Util.buildPlayerList(t.getSignups()), false);
                    }
                    e.setColor(Color.TAHITI_GOLD);
                }).block();
        for (TimeEntry t : this.getTimes()) {
            MessageChannel channel = (MessageChannel) DiscordBot.getBot().getGateway().getChannelById(Snowflake.of(this.channelToPost)).block();
            Message message = channel.createMessage(t.getTime()).block();
            message.addReaction(ReactionEmoji.unicode("✅")).block();
            this.entries.put(message, t);
        }
        CTFDiscordBot.data.meetings.add(this);
        try {
            CTFDiscordBot.data.save(CTFDiscordBot.DATA_FILE);
        }catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void handleReaction() {
        this.announcement.edit(m -> {
            m.setEmbed(e -> {
                e.setTitle(this.getName());
                e.setDescription(this.getDesc());
                e.setColor(Color.TAHITI_GOLD);
                for (TimeEntry t : this.getTimes()) {
                    e.addField(t.getTime(), t.getSignups().isEmpty() ? "No One :sob:" : Util.buildPlayerList(t.getSignups()), false);
                }
            });
        }).block();
    }

    @Override
    public String toString() {
        String ret = "**Name: **" + this.name + "\n" +
                "**Description: **" + this.desc + "\n" +
                "**Post in: ** <#" + this.channelToPost + "> \n" +
                "**Times: ** \n";
        for (TimeEntry entry : this.getTimes()) {
            ret += entry.getTime() + "\n";
        }
        return ret;
    }

    public TimeEntry getTimeEntry(Message m) {
        for (Map.Entry<Message, TimeEntry> entries : this.getEntries().entrySet()) {
            if (m.getId().asString().equalsIgnoreCase(entries.getKey().getId().asString())) {
                return entries.getValue();
            }
        }
        return null;
    }

    @Getter
    @Setter
    public static class TimeEntry {

        String time;
        List<User> signups;

        public TimeEntry() {
            this.time = null;
            this.signups = new ArrayList<>();
        }
    }
}
