import java.util.*;

/**
 * {@code Storage} is the storage used in every Location and for the player's inventory
 *
 * @see Item
 */
public class Storage{
    public final static double WEIGHT_UNLIMITED = 15;
    private final double maxWeight;
    private Map<String, List<Item>> stor;

    /**
     * First constructor used for the Location as it create an unlimited weight storage
     */
    public Storage() {
        this(WEIGHT_UNLIMITED);
    }

    /**
     * Second constructor used for the player's inventory
     *
     * @param maxWeight the maximum weight of the storage
     */
    public Storage(double maxWeight) {
        this.maxWeight = maxWeight;
        this.stor = new HashMap<>();
    }

    /**
     * Returns the maximum weight of the storage
     *
     * @return the maximum weight of the storage
     */
    public double getMaxWeight() {
        return maxWeight;
    }

    /**
     * Returns how many items are stored in the storage
     *
     * @return how many items are stored in the storage
     */
    public int getWeight() {
        int weight = 0;
        for(String key : stor.keySet()) {
            weight += stor.get(key).size();
        }
        return weight;
    }

    /**
     * Check if the item is contained in the storage
     *
     * @param itemName the name of the item
     * @return {@code true} if the storage as a key equal to itemName, {@code false} otherwise
     */
    public boolean hasItem(String itemName) {
        return stor.containsKey(itemName);
    }

    /**
     * Adds a new item to the storage
     *
     * @param item the item to add
     * @return the {@code item} added if the operation ended correctly, {@code null} otherwise
     */
    public Item add(Item item) {
        if (getWeight() < maxWeight) {
            if (stor.containsKey(item.getName())) {
                stor.get(item.getName()).add(item);
            }
            else{
                List<Item> items = new ArrayList<>();
                items.add(item);
                stor.put(item.getName(), items);
            }
            return item;
        }
        return null;
    }

    /**
     * Removes the item from the storage
     *
     * @param itemName the name of the item to remove
     * @return the removed {@code Item}, or {@code null} if no items with the specified name exist
     */
    public Item remove(String itemName) {
        if (!stor.containsKey(itemName)) {
            return null;
        }
        List<Item> items = stor.get(itemName);
        Item removed=items.removeLast();
        if (items.isEmpty())
            stor.remove(itemName);
        return removed;
    }

    /**
     * Removes the item from the storage
     *
     * @param item the item to remove
     * @return the removed {@code Item}, or {@code null} if {@code item} does not exist
     */
    public Item removeItem(Item item) {
        String name = item.getName();
        if (!stor.containsKey(name)) {
            return null;
        }
        List<Item> items = stor.get(name);
        Item removed=null;
        for (int i=0; i< items.size(); i++){
            if (items.get(i).getId().equals(item.getId()))
                removed = items.remove(i);
        }
        if (items.isEmpty())
            stor.remove(name);
        return removed;
    }

    /**
     * Returns whether the storage is empty or not
     *
     * @return {@code true} if the storage is empty, {@code false} otherwise
     */
    public boolean isEmpty() {
        return this.stor.isEmpty();
    }

    /**
     * Returns the item if it is contained in the storage
     *
     * @param itemName the name of the item to get
     * @return {@code item} if it exists in the storage, {@code null} otherwise
     */
    public Item getItem(String itemName) {
        if (!stor.containsKey(itemName))
            return null;
        List<Item> items = stor.get(itemName);
        return items.getFirst();
    }

    /**
     * Returns all the items stored in the storage
     *
     * @return all the items stored in the storage
     */
    public String printItems() {
        if (this.stor.isEmpty()) {
            return "No items in here, search somewhere else...";
        }
        StringBuilder content = new StringBuilder();
        for (String key : stor.keySet()){
            for (Item item : stor.get(key)) {
                if (item.isVisible())
                    content.append("- ").append(item.getName()).append("\n");
            }
        }
        return content.toString();
    }
}