package me.ryguy.ctfbot.cmds.ctf;

import com.google.gson.reflect.TypeToken;
import discord4j.core.object.entity.Message;
import discord4j.rest.util.Color;
import me.ryguy.ctfbot.CTFDiscordBot;
import me.ryguy.ctfbot.types.TypeSerializer;
import me.ryguy.ctfbot.util.Util;
import me.ryguy.discordapi.command.Command;
import reactor.core.publisher.Mono;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;
import java.util.stream.Collectors;

public class FindMapCommand extends Command {
    public FindMapCommand() {
        super("findmap", "mapfind");
    }

    @Override
    public Mono<Void> execute(Message message, String s, String[] args) {
        List<Map> maps;
        if (args.length == 0) {
            message.getChannel().block().createMessage(m -> {
                m.setEmbed(e -> {
                    e.setColor(Color.RED);
                    e.setDescription(":x: You need to have some arugments !!");
                });
            }).block();
            return null;
        }
        try {
            BufferedReader br = new BufferedReader(new FileReader(CTFDiscordBot.MAP_FILE));
            maps = CTFDiscordBot.gson().fromJson(br, new TypeToken<List<Map>>() {}.getType());
            br.close();
        } catch (Exception ex) {
            message.getChannel().block().createMessage(m -> {
                m.setEmbed(e -> {
                    e.setColor(Color.RED);
                    e.setDescription(":x: Error tryna do something, why'd you break me :(");
                });
            }).block();
            ex.printStackTrace();
            Util.sendErrorMessage(ex, message);
            return null;
        }

        String request = String.join(" ", args);
        if (request.matches("([0-9]*)")) {
            List<Map> res = maps.parallelStream().filter(m -> String.valueOf(m.map_id).contains(request)).collect(Collectors.toList());
            if (!res.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (Map map : res) {
                    sb.append(map.map_id + " - " + map.name + "\n");
                }
                message.getChannel().block().createMessage(m -> {
                    m.setEmbed(e -> {
                        e.setColor(Color.GREEN);
                        e.setTitle("Found " + res.size() + " maps!");
                        e.setDescription(sb.toString());
                    });
                }).block();
            } else {
                message.getChannel().block().createMessage(m -> {
                    m.setEmbed(e -> {
                        e.setColor(Color.RED);
                        e.setDescription(":x: There is no map with this id string :(((");
                    });
                }).block();
            }
        } else {
            String query = String.join(" ", args);
            List<Map> res = maps.parallelStream().filter(m -> m.name.toLowerCase().contains(query.toLowerCase())).collect(Collectors.toList());
            if (!res.isEmpty()) {
                try {
                    StringBuilder sb = new StringBuilder();
                    for (Map map : res) {
                        sb.append(map.map_id + " - " + map.name + "\n");
                    }
                    message.getChannel().block().createMessage(m -> {
                        m.setEmbed(e -> {
                            e.setColor(Color.GREEN);
                            e.setTitle("Found " + res.size() + " maps!");
                            e.setDescription(sb.toString());
                        });
                    }).block();
                } catch (Exception ex) {
                    message.getChannel().block().createMessage(m -> {
                        m.setEmbed(e -> {
                            e.setColor(Color.RED);
                            e.setDescription(":x: Too many results! :c");
                        });
                    }).block();
                }
            } else {
                message.getChannel().block().createMessage(m -> {
                    m.setEmbed(e -> {
                        e.setColor(Color.RED);
                        e.setDescription(":x: There is no map that matches this name :c");
                    });
                }).block();
            }
        }
        return null;
    }

    class Map {
        int map_id;
        String name;

    }
}
