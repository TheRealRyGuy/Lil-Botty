package me.ryguy.ctfbot.types

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import discord4j.core.`object`.entity.Guild
import discord4j.core.`object`.entity.Message
import discord4j.core.`object`.entity.Role
import discord4j.core.`object`.entity.channel.MessageChannel

/**
 * Class to help GSON understand Discord types
 *
 * To use,
 * CTFDiscordBot.gson().doSomething()
 */
object TypeSerializer {
    val gson: Gson

    init {
        val builder = GsonBuilder()

        builder.registerTypeAdapter(Event::class.java, UserSerializer())
        builder.registerTypeAdapter(Event::class.java, UserDeserializer())

        builder.registerTypeAdapter(Role::class.java, RoleSerializer())
        builder.registerTypeAdapter(Role::class.java, RoleDeserializer())

        builder.registerTypeAdapter(Guild::class.java, GuildSerializer())
        builder.registerTypeAdapter(Guild::class.java, GuildDeserializer())

        builder.registerTypeAdapter(Message::class.java, MessageSerializer())
        builder.registerTypeAdapter(Message::class.java, MessageDeserializer())

        builder.registerTypeAdapter(MessageChannel::class.java, MessageChannelSerializer())
        builder.registerTypeAdapter(MessageChannel::class.java, MessageChannelDeserializer())

        gson = builder.create()
    }
}