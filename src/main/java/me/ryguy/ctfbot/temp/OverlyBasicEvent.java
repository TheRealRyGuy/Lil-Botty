package me.ryguy.ctfbot.temp;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.rest.util.Color;
import me.ryguy.ctfbot.CTFDiscordBot;
import me.ryguy.discordapi.DiscordBot;

import java.util.ArrayList;
import java.util.List;

public class OverlyBasicEvent {

    public static List<OverlyBasicEvent> events = new ArrayList<>();

    private List<User> players;
    private ReactionEmoji toSignUp;
    private Message announcement;
    private Message signups;
    private String name;

    public OverlyBasicEvent(String name, ReactionEmoji emoji) {
        this.name = name;
        this.toSignUp = emoji;
        this.players = new ArrayList<>();
    }

    public static OverlyBasicEvent getEvent(Message announcementMessage) {
        for (OverlyBasicEvent e : events) {
            if (e.getMessage().getId().asString().equalsIgnoreCase(announcementMessage.getId().asString()))
                return e;
        }
        return null;
    }

    public void addPlayer(User player) {
        this.players.add(player);
        this.setupSignups();
    }

    public void removePlayer(User player) {
        this.players.remove(player);
        this.setupSignups();
    }

    public void init() {
        MessageChannel ppms = (MessageChannel) DiscordBot.getBot().getGateway().getGuildById(Snowflake.of(CTFDiscordBot.CTF_DISCORD_ID)).block().getChannelById(Snowflake.of(CTFDiscordBot.PPM_CHANNEL)).block();
        MessageChannel signups = (MessageChannel) DiscordBot.getBot().getGateway().getGuildById(Snowflake.of(CTFDiscordBot.CTF_DISCORD_ID)).block().getChannelById(Snowflake.of(CTFDiscordBot.SIGNUPS_CHANNEL)).block();
        Message ann = ppms.createMessage(name + "\n\nReact with :white_check_mark: to sign up!").block();
        announcement = ann;
        ann.addReaction(ReactionEmoji.unicode("✅")).block();
        this.signups = signups.createMessage(":wc:").block();
        events.add(this);
    }

    private String grabPlayerList() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < players.size(); i++) {
            sb.append((i + 1) + ": " + players.get(i).getMention() + "\n");
        }
        return sb.toString();
    }

    public void setupSignups() {
        signups.edit(e -> {
            e.setEmbed(em -> {
                em.setTitle(this.name);
                em.setDescription("I really need to add functionality to put information here");
                em.addField("Players", grabPlayerList().isEmpty() ? "no one :sob:" : grabPlayerList(), false);
                em.setColor(Color.TAHITI_GOLD);
            });
            e.setContent(":wc:");
        }).subscribe();
    }

    public ReactionEmoji getEmoji() {
        return this.toSignUp;
    }

    public void setEmoji(ReactionEmoji gi) {
        this.toSignUp = gi;
    }

    public Message getMessage() {
        return this.announcement;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String s) {
        this.name = s;
    }

}
