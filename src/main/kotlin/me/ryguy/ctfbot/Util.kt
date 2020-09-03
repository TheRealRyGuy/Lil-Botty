package me.ryguy.ctfbot

import discord4j.core.`object`.entity.Member
import discord4j.core.`object`.entity.Message
import discord4j.rest.util.Color
import org.jsoup.Jsoup
import java.net.URL

fun Member.isPpmHost(): Boolean {
    return this.roles.map { it.name }.collectList().block()?.contains("PPM Host") ?: false
}

fun URL.parseHtml(): org.jsoup.nodes.Document? {
    this.openConnection().apply {
        addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.135 Safari/537.36")
    }.run {
        return Jsoup.parse(this.getInputStream().reader(Charsets.UTF_8)
                .readLines()
                .joinToString("\n"))
    }
}

fun Message?.replyWithFailure(msg: String) {
    this?.channel?.block()?.createMessage { m ->
        m.setEmbed {
            it.setColor(Color.RED)
            it.setDescription(msg)
        }
    }
}

fun Message?.replyWithSuccess(msg: String) {
    this?.channel?.block()?.createMessage { m ->
        m.setEmbed {
            it.setColor(Color.TAHITI_GOLD)
            it.setDescription(msg)
        }
    }
}