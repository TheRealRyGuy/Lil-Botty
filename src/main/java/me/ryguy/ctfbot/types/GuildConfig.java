package me.ryguy.ctfbot.types;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import me.ryguy.ctfbot.modules.Module;

import java.util.List;

@Getter @Setter @Builder
public class GuildConfig {
    long guildId;
    List<Module> modules;
    String prefix;
    Data data;
}
