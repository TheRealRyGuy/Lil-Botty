package me.ryguy.ctfbot.util

import org.jsoup.nodes.Document
import java.lang.Integer.max
import java.lang.Integer.min
import java.net.URL
import java.util.regex.Pattern

/**
 * Utility class that understands how CTF specifies game stats
 */
object CTFGame {
    val MPS_URL = "https://www.brawl.com/MPS/MPSStatsCTF.php"
    val FRIENDLY_URL = "https://www.brawl.com/games/ctf/lookup"

    /**
     * Retrieves the ID of the most recent CTF game
     * @return The most recent game id if found, -1 otherwise
     */
    fun getMostRecentCTFGame(): Int {
        val recentDoc = URL(MPS_URL).parseHtml() ?: return -1

        val elem = recentDoc.getElementsByTag("a").first() // find first link

        return Integer.parseInt(elem.text())
    }

    /**
     * Returns the n most recent ctf games that satisfy the filter, attempting up to 40 retrievals
     * @param n Number of games to retrieve, will go up to 5
     * @return List of parsed webpages that satisfy the conditions
     */
    fun getMostRecentCTFGames(n: Int, filter: (Document) -> Boolean = { true }): Map<Int, Document> {
        var attempted = 0
        var currentId = getMostRecentCTFGame()

        val result = mutableMapOf<Int, Document>()

        while (result.size < max(0, min(n, 5)) && attempted < 40) {
            val doc = URL("https://www.brawl.com/MPS/MPSStatsCTF.php?game=$currentId").parseHtml()

            if (doc != null && filter.invoke(doc)) {
                result[currentId] = doc
            }

            attempted++
            currentId--
        }

        return result
    }

    fun getServer(doc: Document): String {
        val urlPattern = Pattern.compile("[0-9]\\..+\\.com")
        return doc.getElementsByTag("h1").first()
                .text()
                .let {
                    val matcher = urlPattern.matcher(it)
                    while (matcher.find()) {
                        return it.slice(matcher.start() until matcher.end())
                    }

                    ""
                }
    }

    fun isMatchServer(url: String): Boolean {
        return url.contains("ctfmatch")
    }

    fun getMapName(doc: Document): String {
        return doc.getElementById("map-name").text()
                .let {
                    it.substring(5 until it.length)
                }
    }

    fun getMapMVP(doc: Document): String {
        return doc.getElementsByTag("a").first()
                .text()
    }
}