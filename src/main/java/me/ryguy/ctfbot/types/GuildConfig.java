package me.ryguy.ctfbot.types;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import me.ryguy.ctfbot.module.Module;

import java.util.List;

@Getter
@Setter
@Data
public class GuildConfig {
    long guildId;
    List<Module> modules;
    String prefix;
}
