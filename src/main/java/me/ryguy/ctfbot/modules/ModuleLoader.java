package me.ryguy.ctfbot.modules;

import me.ryguy.ctfbot.CTFDiscordBot;
import me.ryguy.discordapi.command.Command;
import me.ryguy.discordapi.command.CommandManager;
import me.ryguy.discordapi.listeners.EventHolder;
import me.ryguy.discordapi.listeners.EventManager;
import me.ryguy.discordapi.listeners.Listener;

import java.util.LinkedList;
import java.util.List;

public class ModuleLoader {
    public ModuleLoader() {
        for(Command cmd : CommandManager.getRegisteredCommands()) {
            ModuleCommand moduleCommand = cmd.getClass().getAnnotation(ModuleCommand.class);
            if(moduleCommand == null) continue;
            if(moduleCommand.module() == null) continue;
            CTFDiscordBot.logger.info("Attached command " + cmd.getName() + " to module " + moduleCommand.module().name());
            moduleCommand.module().addCommand(cmd);
        }
        List<Listener> listeners = new LinkedList<>();
        for(EventHolder eh : EventManager.getEvents())
            listeners.addAll(eh.getMethods().keySet());
        for(Listener listener : listeners) {
            ModuleListener moduleListener = listener.getClass().getAnnotation(ModuleListener.class);
            if(moduleListener == null) continue;
            if(moduleListener.module() == null) continue;
            CTFDiscordBot.logger.info("Attached listener " + listener.getClass().getSimpleName() + " to module " + moduleListener.module().name());
            moduleListener.module().addListener(listener);
        }

    }
}
