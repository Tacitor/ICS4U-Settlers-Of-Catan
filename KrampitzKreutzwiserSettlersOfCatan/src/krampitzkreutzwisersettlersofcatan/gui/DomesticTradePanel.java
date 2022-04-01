/*
 * Lukas Krampitz
 * Mar 25, 2022
 * A JFrame that the user will interact with to trade domestically with other users/players
 */
package krampitzkreutzwisersettlersofcatan.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import javax.swing.JPanel;
import krampitzkreutzwisersettlersofcatan.worldObjects.buttons.SettlerBtn;
import textures.ImageRef;
import static krampitzkreutzwisersettlersofcatan.gui.GamePanel.scaleInt;
import krampitzkreutzwisersettlersofcatan.worldObjects.buttons.SettlerLbl;

/**
 *
 * @author Tacitor
 */
public class DomesticTradePanel extends JPanel implements MouseMotionListener {

    private GameFrame gameFrame;
    private GamePanel theGamePanel;

    private int mouseMotionPosX;
    private int mouseMotionPosY;

    //Settler Components
    private SettlerBtn cancelTradeBtn;
    //The array for the buttons
    private SettlerBtn[] settlerBtns;
    //Settler Lable
    private SettlerLbl titleLbl, playerSelectLbl, initiatePlayerReceivesLbl; //receives
    //array for the labels
    private SettlerLbl[] settlerLbls;

    //data for the trading
    private int playerStartedDomestic; //the player ID of the user the clicked the domestic trade button
    private int playerCount; //the number of players in the game
    private int[] nonInitiatePlayers; //the list of the remaining player(s) that did not start the trade

    /**
     * Domestic trade Constructor
     *
     * @param frame
     */
    public DomesticTradePanel(GameFrame frame) {

        gameFrame = frame;
        theGamePanel = gameFrame.getGamePanel();

        initSettlerLbl();

        //add in the motion listener for hovering
        addMouseMotionListener(this);

        //add a mouse listener that call the mouse click even handler
        addMouseListener(new MouseAdapter() {
            /**
             * Triggered when the user clicks on the trade panel. Calls the
             * trade panel's click event method.
             *
             * @param event
             */
            @Override
            public final void mouseReleased(MouseEvent event) {
                //send the mouse event to the trade panel click handler
                tradeMouseClicked(event);
            }
        });

        //setup the buttons
        cancelTradeBtn = new SettlerBtn(true, 1, 9);
        //add then to the array
        settlerBtns = new SettlerBtn[]{cancelTradeBtn};

        //setup the labels
        settlerLbls = new SettlerLbl[]{titleLbl, playerSelectLbl, initiatePlayerReceivesLbl};

    }

    /**
     * Setup the custom Labels
     */
    private void initSettlerLbl() {
        //setup the label and text
        titleLbl = new SettlerLbl("Domestic Trade");
        playerSelectLbl = new SettlerLbl("Select a player to trade with:");
        initiatePlayerReceivesLbl = new SettlerLbl("ERROR: this is temporary text");

        //setup the colour
        Color beigeColor = new Color(255, 255, 225);
        titleLbl.setForeground(beigeColor); //beige
        playerSelectLbl.setForeground(beigeColor);
        initiatePlayerReceivesLbl.setForeground(beigeColor);
    }

    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        draw(g);

    }

    /**
     * Draw the trade UI
     *
     * @param g
     */
    private void draw(Graphics g) {
        //update the gamePanel ref
        theGamePanel = gameFrame.getGamePanel();

        panelSizeDependantCalculations();

        Graphics2D g2d = (Graphics2D) g;

        //updated the number of players
        playerCount = GamePanel.getPlayerCount();

        //update the Settler Label positions
        settlerVarPos(g2d); //update the positions

        g2d.drawImage(ImageRef.WOOD_BACKGROUND,
                0,
                0,
                gameFrame.getWidth(),
                gameFrame.getHeight(), this);

        //=-=-=-=draw the player icons=-=-=-=
        //calc which ones to draw
        calcPlayerIconsToDraw();

        Image playerIconImage = theGamePanel.getPlayerImage(1, true); //the image that will be drawn for the player

        //calc the start postion
        int startPos;
        //place the start so that the icons are in the middle if the screen horizontally
        startPos = (gameFrame.getWidth() / 2) - ((theGamePanel.getImgWidth(playerIconImage) * (playerCount - 1)) / 2);

        //do the drawing
        for (int i = 0; i < nonInitiatePlayers.length; i++) { //loo though the players

            playerIconImage = theGamePanel.getPlayerImage(nonInitiatePlayers[i], true);

            g2d.drawImage(playerIconImage,
                    startPos + ((theGamePanel.getImgWidth(playerIconImage)) * (i)), //put it in the corner with some padding space
                    playerSelectLbl.getYPos() + scaleInt(30), //put it in the corner with some padding space
                    theGamePanel.getImgWidth(playerIconImage), //scale the image
                    theGamePanel.getImgHeight(playerIconImage),
                    null);

        }

        //=-=-=-=-=-=-=-=-=-= Draw the Settlerbuttons =-=-=-=-=-=-=-=-=-=
        for (SettlerBtn btn : settlerBtns) {
            btn.updateButtonImages();
            btn.updateText();

            //draw the base        
            theGamePanel.drawSettlerBtn(g2d, btn.getBaseImage(), btn, 0);

            //draw the text
            theGamePanel.drawSettlerBtn(g2d, btn.getTextImage(), btn, 0);

            //draw the disabled overlay if required
            if (!btn.isEnabled()) {
                theGamePanel.drawSettlerBtn(g2d, btn.getDisabledImage(), btn, 0);
            }
            //draw the mouseHover overlay if required
            if (btn.isMouseHover()) {
                theGamePanel.drawSettlerBtn(g2d, btn.getHoverImage(), btn, 1);
            }
        }

        //=-=-=-=-=-=-=-=-=-= End the drawing of Settlerbuttons =-=-=-=-=-=-=-=-=-=
        //go through and draw all the labels
        for (SettlerLbl settlerLbl : settlerLbls) {
            settlerLbl.draw(g2d);
        }

        //=-=-=-=draw on the player dots for the labels=-=-=-=
        //the the image
        Image playerDot = ImageRef.PLAYER_DOTS[playerStartedDomestic];
        //set the font
        g2d.setFont(initiatePlayerReceivesLbl.getFont());
        //draw the receive player dot
        g2d.drawImage(playerDot,
                initiatePlayerReceivesLbl.getXPos() + (g2d.getFontMetrics().stringWidth(initiatePlayerReceivesLbl.getText().substring(0, 10))) + scaleInt(4),
                initiatePlayerReceivesLbl.getYPos() - theGamePanel.getImgHeight(playerDot) + scaleInt(8),
                theGamePanel.getImgWidth(playerDot),
                theGamePanel.getImgHeight(playerDot), this);

        // Add alignment lines
        g2d.drawLine(this.getWidth() / 2, 0, this.getWidth() / 2, this.getHeight());
        g2d.drawLine(0, this.getHeight() / 2, this.getWidth(), this.getHeight() / 2);
    }

    private void settlerVarPos(Graphics2D g2d) {
        //Align the components
        //top to bottom
        titleLbl.setXPos(scaleInt(10));
        titleLbl.setYPos(scaleInt(35));

        //calce the positon for the labels
        g2d.setFont(playerSelectLbl.getFont()); //make sure it has the right font size
        //player select
        int stringWidth = g2d.getFontMetrics().stringWidth(playerSelectLbl.getText()); //calc how much room it will take up
        playerSelectLbl.setXPos((gameFrame.getWidth() / 2) - (stringWidth / 2));
        playerSelectLbl.setYPos(50);
        //player initate receive
        initiatePlayerReceivesLbl.setText("Player " + playerStartedDomestic + " (      ) receives:"); //make sure text is up to date
        stringWidth = g2d.getFontMetrics().stringWidth(initiatePlayerReceivesLbl.getText()); //calc how much room it will take up
        initiatePlayerReceivesLbl.setXPos((gameFrame.getWidth() / 2) - (stringWidth / 2));
        initiatePlayerReceivesLbl.setYPos(playerSelectLbl.getYPos() + theGamePanel.getImgHeight(ImageRef.SMALL_PLAYER_RED) + scaleInt(100));

        cancelTradeBtn.setXPos(titleLbl.getXPos());
        cancelTradeBtn.setYPos(gameFrame.getHeight() - theGamePanel.getImgHeight(cancelTradeBtn.getBaseImage()) - scaleInt(6));
    }

    private void cancelTradeBtnPressed() {
        gameFrame.switchToTrade(false);
    }

    /**
     * Actions to take when the user clicks on the trade panel
     *
     * @param evt
     */
    public void tradeMouseClicked(MouseEvent evt) {
        //Loop through all the Buttons
        for (SettlerBtn btn : settlerBtns) {
            if (evt.getX() > btn.getXPos()
                    && evt.getY() > btn.getYPos()
                    && evt.getX() < (btn.getXPos() + theGamePanel.getImgWidth(btn.getBaseImage()))
                    && evt.getY() < (btn.getYPos() + theGamePanel.getImgHeight(btn.getBaseImage()))
                    && btn.isEnabled()) { //and that it is enabled

                //Check what button was pressed
                if (btn.equals(cancelTradeBtn)) {
                    //if it's the cancel button
                    //close the trade menu
                    cancelTradeBtnPressed();
                }
            }
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseMotionPosX = e.getX();
        mouseMotionPosY = e.getY();
        mouseMoveAction();
    }

    private void mouseMoveAction() {
        //check if the player moved the mouse over one of the SettlerBtns
        //loop through all the custom buttons
        for (SettlerBtn btn : settlerBtns) {
            if (mouseMotionPosX > btn.getXPos()
                    && mouseMotionPosY > btn.getYPos()
                    && mouseMotionPosX < (btn.getXPos() + theGamePanel.getImgWidth(btn.getBaseImage()))
                    && mouseMotionPosY < (btn.getYPos() + theGamePanel.getImgHeight(btn.getBaseImage()))
                    && btn.isEnabled()) { //and that it is enabled

                //set the hover
                btn.setmouseHover(true);

            } else {

                //make suer there is no hover over that button
                btn.setmouseHover(false);
            }

        }

        repaint();
    }

    /**
     * Make calculations and update variables that have to do with scaling and
     * depend on knowing the width or height of the gamePanel
     */
    private void panelSizeDependantCalculations() {
        Font headerFont = new Font(GamePanel.TIMES_NEW_ROMAN.getName(), GamePanel.TIMES_NEW_ROMAN.getStyle(), scaleInt(25));

        //Settler Label Font Size
        titleLbl.setFont(new Font(GamePanel.TIMES_NEW_ROMAN.getName(), Font.BOLD, scaleInt(40)));
        playerSelectLbl.setFont(headerFont);
        initiatePlayerReceivesLbl.setFont(headerFont);
    }

    /**
     * Accessors and Mutators
     */
    /**
     * Get the ID of the player that initiated the domestic trade
     *
     * @return
     */
    public int getPlayerStartedDomestic() {
        return playerStartedDomestic;
    }

    /**
     * Set the ID of the player that initiated the domestic trade
     *
     * @param playerStartedDomestic
     */
    public void setPlayerStartedDomestic(int playerStartedDomestic) {
        this.playerStartedDomestic = playerStartedDomestic;
    }

    /**
     * Go through and decide which icons to draw. Will draw all except the
     * player that started the trade.
     *
     */
    private void calcPlayerIconsToDraw() {

        //init array 
        nonInitiatePlayers = new int[playerCount - 1];

        //loop through the players
        for (int i = 0; i < playerCount; i++) {
            //if it's  not one of the starting players draw it
            if (i + 1 != playerStartedDomestic) {
                if (i + 1 < playerStartedDomestic) {

                    nonInitiatePlayers[i] = i + 1;
                } else {
                    nonInitiatePlayers[i - 1] = i + 1;
                }
            }
        }

    }
}
