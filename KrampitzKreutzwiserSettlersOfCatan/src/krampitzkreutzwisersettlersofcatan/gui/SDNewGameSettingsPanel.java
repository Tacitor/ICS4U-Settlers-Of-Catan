/*
 * Lukas Krampitz
 * August 26, 2023
 * The JPanel for the New Game Settings now using Settler Dev Buttons.
 */
package krampitzkreutzwisersettlersofcatan.gui;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import krampitzkreutzwisersettlersofcatan.worldObjects.buttons.SettlerBtn;
import krampitzkreutzwisersettlersofcatan.worldObjects.buttons.SettlerLbl;
import krampitzkreutzwisersettlersofcatan.worldObjects.buttons.SettlerRadioBtn;
import textures.ImageRef;

/**
 *
 * @author Tacitor
 */
public class SDNewGameSettingsPanel extends javax.swing.JPanel implements MouseMotionListener, SDScaleImageResizeable {

    private SDMenuFrame sDMenuFrame;
    private static double localScaleFactor; //The factor to scale this panel by when drawing elemets
    private int mouseMotionPosX; //acording to the MouseMotionListener where is the mouse located
    private int mouseMotionPosY;

    //Settler Compoments
    private SettlerBtn exitBtn;
    //The array for the buttons
    private SettlerBtn[] settlerBtns;
    //Settler Labels
    private SettlerLbl playerNumLbl, startResMainLbl, startResSubLbl, snakeRulesMainLbl, snakeRulesSubLbl, multiPlayerLbl, limitGmPcMainLbl, limitGmPcSubLbl, houseRuleLbl;
    //The array for the buttons
    private SettlerLbl[] settlerLbls;
    //Settler Radio Buttons
    private SettlerRadioBtn playerNum2RBtn, playerNum3RBtn, playerNum4RBtn, startResYesRBtn, startResNoRBtn, snakeRulesYesRBtn, snakeRulesNoRBtn, multiPlayerLocRBtn, multiPlayerOnlineRBtn, limitGmPc15_5_4RBtn, limitGmPcInfRBtn;
    //arry for each group of radio buttons
    private SettlerRadioBtn[] settlerRadioPlayerNumBtns, settlerRadioStartResBtns, settlerRadioSnakeRulesBtns, settlerRadioMultiPlayerBtns, settlerRadioLimitGmPcBtns;
    //main array for all the radio buttons groups
    private SettlerRadioBtn[][] settlerRadioBtnGroups;

    //Fonts
    public Font COMPASS_GOLD;

    /**
     * Main Constructor
     *
     * @param sDFrame
     */
    public SDNewGameSettingsPanel(SDMenuFrame sDFrame) {
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
        playerNumLbl = new SettlerLbl("Number of players:");
        playerNumLbl.setForeground(DomesticTradePanel.BEIGE_COLOUR);
        startResMainLbl = new SettlerLbl("Starting Resources:");
        startResMainLbl.setForeground(DomesticTradePanel.BEIGE_COLOUR);
        startResSubLbl = new SettlerLbl("Give each player one harvest from each conected hex for the last settlement placed. Only once on startup.");
        startResSubLbl.setForeground(DomesticTradePanel.BEIGE_COLOUR);
        startResSubLbl.setLineWrap(true);
        startResSubLbl.setLinewrapSpace(28);
        snakeRulesMainLbl = new SettlerLbl("Snake Rules:");
        snakeRulesMainLbl.setForeground(DomesticTradePanel.BEIGE_COLOUR);
        snakeRulesSubLbl = new SettlerLbl("Set up phase follows \'snake\' rules, (i.e. first pick also gets last pick) to make it more fair. For example, in a three player game the setup turns would go 1, 2, 3, 3, 2, 1.");
        snakeRulesSubLbl.setForeground(DomesticTradePanel.BEIGE_COLOUR);
        snakeRulesSubLbl.setLineWrap(true);
        snakeRulesSubLbl.setLinewrapSpace(28);
        multiPlayerLbl = new SettlerLbl("Multiplayer Mode:");
        multiPlayerLbl.setForeground(DomesticTradePanel.BEIGE_COLOUR);
        limitGmPcMainLbl = new SettlerLbl("Limit Game Pieces:");
        limitGmPcMainLbl.setForeground(DomesticTradePanel.BEIGE_COLOUR);
        limitGmPcSubLbl = new SettlerLbl("The number of play pieces each player will have for their colour. Can be set to infite.");
        limitGmPcSubLbl.setForeground(DomesticTradePanel.BEIGE_COLOUR);
        limitGmPcSubLbl.setLineWrap(true);
        limitGmPcSubLbl.setLinewrapSpace(28);
        houseRuleLbl = new SettlerLbl("Have a house rule you want added as an option? Add and issue to the GitHub or find any other way you want to contact me.");
        houseRuleLbl.setForeground(DomesticTradePanel.BEIGE_COLOUR);
        houseRuleLbl.setLineWrap(true);
        houseRuleLbl.setLinewrapSpace(28);
        //add them to the array
        settlerLbls = new SettlerLbl[]{playerNumLbl, startResMainLbl, startResSubLbl, snakeRulesMainLbl, snakeRulesSubLbl, multiPlayerLbl, limitGmPcMainLbl, limitGmPcSubLbl, houseRuleLbl};

        //setup the radio buttons
        playerNum2RBtn = new SettlerRadioBtn(true, true, 3);
        playerNum3RBtn = new SettlerRadioBtn(true, false, 4);
        playerNum4RBtn = new SettlerRadioBtn(true, false, 5);
        startResYesRBtn = new SettlerRadioBtn(true, true, 6);
        startResNoRBtn = new SettlerRadioBtn(true, false, 7);
        snakeRulesYesRBtn = new SettlerRadioBtn(true, true, 6);
        snakeRulesNoRBtn = new SettlerRadioBtn(true, false, 7);
        multiPlayerLocRBtn = new SettlerRadioBtn(true, true, 8);
        multiPlayerOnlineRBtn = new SettlerRadioBtn(true, false, 9);
        limitGmPc15_5_4RBtn = new SettlerRadioBtn(true, true, 10);
        limitGmPcInfRBtn = new SettlerRadioBtn(true, false, 11);

        //add them to the group array
        settlerRadioPlayerNumBtns = new SettlerRadioBtn[]{playerNum2RBtn, playerNum3RBtn, playerNum4RBtn};
        settlerRadioStartResBtns = new SettlerRadioBtn[]{startResYesRBtn, startResNoRBtn};
        settlerRadioSnakeRulesBtns = new SettlerRadioBtn[]{snakeRulesYesRBtn, snakeRulesNoRBtn};
        settlerRadioMultiPlayerBtns = new SettlerRadioBtn[]{multiPlayerLocRBtn, multiPlayerOnlineRBtn};
        settlerRadioLimitGmPcBtns = new SettlerRadioBtn[]{limitGmPc15_5_4RBtn, limitGmPcInfRBtn};

        //add the group to the main array
        settlerRadioBtnGroups = new SettlerRadioBtn[5][];
        settlerRadioBtnGroups[0] = settlerRadioPlayerNumBtns;
        settlerRadioBtnGroups[1] = settlerRadioStartResBtns;
        settlerRadioBtnGroups[2] = settlerRadioSnakeRulesBtns;
        settlerRadioBtnGroups[3] = settlerRadioMultiPlayerBtns;
        settlerRadioBtnGroups[4] = settlerRadioLimitGmPcBtns;

        //setup the custom radio buttons to go into the groups
        for (SettlerRadioBtn[] grp : settlerRadioBtnGroups) {
            SettlerRadioBtn.setUpGroup(grp);
        }

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
        g2d.drawString("New Game Settings",
                (this.getWidth() / 2) - (g2d.getFontMetrics().stringWidth("New Game Settings") / 2),
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
    }

    /**
     * Update the positions of the SD Components
     */
    private void settlerVarPos(Graphics2D g2d) {
        //Label Loop
        playerNumLbl.setFont(new Font(COMPASS_GOLD.getName(), Font.PLAIN, localScaleInt(50)));
        startResMainLbl.setFont(new Font(COMPASS_GOLD.getName(), Font.PLAIN, localScaleInt(50)));
        startResSubLbl.setFont(new Font(COMPASS_GOLD.getName(), Font.PLAIN, localScaleInt(40)));
        snakeRulesMainLbl.setFont(new Font(COMPASS_GOLD.getName(), Font.PLAIN, localScaleInt(50)));
        snakeRulesSubLbl.setFont(new Font(COMPASS_GOLD.getName(), Font.PLAIN, localScaleInt(40)));
        multiPlayerLbl.setFont(new Font(COMPASS_GOLD.getName(), Font.PLAIN, localScaleInt(50)));
        limitGmPcMainLbl.setFont(new Font(COMPASS_GOLD.getName(), Font.PLAIN, localScaleInt(50)));
        limitGmPcSubLbl.setFont(new Font(COMPASS_GOLD.getName(), Font.PLAIN, localScaleInt(40)));
        houseRuleLbl.setFont(new Font(COMPASS_GOLD.getName(), Font.PLAIN, localScaleInt(40)));

        //calc the number of lines for the labels that will be multi line
        playerNumLbl.setXPos(localScaleInt(300));
        playerNumLbl.setYPos(localScaleInt(200));

        playerNum2RBtn.setXPos(playerNumLbl.getXPos());
        playerNum2RBtn.setYPos(playerNumLbl.getYPos() + localScaleInt(15));

        playerNum3RBtn.setXPos(playerNum2RBtn.getXPos() + getLocalImgWidth(playerNum2RBtn.getBaseImage()) + localScaleInt(6));
        playerNum3RBtn.setYPos(playerNum2RBtn.getYPos());

        playerNum4RBtn.setXPos(playerNum3RBtn.getXPos() + getLocalImgWidth(playerNum3RBtn.getBaseImage()) + localScaleInt(6));
        playerNum4RBtn.setYPos(playerNum2RBtn.getYPos());

        startResMainLbl.setXPos(playerNum2RBtn.getXPos());
        startResMainLbl.setYPos(playerNum2RBtn.getYPos() + getLocalImgHeight(playerNum2RBtn.getBaseImage()) + localScaleInt(60));

        startResSubLbl.setXPos(startResMainLbl.getXPos());
        startResSubLbl.setYPos(startResMainLbl.getYPos() + localScaleInt(30));
        startResSubLbl.setSpaceForText(localScaleInt(550));
        startResSubLbl.calcNumLines(g2d);

        startResYesRBtn.setXPos(startResMainLbl.getXPos());
        startResYesRBtn.setYPos(startResSubLbl.getYPos() - localScaleInt(8) + (startResSubLbl.getNumLines() * localScaleInt(startResSubLbl.getLinewrapSpace())));

        startResNoRBtn.setXPos(startResYesRBtn.getXPos() + getLocalImgWidth(startResYesRBtn.getBaseImage()) + localScaleInt(6));
        startResNoRBtn.setYPos(startResYesRBtn.getYPos());

        snakeRulesMainLbl.setXPos(startResYesRBtn.getXPos());
        snakeRulesMainLbl.setYPos(startResYesRBtn.getYPos() + getLocalImgHeight(startResYesRBtn.getBaseImage()) + localScaleInt(60));

        snakeRulesSubLbl.setXPos(snakeRulesMainLbl.getXPos());
        snakeRulesSubLbl.setYPos(snakeRulesMainLbl.getYPos() + localScaleInt(30));
        snakeRulesSubLbl.setSpaceForText(localScaleInt(550));
        snakeRulesSubLbl.calcNumLines(g2d);

        snakeRulesYesRBtn.setXPos(snakeRulesSubLbl.getXPos());
        snakeRulesYesRBtn.setYPos(snakeRulesSubLbl.getYPos() - localScaleInt(8) + (snakeRulesSubLbl.getNumLines() * localScaleInt(snakeRulesSubLbl.getLinewrapSpace())));

        snakeRulesNoRBtn.setXPos(snakeRulesYesRBtn.getXPos() + getLocalImgWidth(snakeRulesYesRBtn.getBaseImage()) + localScaleInt(6));
        snakeRulesNoRBtn.setYPos(snakeRulesYesRBtn.getYPos());

        multiPlayerLbl.setXPos(localScaleInt(900));
        multiPlayerLbl.setYPos(playerNumLbl.getYPos());

        multiPlayerLocRBtn.setXPos(multiPlayerLbl.getXPos());
        multiPlayerLocRBtn.setYPos(multiPlayerLbl.getYPos() + localScaleInt(15));

        multiPlayerOnlineRBtn.setXPos(multiPlayerLocRBtn.getXPos());
        multiPlayerOnlineRBtn.setYPos(multiPlayerLocRBtn.getYPos() + getLocalImgHeight(multiPlayerLocRBtn.getBaseImage()) + localScaleInt(6));

        limitGmPcMainLbl.setXPos(multiPlayerLbl.getXPos());
        limitGmPcMainLbl.setYPos(multiPlayerLbl.getYPos() + localScaleInt(100));

        limitGmPcSubLbl.setXPos(limitGmPcMainLbl.getXPos());
        limitGmPcSubLbl.setYPos(limitGmPcMainLbl.getYPos() + localScaleInt(30));
        limitGmPcSubLbl.setSpaceForText(localScaleInt(550));
        limitGmPcSubLbl.calcNumLines(g2d);

        limitGmPc15_5_4RBtn.setXPos(limitGmPcMainLbl.getXPos());
        limitGmPc15_5_4RBtn.setYPos(limitGmPcSubLbl.getYPos() - localScaleInt(8) + (limitGmPcSubLbl.getNumLines() * localScaleInt(limitGmPcSubLbl.getLinewrapSpace())));

        limitGmPcInfRBtn.setXPos(limitGmPc15_5_4RBtn.getXPos());
        limitGmPcInfRBtn.setYPos(limitGmPc15_5_4RBtn.getYPos() + getLocalImgHeight(limitGmPc15_5_4RBtn.getBaseImage()) + localScaleInt(6));

        houseRuleLbl.setXPos(limitGmPcMainLbl.getXPos());
        houseRuleLbl.setYPos(snakeRulesSubLbl.getYPos());
        houseRuleLbl.setSpaceForText(localScaleInt(550));
        houseRuleLbl.calcNumLines(g2d);

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

        repaint();
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

    @Override
    public void mouseDragged(MouseEvent e) {
        //System.out.println("Mouse Dragged");
    }

    private void exitBtnActionPerformed() {
        exitBtn.setmouseHover(false);
        sDMenuFrame.switchPanel(this, sDMenuFrame.getSDMainMenuPanel());
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
