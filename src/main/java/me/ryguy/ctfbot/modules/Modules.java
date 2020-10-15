package me.ryguy.ctfbot.modules;

import me.ryguy.discordapi.command.Command;
import me.ryguy.discordapi.listeners.Listener;

public enum Modules {
    CTF(Module.builder().name("CTF Module").description("Module to cover the CTF Community Discord!").build()),
    EVENTS(Module.builder().name("Event Module").description("Module to cover the event system!").build()),
    GAMES(Module.builder().name("Games Module").description("Module to cover some fun chat games!").build()),
    MEETINGS(Module.builder().name("Meetings Module").description("Module to cover the meeting system!").build()),
    POLLS(Module.builder().name("Polls Module").description("Module to cover the polls system!").build()),
    QUOTES(Module.builder().name("Quotes Module").description("Module to allow users to quote messages!").build()),
    REMINDERS(Module.builder().name("CTF Module").description("Module to cover the reminder system!").build()),
    ROLES(Module.builder().name("Roles Module").description("Module to cover role management!").build());

    Module module;

    Modules(Module module) {
        this.module = module;
    }
    public void addCommand(Command command) {
        if(!this.module.getCommands().contains(command))
            this.module.getCommands().add(command);
    }
    public void removeCommand(Command command) {
        if(this.module.getCommands().contains(command))
            this.module.getCommands().remove(command);
    }
    public void addListener(Listener listener) {
        if(!this.module.getListeners().contains(listener))
            this.module.getListeners().add(listener);
    }
    public void removeListener(Listener listener) {
        if(this.module.getListeners().contains(listener))
            this.module.getListeners().remove(listener);
    }
}
