import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class ControllerTest {
    private Controller controller;

    @Before
    public void testConstructor()
    {
        try
        {
            controller = new Controller();
        }
        catch (Exception e) {
            fail("Expected no exception, but got: " + e.getMessage());
        }
        assertTrue(controller.isCloudActive);
        assertNotNull(controller.window);
        assertEquals("Welcome!\nInsert your name",controller.outputField.getText());
    }

    @Test
    public void testLoad() {
        controller.game = null;
        try
        {
            controller.readInput("TestName");
            controller.readInput("load game");
            assertNotNull(controller.game);
            assertEquals("TestName", controller.name);
        }
        catch(Exception e)
        {
            fail("Expected no exception, but got: " + e.getMessage());
        }
        controller.game = null;
        controller.name = "TestName";
        controller.game = controller.load();
        assertNotNull(controller.game);
    }

    @Test
    public void testShowCommand()
    {
        String text = "Test show";
        controller.show(text);
        assertEquals(text, controller.outputField.getText());
    }

    @Test
    public void testUpdateCommand()
    {
        String textShow = "Test show";
        controller.show(textShow);
        String textUpdate = "Test update";
        controller.update(textUpdate);
        assertEquals(textShow + "\n" + textUpdate,controller.outputField.getText());
    }


    @Test
    public void testShowImmage()
    {
        controller.imageLabel.setIcon(null);
        controller.showImage("hall");
        assertNotNull(controller.imageLabel.getIcon());
    }

    @Test
    public void testLastMessage()
    {
        try {
            String text = "TestLastMessage";
            controller.readInput("Bob");
            controller.readInput("new game");
            controller.readInput(text);
            assertEquals("Command not found, help will show a list of all the commands\n" + "Remember to specify the item when required", controller.getLastMessage());
        }
        catch(Exception e)
        {
            fail("Expected no exception, but got: " + e.getMessage());
        }
    }

    @Test
    public void testRestart()
    {
        try {
            controller.readInput("Bob");
            controller.readInput("new game");
            controller.readInput("go studio");
            controller.readInput("interact carpet");
            controller.readInput("pick key");
            controller.readInput("go hall");
            controller.readInput("go kitchen");
        }
        catch(InterruptedException e)
        {
            fail("Expected no exception, but got: " + e.getMessage());
        }
        controller.restart();
        assertTrue(controller.game.getPlayer().getInventory().isEmpty());
        assertEquals("Bob", controller.name);
        assertEquals("hall", controller.game.getPlayer().getLocation().getName());
    }

    @Test
    public void testSetupCloudBucket()
    {
        try
        {
            controller.setupCloudBucket();
        }
        catch(Exception e)
        {
            fail("Expected no exception, but got: " + e.getMessage());
        }
    }

    @Test
    public void testMainFieldNewGame() throws InterruptedException {
        controller.readInput("Bob");
        controller.readInput("new game");
        assertEquals("Welcome to my castle " + controller.name + ". There's a note in front of you. Enter \"interact note\" to read it.", controller.outputField.getText());
    }

    @Test
    public void testReadInput()
    {
        try {
            controller.readInput("Bob");
            assertEquals("Hi " + controller.name + "! Start new game or load an old one?\n(new game, load game)", controller.outputField.getText());
            controller.readInput("new game");
            assertEquals("Welcome to my castle " + controller.name + ". There's a note in front of you. Enter \"interact note\" to read it.", controller.outputField.getText());
            controller.readInput("Wrong text");
            assertEquals("Command not found, help will show a list of all the commands", controller.outputField.getText());
        }
        catch(InterruptedException e)
        {
            fail("Expected no exception, but got: " + e.getMessage());
        }
    }


    @Test
    public void TestVictory() {
        try {
            controller.readInput("Bob");
            controller.readInput("new game");
            controller.readInput("go studio");
            controller.readInput("interact carpet");
            controller.readInput("pick key");
            controller.readInput("go hall");
            controller.readInput("go kitchen");
            controller.readInput("interact soup");
            controller.readInput("pick key");
            controller.readInput("go hall");
            controller.readInput("interact door");
            controller.readInput("use key");
            controller.readInput("interact door");
            controller.readInput("use key");
            controller.readInput("go studio");
            controller.readInput("interact drawer");
            controller.readInput("pick leaflet");
            controller.readInput("go hall");
            controller.readInput("go kitchen");
            controller.readInput("interact cookbook");
            controller.readInput("pick recipe");
            controller.readInput("go hall");
            controller.readInput("go lab");
            controller.readInput("interact computer");
            controller.readInput("use recipe");
            controller.readInput("interact machine");
            controller.readInput("use leaflet");
            controller.readInput("interact drawer");
            controller.readInput("pick gloves");
            controller.readInput("pick acid");
            controller.readInput("go hall");
            controller.readInput("interact gate");
            controller.readInput("use acid");
            controller.readInput("go lab");
            controller.readInput("pick hammer");
            controller.readInput("go hall");
            controller.readInput("go dungeons");
            controller.readInput("interact left-cell");
            controller.readInput("use hammer");
            controller.readInput("interact gateway");
            assertTrue(controller.game.isWinCondition());
        }
        catch(InterruptedException e)
        {
            fail("Expected no exception, but got: " + e.getMessage());
        }
    }


    @Test
    public void TestLoseLifePointsTray() {
        try {
            controller.readInput("Bob");
            controller.readInput("new game");
            controller.readInput("go kitchen");
            controller.readInput("interact oven");
            controller.readInput("interact tray");
            controller.readInput("interact tray");
            controller.readInput("interact tray");
            assertTrue((controller.game.getPlayer().getLifePoints())<=0);
        }
        catch(InterruptedException e)
        {
            fail("Expected no exception, but got: " + e.getMessage());
        }
    }

    @Test
    public void TestLoseLifePointsFood() {
        try {
            controller.readInput("Bob");
            controller.readInput("new game");
            controller.readInput("go kitchen");
            controller.readInput("interact pantry");
            controller.readInput("interact food");
            controller.readInput("interact food");
            controller.readInput("interact food");
            assertTrue((controller.game.getPlayer().getLifePoints())<=0);
        }
        catch(InterruptedException e)
        {
            fail("Expected no exception, but got: " + e.getMessage());
        }
    }

    @Test
    public void TestLoseLifePointsAcid() {
        try {
            controller.readInput("Bob");
            controller.readInput("new game");
            controller.readInput("go studio");
            controller.readInput("interact carpet");
            controller.readInput("pick key");
            controller.readInput("go hall");
            controller.readInput("go kitchen");
            controller.readInput("interact soup");
            controller.readInput("pick key");
            controller.readInput("go hall");
            controller.readInput("interact door");
            controller.readInput("use key");
            controller.readInput("interact door");
            controller.readInput("use key");
            controller.readInput("go studio");
            controller.readInput("interact drawer");
            controller.readInput("pick leaflet");
            controller.readInput("go hall");
            controller.readInput("go kitchen");
            controller.readInput("interact cookbook");
            controller.readInput("pick recipe");
            controller.readInput("go hall");
            controller.readInput("go lab");
            controller.readInput("interact computer");
            controller.readInput("use recipe");
            controller.readInput("interact machine");
            controller.readInput("use leaflet");
            controller.readInput("pick acid");
            assertTrue((controller.game.getPlayer().getLifePoints())<=2);
        }
        catch(InterruptedException e)
        {
            fail("Expected no exception, but got: " + e.getMessage());
        }
    }

    @Test
    public void TestLoseLifePointsEnemy() {
        try {
            controller.readInput("Bob");
            controller.readInput("new game");
            controller.readInput("go studio");
            controller.readInput("interact carpet");
            controller.readInput("pick key");
            controller.readInput("go hall");
            controller.readInput("go kitchen");
            controller.readInput("interact soup");
            controller.readInput("pick key");
            controller.readInput("go hall");
            controller.readInput("interact door");
            controller.readInput("use key");
            controller.readInput("interact door");
            controller.readInput("use key");
            controller.readInput("go studio");
            controller.readInput("interact drawer");
            controller.readInput("pick leaflet");
            controller.readInput("go hall");
            controller.readInput("go kitchen");
            controller.readInput("interact cookbook");
            controller.readInput("pick recipe");
            controller.readInput("go hall");
            controller.readInput("go lab");
            controller.readInput("interact computer");
            controller.readInput("use recipe");
            controller.readInput("interact machine");
            controller.readInput("use leaflet");
            controller.readInput("interact drawer");
            controller.readInput("pick gloves");
            controller.readInput("pick acid");
            controller.readInput("go hall");
            controller.readInput("interact gate");
            controller.readInput("use acid");
            controller.readInput("go lab");
            controller.readInput("pick hammer");
            controller.readInput("go hall");
            controller.readInput("go dungeons");
            controller.readInput("interact right-cell");
            controller.readInput("use hammer");
            assertTrue((controller.game.getPlayer().getLifePoints())<=2);
        }
        catch(InterruptedException e)
        {
            fail("Expected no exception, but got: " + e.getMessage());
        }
    }
}
