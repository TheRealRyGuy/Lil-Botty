package me.ryguy.ctfbot.types;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.Role;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.rest.util.Image;
import lombok.Getter;
import lombok.Setter;
import me.ryguy.discordapi.DiscordBot;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter @Setter
public class Event {

    public static List<Event> events = new ArrayList<>();

    private String name;
    private String description;
    private List<Long> tagRoles;
    private Long announceChannel;
    private Long listChannel;
    private String signUpEmoji;
    private String rejectEmoji;
    private List<User> playing;
    private List<User> notPlaying;
    private Role giveRole;
    private Message announcementMessage;
    private Message listMessage;
    private Guild guild;

    public Event() {
        this.name = null;
        this.description = null;
        this.tagRoles = new ArrayList<>();
        this.announceChannel = null;
        this.listChannel = null;
        this.signUpEmoji = null;
        this.rejectEmoji = null;
        this.playing = new ArrayList<>();
        this.notPlaying = new ArrayList<>();
        this.giveRole = null;
        this.announcementMessage = null;
        this.listMessage = null;
    }

    public void addPlayer(User user) {
        this.getPlaying().add(user);
        this.handleReaction();
    }
    public void removePlayer(User user) {
        this.getPlaying().remove(user);
        this.handleReaction();
    }
    public void addNonPlayer(User user) {
        this.getNotPlaying().add(user);
        this.handleReaction();
    }
    public void removeNonPlayer(User user) {
        this.getNotPlaying().remove(user);
        this.handleReaction();
    }

    public void init() {
        String desc = this.getDescription();
        if(this.signUpEmoji != null)
            desc += "\n To Sign up: React with " + signUpEmoji;
        if(this.rejectEmoji != null)
            desc += "\n If you can't play, react with " + rejectEmoji;

        String finalDesc = desc;
        this.announcementMessage = ((MessageChannel) DiscordBot.getBot().getGateway().getChannelById(Snowflake.of(this.announceChannel)).block())
                .createMessage(m -> {
                    if(!this.getTagRoles().isEmpty()) {
                        m.setContent(this.getTagRoles().parallelStream().map(l -> guild.getRoleById(Snowflake.of(l)).block().getMention()).collect(Collectors.toList()).toString());
                    }
                    m.setEmbed(e -> {
                        e.setTitle(this.getName());
                        e.setDescription(finalDesc);
                        if(guild.getIconUrl(Image.Format.UNKNOWN).isPresent()) {
                            e.setThumbnail(guild.getIconUrl(Image.Format.UNKNOWN).get());
                        }
                    });
                }).block();
        this.listMessage = ((MessageChannel) DiscordBot.getBot().getGateway().getChannelById(Snowflake.of(this.listChannel)).block())
                .createMessage(":wc:").block();
        this.announcementMessage.addReaction(ReactionEmoji.unicode(this.signUpEmoji)).block();
        this.announcementMessage.addReaction(ReactionEmoji.unicode(this.rejectEmoji)).block();
        this.handleReaction();
        events.add(this);
    }
    public void handleReaction() {
        System.out.println("Calling handleReaction");

        listMessage.edit(m -> {
           m.setContent("");
           m.setEmbed(e -> {
               e.setTitle(this.getName());
               e.setDescription(this.getDescription());
               e.addField(this.signUpEmoji + " Players: " + this.getPlaying().size(), buildPlayerList(this.getPlaying()).isEmpty() ? "no one :c :sob:" : buildPlayerList(this.getPlaying()), false);
               e.addField(this.rejectEmoji + " Can't Play: " + this.getNotPlaying().size(), buildPlayerList(this.getNotPlaying()).isEmpty() ? "NO ONE :tada:" : buildPlayerList(this.getNotPlaying()), false);
               e.setFooter("love and waffles!", null);
           });
        }).block();
    }
    private String buildPlayerList(List<User> users) {
        synchronized(users) {
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < users.size(); i++) {
                sb.append((i + 1) + ": " + users.get(i).getMention() + "\n");
            }
            return sb.toString();
        }
    }
    @Override
    public String toString() {
        return "**Name:** " + this.name + "\n" +
                "**Description:** " + this.description + "\n" +
                "**Role for players:** " + (this.getGiveRole() != null ? this.getGiveRole().getName() : "None") + "\n" +
                "**Announcement channel:** " + DiscordBot.getBot().getGateway().getChannelById(Snowflake.of(this.announceChannel)).block().getMention() + "\n" +
                "**Participants list channel:** " + DiscordBot.getBot().getGateway().getChannelById(Snowflake.of(this.listChannel)).block().getMention() + "\n" +
                "**Sign up emoji:** " + this.signUpEmoji + "\n" +
                "**Reject emoji:** " + this.rejectEmoji;
    }
    public static Event getEvent(Message announcementMessage) {
        for (Event e : events) {
            if (e.getAnnouncementMessage().getId().asString().equalsIgnoreCase(announcementMessage.getId().asString()))
                return e;
        }
        return null;
    }

}
