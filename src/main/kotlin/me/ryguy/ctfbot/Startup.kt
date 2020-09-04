package me.ryguy.ctfbot

import me.ryguy.ctfbot.cmds.*
import me.ryguy.ctfbot.listeners.MemeChatListeners
import me.ryguy.ctfbot.listeners.EventReactionsListener
import me.ryguy.ctfbot.temp.BasicEventReactionsListener
import me.ryguy.ctfbot.temp.BasicSignUpCommand

object Startup {
    fun registerCommands() {
        EventCommand().register()
        BrawlCommand().register() //DONE
        SetRolesCommand().register() //DONE
        BasicSignUpCommand().register() //DONE
        RemoveRolesCommand().register() //DONE

        FindMapCommand().register() // DONE
        InviteCommand().register()

        CTFGameStatsCommand().register()
        CTFStatsCommand().register()
    }

    fun registerListeners() {
        MemeChatListeners().register() //DONE
        BasicEventReactionsListener().register() //DONE
        EventReactionsListener().register()
    }
}