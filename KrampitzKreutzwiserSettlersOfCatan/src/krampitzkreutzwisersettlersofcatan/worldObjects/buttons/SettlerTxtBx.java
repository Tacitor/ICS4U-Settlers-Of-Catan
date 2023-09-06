/*
 * Lukas Krampitz
 * Sep 2, 2023
 * A custom text box the fits the test of the Settlers of Catan Theme
 */
package krampitzkreutzwisersettlersofcatan.worldObjects.buttons;

import java.awt.Graphics2D;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import krampitzkreutzwisersettlersofcatan.gui.SDScaleImageResizeable;
import krampitzkreutzwisersettlersofcatan.worldObjects.WorldObject;
import textures.ImageRef;

/**
 *
 * @author Tacitor
 */
public class SettlerTxtBx extends SettlerComponent {

    //attibutes
    protected int cursorPos; //the position of the cursor. This is where text gets added and removed from
    protected int startDisplayPos; //the position of where to start displaying the text withing the box.
    protected char[] chars; //the char array that will contain the text of the text box.

    //static attributes    
    private final static Image TEXT_BOX_REG_BASE = new ImageIcon(ImageRef.class.getResource("settlerBtn/mainMenu/radio/radioLongBtn.png")).getImage();

    /**
     * Primary Constructor
     *
     * @param enabled
     * @param type
     */
    public SettlerTxtBx(boolean enabled, int type) {
        xPos = 0;
        yPos = 0;

        this.enabled = enabled;
        this.type = type;
        mouseHover = false;
        tabSelected = false;

        updateText();
        updateButtonImages();
    }

    /**
     * Secondary Constructor
     *
     * @param xPos
     * @param yPos
     * @param enabled
     * @param type
     */
    public SettlerTxtBx(int xPos, int yPos, boolean enabled, int type) {
        this(enabled, type);

        this.xPos = xPos;
        this.yPos = yPos;

        updateText();
        updateButtonImages();
    }

    @Override
    public void updateButtonImages() {
        switch (type) {
            case 0:
                //if the type is standard sized text box
                baseImage = TEXT_BOX_REG_BASE;
                disabledImage = ImageRef.ERROR_IMAGE;
                hoverImage = ImageRef.ERROR_IMAGE;
                break;

            default:
                //default to error images
                baseImage = ImageRef.ERROR_IMAGE;
                disabledImage = ImageRef.ERROR_IMAGE;
                hoverImage = ImageRef.ERROR_IMAGE;
                break;
        }
    }

    @Override
    public void updateText() {
        textImage = null;
    }

    /**
     * Draw the text box
     *
     * @param g2d
     * @param parent
     */
    public void draw(Graphics2D g2d, JComponent parent) {
        //check to make sure the call is correct
        if (parent instanceof SDScaleImageResizeable) {
            SDScaleImageResizeable SDParent = (SDScaleImageResizeable) parent;

            //draw the base        
            g2d.drawImage(baseImage,
                    xPos,
                    yPos,
                    SDParent.getLocalImgWidth(baseImage),
                    SDParent.getLocalImgHeight(baseImage),
                    null);

            //draw the disabled overlay if required
            if (!enabled) {
                g2d.drawImage(disabledImage,
                        xPos,
                        yPos,
                        SDParent.getLocalImgWidth(disabledImage),
                        SDParent.getLocalImgHeight(disabledImage),
                        null);
            }

            //draw the mouseHover overlay if required
            if (mouseHover) {
                g2d.drawImage(hoverImage,
                        xPos,
                        yPos,
                        SDParent.getLocalImgWidth(hoverImage),
                        SDParent.getLocalImgHeight(hoverImage),
                        null);
            }
        } else {
            System.out.println("ERROR: The parent JComponent in draw method of SettlerTxtBx does not implement SDScaleImageResizeable.");
        }
    }

    @Override
    public WorldObject clone() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
