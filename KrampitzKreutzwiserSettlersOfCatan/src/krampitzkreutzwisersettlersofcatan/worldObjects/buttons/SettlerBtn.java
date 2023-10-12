/*
 * Lukas Krampitz
 * Mar 13, 2021
 * A custom button that fits the sytle of the game better and is easier to use than Swing button
 */
package krampitzkreutzwisersettlersofcatan.worldObjects.buttons;

import java.awt.Image;
import javax.swing.ImageIcon;
import krampitzkreutzwisersettlersofcatan.worldObjects.WorldObject;
import textures.ImageRef;
import static textures.ImageRef.ERROR_IMAGE;

/**
 *
 * @author Tacitor
 */
public class SettlerBtn extends SettlerComponent {

    //data attribute
    private int mode; //the mode the button is in. Decides what text to display

    //static button images
    /**
     * =-=-=-=-=-=-=-=-=-=-=-= FONT FOR THE BUTTON TEXT =-=-=-=-=-=-=-=-=-=-=-=
     * Calibri, size 17, dual layer with 1 pixel horizontal translation
     */
    private final static Image CARD_BTN = new ImageIcon(ImageRef.class.getResource("settlerBtn/dev/cardBtn.png")).getImage();
    private final static Image CARD_DISABLED_BTN = new ImageIcon(ImageRef.class.getResource("settlerBtn/dev/cardBtnDisabled.png")).getImage();
    private final static Image HOVER_BTN = new ImageIcon(ImageRef.class.getResource("settlerBtn/dev/cardBtnHover.png")).getImage();
    private final static Image CARD_BTN_TEXT1 = new ImageIcon(ImageRef.class.getResource("settlerBtn/dev/cardBtnText1.png")).getImage();
    private final static Image CARD_BTN_TEXT2 = new ImageIcon(ImageRef.class.getResource("settlerBtn/dev/cardBtnText2.png")).getImage();

    private final static Image BUY_DEV_BTN_TEXT = new ImageIcon(ImageRef.class.getResource("settlerBtn/dev/buyDevBtnText.png")).getImage();

    private final static Image USE_DEV_BTN_TEXT1 = new ImageIcon(ImageRef.class.getResource("settlerBtn/dev/useDevBtnText1.png")).getImage();
    private final static Image USE_DEV_BTN_TEXT2 = new ImageIcon(ImageRef.class.getResource("settlerBtn/dev/useDevBtnText2.png")).getImage();

    //turn switch button images
    private final static Image TURN_SWITCH_TBN_TEXT0 = new ImageIcon(ImageRef.class.getResource("settlerBtn/turnSwitch/turnBtnText0.png")).getImage();
    private final static Image TURN_SWITCH_TBN_TEXT1 = new ImageIcon(ImageRef.class.getResource("settlerBtn/turnSwitch/turnBtnText1.png")).getImage();

    //trade buttons
    private final static Image TRADE_BTN = new ImageIcon(ImageRef.class.getResource("settlerBtn/maritimeTrade/tradeBtn.png")).getImage();
    private final static Image TRADE_DISABLED_BTN = new ImageIcon(ImageRef.class.getResource("settlerBtn/maritimeTrade/tradeBtnDisabled.png")).getImage();
    private final static Image TRADE_CANCEL = new ImageIcon(ImageRef.class.getResource("settlerBtn/maritimeTrade/tradeBtnCancel.png")).getImage();
    private final static Image TRADE_4 = new ImageIcon(ImageRef.class.getResource("settlerBtn/maritimeTrade/trade4to1Text.png")).getImage();
    private final static Image TRADE_3 = new ImageIcon(ImageRef.class.getResource("settlerBtn/maritimeTrade/trade3to1Text.png")).getImage();
    private final static Image TRADE_2 = new ImageIcon(ImageRef.class.getResource("settlerBtn/maritimeTrade/trade2to1Text.png")).getImage();
    //domestic trade buttons
    private final static Image TRADE_DOMESTIC = new ImageIcon(ImageRef.class.getResource("settlerBtn/domesticTrade/tradeDomesticText.png")).getImage();
    private final static Image LOCK_TRADE_DOMESTIC = new ImageIcon(ImageRef.class.getResource("settlerBtn/domesticTrade/lockTradeDomesticText.png")).getImage();
    private final static Image UNLOCK_TRADE_DOMESTIC = new ImageIcon(ImageRef.class.getResource("settlerBtn/domesticTrade/unlockTradeDomesticText.png")).getImage();
    //domestic trade accept button
    private final static Image ACCEPT_TRADE_DOMESTIC = new ImageIcon(ImageRef.class.getResource("settlerBtn/domesticTrade/acceptTradeDomesticText.png")).getImage();
    private final static Image CANCEL_TRADE_DOMESTIC = new ImageIcon(ImageRef.class.getResource("settlerBtn/domesticTrade/cancelTradeDomesticText.png")).getImage();

    //util buttons
    //build
    private final static Image BUILD_TEXT = new ImageIcon(ImageRef.class.getResource("settlerBtn/util/buildBtnText.png")).getImage();
    //exit buttons
    private final static Image EXIT_SAVE_TEXT = new ImageIcon(ImageRef.class.getResource("settlerBtn/util/exitSaveText.png")).getImage();
    private final static Image EXIT_NO_SAVE_TEXT = new ImageIcon(ImageRef.class.getResource("settlerBtn/util/exitNoSaveText.png")).getImage();

    //SD Main Menu buttons
    private final static Image BIG_BTN = new ImageIcon(ImageRef.class.getResource("settlerBtn/mainMenu/bigBtn.png")).getImage();
    private final static Image BIG_DISABLED_BTN = new ImageIcon(ImageRef.class.getResource("settlerBtn/mainMenu/bigBtnDisabled.png")).getImage();
    //Big shorts
    private final static Image BIG_SHORT_BTN = new ImageIcon(ImageRef.class.getResource("settlerBtn/mainMenu/bigShortBtn.png")).getImage();
    private final static Image BIG_SHORT_DISABLED_BTN = new ImageIcon(ImageRef.class.getResource("settlerBtn/mainMenu/bigShortBtnDisabled.png")).getImage();
    //Text Images
    private final static Image NEW_GAME_BTN_TEXT = new ImageIcon(ImageRef.class.getResource("settlerBtn/mainMenu/newGameText.png")).getImage();
    private final static Image LOAD_GAME_BTN_TEXT = new ImageIcon(ImageRef.class.getResource("settlerBtn/mainMenu/loadGameText.png")).getImage();
    private final static Image LOAD_AUTOSAVE_BTN_TEXT = new ImageIcon(ImageRef.class.getResource("settlerBtn/mainMenu/loadAutosaveText.png")).getImage();
    private final static Image OPTIONS_BTN_TEXT = new ImageIcon(ImageRef.class.getResource("settlerBtn/mainMenu/optionsText.png")).getImage();
    private final static Image JOIN_ONLINE_GAME_BTN_TEXT = new ImageIcon(ImageRef.class.getResource("settlerBtn/mainMenu/joinOnlineGameText.png")).getImage();
    private final static Image LOAD_GAME_TO_ONLINE_MODE_BTN_TEXT = new ImageIcon(ImageRef.class.getResource("settlerBtn/mainMenu/loadGameToOnlineModeText.png")).getImage();
    private final static Image CREDITS_BTN_TEXT = new ImageIcon(ImageRef.class.getResource("settlerBtn/mainMenu/creditsText.png")).getImage();
    private final static Image USER_MANUAL_BTN_TEXT = new ImageIcon(ImageRef.class.getResource("settlerBtn/mainMenu/userManualText.png")).getImage();
    private final static Image GAME_EXIT_BTN_TEXT = new ImageIcon(ImageRef.class.getResource("settlerBtn/mainMenu/exitText.png")).getImage();
    private final static Image BACK_BTN_TEXT = new ImageIcon(ImageRef.class.getResource("settlerBtn/mainMenu/backText.png")).getImage();

    //texts for the big button on the other menu pages
    private final static Image START_GAME_BTN_TEXT = new ImageIcon(ImageRef.class.getResource("settlerBtn/mainMenu/startGameText.png")).getImage();
    private final static Image SAVE_BTN_TEXT = new ImageIcon(ImageRef.class.getResource("settlerBtn/mainMenu/saveText.png")).getImage();
    private final static Image CONNECT_CLIENT_BTN_TEXT = new ImageIcon(ImageRef.class.getResource("settlerBtn/mainMenu/connectClientText.png")).getImage();
    private final static Image REQUEST_COLOUR_BTN_TEXT = new ImageIcon(ImageRef.class.getResource("settlerBtn/mainMenu/requestColourText.png")).getImage();

    //static button image arrays for text
    private final static Image[] CARD_BTN_TEXTS = new Image[]{CARD_BTN_TEXT1, CARD_BTN_TEXT2};
    private final static Image[] USE_DEV_BTN_TEXTS = new Image[]{USE_DEV_BTN_TEXT1, USE_DEV_BTN_TEXT2};
    //trade button ones
    private final static Image[] TRADE_BTN_4TO_TEXTS = new Image[]{TRADE_CANCEL, TRADE_4};
    private final static Image[] TRADE_BTN_3TO_TEXTS = new Image[]{TRADE_CANCEL, TRADE_3};
    private final static Image[] TRADE_BTN_2TO_TEXTS = new Image[]{TRADE_CANCEL, TRADE_2};
    private final static Image[] TRADE_BTN_DOM_TEXTS = new Image[]{TRADE_CANCEL, TRADE_DOMESTIC};
    private final static Image[] DOM_TRADE_LOCK_BTN_TEXTS = new Image[]{LOCK_TRADE_DOMESTIC, UNLOCK_TRADE_DOMESTIC};
    //util arrays
    private final static Image[] BUILD_BTN_TEXTS = new Image[]{TRADE_CANCEL, BUILD_TEXT};

    //constructors
    /**
     * Basic constructor
     */
    public SettlerBtn() {
        xPos = 0;
        yPos = 0;

        enabled = false;
        mouseHover = false;
        tabSelected = false;
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
    @Override
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
            case 7:
                //if the type is a dosmestic trade button
                baseImage = TRADE_BTN; //still has the right length
                disabledImage = TRADE_DISABLED_BTN;
                break;
            case 8:
                //if the type is a build button
                baseImage = TRADE_BTN; //still has the right length
                disabledImage = TRADE_DISABLED_BTN;
                break;
            case 9:
            //if the type is an exit without saving button
            case 10:
            //if the type is an exit with saving button
            case 11:
            //if it's a domestic trade lock button
            case 12:
            //if it's a domestic trade accept button
            case 13:
                //if it'a a cancel domestic trade button
                baseImage = CARD_BTN; //still has the right length
                disabledImage = CARD_DISABLED_BTN;
                break;
            case 14:
            //If it's a new game button
            case 15:
            //If it's a load game button
            case 16:
            //if it's a Join onlime game button
            case 17:
            //if it's a load game to online mode button
            case 18:
                //if it's a big exit button for the SD main menu
                baseImage = BIG_BTN;
                disabledImage = BIG_DISABLED_BTN;
                break;
            case 19:
            //if it's a big short load autosave button for the SD main menu
            case 20:
            //if it's a big short options button for the SD main menu
            case 21:
            //if it's a big short credits button for the SD main menu
            case 22:
                //if it's a big short user manual button for the SD main menu
                baseImage = BIG_SHORT_BTN;
                disabledImage = BIG_SHORT_DISABLED_BTN;
                break;
            case 23:
            //if it's a big back button
            case 24:
            //if it's a big Start Game Button
            case 25:
            //if it's a big save Button
            case 26:
            //if it's a big connect button for clients of the game server
            case 27:
                //if it's a big request colour button
                baseImage = BIG_BTN;
                disabledImage = BIG_DISABLED_BTN;
                break;
            default:
                //default to error images
                baseImage = ERROR_IMAGE;
                disabledImage = ERROR_IMAGE;
                break;
        }

        hoverImage = HOVER_BTN;
        tabSelectionImages = new Image[]{FOCUS_LEFT, FOCUS_RIGHT};
    }

    /**
     * Update the textImage variable to reflect the text assigned by the mode
     * variable
     */
    @Override
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
                //check its in end turn mode or start turn mode
                if (mode > 4 && mode < 9) { //if the mode is greater than 4 then it's the ending text
                    textImage = TURN_SWITCH_TBN_TEXT0;
                } else if (mode > 0 && mode < 5) { //if in the range 1-4 it's a start text mode
                    textImage = TURN_SWITCH_TBN_TEXT1;
                } else {
                    textImage = ERROR_IMAGE;
                }

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
            case 7:
                //if the type is a trade 4:1 button
                textImage = TRADE_BTN_DOM_TEXTS[mode];
                break;
            case 8:
                //if the type is a trade 4:1 button
                textImage = BUILD_BTN_TEXTS[mode];
                break;
            case 9:
                //if the type is a trade 4:1 button
                textImage = EXIT_NO_SAVE_TEXT;
                break;
            case 10:
                //if the type is a trade 4:1 button
                textImage = EXIT_SAVE_TEXT;
                break;
            case 11:
                //if the type if a domestic trade lock button
                textImage = DOM_TRADE_LOCK_BTN_TEXTS[mode];
                break;
            case 12:
                //if the type is a domestic trade accept button
                textImage = ACCEPT_TRADE_DOMESTIC;
                break;
            case 13:
                //if its a cancel domestic trade button
                textImage = CANCEL_TRADE_DOMESTIC;
                break;
            case 14:
                //if it's a big new game button for the SD main menu
                textImage = NEW_GAME_BTN_TEXT;
                break;
            case 15:
                //if it's a big load game button for the SD main menu
                textImage = LOAD_GAME_BTN_TEXT;
                break;
            case 16:
                //if it's a big join online gam button for the SD main menu
                textImage = JOIN_ONLINE_GAME_BTN_TEXT;
                break;
            case 17:
                //if it's a big load to online mode button for the SD main menu
                textImage = LOAD_GAME_TO_ONLINE_MODE_BTN_TEXT;
                break;
            case 18:
                //if it's a big exit button for the SD main menu
                textImage = GAME_EXIT_BTN_TEXT;
                break;
            case 19:
                //if it's a load autosave button for the SD main menu
                textImage = LOAD_AUTOSAVE_BTN_TEXT;
                break;
            case 20:
                //if it's a options button for the SD main menu
                textImage = OPTIONS_BTN_TEXT;
                break;
            case 21:
                //if it's a credits button for the SD main menu
                textImage = CREDITS_BTN_TEXT;
                break;
            case 22:
                //if it's a user manual button for the SD main menu
                textImage = USER_MANUAL_BTN_TEXT;
                break;
            case 23:
                //if it's a big back button
                textImage = BACK_BTN_TEXT;
                break;
            case 24:
                //if it's a big start game button
                textImage = START_GAME_BTN_TEXT;
                break;
            case 25:
                //if it's a big start game button
                textImage = SAVE_BTN_TEXT;
                break;
            case 26:
                //if it's a big connect button for clients of the game server
                textImage = CONNECT_CLIENT_BTN_TEXT;
                break;
            case 27:
                //if it's a big request colour button
                textImage = REQUEST_COLOUR_BTN_TEXT;
                break;
            default:
                //deflault to error image
                textImage = ERROR_IMAGE;
                break;
        }
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
