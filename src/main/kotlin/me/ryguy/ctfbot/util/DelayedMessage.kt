package me.ryguy.ctfbot.util

import discord4j.core.`object`.entity.channel.MessageChannel
import discord4j.core.spec.EmbedCreateSpec
import discord4j.rest.util.Color
import java.time.Instant

/**
 * Represents a message that we can send later
 */
class DelayedMessage(var channel: MessageChannel, val title: String, val contents: String, val color: Color) {
    fun send() {
        channel.createEmbed { e: EmbedCreateSpec ->
            e.setTitle(title)
            e.setColor(color)
            e.setDescription(contents)
            e.setTimestamp(Instant.now())
        }.block()
    }
}