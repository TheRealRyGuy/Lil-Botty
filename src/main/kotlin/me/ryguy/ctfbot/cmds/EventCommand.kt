package me.ryguy.ctfbot.cmds;

import discord4j.core.`object`.entity.Message
import me.ryguy.ctfbot.isPpmHost
import me.ryguy.ctfbot.types.CTFDiscordOnlyCommand
import me.ryguy.ctfbot.types.Event
import me.ryguy.ctfbot.util.EmbedPresets
import me.ryguy.discordapi.util.WorkFlow
import reactor.core.publisher.Mono

class EventCommand : CTFDiscordOnlyCommand("event", "ppm") {
    override fun execute(message: Message?, alias: String?, args: Array<out String>?): Mono<Void> {
        if (message?.authorAsMember?.block()?.isPpmHost() == true) {
            WorkFlow(Event(), message.channel.block(), message.author.get())
                    .run {
                        this.addRule("!cancel") { wf ->
                            wf.end()
                            message.channel.block()?.createMessage {e ->
                                e.setEmbed {
                                    EmbedPresets.success().setDescription(":x: Event Cancelled!")
                                }
                            }
                        }
                    }
        }

        return Mono.empty()
    }
}
