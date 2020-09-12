package me.ryguy.ctfbot

import me.ryguy.ctfbot.cmds.*
import me.ryguy.ctfbot.cmds.ctf.*;
import me.ryguy.ctfbot.cmds.games.*;
import me.ryguy.ctfbot.cmds.general.*;
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
        RemoveRolesCommand().register()
        HelpCommand().register()
        MemCommand().register()
        EightBallCommand().register()
        CoinFlipCommand().register()
        DiceRollCommand().register()
        RockPaperScissorsCommand().register()
        RestartCommand().register()
        //PPM / CTF Commands
        FindMapCommand().register()
        CTFGameStatsCommand().register()
        CTFStatsCommand().register()
        ThreadCommand().register()
        SSCommand().register()
        SS2Command().register()
    }

    fun registerListeners() {
        PollListeners().register()
        MemeChatListeners().register()
        EventReactionsListener().register()
        MeetingListeners().register()
        MainListener().register()
        QuoteListener().register()
    }
}