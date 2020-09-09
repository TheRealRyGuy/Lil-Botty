package me.ryguy.ctfbot

import me.ryguy.ctfbot.cmds.*
import me.ryguy.ctfbot.listeners.*

object Startup {
    fun registerCommands() {
        //General Commands
        EventCommand().register()
        BrawlCommand().register()
        SetRolesCommand().register()
        InviteCommand().register()
        PollCommand().register()
        MeetingCommand().register()
        //PPM / CTF Commands
        RemoveRolesCommand().register()
        FindMapCommand().register()
        CTFGameStatsCommand().register()
        CTFStatsCommand().register()
        ThreadCommand().register()
    }

    fun registerListeners() {
        PollListeners().register()
        MemeChatListeners().register()
        EventReactionsListener().register()
        MeetingListeners().register()
        MainListener().register()
    }
}