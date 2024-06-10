import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class LocationTest {
    private Gson gson;
    private Location location;
    private Storage storage;

    @BeforeEach
    void setUp() {
        // Initialize Gson with custom serializers and deserializers
        gson = new GsonBuilder()
                .registerTypeAdapter(Location.class, new Location.LocationSerializer())
                .registerTypeAdapter(Location.class, new Location.LocationDeserializer())
                .create();
        storage = new Storage(); // Assuming Storage has a default constructor
        location = new Location("Test", "This is a test", storage, List.of("North", "East"), true);

    }

    @Test
    void testFirstConstructor(){
        Storage testStorage = new Storage();
        List<String> testList = new ArrayList<>();
        Location testLocation = new Location("test","This is a test",testStorage,testList,true);
        assertEquals("test",testLocation.getName());
        assertEquals("This is a test", testLocation.getDescription());
        assertNotNull(testLocation.getStorage());
        assertNotNull(testLocation.getExit());
        assertTrue(testLocation.isUnlocked());
    }

    @Test
    void testSecondConstructor(){
        Storage testStorage = new Storage();
        List<String> testList = new ArrayList<>();
        UUID id =UUID.randomUUID();
        Location testLocation = new Location(id,"test","This is a test",testStorage,testList,true);
        assertNotNull(testLocation.getId());
        assertEquals("test",testLocation.getName());
        assertEquals("This is a test", testLocation.getDescription());
        assertNotNull(testLocation.getStorage());
        assertNotNull(testLocation.getExit());
        assertTrue(testLocation.isUnlocked());
        assertEquals(id,testLocation.getId());
    }

    @Test
    void testGetName(){
        assertEquals("Test",location.getName());
    }

    @Test
    void testGetDescription(){
        assertEquals("This is a test",location.getDescription());
    }

    @Test
    void testGetStorage(){
        assertEquals(storage, location.getStorage());
    }

    @Test
    void testGetExit(){
        assertEquals(List.of("North", "East"), location.getExit());
    }

    @Test
    void testIsUnlocked(){
        assertTrue(location.isUnlocked());
    }

    @Test
    void testSetUnlock(){
        Storage testStorage = new Storage();
        List<String> testList = new ArrayList<>();
        Location testLocation = new Location("test","This is a test",testStorage,testList,false);
        assertFalse(testLocation.isUnlocked());
        testLocation.setUnlocked();
        assertTrue(testLocation.isUnlocked());
    }

    @Test
    void testLocationSerialization() {
        String json = gson.toJson(location);
        Location deserializedLocation = gson.fromJson(json, Location.class);

        assertEquals(location.getId(), deserializedLocation.getId());
        assertEquals(location.getName(), deserializedLocation.getName());
        assertEquals(location.getDescription(), deserializedLocation.getDescription());
        assertEquals(location.isUnlocked(), deserializedLocation.isUnlocked());
        assertEquals(location.getStorage(), deserializedLocation.getStorage());
        assertEquals(location.getExit(), deserializedLocation.getExit());
    }

    @Test
    void testLocationDeserialization() {
        String json = gson.toJson(location);
        Location deserializedLocation = gson.fromJson(json, Location.class);

        assertEquals(location.getId(), deserializedLocation.getId());
        assertEquals(location.getName(), deserializedLocation.getName());
        assertEquals(location.getDescription(), deserializedLocation.getDescription());
        assertEquals(location.isUnlocked(), deserializedLocation.isUnlocked());
        assertEquals(location.getStorage(), deserializedLocation.getStorage());
        assertEquals(location.getExit(), deserializedLocation.getExit());
    }


}
