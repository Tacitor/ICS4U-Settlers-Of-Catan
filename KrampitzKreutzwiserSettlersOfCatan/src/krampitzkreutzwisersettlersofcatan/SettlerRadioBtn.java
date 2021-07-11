/*
 * Lukas Krampitz
 * Jul 10, 2021
 * A custom radio button that fits the sytle of the game better and is easier to use than Swing button
 */
package krampitzkreutzwisersettlersofcatan;

import java.awt.Graphics2D;
import java.awt.Image;
import javax.swing.ImageIcon;
import textures.ImageRef;
import static textures.ImageRef.ERROR_IMAGE;

/**
 *
 * @author Tacitor
 */
public class SettlerRadioBtn extends WorldObject {

    //data attribu
    private boolean enabled; //stores whether or not the button is enabled
    private boolean selected; //is this radio button selected
    private int type; //the type of button. (Road button, city toggle, settlemtn, etc.)
    private SettlerRadioBtn[] groupedRadioBtns; //other radio buttons that ARE NOT this one. They will be deselected whenever this one is
    //image attributes
    private Image baseImage; //the main image of the button
    private Image disabledImage; //the overlay to dim the button to indecate a disabled button
    private Image textImage; //the text of the button
    private Image selectionImage; //the 'x' to show that this is the selected button

    //static radio button images
    private final static Image RADIO_BTN_BASE = new ImageIcon(ImageRef.class.getResource("settlerBtn/util/radioBtn.png")).getImage();
    private final static Image RADIO_BTN_DISABLED = new ImageIcon(ImageRef.class.getResource("settlerBtn/util/radioBtnDisabled.png")).getImage();
    private final static Image RADIO_BTN_SELECTION = new ImageIcon(ImageRef.class.getResource("settlerBtn/util/rBtnSelection.png")).getImage();
    //the texts
    private final static Image RADIO_BTN_ROAD_TEXT = new ImageIcon(ImageRef.class.getResource("settlerBtn/util/roadBtnText.png")).getImage();
    private final static Image RADIO_BTN_SETTLEMENT_TEXT = new ImageIcon(ImageRef.class.getResource("settlerBtn/util/settlementBtnText.png")).getImage();
    private final static Image RADIO_BTN_CITY_TEXT = new ImageIcon(ImageRef.class.getResource("settlerBtn/util/cityBtnText.png")).getImage();

    //constructors
    /**
     * Basic constructor
     */
    public SettlerRadioBtn() {
        xPos = 0;
        yPos = 0;

        enabled = false;
        selected = false;
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
    public void updateButtonImages() {
        switch (type) {
            case 0:
                //if type is road radio button
                baseImage = RADIO_BTN_BASE;
                disabledImage = RADIO_BTN_DISABLED;
                selectionImage = RADIO_BTN_SELECTION;
                break;
            case 1:
                //if type is settlement radio button
                baseImage = RADIO_BTN_BASE;
                disabledImage = RADIO_BTN_DISABLED;
                selectionImage = RADIO_BTN_SELECTION;
                break;
            case 2:
                //if type is city radio button
                baseImage = RADIO_BTN_BASE;
                disabledImage = RADIO_BTN_DISABLED;
                selectionImage = RADIO_BTN_SELECTION;
                break;
            default:
                //default to error images
                baseImage = ERROR_IMAGE;
                disabledImage = ERROR_IMAGE;
                selectionImage = ERROR_IMAGE;
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
     * Draw the radio button
     *
     * @param g2d
     * @param theGamePanel
     */
    public void draw(Graphics2D g2d, GamePanel theGamePanel) {

        //draw the base        
        g2d.drawImage(baseImage,
                xPos,
                yPos,
                theGamePanel.getImgWidth(baseImage),
                theGamePanel.getImgHeight(baseImage), null);

        //draw the text
        g2d.drawImage(textImage,
                xPos,
                yPos,
                theGamePanel.getImgWidth(textImage),
                theGamePanel.getImgHeight(textImage), null);

        //draw the selected overlay if required
        if (selected) {
            g2d.drawImage(selectionImage,
                    xPos,
                    yPos,
                    theGamePanel.getImgWidth(selectionImage),
                    theGamePanel.getImgHeight(selectionImage), null);
        }

        //draw the disabled overlay if required
        if (!enabled) {
            g2d.drawImage(disabledImage,
                    xPos,
                    yPos,
                    theGamePanel.getImgWidth(disabledImage),
                    theGamePanel.getImgHeight(disabledImage), null);
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

}
