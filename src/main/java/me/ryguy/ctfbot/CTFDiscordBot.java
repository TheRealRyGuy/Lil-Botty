package me.ryguy.ctfbot;

import com.google.gson.Gson;
import me.ryguy.ctfbot.cmds.*;
import me.ryguy.ctfbot.listeners.MemeChatListeners;
import me.ryguy.ctfbot.temp.BasicEventReactionsListener;
import me.ryguy.ctfbot.temp.BasicSignUpCommand;
import me.ryguy.discordapi.DiscordBot;
import org.apache.commons.lang3.SystemUtils;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class CTFDiscordBot {
    //List of many many things
    public static final long CTF_DISCORD_ID = 276518289289773067L;
    public static final long TEST_GUILD_ID = 433097833080684575L;
    public static final long PPM_CHANNEL = 309172630814982156L;
    public static final long SIGNUPS_CHANNEL = 483993420189138975L;

    public static final Gson GSON = new Gson();
    public static final List<String> ROLES_TO_REMOVE = Arrays.asList("Red Team", "Blue Team", "playing");
    public static File MAP_FILE;
    private static DiscordBot bot;

    static {
        if (SystemUtils.IS_OS_WINDOWS) {
            MAP_FILE = new File(System.getProperty("user.dir") + "/src/main/resources/maps.json");
        } else {
            MAP_FILE = new File("/Shared/maps.json");
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

        new BrawlCommand().register(); //DONE
        new SetRolesCommand().register(); //DONE
        new BasicSignUpCommand().register(); //DONE
        new RemoveRolesCommand().register(); //DONE
        new FindMapCommand().register(); // DONE
        new InviteCommand().register();

        new MemeChatListeners().register(); //DONE
        new BasicEventReactionsListener().register(); //DONE

        bot.endStartup();
    }
}
