import me.ryguy.ctfbot.parseHtml
import me.ryguy.ctfbot.util.CTFGame
import org.junit.Assert
import org.junit.Test
import java.net.URL
import kotlin.test.*

class CTFGameStatsTest {
    @Test
    fun `test most recent games`() {
        val mostRecentGame = CTFGame.getMostRecentCTFGame()

        assertTrue(mostRecentGame >= 274595, "Most recent game ID is $mostRecentGame") // current id as of 2020-09-02
        assertFalse(CTFGame.getMostRecentCTFGames(1).isEmpty(), "Should be able to retrieve most recent game")
    }

    @Test
    fun `parse url finding`() {
        val doc = URL("https://www.brawl.com/MPS/MPSStatsCTF.php?game=274595").parseHtml()

        if (doc == null) {
            Assert.fail("Could not retrieve document")
            return
        }

        val url = CTFGame.getServer(doc)

        assertEquals(url, "1.mcctf.com")
        assertFalse(CTFGame.isMatchServer(url), "URL shouldn't be considered match server")
        assertTrue(CTFGame.isMatchServer("2.ctfmatch.brawl.com"), "2.ctfmatch.brawl.com should be match server")
    }
}