package me.ryguy.ctfbot.types

import com.google.gson.*
import discord4j.common.util.Snowflake
import discord4j.core.`object`.entity.Message
import me.ryguy.discordapi.DiscordBot
import java.lang.reflect.Type

/**
 * Store message in format of
 * {
 *  "id": <message id>,
 *  "channel": <channel id>
 * }
 */

class MessageSerializer : JsonSerializer<Message> {
    override fun serialize(p0: Message?, p1: Type?, p2: JsonSerializationContext?): JsonElement {
        val id = p0?.id?.asLong() ?: throw Exception("Null Message ID")
        val channelId = p0.channelId.asLong()

        val json = JsonObject()
        json.addProperty("id", id)
        json.addProperty("channel", channelId)

        return json
    }

}

class MessageDeserializer : JsonDeserializer<Message> {
    override fun deserialize(p0: JsonElement?, p1: Type?, p2: JsonDeserializationContext?): Message {
        val id = p0?.asJsonObject?.get("id")?.asLong ?: throw Exception("Null message id")
        val channelId = p0.asJsonObject.get("channel").asLong

        return DiscordBot.getBot().gateway.getMessageById(Snowflake.of(channelId), Snowflake.of(id)).block()
                ?: throw Exception("Failed to retrieve Message {id: $id, channel: $channelId}")
    }

}