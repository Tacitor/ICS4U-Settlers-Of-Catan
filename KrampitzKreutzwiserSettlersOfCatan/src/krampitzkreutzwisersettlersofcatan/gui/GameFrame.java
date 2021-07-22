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
import krampitzkreutzwisersettlersofcatan.Catan;
import textures.ImageRef;

/**
 *
 * @author Tacitor
 */

public class GameFrame extends javax.swing.JFrame implements KeyListener {
  
    private final MainMenu mainMenuFrame; //ref to the main menu
    private Dimension screenSize; //keeps track of the display the game is being played on
    private GamePanel theGamePanel; //referance to *a* GamePanel
    
    public GameFrame(MainMenu m) {
        
        setIcon();

        mainMenuFrame = m;
        screenSize = Toolkit.getDefaultToolkit().getScreenSize(); //acctually gets the data for the display
        
        initFrame();
    }
    
    /**
     * Set up the JFrame
     */
    private void initFrame() {
        setTitle("Settlers of Catan");
        setSize(1920, 1080); //set the size to 1080p as a back up 
        setSize(screenSize); //set the JFrame size to match the display
        //setSize(1080, 1920);
        //setSize(1280, 720);
        //setSize(720, 1280);
        //setSize(800, 600);
        //setSize(600, 800);
        //setSize(3840 , 2160);        
        setDefaultCloseOperation(EXIT_ON_CLOSE); //make sure it closes the thread when the frame closes
        theGamePanel = new GamePanel(this); //creates a new blank game
        add(theGamePanel); //adds it to the JFrame
        setLocationRelativeTo(null); //center the frame on screen
        setResizable(false); //do not allow the user to resize the window
        //setExtendedState(JFrame.MAXIMIZED_BOTH); //this would normaly set the size to the display size but I don't want to deal with scaling elemnts nor do I have the time
        setUndecorated(true); //removes the boarders and control buttons, this makes it full screen for 1080p displays and just a really wierd borderless window for anything higher. Most likly broken for anything lower
        setVisible(false);
        addKeyListener(this); //allow the game to access presses on the keyboard
        this.setFocusTraversalKeysEnabled(false); //set to ignore tabbing keys and treat them like regular presses
        
        //debug screen size
        //System.out.println("Width: " + this.getWidth());
        //System.out.println("Height: " + this.getHeight());
        
    }
    
    /**
     * Set the icon for the JFRame
     */
    private void setIcon() {
        this.setIconImage(ImageRef.ICON);
    }
    
    /**
     * Provide reference to the main menu to any subclasses
     * @return 
     */
    public MainMenu getMainMenu() {
        return mainMenuFrame;
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
     * Passes the load address on to the GamePanel 
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
