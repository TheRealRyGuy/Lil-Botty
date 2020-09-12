package me.ryguy.ctfbot.types

import com.google.gson.*
import discord4j.common.util.Snowflake
import discord4j.core.`object`.entity.channel.MessageChannel
import me.ryguy.discordapi.DiscordBot
import java.lang.reflect.Type

/**
 * Store MessageChannel as its primitive ID
 */

class MessageChannelSerializer : JsonSerializer<MessageChannel> {
    override fun serialize(p0: MessageChannel?, p1: Type?, p2: JsonSerializationContext?): JsonElement {
        return JsonPrimitive(p0?.id?.asLong())
    }
}

class MessageChannelDeserializer : JsonDeserializer<MessageChannel> {
    override fun deserialize(p0: JsonElement?, p1: Type?, p2: JsonDeserializationContext?): MessageChannel {
        val id = p0?.asLong ?: throw Exception("Null message channel in deserialization")
        return DiscordBot.getBot().gateway.getChannelById(Snowflake.of(id)).block() as MessageChannel
    }
}