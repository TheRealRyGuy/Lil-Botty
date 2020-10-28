package me.ryguy.ctfbot.cmds;

import discord4j.core.object.entity.Message;
import me.ryguy.ctfbot.CTFDiscordBot;
import me.ryguy.discordapi.command.Command;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicInteger;

public class CleanDataCommand extends Command {
    public CleanDataCommand() {
        super("cleandata");
    }

    @Override
    public Mono<Void> execute(Message message, String alias, String[] args) {
        AtomicInteger strikeReminders = new AtomicInteger();
        AtomicInteger reminders = new AtomicInteger();
        if (CTFDiscordBot.data.strikeReminders != null) {
            CTFDiscordBot.data.strikeReminders.forEach(reminder -> {
                if (reminder.isSent()) {
                    CTFDiscordBot.data.strikeReminders.remove(reminder);
                    strikeReminders.getAndIncrement();
                }
            });
        }
        message.getChannel().block().createMessage("Cleaned " + strikeReminders.get() + " strike reminders");
        if (CTFDiscordBot.data.reminders != null) {
            CTFDiscordBot.data.reminders.forEach(reminder -> {
                if (reminder.isSent()) {
                    CTFDiscordBot.data.reminders.remove(reminder);
                    reminders.getAndIncrement();
                }
            });
        }
        message.getChannel().block().createMessage("Cleaned " + reminders.get() + " reminders");
        return Mono.empty();
    }

    @Override
    public boolean canExecute(Message e, boolean asdf) {
        if (e.getAuthor().isPresent()) {
            return e.getAuthor().get().getId().asLong() == CTFDiscordBot.BOT_OWNER;
        }
        return false;
    }
}
