package me.ryguy.ctfbot.types

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import discord4j.core.`object`.entity.Role

/**
 * Class to help GSON understand Discord types
 *
 * To use,
 * kotlin:
 *  TypeSerializer.gson.doSomething()
 *
 * java:
 *  TypeSerializer.INSTANCE.getGson().doSomething()
 */
object TypeSerializer {
    val gson: Gson

    init {
        val builder = GsonBuilder()

        builder.registerTypeAdapter(Event::class.java, UserSerializer())
        builder.registerTypeAdapter(Event::class.java, UserDeserializer())

        builder.registerTypeAdapter(Role::class.java, RoleSerializer())
        builder.registerTypeAdapter(Role::class.java, RoleDeserializer())

        // TODO: implement the rest for Guild, Message, MessageChannel
        //  and possibly special defined enums (like Poll.Option..)

        gson = builder.create()
    }
}