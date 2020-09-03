package me.ryguy.ctfbot.cmds

import discord4j.core.`object`.entity.Message
import me.ryguy.ctfbot.replyWithFailure
import me.ryguy.ctfbot.replyWithSuccess
import me.ryguy.ctfbot.types.CTFDiscordOnlyCommand
import me.ryguy.ctfbot.util.CTFGame
import me.ryguy.ctfbot.util.parseHtml
import reactor.core.publisher.Mono
import java.net.URL

/**
 * Syntax: !gamestats <id>
 * !ppmstats <#>
 */
class CTFGameStatsCommand : CTFDiscordOnlyCommand("gamestats", "ppmstats") {
    override fun execute(message: Message?, alias: String?, args: Array<out String>?): Mono<Void> {
        if (alias == "ppmstats") {
            val n = if (args == null || args.isEmpty()) 3 else Integer.parseInt(args[0])

            val games = CTFGame.getMostRecentCTFGames(n) { CTFGame.isMatchServer(CTFGame.getServer(it)) }

            if (games.isEmpty()) {
                message.replyWithFailure(":x: Could not find any recent games")
                return Mono.empty()
            }

            message.replyWithSuccess("Found ${games.size} games!\n"
                    .plus(games.keys.sorted()
                            .map { "${CTFGame.FRIENDLY_URL}/$it/" }
                            .joinToString("\n")
                    )
            )

        } else {
            if (args?.isEmpty() != false) {
                message.replyWithFailure("You need to specify a game id")
                return Mono.empty()
            }

            val id = Integer.parseInt(args[0])

            if (id < 0) {
                message.replyWithFailure("Game id must be positive")
                return Mono.empty()
            }

            val doc = URL("${CTFGame.MPS_URL}?game=$id").parseHtml()

            if (doc == null) {
                message.replyWithFailure(":x: Could not retrieve game for that id")
                return Mono.empty()
            }

            //TODO: do some cool display here instead
            return Mono.empty()

        }

        return Mono.empty()
    }
}