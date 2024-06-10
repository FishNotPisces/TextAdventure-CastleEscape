import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * The {@code Registry} class provides methods for registering items and locations during deserialization process
 * using their unique identifiers (UUIDs).
 *
 * @see Item
 * @see Location
 */
public class Registry {
    private static final Map<UUID, Item> itemMap = new HashMap<>();
    private static final Map<UUID, Location> locationMap = new HashMap<>();

    /**
     * Registers the item in a Map. The keys are the UUIDs to prevent duplicates
     *
     * @param item the item to register
     */
    public static void registerItem(Item item) {
        itemMap.put(item.getId(), item);
    }

    /**
     * Returns the item with the specified identifier, or null if not found
     *
     * @param id the unique identifier of the item
     * @return the item with the specified identifier, or {@code null} if not found
     */
    public static Item getItem(UUID id) {
        return itemMap.get(id);
    }

    /**
     * Registers the location in a Map. The keys are the UUIDs to prevent duplicates
     *
     * @param location the location to register
     */
    public static void registerLoc(Location location) {
        locationMap.put(location.getId(), location);
    }

    /**
     * Returns the location with the specified identifier, or null if not found
     *
     * @param id the unique identifier of the location
     * @return the location with the specified identifier, or {@code null} if not found
     */
    public static Location getLoc(UUID id) {
        return locationMap.get(id);
    }
}
