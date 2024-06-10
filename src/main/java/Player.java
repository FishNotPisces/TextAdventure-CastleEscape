import java.util.Stack;

/**
 *  The class is used to create the {@code Player}
 *
 * @see Location
 * @see Storage
 * @see Item
 */
public class Player{
    public String name;
    private Location location;
    private Storage inventory;
    private int lifePoints=3;
    private Stack<Location> lastLocations = new Stack<> ();

    /**
     * First and only constructor of the class
     *
     * @param name The name of the player
     */
    public Player(String name){
        this.name=name;
        inventory=new Storage(2);
    }

    /**
     * Returns the player's name
     *
     * @return name
     */
    public String getName(){
        return name;
    }

    /**
     * Returns player's inventory
     *
     * @return inventory
     */
    public Storage getInventory(){return inventory;}

    /**
     * Returns the room I am
     *
     * @return current room
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Sets the room where I am
     *
     * @param newLocation new room where I want to go
     */
    public void setLocation(Location newLocation) {
        lastLocations.push(newLocation);
        location = newLocation;
    }

    /**
     * Returns the room I was in before
     *
     * @return previous room
     */
    public Location popLastLocation(){
        if (lastLocations.size()>=2){
            Location current = lastLocations.pop();         //current location
            return lastLocations.pop();
        }
        else
            return null;
    }

    /**
     * If I do something wrong I lose life points
     *
     * @param d life points to lose
     */
    public void loseLifePoints(int d){
        lifePoints-=d;
    }

    /**
     * Return the player's life points
     *
     * @return life points
     */
    public int getLifePoints(){return lifePoints;}

    /**
     * Returns the life points and the items in the inventory
     *
     * @return message
     */
    public String status(){
        if (getInventory().isEmpty())
            return "You have " + lifePoints + " life points\n" + "Your inventory is empty";
        return "You have " + lifePoints + " life points\n" + "In your inventory you have:\n" + getInventory().printItems();
    }

    /**
     * Game command: pick
     * Returns the item I picked up and placed in my inventory, removing it from room's storage
     *
     * @param item object I want to pick
     * @return item picked up
     */
    public Item pick(String item){
        Storage roomStorage = location.getStorage();
        Item object = null;
        if (roomStorage.hasItem(item) && (getInventory().getWeight()<getInventory().getMaxWeight()) && roomStorage.getItem(item).isVisible() && roomStorage.getItem(item).isCollectable())
        {
            object = specialCases(roomStorage.getItem(item));
            if (object!=null){
                roomStorage.remove(item);
                inventory.add(object);
            }
        }
        return object;
    }

    /**
     * Handles some special cases
     *
     * @param item object I picked
     * @return message
     */
    private Item specialCases(Item item)
    {
        //se raccogli l'acido senza guanti perdi 1 vita
        if(item.getName().equals("acid"))
        {
            if(!inventory.hasItem("gloves"))
            {
                loseLifePoints(1);
                return null;
            }
        }
        return item;
    }

    /**
     * Game command: drop
     * Returns the item I dropped and removed from my inventory, placing it in the current room's storage
     *
     * @param item object that I want to drop
     * @return the item dropped
     */
    public Item drop(String item){
        Storage roomStorage = location.getStorage();
        Item object = null;
        if (inventory.hasItem(item))
        {
            object = inventory.getItem(item);
            inventory.remove(item);
            roomStorage.add(object);
        }
        return object;
    }

    /**
     * Game command: look around
     * Returns the list of the object in the current room
     *
     * @return message
     */
    public String  lookAround(){
        return "You see: \n"+location.getStorage().printItems();
    }
}