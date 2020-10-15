package me.ryguy.ctfbot.modules;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import me.ryguy.discordapi.command.Command;
import me.ryguy.discordapi.listeners.Listener;

import java.util.List;

@Data
@Getter
@Setter
@Builder
public class Module {
    public String name;
    public String description;
    public List<Command> commands;
    public List<Listener> listeners;
}
