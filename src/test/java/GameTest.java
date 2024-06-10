import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    private Game game;
    private Controller controller;

    @BeforeEach
    void setUp() throws IOException, InterruptedException {
        controller = new Controller();
        controller.readInput("TestPlayer");
        controller.readInput("new game");
        game=controller.game;
    }

    @Test
    void testConstructor(){
        Game gameTest = new Game(controller,"test");
        assertNotNull(gameTest.controller);
        assertNotNull(gameTest.getMap());
        assertNotNull(gameTest.getPlayer());
    }
    @Test
    void testCreatePlayer() {
        assertNotNull(game.getPlayer());
        assertEquals("TestPlayer", game.getPlayer().getName());
        assertEquals("hall", game.getPlayer().getLocation().getName());
    }

    @Test
    void testInteractWithItemFound() {
        Item result = game.interact("note");
        assertNotNull(result);
        assertEquals(game.getMap().get("hall").getStorage().getItem("note"), result);
    }

    @Test
    void testInteractWithItemNotFound() {
        Item result = game.interact("nonexistent");
        assertNull(result);
        assertEquals("Item not found", controller.getLastMessage());
    }

    @Test
    void testInteractItemInInventory() throws InterruptedException {
        controller.readInput("pick note");
        Item result = game.interact("note");
        assertNotNull(result);
        assertEquals("You are trapped in my castle, you are now in the hall. " +
                "This room has four doors, but two of them are locked. To the east there " +
                "is the kitchen, to the west the study. The north gate and the south door are locked. " +
                "Use your wits to solve the puzzles and find a way out. I wish you good luck, you'll need" +
                " it.\nEnter \"help\" to see all the commands",controller.getLastMessage());
    }

    @Test
    void testInteract() {
        Item interactedItem = game.interact("note");
        assertNotNull(interactedItem);
        assertEquals("note", interactedItem.getName());
        assertEquals("You are trapped in my castle, you are now in the hall. This room has four doors, " +
                "but two of them are locked. To the east there is the kitchen, to the west the study. The north" +
                " gate and the south door are locked. Use your wits to solve the puzzles and find a way out. " +
                "I wish you good luck, you'll need it.\nEnter \"help\" to see all the commands",controller.getLastMessage());
    }

    @Test
    void testUseItemNotInInventory() {
        Item result = game.use("nonexistent");
        assertNull(result);
        assertEquals("nonexistent is not in your inventory", controller.getLastMessage());
    }

    @Test
    void testUseItemInInventory() throws InterruptedException {
        controller.readInput("go studio");
        controller.readInput("interact carpet");
        controller.readInput("pick key");
        controller.readInput("go hall");
        controller.readInput("interact door");
        controller.readInput("use key");
        assertFalse(game.getPlayer().getInventory().hasItem("key"));
        assertEquals("key used successfully\n",controller.getLastMessage());
    }

    @Test
    void testUseWrongItem() throws InterruptedException {
        controller.readInput("go studio");
        controller.readInput("interact carpet");
        controller.readInput("pick key");
        controller.readInput("go hall");
        controller.readInput("interact gate");
        controller.readInput("use key");
        assertTrue(game.getPlayer().getInventory().hasItem("key"));
        assertEquals("You can't use this item here",controller.getLastMessage());
    }


    @Test
    void testSpecialCases() throws InterruptedException {
        controller.readInput("go kitchen");
        controller.readInput("interact oven");
        controller.readInput("interact tray");
        assertEquals("tray",game.lastItemUsed.getName());
        assertEquals("Inside the oven a baking tray contains a baked crane, the smell is delicious!\n" +
                "You lost a life point because you didn't use gloves, the oven tray was glowing",controller.getLastMessage());
    }

    @Test
    void testGetMap() {
        assertNotNull(game.getMap());
    }

    @Test
    void testHelp() {
        game.help();
        assertTrue(controller.getLastMessage().contains("The possible commands are:"));
    }

    @Test
    void testGoDirection() {
        Location result= game.go("east");
        assertEquals(result, game.getPlayer().getLocation());
    }

    @Test
    void testGoBack() {
        Location result= game.go("back");
        assertNull(result);
        assertEquals("This is the first location you visited, you cannot go back anymore",controller.getLastMessage());
        game.go("east");
        result=game.go("back");
        assertNotNull(result);
        assertEquals(game.getPlayer().getLocation(),result);
    }

    @Test
    void testGo() {
        Location result= game.go(null);
        assertNull(result);
        assertTrue( controller.getLastMessage().contains("You can go:"));
    }

    @Test
    void testGoInvalidDirection() {
        game.go("invalidDirection");
        assertEquals("Direction not valid", controller.getLastMessage());
        Location result= game.go("north");
        assertNull(result);
        assertEquals("Direction not valid",controller.getLastMessage());
    }

    @Test
    void testUpdateLastItemUsed(){
        game.updateLastItem("InvalidItem");
        assertNull(game.lastItemUsed);
        game.updateLastItem("note");
        assertEquals("note",game.lastItemUsed.getName());
        game.updateLastItem("candlestick");
        assertEquals("candlestick",game.lastItemUsed.getName());
    }

    @Test
    void testVictory() {
        game.victory();
        assertTrue(game.isWinCondition());
    }
}
