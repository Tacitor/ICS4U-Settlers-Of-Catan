/*
 * Lukas Krampitz
 * Jan 1, 2023
 * The JPanel for 
 */
package krampitzkreutzwisersettlersofcatan.gui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import textures.ImageRef;

/**
 *
 * @author Tacitor
 */
public abstract class SDMenuTemplate extends javax.swing.JPanel {

    private SDMenuFrame sDMenuFrame;

    //Settler Compoments
    
    /**
     * Main Constructor
     *
     * @param sDFrame
     */
    public SDMenuTemplate(SDMenuFrame sDFrame) {
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
     * Draw the UI
     *
     * @param g
     */
    private void draw(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;
        
        //draw the background image
        g2d.drawImage(ImageRef.WOOD_BACKGROUND, 
                0, 
                0, 
                sDMenuFrame.getWidth(), 
                sDMenuFrame.getHeight(), this);

    }

}
