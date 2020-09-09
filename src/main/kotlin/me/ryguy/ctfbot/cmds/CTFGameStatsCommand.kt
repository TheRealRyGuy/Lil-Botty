package me.ryguy.ctfbot.cmds

import discord4j.core.`object`.entity.Message
import me.ryguy.ctfbot.replyWithFailure
import me.ryguy.ctfbot.replyWithSuccess
import me.ryguy.ctfbot.types.CTFDiscordOnlyCommand
import me.ryguy.ctfbot.util.CTFGame
import me.ryguy.ctfbot.util.parseHtml
import reactor.core.publisher.Mono
import java.lang.NumberFormatException
import java.net.URL

/**
 * Syntax: !gamestats <id>
 * !ppmstats <#>
 */
class CTFGameStatsCommand : CTFDiscordOnlyCommand("gamestats", "ppmstats") {
    companion object {
        fun parseArgs(args: Array<out String>?): Int {
            if (args == null || args.isEmpty())
                return 3

            return try {
                Integer.parseInt(args[0])
            } catch (exception: NumberFormatException) {
                0
            }
        }
    }

    override fun execute(message: Message?, alias: String?, args: Array<out String>?): Mono<Void> {
        if (alias == "ppmstats") {
            val n = parseArgs(args)
            val games = CTFGame.getMostRecentCTFGames(n) { CTFGame.isMatchServer(CTFGame.getServer(it)) }

            if (games.isEmpty()) {
                message.replyWithFailure(":x: Could not find any recent games")
                return Mono.empty()
            }

            message?.channel?.block()?.createEmbed {
                it.setTitle("Recent PPM Games")
                it.setDescription(games.entries.sortedBy { it.key }
                        .map { (id, doc) -> Pair(CTFGame.getMapName(doc), CTFGame.getMapMVP(doc)) to "${CTFGame.FRIENDLY_URL}/$id/" }
                        .map { (pair, url) -> "**:map: ${pair.first} | :trophy: ${pair.second}**\n" +
                                              "$url\n"
                        }
                        .joinToString("\n")
                )
            }?.block()

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