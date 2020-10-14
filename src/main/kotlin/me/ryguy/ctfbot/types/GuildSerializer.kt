package me.ryguy.ctfbot.types

import com.google.gson.*
import discord4j.common.util.Snowflake
import discord4j.core.`object`.entity.Guild
import me.ryguy.discordapi.DiscordBot
import java.lang.reflect.Type

/**
 * Store guild as its ID
 */

class GuildSerializer : JsonSerializer<Guild> {
    override fun serialize(p0: Guild?, p1: Type?, p2: JsonSerializationContext?): JsonElement {
        val id = p0?.id?.asLong() ?: throw Exception("Null guild")
        return JsonPrimitive(id)
    }
}

class GuildDeserializer : JsonDeserializer<Guild> {
    override fun deserialize(p0: JsonElement?, p1: Type?, p2: JsonDeserializationContext?): Guild {
        val id = p0?.asLong ?: throw Exception("Null guild in deserialization")
        return DiscordBot.getBot().gateway.getGuildById(Snowflake.of(id)).block()
                ?: throw Exception("Could not find user")
    }
}