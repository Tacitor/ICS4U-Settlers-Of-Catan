/*
 * Lukas Krampitz
 * Jul 10, 2021
 * A custom radio button that fits the sytle of the game better and is easier to use than Swing button
 */
package krampitzkreutzwisersettlersofcatan.worldObjects.buttons;

import java.awt.Graphics2D;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import krampitzkreutzwisersettlersofcatan.gui.GamePanel;
import krampitzkreutzwisersettlersofcatan.gui.SDScaleImageResizeable;
import krampitzkreutzwisersettlersofcatan.worldObjects.WorldObject;
import textures.ImageRef;
import static textures.ImageRef.ERROR_IMAGE;

/**
 *
 * @author Tacitor
 */
public class SettlerRadioBtn extends SettlerComponent {

    //data attributes
    private boolean selected; //is this radio button selected
    private SettlerRadioBtn[] groupedRadioBtns; //other radio buttons that ARE NOT this one. They will be deselected whenever this one is
    //image attribute
    private Image selectionImage; //the 'x' to show that this is the selected button

    //static radio button images
    private final static Image RADIO_BTN_BASE = new ImageIcon(ImageRef.class.getResource("settlerBtn/util/radioBtn.png")).getImage();
    private final static Image RADIO_BTN_DISABLED = new ImageIcon(ImageRef.class.getResource("settlerBtn/util/radioBtnDisabled.png")).getImage();
    private final static Image RADIO_BTN_SELECTION = new ImageIcon(ImageRef.class.getResource("settlerBtn/util/rBtnSelection.png")).getImage();
    private final static Image RADIO_BTN_HOVER = new ImageIcon(ImageRef.class.getResource("settlerBtn/util/radioHoverBtn.png")).getImage();
    //statics for the smaller radio buttons
    private final static Image RADIO_SML_BTN_BASE = new ImageIcon(ImageRef.class.getResource("settlerBtn/mainMenu/radio/radioSmallBtn.png")).getImage();
    private final static Image RADIO_SML_BTN_DISABLED = new ImageIcon(ImageRef.class.getResource("settlerBtn/mainMenu/radio/radioSmallBtnDisabled.png")).getImage();
    private final static Image RADIO_SML_BTN_SELECTION = new ImageIcon(ImageRef.class.getResource("settlerBtn/mainMenu/radio/rSmallBtnSelection.png")).getImage();
    private final static Image RADIO_SML_BTN_HOVER = new ImageIcon(ImageRef.class.getResource("settlerBtn/mainMenu/radio/radioSmallHoverBtn.png")).getImage();
    //the texts for the game panel
    private final static Image RADIO_BTN_ROAD_TEXT = new ImageIcon(ImageRef.class.getResource("settlerBtn/util/roadBtnText.png")).getImage();
    private final static Image RADIO_BTN_SETTLEMENT_TEXT = new ImageIcon(ImageRef.class.getResource("settlerBtn/util/settlementBtnText.png")).getImage();
    private final static Image RADIO_BTN_CITY_TEXT = new ImageIcon(ImageRef.class.getResource("settlerBtn/util/cityBtnText.png")).getImage();
    //the texts for the menu
    private final static Image RADIO_BTN_YES_TEXT = new ImageIcon(ImageRef.class.getResource("settlerBtn/mainMenu/radio/yesBtnText.png")).getImage();
    private final static Image RADIO_BTN_NO_TEXT = new ImageIcon(ImageRef.class.getResource("settlerBtn/mainMenu/radio/noBtnText.png")).getImage();
    private final static Image RADIO_BTN_2_TEXT = new ImageIcon(ImageRef.class.getResource("settlerBtn/mainMenu/radio/2BtnText.png")).getImage();
    private final static Image RADIO_BTN_3_TEXT = new ImageIcon(ImageRef.class.getResource("settlerBtn/mainMenu/radio/3BtnText.png")).getImage();
    private final static Image RADIO_BTN_4_TEXT = new ImageIcon(ImageRef.class.getResource("settlerBtn/mainMenu/radio/4BtnText.png")).getImage();

    //constructors
    /**
     * Basic constructor
     */
    public SettlerRadioBtn() {
        xPos = 0;
        yPos = 0;

        enabled = false;
        selected = false;
        mouseHover = false;
        tabSelected = false;
        type = 0; //set to road radio button

        //set up the button group
        groupedRadioBtns = new SettlerRadioBtn[0];

        updateText(); //update the text
        updateButtonImages(); //update the base image and disabled images
    }

    /**
     * Half constructor
     *
     * @param enabled
     * @param selected
     * @param type
     */
    public SettlerRadioBtn(boolean enabled, boolean selected, int type) {
        this();
        xPos = -1; //set the coords to negative one as this constuctor is used when the coordinates cannot be known
        yPos = -1;

        this.enabled = enabled;
        this.selected = selected;
        this.type = type; //set to the button types passed in as parameter

        updateText(); //update the text
        updateButtonImages(); //update the base image and disabled images
    }

    /**
     * Full constructor
     *
     * @param xPos
     * @param yPos
     * @param enabled
     * @param selected
     * @param type
     */
    public SettlerRadioBtn(int xPos, int yPos, boolean enabled, boolean selected, int type) {
        this(enabled, selected, type);

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
                //if type is road radio button
                baseImage = RADIO_BTN_BASE;
                disabledImage = RADIO_BTN_DISABLED;
                selectionImage = RADIO_BTN_SELECTION;
                hoverImage = RADIO_BTN_HOVER;
                tabSelectionImages = new Image[]{FOCUS_LEFT, FOCUS_RIGHT};
                break;
            case 1:
                //if type is settlement radio button
                baseImage = RADIO_BTN_BASE;
                disabledImage = RADIO_BTN_DISABLED;
                selectionImage = RADIO_BTN_SELECTION;
                hoverImage = RADIO_BTN_HOVER;
                tabSelectionImages = new Image[]{FOCUS_LEFT, FOCUS_RIGHT};
                break;
            case 2:
                //if type is city radio button
                baseImage = RADIO_BTN_BASE;
                disabledImage = RADIO_BTN_DISABLED;
                selectionImage = RADIO_BTN_SELECTION;
                hoverImage = RADIO_BTN_HOVER;
                tabSelectionImages = new Image[]{FOCUS_LEFT, FOCUS_RIGHT};
                break;
            default:
                //default to error images
                baseImage = ERROR_IMAGE;
                disabledImage = ERROR_IMAGE;
                selectionImage = ERROR_IMAGE;
                hoverImage = RADIO_BTN_HOVER;
                tabSelectionImages = new Image[]{FOCUS_LEFT, FOCUS_RIGHT};
                break;
        }
    }

    /**
     * Update the textImage variable to reflect the text assigned by the mode
     * variable
     */
    @Override
    public void updateText() {
        switch (type) {
            case 0:
                //if type is road radio button
                textImage = RADIO_BTN_ROAD_TEXT;
                break;
            case 1:
                //if type is settlement radio button
                textImage = RADIO_BTN_SETTLEMENT_TEXT;
                break;
            case 2:
                //if type is city radio button
                textImage = RADIO_BTN_CITY_TEXT;
                break;
            default:
                //deflault to error image
                textImage = ERROR_IMAGE;
                break;
        }
    }

    /**
     * This method is used to make other methods interoperable with deprecated
     * called from the gamePanel and calls from other classes.
     *
     * @param image
     * @param parent
     * @return
     */
    private int custGetImgWidth(Image image, JComponent parent) {

        //check if need to use gamepanel
        if (parent instanceof GamePanel) {
            return ((GamePanel) parent).getImgWidth(image);
        } else if (parent instanceof SDScaleImageResizeable) {
            return ((SDScaleImageResizeable) parent).getLocalImgWidth(image);
        } else {
            System.out.println("ERROR: custGetImgWidth() in SettlerRadioBtn does not have a case for this type of JComponent");
            return 50;
        }
    }

    /**
     * This method is used to make other methods interoperable with deprecated
     * called from the gamePanel and calls from other classes.
     * 
     * @param image
     * @param parent
     * @return 
     */
    private int custGetImgHeight(Image image, JComponent parent) {

        //check if need to use gamepanel
        if (parent instanceof GamePanel) {
            return ((GamePanel) parent).getImgHeight(image);
        } else if (parent instanceof SDScaleImageResizeable) {
            return ((SDScaleImageResizeable) parent).getLocalImgHeight(image);
        } else {
            System.out.println("ERROR: custGetImgHeight() in SettlerRadioBtn does not have a case for this type of JComponent");
            return 50;
        }
    }

    /**
     * Draw the radio button
     *
     * @param g2d
     * @param parent - the parent responsible for the sizing
     */
    public void draw(Graphics2D g2d, JComponent parent) {

        //draw the base        
        g2d.drawImage(baseImage,
                xPos,
                yPos,
                custGetImgWidth(baseImage, parent),
                custGetImgHeight(baseImage, parent), null);

        //draw the text
        g2d.drawImage(textImage,
                xPos,
                yPos,
                custGetImgWidth(textImage, parent),
                custGetImgHeight(textImage, parent), null);

        //draw the selected overlay if required
        if (selected) {
            g2d.drawImage(selectionImage,
                    xPos,
                    yPos,
                    custGetImgWidth(selectionImage, parent),
                    custGetImgHeight(selectionImage, parent), null);
        }

        //draw the tab selected overlay if required
        if (tabSelected) {
            //draw the left
            g2d.drawImage(tabSelectionImages[0],
                    xPos - GamePanel.scaleInt(5),
                    yPos - GamePanel.scaleInt(5),
                    custGetImgWidth(tabSelectionImages[0], parent),
                    custGetImgHeight(tabSelectionImages[0], parent), null);
            //draw the right
            g2d.drawImage(tabSelectionImages[1],
                    xPos + custGetImgWidth(baseImage, parent) + GamePanel.scaleInt(5) - custGetImgWidth(tabSelectionImages[1], parent),
                    yPos - GamePanel.scaleInt(5),
                    custGetImgWidth(tabSelectionImages[1], parent),
                    custGetImgHeight(tabSelectionImages[1], parent), null);
        }

        //draw the disabled overlay if required
        if (!enabled) {
            g2d.drawImage(disabledImage,
                    xPos,
                    yPos,
                    custGetImgWidth(disabledImage, parent),
                    custGetImgHeight(disabledImage, parent), null);
        }

        //draw the mouseHover overlay if required
        if (mouseHover) {
            g2d.drawImage(hoverImage,
                    xPos,
                    yPos,
                    custGetImgWidth(hoverImage, parent),
                    custGetImgHeight(hoverImage, parent), null);
        }

    }

    /**
     * Access the selected state of the button
     *
     * @return
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Mutate the selected state of the button
     *
     * @param selected
     */
    public void setSelected(boolean selected) {

        //check if this is being selected
        if (selected) {
            //deselect the other
            for (SettlerRadioBtn RBtn : groupedRadioBtns) {
                RBtn.setSelected(false);
            }
        }

        this.selected = selected;
    }

    /**
     * Mutate the enabled state of the button
     *
     * @param RBtn
     */
    public void addGroupedRadioBtn(SettlerRadioBtn RBtn) {
        //create a new array
        SettlerRadioBtn[] newArray = new SettlerRadioBtn[groupedRadioBtns.length + 1];

        //get all the same as from the old one
        System.arraycopy(groupedRadioBtns, 0, newArray, 0, groupedRadioBtns.length);

        //add the new one to the end
        newArray[groupedRadioBtns.length] = RBtn;

        //set the new array
        groupedRadioBtns = newArray;

    }

    /**
     * Clear the group
     */
    public void clearGroupedRadioBtns() {
        //remove all the values
        groupedRadioBtns = new SettlerRadioBtn[0];
    }

    /**
     * Access the buttons that are listed as being in the group of this radio
     * button
     *
     * @return
     */
    public SettlerRadioBtn[] getRadioBtnGroup() {
        return groupedRadioBtns;
    }

    /**
     * Check if this object's attributes are equal to another object's
     *
     * @param other The Button to compare to
     * @return Whether or not the objects are the same
     */
    public boolean equals(SettlerRadioBtn other) {
        // Return if the position of the objects are equal or not
        return super.equals(other)
                && enabled == other.enabled
                && selected == other.selected
                && type == other.type
                && baseImage == other.baseImage
                && textImage == other.textImage
                && disabledImage == other.disabledImage
                && selectionImage == other.selectionImage;
    }

    @Override
    public WorldObject clone() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Given an array of SettlerRadioBtns set them up to acknowledge the others
     * are in the group
     *
     * @param groupBtns
     */
    public static void setUpGroup(SettlerRadioBtn[] groupBtns) {
        //loop through the buttons
        for (SettlerRadioBtn buttonAddTo : groupBtns) {

            //loop through the buttons again to add them
            for (SettlerRadioBtn buttonToAdd : groupBtns) {

                //make sure it's not the same button
                if (buttonAddTo != buttonToAdd) {
                    buttonAddTo.addGroupedRadioBtn(buttonToAdd);
                }
            }
        }
    }

    /**
     * Out of a group of SettlerBtns, return the selected one
     *
     * @param groupBtns
     * @return
     */
    public static SettlerRadioBtn getGroupSelection(SettlerRadioBtn[] groupBtns) {
        SettlerRadioBtn theSelectedOne = null;

        //loop through the buttons
        for (SettlerRadioBtn radioBtn : groupBtns) {
            if (radioBtn.isSelected()) {
                theSelectedOne = radioBtn;
            }
        }

        return theSelectedOne;

    }

}
