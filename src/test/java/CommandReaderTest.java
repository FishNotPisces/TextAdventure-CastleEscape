import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class CommandReaderTest
{
    private CommandReader commandReader;
    private Controller controller;
    private Game game;

    @Before
    public void testContructor()
    {
        try {
            controller = new Controller();
            game = new Game(controller,"Bob");
            commandReader = new CommandReader(controller,game);
        }
        catch (Exception e)
        {
            fail("Expected no exception, but got: " + e.getMessage());
        }
        assertEquals("",commandReader.lastCommandUsed);
    }

    @Test
    public void testReadExit()
    {
        commandReader.read("exit");
        assertEquals("exit",commandReader.lastCommandUsed);
    }

    @Test
    public void testReadLookAround()
    {
        commandReader.read("look around");
        assertEquals("look",commandReader.lastCommandUsed);
    }


    @Test
    public void testReadPick()
    {
        commandReader.read("pick candlestick");
        assertEquals("pick",commandReader.lastCommandUsed);
    }

    @Test
    public void testReadInteract()
    {
        commandReader.read("interact door");
        assertEquals("interact",commandReader.lastCommandUsed);
        assertEquals("door",game.lastItemUsed.getName());
    }

    @Test
    public void testUpdateLastCommand()
    {
        commandReader.updateLastCommand("TestLastCommand");
        assertEquals("TestLastCommand",commandReader.lastCommandUsed);
    }
}

