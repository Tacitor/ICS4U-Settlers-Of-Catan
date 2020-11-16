/*
 * Lukas Krampitz
 * Nov 7, 2020
 * The JFrame that will hold the Panel that will be the main part of the game
 */
package krampitzkreutzwisersettlersofcatan;

/**
 *
 * @author Tacitor
 */
public class GameFrame extends javax.swing.JFrame {

    private final MainMenu mainMenuFrame; //ref to the main menu
    
    public GameFrame(MainMenu m) {

        mainMenuFrame = m;
        
        initFrame();
    }
    
    /**
     * Set up the JFrame
     */
    private void initFrame() {
        setTitle("Settlers of Catan");
        setSize(1920, 1080); //set the size to 1080p
        //setSize(1280, 720);
        setDefaultCloseOperation(EXIT_ON_CLOSE); //make sure it closes the thread when the frame closes
        add(new GamePanel(this));
        setLocationRelativeTo(null); //center the frame on screen
        setResizable(false); //do not allow the user to resize the window
        //setExtendedState(JFrame.MAXIMIZED_BOTH); //this would normaly set the size to the display size but I don't want to deal with scaling elemnts nor do I have the time
        setUndecorated(true); //removes the boarders and control buttons, this makes it full screen for 1080p displays and just a really wierd borderless window for anything higher. Most likly broken for anything lower
        setVisible(false);
        
    }
    
    /**
     * Provide reference to the main menu to any subclasses
     * @return 
     */
    protected MainMenu getMainMenu() {
        return mainMenuFrame;
    }

}
