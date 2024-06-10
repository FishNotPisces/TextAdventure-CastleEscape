import java.io.Serializable;
import java.util.UUID;

/**
 * {@code ContainerItem} is a type of {@code Item} that contains another item inside it initially invisible to the player
 * It extends the {@code Item} class and implements {@code Serializable} for potential serialization.
 *
 * @see Item
 * @see Serializable
 */
public class ContainerItem extends Item implements Serializable {
    private Item itemContained;
    private String altDescription="";

    /**
     * First constructor of the class
     *
     * @param name the name of the item
     * @param description the description of the  item
     * @param visible if the container item is visible
     * @param collectable if the container item is collectable
     * @param itemContained the item contained within this container item
     * @param altDescription an alternative description for the container item showed after the first interaction with it
     */
    public ContainerItem(String name, String description, boolean visible, boolean collectable, Item itemContained, String altDescription) {
        super(name, description, visible, collectable);
        this.itemContained = itemContained;
        this.altDescription=altDescription;
    }

    /**
     * Second constructor of the class
     *
     * @param id the unique identifier for the locked item
     * @param name the name of the item
     * @param description the description of the  item
     * @param visible if the container item is visible
     * @param collectable if the container item is collectable
     * @param itemContained the item contained within this container item
     * @param altDescription an alternative description for the container item showed after the first interaction with it
     */
    public ContainerItem(UUID id, String name, String description, boolean visible, boolean collectable, Item itemContained, String altDescription) {
        super(id, name, description, visible, collectable);
        this.itemContained = itemContained;
        this.altDescription = altDescription;
    }

    /**
     * Returns the item contained in the container item
     *
     * @return the item contained in this container item
     */
    public Item getItemContained() {
        return itemContained;
    }

    /**
     * Returns the alternative description
     *
     * @return the alternative description
     */
    public String getAltDescription(){
        return altDescription;
    }

    /**
     * Makes the contained item visible.
     */
    public void setVisibleItemContained(){
        itemContained.setVisible(true);
    }

}