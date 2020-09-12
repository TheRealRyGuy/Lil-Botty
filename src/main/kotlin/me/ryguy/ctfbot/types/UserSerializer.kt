package me.ryguy.ctfbot.types

import com.google.gson.*
import discord4j.common.util.Snowflake
import discord4j.core.`object`.entity.User
import me.ryguy.discordapi.DiscordBot
import java.lang.reflect.Type

/**
 * Store User as its primitive id
 */

class UserSerializer : JsonSerializer<User> {
    override fun serialize(p0: User?, p1: Type?, p2: JsonSerializationContext?): JsonElement {
        return JsonPrimitive(p0?.id?.asLong())
    }
}

class UserDeserializer : JsonDeserializer<User> {
    override fun deserialize(p0: JsonElement?, p1: Type?, p2: JsonDeserializationContext?): User {
        val id = p0?.asLong ?: throw Exception("Null user in deserialization")
        return DiscordBot.getBot().gateway.getUserById(Snowflake.of(id)).block() ?: throw Exception("Could not find user $id")
    }
}