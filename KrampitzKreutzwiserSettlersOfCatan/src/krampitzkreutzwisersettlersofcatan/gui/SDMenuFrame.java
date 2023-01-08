/*
 * Lukas Krampitz
 * Jan 1, 2023
 * The JFrame for the menu elements of the game. Will hold the other SD Panels with settler buttons
 */
package krampitzkreutzwisersettlersofcatan.gui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;
import javax.swing.JComponent;
import javax.swing.JPanel;
import krampitzkreutzwisersettlersofcatan.Catan;
import krampitzkreutzwisersettlersofcatan.worldObjects.buttons.SettlerBtn;
import textures.ImageRef;

/**
 *
 * @author Tacitor
 */
public class SDMenuFrame extends javax.swing.JFrame {

    private Dimension screenSize; //keeps track of the display the game is being played on
    private SDMainMenuPanel sDMainMenuPanel;
    
    //attributes
    final static int MENU_PACKING_HEIGHT = 12;

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
        setIcon();
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

    /**
     * Calculates the new scaled width of an image with a locked aspect ratio.
     * Based on the JCompoment give.
     *
     * @param image
     * @param parent
     * @return
     */
    public final int getImgWidthLocal(Image image, JComponent parent) {

        if (parent.getWidth() > parent.getHeight()) {
            return (int) (getImgHeightLocal(image, parent) * ((float) image.getWidth(null) / image.getHeight(null)));
        } else {
            return (int) (image.getWidth(null) / 1920.0 * parent.getWidth());
        }

    }

    /**
     * Calculates the new scaled height of an image with a locked aspect ratio.
     * Based on the JCompoment give.
     *
     * @param image
     * @param parent
     * @return
     */
    public final int getImgHeightLocal(Image image, JComponent parent) {
        if (parent.getWidth() > parent.getHeight()) {
            return (int) (image.getHeight(null) / 1080.0 * parent.getHeight());
        } else {
            return (int) (getImgWidthLocal(image, parent) / ((float) image.getWidth(null) / image.getHeight(null)));
        }
    }

    /**
     * Draw a SettlerBtn with Graphics 2D
     *
     * @param g2d
     * @param btnImage
     * @param btn
     * @param drawMode
     * @param parent
     */
    public void drawSettlerBtn(Graphics2D g2d, Image btnImage, SettlerBtn btn, int drawMode, JComponent parent) {
        //get the width and height of the image
        int width;
        int height;
        if (drawMode == 0) { //draw mode is image dimensions
            width = getImgWidthLocal(btnImage, parent);
            height = getImgHeightLocal(btnImage, parent);
        } else { //1 is button dimensions
            width = getImgWidthLocal(btn.getBaseImage(), parent);
            height = getImgHeightLocal(btn.getBaseImage(), parent);
        }

        g2d.drawImage(btnImage,
                btn.getXPos(),
                btn.getYPos(),
                width,
                height, null);
    }

    /**
     * Set the icon for the JFRame
     */
    private void setIcon() {
        this.setIconImage(ImageRef.ICON);
    }

    /**
     * Accessor for the Main Menu Panel
     *
     * @return
     */
    public SDMainMenuPanel getSDMainMenuPanel() {
        return sDMainMenuPanel;
    }

    /**
     * Return the Compass Gold font. Setup and load the TrueType font from the
     * file system for use in game.
     *
     * @return
     */
    public Font setUpCompassGoldFont() {

        Font tempFont = null;

        try {
            //System.out.println(url.getPath());
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getClassLoader().getResourceAsStream("font/CompassGold.ttf")));

            tempFont = new Font("CompassGold", Font.PLAIN, 20);

        } catch (FontFormatException ex) {
            System.out.println("FontFormatException: " + ex);
        } catch (IOException ex) {
            System.out.println("IOException: " + ex);
        }

        return tempFont;
    }

    /**
     * Switch the active menu element
     *
     * @param oldPanel
     * @param newPanel
     */
    public void switchPanel(JPanel oldPanel, JPanel newPanel) {
        oldPanel.setVisible(false);
        remove(oldPanel);
        add(newPanel);
        newPanel.setVisible(true);

        newPanel.repaint();
    }

}
