/*
 * Lukas Krampitz
 * Jan 1, 2023
 * The JFrame for the menu elements of the game. Will hold the other SD Panels with settler buttons
 */
package krampitzkreutzwisersettlersofcatan.gui;

import java.awt.Dimension;
import java.awt.Toolkit;

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
        setVisible(true);

    }

}
