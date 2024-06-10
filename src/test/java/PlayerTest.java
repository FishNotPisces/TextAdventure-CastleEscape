import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PlayerTest {

    private Player player;
    private Location location;
    private Storage storage;
    private Item item;

    @BeforeEach
    public void setUp() {
        player = new Player("TestPlayer");
        storage = new Storage(10);
        location = new Location("location", "description", storage, null, true);
        item = new Item("item", "description", true, true);
    }

    @Test
    public void testGetName() {
        assertEquals("TestPlayer", player.getName());
    }

    @Test
    public void testGetInventory() {
        assertNotNull(player.getInventory());
        assertEquals(2, player.getInventory().getMaxWeight());
    }

    @Test
    public void testGetLocation() {
        assertNull(player.getLocation());
        player.setLocation(location);
        assertEquals(location, player.getLocation());
    }

    @Test
    public void testSetLocation() {
        player.setLocation(location);
        assertEquals(location, player.getLocation());
    }

    @Test
    public void testPopLastLocation() {
        player.setLocation(location);
        assertNull(player.popLastLocation());
        player.setLocation(new Location("location2", "description2", storage, null, true));
        Location previousLocation = player.popLastLocation();
        assertEquals(location, previousLocation);
    }

    @Test
    public void testLoseLifePoints() {
        assertEquals(3, player.getLifePoints());
        player.loseLifePoints(1);
        assertEquals(2, player.getLifePoints());
    }

    @Test
    public void testGetLifePoints() {
        assertEquals(3, player.getLifePoints());
    }

    @Test
    public void testStatus() {
        String emptyStatus = "You have 3 life points\nYour inventory is empty";
        assertEquals(emptyStatus, player.status());
    }

    @Test
    public void testPick() {
        player.setLocation(location);
        assertNull(player.pick("item"));
        storage.add(item);

        Item pickedItem = player.pick("item");
        assertNotNull(pickedItem);
        assertTrue(player.getInventory().hasItem("item"));
        assertFalse(storage.hasItem("item"));
    }

    @Test
    public void testPickAcidWithoutGloves() {
        Item acid = new Item("acid", "description",true, true);
        player.setLocation(location);
        storage.add(acid);

        Item pickedItem = player.pick("acid");
        assertNull(pickedItem);
        assertEquals(2, player.getLifePoints());
    }

    @Test
    public void testDrop() {
        player.setLocation(location);
        assertNull(player.drop("item"));
        player.getInventory().add(item);

        Item droppedItem = player.drop("item");
        assertNotNull(droppedItem);
        assertFalse(player.getInventory().hasItem("item"));
        assertTrue(location.getStorage().hasItem("item"));
    }

    @Test
    public void testLookAround() {
        player.setLocation(location);
        storage.add(item);

        String lookAroundResult = player.lookAround();
        assertEquals("You see: \n- item\n", lookAroundResult);
    }
}