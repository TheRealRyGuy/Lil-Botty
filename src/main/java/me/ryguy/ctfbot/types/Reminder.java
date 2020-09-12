package me.ryguy.ctfbot.types;

import discord4j.core.object.entity.channel.MessageChannel;
import lombok.Getter;
import me.ryguy.ctfbot.CTFDiscordBot;

import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

@Getter
public class Reminder {

    private long owner;
    private MessageChannel channel;
    private long timestamp;
    private Consumer<MessageChannel> toSend;
    private boolean sent;

    public Reminder(long owner, MessageChannel channel, long timestamp, Consumer<MessageChannel> consumer) {
        this.owner = owner;
        this.channel = channel;
        this.timestamp = timestamp;
        this.toSend = consumer;

        this.schedule(channel);
        CTFDiscordBot.data.reminders.add(this);
        try {
            CTFDiscordBot.data.save(CTFDiscordBot.DATA_FILE);
        }catch(IOException e) {
            e.printStackTrace();
        }
    }
    public void schedule(MessageChannel ch) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                toSend.accept(ch);
                sent = false;
            }
        }, new Date(timestamp));
    }
}
