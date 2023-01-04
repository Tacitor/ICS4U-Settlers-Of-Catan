/*
 * Lukas Krampitz
 * Jan 1, 2023
 * The JPanel for the Main Menu now using Settler Dev Buttons.
 */
package krampitzkreutzwisersettlersofcatan.gui;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import krampitzkreutzwisersettlersofcatan.worldObjects.buttons.SettlerBtn;
import textures.ImageRef;

/**
 *
 * @author Tacitor
 */
public class SDMainMenuPanel extends javax.swing.JPanel {

    private SDMenuFrame sDMenuFrame;
    private static double localScaleFactor; //The factor to scale this panel by when drawing elemets

    //Settler Compoments
    private SettlerBtn exitMainMenuBtn;
    //The array for the buttons
    private SettlerBtn[] settlerBtns;

    //Fonts
    public Font COMPASS_GOLD;

    /**
     * Main Constructor
     *
     * @param sDFrame
     */
    public SDMainMenuPanel(SDMenuFrame sDFrame) {
        sDMenuFrame = sDFrame;

        COMPASS_GOLD = setUpCompassGoldFont();

        //setup the buttons
        exitMainMenuBtn = new SettlerBtn(true, 0, 13);

        //add them to the array
        settlerBtns = new SettlerBtn[]{exitMainMenuBtn};

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

        //update the scale factor
        localScaleFactor = sDMenuFrame.calcScaleFactor(this);

        //update the button positions
        settlerVarPos();

        //draw the background image
        g2d.drawImage(ImageRef.WOOD_BACKGROUND,
                0,
                0,
                this.getWidth(),
                this.getHeight(), this);

        g2d.setFont(new Font(COMPASS_GOLD.getName(), Font.PLAIN, localScaleInt(180)));
        g2d.setColor(DomesticTradePanel.BEIGE_COLOUR);

        //Draw the Title
        g2d.drawString("Settlers of Catan",
                (this.getWidth() / 2) - (g2d.getFontMetrics().stringWidth("Settlers of Catan") / 2),
                localScaleInt(150));

        //=-=-=-=-=-=-=-=-=-= Draw the Settlerbuttons =-=-=-=-=-=-=-=-=-=
        for (SettlerBtn btn : settlerBtns) {
            btn.updateButtonImages();
            btn.updateText();

            //draw the base        
            sDMenuFrame.drawSettlerBtn(g2d, btn.getBaseImage(), btn, 0, this);

            //draw the text
            sDMenuFrame.drawSettlerBtn(g2d, btn.getTextImage(), btn, 0, this);

            //draw the disabled overlay if required
            if (!btn.isEnabled()) {
                sDMenuFrame.drawSettlerBtn(g2d, btn.getDisabledImage(), btn, 0, this);
            }
            //draw the mouseHover overlay if required
            if (btn.isMouseHover()) {
                sDMenuFrame.drawSettlerBtn(g2d, btn.getHoverImage(), btn, 1, this);
            }

        }

        //=-=-=-=-=-=-=-=-=-= END OF the drawing of Settlerbuttons =-=-=-=-=-=-=-=-=-=
    }

    private void settlerVarPos() {
        exitMainMenuBtn.setXPos(localScaleInt(10));
        exitMainMenuBtn.setYPos(this.getHeight() - localScaleInt(10) - sDMenuFrame.getImgHeightLocal(exitMainMenuBtn.getBaseImage(), this));
    }

    /**
     * Return the Compass Gold font. Setup and load the TrueType font from the
     * file system for use in game.
     *
     * @return
     */
    private Font setUpCompassGoldFont() {

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
     * Scale a number to match the resolution of the screen
     *
     * @param num
     * @return
     */
    public static int localScaleInt(int num) {
        return (int) (num / localScaleFactor);
    }

}
