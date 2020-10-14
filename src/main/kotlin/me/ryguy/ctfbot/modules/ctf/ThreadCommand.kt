package me.ryguy.ctfbot.modules.ctf

import discord4j.core.`object`.entity.Message
import discord4j.core.spec.EmbedCreateSpec
import discord4j.rest.util.Color
import me.ryguy.ctfbot.util.Util
import me.ryguy.discordapi.command.Command
import org.w3c.dom.Element
import org.w3c.dom.Node
import reactor.core.publisher.Mono
import javax.xml.parsers.DocumentBuilderFactory

class ThreadCommand : Command("thread", "teamthread") {
    override fun execute(message: Message, alias: String, args: Array<String>): Mono<Void> {
        val res: MutableMap<String, String> = HashMap()
        try {
            val dbf = DocumentBuilderFactory.newInstance()
            val db = dbf.newDocumentBuilder()
            val doc = db.parse(Util.requestBrawlURL(RSS_OFFICIAL_TEAMS))
            val threads = doc.getElementsByTagName("item")

            for (i in 0 until threads.length) {
                val threadNode = threads.item(i)
                if (threadNode.nodeType == Node.ELEMENT_NODE) {
                    val thread = threadNode as Element
                    val author = thread.getElementsByTagName("dc:creator").item(0).textContent
                    val link = thread.getElementsByTagName("link").item(0).textContent
                    val title = parseName(thread.getElementsByTagName("title").item(0).textContent)
                    val date = thread.getElementsByTagName("pubDate").item(0).textContent
                    res[title] = buildDesc(author, link, date)
                }
            }
        } catch (e: Exception) {
            message.channel.block()?.createEmbed { em: EmbedCreateSpec ->
                em.setDescription(":x: Error grabbing team threads!")
                em.setColor(Color.RED)
            }?.block()
            Util.sendErrorMessage(e, message)
            e.printStackTrace()

            return Mono.empty()
        }
        if (args.isEmpty()) {
            message.channel.block()?.createEmbed { em: EmbedCreateSpec ->
                val possible = res.keys.joinToString(", ") + ", all, *"

                em.setDescription(":x: You need to include arguments!\nPossible arguments: `$possible`")
                em.setColor(Color.RED)
            }?.block()
        } else {
            if (args[0].equals("all", ignoreCase = true) || args[0].contains("*")) {
                message.channel.block()?.createEmbed { e: EmbedCreateSpec ->
                    e.setColor(Color.TAHITI_GOLD)
                    e.setTitle("Official Team Threads!")
                    e.setFooter("love and pugs :D", null)
                    for ((key, value) in res) {
                        e.addField(key, value, true)
                    }
                }?.block()
            } else {
                val teamName = Util.matchStringFragment(res.keys, args.joinToString(" "))
                if (teamName != null) {
                    message.channel.block()?.createEmbed { e: EmbedCreateSpec ->
                        e.setColor(Color.TAHITI_GOLD)
                        e.setTitle("$teamName's Official Thread!")
                        e.setDescription(res.getOrDefault(teamName, "Well something broke, oopsies"))
                        e.setFooter("love and pugs :D", null)
                    }?.block()
                } else {
                    message.channel.block()?.createEmbed { em: EmbedCreateSpec ->
                        val possible = res.keys.joinToString(", ") + ", all, *"
                        em.setDescription(
                                """
                                :x: Invalid Argument `${args[0]}`!
                                Possible arguments: `$possible`
                            """.trimIndent()
                        )
                        em.setColor(Color.RED)
                    }?.block()
                }
            }
        }

        return Mono.empty()
    }

    companion object {
        const val RSS_OFFICIAL_TEAMS = "https://www.brawl.com/forums/299/index.rss"

        private fun buildDesc(author: String, link: String, date: String): String {
            return """
                **Creator: ** $author
                **Last Comment at: **$date
                **URL: **$link
            """.trimIndent()
        }

        fun parseName(threadName: String): String {
//        val args = threadName.split(" ").toTypedArray()
//        val ret = StringBuilder()
//        for (s in args) {
//            if (s.contains("Recruiting")) continue
//            if (s.matches(".*\\d.*") && (s.contains("/") || s.contains("\\") || s.contains("[") || s.contains("(") || s.contains("]") || s.contains(")"))) continue
//            ret.append("$s ")
//        }

            val idx = threadName.indexOfFirst { it.isDigit() || it == '[' || it == '(' } // stop processing after member count
            return (if (idx == -1) threadName else threadName.substring(0 until idx).trim())
                    .split(" ")
                    .filter { it.isNotEmpty() && it[0].isUpperCase() } // get only words that start uppercase
                    .joinToString(" ")
        }
    }
}