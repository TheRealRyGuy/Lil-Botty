package me.ryguy.ctfbot.util;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Role;
import discord4j.core.object.entity.User;
import me.ryguy.ctfbot.CTFDiscordBot;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.URL;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

public class Util {
    public static boolean isPpmHost(Member mem) {
        return mem.getRoles().map(Role::getName).collect(Collectors.toList()).block().contains("PPM Host");
    }
    public static InputStream requestBrawlURL(String path) throws IOException {
        URL url = new URL(path);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.135 Safari/537.36");
        return conn.getInputStream();
    }
    public static String parseMention(String input) {
        String toCheck = input.replaceAll("\n", "").trim();
        if (toCheck.startsWith("<@") && toCheck.endsWith(">")) { //user or role mentions
            String mention = toCheck.substring(2, toCheck.length() - 1);
            if (mention.startsWith("!") || mention.startsWith("&")) {
                mention = mention.substring(1);
            }
            return mention;
        }else if(toCheck.startsWith("<#") && toCheck.endsWith(">")) { //channels
            return toCheck.substring(2, toCheck.length() - 1);
        }
        return null;
    }

    public static boolean isValidMention(String input, Guild guild) {
        if (parseMention(input) == null) return false;
        return guild.getRoleById(Snowflake.of(parseMention(input))).blockOptional().isPresent() || guild.getMemberById(Snowflake.of(parseMention(input))).blockOptional().isPresent();
    }

    public static List<Role> getRolesToRemove(Guild g) {
        return g.getRoles().filter(role -> CTFDiscordBot.ROLES_TO_REMOVE.contains(role.getName())).collect(Collectors.toList()).block();
    }

    public static Role getRoleByName(String name, Guild g) {
        for (Role r : g.getRoles().toIterable()) {
            if (r.getName().equalsIgnoreCase(name)) {
                return r;
            }
        }
        return null;
    }
    public static String buildPlayerList(List<User> users) {
        synchronized(users) {
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < users.size(); i++) {
                sb.append((i + 1) + ": " + users.get(i).getMention() + "\n");
            }
            return sb.toString();
        }
    }
    public static Boolean getBoolean(String s) {
        if(s.equalsIgnoreCase("true") || s.equalsIgnoreCase("yes"))
            return true;
        else if(s.equalsIgnoreCase("false") || s.equalsIgnoreCase("no"))
            return false;

        return null;
    }
}
