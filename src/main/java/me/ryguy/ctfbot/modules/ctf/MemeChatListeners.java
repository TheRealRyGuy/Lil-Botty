package me.ryguy.ctfbot.modules.ctf;

import discord4j.core.event.domain.message.MessageCreateEvent;
import me.ryguy.ctfbot.module.ModuleListener;
import me.ryguy.ctfbot.module.Modules;
import me.ryguy.discordapi.listeners.DiscordEvent;
import me.ryguy.discordapi.listeners.Listener;

@ModuleListener(module = Modules.CTF)
public class MemeChatListeners implements Listener {
    @DiscordEvent
    public void onChat(MessageCreateEvent e) {
        if (!e.getMessage().getAuthor().isPresent()) return;
        if (e.getMessage().getAuthor().get().isBot()) return;
        if (e.getMessage().getContent().toLowerCase().contains("love and waffles"))
            e.getMessage().getChannel().block().createMessage("Love and Waffles!").block();
        else if (e.getMessage().getContent().toLowerCase().contains("hugs and pugs"))
            e.getMessage().getChannel().block().createMessage("Hugs and Pugs!").block();
    }
}
