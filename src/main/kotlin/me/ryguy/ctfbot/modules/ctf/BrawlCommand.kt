package me.ryguy.ctfbot.modules.ctf

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
import java.util.Comparator

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

        val emojis = mapOf(
            "br_brawl_com" to ":drop_of_blood:",
            "ctfmatch_brawl_com" to ":triangular_flag_on_post:",
            "lobby_brawl_com" to ":couch:",
            "mc_hg_com" to ":drop_of_blood:",
            "mc_war_com" to ":gun:",
            "mc_warz_com" to ":zombie:",
            "mcctf_com" to ":triangular_flag_on_post:",
            "minecraftbuild_com" to ":construction_site:",
            "minecraftparty_com" to ":tada:",
            "raid_mcpvp_com" to ":map:",
            "test_brawl_com" to ":gear:",
            "wildwest_brawl_com" to ":cowboy:",
            "kit_brawl_com" to ":crossed_swords:")
    }

    override fun execute(message: Message, alias: String, args: Array<String>): Mono<Void> {
        try {
            val s = URL("https://www.brawl.com/data/playerCount.json").GET()
            val result = Gson().fromJson<Map<String, String>>(s, object: TypeToken<Map<String, String>>() {}.type)

            result.filterKeys { it in servers.keys && it != "total" }.toSortedMap(Comparator.comparing { servers[it] ?: error("") }).map { (url, count) ->
                "${emojis[url]} **${servers[url]}**: $count"
            }.joinToString("\n").run {

                val total = result.filterKeys { it in servers.keys && it != "total" }
                        .values.sumBy { Integer.parseInt(it) }

                message.channel.block()?.createEmbed {
                    it.setDescription(this)
                    it.setFooter("Total: $total", null)
                }?.block()
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