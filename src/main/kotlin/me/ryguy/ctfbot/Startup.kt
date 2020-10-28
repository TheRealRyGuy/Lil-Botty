package me.ryguy.ctfbot

import me.ryguy.ctfbot.cmds.*
import me.ryguy.ctfbot.listeners.MainListener
import me.ryguy.ctfbot.modules.ctf.*
import me.ryguy.ctfbot.modules.events.EventCommand
import me.ryguy.ctfbot.modules.events.EventReactionsListener
import me.ryguy.ctfbot.modules.games.CoinFlipCommand
import me.ryguy.ctfbot.modules.games.DiceRollCommand
import me.ryguy.ctfbot.modules.games.EightBallCommand
import me.ryguy.ctfbot.modules.games.RockPaperScissorsCommand
import me.ryguy.ctfbot.modules.meetings.MeetingCommand
import me.ryguy.ctfbot.modules.meetings.MeetingListeners
import me.ryguy.ctfbot.modules.poll.PollCommand
import me.ryguy.ctfbot.modules.poll.PollListeners
import me.ryguy.ctfbot.modules.quotes.QuoteListener
import me.ryguy.ctfbot.modules.reminders.ReminderCommand
import me.ryguy.ctfbot.modules.roles.RemoveRolesCommand
import me.ryguy.ctfbot.modules.roles.SetRolesCommand

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
        ReminderCommand().register()
        EvalCommand().register()
        CleanDataCommand().register()
        ReportCommand().register()
        SuggestCommand().register()
        ModuleCommand().register()
        //PPM / CTF Commands
        FindMapCommand().register()
        CTFGameStatsCommand().register()
        CTFStatsCommand().register()
        ThreadCommand().register()
        SSCommand().register()
        SS2Command().register()
        StrikeCommand().register()
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