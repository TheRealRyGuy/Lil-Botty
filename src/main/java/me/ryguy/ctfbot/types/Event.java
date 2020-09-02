package me.ryguy.ctfbot.types;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.Role;
import discord4j.core.object.entity.User;
import discord4j.core.object.reaction.ReactionEmoji;

import java.util.ArrayList;
import java.util.List;

public class Event {

    public static List<Event> events = new ArrayList<>();
    private String name;
    private String description;
    private Message announcementMessage;
    private Message signupMessage;
    private List<User> playing;
    private List<User> notPlaying;
    private Role giveRole;
    private ReactionEmoji signUpEmoji;
    private ReactionEmoji rejectEmoji;
    public Event() {
    }

    public void init() {

    }

    public void doMessage() {

    }

}
