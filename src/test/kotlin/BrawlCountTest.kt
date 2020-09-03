import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import me.ryguy.ctfbot.cmds.BrawlCommand
import me.ryguy.ctfbot.util.GET
import me.ryguy.ctfbot.util.WebUtil
import org.junit.Test
import java.net.URL
import kotlin.test.assertTrue

class BrawlCountTest {
    @Test
    fun `test brawl count parsing`() {
        val s = URL("https://www.brawl.com/data/playerCount.json").GET()
        val result = Gson().fromJson<Map<String, String>>(s, object: TypeToken<Map<String, String>>() {}.type)

        assertTrue(result.isNotEmpty(), "Obtained empty brawl json")

        result.filterKeys { it in BrawlCommand.servers.keys }.forEach { (url, count) ->
            assertTrue(url in BrawlCommand.servers.keys, "Unknown url found: $url")
            assertTrue(Integer.parseInt(count) >= 0, "Could not parse integer $count")
        }
    }
}