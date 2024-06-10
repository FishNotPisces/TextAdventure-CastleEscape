import java.util.ArrayList;
import java.util.List;

/**
 * {@code GameMap} is a list of the rooms of the map. Here all the {@code Item} and all
 * the {@code Location} are initialized.
 *
 * @see Item
 * @see ContainerItem
 * @see LockedItem
 * @see Location
 * @see Storage
 */
public class GameMap {
    private List<Location> rooms;

    /**
     *  First and only constructor of the class. Calls the method createRooms.
     */
    public GameMap() {
        createRooms();
    }

    /**
     * It returns the location if it exists
     *
     * @param nameLocation the name of the location
     * @return the Location if it exists, otherwise null
     */
    public Location get(String nameLocation){
        for (Location loc : rooms)
            if (loc.getName().equals(nameLocation))
                return loc;
        return null;
    }

    /**
     * It returns the new location if the direction provided by the player is allowed
     *
     * @param location The location the player is in
     * @param direction The direction the player wants to go
     * @return the new location if the direction is allowed, otherwise null
     */
    public Location getExit(Location location, String direction){
        if (location.getExit().contains(direction)){
            String dir=translateCoordinate(location.getName(), direction);
            return get(dir);
        }
        return null;
    }

    /**
     * The method translate del cardinal direction into the name of the location based on the location the player is in
     *
     * @param position The position of the player as the name of the room
     * @param direction The direction the player wants to go, either as a cardinal direction or name of the room
     * @return The new Location as the name of the room
     */
    private String translateCoordinate(String position, String direction){
        if (position.equals("studio") || position.equals("kitchen") || position.equals("lab") || position.equals("dungeons"))
            return "hall";
        else{
            switch (direction){
                case "north" : return "dungeons";
                case "south" : return "lab";
                case "east" : return "kitchen";
                case "west" : return "studio";
            }
        }
        return direction;
    }

    /**
     * It prints the directions the player can go from the location is in
     *
     * @param location The Location the player is in
     * @return All the possible directions the player can go from that position
     */
    public String printDirections(Location location) {
        String direction="You can go:\n";
        for (String exit : location.getExit()) {
            String exitTranslated=translateCoordinate(location.getName(),exit);
            if (get(exitTranslated).isUnlocked())
                direction += exit + "\n";
        }
        return direction;
    }

    /**
     *  Initializes all the Location and all the Object of the map
     */
    private void createRooms() {

        String hallDescription = "You are now in the hall. The medieval castle hall, illuminated by sparkling " +
                "chandeliers, glows with precious antiques, creating a magical and regal atmosphere.";
        String studioDescription = "You are now in the studio. The medieval castle studio, with brick walls and " +
                "shelves full of dusty books, is a perfect refuge for moments of reflection.";
        String kitchenDescription = "You are now in the kitchen. The medieval castle kitchen, animated by blazing " +
                "braziers and steaming pots, smells of freshly baked bread and exotic spices, offering a cosy rustic atmosphere";
        String labDescription = "You are now in the lab. The medieval castle workshop, full of glittering stills and " +
                "sparkling potions, is a fascinating place where science meets magic.";
        String dungeonDescription = "You are now in the dungeons. The medieval castle dungeons, with their eerie shadows and rusty " +
                "chains, exude a dark fascination, whispering stories of mysteries and adventures lost in time";

        Storage storageHall = new Storage();
        Storage storageStudio = new Storage();
        Storage storageKitchen = new Storage();
        Storage storageLab = new Storage();
        Storage storageDungeon = new Storage();

        List<String>hallExits= new ArrayList<>();
        List<String>studioExits= new ArrayList<>();
        List<String>kitchenExits = new ArrayList<>();
        List<String>labExits = new ArrayList<>();
        List<String>dungeonExits = new ArrayList<>();

        Location hall = new Location("hall", hallDescription, storageHall, hallExits, true);
        Location studio = new Location("studio", studioDescription, storageStudio, studioExits, true);
        Location kitchen = new Location("kitchen", kitchenDescription, storageKitchen, kitchenExits, true);
        Location lab = new Location("lab",labDescription,storageLab,labExits,false);
        Location dungeons = new Location("dungeons", dungeonDescription, storageDungeon, dungeonExits, false);


        Item note = new Item("note","You are trapped in my castle, you are now in the hall. This room has four doors, but two of them are locked. To the east there is the kitchen, to the west the study. The north gate and the south door are locked. Use your wits to solve the puzzles and find a way out. I wish you good luck, you'll need it.\nEnter \"help\" to see all the commands", true, true );
        Item armour = new Item("armour","A medieval armour is placed next to the gate. Intrigued, you get closer " +
                " and all of a sudden it starts talking: \n\"To go through this door, you need something keen,\n" +
                "a substance that burns, though not often seen.\n" +
                "It can wear down the strong, leave its mark in the flash,\n" +
                "though it's clear and quite common, it makes metals clash\"\nIt sounds like a riddle, what does that mean?", true, false );
        Item candlestick = new Item("Candlestick","A gold candlestick with 3 candles rests over the table. " +
                "The light it produces is not much but could come in handy", true, true);
        Item painting = new Item("painting","The painting depicts the owner of the castle",true,false);
        List<Item> itemsDoor = new ArrayList<>();
        LockedItem door = new LockedItem("door",  "In the south facing wall there is an armored door. " +
                "There are 2 locks. Maybe the exit is back here... but you need 2 keys",
                false, true, itemsDoor, lab, "Good job, you found both the keys and opened the door. You just opened" +
                " a way to the lab","The door is opened" );
        List<Item> itemsGate = new ArrayList<>();
        LockedItem gate = new LockedItem("gate", "In the north facing wall there is a large and powerful " +
                "door of solid wood. The gear is locked and there is no way to open it... You have to find an alternative way to " +
                "go through", false, true, itemsGate, dungeons, "Good job, you figured out the solution to the " +
                "riddle and managed to synthesise the acid and now you have created a passage to the castle dungeons","The gate is opened");

        Item leaflet = new Item("leaflet", "The sheet has only one writing in the center, H2SO4. It looks like some kind of code... but who knows? " +
                "To be kept with such care it must surely have a very important meaning", false, true);
        ContainerItem drawer = new ContainerItem("drawer", "The last drawer at the bottom of the desk is open. " +
                "Inside there is a leaflet",true,false, leaflet, "You already opened this drawer, " +
                "nothing new magically appeared.");
        Item key1 = new Item("key", "The key has a modern look. It seems to be the key of an armored door...",false,true);
        itemsDoor.add(key1);
        ContainerItem carpet= new ContainerItem("carpet","A beautiful persian carpet covers much of the studio floor. Looking for clues you " +
                "decide to raise it and with great surprise you find a mysterious key", true,false, key1,"A beautiful persian carpet covers much of the studio floor.");
        Item sword = new Item("sword", "Hanging on the wall is a sword whose nameplate reads: \"The legend says that this is the sword belonged " +
                "to King Artu', no sword is sharper and brighter\"\nWell, at least you have something to defend yourself with.", true, true);
        Item crane = new Item("crane", "An embalmed crane is standing on a pedestal. Its white and grey feathers and sharp beak make it an elegant hunter." +
                "Such an odd animal to keep in your studio, maybe it had some special meaning to the owner", true, false);
        Item key2 = new Item("key", "The key has a modern look. It seems to be the key of an armored door...",
                false, true);
        itemsDoor.add(key2);

        ContainerItem soup = new ContainerItem("soup", "In the middle of the kitchen table there is a soup that smells great. Tempted by " +
                "its smell you taste it and spoon after spoon you finish it. On the bottom of the empty plate you find a key", true, false, key2, "Your already" +
                " ate the soup!!");
        Item recipe = new Item("recipe","\"1 Gru\n2 cloveS of garlic\n1 sprig of rosemarY\nsalt and Pepper\neXtra virgin olive oil\n1 glass of wHite" +
                " wine\n1 Lemon\n1 tablespoon of Apples\"\n What a weird choice of uppercase letters, I wonder if they mean something...",false,true);
        ContainerItem cookbook = new ContainerItem("cookbook","What an amazing discovery, the cook’s recipe book. Among the many, " +
                "a recipe in particular arouses your attention.",true,false, recipe, "The cook's recipe book contains many " +
                "recipes of the best dishes in the world to satisfy the fine taste of the castle owner");
        Item tray = new Item("tray","Inside the oven a baking tray contains a baked crane, the smell is delicious!",false,false);
        ContainerItem oven = new ContainerItem("oven", "Intrigued by the smell in the air you approach the oven. Looking inside you see a baking tray. Because it " +
                "looks so good and smells so incredible, you decide to open the oven to inspect it.", true, false, tray,"The oven has been open since " +
                "the last time you opened it, remember to use something not to burn yourself");
        Item food = new Item("food","It definitely doesn't taste the best...",false,false);
        ContainerItem pantry = new ContainerItem("pantry","You expect the kitchen pantry to have an endless supply of the most sought-after ingredients from all over the world " +
                "but instead you only find a bag of bad-smelling food",true,
                false,food,"Well, all the same here since the last check");

        Item skull = new Item("skull", "Leaning against a shelf there is a skull in plain sight. It is the skull of the legendary Hamlet, King of Denmark. ", true, true);
        Item gloves = new Item("gloves","Ordinary-looking laboratory gloves, white in colour and made of a very durable material. ", false, true);
        ContainerItem drawer1 = new ContainerItem("drawer", "Inside one of the many drawer in the lab you will find some " +
                "safety devices, including gloves. ",true,false, gloves, "You already opened this drawer, " +
                "nothing new appeared magically.");
        Item hammer = new Item("hammer","The hammer looks powerful and seems to be very heavy, it will be useful if you need to break something.",true, true);
        Item acid = new Item("acid","The bottle contains a clear, odourless liquid that is still steaming. Sulphuric acid has a powerful " +
                "corrosive action, and must be handled with care.",false,true);
        itemsGate.add(acid);
        List<Item> itemsMachine = new ArrayList<>();
        itemsMachine.add(leaflet);
        LockedItem machine = new LockedItem("machine","The laboratory machine seems to be very complex, it has thousands of tubes and " +
                "as many steaming test tubes of every possible colour. The machine is used to create chemicals but requires the formula, maybe it is writter somewhere",false,false,itemsMachine,acid,"What an insight, " +
                "you realised that H2SO4 is chemical formula of sulphuric acid. The machine just synthesized a test tube of it.","You can synthesise other chemicals if you find other formulas around the castle.");
        List<Item> itemsPC= new ArrayList<>();
        itemsPC.add(recipe);
        LockedItem computer = new LockedItem("computer","A modern-looking computer sits on top of the table. Intrigued by such a modern object you decide " +
                "to open it, but to your misfortune it requires a password, maybe it is written on some piece of paper.",false,true,itemsPC,machine,"Well done, the password is" +
                " correct. The computer has activated some gears and from the centre of the table rises a strange looking-machine.","You have already entered the password, " +
                "you can now use the machine");

        Item chichibio = new Item("Chichibio","\"We'd better find a way out before someone come back...\"",false,false);
        Item claudio = new Item("Claudio","",false, false);
        List<Item> itemsLeftCell= new ArrayList<>();
        itemsLeftCell.add(hammer);
        List<Item> itemsRightCell= new ArrayList<>();
        itemsRightCell.add(hammer);
        LockedItem leftCell = new LockedItem("left-cell","Inside the cell there is Chichibio " +
                "who introduces himself: \"I am a Venetian cook, I am accused of eating a leg of the crane intended for my master, " +
                "but I am innocent, all he had to do was shout loudly and the second leg would come out, please help me\"",false,true,itemsLeftCell,chichibio,
                "Well done, you freed Chichibio and now he can help you get out of this castle","You have already opened the cell, it is now empty");
        LockedItem rightCell = new LockedItem("right-cell","Inside the cell is Claudius, Hamlet's uncle, who introduces himself: \"my nephew " +
                "Hamlet has accused me of killing my brother, but I am innocent, you must help me\".",false,true,itemsRightCell,claudio,"Claudius is a " +
                "shrewd and ambitious character whose thirst for power leads him to commit nefarious acts. You open his cell and immediately he challenges you to a duel, after " +
                "which he disappears into the darkness. ","You have already opened the cell, it is now empty");
        Item gateway = new Item("gateway","A huge, mighty-looking gateway is blocked by thoughtful gears. The exit might be hiding behind it," +
                " you need someone to help you turn the rusty gears, but choose well.",true,false);


        hallExits.add("kitchen");
        hallExits.add("studio");
        hallExits.add("lab");
        hallExits.add("dungeons");
        kitchenExits.add("hall");
        studioExits.add("hall");
        labExits.add("hall");
        dungeonExits.add("hall");

        hallExits.add("east");
        hallExits.add("west");
        hallExits.add("south");
        hallExits.add("north");
        kitchenExits.add("west");
        studioExits.add("east");
        labExits.add("north");
        dungeonExits.add("south");


        storageHall.add(note);
        storageHall.add(armour);
        storageHall.add(candlestick);
        storageHall.add(painting);
        storageHall.add(door);
        storageHall.add(gate);

        storageStudio.add(leaflet);
        storageStudio.add(drawer);
        storageStudio.add(key1);
        storageStudio.add(carpet);
        storageStudio.add(sword);
        storageStudio.add(crane);

        storageKitchen.add(key2);
        storageKitchen.add(soup);
        storageKitchen.add(recipe);
        storageKitchen.add(cookbook);
        storageKitchen.add(oven);
        storageKitchen.add(tray);
        storageKitchen.add(pantry);
        storageKitchen.add(food);

        storageLab.add(skull);
        storageLab.add(gloves);
        storageLab.add(drawer1);
        storageLab.add(hammer);
        storageLab.add(acid);
        storageLab.add(machine);
        storageLab.add(computer);

        storageDungeon.add(chichibio);
        storageDungeon.add(claudio);
        storageDungeon.add(leftCell);
        storageDungeon.add(rightCell);
        storageDungeon.add(gateway);


        rooms = new ArrayList<>();
        rooms.add(hall);
        rooms.add(studio);
        rooms.add(kitchen);
        rooms.add(lab);
        rooms.add(dungeons);
    }
}
