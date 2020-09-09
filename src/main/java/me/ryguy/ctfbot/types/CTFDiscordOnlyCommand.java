package me.ryguy.ctfbot.types;

import discord4j.core.event.domain.message.MessageCreateEvent;
import me.ryguy.ctfbot.CTFDiscordBot;
import me.ryguy.discordapi.command.Command;

public abstract class CTFDiscordOnlyCommand extends Command {
    public CTFDiscordOnlyCommand(String name) {
        super(name);
    }

    public CTFDiscordOnlyCommand(String name, String... aliases) {
        super(name, aliases);
    }

    @Override
    public boolean canExecute(MessageCreateEvent message) {
        if (!message.getGuildId().isPresent()) return false;
        return message.getGuildId().get().asLong() == CTFDiscordBot.CTF_DISCORD_ID || message.getGuildId().get().asLong() == CTFDiscordBot.TEST_GUILD_ID;
    }
}
