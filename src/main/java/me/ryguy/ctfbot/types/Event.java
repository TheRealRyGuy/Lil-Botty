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
import me.ryguy.ctfbot.util.Util;
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
    public String buildRoleTagString() {
        StringBuilder sb = new StringBuilder();
        for(Long l : this.getTagRoles()) {
            if(l == guild.getEveryoneRole().block().getId().asLong()) {
                sb.append("@everyone ");
            }else {
                sb.append(this.guild.getRoleById(Snowflake.of(l)).block().getMention() + " ");
            }
        }
        return sb.toString();
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
        String desc = this.getDescription() + "\n";
        if(this.signUpEmoji != null)
            desc += "\n React with " + signUpEmoji + " to play";
        if(this.rejectEmoji != null)
            desc += "\n React with " + rejectEmoji + " if you can't play";

        String finalDesc = desc;
        this.announcementMessage = ((MessageChannel) DiscordBot.getBot().getGateway().getChannelById(Snowflake.of(this.announceChannel)).block())
                .createMessage(m -> {
                    if(!this.getTagRoles().isEmpty()) {
                        m.setContent(this.buildRoleTagString());
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
        listMessage.edit(m -> {
           m.setContent("");
           m.setEmbed(e -> {
               e.setTitle(this.getName());
               e.setDescription(this.getDescription());
               e.addField(this.signUpEmoji + " Players: " + this.getPlaying().size(), Util.buildPlayerList(this.getPlaying()).isEmpty() ? "no one :c :sob:" : Util.buildPlayerList(this.getPlaying()), false);
               e.addField(this.rejectEmoji + " Can't Play: " + this.getNotPlaying().size(), Util.buildPlayerList(this.getNotPlaying()).isEmpty() ? "NO ONE :tada:" : Util.buildPlayerList(this.getNotPlaying()), false);
               e.setFooter("love and waffles!", null);
           });
        }).block();
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
