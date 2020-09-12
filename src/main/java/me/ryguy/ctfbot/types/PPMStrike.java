package me.ryguy.ctfbot.types;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.rest.util.Color;
import lombok.Getter;
import lombok.Setter;
import me.ryguy.ctfbot.CTFDiscordBot;
import me.ryguy.discordapi.DiscordBot;

import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Getter @Setter
public class PPMStrike {

    private long timestamp;
    private long striked;
    private long strikedBy;
    private Tier tier;
    private String reason;
    private int id;

    public PPMStrike(Tier tier, String reason, long timestamp, long striked, long strikedBy) {
        this.tier = tier;
        this.reason = reason;
        this.timestamp = timestamp;
        this.striked = striked;
        this.strikedBy = strikedBy;
        this.id = CTFDiscordBot.data.strikes.size();

        Reminder r = new Reminder(tier);
        r.schedule((MessageChannel) DiscordBot.getBot().getGateway().getChannelById(Snowflake.of(313870688727597058L)).block());
        CTFDiscordBot.data.strikes.add(this);
        try {
            CTFDiscordBot.data.save(CTFDiscordBot.DATA_FILE);
        }catch(IOException e) {
            e.printStackTrace();
        }
    }

    protected class Reminder extends me.ryguy.ctfbot.types.Reminder {
        public Reminder(Tier tier) {
            super(striked, (MessageChannel) DiscordBot.getBot().getGateway().getChannelById(Snowflake.of(313870688727597058L)).block(), (System.currentTimeMillis() + tier.getDuration()), ch -> {
                ch.createEmbed(em -> {
                    em.setTitle(":zap: Strike expired");
                    em.setColor(Color.TAHITI_GOLD);
                    em.setDescription(tier.getEmoji() + " <@" + striked + ">, striked by <@" + strikedBy + ">, " + "\n" +
                            "Reason: " + reason);
                    em.setTimestamp(Instant.now());
                }).block();
            });
        }
    }
    public enum Tier {
        FIRST(2),
        SECOND(5),
        THIRD(14);

        int length;

        Tier(int length) {
            this.length = length;
        }

        public long getDuration() {
            return TimeUnit.DAYS.toMillis(length);
        }

        public String getEmoji() {
            String emoji = "";
            switch (this) {
                case FIRST:
                    emoji = ":one:";
                    break;
                case SECOND:
                    emoji = ":two:";
                    break;
                case THIRD:
                    emoji = ":three:";
                    break;
            }
            return emoji;
        }

        public Tier getNextLevel() {
            switch (this) {
                case FIRST:
                    return SECOND;
                case SECOND:
                    return THIRD;
                case THIRD:
                    return FIRST;
            }
            return FIRST;
        }
    }
}
