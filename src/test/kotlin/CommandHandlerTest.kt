import me.ryguy.discordapi.command.CommandHandler
import org.junit.Test
import kotlin.test.assertEquals

class CommandHandlerTest {
    @Test
    fun `test command parsing`() {
        var cmd = "!ppmstats 3"

        assertEquals(listOf("ppmstats", "3"), CommandHandler.parseCommand(cmd), "Failed to parse 1 argument command")

        cmd = "!new multiline \n" +
                "command!"

        assertEquals(listOf("new", "multiline", "command"), CommandHandler.parseCommand(cmd), "Failed to parse multiline command")
    }
}