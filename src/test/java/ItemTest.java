import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemTest {
    private Gson gson;
    private Item item;
    private ContainerItem containerItem;
    private LockedItem lockedItem;
    private Item itemTester;
    private Location location;

    @BeforeEach
    void setUp() {
        // Initialize Gson with custom serializers and deserializers
        gson = new GsonBuilder()
                .registerTypeAdapter(Item.class, new Item.ItemSerializer())
                .registerTypeAdapter(Item.class, new Item.ItemDeserializer())
                .create();

        // Initialize sample data
        location = new Location("TestLocation", "TestLocation", null, null, false);                    //some value are put as null to simplify the notation as they are not used.
        itemTester = new Item("itemTester", "This is a tester item", false, true);
        List<Item> list = new ArrayList<>();
        list.add(itemTester);
        item = new Item("testItem", "This is a testItem", true, true);
        containerItem = new ContainerItem("testContainerItem", "This is a testContainerItem", true, false, itemTester, "This is a test description for containerItem");
        lockedItem = new LockedItem("testLockedItem", "This is a testLockedItem", true, true, list, location, "unlock", "This is a test description for lockedItem");
    }

    @Test
    void testItemConstructor() {
        item = new Item("Item", "This is an item", true, true);
        assertEquals("item", item.getName());
        assertEquals("This is an item", item.getDescription());
        assertTrue(item.isVisible());
        assertTrue(item.isCollectable());
        assertNotNull(item.getId());
    }

    @Test
    void testContainerItemConstructor() {
        containerItem = new ContainerItem("ContainerItem", "This is a ContainerItem", true, false, itemTester, "This is a description");
        assertEquals("containeritem", containerItem.getName());
        assertEquals("This is a ContainerItem", containerItem.getDescription());
        assertTrue(containerItem.isVisible());
        assertFalse(containerItem.isCollectable());
        assertNotNull(containerItem.getId());
        assertEquals(itemTester, containerItem.getItemContained());
        assertEquals("This is a description", containerItem.getAltDescription());
    }

    @Test
    void testLockedItemConstructor() {
        List<Item> list = new ArrayList<>();
        list.add(itemTester);
        lockedItem = new LockedItem("LockedItem", "This is a LockedItem", true, true, list, location, "unlock", "This is a description");
        assertEquals("lockeditem", lockedItem.getName());
        assertEquals("This is a LockedItem", lockedItem.getDescription());
        assertTrue(lockedItem.isVisible());
        assertTrue(lockedItem.isCollectable());
        assertNotNull(lockedItem.getId());
        assertEquals(list, lockedItem.getObjects());
        assertEquals(location, lockedItem.getItemBlocked());
        assertEquals("unlock", lockedItem.getAction());
        assertEquals("This is a description", lockedItem.getAltDescription());
    }

    @Test
    void testGetName(){
        assertEquals("testitem",item.getName());
        assertEquals("testcontaineritem",containerItem.getName());
        assertEquals("testlockeditem",lockedItem.getName());
    }

    @Test
    void testGetDescription(){
        assertEquals("This is a testItem", item.getDescription());
        assertEquals("This is a testContainerItem", containerItem.getDescription());
        assertEquals("This is a testLockedItem", lockedItem.getDescription());
    }

    @Test
    void testIsCollectable(){
        assertTrue(item.isCollectable());
        assertFalse(containerItem.isCollectable());
        assertTrue(lockedItem.isCollectable());
    }

    @Test
    void testIsVisible(){
        assertTrue(item.isVisible());
        assertTrue(containerItem.isVisible());
        assertTrue(lockedItem.isVisible());
    }

    @Test
    void testSetVisible(){
        assertTrue(item.isVisible());
        assertTrue(containerItem.isVisible());
        assertTrue(lockedItem.isVisible());
        item.setVisible(false);
        containerItem.setVisible(false);
        lockedItem.setVisible(false);
        assertFalse(item.isVisible());
        assertFalse(containerItem.isVisible());
        assertFalse(lockedItem.isVisible());
    }

    @Test
    void testItemSerialization() {
        String json = gson.toJson(item);
        Item deserializedItem = gson.fromJson(json, Item.class);

        assertEquals(item.getId(), deserializedItem.getId());
        assertEquals(item.getName(), deserializedItem.getName());
        assertEquals(item.getDescription(), deserializedItem.getDescription());
        assertEquals(item.isVisible(), deserializedItem.isVisible());
        assertEquals(item.isCollectable(), deserializedItem.isCollectable());
    }

    @Test
    void testGetAltDescription(){
        assertEquals("This is a test description for containerItem",containerItem.getAltDescription());
        assertEquals("This is a test description for lockedItem",lockedItem.getAltDescription());
    }

    @Test
    void testGetAction(){
        assertEquals("unlock",lockedItem.getAction());
    }

    @Test
    void testObjectsSize(){
        assertEquals(1,lockedItem.objectsSize());
        Item test = new Item("","",true,true);
        lockedItem.getObjects().add(test);
        assertEquals(2,lockedItem.objectsSize());
    }

    @Test
    void testGetObject(){
        Item test = new Item("test","",true,true);
        lockedItem.getObjects().add(test);
        assertEquals(test,lockedItem.getObject("test"));
        assertNull(lockedItem.getObject("invalidName"));
    }

    @Test
    void testGetItemBlocked(){
        assertEquals(location,lockedItem.getItemBlocked());
    }

    @Test
    void unlockItem(){
        assertEquals("itemtester used successfully\nunlock",lockedItem.unlockItem(itemTester));
        assertNull(lockedItem.unlockItem(item));
    }

    @Test
    void testRemoveObject(){
        assertEquals(1,lockedItem.objectsSize());
        lockedItem.removeObj(itemTester);
        assertEquals(0,lockedItem.objectsSize());
    }

    @Test
    void testGetItemContained(){
        assertEquals(itemTester,containerItem.getItemContained());
    }

    @Test
    void testSetVisibleItemContained(){
        assertFalse(containerItem.getItemContained().isVisible());
        containerItem.setVisibleItemContained();
        assertTrue(containerItem.getItemContained().isVisible());
    }

    @Test
    void testContainerItemSerialization() {
        String json = gson.toJson(containerItem);
        ContainerItem deserializedContainerItem = gson.fromJson(json, ContainerItem.class);

        assertEquals(containerItem.getId(), deserializedContainerItem.getId());
        assertEquals(containerItem.getName(), deserializedContainerItem.getName());
        assertEquals(containerItem.getDescription(), deserializedContainerItem.getDescription());
        assertEquals(containerItem.isVisible(), deserializedContainerItem.isVisible());
        assertEquals(containerItem.isCollectable(), deserializedContainerItem.isCollectable());
        assertEquals(containerItem.getItemContained().getId(), deserializedContainerItem.getItemContained().getId());
        assertEquals(containerItem.getAltDescription(), deserializedContainerItem.getAltDescription());
    }

    @Test
    void testLockedItemSerialization() {
        String json = gson.toJson(lockedItem);
        LockedItem deserializedLockedItem = gson.fromJson(json, LockedItem.class);

        assertEquals(lockedItem.getId(), deserializedLockedItem.getId());
        assertEquals(lockedItem.getName(), deserializedLockedItem.getName());
        assertEquals(lockedItem.getDescription(), deserializedLockedItem.getDescription());
        assertEquals(lockedItem.isVisible(), deserializedLockedItem.isVisible());
        assertEquals(lockedItem.isCollectable(), deserializedLockedItem.isCollectable());
        assertEquals(lockedItem.getObjects().size(), deserializedLockedItem.getObjects().size());
        assertEquals(lockedItem.getAction(), deserializedLockedItem.getAction());
        assertEquals(lockedItem.getAltDescription(), deserializedLockedItem.getAltDescription());
    }
}
