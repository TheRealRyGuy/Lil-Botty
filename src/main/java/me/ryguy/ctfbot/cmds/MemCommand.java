package me.ryguy.ctfbot.cmds;

import discord4j.core.object.entity.Message;
import discord4j.rest.util.Color;
import discord4j.rest.util.Image;
import me.ryguy.ctfbot.CTFDiscordBot;
import me.ryguy.ctfbot.types.Event;
import me.ryguy.ctfbot.types.Meeting;
import me.ryguy.ctfbot.types.Poll;
import me.ryguy.discordapi.command.Command;
import reactor.core.publisher.Mono;

public class MemCommand extends Command {
    public MemCommand() {
        super("mem");
    }
    @Override
    public Mono<Void> execute(Message message, String s, String[] strings) {
        message.getChannel().block().createEmbed(e -> {
            e.setColor(Color.TAHITI_GOLD);
            e.setTitle("Memory Information");
            e.setDescription("```java\n" + buildMemInfo() + "```");
            e.setFooter("Love and waffles <3", null);
            if(message.getAuthor().isPresent()) {
                if(message.getAuthor().get().getAvatarUrl(Image.Format.JPEG).isPresent()) {
                    e.setThumbnail(message.getAuthor().get().getAvatarUrl(Image.Format.JPEG).get());
                }
            }
        }).block();
        return null;
    }
    private String buildMemInfo() {
        String polls = String.valueOf(Poll.polls.size());
        String meetings = String.valueOf(Meeting.meetings.size());
        String events = String.valueOf(Event.events.size());
        String freeMem = Math.round(((Runtime.getRuntime().freeMemory() / 1024 / 1024) * 100) / 100) + " MB";
        String totalMem = Math.round(Runtime.getRuntime().maxMemory() / 1073741824) + " GB";
        return "Polls: " + polls + "\n" +
                "Meetings: " + meetings + "\n" +
                "Events: " + events + "\n" +
                "Mem: " + freeMem + " / " + totalMem;
    }
    @Override
    public boolean canExecute(Message e, boolean asdf) {
        if(e.getAuthor().isPresent()) {
            return e.getAuthor().get().getId().asLong() == CTFDiscordBot.BOT_OWNER;
        }
        return false;
    }
}
