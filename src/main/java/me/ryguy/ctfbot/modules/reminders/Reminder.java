package me.ryguy.ctfbot.modules.reminders;

import discord4j.core.object.entity.channel.MessageChannel;
import lombok.Getter;
import me.ryguy.ctfbot.CTFDiscordBot;
import me.ryguy.ctfbot.util.DelayedMessage;

import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

@Getter
public class Reminder {

    private final long owner;
    private final MessageChannel channel;
    private final long timestamp;
    private final DelayedMessage toSend;
    private boolean sent;

    public Reminder(long owner, MessageChannel channel, long timestamp, DelayedMessage message) {
        this.owner = owner;
        this.channel = channel;
        this.timestamp = timestamp;
        this.toSend = message;

        this.schedule(channel);
        CTFDiscordBot.data.reminders.add(this);
        try {
            CTFDiscordBot.data.save(CTFDiscordBot.DATA_FILE);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    public void schedule(MessageChannel ch) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                toSend.setChannel(ch);
                toSend.send();
                sent = true;
            }
        }, new Date(timestamp));
        try {
            CTFDiscordBot.data.save(CTFDiscordBot.DATA_FILE);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
