import me.ryguy.ctfbot.cmds.CTFGameStatsCommand
import me.ryguy.ctfbot.util.parseHtml
import me.ryguy.ctfbot.util.CTFGame
import org.junit.Test
import java.net.URL
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CTFGameStatsTest {
    @Test
    fun `test most recent games`() {
        val mostRecentGame = CTFGame.getMostRecentCTFGame()

        assertTrue(mostRecentGame >= 274595, "Most recent game ID is $mostRecentGame") // current id as of 2020-09-02
        assertFalse(CTFGame.getMostRecentCTFGames(1).isEmpty(), "Should be able to retrieve most recent game")
    }

    @Test
    fun `test command arguments`() {
        var args: Array<String>? = arrayOf()
        var n = CTFGameStatsCommand.parseArgs(args)
        assertEquals(3, n, "Parsed empty array poorly")

        args = null
        n = CTFGameStatsCommand.parseArgs(args)
        assertEquals(3, n, "Parsed null array poorly")

        args = arrayOf("junk")
        n = CTFGameStatsCommand.parseArgs(args)
        assertEquals(0, n, "Failed to handle junk array")

        args = arrayOf("5")
        n = CTFGameStatsCommand.parseArgs(args)
        assertEquals(5, n, "Parsed actual array incorrectly")

        args = arrayOf("5", "10")
        n = CTFGameStatsCommand.parseArgs(args)
        assertEquals(5, n, "Parsed longer array incorrectly")
    }

    @Test
    fun `test retrieving ppm stats`() {
        var ppmGames = CTFGame.getMostRecentCTFGames(4) {
            // retrieve casual games since match server ones aren't technically guaranteed
            !CTFGame.isMatchServer(CTFGame.getServer(it))
        }

        assertEquals(4, ppmGames.size)

        // test clipping values to 0-5
        ppmGames = CTFGame.getMostRecentCTFGames(10)
        assertEquals(5, ppmGames.size)

        ppmGames = CTFGame.getMostRecentCTFGames(-3)
        assertEquals(0, ppmGames.size)
    }

    @Test
    fun `parse url finding`() {
        val doc = URL("${CTFGame.MPS_URL}?game=274595").parseHtml()
                ?: throw AssertionError("Could not retrieve document")

        val url = CTFGame.getServer(doc)

        assertEquals("1.mcctf.com", url)
        assertFalse(CTFGame.isMatchServer(url), "URL shouldn't be considered match server")
        assertTrue(CTFGame.isMatchServer("2.ctfmatch.brawl.com"), "2.ctfmatch.brawl.com should be match server")
    }
}