package me.ryguy.ctfbot.types;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.TimeUnit;

@AllArgsConstructor @Getter @Setter
public class Strike {

    private long timestamp;
    private long striked;
    private long strikedBy;
    private Tier tier;
    private String reason;

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
