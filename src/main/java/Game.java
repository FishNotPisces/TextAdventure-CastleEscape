public class Game{
    private GameMap map;
    private Player player=null;
    public transient Controller controller=null;
    protected Item lastItemUsed = null;
    private boolean WIN_CONDITION = false;

    /**
     * First and only constructor of the class.Starts a new game.
     * It prints the introduction text first and asks for the name of the player's
     * character and welcomes him / her. After that, it goes to the normal game prompt.
     *
     * @param controller the controller to interact with for the user interface
     * @param nome the name of the player
     */
    public Game(Controller controller, String nome){
        map= new GameMap();
        this.controller=controller;
        createPlayer(nome);
    }

    /**
     * Creates a new player
     *
     * @param nome The name of the player
     */
    public void createPlayer(String nome){
        player=new Player(nome);
        player.setLocation(map.get("hall"));
    }
    /**
     * Returns the player
     *
     * @return the player
     */
    public Player getPlayer(){ return player; }

    /**
     * Returns the map
     *
     * @return the game map
     */
    public GameMap getMap(){
        return map;
    }

    /**
     * Game command: interact
     * Returns the object I interacted with and prints the description of it, unlocking any hidden object
     *
     * @param item object I want to interact with
     * @return object I interacted with
     */
    public Item interact(String item){
        Storage roomStorage = player.getLocation().getStorage();
        Item item1 = null;
        String message = "Item not found";
        if((roomStorage.hasItem(item) && roomStorage.getItem(item).isVisible()) || player.getInventory().hasItem(item)) {
            if(roomStorage.hasItem(item))
                item1 = roomStorage.getItem(item);
            else
                item1 = player.getInventory().getItem(item);
            message=item1.getDescription();
            if (item1 instanceof ContainerItem item2){
                if (item2.getItemContained().isVisible()){
                    message=item2.getAltDescription();
                }
                else
                    item2.setVisibleItemContained();
            }
            else if(item1 instanceof LockedItem item2){
                if(item2.objectsSize()==0)
                    message=item2.getAltDescription();
            }
            String specialCases=specialCases(item1);
            if(!specialCases.isEmpty())   message = message + "\n" + specialCases;
            if (isWinCondition()) message = victory();
        }
        controller.show(message);
        return item1;
    }

    /**
     * Game command: use
     * Returns the object I used and prints a message to say if the use of this object on the one I interacted with was successful
     *
     * @param item object I want to use
     * @return object I used
     */
    public Item use(String item){
        Storage playerStorage = player.getInventory();
        if (playerStorage.getItem(item)==null){
            controller.show(item+ " is not in your inventory");
            return null;
        }
        String message = null;
        Item itemRemoved = null;
        if(playerStorage.hasItem(item))
        {
            Item playerItem = playerStorage.getItem(item);
            String secMessage="";
            if (lastItemUsed instanceof LockedItem lockedItem){
                message= lockedItem.unlockItem(playerItem);
                secMessage=specialCases((LockedItem) lastItemUsed, playerStorage.getItem(item));
            }
            if (message == null && secMessage.isEmpty())
                message = "You can't use this item here";
            else{
                itemRemoved = playerStorage.removeItem(playerItem);
                if (message==null)
                    message=secMessage;
                else
                    message+="\n"+secMessage;
                specialCases(itemRemoved);
            }
        }
        controller.show(message);
        return itemRemoved;
    }

    /**
     * Handles some special cases
     *
     * @param item object I used or object I interacted with
     * @return message
     */
    private String specialCases(Item item)
    {
        Throwable t = new Throwable();
        StackTraceElement[] elements = t.getStackTrace();
        if(item.getName().equals("gloves") || item.getName().equals("hammer") && elements[1].getMethodName().equals("use"))
            player.getInventory().add(item);

        if(item.getName().equals("tray"))
        {
            if(!player.getInventory().hasItem("gloves"))
            {
                player.loseLifePoints(1);
                return "You lost a life point because you didn't use gloves, the oven tray was glowing";
            }
        }

        if(item.getName().equals("food"))
        {
            player.loseLifePoints(1);
            return "You lost a life point because you ate rotten food";
        }

        if(item.getName().equals("gateway") && player.getLocation().getStorage().getItem("chichibio").isVisible())
            return victory();

        return "";
    }
    /**
     * Handles some special cases
     *
     * @param lastItemUsed object I interacted with
     * @param item object I used
     * @return message
     */
    private String specialCases(LockedItem lastItemUsed, Item item)
    {
        if (lastItemUsed.getName().equals("door") || lastItemUsed.getName().equals("gate"))
        {
            Location location = (Location) lastItemUsed.getItemBlocked();
            if (location.isUnlocked())
                lastItemUsed.setVisible(false);
        }

        if (lastItemUsed.getName().equals("right-cell"))
        {
            Item itemBlocked= (Item) lastItemUsed.getItemBlocked();
            if (itemBlocked.getName().equals("claudio"))
            {
                itemBlocked.setVisible(false);
                if (!player.getInventory().hasItem("sword"))
                {
                    player.loseLifePoints(2);
                    return "You just lost two life points because you didn't have the sword to defend yourself";
                }
                else
                    return "Luckily you had the sword to defend yourself!!!";
            }
        }

        if(item.getName().equals("hammer") && (lastItemUsed.getName().equals("gate") || lastItemUsed.getName().equals("computer") || lastItemUsed.getName().equals(("machine"))))
        {
            player.loseLifePoints(1);
            return "You lost a life point because you damaged the " + lastItemUsed.getName()+ ". Remember, brute force is not always the solution!";
        }
        return "";
    }

    /**
     * Game command: help
     * Shows all possible commands
     */
    public void help(){
        controller.show("""
                The possible commands are:
                - look around: to show the list of items in the room
                - go: to show the rooms/directions where you can move to
                - go <direction> or go <room>: to move in the indicated room/direction, if possible
                - go back: to move back to the previous room
                - interact <item>: to show more information about the item
                - use <item>: to use the item with the object you previously interacted with, call this command only after interact
                - pick <item>: to collect the item and keep it in the inventory
                - drop <item>: to drop an item from the inventory
                - status: to show your life points and inventory
                - save: to save the game
                - exit: to exit the game
                - restart: to restart the game""");
    }

    /**
     * Game command: go
     * Moves the player in the selected room/direction or shows the possible direction
     *
     * @param direction room/direction/null
     * @return the room where I want to go
     */
    public Location go(String direction){
        if (direction == null)
        {
            controller.show(map.printDirections(player.getLocation()));
        }
        else if (direction.equals("back"))
        {
            Location backLocation = player.popLastLocation();
            if (backLocation!= null){
                player.setLocation(backLocation);
                controller.showImage(backLocation.getName());
                controller.show(backLocation.getDescription());
            } else
                controller.show("This is the first location you visited, you cannot go back anymore");
            return backLocation;
        }
        else
        {
            Location newLocation = map.getExit(player.getLocation(),direction);
            if (newLocation == null || !newLocation.isUnlocked())
            {
                controller.show("Direction not valid");
                return null;
            }
            player.setLocation(newLocation);
            controller.showImage(newLocation.getName());
            controller.show(newLocation.getDescription());
            return newLocation;
        }
        return null;
    }

    /**
     * Closes the game windows
     */
    public void exit(){
        controller.close();
    }

    /**
     * Saves the last object I dealt with
     *
     * @param item the last object I dealt with
     */
    public void updateLastItem(String item)
    {
        Storage playerStorage = player.getInventory();
        if (playerStorage.hasItem(item))
            lastItemUsed=playerStorage.getItem(item);
        else
            lastItemUsed=player.getLocation().getStorage().getItem(item);
    }

    /**
     * Handles the game after victory event
     *
     * @return message
     */
    public String victory() {
        this.WIN_CONDITION = true;
        controller.showImage("victory");
        controller.show("");
        return  "YOU WON! \nCongratulations, you escaped the castle, now you're free!\n\nEnter: restart or exit";

    }

    /**
     * Returns if the player achieved the winning condition or not
     *
     * @return if the player achieved the winning condition or not
     */
    public boolean isWinCondition() {
        return WIN_CONDITION;
    }
}