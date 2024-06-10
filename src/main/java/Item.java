import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.io.Serializable;
import java.util.*;

/**
 * Item class represents a basic item in the game
 *
 * @see java.io.Serializable
 * @see java.io.ObjectOutputStream
 * @see java.io.ObjectInputStream
 *
 * @serial Every Item has a unique UUID id that allow to avoid duplicates
 *
 */
public class Item implements Serializable {
    private final String name;
    private final String description;
    private boolean visible;
    private boolean collectable;
    /**
     * It is used during the serialization/deserialization process
     */
    private final UUID id;

    /**
     * First constructor of the class
     *
     * @param name name of the item. The player interacts with the item with this name
     * @param description description of the item. It will show with interact command
     * @param visible indicates if the item is visible to player or not
     * @param collectable indicates if the item can be picked up by the player
     */
    public Item(String name, String description, boolean visible, boolean collectable) {
        id=UUID.randomUUID();
        this.name = name;
        this.description = description;
        this.visible = visible;
        this.collectable = collectable;

        Registry.registerItem(this);
    }

    /**
     * Second constructor of the class
     *
     * @param id unique id for every item
     * @param name the name of the item. The player interacts with the item with this name
     * @param description description of the item. It will show with interact command
     * @param visible indicates if the item is visible to player or not
     * @param collectable indicates if the item can be picked up by the player
     */
    public Item(UUID id, String name, String description, boolean visible, boolean collectable) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.visible = visible;
        this.collectable = collectable;

        // Register the item in the registry upon creation
        Registry.registerItem(this);
    }

    /**
     * Returns the name of the item
     *
     * @return the name of the item
     */
    public String getName() {
        return name.toLowerCase();
    }

    /**
     * Returns the description of the item
     *
     * @return the description of the item
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether the player can pick up the item or not
     *
     * @return if the player can pick up the item or not
     */
    public Boolean isCollectable() {
        return collectable;
    }

    /**
     * Returns whether the item is visible to the player or not
     *
     * @return if the item is visible to the player or not
     */
    public Boolean isVisible() {
        return visible;
    }

    /**
     * Sets the visibility of the item to the specified value.
     *
     * @param visible the new visibility state of the item
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * Returns the id of the item
     *
     * @return the id of the item
     */
    public UUID getId() {
        return id;
    }

    /**
     * {@code ItemSerializer} is a custom serializer for Item objects, implementing the
     * {@code JsonSerializer<Item>} interface from Gson. This serializer converts {@code Item} instances
     * into their JSON representation.
     *
     * @see JsonSerializer
     * @see Item
     * @see ContainerItem
     * @see LockedItem
     */
    public static class ItemSerializer implements JsonSerializer<Item> {

        /**
         * Serializes an {@code Item} object into its JSON representation.
         *
         * @param item the item to be serialized
         * @param typeOfSrc the actual type of the source object
         * @param context the context of the serialization process
         * @return a {@code JsonElement} representing the serialized form of the {@code Item}
         */
        @Override
        public JsonElement serialize(Item item, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("id", item.getId().toString());
            jsonObject.addProperty("name", item.getName());
            jsonObject.addProperty("description", item.getDescription());
            jsonObject.addProperty("visible", item.isVisible());
            jsonObject.addProperty("collectable", item.isCollectable());


            if (item instanceof ContainerItem) {
                jsonObject.addProperty("type", "ContainerItem");
                ContainerItem containerItem = (ContainerItem) item;
                jsonObject.add("itemContained", context.serialize(containerItem.getItemContained()));
                jsonObject.addProperty("altDescription", containerItem.getAltDescription());
            } else if (item instanceof LockedItem) {
                jsonObject.addProperty("type", "LockedItem");
                LockedItem lockedItem = (LockedItem) item;
                jsonObject.add("objects", context.serialize(lockedItem.getObjects()));

                    if (lockedItem.getItemBlocked() instanceof Item) {
                        jsonObject.addProperty("itemBlockedType", "Item");
                        jsonObject.add("itemBlocked", context.serialize(lockedItem.getItemBlocked(), Item.class));
                    } else if (lockedItem.getItemBlocked() instanceof Location) {
                        jsonObject.addProperty("itemBlockedType", "Location");
                        jsonObject.add("itemBlocked", context.serialize(lockedItem.getItemBlocked(), Location.class));
                    } else if (lockedItem.getItemBlocked() instanceof LockedItem) {
                        jsonObject.addProperty("itemBlockedType", "LockedItem");
                        jsonObject.add("itemBlocked", context.serialize(lockedItem.getItemBlocked(), LockedItem.class));
                    }

                    jsonObject.addProperty("action", lockedItem.getAction());
                    jsonObject.addProperty("altDescription", lockedItem.getAltDescription());
                } else {
                    jsonObject.addProperty("type", "Item");
                }

                return jsonObject;
            }
        }

    /**
     * {@code ItemDeserializer} is a custom deserializer for {@code Item} objects, implementing the
     * {@code JsonDeserializer<Item>} interface from Gson. This deserializer converts JSON data into
     * {@code Item} instances.
     *
     * @see JsonDeserializer
     * @see Item
     * @see ContainerItem
     * @see LockedItem
     * @see Registry
     */
    public static class ItemDeserializer implements JsonDeserializer<Item> {
        /**
         * Deserializes a JSON element into an {@code Item} object.
         *
         * @param json the JSON data being deserialized
         * @param typeOfT the type of the object to deserialize to
         * @param context the context of the deserialization process
         * @return an {@code Item} object deserialized from the JSON data
         * @throws JsonParseException if the JSON data is invalid or cannot be deserialized
         */
            @Override
            public Item deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context)
                    throws JsonParseException {
                JsonObject jsonObject = json.getAsJsonObject();
                UUID id = UUID.fromString(jsonObject.get("id").getAsString());
                String type = jsonObject.get("type").getAsString();
                String name = jsonObject.get("name").getAsString();
                String description = jsonObject.get("description").getAsString();
                boolean visible = jsonObject.get("visible").getAsBoolean();
                boolean collectable = jsonObject.get("collectable").getAsBoolean();

                // Check if the item already exists in the registry
                Item existingItem = Registry.getItem(id);
                if (existingItem != null) {
                    return existingItem;
                }

                Item item;

                if ("ContainerItem".equals(type)) {
                    Item itemContained = context.deserialize(jsonObject.get("itemContained"), Item.class);
                    String altDescription = jsonObject.get("altDescription").getAsString();
                    item = new ContainerItem(id, name, description, visible, collectable, itemContained, altDescription);
                } else if ("LockedItem".equals(type)) {
                    List<Item> objects = context.deserialize(jsonObject.get("objects"), new TypeToken<List<Item>>(){}.getType());
                    String itemBlockedType = jsonObject.get("itemBlockedType").getAsString();
                    Object itemBlocked;

                    switch (itemBlockedType) {
                        case "Item":
                            itemBlocked = context.deserialize(jsonObject.get("itemBlocked"), Item.class);
                            break;
                        case "Location":
                            itemBlocked = context.deserialize(jsonObject.get("itemBlocked"), Location.class);
                            break;
                        case "LockedItem":
                            itemBlocked = context.deserialize(jsonObject.get("itemBlocked"), LockedItem.class);
                            break;
                        default:
                            throw new JsonParseException("Unknown itemBlocked type: " + itemBlockedType);
                    }

                    String action = jsonObject.get("action").getAsString();
                    String altDescription = jsonObject.get("altDescription").getAsString();
                    item = new LockedItem(id, name, description, visible, collectable, objects, itemBlocked, action, altDescription);
                } else {
                    item = new Item(id, name, description, visible, collectable);
                }

                Registry.registerItem(item);
                return item;
            }
        }
    }
