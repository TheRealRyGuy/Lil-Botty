package me.ryguy.ctfbot.cmds

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import discord4j.core.`object`.entity.Message
import discord4j.core.spec.EmbedCreateSpec
import discord4j.core.spec.MessageCreateSpec
import discord4j.rest.util.Color
import me.ryguy.ctfbot.util.GET
import me.ryguy.discordapi.command.Command
import reactor.core.publisher.Mono
import java.net.URL

class BrawlCommand : Command("brawl", "brool") {
    companion object {
        val servers = mapOf(
            "br_brawl_com" to "Battle Royale",
            "ctfmatch_brawl_com" to "Capture the Flag (Match)",
            "lobby_brawl_com" to "Lobby",
            "mc_hg_com" to "Hardcore Games",
            "mc_war_com" to "MC-War",
            "mc_warz_com" to "MC-WarZ",
            "mcctf_com" to "Capture the Flag",
            "minecraftbuild_com" to "Build",
            "minecraftparty_com" to "Minecraft Party",
            "raid_mcpvp_com" to "Raid",
            "test_brawl_com" to "Test",
            "wildwest_brawl_com" to "Wild West",
            "kit_brawl_com" to "KitBrawl",
            "total" to "Total")
    }

    override fun execute(message: Message, alias: String, args: Array<String>): Mono<Void> {
        try {
            val s = URL("https://www.brawl.com/data/playerCount.json").GET()
            val result = Gson().fromJson<Map<String, String>>(s, object: TypeToken<Map<String, String>>() {}.type)

            result.filterKeys { it in servers.keys }.map { (key, value) ->
                String.format(":arrow_forward: **%s**: %s", servers[key], value)
            }.joinToString("\n").run {
                message.channel.block()?.createMessage(this)?.block()
            }
        } catch (e: Exception) {
            message.channel.block()?.createMessage { msg: MessageCreateSpec ->
                msg.setEmbed { embed: EmbedCreateSpec ->
                    embed.setColor(Color.TAHITI_GOLD)
                    embed.setDescription(":x: Error pulling brawl data!")
                }
            }?.block()

            e.printStackTrace()
        }
        return Mono.empty()
    }
}