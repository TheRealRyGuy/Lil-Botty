package me.ryguy.ctfbot.cmds

import discord4j.core.`object`.entity.Message
import me.ryguy.ctfbot.replyWithSuccess
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

        const val GITHUB_LINK = "https://nineonefive.github.io/CTFTeamStats/leaderboards.html"
    }

    override fun execute(message: Message?, alias: String?, args: Array<out String>?): Mono<Void> {
        // TODO: actual display later
        message?.channel?.block()?.createEmbed {
            it.setTitle("CTF Leaderboards")
            it.setDescription(
                    """
                        Other features coming soon:tm:..
                        
                        :link: $GITHUB_LINK
                    """.trimIndent()
            )
            it.setFooter("Last updated 2020-09-11", null)
        }?.block()

        return Mono.empty()
    }
}