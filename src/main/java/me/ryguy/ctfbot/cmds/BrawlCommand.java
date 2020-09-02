package me.ryguy.ctfbot.cmds;

import discord4j.core.object.entity.Message;
import discord4j.rest.util.Color;
import me.ryguy.ctfbot.util.WebUtil;
import me.ryguy.discordapi.command.Command;
import reactor.core.publisher.Mono;

import java.net.ConnectException;
import java.util.HashMap;
import java.util.Map;

public class BrawlCommand extends Command {
    public BrawlCommand() {
        super("brawl", "brool");
    }

    private static final Map<String, String> servers = new HashMap<>();
    private HashMap<String, String> result;

    static {
        servers.put("br_brawl_com", "Battle Royale");
        servers.put("ctfmatch_brawl_com", "Capture the Flag (Match)");
        servers.put("lobby_brawl_com", "Lobby");
        servers.put("mc_hg_com", "Hardcore Games");
        servers.put("mc_war_com", "MC-War");
        servers.put("mc_warz_com", "MC-WarZ");
        servers.put("mcctf_com", "Capture the Flag");
        servers.put("minecraftbuild_com", "Build");
        servers.put("minecraftparty_com", "Minecraft Party");
        servers.put("raid_mcpvp_com", "Raid");
        servers.put("test_brawl_com", "Test");
        servers.put("wildwest_brawl_com", "Wild West");
        servers.put("kit_brawl_com", "KitBrawl");
        servers.put("total", "Total");
    }

    @Override
    public Mono<Void> execute(Message message, String alias, String[] args) {
        try {
            String s = WebUtil.getJSONApi("https://www.brawl.com/data/playerCount.json");
                s = s.replaceAll("\"", "");
                s = s.replace("{", "");
                s = s.replace("}", "");
                result = new HashMap<>();
                for(String res : s.split(",")) {
                    if(servers.containsKey(res.split(":")[0])) {
                        result.put(servers.get(res.split(":")[0]), res.split(":")[1]);
                    }
                }
        }catch(Exception e) {
            if(e instanceof ConnectException) {
                message.getChannel().block().createMessage(msg -> {
                    msg.setEmbed(embed -> {
                        embed.setColor(Color.TAHITI_GOLD);
                        embed.setDescription(":x: Cannot access brawl.com");
                    });
                }).block();
                return null;
            }else {
                message.getChannel().block().createMessage(msg -> {
                    msg.setEmbed(embed -> {
                        embed.setColor(Color.TAHITI_GOLD);
                        embed.setDescription(":x: Error pulling brawl data!");
                    });
                }).block();
                e.printStackTrace();
                return null;
            }
        }
        if(result != null) {
            StringBuilder sb = new StringBuilder();
            for(Map.Entry<String,String> entry : result.entrySet()) {
                sb.append(String.format(":arrow_forward: **%s**: %s \n", entry.getKey(), entry.getValue()));
            }
            message.getChannel().block().createMessage(sb.toString()).block();
        }
        return null;
    }
}
