import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

/**
 * {@code Location} represents a room in the game. It contains details about the location,
 * such as its name, description, whether it is unlocked, the storage it contains, and the exits leading to other locations.
 * It extends the {@code Serializable} interface for serialization.
 *
 * @see Serializable
 * @see Storage
 * @see Registry
 */
public class Location implements Serializable {
    private final String name;
    private String description;
    private boolean unlocked;
    private Storage storage = new Storage();
    private List<String> exits;
    private final UUID id;

    /**
     * First constructor of the class
     *
     * @param name the name of the location
     * @param description the description of the location
     * @param storage the storage associated with the location
     * @param exits the list of exits
     * @param unlocked if the location is unlocked
     */
    public Location(String name, String description, Storage storage, List<String> exits, boolean unlocked) {
        id= UUID.randomUUID();
        this.name = name;
        this.description = description;
        this.storage = storage;
        this.exits = exits;
        this.unlocked = unlocked;

        Registry.registerLoc(this);
    }

    /**
     * Second constructor of the class
     *
     * @param id unique id for every location
     * @param name the name of the location
     * @param description the description of the location
     * @param storage the storage associated with the location
     * @param exits the list of exits
     * @param unlocked if the location is unlocked
     */
    public Location(UUID id, String name, String description, Storage storage, List<String> exits, boolean unlocked) {
        this.id=id;
        this.name = name;
        this.description = description;
        this.storage = storage;
        this.exits = exits;
        this.unlocked = unlocked;

        Registry.registerLoc(this);
    }

    /**
     * Returns the name of the location
     *
     * @return the name of the location
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the description of the Location
     *
     * @return the description of the Location
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the List of the exits from this Location
     *
     * @return the List of the exits from this Location
     */
    public List<String> getExit() {
        return exits;
    }

    /**
     * Returns the Storage of the location
     *
     * @return the Storage of the location
     */
    public Storage getStorage() {
        return storage;
    }

    /**
     * Returns whether the location is locked or unlocked
     *
     * @return {@code true} if the location is unlocked, {@code false} otherwise
     */
    public boolean isUnlocked() {
        return unlocked;
    }

    /**
     * Unlocks the location
     */
    public void setUnlocked(){
        if(!unlocked)
            unlocked = true;
    }

    /**
     * Returns the id of the location
     *
     * @return the id of the location
     */
    public UUID getId() {
        return id;
    }

    /**
     * A custom serializer for the {@code Location} class.
     * This class is responsible for converting a {@code Location} object into its JSON representation.
     *
     * @see JsonSerializer
     * @see Location
     */
    public static class LocationSerializer implements JsonSerializer<Location> {
        /**
         * Serializes a {@code Location} object into its equivalent JSON representation.
         *
         * @param location the {@code Location} object to serialize
         * @param typeOfSrc the actual type of the source object
         * @param context the context of the serialization process
         * @return a {@code JsonElement} representing the serialized form of the {@code Location}
         */
        @Override
        public JsonElement serialize(Location location, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("id", location.getId().toString());
            jsonObject.addProperty("name", location.getName());
            jsonObject.addProperty("description", location.getDescription());
            jsonObject.addProperty("unlocked", location.isUnlocked());
            jsonObject.add("storage", context.serialize(location.getStorage()));
            jsonObject.add("exits", context.serialize(location.getExit()));

            return jsonObject;
        }
    }

    /**
     * A custom deserializer for the {@code Location} class.
     * This class is responsible for converting a JSON representation of a {@code Location} object back into a {@code Location} instance.
     *
     *  @see JsonDeserializer
     *  @see Location
     *  @see Registry
     */
    public static class LocationDeserializer implements JsonDeserializer<Location> {
        /**
         * Deserializes a JSON representation into a {@code Location} object.
         *
         * @param json the JSON data being deserialized
         * @param typeOfT the type of the object to deserialize to
         * @param context the context of the deserialization process
         * @return a {@code Location} object deserialized from the JSON data
         * @throws JsonParseException if the JSON data cannot be properly parsed into a {@code Location}
         */
        @Override
        public Location deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            UUID id = UUID.fromString(jsonObject.get("id").getAsString());
            String name = jsonObject.get("name").getAsString();
            String description = jsonObject.get("description").getAsString();
            boolean unlocked = jsonObject.get("unlocked").getAsBoolean();
            Storage storage = context.deserialize(jsonObject.get("storage"), Storage.class);
            List<String> exits = context.deserialize(jsonObject.get("exits"), new TypeToken<List<String>>(){}.getType());

            // Check if the location already exists in the registry
            Location existingLocation = Registry.getLoc(id);
            if (existingLocation != null) {
                return existingLocation;
            }

            Location location = new Location(id, name, description, storage, exits, unlocked);

            // Register the new location
            Registry.registerLoc(location);
            return location;
        }
    }
}