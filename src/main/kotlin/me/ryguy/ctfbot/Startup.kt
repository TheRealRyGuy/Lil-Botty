package me.ryguy.ctfbot

import me.ryguy.ctfbot.cmds.*
import me.ryguy.ctfbot.listeners.MemeChatListeners
import me.ryguy.ctfbot.listeners.EventReactionsListener

object Startup {
    fun registerCommands() {
        EventCommand().register()
        BrawlCommand().register() //DONE
        SetRolesCommand().register() //DONE
        RemoveRolesCommand().register() //DONE

        FindMapCommand().register() // DONE
        InviteCommand().register()

        CTFGameStatsCommand().register()
        CTFStatsCommand().register()
    }

    fun registerListeners() {
        MemeChatListeners().register() //DONE
        EventReactionsListener().register()
    }
}