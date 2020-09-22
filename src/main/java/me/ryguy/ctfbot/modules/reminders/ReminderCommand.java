package me.ryguy.ctfbot.modules.reminders;

import discord4j.core.object.entity.Message;
import discord4j.rest.util.Color;
import me.ryguy.ctfbot.util.DelayedMessage;
import me.ryguy.ctfbot.util.Util;
import me.ryguy.discordapi.command.Command;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ReminderCommand extends Command {
    public ReminderCommand() {
        super("remind", "reminder", "remindme");
    }

    public static Map<Character, TimeUnit> timeUnits = new HashMap<>();
    static {
        timeUnits.put('s', TimeUnit.SECONDS);
        timeUnits.put('m', TimeUnit.MINUTES);
        timeUnits.put('h', TimeUnit.HOURS);
        timeUnits.put('d', TimeUnit.DAYS);
    }

    @Override
    public Mono<Void> execute(Message message, String s, String[] args) {
        if(args.length <= 1) {
            message.getChannel().block().createEmbed(em -> {
                em.setColor(Color.RED);
                em.setTitle(":x: Invalid Usage!");
                em.setDescription("Invalid argument length! Proper Usage Examples: \n`!remind 1h I need to do laundry`\n`!remind 30d Reset Discord invites`");
            }).block();
            return null;
        }
        if(!Util.isInteger(args[0].substring(0, args[0].length() - 1))) {
            message.getChannel().block().createEmbed(em -> {
                em.setColor(Color.RED);
                em.setTitle(":x: Invalid Usage!");
                em.setDescription("You need to use an integer in your time declaration! Usage Examples: \n`!remind 1h I need to do laundry`\n`!remind 30d Reset Discord invites`");
            }).block();
            return null;
        }
        Character unit = args[0].charAt(args[0].length() - 1);
        TimeUnit timeUnit = null;
        Integer length = Integer.valueOf(args[0].substring(0, args[0].length() - 1));
        if(unit.equals('w')) {
            length = length * 7;
            timeUnit = TimeUnit.DAYS;
        }else if(timeUnits.containsKey(unit)) {
            timeUnit = timeUnits.get(unit);
        }else {
            message.getChannel().block().createEmbed(em -> {
                em.setColor(Color.RED);
                em.setTitle(":x: Invalid Usage!");
                em.setDescription("Invalid Time Unit! Usage Examples: \n`!remind 1h I need to do laundry`\n`!remind 30d Reset Discord invites`");
            }).block();
            return null;
        }
        Reminder reminder = new Reminder(message.getAuthor().get().getId().asLong(), message.getAuthor().get().getPrivateChannel().block(),
                System.currentTimeMillis() + timeUnit.toMillis(length), new DelayedMessage(message.getAuthor().get().getPrivateChannel().block(),
                ":zap: Reminder!!!!!",
                Util.connectArray(args, 1), Color.TAHITI_GOLD));
        message.getChannel().block().createEmbed(em -> {
            em.setColor(Color.TAHITI_GOLD);
            em.setTitle(":white_check_mark: Success!");
            em.setDescription("Sending you this reminder at " + new Date(reminder.getTimestamp()).toString());
            em.setTimestamp(Instant.now());
        }).block();
        return null;
    }
}
