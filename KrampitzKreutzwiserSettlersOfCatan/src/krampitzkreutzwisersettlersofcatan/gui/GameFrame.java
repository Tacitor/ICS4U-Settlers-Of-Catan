/*
 * Lukas Krampitz
 * Nov 7, 2020
 * The JFrame that will hold the Panel that will be the main part of the game
 */
package krampitzkreutzwisersettlersofcatan.gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import krampitzkreutzwisersettlersofcatan.Catan;
import textures.ImageRef;

/**
 *
 * @author Tacitor
 */
public class GameFrame extends javax.swing.JFrame implements KeyListener {

    private final SDMainMenuPanel mainMenuPanel; //ref to the main menu
    private Dimension screenSize; //keeps track of the display the game is being played on
    private GamePanel theGamePanel; //referance to *a* GamePanel
    private DomesticTradePanel domesticTradePanel; //referance to the domestic trade window
    private boolean showTrade; //true if showing the trade panel

    public GameFrame(SDMainMenuPanel m) {

        setIcon();

        mainMenuPanel = m;
        screenSize = Toolkit.getDefaultToolkit().getScreenSize(); //acctually gets the data for the display

        initFrame();
    }

    /**
     * Set up the JFrame
     */
    private void initFrame() {
        domesticTradePanel = new DomesticTradePanel(this);

        setTitle("Settlers of Catan");
        setSize(1920, 1080); //set the size to 1080p as a back up 
        setSize(screenSize); //set the JFrame size to match the display
        //setSize(1080, 1920);
        //setSize(1280, 720); //720p
        //setSize(720 /*NOT 720p*/, 1280);
        //setSize(800, 600);
        //setSize(600, 800);
        //setSize(3840, 2160);        
        setDefaultCloseOperation(EXIT_ON_CLOSE); //make sure it closes the thread when the frame closes
        theGamePanel = new GamePanel(this); //creates a new blank game
        add(theGamePanel); //adds it to the JFrame
        setLocationRelativeTo(null); //center the frame on screen
        setResizable(true); //do not allow the user to resize the window
        //setExtendedState(JFrame.MAXIMIZED_BOTH); //this would normaly set the size to the display size but I don't want to deal with scaling elemnts nor do I have the time
        setUndecorated(true); //removes the boarders and control buttons, this makes it full screen for 1080p displays and just a really wierd borderless window for anything higher. Most likly broken for anything lower
        setVisible(false);
        addKeyListener(this); //allow the game to access presses on the keyboard
        this.setFocusTraversalKeysEnabled(false); //set to ignore tabbing keys and treat them like regular presses

        //debug screen size
        //System.out.println("Width: " + this.getWidth());
        //System.out.println("Height: " + this.getHeight());
        //Then do another check to see if to run in the online debug mode
        //This would mean running in 720p windowed with decorations
        if (Catan.DEBUG_ONLINE_MODE) { //Checks to see if the flag is set in the Main class
            setSize(1280, 720); //720p
            setUndecorated(false); //show the boarder
        }
    }

    /**
     * Set the icon for the JFRame
     */
    private void setIcon() {
        this.setIconImage(ImageRef.ICON);
    }

    /**
     * Provide reference to the main menu to any subclasses
     *
     * @return
     */
    public SDMainMenuPanel getMainMenu() {
        return mainMenuPanel;
    }

    /**
     * Return the game panel
     *
     * @return
     */
    public GamePanel getGamePanel() {
        return theGamePanel;
    }

    /**
     * Return the domestic trade panel.
     *
     * @return
     */
    public DomesticTradePanel getDomesticTradePanel() {
        return domesticTradePanel;
    }

    /**
     * Removes the old game and replaces it with a new one
     */
    public void resetGamePanel() {
        remove(theGamePanel);
        theGamePanel = new GamePanel(this);
        add(theGamePanel);

        //update the gamepanel in the main method
        Catan.updateGamePanel();
    }

    /**
     * Switch modes of displaying the game. Switches between the regular game
     * panel and the domestic trade panel.
     *
     * @param showTrade. If true Remove theGamePanel, and put it into domestic
     * trade mode. If false do the reverse and show the game panel.
     */
    public void switchToTrade(boolean showTrade) {
        this.showTrade = showTrade;

        if (showTrade) { //show the domestic trade menu
            remove(theGamePanel); //take out game panel

            //debugg the cards getting deleted
            //System.out.println("Test 3: " + theGamePanel.getResourceCards()[theGamePanel.getCurrentPlayer()]);
            domesticTradePanel.resetToStartingMode();

            //debugg the cards getting deleted
            //System.out.println("Test 4: " + theGamePanel.getResourceCards()[theGamePanel.getCurrentPlayer()]);
            add(domesticTradePanel); //switch over to domestic trade mode

            //save the player that started the domestic trade
            domesticTradePanel.setPlayerStartedDomestic(theGamePanel.getCurrentPlayer());
            //save the cards they had at the time of clicking the trade button            
            domesticTradePanel.setTradeCardsAlreadyHadPlayerStartedDomestic((ArrayList<Integer>) theGamePanel.getResourceCards()[theGamePanel.getCurrentPlayer()].clone());

            domesticTradePanel.repaint(); //ensure there is something to diaplay

        } else { //show the main game panel
            remove(domesticTradePanel); //take out game panel
            add(theGamePanel); //switch over to domestic trade mode

            //update the build buttons
            theGamePanel.updateBuildButtons();

            theGamePanel.repaint(); //ensure there is something to diaplay

        }

        this.setVisible(true); //ensure the frame is active and visible
        this.setFocusable(true); //ensure the Frame has the keyListener focus.

    }

    /**
     * Is the GameFrame currently showing the domestic trade panel or not?
     *
     * @return
     */
    public boolean getShowTrade() {
        return showTrade;
    }

    /**
     * Passes the load address on to the GamePanel
     *
     * @param filePathString
     */
    public void loadFromFile(String filePathString) {
        theGamePanel.load(filePathString);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        //do nothing
    }

    @Override
    public void keyPressed(KeyEvent e) {
        //send this to the gamePanel
        theGamePanel.keyPress(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        //do nothing
    }

}
