import java.io.Serializable;
import java.util.List;
import java.util.UUID;

/**
 * {@code LockedItem} is a type of {@code Item} that is initially locked and requires specific items to unlock.
 * It extends the {@code Item} class and implements {@code Serializable} for serialization.
 *
 * @see Item
 * @see Serializable
 * @see ContainerItem
 * @see Location
 */
public class LockedItem extends Item implements Serializable {
    private List<Item> objects;
    private Object itemBlocked;
    private String action = "";
    private String altDescription = "";

    /**
     * First constructor of the class
     *
     * @param name the name of the item
     * @param description the description of the item
     * @param collectable if the item is collectable
     * @param visible if the item is visible
     * @param objects the list of objects required to unlock this item
     * @param itemBlocked the item or location blocked by this item
     * @param action the action showed when the correct item is used to unlock this item
     * @param altDescription an alternative description of the item showed when itemBlocked has been unlocked
     */
    public LockedItem(String name, String description, Boolean collectable, Boolean visible, List<Item> objects, Object itemBlocked, String action, String altDescription){
        super(name, description, visible, collectable);
        this.objects = objects;
        this.itemBlocked = itemBlocked;
        this.action = action;
        this.altDescription = altDescription;
    }

    /**
     * Second constructor of the class
     *
     * @param id the unique identifier for the locked item
     * @param name the name of the locked item
     * @param description the description of the item
     * @param collectable if the item is collectable
     * @param visible if the item is visible
     * @param objects the list of objects required to unlock this item
     * @param itemBlocked the item or location blocked by this item
     * @param action the action showed when the correct item is used to unlock this item
     * @param altDescription an alternative description of the item showed when itemBlocked has been unlocked
     */
    public LockedItem(UUID id, String name, String description, boolean visible, boolean collectable, List<Item> objects, Object itemBlocked, String action, String altDescription){
        super(id, name, description, visible, collectable);
        this.objects = objects;
        this.itemBlocked = itemBlocked;
        this.action = action;
        this.altDescription = altDescription;
    }

    /**
     * Returns the number of objects required to unlock this item
     *
     * @return the number of objects required to unlock this item
     */
    public int objectsSize(){
        return objects.size();
    }

    /**
     * Returns the object from the list required to unlock this item that matches the given name.
     *
     * @param nome the name of the item
     * @return the object that matches the given name, or {@code null} if no match is found
     */
    public Item getObject(String nome){
        for (Item item : objects){
            if (item.getName().equals(nome)){
                return item;
            }
        }
        return null;
    }

    /**
     * Returns the list of objects required to unlock this item
     *
     * @return the list of objects required to unlock this item
     */
    public List<Item> getObjects(){
        return objects;
    }

    /**
     * Returns the alternative description used when the item locked has been unlocked
     *
     * @return the alternative description
     */
    public String getAltDescription(){
        return altDescription;
    }

    /**
     * Returns the action used during the process of unlocking the item
     *
     * @return the action description
     */
    public String getAction(){
        return action;
    }

    /**
     * Removes the specified object from the list of objects required to unlock this item.
     *
     * @param item the object to remove from the list
     */
    public void removeObj(Item item){
        objects.remove(item);
    }

    /**
     * Returns the item or location blocked by this locked item.
     *
     * @return the item or location blocked by this locked item
     */
    public Object getItemBlocked(){
        return itemBlocked;
    }

    /**
     * Unlocks this item using the specified object, making the blocked item visible or the blocked
     * location accessible if all required objects have been used
     *
     * @param item the object used to unlock this item
     * @return a message indicating the result of the unlock attempt
     */
    public String unlockItem(Item item){
        if (item == null || getObject(item.getName()) == null){
            return null;
        }
        if (objectsSize() == 0) {
            return getAltDescription();
        }
        removeObj(item);
        if (objects.isEmpty()) {
            if (itemBlocked instanceof LockedItem lockedItem){
                lockedItem.setVisible(true);
            } else if (itemBlocked instanceof Item item1){
                item1.setVisible(true);
            } else if (itemBlocked instanceof Location location){
                location.setUnlocked();
            }
            return item.getName() + " used successfully\n" + getAction();
        }
        return item.getName() + " used successfully";
    }
}