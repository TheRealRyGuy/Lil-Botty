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
    fun `test retrieving ppm stats`() {
        var ppmGames = CTFGame.getMostRecentCTFGames(4) {
            // retrieve casual games since match server ones aren't technically guaranteed
            !CTFGame.isMatchServer(CTFGame.getServer(it))
        }

        assertEquals(ppmGames.size, 4)

        // test clipping values to 0-5
        ppmGames = CTFGame.getMostRecentCTFGames(10)
        assertEquals(ppmGames.size, 5)

        ppmGames = CTFGame.getMostRecentCTFGames(-3)
        assertEquals(ppmGames.size, 0)
    }

    @Test
    fun `parse url finding`() {
        val doc = URL("${CTFGame.MPS_URL}?game=274595").parseHtml()
                ?: throw AssertionError("Could not retrieve document")

        val url = CTFGame.getServer(doc)

        assertEquals(url, "1.mcctf.com")
        assertFalse(CTFGame.isMatchServer(url), "URL shouldn't be considered match server")
        assertTrue(CTFGame.isMatchServer("2.ctfmatch.brawl.com"), "2.ctfmatch.brawl.com should be match server")
    }
}