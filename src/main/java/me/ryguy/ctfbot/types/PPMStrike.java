package me.ryguy.ctfbot.types;

import com.google.gson.annotations.SerializedName;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.rest.util.Color;
import lombok.Getter;
import lombok.Setter;
import me.ryguy.ctfbot.CTFDiscordBot;
import me.ryguy.ctfbot.util.DelayedMessage;
import me.ryguy.ctfbot.util.DiscordUtil;
import me.ryguy.discordapi.DiscordBot;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Getter
@Setter
public class PPMStrike {
    public static final MessageChannel PPM_STRIKE_CHANNEL = (MessageChannel) DiscordBot.getBot().getGateway().getChannelById(Snowflake.of(462631709804855296L)).block();

    private long timestamp;
    private long striked;
    private long strikedBy;
    private Tier tier;
    private String reason;
    private int id;
    private long expiration;

    public PPMStrike(Tier tier, String reason, long striked, long strikedBy) {
        this.tier = tier;
        this.reason = reason;
        this.timestamp = System.currentTimeMillis();
        this.expiration = timestamp + tier.getDuration();
        this.striked = striked;
        this.strikedBy = strikedBy;
        this.id = CTFDiscordBot.data.strikes.size();

        Reminder r = new Reminder();
        r.schedule((MessageChannel) DiscordBot.getBot().getGateway().getChannelById(Snowflake.of(313870688727597058L)).block());
    }
    public boolean isActive() {
        return System.currentTimeMillis() < this.expiration;
    }
    private class Reminder extends me.ryguy.ctfbot.types.Reminder {
        public Reminder() {
            super(striked, PPM_STRIKE_CHANNEL,
                    (expiration),
                    new DelayedMessage(PPM_STRIKE_CHANNEL,
                            ":zap: Strike expired",
                            tier.getEmoji() + " <@" + striked + ">, striked by <@" + strikedBy + ">, " + "\n" +
                                    "Reason: " + reason,
                            Color.TAHITI_GOLD
                    ));
        }
    }

    @Override
    public String toString() {
        return "(ID: " + this.id + ") " + this.getTier().getEmoji() + " " + DiscordUtil.getUserTag(this.getStriked()) + "was striked by " + DiscordUtil.getUserTag(this.getStrikedBy());
    }

    public enum Tier {
        @SerializedName("1") FIRST(2),
        @SerializedName("2") SECOND(5),
        @SerializedName("3") THIRD(14);

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
