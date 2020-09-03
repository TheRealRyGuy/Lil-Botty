package me.ryguy.ctfbot.cmds

import discord4j.core.`object`.entity.Message
import me.ryguy.discordapi.command.Command
import reactor.core.publisher.Mono

/**
 * Syntax: !ctfstats [player] [class] [comp|casual]
 */
class CTFStatsCommand : Command("ctfstats") {
    companion object {
        val CTFClasses = listOf("Archer", "Assassin", "Chemist", "Dwarf", "Elf", "Engineer",
                "Fashionista", "Heavy", "Mage", "Medic", "Necro", "Ninja", "Pyro", "Scout", "Soldier"
        )
    }

    override fun execute(message: Message?, alias: String?, args: Array<out String>?): Mono<Void> {
        // TODO: retrieve from ctf stats repo
        return Mono.empty()
    }
}