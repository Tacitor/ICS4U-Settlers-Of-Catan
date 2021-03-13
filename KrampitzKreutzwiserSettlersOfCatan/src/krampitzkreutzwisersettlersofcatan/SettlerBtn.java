/*
 * Lukas Krampitz
 * Mar 13, 2021
 * A custom button that fits the sytle of the game better and is easier to use than Swing button
 */
package krampitzkreutzwisersettlersofcatan;

import java.awt.Image;
import javax.swing.ImageIcon;
import textures.ImageRef;
import static textures.ImageRef.ERROR_IMAGE;

/**
 *
 * @author Tacitor
 */
public class SettlerBtn extends WorldObject {

    //data attributes
    private boolean enabled; //stores whether or not the button is enabled
    private int mode; //the mode the button is in. Decides what text to display
    private int type; //the type of button. (Trade button, card toggle, build, etc.)
    //image attributes
    private static Image baseImage; //the main image of the button
    private static Image disabledImage; //the overlay to dim the button to indecate a disabled button
    private static Image textImage; //the text of the button

    //static button images
    private final static Image CARD_BTN = new ImageIcon(ImageRef.class.getResource("cardBtn.png")).getImage();
    private final static Image CARD_DISABLED_BTN = new ImageIcon(ImageRef.class.getResource("cardBtnDisabled.png")).getImage();
    private final static Image CARD_BTN_TEXT1 = new ImageIcon(ImageRef.class.getResource("cardBtnText1.png")).getImage();
    private final static Image CARD_BTN_TEXT2 = new ImageIcon(ImageRef.class.getResource("cardBtnText2.png")).getImage();

    //static button image arrays for text
    private final static Image[] CARD_BTN_TEXTS = new Image[]{CARD_BTN_TEXT1, CARD_BTN_TEXT2};

    //constructors
    /**
     * Basic constructor
     */
    public SettlerBtn() {
        xPos = 0;
        yPos = 0;

        enabled = false;
        mode = 0;
        type = 0; //set to card togggle button

        updateText(); //update the text
        updateButtonImages(); //update the base iamge and disabled images
    }
    
    /**
     * Full constructor
     * 
     * @param xPos
     * @param yPos
     * @param enabled
     * @param mode
     * @param type 
     */
    public SettlerBtn(int xPos, int yPos, boolean enabled, int mode, int type) {
        this();
        
        this.xPos = xPos;
        this.yPos = yPos;

        this.enabled = enabled;
        this.mode = mode;
        this.type = type; //set to card togggle button

        updateText(); //update the text
        updateButtonImages(); //update the base iamge and disabled images
    }

    /**
     * Update the base image and disabled image
     */
    public void updateButtonImages() {
        if (type == 0) { //if type if card toggle button
            baseImage = CARD_BTN;
            disabledImage = CARD_DISABLED_BTN;
        } else { //deflault to error images
            baseImage = ERROR_IMAGE;
            disabledImage = ERROR_IMAGE;
        }
    }

    /**
     * Update the textImage variable to reflect the text assigned by the mode
     * variable
     */
    public void updateText() {
        if (type == 0) { //if the type is card toggle button
            textImage = CARD_BTN_TEXTS[mode];
        } else { //deflault to error image
            textImage = ERROR_IMAGE;
        }
    }
    
    /**
     * Access the enabled state of the button
     *
     * @return
     */
    public boolean getEnabled() {
        return enabled;
    }
    
    /**
     * Mutate the enabled state of the button
     *
     * @param enabled
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    /**
     * Access the text mode of the button
     *
     * @return
     */
    public int getMode() {
        return mode;
    }
    
    /**
     * Mutate the text mode of the button
     *
     * @param mode
     */
    public void setMode(int mode) {
        this.mode = mode;
        updateText();
    }
    
    /**
     * Access the button type dictating the shape and text of the button
     *
     * @return
     */
    public int getType() {
        return type;
    }
    
    /**
     * Mutate the button type dictating the shape and text of the button
     *
     * @param type
     */
    public void setType(int type) {
        this.type = type;
        updateButtonImages();
        updateText();
    }

    @Override
    public WorldObject clone() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Check if this object's attributes are equal to another object's
     *
     * @param other The Button to compare to
     * @return Whether or not the objects are the same
     */
    public boolean equals(SettlerBtn other) {
        // Return if the position of the objects are equal or not
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        //return super.equals(other);
    }

    /**
     * Create a string representations of the button object
     *
     * @return The object as a string
     */
    @Override
    public String toString() {
        // Create a String out of the position of the object
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        //return "Position (X, Y): (" + xPos + ", " + yPos + ")";
    }

}
