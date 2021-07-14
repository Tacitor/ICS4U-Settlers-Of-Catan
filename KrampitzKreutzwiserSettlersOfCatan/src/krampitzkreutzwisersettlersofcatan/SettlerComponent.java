/*
 * Lukas Krampitz
 * Jul 14, 2021
 * 
 */
package krampitzkreutzwisersettlersofcatan;

import java.awt.Image;

/**
 *
 * @author Tacitor
 */
public abstract class SettlerComponent extends WorldObject {
    
    //data attribute
    protected boolean enabled; //stores whether or not the button is enabled    
    protected int type; //the type of button. (Trade button, card toggle, build, etc.)
    protected boolean mouseHover; //is the mouse currently being hovered over this button
    
    protected Image baseImage; //the main image of the button
    protected Image disabledImage; //the overlay to dim the button to indecate a disabled button
    protected Image textImage; //the text of the button
    protected Image hoverImage; //the layer to add if there is a hover over the button
    
    //methods
    public abstract void updateButtonImages();
    public abstract void updateText();
    
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
    public boolean isEnabled() {
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
     * Return the Image for the hover overlay of the button
     *
     * @return
     */
    public Image getHoverImage() {
        return hoverImage;
    }

    /**
     * Access the enabled state of the mouseHover
     *
     * @return mouseHover
     */
    public boolean isMouseHover() {
        return mouseHover;
    }

    /**
     * Mutate the enabled state of the mouseHover
     *
     * @param mouseHover
     */
    public void setmouseHover(boolean mouseHover) {
        this.mouseHover = mouseHover;
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
    
}
