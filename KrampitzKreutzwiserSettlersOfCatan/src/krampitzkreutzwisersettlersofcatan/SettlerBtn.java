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

    //data attribu
    private boolean enabled; //stores whether or not the button is enabled
    private int mode; //the mode the button is in. Decides what text to display
    private int type; //the type of button. (Trade button, card toggle, build, etc.)
    //image attributes
    private Image baseImage; //the main image of the button
    private Image disabledImage; //the overlay to dim the button to indecate a disabled button
    private Image textImage; //the text of the button

    //static button images
    /**
     * =-=-=-=-=-=-=-=-=-=-=-= FONT FOR THE BUTTON TEXT =-=-=-=-=-=-=-=-=-=-=-=
     * Calibri, size 17, dual layer with 1 pixel translation
     */
    private final static Image CARD_BTN = new ImageIcon(ImageRef.class.getResource("settlerBtn/dev/cardBtn.png")).getImage();
    private final static Image CARD_DISABLED_BTN = new ImageIcon(ImageRef.class.getResource("settlerBtn/dev/cardBtnDisabled.png")).getImage();
    private final static Image CARD_BTN_TEXT1 = new ImageIcon(ImageRef.class.getResource("settlerBtn/dev/cardBtnText1.png")).getImage();
    private final static Image CARD_BTN_TEXT2 = new ImageIcon(ImageRef.class.getResource("settlerBtn/dev/cardBtnText2.png")).getImage();

    private final static Image BUY_DEV_BTN_TEXT = new ImageIcon(ImageRef.class.getResource("settlerBtn/dev/buyDevBtnText.png")).getImage();

    private final static Image USE_DEV_BTN_TEXT1 = new ImageIcon(ImageRef.class.getResource("settlerBtn/dev/useDevBtnText1.png")).getImage();
    private final static Image USE_DEV_BTN_TEXT2 = new ImageIcon(ImageRef.class.getResource("settlerBtn/dev/useDevBtnText2.png")).getImage();

    //turn switch button images
    private final static Image TURN_SWITCH_TBN_TEXT0 = new ImageIcon(ImageRef.class.getResource("settlerBtn/turnSwitch/turnBtnText0.png")).getImage();
    private final static Image TURN_SWITCH_TBN_TEXT1 = new ImageIcon(ImageRef.class.getResource("settlerBtn/turnSwitch/turnBtnText1.png")).getImage();
    private final static Image TURN_SWITCH_TBN_TEXT2 = new ImageIcon(ImageRef.class.getResource("settlerBtn/turnSwitch/turnBtnText2.png")).getImage();
    private final static Image TURN_SWITCH_TBN_TEXT3 = new ImageIcon(ImageRef.class.getResource("settlerBtn/turnSwitch/turnBtnText3.png")).getImage();
    private final static Image TURN_SWITCH_TBN_TEXT4 = new ImageIcon(ImageRef.class.getResource("settlerBtn/turnSwitch/turnBtnText4.png")).getImage();

    //trade buttons
    private final static Image TRADE_BTN = new ImageIcon(ImageRef.class.getResource("settlerBtn/trade/tradeBtn.png")).getImage();
    private final static Image TRADE_DISABLED_BTN = new ImageIcon(ImageRef.class.getResource("settlerBtn/trade/tradeBtnDisabled.png")).getImage();
    private final static Image TRADE_CANCEL = new ImageIcon(ImageRef.class.getResource("settlerBtn/trade/tradeBtnCancel.png")).getImage();
    private final static Image TRADE_4 = new ImageIcon(ImageRef.class.getResource("settlerBtn/trade/trade4to1Text.png")).getImage();
    private final static Image TRADE_3 = new ImageIcon(ImageRef.class.getResource("settlerBtn/trade/trade3to1Text.png")).getImage();
    private final static Image TRADE_2 = new ImageIcon(ImageRef.class.getResource("settlerBtn/trade/trade2to1Text.png")).getImage();

    //static button image arrays for text
    private final static Image[] CARD_BTN_TEXTS = new Image[]{CARD_BTN_TEXT1, CARD_BTN_TEXT2};
    private final static Image[] USE_DEV_BTN_TEXTS = new Image[]{USE_DEV_BTN_TEXT1, USE_DEV_BTN_TEXT2};
    private final static Image[] TURN_SWITCH_BTN_TEXTS = new Image[]{TURN_SWITCH_TBN_TEXT0, TURN_SWITCH_TBN_TEXT1, TURN_SWITCH_TBN_TEXT2, TURN_SWITCH_TBN_TEXT3, TURN_SWITCH_TBN_TEXT4};
    //trade button ones
    private final static Image[] TRADE_BTN_4TO_TEXTS = new Image[]{TRADE_CANCEL, TRADE_4};
    private final static Image[] TRADE_BTN_3TO_TEXTS = new Image[]{TRADE_CANCEL, TRADE_3};
    private final static Image[] TRADE_BTN_2TO_TEXTS = new Image[]{TRADE_CANCEL, TRADE_2};

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
        updateButtonImages(); //update the base image and disabled images
    }

    /**
     * Half constructor
     *
     * @param enabled
     * @param mode
     * @param type
     */
    public SettlerBtn(boolean enabled, int mode, int type) {
        this();
        xPos = -1; //set the coords to negative one as this constuctor is used when the coordinates cannot be known
        yPos = -1;

        this.enabled = enabled;
        this.mode = mode;
        this.type = type; //set to the card passed in as parameter

        updateText(); //update the text
        updateButtonImages(); //update the base image and disabled images
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
        this(enabled, mode, type);

        this.xPos = xPos;
        this.yPos = yPos;

        updateText(); //update the text
        updateButtonImages(); //update the base iamge and disabled images
    }

    /**
     * Update the base image and disabled image
     */
    public void updateButtonImages() {
        switch (type) {
            case 0:
                //if type is card toggle button
                baseImage = CARD_BTN;
                disabledImage = CARD_DISABLED_BTN;
                break;
            case 1:
                //if type is dev card buy button
                baseImage = CARD_BTN;
                disabledImage = CARD_DISABLED_BTN;
                break;
            case 2:
                //if type is dev card buy button
                baseImage = CARD_BTN;
                disabledImage = CARD_DISABLED_BTN;
                break;
            case 3:
                //if the type is a turn switch button
                baseImage = CARD_BTN; //still has the right length
                disabledImage = CARD_DISABLED_BTN;
                break;
            case 4:
                //if the type is a trade 4:1 button
                baseImage = TRADE_BTN; //still has the right length
                disabledImage = TRADE_DISABLED_BTN;
                break;
            case 5:
                //if the type is a trade 3:1 button
                baseImage = TRADE_BTN; //still has the right length
                disabledImage = TRADE_DISABLED_BTN;
                break;
            case 6:
                //if the type is a trade 2:1 button
                baseImage = TRADE_BTN; //still has the right length
                disabledImage = TRADE_DISABLED_BTN;
                break;
            default:
                //default to error images
                baseImage = ERROR_IMAGE;
                disabledImage = ERROR_IMAGE;
                break;
        }
    }

    /**
     * Update the textImage variable to reflect the text assigned by the mode
     * variable
     */
    public void updateText() {
        switch (type) {
            case 0:
                //if the type is card toggle button
                textImage = CARD_BTN_TEXTS[mode];
                break;
            case 1:
                //if the type is dev card buy button
                textImage = BUY_DEV_BTN_TEXT;
                break;
            case 2:
                //if the type is dev card buy button
                textImage = USE_DEV_BTN_TEXTS[mode];
                break;
            case 3:
                //if the type is a turn switch button
                textImage = TURN_SWITCH_BTN_TEXTS[mode];
                break;
            case 4:
                //if the type is a trade 4:1 button
                textImage = TRADE_BTN_4TO_TEXTS[mode];
                break;
            case 5:
                //if the type is a trade 4:1 button
                textImage = TRADE_BTN_3TO_TEXTS[mode];
                break;
            case 6:
                //if the type is a trade 4:1 button
                textImage = TRADE_BTN_2TO_TEXTS[mode];
                break;
            default:
                //deflault to error image
                textImage = ERROR_IMAGE;
                break;
        }
    }

    /**
     * Return the Image for the base of the button
     *
     * @return
     */
    public Image getBaseImage() {
        return baseImage;
    }

    /**
     * Return the Image for the text of the button
     *
     * @return
     */
    public Image getTextImage() {
        return textImage;
    }

    /**
     * Return the Image for the disable overlay of the button
     *
     * @return
     */
    public Image getDisabledImage() {
        return disabledImage;
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
        return super.equals(other)
                && enabled == other.enabled
                && mode == other.mode
                && type == other.type
                && baseImage == other.baseImage
                && textImage == other.textImage
                && disabledImage == other.disabledImage;
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
