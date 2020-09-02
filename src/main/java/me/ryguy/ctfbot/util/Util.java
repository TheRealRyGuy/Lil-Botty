package me.ryguy.ctfbot.util;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.*;
import discord4j.core.object.reaction.ReactionEmoji;
import me.ryguy.ctfbot.CTFDiscordBot;

import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;

public class Util {
    public static boolean isPpmHost(Member mem) {
        return mem.getRoles().map(Role::getName).collect(Collectors.toList()).block().contains("PPM Host");
    }
    public static String parseMention(String input) {
        String toCheck = input.replaceAll("\n", "").trim();
        if(toCheck.startsWith("<@") && toCheck.endsWith(">")) {
            String mention = toCheck.substring(2, toCheck.length() - 1);
            if(mention.startsWith("!") || mention.startsWith("&")) {
                mention = mention.substring(1);
            }
            return mention;
        }
        return null;
    }
    public static boolean isValidMention(String input, Guild guild) {
        if(parseMention(input) == null) return false;
        if(guild.getRoleById(Snowflake.of(parseMention(input))).blockOptional().isPresent() || guild.getMemberById(Snowflake.of(parseMention(input))).blockOptional().isPresent())
            return true;
        return false;
    }
    public static List<Role> getRolesToRemove(Guild g) {
        return g.getRoles().filter(role -> CTFDiscordBot.ROLES_TO_REMOVE.contains(role.getName())).collect(Collectors.toList()).block();
    }
    public static Role getRoleByName(String name, Guild g) {
        for(Role r : g.getRoles().toIterable()) {
            if(r.getName().equalsIgnoreCase(name)) {
                return r;
            }
        }
        return null;
    }
}
