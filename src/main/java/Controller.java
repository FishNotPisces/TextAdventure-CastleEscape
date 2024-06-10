import software.amazon.awssdk.regions.Region;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Objects;
import javax.swing.JScrollPane;

/**
 * The Controller class handles the main interface and game logic interactions
 * for the Escape Castle game. It manages user input, game state, and cloud save/load functionality
 */
public class Controller extends JFrame
{
    protected Game game;
    protected CommandReader reader;
    protected JFrame window;
    private JPanel mainPanel;
    protected JLabel imageLabel;
    private JTextField inputField;
    protected JTextPane outputField;
    private JScrollPane jsPane;
    private boolean gameStarted = false;
    private final String imageHallPath = "immagini/IMG_0927.JPG";
    private final String imageLabPath = "immagini/IMG_0925.JPG";
    private final String imageKitchenPath = "immagini/IMG_0926.JPG";
    private final String imageStudioPath = "immagini/IMG_0928.JPG";
    private final String imageDungeonsPath = "immagini/IMG_0924.JPG";
    private final String imageStartPath = "immagini/intro.jpg";
    private final String imageVictoryPath = "immagini/victory.jpg";
    protected String name = null;    //nome giocatore
    private S3bucket bucket;
    protected boolean isCloudActive;
    protected String lastMessageDisplayed="";

    //*************************************************************************************
    //Metodi pubblici
    //*************************************************************************************
    /**
     * Constructs a new Controller instance, setting up the main game window and cloud save functionality
     *
     * @throws IOException if an I/O error occurs while setting up cloud saves
     */
    public Controller() throws IOException   //Costruttore
    {
        isCloudActive = setupCloudBucket();

        createMainField();  //crea la schermata generale
        setStartField();    //imposta la schermata pre-gioco

        if (!isCloudActive) update("<!> Cloud saves are disabled for this session <!>");

        inputField.addKeyListener(new KeyAdapter()  //Lettore pressione tasto invio
        {
            @Override
            public void keyPressed(KeyEvent e)
            {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) //controlla che il tasto premuto sia invio
                {
                    try {
                        readInput(inputField.getText());   //legge cosa ha digitato il giocatore
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });
    }

    /**
     * Saves the current game state to the cloud
     */
    public void save()
    {
        isCloudActive= S3bucket.checkCloudConnection();
        String report = bucket.saveGameState(name + ".json", game);
        update(report);
    }

    /**
     * Loads the game state from the cloud
     *
     * @return the loaded Game object, or null if loading fails
     */
    public Game load() {
        isCloudActive= S3bucket.checkCloudConnection();
        Game tempGame= bucket.loadGameState(name + ".json", Game.class);
        if (tempGame!=null)
            tempGame.controller=this;
        return tempGame;
    }

    /**
     * Displays the specified text in the output field
     * @param text the text to display
     */
    public void show(String text)   //funzione pubblica invocata da game per stampare del testo
    {
        lastMessageDisplayed=text;
        outputField.setText(text);
        outputField.setCaretPosition(0);
    }

    /**
     * Appends the specified text to the output field
     *
     * @param text the text to append
     */
    public void update(String text)   //funzione pubblica invocata da game per AGGIUNGERE del testo
    {
        lastMessageDisplayed=text;
        String temp = outputField.getText();
        outputField.setText(temp + "\n" + text);
        outputField.setCaretPosition(0);
    }

    /**
     * Returns the last message displayed in the main field
     *
     * @return the last message displayed in the main field
     */
    protected String getLastMessage(){
        return lastMessageDisplayed;
    }

    /**
     * Changes the displayed image based on the specified room name
     *
     * @param text the name of the location
     */
    public void showImage(String text)  //funzione pubblica invocata da game per cambiare immagine
    {
        changeImage(text);
    }

    /**
     * Restarts the game, reinitializing game state and UI components.
     */
    public void restart(){
        game  = new Game(this, name);
        reader = new CommandReader(this, game);
        setMainField();
        changeImage("hall");
        inputField.setText("");
        gameStarted = true;
    }

    /**
     * Closes the game window.
     */
    public void close(){    window.dispose();   }

    //*************************************************************************************
    //Metodi privati
    //*************************************************************************************

    /**
     * Sets up the cloud bucket for saving and loading game state.
     *
     * @return true if cloud saves are active, false otherwise
     * @throws IOException if an I/O error occurs while setting up cloud saves
     */
    protected boolean setupCloudBucket() throws IOException {
        boolean isActive = (S3bucket.checkCredentials()  && S3bucket.checkCloudConnection());

//        if (isActive) {
//            System.setProperty("aws.accessKeyId", Objects.requireNonNull(S3bucket.getCredentials("key")));
//            System.setProperty("aws.secretAccessKey", Objects.requireNonNull(S3bucket.getCredentials("secretKey")));
//        }
        bucket = new S3bucket(S3bucket.getCredentials("bucketName"), Region.EU_NORTH_1, isActive);
        return isActive;
    }


    /**
     * Sets up the start screen for the game
     */
    private void setStartField()
    {
        show("Welcome!\nInsert your name");
        inputField.setText("");
        imageLabel.setIcon(new ImageIcon(getClass().getResource(imageStartPath)));
    }

    /**
     * Sets up the main game screen
     */
    private void setMainField()
    {
        show("Welcome to my castle " + name + ". There\'s a note in front of you. Enter \"interact note\" to read it.");
        inputField.setText("");
        changeImage("hall");
    }

    /**
     * Creates the main game window and sets up UI components
     */
    private void createMainField()
    {
        window = new JFrame();
        window.setExtendedState(MAXIMIZED_BOTH);    //avvia grafica sempre a schermo intero
        window.setSize(1280, 720);
        customizeScrollBar();
        window.setContentPane(this.mainPanel);
        window.getContentPane().setBackground(Color.decode("#202530"));
        window.setTitle("Escape Castle"); //Titolo della finestra
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);  //Imposta la finestra che sia visibile
        inputField.setFont(new Font("monospaced",Font.BOLD,17));    //font
        inputField.setForeground(Color.white);  //colore testo
        inputField.setCaretColor(Color.WHITE);
        outputField.setFont(new Font("monospaced",Font.BOLD,17));   //font
        outputField.setForeground(Color.white); //colore testo
        inputField.setBackground(Color.decode("#2F2F3F"));  //sfondo casella di testo
        outputField.setBackground(Color.decode("#202530"));    //sfondo casella di testo
         outputField.setCaretColor(Color.decode("#202530"));
        jsPane.getViewport().setBackground(Color.decode("#202530"));
        outputField.setCaretPosition(0);
    }

    /**
     * Changes the displayed image based on the room name
     *
     * @param room the name of the room
     */
    private void changeImage(String room)
    {
        String str = room.toLowerCase();    //porta la stringa in minuscolo
        switch (str)
        {
            case "hall":
                imageLabel.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource(imageHallPath))));
                break;
            case "studio":
                imageLabel.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource(imageStudioPath))));
                break;
            case "kitchen":
                imageLabel.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource(imageKitchenPath))));
                break;
            case "lab":
                imageLabel.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource(imageLabPath))));
                break;
            case "dungeons":
                imageLabel.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource(imageDungeonsPath))));
                break;
            case "victory":
                imageLabel.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource(imageVictoryPath))));
            default:
                //do nothing
        }

    }

    /**
     * Processes the initial input for new game and load game, then delegates to CommandReader
     *
     * @param text the user input text
     * @throws InterruptedException if the thread is interrupted
     */
    protected void readInput(String text) throws InterruptedException {
        String str = text.toLowerCase();    //porta la stringa in minuscolo
        if((!gameStarted)&&(name == null))
        {
            name = text;
            show("Hi " + name + "! Start new game or load an old one?\n(new game, load game)");
            inputField.setText("");
        }
        else if((!gameStarted)&&(str.equals("new game"))&&(name != null))
        {
            //show("Inserire il nome del giocatore");
            game  = new Game(this, name);
            reader = new CommandReader(this, game);
            setMainField();
            changeImage("hall");
            inputField.setText("");
            gameStarted = true;
        }

        else if((!gameStarted)&&(str.equals("load game"))&&(name != null))
        {
            //show("Load an existing game");
            Game tempGame=load();
            if (tempGame!=null){
                setMainField();
                gameStarted = true;
                game = tempGame;
                reader = new CommandReader(this, game);
                changeImage(game.getPlayer().getLocation().getName());
                if (isCloudActive)
                    show("Game loaded from cloud correctly\n" + game.getPlayer().getLocation().getDescription());
                else
                    show("Game loaded from local saves due to absent internet connection\n" + game.getPlayer().getLocation().getDescription());
                inputField.setText("");
            }
            else{
                if (isCloudActive){
                    show("No game saved with the name "+name+" was found, please try again.\nInsert your name");
                    inputField.setText("");
                    name=null;
                }
                else{
                    show("Cloud saves are not available in this session. Make sure to have an internet connection or choose new game");
                    inputField.setText("");
                }
            }

        }
        else if(gameStarted){
            inputField.setText("");
            reader.read(text);
        }
        else
        {
            inputField.setText("");
            show("Try again.");
        }
    }

    private void customizeScrollBar() {
        JScrollBar verticalScrollBar = jsPane.getVerticalScrollBar();
        verticalScrollBar.setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = Color.gray; // Cambia il colore della thumb (la parte che si muove)
                this.trackColor = Color.decode("#202530"); // Cambia il colore della traccia
            }

            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }

            private JButton createZeroButton() {
                JButton jbutton = new JButton();
                jbutton.setPreferredSize(new Dimension(0, 0));
                jbutton.setMinimumSize(new Dimension(0, 0));
                jbutton.setMaximumSize(new Dimension(0, 0));
                return jbutton;
            }
        });
    }
}

