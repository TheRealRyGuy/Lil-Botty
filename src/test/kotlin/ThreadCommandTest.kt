import me.ryguy.ctfbot.modules.ctf.ThreadCommand
import org.junit.Test
import kotlin.test.assertEquals

class ThreadCommandTest {
    @Test
    fun `parse thread names`() {
        var name = " Delta Force [21/25] - Recruiting Bulks and Ninjas!"
        assertEquals("Delta Force", ThreadCommand.parseName(name), "Failed to parse with [##/25] format")

        name = "Olympus"
        assertEquals("Olympus", ThreadCommand.parseName(name), "Failed to parse normal team thread name")

        name = "The Roxerces (22/25)"
        assertEquals("The Roxerces", ThreadCommand.parseName(name), "Failed to parse team thread with (##/25) format")

        name = "The Burger Knights 19/25"
        assertEquals("The Burger Knights", ThreadCommand.parseName(name), "Failed to parse team thread with unenclosed counter")

        name = "CITRIX (23/25) [Recruiting Defense]"
        assertEquals("CITRIX", ThreadCommand.parseName(name), "Failed to parse double tag thread name")
    }
}