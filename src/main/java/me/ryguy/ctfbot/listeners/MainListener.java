package me.ryguy.ctfbot.listeners;

import discord4j.core.event.domain.lifecycle.GatewayLifecycleEvent;
import me.ryguy.ctfbot.util.Util;
import me.ryguy.discordapi.listeners.DiscordEvent;
import me.ryguy.discordapi.listeners.Listener;

public class MainListener implements Listener {
    @DiscordEvent
    public void onConnect(GatewayLifecycleEvent e) {
        System.out.println(e.getClass().getName() + " called!");
        Util.messageMe(e.getClass().getName() + " called!");
    }

}
