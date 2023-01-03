/*
 * Lukas Krampitz
 * Jan 1, 2023
 * The JPanel for the Main Menu now using Settler Dev Buttons.
 */
package krampitzkreutzwisersettlersofcatan.gui;

import java.awt.Graphics;
import java.awt.Graphics2D;

/**
 *
 * @author Tacitor
 */
public class SDMainMenuPanel extends javax.swing.JPanel {

    private SDMenuFrame sDMenuFrame;

    //Settler Compoments
    /**
     * Main Constructor
     *
     * @param sDFrame
     */
    public SDMainMenuPanel(SDMenuFrame sDFrame) {
        sDMenuFrame = sDFrame;

    }

    /**
     * Override the JPanel default paintComponent() method.
     */
    @Override
    public void paintComponent(Graphics g) {

        //call the paintCompnent from the super class
        super.paintComponent(g);
        //call the custom layer
        draw(g);
    }

    /**
     * Draw the UI for the Main Menu
     *
     * @param g
     */
    private void draw(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;
        
        g2d.drawRect(100, 100, 400, 400);

    }

}
