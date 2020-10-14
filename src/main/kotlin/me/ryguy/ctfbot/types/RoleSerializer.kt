package me.ryguy.ctfbot.types

import com.google.gson.*
import discord4j.common.util.Snowflake
import discord4j.core.`object`.entity.Role
import me.ryguy.discordapi.DiscordBot
import java.lang.reflect.Type

/**
 * Store Role in format of
 * {
 *  "id": <role id>,
 *  "guild": <guild id>
 * }
 *
 * where <guild id> is the id of the guild that the role belongs to
 */

class RoleSerializer : JsonSerializer<Role> {
    override fun serialize(p0: Role?, p1: Type?, p2: JsonSerializationContext?): JsonElement {
        val json = JsonObject()

        val guild = p0?.guild?.block()?.id?.asLong() ?: throw Exception("Null guild")

        json.addProperty("id", p0.id.asLong())
        json.addProperty("guild", guild)

        return json
    }

}

class RoleDeserializer : JsonDeserializer<Role> {
    override fun deserialize(p0: JsonElement?, p1: Type?, p2: JsonDeserializationContext?): Role {
        val guildId = p0?.asJsonObject?.getAsJsonPrimitive("guild")?.asLong ?: throw Exception("Null guild id")

        val guild = DiscordBot.getBot().gateway.getGuildById(Snowflake.of(guildId)).block()
        val role = guild?.getRoleById(Snowflake.of(p0.asJsonObject.getAsJsonPrimitive("id").asLong))?.block()

        return role ?: throw Exception("Failed to find role for guild id $guildId")
    }
}