package me.ryguy.ctfbot.cmds

import discord4j.core.`object`.entity.Message
import me.ryguy.ctfbot.replyWithFailure
import me.ryguy.ctfbot.replyWithSuccess
import me.ryguy.ctfbot.types.CTFDiscordOnlyCommand
import me.ryguy.ctfbot.util.*
import org.jsoup.nodes.Document
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

        fun getGameDisplay(games: Map<Int, Document>): String {
            return games.entries.sortedBy { it.key }
                    .map { (id, doc) -> "${CTFGame.FRIENDLY_URL}/$id/" to doc}
                    .map { (url, doc) -> "**:map: ${doc.map()} | :trophy: ${doc.mvp()}**\n" +
                            "$url\n"
                    }
                    .joinToString("\n")
        }
    }

    override fun execute(message: Message?, alias: String?, args: Array<out String>?): Mono<Void> {
        if (alias == "ppmstats") {
            val n = parseArgs(args)
            val games1 = CTFGame.getMostRecentCTFGames(n) { it.isMatchServer() && it.getMatchServer() == 1 }
            val games2 = CTFGame.getMostRecentCTFGames(n) { it.isMatchServer() && it.getMatchServer() == 2 }

            if (games1.isEmpty() && games2.isEmpty()) {
                message.replyWithFailure(":x: Could not find any recent games")
                return Mono.empty()
            }

            message?.channel?.block()?.createEmbed {
                it.setTitle("Recent PPM Games")
                if (games2.isEmpty()) {
                    it.setDescription(getGameDisplay(games1))
                    it.setFooter("CTF Match 1", null)
                } else if (games1.isEmpty()) {
                    it.setDescription(getGameDisplay(games2))
                    it.setFooter("CTF Match 2", null)
                } else
                    it.setDescription(
                            "**__CTF Match 1__**\n" +
                            "${getGameDisplay(games1)}\n" +
                            "**__CTF Match 2__**\n" +
                            getGameDisplay(games2)
                    )
            }?.block()

        }
//        else {
//            if (args?.isEmpty() != false) {
//                message.replyWithFailure("You need to specify a game id")
//                return Mono.empty()
//            }
//
//            val id = Integer.parseInt(args[0])
//
//            if (id < 0) {
//                message.replyWithFailure("Game id must be positive")
//                return Mono.empty()
//            }
//
//            val doc = URL("${CTFGame.MPS_URL}?game=$id").parseHtml()
//
//            if (doc == null) {
//                message.replyWithFailure(":x: Could not retrieve game for that id")
//                return Mono.empty()
//            }
//
//            //TODO: do some cool display here instead
//            return Mono.empty()
//
//        }

        return Mono.empty()
    }
}