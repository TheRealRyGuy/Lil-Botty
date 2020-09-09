package me.ryguy.ctfbot;

import com.google.gson.Gson;
import discord4j.common.util.Snowflake;
import discord4j.discordjson.json.gateway.StatusUpdate;
import discord4j.rest.util.Color;
import me.ryguy.discordapi.DiscordBot;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class CTFDiscordBot {
    //List of many many things
    public static final long CTF_DISCORD_ID = 276518289289773067L;
    public static final long TEST_GUILD_ID = 433097833080684575L;
    public static final long BOT_OWNER = 151474463550996480L;

    public static final Gson GSON = new Gson();
    public static final List<String> ROLES_TO_REMOVE = Arrays.asList("Red Team", "Blue Team", "playing");
    public static File MAP_FILE;
    static {
        if(SystemUtils.IS_OS_WINDOWS) {
            MAP_FILE = new File(System.getProperty("user.dir") + "/src/main/resources/maps.json"); //running it locally
        }else {
            MAP_FILE = new File("/Shared/maps.json"); //running on vps
        }
    }
    private static DiscordBot bot;

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("You need to include a token in your arguments!");
            System.exit(0);
            return;
        }
        bot = new DiscordBot(args[0], "!");
        bot.loginBot();
        bot.getGateway().updatePresence(StatusUpdate.builder().status("Love and Waffles!").afk(false).build());

        bot.setCommandErrorHandler((ex, cmd) -> {
            ex.printStackTrace();
            bot.getGateway().getUserById(Snowflake.of(BOT_OWNER)).block().getPrivateChannel().block().createEmbed(e -> {
                e.setColor(Color.RED);
                e.setTitle("Error using command " + cmd.getName() + "!");
                e.setDescription("Error: " + ex.getClass().getName());
                e.addField("StackTrace", ":wc: ```" + ExceptionUtils.getStackTrace(ex) + "``` ", false);
            }).block();
        });
        bot.setEventErrorHandler((ex, event) -> {
            ex.printStackTrace();
            bot.getGateway().getUserById(Snowflake.of(BOT_OWNER)).block().getPrivateChannel().block().createEmbed(e -> {
                e.setColor(Color.RED);
                e.setTitle("Error running event  " + event.getClass().getName() + "!");
                e.setDescription("Error: " + ex.getClass().getName());
                e.addField("StackTrace", ":wc: ```" + ExceptionUtils.getStackTrace(ex) + "``` ", false);
            }).block();
        });

        Startup.INSTANCE.registerCommands();
        Startup.INSTANCE.registerListeners();

        bot.endStartup();
    }
}
