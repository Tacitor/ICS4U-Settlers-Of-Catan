/*
 * Lukas Krampitz
 * Sep 2, 2023
 * A custom text box the fits the test of the Settlers of Catan Theme
 */
package krampitzkreutzwisersettlersofcatan.worldObjects.buttons;

import animation.TextBxAnimationData;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
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
    protected boolean selected; //did the user click on the text box and select it.
    protected int cursorPos; //the position of the cursor. This is where text gets added and removed from
    protected int startDisplayPos; //the position of where to start displaying the text withing the box.
    protected char[] chars; //the char array that will contain the text of the text box.
    private TextBxAnimationData textBxAnimationData;

    private StringBuilder drawnSB; //the string builder used to assemble a string when it needs to draw the char array stored in this text box

    //static attributes    
    private final static Image TEXT_BOX_REG_BASE = new ImageIcon(ImageRef.class.getResource("settlerBtn/mainMenu/radio/radioLongBtn.png")).getImage();
    private final static Image TEXT_BOX_REG_HOVER = new ImageIcon(ImageRef.class.getResource("settlerBtn/mainMenu/radio/radioLongHoverBtn.png")).getImage();
    private final static int CURSOR_BLINK_FRAME_NUM = 2; //the number of frames in the blink sequence of the cursor (just 2: on, and off)

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
        selected = false;
        cursorPos = 0;
        startDisplayPos = 0;
        chars = new char[128];

        textBxAnimationData = new TextBxAnimationData();

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
                hoverImage = TEXT_BOX_REG_HOVER;
                break;

            default:
                //default to error images
                baseImage = ImageRef.ERROR_IMAGE;
                disabledImage = ImageRef.ERROR_IMAGE;
                hoverImage = ImageRef.ERROR_IMAGE;
                break;
        }

        tabSelectionImages = new Image[]{FOCUS_LEFT, FOCUS_RIGHT};
    }

    @Override
    public void updateText() {
        textImage = null;
    }

    /**
     * Get the current frame of the animation. For the cursor, whether or not it
     * is blinking. Return true if the cursor should be displayed.
     *
     * @return
     */
    public boolean getAnimationFrame() {
        //the image the method will return
        boolean drawCursor;
        int frameTime;

        //the index of the array that contains the current frame of animation
        int frameIndex;

        //pick one of the frame times
        frameTime = textBxAnimationData.getFrameTimeBlink();

        //decide if a new frame needs to be displayed or if the current one is still the one it should be on
        if (System.currentTimeMillis() - (textBxAnimationData.getLastFrameStart()) > frameTime) {
            //yes it is time for a new frame

            //debug frame times
            //System.out.println("Frame time: " + (System.currentTimeMillis() - lastFrameStart));
            //calculate the index the frame needs to be pulled from
            frameIndex = textBxAnimationData.getCurrentFrameIndex() + 1; //the new frame will just be one after the current one

            //and make a check that it won't be out of bounds
            if (frameIndex >= CURSOR_BLINK_FRAME_NUM) {
                frameIndex = 0; //reset it to the beginning
            }

            //get the new frame
            drawCursor = frameIndex == 1;

            //update the time
            textBxAnimationData.setLastFrameStart(System.currentTimeMillis());

            //update the frame index
            textBxAnimationData.setCurrentFrameIndex(frameIndex);

        } else { //if the minimum frame has not yet passed pass the current frame again
            drawCursor = textBxAnimationData.getCurrentFrameIndex() == 1;
        }

        return drawCursor;
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

            //save the colour it came with to restore afterwards
            Color colour = g2d.getColor();
            Font font = g2d.getFont();

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

            //draw the tab selected overlay if required
            if (tabSelected) {
                //draw the left
                g2d.drawImage(tabSelectionImages[0],
                        xPos - SDParent.localScaleInt(5),
                        yPos - SDParent.localScaleInt(5),
                        SDParent.getLocalImgWidth(tabSelectionImages[0]),
                        SDParent.getLocalImgHeight(tabSelectionImages[0]),
                        null);
                //draw the right
                g2d.drawImage(tabSelectionImages[1],
                        xPos + SDParent.getLocalImgWidth(baseImage) + SDParent.localScaleInt(5) - SDParent.getLocalImgWidth(tabSelectionImages[1]),
                        yPos - SDParent.localScaleInt(5),
                        SDParent.getLocalImgWidth(tabSelectionImages[1]),
                        SDParent.getLocalImgHeight(tabSelectionImages[1]),
                        null);
            }

            g2d.setColor(new Color(74, 54, 37));
            g2d.setFont(new Font(font.getName(), font.getStyle(), SDParent.localScaleInt(30)));

            //draw the text
            //generate the string to draw
            generateStringBuilder();

            //ensure that the string can be displayed fully within the bounds of the text box
            stringDisplayCutoff(g2d, SDParent);

            drawnSB.trimToSize();
            //draw the text
            g2d.drawString(drawnSB.toString(),
                    xPos + SDParent.localScaleInt(10),
                    yPos + SDParent.localScaleInt(28));

            //draw the blinking cursor if selected
            if (selected && getAnimationFrame()) {
                //draw a small cursor
                g2d.fillRect(xPos + SDParent.localScaleInt(10) + g2d.getFontMetrics().stringWidth(drawnSB.toString().substring(0, cursorPos - startDisplayPos)),
                        yPos + SDParent.localScaleInt(8),
                        SDParent.localScaleInt(3),
                        SDParent.localScaleInt(SDParent.getLocalImgHeight(baseImage) - SDParent.localScaleInt(16)));
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

            //reset the colour to what it came in with
            g2d.setColor(colour);
            g2d.setFont(font);

        } else {
            System.out.println("ERROR: The parent JComponent in draw method of SettlerTxtBx does not implement SDScaleImageResizeable.");
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
        this.selected = selected;

        //if the box gets deselected reset the animation
        if (!selected) {
            textBxAnimationData.setCurrentFrameIndex(0);
            textBxAnimationData.setLastFrameStart(0);
        }
    }

    /**
     * What to do when the user clicks a key on their keyboard This will be
     * called by the SDJoinOnlineGameMenu which was called by the
     * SDMainMenuPanel which was called by the SDMenuFrame
     *
     * @param evt
     */
    public void keyPress(KeyEvent evt) {
        //only add in certain ASCII values. Care about alpha numberic, colons, slashed, dots, etc.
        //Allow: A-Z, a-z, 0-9, -, ., _, ~, :, /, ?, #, [, ], @, !, $, &, ', (, ), *, +, ,, ;, %, and = 

        int keyCharCode = (int) evt.getKeyChar();

        //check within the interested ranges
        if ((keyCharCode >= 33 && keyCharCode <= 59)
                || keyCharCode == 61
                || (keyCharCode >= 63 && keyCharCode <= 91)
                || keyCharCode == 93
                || keyCharCode == 95
                || (keyCharCode >= 97 && keyCharCode <= 126)) {

            //add it to the char array if it's within bounds
            if (cursorPos < chars.length) {
                chars[cursorPos] = (char) keyCharCode;
                //move the cursor
                cursorPos++;

                //clear the String builder now that the text has changed
                drawnSB = null;
            }

        }

    }

    /**
     * Assemble a String Builder based off the stored chars and the start index.
     */
    private void generateStringBuilder() {
        //set up the string to draw
        drawnSB = new StringBuilder();
        //loop through the chars and assemble a string
        for (int i = startDisplayPos; i < chars.length && (int) chars[i] != 0; i++) {
            drawnSB.append(chars[i]);
        }
    }

    /**
     * Recursively check if the string is short enough to fit in the text box
     *
     * @param g2d
     */
    private void stringDisplayCutoff(Graphics2D g2d, SDScaleImageResizeable SDParent) {

        //check if String builder exists
        if (drawnSB == null) {

            System.out.println("ERROR: String builder drawnSB has not been initializied in stringDisplayCutoff()");
        } //check if it would go out of bounds
        else if (g2d.getFontMetrics().stringWidth(drawnSB.toString()) > (SDParent.getLocalImgWidth(baseImage) - ((SDParent.localScaleInt(10)) * 2))) {
            //move the start index over
            startDisplayPos++;

            //rerender the string
            generateStringBuilder();
        }
    }

    @Override
    public WorldObject clone() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
