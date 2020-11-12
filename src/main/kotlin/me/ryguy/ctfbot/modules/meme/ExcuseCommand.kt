package me.ryguy.ctfbot.modules.meme

import discord4j.core.`object`.entity.Message
import me.ryguy.discordapi.command.Command
import org.jsoup.Jsoup
import reactor.core.publisher.Mono
import java.net.URL

class ExcuseCommand : Command("excuse") {
    override fun execute(p0: Message?, p1: String?, p2: Array<out String>?): Mono<Void> {
        Jsoup.parse(URL("https://programmingexcuses.com"), 1000)
                .getElementsByTag("a")[0].text().let {
            p0?.channel?.block()?.createMessage(it)?.block()
        }

        return Mono.empty()
    }
}