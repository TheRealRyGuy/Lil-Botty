package me.ryguy.ctfbot.types

import com.google.gson.*
import discord4j.common.util.Snowflake
import discord4j.core.`object`.entity.channel.MessageChannel
import discord4j.core.`object`.entity.channel.PrivateChannel
import me.ryguy.discordapi.DiscordBot
import java.lang.reflect.Type

/**
 * Store MessageChannel as its primitive ID
 */

class MessageChannelSerializer : JsonSerializer<MessageChannel> {
    override fun serialize(p0: MessageChannel?, p1: Type?, p2: JsonSerializationContext?): JsonElement {
        val json = JsonObject()

        json.addProperty("id", p0?.id?.asLong())
        json.addProperty("type", p0?.type?.name)

        return json
    }
}

class MessageChannelDeserializer : JsonDeserializer<MessageChannel> {
    override fun deserialize(p0: JsonElement?, p1: Type?, p2: JsonDeserializationContext?): MessageChannel {
        val id = p0?.asJsonObject?.getAsJsonPrimitive("id")?.asLong
                ?: throw Exception("Null message channel in deserialization")
        val type = p0.asJsonObject?.getAsJsonPrimitive("type")?.asString
                ?: throw Exception("Null MessageChannel type in deserialization")
        if (type.contains("DM", true)) {
            val user = DiscordBot.getBot().gateway.getUserById(Snowflake.of(id)).block()
                    ?: throw Exception("Null User in Private Channel deserialization")
            return user.privateChannel.block() as PrivateChannel
        } else
            return DiscordBot.getBot().gateway.getChannelById(Snowflake.of(id)).block() as MessageChannel
    }
}