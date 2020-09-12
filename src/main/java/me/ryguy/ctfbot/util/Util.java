package me.ryguy.ctfbot.util;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.Event;
import discord4j.core.object.entity.*;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.entity.channel.PrivateChannel;
import discord4j.rest.util.Color;
import discord4j.rest.util.Image;
import me.ryguy.ctfbot.CTFDiscordBot;
import me.ryguy.discordapi.DiscordBot;
import me.ryguy.discordapi.listeners.Listener;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.URL;

import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Set;
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
    public static String matchStringFragment(Set<String> s, String toCheck) {
        for(String string : s) {
            if(string.toLowerCase().contains(toCheck.toLowerCase()))
                return string;
        }
        return null;
    }

    public static void sendErrorMessage(Exception ex, Message msg) {
        DiscordBot.getBot().getGateway().getUserById(Snowflake.of(CTFDiscordBot.BOT_OWNER)).block().getPrivateChannel().block().createEmbed(e -> {
            e.setColor(Color.RED);
            e.setTitle("Custom Error: " + ex.getClass().getName());
            if(msg.getChannel().block() instanceof PrivateChannel) {
                e.setFooter(msg.getAuthor().get().getTag(), null);
            }else if(msg.getChannel().block() instanceof MessageChannel){
                e.setFooter(msg.getChannel().block().getMention() + " - " + msg.getGuild().block().getName(), null);
            }else {
                e.setFooter("This shouldn't be the footer", null);
            }
            if(msg.getGuild().block().getIconUrl(Image.Format.JPEG).isPresent()) {
                e.setThumbnail(msg.getGuild().block().getIconUrl(Image.Format.JPEG).get());
            }
            if(msg.getAuthor().isPresent()) {
                if(msg.getAuthor().get().getAvatarUrl(Image.Format.JPEG).isPresent()) {
                    e.setAuthor(msg.getAuthor().get().getTag(), null, msg.getAuthor().get().getAvatarUrl(Image.Format.JPEG).get());
                }else {
                    e.setAuthor(msg.getAuthor().get().getTag(), null, null);
                }
            }
            e.addField("StackTrace", ":wc: ```" + ExceptionUtils.getStackTrace(ex.getCause()) + "``` ", false);
        }).block();
    }
    public static void sendErrorMessage(Exception ex, Listener listener, Event event) {
        DiscordBot.getBot().getGateway().getUserById(Snowflake.of(CTFDiscordBot.BOT_OWNER)).block().getPrivateChannel().block().createEmbed(e -> {
            e.setColor(Color.RED);
            e.setTitle("Custom Error: " + listener.getClass().getName());
            e.setAuthor(event.getClass().getName(), null, null);
            e.addField("StackTrace", ":wc: ```" + ExceptionUtils.getStackTrace(ex.getCause()) + "``` ", false);
        }).block();
    }

    public static void messageMe(String s) {
        DiscordBot.getBot().getGateway().getUserById(Snowflake.of(CTFDiscordBot.BOT_OWNER)).block().getPrivateChannel().block().createMessage(s).block();
    }
    public static enum Direction {
        UP, DOWN, LEFT, RIGHT;
    }
}
