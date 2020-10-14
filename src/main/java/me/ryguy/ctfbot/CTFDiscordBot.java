package me.ryguy.ctfbot;

import com.google.gson.Gson;
import discord4j.discordjson.json.gateway.StatusUpdate;
import lombok.Getter;
import me.ryguy.ctfbot.modules.ctf.PPMStrike;
import me.ryguy.ctfbot.modules.reminders.Reminder;
import me.ryguy.ctfbot.types.Data;
import me.ryguy.ctfbot.types.TypeSerializer;
import me.ryguy.discordapi.DiscordBot;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class CTFDiscordBot {
    //List of many many things
    public static final long CTF_DISCORD_ID = 276518289289773067L;
    public static final long TEST_GUILD_ID = 433097833080684575L;
    public static final long BOT_OWNER = 151474463550996480L;

    public static final List<String> ROLES_TO_REMOVE = Arrays.asList("Red Team", "Blue Team", "playing");
    public static File MAP_FILE;
    public static File SHEETS_CREDENTIALS;
    public static File DATA_FILE;
    public static Data data;
    @Getter
    private static DiscordBot bot;

    static {
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            SHEETS_CREDENTIALS = new File(System.getProperty("user.dir") + "/src/main/resources/credentials.json");
            MAP_FILE = new File(System.getProperty("user.dir") + "/src/main/resources/maps.json"); //running it locally
            DATA_FILE = new File(System.getProperty("user.dir") + "/src/main/resources/data.json");
        } else {
            MAP_FILE = new File("/Shared/maps.json"); //running on vps
            SHEETS_CREDENTIALS = new File("/Shared/credentials.json");
            DATA_FILE = new File("/Shared/data.json");
        }
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("You need to include a token in your arguments!");
            System.exit(0);
            return;
        }
        bot = new DiscordBot(args[0], "!");
        bot.loginBot();
        bot.getGateway().updatePresence(StatusUpdate.builder().status("Love and Waffles!").afk(false).build());



        /*bot.setCommandErrorHandler((ex, cmd) -> {
            ex.printStackTrace();
            bot.getGateway().getUserById(Snowflake.of(BOT_OWNER)).block().getPrivateChannel().block().createEmbed(e -> {
                e.setColor(Color.RED);
                e.setTitle("Error using command " + cmd.getName() + " - " + ex.getClass().getName() + "!");
                e.setDescription(":wc: ```" + ExceptionUtils.getStackTrace(ex.getCause()) + "``` ");
            }).block();
        });
        bot.setEventErrorHandler((ex, event) -> {
            ex.printStackTrace();
            bot.getGateway().getUserById(Snowflake.of(BOT_OWNER)).block().getPrivateChannel().block().createEmbed(e -> {
                e.setColor(Color.RED);
                e.setTitle("Error running event  " + event.getClass().getName() + " - " + ex.getClass().getName() + "!");
                e.setDescription(":wc: ```" + ExceptionUtils.getStackTrace(ex.getCause()) + "``` ");
            }).block();
        });*/

        Startup.INSTANCE.registerCommands();
        Startup.INSTANCE.registerListeners();

        try {
            data = Data.load(DATA_FILE);
            if (data.reminders != null) {
                Reminder.initialize();
            }
            if (data.strikeReminders != null) {
                PPMStrike.initializeReminders();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(0);
        }

        bot.endStartup();
    }

    public static Gson gson() {
        return TypeSerializer.INSTANCE.getGson();
    }

    public static void save() {
        try {
            data.save(CTFDiscordBot.DATA_FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
