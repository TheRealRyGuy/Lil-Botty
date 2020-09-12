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
import java.util.List;

@Getter @Setter
public class Poll {

    private String name;
    private String description;
    private Long channelToPost;
    private List<Option> options;
    private boolean showVotes;
    private Message message;

    public Poll() {
        name = null;
        description = null;
        options = new ArrayList<>();
        channelToPost = null;
    }

    public void init() {
        this.message = ((MessageChannel) DiscordBot.getBot().getGateway().getChannelById(Snowflake.of(this.channelToPost)).block())
                .createEmbed(e -> {
                    e.setTitle(this.name + " (ID: " + CTFDiscordBot.data.polls.size() + ")");
                    e.setDescription(this.description);
                    e.setFooter("React with the corresponding emoji to vote for an option!", null);
                    e.setColor(Color.TAHITI_GOLD);
                    for(Option o : this.options) {
                        e.addField(o.getEmoji() + " - " + o.getDescription(), buildPlayerList(o), false);
                    }
                }).block();
        for(Option o : this.options) {
            this.message.addReaction(ReactionEmoji.unicode(o.getEmoji())).block();
        }
        CTFDiscordBot.data.polls.add(this);
        try {
            CTFDiscordBot.data.save(CTFDiscordBot.DATA_FILE);
        }catch(IOException e) {
            e.printStackTrace();
        }
    }
    public void handleReaction() {
        this.message.edit(m -> {
            m.setEmbed(e -> {
                e.setTitle(this.name);
                e.setDescription(this.description);
                e.setFooter("React with the corresponding emoji to vote for an option!", null);
                e.setColor(Color.TAHITI_GOLD);
                for(Option o : this.options) {
                    e.addField(o.getEmoji() + " - " + o.getDescription(), buildPlayerList(o), false);
                }
            });
        }).block();
    }
    //did this debug because i did something stupid
    //TODO: Simplify in the future
    public String buildPlayerList(Option o) {
        if(this.isShowVotes()) {
            if(o.getPlayers().isEmpty()) {
                return "No one";
            }else {
                return Util.buildPlayerList(o.getPlayers());
            }
        }else {
            return "Votes: " + o.getPlayers().size();
        }
    }

    @Override
    public String toString() {
        String ret = "**Name: **" + this.name + "\n" +
                "**Description: **" +  this.description + "\n" +
                "**Channel: ** <#" + this.getChannelToPost() + ">\n" +
                "**Options: ** \n";
        for(Option o : this.options) {
            ret += o.getEmoji() + " - " + o.getDescription() + "\n";
        }
        return ret;
    }
    public static Poll getPoll(Message announcementMessage) {
        for (Poll e : CTFDiscordBot.data.polls) {
            if (e.getMessage().getId().asString().equalsIgnoreCase(announcementMessage.getId().asString()))
                return e;
        }
        return null;
    }

    @Getter @Setter
    public static class Option {

        public Option() {
            emoji = null;
            description = null;
            players = new ArrayList<>();
        }

        String emoji;
        String description;
        List<User> players;

        public void addPlayer(User u) {
            players.add(u);
        }
        public void removePlayer(User u) {
            players.remove(u);
        }
    }
}
