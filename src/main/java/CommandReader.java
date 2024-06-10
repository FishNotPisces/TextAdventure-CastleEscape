import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The CommandReader class processes user input commands and interacts with the Game and Controller classes.
 *
 * @see Game
 * @see Controller
 */
public class CommandReader
{
    private Game game = null;
    private Controller controller = null;
    protected String lastCommandUsed = "";

    //*************************************************************************************
    //Metodi pubblici
    //*************************************************************************************

    /**
     * Constructor to initialize the CommandReader with a Controller and a Game instance.
     *
     * @param contr the controller to interact with the user interface
     * @param gam   the game instance to control the game logic
     */
    public CommandReader(Controller contr,Game gam)
    {
        controller=contr;
        game=gam;
    }
    /**
     * Reads and processes the user input command.
     *
     * @param text the command input from the user
     */
    public void read(String text)  //lettore input utente
    {
        Object checkCorrectCall = null; //oggetto usato per verificare che le operazioni siano andate a buon fine
        text = text.toLowerCase().trim();
        String[] splitText = text.split(" ");

        if(text.equals("exit") || text.equals("exit game"))
        {
            if(game.isWinCondition() || lastCommandUsed.equals(text))
            {
                game.exit();
            }
            else
            {
                controller.show("Save game?\nEnter save or exit");
                updateLastCommand(text);
            }
        }
        else if(text.equals("restart") || text.equals("restart game"))  {   controller.restart(); }
        else if (game.isWinCondition()) {controller.show("The game is come to an end. \nEnter: restart or exit");}
        else if(text.equals("help"))   {   game.help();    }
        else if(text.equals("save") || text.equals("save game") || text.equals("sv"))
        {
            if(lastCommandUsed.equals("exit"))
            {
                controller.save();
                game.exit();
            }
            else    {   controller.save();  }

        }
        else if(text.equals("status") || text.equals("st")){
            controller.show(game.getPlayer().status());
        }
        else if(text.equals("look") || text.equals("look around") || text.equals("lk"))
        {
            controller.show(game.getPlayer().lookAround());  //stampa la descrizione della stanza
            updateLastCommand(splitText[0]);    //aggiorna ultimo comando usato
        }
        else if(splitText[0].equals("go"))
        {
            if(text.equals("go"))   {   controller.show(game.getMap().printDirections(game.getPlayer().getLocation())); }
            else    {   checkCorrectCall = game.go(splitText[1]);   }
        }
        else if (splitText.length ==1) controller.show("Command not found, help will show a list of all the commands\nRemember to specify the item when required");
        else if(splitText[0].equals("pick") || splitText[0].equals("pk"))
        {
            checkCorrectCall = game.getPlayer().pick(splitText[1]);
            if(checkCorrectCall != null)    {   controller.show(splitText[1] + " picked up");}
            else if (game.getPlayer().getInventory().hasItem(splitText[1]))
                controller.show("You already have "+splitText[1]+" in your inventory");
            else if (game.getPlayer().getInventory().getWeight()==game.getPlayer().getInventory().getMaxWeight())
                controller.show("Your inventory is full");
            else if (!game.getPlayer().getLocation().getStorage().hasItem(splitText[1]))
                controller.show(splitText[1] + " is not here");
            else if(!game.getPlayer().getLocation().getStorage().getItem(splitText[1]).isCollectable())
                controller.show("You cannot pick this item");
            else if(!game.getPlayer().getLocation().getStorage().getItem(splitText[1]).isVisible())
                controller.show(splitText[1]+" not found");
            else
                controller.show("You lost 1 lifepoint.\nNext time make sure you have gloves before handling such dangerous substances");
        }
        else if(splitText[0].equals("drop") || splitText[0].equals("dp"))
        {
            checkCorrectCall=game.getPlayer().drop(splitText[1]);
            if(checkCorrectCall != null)    {   controller.show(splitText[1] + " dropped");}
            else controller.show(splitText[1] + " must be in your inventory to be dropped");
        }
        else if(splitText[0].equals("interact") || splitText[0].equals("ic"))
        {
            checkCorrectCall=game.interact(splitText[1]);
            if(checkCorrectCall != null)    {   game.updateLastItem(splitText[1]);  }
        }
        else if(splitText[0].equals("use"))
        {
            if (lastCommandUsed.equals("interact") || lastCommandUsed.equals("ic")) {   game.use(splitText[1]);    }
            else {  controller.show("You have to interact with an item before utilize command use");    }
        }
        else controller.show("Command not found, help will show a list of all the commands");

        //aggiorna ultimo comando usato se le funzioni hanno ritornato oggetti != da null
        if(checkCorrectCall != null)    {   updateLastCommand(splitText[0]);    }

        // se non ha punti vita perde
        if(game.getPlayer().getLifePoints()<=0)
        {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(() -> {
                try {
                    Thread.sleep(10000);
                    controller.restart();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            controller.update("You have 0 life points!\nGame over!      (a new game will start in 10 seconds)");
            executor.shutdown();
        }
    }

    //*************************************************************************************
    //Metodi privati
    //*************************************************************************************
    /**
     * Updates the last used command
     *
     * @param text the command to update
     */
    protected void updateLastCommand(String text)
    {
        lastCommandUsed = text;
    }
}

