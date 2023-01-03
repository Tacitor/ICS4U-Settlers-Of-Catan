/*
 * Lukas Krampitz
 * Jan 1, 2023
 * The JFrame for the menu elements of the game. Will hold the other SD Panels with settler buttons
 */
package krampitzkreutzwisersettlersofcatan.gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JPanel;
import krampitzkreutzwisersettlersofcatan.Catan;

/**
 *
 * @author Tacitor
 */
public class SDMenuFrame extends javax.swing.JFrame {

    private Dimension screenSize; //keeps track of the display the game is being played on
    private SDMainMenuPanel sDMainMenuPanel;

    /**
     * Main Constructor
     */
    public SDMenuFrame() {
        screenSize = Toolkit.getDefaultToolkit().getScreenSize(); //acctually gets the data for the display

        initFrame();
    }

    /**
     * Initialize and set up the JFrame
     */
    private void initFrame() {
        sDMainMenuPanel = new SDMainMenuPanel(this);

        setTitle("Settlers of Catan - ICS4U Edition");
        setSize(screenSize);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        //add in the JPanel
        add(sDMainMenuPanel);
        setUndecorated(true);
        setLocationRelativeTo(null); //centre the frame on the screen

        //Then do another check to see if to run in the online debug mode
        //This would mean running in 720p windowed with decorations
        if (Catan.DEBUG_ONLINE_MODE) { //Checks to see if the flag is set in the Main class
            setSize(1280, 720); //720p
            setUndecorated(false); //show the boarder
        }

    }

    /**
     * Figure out the ratio to scale an object by
     *
     * @param panel
     * @return
     */
    public double calcScaleFactor(JPanel panel) {
        double scaleFactor;

        //set up the circle scaler
        if (panel.getWidth() <= panel.getHeight()) {
            scaleFactor = (1920.0 / panel.getWidth());
        } else {
            scaleFactor = (1080.0 / panel.getHeight());
        }

        return scaleFactor;
    }

}
