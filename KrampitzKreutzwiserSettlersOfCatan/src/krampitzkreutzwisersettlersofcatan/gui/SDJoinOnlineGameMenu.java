/*
 * Lukas Krampitz
 * Septembre 02, 2023
 * The JPanel for the Join Online Game Menu now using Settler Dev Buttons.
 */
package krampitzkreutzwisersettlersofcatan.gui;

import dataFiles.OldCode;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.InputStream;
import java.util.Scanner;
import krampitzkreutzwisersettlersofcatan.worldObjects.buttons.SettlerBtn;
import krampitzkreutzwisersettlersofcatan.worldObjects.buttons.SettlerLbl;
import krampitzkreutzwisersettlersofcatan.worldObjects.buttons.SettlerRadioBtn;
import krampitzkreutzwisersettlersofcatan.worldObjects.buttons.SettlerTxtBx;
import textures.ImageRef;

/**
 *
 * @author Tacitor
 */
public class SDJoinOnlineGameMenu extends javax.swing.JPanel implements MouseMotionListener, SDScaleImageResizeable {

    private SDMenuFrame sDMenuFrame;
    private static double localScaleFactor; //The factor to scale this panel by when drawing elemets
    private int mouseMotionPosX; //acording to the MouseMotionListener where is the mouse located
    private int mouseMotionPosY;

    //Settler Compoments
    private SettlerBtn exitBtn;
    //The array for the buttons
    private SettlerBtn[] settlerBtns;
    //Settler Labels
    private SettlerLbl mainDesc, connectionIPLbl, connectionPortLbl;
    //The array for the buttons
    private SettlerLbl[] settlerLbls;
    //Settler Radio Buttons
    //private SettlerRadioBtn RadioBtn;
    //arry for each group of radio buttons
    //private SettlerRadioBtn[] settlerRadioGROUPBtns;
    //main array for all the radio buttons groups
    private SettlerRadioBtn[][] settlerRadioBtnGroups;
    //settler text boxes
    private SettlerTxtBx connectionIPTxtBx;
    //the array for the text boxes
    private SettlerTxtBx[] settlerTxtBxs;

    //Fonts
    public Font COMPASS_GOLD;

    /**
     * Main Constructor
     *
     * @param sDFrame
     */
    public SDJoinOnlineGameMenu(SDMenuFrame sDFrame) {
        sDMenuFrame = sDFrame;

        COMPASS_GOLD = sDMenuFrame.setUpCompassGoldFont();

        //add the mouse motion listener
        addMouseMotionListener(this);

        //add a mouse listener that call the mouse click event handler
        addMouseListener(new MouseAdapter() {
            /**
             * Triggered when the user clicks on the SDCreditsPanel. Calls the
             * menu panel's click event method.
             *
             * @param event
             */
            @Override
            public final void mouseReleased(MouseEvent event) {
                //send the mouse event to the menu panel click handler
                mouseClick(event);
            }
        });

        //setup the buttons        
        exitBtn = new SettlerBtn(true, 0, 23);
        //add them to the array
        settlerBtns = new SettlerBtn[]{exitBtn};
        //Setup the labels
        mainDesc = new SettlerLbl("To join an online game please ensure the folowing:");
        mainDesc.setForeground(DomesticTradePanel.BEIGE_COLOUR);
        mainDesc.setLinewrapSpace(40);
        mainDesc.setUseNewLineChar(true);
        connectionIPLbl = new SettlerLbl("Destination IP Address or server URL:");
        connectionIPLbl.setForeground(DomesticTradePanel.BEIGE_COLOUR);
        connectionPortLbl = new SettlerLbl("Connection port:");
        connectionPortLbl.setForeground(DomesticTradePanel.BEIGE_COLOUR);

        //add them to the array
        settlerLbls = new SettlerLbl[]{mainDesc, connectionIPLbl, connectionPortLbl};

        //setup the radio buttons        
        //go here
        //add them to the group array
        //go here
        //add the group to the main array
        settlerRadioBtnGroups = new SettlerRadioBtn[0][];

        //setup the custom radio buttons to go into the groups
        for (SettlerRadioBtn[] grp : settlerRadioBtnGroups) {
            SettlerRadioBtn.setUpGroup(grp);
        }

        //setup the text boxes
        connectionIPTxtBx = new SettlerTxtBx(true, 0);
        //add it to the array
        settlerTxtBxs = new SettlerTxtBx[]{connectionIPTxtBx};

        loadMaterial();

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
     * Draw the UI for the SDPanel
     *
     * @param g
     */
    private void draw(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;

        //update the scale factor
        localScaleFactor = sDMenuFrame.calcScaleFactor(this);

        //update the button positions
        settlerVarPos(g2d);

        //draw the background image
        g2d.drawImage(ImageRef.WOOD_BACKGROUND,
                0,
                0,
                this.getWidth(),
                this.getHeight(), this);

        g2d.setFont(new Font(COMPASS_GOLD.getName(), Font.PLAIN, localScaleInt(120)));
        g2d.setColor(DomesticTradePanel.BEIGE_COLOUR);

        //Draw the Title
        g2d.drawString("Join Online Game Server",
                (this.getWidth() / 2) - (g2d.getFontMetrics().stringWidth("Join Online Game Server") / 2),
                localScaleInt(100));

        g2d.setFont(new Font(COMPASS_GOLD.getName(), Font.PLAIN, localScaleInt(70)));

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
        //draw the radio buttons
        //itterate over the groups
        for (SettlerRadioBtn[] settlerRadioBtnGroup : settlerRadioBtnGroups) {
            for (SettlerRadioBtn settlerRadioBtn : settlerRadioBtnGroup) {
                settlerRadioBtn.draw(g2d, this);
            }
        }

        //go through and draw all the labels
        for (SettlerLbl settlerLbl : settlerLbls) {
            settlerLbl.draw(g2d, localScaleFactor);
        }

        //draw all the text boxes
        for (SettlerTxtBx txtBx : settlerTxtBxs) {
            txtBx.draw(g2d, this);
        }
    }

    /**
     * Update the positions of the SD Components
     */
    private void settlerVarPos(Graphics2D g2d) {
        //Label Loop
        mainDesc.setFont(new Font(COMPASS_GOLD.getName(), Font.PLAIN, localScaleInt(40)));
        mainDesc.setSpaceForText(localScaleInt(1500));
        mainDesc.calcNumLinesCarriageReturn();
        connectionIPLbl.setFont(new Font(COMPASS_GOLD.getName(), Font.PLAIN, localScaleInt(50)));
        connectionPortLbl.setFont(new Font(COMPASS_GOLD.getName(), Font.PLAIN, localScaleInt(50)));

        //set positions
        mainDesc.setXPos(localScaleInt(50)); //line up with the title
        mainDesc.setYPos(localScaleInt(170));

        connectionIPLbl.setXPos(mainDesc.getXPos());
        connectionIPLbl.setYPos(mainDesc.getYPos() + ((mainDesc.getNumLines() + 1) * localScaleInt(mainDesc.getLinewrapSpace())));

        connectionIPTxtBx.setXPos(connectionIPLbl.getXPos());
        connectionIPTxtBx.setYPos(connectionIPLbl.getYPos() + localScaleInt(30));

        connectionPortLbl.setXPos(localScaleInt(1200));
        connectionPortLbl.setYPos(connectionIPLbl.getYPos());

        exitBtn.setXPos(this.getWidth() / 2 - sDMenuFrame.getImgWidthLocal(exitBtn.getBaseImage(), this) / 2);
        //Line this up with the exit button from the SDMainMenuPanel.java
        exitBtn.setYPos(localScaleInt(250) + ((localScaleInt(SDMenuFrame.MENU_PACKING_HEIGHT) + sDMenuFrame.getImgHeightLocal(exitBtn.getBaseImage(), this)) * 6));

    }

    /**
     * Handles click releases from mouse input.
     *
     * @param evt
     */
    public void mouseClick(MouseEvent evt) {

        //deselect all the text boxes and then later enable one if there was a click on it
        connectionIPTxtBx.setSelected(false);

        //check if the player clicked on one of the SettlerBtns
        //loop through all the custom buttons
        for (SettlerBtn btn : settlerBtns) {
            if (evt.getX() > btn.getXPos()
                    && evt.getY() > btn.getYPos()
                    && evt.getX() < (btn.getXPos() + sDMenuFrame.getImgWidthLocal(btn.getBaseImage(), this))
                    && evt.getY() < (btn.getYPos() + sDMenuFrame.getImgHeightLocal(btn.getBaseImage(), this))
                    && btn.isEnabled()) { //and that it is enabled

                //check the button that was pressed
                if (btn.equals(exitBtn)) { //if it was the exit game button
                    exitBtnActionPerformed();
                }
            }
        }

        //check if the player clicked on one of the SettlerRadionBtns
        for (SettlerRadioBtn[] rbtnGroup : settlerRadioBtnGroups) {
            for (SettlerRadioBtn radioBtn : rbtnGroup) {
                if (evt.getX() > radioBtn.getXPos()
                        && evt.getY() > radioBtn.getYPos()
                        && evt.getX() < (radioBtn.getXPos() + getLocalImgWidth(radioBtn.getBaseImage()))
                        && evt.getY() < (radioBtn.getYPos() + getLocalImgHeight(radioBtn.getBaseImage()))
                        && radioBtn.isEnabled()) { //and that it is enabled

                    radioBtn.setSelected(true);
                }
            }
        }

        //check if the player clicked on one of the SettlerTxtBxs
        for (SettlerTxtBx bx : settlerTxtBxs) {
            if (evt.getX() > bx.getXPos()
                    && evt.getY() > bx.getYPos()
                    && evt.getX() < (bx.getXPos() + sDMenuFrame.getImgWidthLocal(bx.getBaseImage(), this))
                    && evt.getY() < (bx.getYPos() + sDMenuFrame.getImgHeightLocal(bx.getBaseImage(), this))
                    && bx.isEnabled()) { //and that it is enabled

                //check the button that was pressed
                if (bx.equals(connectionIPTxtBx)) { //if it was the exit game button
                    connectionIPTxtBxActionPerformed();
                }
            }
        }

        //repaint();
    }

    /**
     * Update the positions of the Mouse Pointer
     *
     * @param e
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        //System.out.println("Moved: " + e.getX() + ", an " + e.getY());
        mouseMotionPosX = e.getX();
        mouseMotionPosY = e.getY();
        mouseMoveAction();
    }

    /**
     * Handles mouse pointer movement.
     */
    private void mouseMoveAction() {

        //check if the player moved the mouse over one of the SettlerBtns
        //loop through all the custom buttons
        for (SettlerBtn btn : settlerBtns) {
            if (mouseMotionPosX > btn.getXPos()
                    && mouseMotionPosY > btn.getYPos()
                    && mouseMotionPosX < (btn.getXPos() + sDMenuFrame.getImgWidthLocal(btn.getBaseImage(), this))
                    && mouseMotionPosY < (btn.getYPos() + sDMenuFrame.getImgHeightLocal(btn.getBaseImage(), this))
                    && btn.isEnabled()) { //and that it is enabled

                //set the hover
                btn.setmouseHover(true);

            } else {

                //make suer there is no hover over that button
                btn.setmouseHover(false);
            }

        }

        //check if the player moved the mouse over one of the SettlerRadionBtns
        for (SettlerRadioBtn[] rbtnGroup : settlerRadioBtnGroups) {
            for (SettlerRadioBtn radioBtn : rbtnGroup) {
                if (mouseMotionPosX > radioBtn.getXPos()
                        && mouseMotionPosY > radioBtn.getYPos()
                        && mouseMotionPosX < (radioBtn.getXPos() + getLocalImgWidth(radioBtn.getBaseImage()))
                        && mouseMotionPosY < (radioBtn.getYPos() + getLocalImgHeight(radioBtn.getBaseImage()))
                        && radioBtn.isEnabled()) { //and that it is enabled

                    //set the hover
                    radioBtn.setmouseHover(true);

                } else {

                    //make suer there is no hover over that button
                    radioBtn.setmouseHover(false);
                }

            }
        }

        //check if the player moved the mouse over one of the SettlerTxtBxs
        for (SettlerTxtBx bx : settlerTxtBxs) {
            if (mouseMotionPosX > bx.getXPos()
                    && mouseMotionPosY > bx.getYPos()
                    && mouseMotionPosX < (bx.getXPos() + sDMenuFrame.getImgWidthLocal(bx.getBaseImage(), this))
                    && mouseMotionPosY < (bx.getYPos() + sDMenuFrame.getImgHeightLocal(bx.getBaseImage(), this))
                    && bx.isEnabled()) { //and that it is enabled

                //set the type of cursor to display for the mouse pointer
                this.setCursor(new Cursor(Cursor.TEXT_CURSOR));

            } else {
                //check if there is a non default cursor
                if (this.getCursor().getType() != Cursor.DEFAULT_CURSOR) {
                    this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
            }

        }

        repaint();
    }

    /**
     * Scale a number to match the resolution of the screen
     *
     * @param num
     * @return
     */
    @Override
    public int localScaleInt(int num) {
        return (int) (num / localScaleFactor);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        //System.out.println("Mouse Dragged");
    }

    private void exitBtnActionPerformed() {
        exitBtn.setmouseHover(false);
        sDMenuFrame.switchPanel(this, sDMenuFrame.getSDMainMenuPanel());
    }

    /**
     * Action to perform when the user clicks on the text box
     */
    private void connectionIPTxtBxActionPerformed() {
        //set the text box to selected so that it can accept keyboard input
        connectionIPTxtBx.setSelected(true);
    }

    /**
     * Read the instructions from in the data file
     */
    public final void loadMaterial() {

        // Declare variablesD
        Scanner fileReader;
        InputStream file = OldCode.class.getResourceAsStream("joinOnlineGame.txt");
        String fileContents = "";

        // Try to read the file
        try {
            // Create the scanner to read the file
            fileReader = new Scanner(file);

            // Read the entire file into a string
            while (fileReader.hasNextLine()) {
                // Read the line of the file into a line of the string
                fileContents += fileReader.nextLine() + "\n";
            }
        } catch (Exception e) {
            // Set the sring to be displayed to an error message
            fileContents = "Error: credits file could not be read.";
            // Output the jsvs error to the standard output
            System.out.println("Error reading credits file: " + e);
        }

        // Display the file's contents from the string
        mainDesc.setText(fileContents);
    }

    @Override
    public int getLocalImgWidth(Image image) {
        return sDMenuFrame.getImgWidthLocal(image, this);
    }

    @Override
    public int getLocalImgHeight(Image image) {
        return sDMenuFrame.getImgHeightLocal(image, this);
    }
}
