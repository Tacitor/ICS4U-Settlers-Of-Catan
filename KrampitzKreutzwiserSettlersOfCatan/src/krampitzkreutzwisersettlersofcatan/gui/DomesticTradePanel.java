/*
 * Lukas Krampitz
 * Mar 25, 2022
 * A JFrame that the user will interact with to trade domestically with other users/players
 */
package krampitzkreutzwisersettlersofcatan.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import static krampitzkreutzwisersettlersofcatan.gui.GamePanel.scaleInt;
import krampitzkreutzwisersettlersofcatan.worldObjects.buttons.SettlerBtn;
import textures.ImageRef;
import krampitzkreutzwisersettlersofcatan.util.CardUtil;
import krampitzkreutzwisersettlersofcatan.worldObjects.buttons.SettlerLbl;
import static textures.ImageRef.*;

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
    private SettlerBtn cancelTradeBtn, lockInitiatePlayerReceiveTradeBtn, lockInitiatePlayerGiveTradeBtn, completeTradeBtn;
    //The array for the buttons
    private SettlerBtn[] settlerBtns;
    //Settler Lable
    private SettlerLbl titleLbl, playerSelectLbl, initiatePlayerReceivesLbl, initiatePlayerGivesLbl, initiatePlayersStaticCardsLbl;
    //array for the labels
    private SettlerLbl[] settlerLbls;

    //data for card drawing
    /**
     * Will be length 3, one for each of the cards hands it will draw. Index 0
     * is the cards playerStartedDomestic has right now. Index 1 is the cards
     * they will receive from the trade. Index 2 are the cards
     * playerStartedDomestic will be trading away.
     */
    boolean[] drawCardStacks;

    //data for the trading
    //data that can be generated
    private int playerCount; //the number of players in the game    
    private int[] nonInitiatePlayers; //the list of the remaining player(s) that did not start the trade
    //player generated and needs to be transfered in online play
    private int playerStartedDomestic; //the player ID of the user the clicked the domestic trade button
    private int playerSelectedForTrade; //the player ID of the player that the playerStartedDomestic selected to trade with
    private ArrayList<Integer> tradeCardsReceivePlayerStartedDomestic; //The cards that the player who started the trade will receive 
    private ArrayList<Integer> tradeCardsGivePlayerStartedDomestic; //The cards that the player who started the trade will give away to the other player 
    private ArrayList<Integer> tradeCardsAlreadyHadPlayerStartedDomestic; //A copy of the cards that the player who started the trade has going in 
    private ArrayList<Integer> tradeCardsAlreadyHadPlayerSelected; //A copy of the cards that the player who was selected for the trade has going in

    private int playerIconStartPos;

    private int domesticTradeMode; //the mode of trade the game is in. Starts at 0. Each stage is incremented by 1.

    /**
     * Domestic trade Constructor
     *
     * @param frame
     */
    public DomesticTradePanel(GameFrame frame) {

        gameFrame = frame;
        theGamePanel = gameFrame.getGamePanel();

        initSettlerLbl();

        drawCardStacks = new boolean[3];

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
        cancelTradeBtn = new SettlerBtn(true, 0, 13);
        lockInitiatePlayerReceiveTradeBtn = new SettlerBtn(true, 0, 11);
        lockInitiatePlayerGiveTradeBtn = new SettlerBtn(true, 0, 11);
        completeTradeBtn = new SettlerBtn(true, 0, 12);
        //add then to the array
        settlerBtns = new SettlerBtn[]{cancelTradeBtn, lockInitiatePlayerReceiveTradeBtn, lockInitiatePlayerGiveTradeBtn, completeTradeBtn};

        //setup the labels
        settlerLbls = new SettlerLbl[]{titleLbl, playerSelectLbl, initiatePlayerReceivesLbl, initiatePlayerGivesLbl, initiatePlayersStaticCardsLbl};

        tradeCardsReceivePlayerStartedDomestic = new ArrayList<>();
        tradeCardsReceivePlayerStartedDomestic.add(1);
        tradeCardsReceivePlayerStartedDomestic.add(1);
        tradeCardsReceivePlayerStartedDomestic.add(1);
        tradeCardsReceivePlayerStartedDomestic.add(2);
        tradeCardsReceivePlayerStartedDomestic.add(2);
        tradeCardsReceivePlayerStartedDomestic.add(2);

        tradeCardsReceivePlayerStartedDomestic.add(3);
        tradeCardsReceivePlayerStartedDomestic.add(3);
        tradeCardsReceivePlayerStartedDomestic.add(3);

        tradeCardsReceivePlayerStartedDomestic.add(4);
        tradeCardsReceivePlayerStartedDomestic.add(4);
        tradeCardsReceivePlayerStartedDomestic.add(4);

        tradeCardsReceivePlayerStartedDomestic.add(5);
        tradeCardsReceivePlayerStartedDomestic.add(5);
        tradeCardsReceivePlayerStartedDomestic.add(5);

        tradeCardsGivePlayerStartedDomestic = new ArrayList<>();
        tradeCardsGivePlayerStartedDomestic.add(5);

        tradeCardsAlreadyHadPlayerStartedDomestic = new ArrayList<>();
        tradeCardsAlreadyHadPlayerSelected = new ArrayList<>();

        resetToStartingMode();

        repaint();

    }

    /**
     * Reset the variables to a default stating mode
     */
    public void resetToStartingMode() {
        lockInitiatePlayerGiveTradeBtn.setEnabled(false);
        lockInitiatePlayerReceiveTradeBtn.setEnabled(false);
        completeTradeBtn.setEnabled(false);

        //loop through all the buttons an reset them to mode 0
        for (SettlerBtn btn : settlerBtns) {
            btn.setMode(0);
        }

        playerSelectedForTrade = 0; //default it to 0 (player none)

        tradeCardsGivePlayerStartedDomestic.clear();
        tradeCardsReceivePlayerStartedDomestic.clear();

        tradeCardsAlreadyHadPlayerStartedDomestic.clear();
        tradeCardsAlreadyHadPlayerSelected.clear();

        domesticTradeMode = 0;
    }

    /**
     * Setup the custom Labels
     */
    private void initSettlerLbl() {
        //setup the label and text
        titleLbl = new SettlerLbl("Domestic Trade");
        playerSelectLbl = new SettlerLbl("Select a player to trade with:");
        initiatePlayerReceivesLbl = new SettlerLbl("ERROR: this is temporary text");
        initiatePlayerGivesLbl = new SettlerLbl("ERROR: 1");
        initiatePlayersStaticCardsLbl = new SettlerLbl("ERROR: 2");

        //setup the colour
        Color beigeColor = new Color(255, 255, 225);
        titleLbl.setForeground(beigeColor); //beige
        playerSelectLbl.setForeground(beigeColor);
        initiatePlayerReceivesLbl.setForeground(beigeColor);
        initiatePlayerGivesLbl.setForeground(beigeColor);
        initiatePlayersStaticCardsLbl.setForeground(beigeColor);
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

        //draw the background image
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
        //place the start so that the icons are in the middle if the screen horizontally
        playerIconStartPos = (gameFrame.getWidth() / 2) - ((theGamePanel.getImgWidth(playerIconImage) * (playerCount - 1)) / 2);

        //do the drawing
        for (int i = 0; i < nonInitiatePlayers.length; i++) { //loop though the players

            playerIconImage = theGamePanel.getPlayerImage(nonInitiatePlayers[i], true);

            g2d.drawImage(playerIconImage,
                    playerIconStartPos + ((theGamePanel.getImgWidth(playerIconImage)) * (i)), //put it in the corner with some padding space
                    playerSelectLbl.getYPos() + scaleInt(30), //put it in the corner with some padding space
                    theGamePanel.getImgWidth(playerIconImage), //scale the image
                    theGamePanel.getImgHeight(playerIconImage),
                    null);

            //draw the hitbox around the icons only if in mode 0
            if (domesticTradeMode == 0) {
                //draw the high light
                g2d.setColor(new java.awt.Color(255, 255, 225, 128));
                g2d.fillRect(playerIconStartPos + ((theGamePanel.getImgWidth(playerIconImage)) * (i)), //put it in the corner with some padding space
                        playerSelectLbl.getYPos() + scaleInt(30), //put it in the corner with some padding space
                        theGamePanel.getImgWidth(SMALL_PLAYER_RED),
                        theGamePanel.getImgHeight(SMALL_PLAYER_RED));
                //draw the boarder
                g2d.setColor(new java.awt.Color(255, 255, 225));
                g2d.drawRect(playerIconStartPos + ((theGamePanel.getImgWidth(playerIconImage)) * (i)), //put it in the corner with some padding space
                        playerSelectLbl.getYPos() + scaleInt(30), //put it in the corner with some padding space
                        theGamePanel.getImgWidth(SMALL_PLAYER_RED),
                        theGamePanel.getImgHeight(SMALL_PLAYER_RED));
            }

        }

        //draw the init player
        Image playerImage = theGamePanel.getPlayerImage(playerStartedDomestic, false);
        g2d.drawImage(playerImage,
                this.getWidth() - theGamePanel.getImgWidth(PLAYER_RED) - scaleInt(10), //put it in the corner with some padding space
                this.getHeight() - theGamePanel.getImgHeight(PLAYER_RED) - scaleInt(10), //put it in the corner with some padding space
                theGamePanel.getImgWidth(PLAYER_RED), //scale the image
                theGamePanel.getImgHeight(PLAYER_RED),
                null);

        //draw the header
        g2d.setFont(new Font("Times New Roman", Font.BOLD, scaleInt(20)));
        g2d.setColor(new java.awt.Color(255, 255, 225));
        g2d.drawString("Initiated Trade:",
                this.getWidth() - theGamePanel.getImgWidth(PLAYER_RED) - scaleInt(10),
                this.getHeight() - theGamePanel.getImgHeight(PLAYER_RED) - scaleInt(20));

        //draw the select player to trade with
        playerImage = theGamePanel.getPlayerImage(playerSelectedForTrade, true);
        g2d.drawImage(playerImage,
                this.getWidth() - (theGamePanel.getImgWidth(PLAYER_RED)) - ((theGamePanel.getImgWidth(SMALL_PLAYER_RED))), //put it in the corner with some padding space
                this.getHeight() - scaleInt(10) - theGamePanel.getImgHeight(SMALL_PLAYER_RED), //put it in the corner with some padding space
                theGamePanel.getImgWidth(SMALL_PLAYER_RED), //scale the image
                theGamePanel.getImgHeight(SMALL_PLAYER_RED),
                null);
        //draw the header
        g2d.setColor(new java.awt.Color(255, 255, 225));
        g2d.drawString("Other Trader:",
                this.getWidth() - (theGamePanel.getImgWidth(PLAYER_RED)) - (theGamePanel.getImgWidth(SMALL_PLAYER_RED)),
                this.getHeight() - scaleInt(20) - theGamePanel.getImgHeight(SMALL_PLAYER_RED));

        //=-=-=-=END OF draw the player icons=-=-=-=
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

        //draw the dot image for the initiatePlayersStaticCardsLbl
        int openBracketPos; //the position of the '(' char in the string
        //calc the sub string of the text
        openBracketPos = initiatePlayersStaticCardsLbl.getText().indexOf('(');

        //set the image based off of what mode the game is in. If mode 1 then the ini player must select cards. If mode 2 then the other player must slect cards
        playerDot = domesticTradeMode == 2 ? ImageRef.PLAYER_DOTS[playerSelectedForTrade] : ImageRef.PLAYER_DOTS[playerStartedDomestic];
        //draw the give player dot
        g2d.drawImage(playerDot,
                initiatePlayersStaticCardsLbl.getXPos() + (g2d.getFontMetrics().stringWidth(initiatePlayersStaticCardsLbl.getText().substring(0, openBracketPos + 1))) + scaleInt(4),
                initiatePlayersStaticCardsLbl.getYPos() - theGamePanel.getImgHeight(playerDot) + scaleInt(8),
                theGamePanel.getImgWidth(playerDot),
                theGamePanel.getImgHeight(playerDot), this);

        //draw the dot image for the initiatePlayerGivesLbl
        //set the image
        playerDot = ImageRef.PLAYER_DOTS[playerSelectedForTrade];

        //calc the sub string of the text
        openBracketPos = initiatePlayerGivesLbl.getText().indexOf('(');
        //draw the give player dot
        g2d.drawImage(playerDot,
                initiatePlayerGivesLbl.getXPos() + (g2d.getFontMetrics().stringWidth(initiatePlayerGivesLbl.getText().substring(0, openBracketPos + 1))) + scaleInt(4),
                initiatePlayerGivesLbl.getYPos() - theGamePanel.getImgHeight(playerDot) + scaleInt(8),
                theGamePanel.getImgWidth(playerDot),
                theGamePanel.getImgHeight(playerDot), this);

        //=-=-=-= END OF draw on the player dots for the labels=-=-=-=
        //=-=-=-=-=-=-=-=-=-= Draw the Settlerbuttons =-=-=-=-=-=-=-=-=-=
        for (SettlerBtn btn : settlerBtns) {
            btn.updateButtonImages();
            btn.updateText();

            //draw the base        
            theGamePanel.drawSettlerBtn(g2d, btn.getBaseImage(), btn, 0);

            //draw the text
            theGamePanel.drawSettlerBtn(g2d, btn.getTextImage(), btn, 0);

            //draw the player's colour dot if it's a lock trade button
            if (btn.getType() == 11) {
                int playerDotNum;

                if (btn.equals(lockInitiatePlayerReceiveTradeBtn)) {
                    playerDotNum = playerSelectedForTrade;
                } else {
                    playerDotNum = playerStartedDomestic;
                }

                //draw the dot 
                g2d.drawImage(PLAYER_DOTS[playerDotNum],
                        btn.getXPos() + scaleInt(92),
                        btn.getYPos() + theGamePanel.getImgHeight(btn.getTextImage()) / 2 - theGamePanel.getImgHeight(PLAYER_DOTS[playerDotNum]) / 4,
                        theGamePanel.getImgWidth(PLAYER_DOTS[playerDotNum]) / 2,
                        theGamePanel.getImgHeight(PLAYER_DOTS[playerDotNum]) / 2,
                        null);
            }

            //draw the disabled overlay if required
            if (!btn.isEnabled()) {
                theGamePanel.drawSettlerBtn(g2d, btn.getDisabledImage(), btn, 0);
            }
            //draw the mouseHover overlay if required
            if (btn.isMouseHover()) {
                theGamePanel.drawSettlerBtn(g2d, btn.getHoverImage(), btn, 1);
            }

        }

        //=-=-=-=-=-=-=-=-=-= END OF the drawing of Settlerbuttons =-=-=-=-=-=-=-=-=-=
        //draw the cards the initation player HAS
        drawCards(g2d, 0, getCardHand(0));

        //draw the cards the intiation player will receive
        drawCards(g2d, 1, tradeCardsReceivePlayerStartedDomestic);

        //draw the cards the init player will trade away
        drawCards(g2d, 2, tradeCardsGivePlayerStartedDomestic);

        //=-=-=-=-=-=-=-=-= draw the menu segment boarders =-=-=-=-=-=-=-=-=
        g2d.setStroke(new BasicStroke(scaleInt(5))); //make the stroke a little thicker
        //draw the player select box
        g2d.drawRect((gameFrame.getWidth() / 2) - ((theGamePanel.getImgWidth(playerIconImage) * (3)) / 2),
                playerSelectLbl.getYPos() - scaleInt(30),
                theGamePanel.getImgWidth(playerIconImage) * 3,
                theGamePanel.getImgHeight(playerIconImage) + scaleInt(60) + scaleInt(10));
        //draw the init player reveive box
        int xPos = cancelTradeBtn.getXPos() + theGamePanel.getImgWidth(cancelTradeBtn.getBaseImage());
        int yPos = initiatePlayerReceivesLbl.getYPos() - scaleInt(30);
        g2d.drawRect(xPos,
                yPos,
                this.getWidth() - (xPos * 2),
                lockInitiatePlayerReceiveTradeBtn.getYPos() + theGamePanel.getImgHeight(lockInitiatePlayerReceiveTradeBtn.getBaseImage()) - yPos + scaleInt(10));
        //draw the init player give box
        yPos = initiatePlayerGivesLbl.getYPos() - scaleInt(30);
        g2d.drawRect(xPos,
                yPos,
                this.getWidth() - (xPos * 2),
                lockInitiatePlayerGiveTradeBtn.getYPos() + theGamePanel.getImgHeight(lockInitiatePlayerGiveTradeBtn.getBaseImage()) - yPos + scaleInt(10));

        //draw the progress bar of the trade process
        //calc where the bar should start being drawn
        int progessBarStartPosX = this.getWidth() - (theGamePanel.getImgWidth(PROGRESS_BAR_COMPLETE) * 4 + scaleInt(5)); //times 4 because of the 4 segments in the bar 
        Image image;
        //draw the 4 segments
        for (int i = 0; i < 4; i++) {

            image = i < domesticTradeMode ? PROGRESS_BAR_COMPLETE : PROGRESS_BAR_UNCOMPLETE;

            g2d.drawImage(image,
                    progessBarStartPosX + (theGamePanel.getImgWidth(PROGRESS_BAR_COMPLETE) * i),
                    scaleInt(5),
                    theGamePanel.getImgWidth(PROGRESS_BAR_COMPLETE),
                    theGamePanel.getImgHeight(PROGRESS_BAR_COMPLETE), this);
        }

        //draw the trade mode
        g2d.drawString("Trade Mode: " + domesticTradeMode, 0, 500);

        //=-=-=-=-=-=-=-=-= END OF draw the menu segment boarders =-=-=-=-=-=-=-=-=
        // Add alignment lines
        //g2d.drawLine(this.getWidth() / 2, 0, this.getWidth() / 2, this.getHeight());
        //g2d.drawLine(0, this.getHeight() / 2, this.getWidth(), this.getHeight() / 2);
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

        lockInitiatePlayerReceiveTradeBtn.setXPos(this.getWidth() - (titleLbl.getXPos() + theGamePanel.getImgWidth(cancelTradeBtn.getBaseImage())) - theGamePanel.getImgWidth(lockInitiatePlayerReceiveTradeBtn.getBaseImage()) - scaleInt(10)); //move it over by a quarter so it doesnot stic out so much
        lockInitiatePlayerReceiveTradeBtn.setYPos(getCardPosY(1, CARD_CLAY) + theGamePanel.getImgHeight(CARD_CLAY) + scaleInt(20)); //set it to mode 1 because this is the button for init receive

        //player initate give
        String otherPlayerName; //the name in the label for the other player. Will be their number unless 0 in which case name will be "none"

        if (playerSelectedForTrade == 0) {
            otherPlayerName = "None";
        } else {
            otherPlayerName = Integer.toString(playerSelectedForTrade);
        }

        initiatePlayerGivesLbl.setText("Player " + otherPlayerName + " (      ) receives:"); //make sure text is up to date
        stringWidth = g2d.getFontMetrics().stringWidth(initiatePlayerGivesLbl.getText()); //calc how much room it will take up
        initiatePlayerGivesLbl.setXPos((gameFrame.getWidth() / 2) - (stringWidth / 2));
        initiatePlayerGivesLbl.setYPos(lockInitiatePlayerReceiveTradeBtn.getYPos()
                + theGamePanel.getImgHeight(lockInitiatePlayerReceiveTradeBtn.getBaseImage()) + scaleInt(100));

        //setup the label the labels the cards the init player will keep static (not involved in the trade)
        initiatePlayersStaticCardsLbl.setText("Player " + playerStartedDomestic + "'s (      ) non traded cards:"); //make sure text is up to date
        stringWidth = g2d.getFontMetrics().stringWidth(initiatePlayersStaticCardsLbl.getText()); //calc how much room it will take up
        initiatePlayersStaticCardsLbl.setXPos((gameFrame.getWidth() / 2) - (stringWidth / 2));
        initiatePlayersStaticCardsLbl.setYPos(getCardPosY(0, CARD_CLAY) - scaleInt(30));

        lockInitiatePlayerGiveTradeBtn.setXPos(lockInitiatePlayerReceiveTradeBtn.getXPos()); //move it over by a quarter so it doesnot stic out so much
        lockInitiatePlayerGiveTradeBtn.setYPos(getCardPosY(2, CARD_CLAY) + theGamePanel.getImgHeight(CARD_CLAY) + scaleInt(20)); //set it to mode 1 because this is the button for init receive

        completeTradeBtn.setXPos(lockInitiatePlayerGiveTradeBtn.getXPos() + theGamePanel.getImgWidth(lockInitiatePlayerGiveTradeBtn.getBaseImage()) - theGamePanel.getImgWidth(completeTradeBtn.getBaseImage()));
        completeTradeBtn.setYPos(lockInitiatePlayerGiveTradeBtn.getYPos() + theGamePanel.getImgHeight(lockInitiatePlayerGiveTradeBtn.getBaseImage()) + scaleInt(20));

        cancelTradeBtn.setXPos(titleLbl.getXPos());
        cancelTradeBtn.setYPos(this.getHeight() - theGamePanel.getImgHeight(cancelTradeBtn.getBaseImage()) - scaleInt(6));

    }

    /**
     * Draws resource cards on the screen at the desired Y position
     *
     * @param g2d
     * @param cardMode The mode the card drawing will operate in. Corresponds to
     * the indices of drawCardStacks. Value of 0 is the cards the player has, 1
     * is the initiator's receive. 2, is the other receive.
     * @param cards
     * @param playerID
     *
     */
    private void drawCards(Graphics2D g2d, int cardMode, ArrayList<Integer> playerCards) {
        int[] cardTypeCount;
        int cardStartPosition;
        Image image;

        int[] cardStackXPositions = CardUtil.getCardStackXPositions(theGamePanel);

        // Get the number of cards the player has
        int listSize = playerCards.size();

        //get the number of each card type the player has
        cardTypeCount = theGamePanel.countCardTypes(listSize, false, playerCards);

        // Calculate where the first card must go to center the list
        cardStartPosition = CardUtil.getCardStartPosition(0, listSize, theGamePanel);

        //check if the cards would go off the screen
        //by checking if the start pos of the cards would be past the ending of the exit button
        if (cardStartPosition < (cancelTradeBtn.getXPos() + theGamePanel.getImgWidth(cancelTradeBtn.getBaseImage()))) {
            drawCardStacks[cardMode] = true;

            //draw the number of cards the payer has of each type
            //change the font
            Font tempFont = g2d.getFont(); //save the current stroke
            g2d.setFont(new Font("Times New Roman", Font.BOLD, scaleInt(40))); //overwrite it      
            g2d.setColor(new java.awt.Color(255, 255, 225));

            //loop through and draw the stacked cards
            for (int i = 0; i < 5; i++) {

                //get the image for that card
                switch (i) {
                    case 0: // Clay card
                        image = CARD_CLAY;
                        break;
                    case 1: // Word card
                        image = CARD_WOOD;
                        break;
                    case 2: // Wheat card
                        image = CARD_WHEAT;
                        break;
                    case 3: // Sheep card
                        image = CARD_SHEEP;
                        break;
                    case 4: // 5: Ore card
                        image = CARD_ORE;
                        break;
                    default: //error "card"
                        image = WATER_RING; //this is a joke. The ring of water is infact NOT a card
                        break;
                }

                //draw one each of the 5 card types
                //draw the card image
                g2d.drawImage(image,
                        cardStackXPositions[i], //align the card to the left edge of the water ring 
                        getCardPosY(cardMode, image),
                        theGamePanel.getImgWidth(image),
                        theGamePanel.getImgHeight(image),
                        null);

                //draw the number of cards of that type
                g2d.drawString("x" + cardTypeCount[i],
                        cardStackXPositions[i] + theGamePanel.getImgWidth(image), //align the number to the right edge of the card
                        getCardPosY(cardMode, image) + theGamePanel.getImgHeight(image) / 2);

                /*
                //draw the hitbox but only if there are cards availible to be taken. No hitbox around a stack that has 0 cards.
                if (showCardHitbox && cardTypeCount[i] > 0) {
                    //decide if to draw this on in the loop
                    if (playerID != currentPlayer) { //if the cards being drawn don't match the current player don't show hitboxes
                        drawSpecificHitbox = false;
                    } else if (tradingMode == 0 && tradeResource == 0) { //if not trading draw it for theif discarding
                        drawSpecificHitbox = true;
                    } else if (tradingMode == 3) { //special handeling for 2:1
                        //check if the card is of the type the player muct trade for 2:1 trading
                        drawSpecificHitbox = (i + 1) != tradeResource && (playerHasPort[playerID][i + 1]) && cardTypeCount[i] >= minTradeCardsNeeded;
                    } else { //if it is for other 4:1 or 3:1 trading purpous do some more checks
                        //has to have more than the minimum or more cards and cannot be the same type of card the play wants to end up with.
                        drawSpecificHitbox = cardTypeCount[i] >= minTradeCardsNeeded && (i + 1) != tradeResource;
                    }

                    if (drawSpecificHitbox) {
                        //draw the high light
                        g2d.setColor(new java.awt.Color(255, 255, 225, 128));
                        g2d.fillRect(cardStackXPositions[i],
                                (int) (this.getHeight() - (theGamePanel.getImgHeight(image) * 1.125)),
                                theGamePanel.getImgWidth(image),
                                theGamePanel.getImgHeight(image));
                        //draw the boarder
                        g2d.setColor(new java.awt.Color(102, 62, 38));
                        Stroke tempStroke = g2d.getStroke();
                        g2d.setStroke(new BasicStroke((float) (5 / scaleFactor)));
                        g2d.drawRect(cardStackXPositions[i],
                                (int) (this.getHeight() - (theGamePanel.getImgHeight(image) * 1.125)),
                                theGamePanel.getImgWidth(image),
                                theGamePanel.getImgHeight(image));
                        g2d.setStroke(tempStroke);
                        g2d.setColor(new java.awt.Color(255, 255, 225));
                    }
                }  */
            }

            //restore the old font
            g2d.setFont(tempFont);

        } else { //if the cards would NOT go off the screen
            drawCardStacks[cardMode] = false;

            // Draw the player's cards
            // Reuse the image variable
            int type;
            for (int i = 0; i < listSize; i++) {
                // Get the card type
                type = playerCards.get(i);
                // Get the image for that card
                switch (type) {
                    case 1: // Clay card
                        image = CARD_CLAY;
                        break;
                    case 2: // Word card
                        image = CARD_WOOD;
                        break;
                    case 3: // Wheat card
                        image = CARD_WHEAT;
                        break;
                    case 4: // Sheep card
                        image = CARD_SHEEP;
                        break;
                    case 5: // 5: Ore card
                        image = CARD_ORE;
                        break;
                    default: //error "card"
                        image = WATER_RING; //this is a joke. The ring of water is infact NOT a card
                        break;
                }

                // Draw the card
                g2d.drawImage(image,
                        (cardStartPosition + (theGamePanel.getImgWidth(CARD_CLAY) + scaleInt(10)) * i),
                        getCardPosY(cardMode, image),
                        theGamePanel.getImgWidth(image),
                        theGamePanel.getImgHeight(image),
                        null);

                /*
                //draw the hitbox
                if (showCardHitbox) {
                    //decide if to draw this on in the loop
                    if (playerID != currentPlayer) { //if the cards being drawn don't match the current player don't show hitboxes
                        drawSpecificHitbox = false;
                    } else if (tradingMode == 0 && tradeResource == 0) { //if not trading draw it for theif discarding
                        drawSpecificHitbox = true;
                    } else if (tradingMode == 3) { //special handeling for 2:1
                        //check if the card is of the type the player muct trade for 2:1 trading
                        drawSpecificHitbox = cards[playerID].get(i) != tradeResource && (playerHasPort[playerID][cards[playerID].get(i)]) && numCardType[type] >= minTradeCardsNeeded;
                    } else { //if it is for trading purpous do some more checks
                        //has to have more than 4 or more cards and cannot be the same type of card the play wants to end up with.
                        drawSpecificHitbox = numCardType[type] >= minTradeCardsNeeded && cards[playerID].get(i) != tradeResource;
                    }

                    if (drawSpecificHitbox) {
                        //draw the high light
                        g2d.setColor(new java.awt.Color(255, 255, 225, 128));
                        g2d.fillRect((cardStartPosition + (theGamePanel.getImgWidth(CARD_CLAY) + scaleInt(10)) * i),
                                (int) (this.getHeight() - (theGamePanel.getImgHeight(image) * 1.125)),
                                theGamePanel.getImgWidth(image),
                                theGamePanel.getImgHeight(image));
                        //draw the boarder
                        g2d.setColor(new java.awt.Color(102, 62, 38));
                        Stroke tempStroke = g2d.getStroke();
                        g2d.setStroke(new BasicStroke((float) (5 / scaleFactor)));
                        g2d.drawRect((cardStartPosition + (theGamePanel.getImgWidth(CARD_CLAY) + scaleInt(10)) * i),
                                (int) (this.getHeight() - (theGamePanel.getImgHeight(image) * 1.125)),
                                theGamePanel.getImgWidth(image),
                                theGamePanel.getImgHeight(image));
                        g2d.setStroke(tempStroke);
                    }
                }*/
            }
        }
    }

    private int getCardPosY(int cardMode, Image image) {
        int cardPosY = 0; //default to the top
        //get the y pos based off of the card mode
        switch (cardMode) {
            case 0:
                //if this is the cards that the player has right now
                cardPosY = (int) (this.getHeight() - (theGamePanel.getImgHeight(image) * 1.125));
                break;
            case 1:
                //if these are the cards that the initiation player receives
                cardPosY = initiatePlayerReceivesLbl.getYPos() + scaleInt(20);
                break;
            case 2:
                //if these are the cards the the init player gives away
                cardPosY = initiatePlayerGivesLbl.getYPos() + scaleInt(20);
                break;
            default:
                break;
        }

        return cardPosY;
    }

    /**
     * If the player would like to cancel the trade show the game panel again.
     */
    private void cancelTradeBtnPressed() {
        //set the mode to online cancelation mode
        domesticTradeMode = -1;

        //if not the auth user send it to the server
        if (!getAuthUser()) { //only sned of not auth user because the auth user will send it automatically.
            //and send the trade data over to the server
            onlineUpdateTradeDataServer();
        }

        gameFrame.switchToTrade(false);
    }

    /**
     * If one of the buttons to lock in the cards a player will receive in a
     * trade was pressed.
     *
     * @param lockBtn
     */
    private void lockInitiatePlayerTradeBtnPressed(SettlerBtn lockBtn) {
        //toggle the mode
        if (lockBtn.getMode() == 0) {
            lockBtn.setMode(1);

            //progress the trade mode to the next stage now that this button is locked
            domesticTradeMode++;
        } else {
            lockBtn.setMode(0);

            //regress the trade mode to the next stage now that this button is unlocked again
            domesticTradeMode--;
        }
    }

    /**
     * Complete the Trade. The players have reached a decision and want to
     * switch cards.
     */
    private void completeTradeBtnPressed() {

        /**
         * Since the cards that are being traded away are already removed from
         * the tradeCardsAlreadyHadPlayerStartedDomestic ArrayLists, just add
         * the cards they will receive to the other ArrayList
         */
        tradeCardsAlreadyHadPlayerStartedDomestic.addAll(tradeCardsReceivePlayerStartedDomestic);

        //and the same for the other player
        tradeCardsAlreadyHadPlayerSelected.addAll(tradeCardsGivePlayerStartedDomestic);

        //sort the cards
        theGamePanel.quickSortCards(tradeCardsAlreadyHadPlayerStartedDomestic, 0, tradeCardsAlreadyHadPlayerStartedDomestic.size() - 1);
        //sort the cards 
        theGamePanel.quickSortCards(tradeCardsAlreadyHadPlayerSelected, 0, tradeCardsAlreadyHadPlayerSelected.size() - 1);

        //get the cards from theGamePanel
        ArrayList<Integer>[] gameCards = theGamePanel.getResourceCards();

        //save said ArrayLists into the Cards array
        gameCards[playerStartedDomestic] = tradeCardsAlreadyHadPlayerStartedDomestic;
        gameCards[playerSelectedForTrade] = tradeCardsAlreadyHadPlayerSelected;

        //save said ArrayLists into the gamePanel
        theGamePanel.setResourceCards(gameCards);

        //if in online mode update the server and there by the other players
        theGamePanel.onlineUpdateServer();

        //set the mode to online cancelation mode
        domesticTradeMode = -1;

        //hide the trade panel
        gameFrame.switchToTrade(false);

    }

    /**
     * Actions to take when the user clicks on the trade panel
     *
     * @param evt
     */
    public void tradeMouseClicked(MouseEvent evt) {
        boolean authorizedUser; //is this user allowed to click things?

        authorizedUser = getAuthUser();

        //Loop through all the Buttons and see if they were clicked
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

                //only check the rest of the buttons if there is an authorized user
                if (authorizedUser) {

                    if (btn.equals(lockInitiatePlayerGiveTradeBtn)) {
                        lockInitiatePlayerTradeBtnPressed(btn);
                    } else if (btn.equals(lockInitiatePlayerReceiveTradeBtn)) {
                        lockInitiatePlayerTradeBtnPressed(btn);
                    } else if (btn.equals(completeTradeBtn)) { //if the accept trade button has been clicked
                        completeTradeBtnPressed();
                    }
                }
            }
        }

        //only check for clicks if the user is authorized
        if (authorizedUser) {

            //if no button was click check what mode the trade is in and see if there is an appropriate action
            if (domesticTradeMode == 0) { //if we are in the starter mode where a player to trade with must be selected

                int tradePlayerPosY = playerSelectLbl.getYPos() + scaleInt(30);
                int tradePlayerPosX;

                //loop through the subsequest players and see if there was a click on one of them. Skip the first player in the ArrayList because it is the current players
                for (int i = 0; i < nonInitiatePlayers.length; i++) {

                    tradePlayerPosX = playerIconStartPos + ((theGamePanel.getImgWidth(SMALL_PLAYER_RED)) * (i));

                    //check if there was a click on one of the sub players
                    if (evt.getX() > tradePlayerPosX
                            && evt.getY() > tradePlayerPosY
                            && evt.getX() < (tradePlayerPosX + (theGamePanel.getImgWidth(SMALL_PLAYER_RED)))
                            && evt.getY() < (tradePlayerPosY + (theGamePanel.getImgHeight(SMALL_PLAYER_RED)))) {

                        //debug
                        //System.out.println("Yuh we got a click on player: " + nonInitiatePlayers[i]);
                        //save the player that was selected for trade
                        playerSelectedForTrade = nonInitiatePlayers[i];

                        //save the cards this player has at the current moment
                        tradeCardsAlreadyHadPlayerSelected = (ArrayList<Integer>) (theGamePanel.getResourceCards()[playerSelectedForTrade].clone());

                        //enter the next mode of trade
                        domesticTradeMode++;

                    }

                }
            } else if (domesticTradeMode == 1 || domesticTradeMode == 2) { //if in mode 1, or 2
                //check if a player clicked on some cards

                int cardYPos;

                //loop through the 3 card hand locations
                for (int cardHandNum = 0; cardHandNum < drawCardStacks.length; cardHandNum++) {

                    //get the y position for the cards
                    cardYPos = getCardPosY(cardHandNum, CARD_CLAY);

                    //check what mode the card drawing is in
                    if (drawCardStacks[cardHandNum]) { //check for a click on a cards in the stacked mode

                        //loop though the 5 stacks
                        for (int i = 0; i < 5; i++) {

                            //check for a click
                            if (evt.getX() > CardUtil.getCardStackXPositions(theGamePanel)[i]
                                    && evt.getX() < (CardUtil.getCardStackXPositions(theGamePanel)[i] + theGamePanel.getImgWidth(CARD_CLAY))
                                    && evt.getY() > cardYPos
                                    && evt.getY() < (cardYPos + theGamePanel.getImgHeight(CARD_CLAY))) {

                                //debug click detection
                                //System.out.println("Card stack Clicked!");
                            }
                        }

                    } else { //check for a click on a card in the full layout mode     

                        //check if the user clicked on any card
                        for (int i = 0; i < getCardHand(cardHandNum).size(); i++) {

                            //get the x position for that card
                            int cardXPos = (CardUtil.getCardStartPosition(0, getCardHand(cardHandNum).size(), theGamePanel) + (theGamePanel.getImgWidth(CARD_CLAY) + scaleInt(10)) * i);

                            //check if there was a click on a card
                            if (evt.getX() > cardXPos
                                    && evt.getY() > cardYPos
                                    && evt.getX() < (cardXPos + theGamePanel.getImgWidth(CARD_CLAY))
                                    && evt.getY() < (cardYPos + theGamePanel.getImgHeight(CARD_CLAY))) {

                                //add the card to the other players receive hand
                                mutateTradeCardLists(getCardHand(cardHandNum).get(i), cardHandNum);
                            }
                        }
                    }
                }
            }

            updateComponentState();
            repaint();

            //and send the trade data over to the server
            onlineUpdateTradeDataServer();

        }
    }

    /**
     * Changes the ArrayLists of cards that are displayed for the trade
     *
     * @param cardNum
     */
    private void mutateTradeCardLists(int cardNum, int cardHandNum) {

        //decicde what list to add/ remove to
        //if in mode 1
        if (domesticTradeMode == 1) {
            //if the players existing cards at the bottom were clicked
            if (cardHandNum == 0) {
                tradeCardsGivePlayerStartedDomestic.add(cardNum);
                tradeCardsAlreadyHadPlayerStartedDomestic.remove(new Integer(cardNum));
            } //if the players cards they will be trading away were clicked
            else if (cardHandNum == 2) {
                tradeCardsGivePlayerStartedDomestic.remove(new Integer(cardNum));
                tradeCardsAlreadyHadPlayerStartedDomestic.add(cardNum);

                //sort the cards 
                theGamePanel.quickSortCards(tradeCardsAlreadyHadPlayerStartedDomestic, 0, tradeCardsAlreadyHadPlayerStartedDomestic.size() - 1);
            }

        } else if (domesticTradeMode == 2) { //if it is now the time for the selected player to put up their cards for trade
            //if the players existing cards at the bottom were clicked
            if (cardHandNum == 0) {
                tradeCardsReceivePlayerStartedDomestic.add(cardNum);
                tradeCardsAlreadyHadPlayerSelected.remove(new Integer(cardNum));
            }//if the players cards they will be trading away were clicked
            else if (cardHandNum == 1) {
                tradeCardsReceivePlayerStartedDomestic.remove(new Integer(cardNum));
                tradeCardsAlreadyHadPlayerSelected.add(cardNum);

                //sort the cards 
                theGamePanel.quickSortCards(tradeCardsAlreadyHadPlayerSelected, 0, tradeCardsAlreadyHadPlayerSelected.size() - 1);
            }

        }

    }

    /**
     * From one of three card hand numbers return a set of cards
     *
     * @param cardHandNum Value of 0 is the cards the player has, 1 is the
     * initiator's receive. 2, is the other receive.
     * @return
     */
    private ArrayList<Integer> getCardHand(int cardHandNum) {
        switch (cardHandNum) {
            case 0: //the hand at the bottom of the screen
                //decide if to show the init player's hand or the selected player
                if (domesticTradeMode == 2) {
                    return tradeCardsAlreadyHadPlayerSelected;
                } else {
                    return tradeCardsAlreadyHadPlayerStartedDomestic;
                }
            case 1:
                return tradeCardsReceivePlayerStartedDomestic;
            case 2:
                return tradeCardsGivePlayerStartedDomestic;
            default:
                JOptionPane.showMessageDialog(null, "Bad Array", "ERROR", JOptionPane.ERROR_MESSAGE);
                System.out.println("ERROR BAD ARRAY");
                ArrayList<Integer> badList = new ArrayList<>();
                badList.add(-1);
                return badList;
        }
    }

    /**
     * Plays a simular role as updateBuildButtons() does in the GamePanel.
     * Mutates many attributes in the buttons, labels, etc.
     */
    public void updateComponentState() {

        //update all the buttons based off the trade mode
        switch (domesticTradeMode) {
            case 0:
                //if in player select mode all buttons should be off
                lockInitiatePlayerReceiveTradeBtn.setEnabled(false);
                lockInitiatePlayerGiveTradeBtn.setEnabled(false);
                completeTradeBtn.setEnabled(false);
                break;
            case 1:
                //if in start player cards give away mode
                lockInitiatePlayerReceiveTradeBtn.setEnabled(false);
                lockInitiatePlayerReceiveTradeBtn.setMode(0);
                lockInitiatePlayerGiveTradeBtn.setEnabled(true);
                lockInitiatePlayerGiveTradeBtn.setMode(0);
                completeTradeBtn.setEnabled(false);
                break;
            case 2:
                //if in trade partner player cards give away mode
                lockInitiatePlayerReceiveTradeBtn.setEnabled(true);
                lockInitiatePlayerReceiveTradeBtn.setMode(0);
                lockInitiatePlayerGiveTradeBtn.setEnabled(true);
                lockInitiatePlayerGiveTradeBtn.setMode(1);
                completeTradeBtn.setEnabled(false);
                break;
            case 3:
                //if in ready to complete trade mode
                lockInitiatePlayerReceiveTradeBtn.setEnabled(true);
                lockInitiatePlayerReceiveTradeBtn.setMode(1);
                lockInitiatePlayerGiveTradeBtn.setEnabled(false);
                lockInitiatePlayerGiveTradeBtn.setMode(1);
                completeTradeBtn.setEnabled(true);
                break;
            default:
                break;
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

        updateComponentState();
        repaint();
    }

    /**
     * If the game is in online mode update the server with the current domestic
     * trade data.
     */
    public void onlineUpdateTradeDataServer() {
        //debug statemtn for updateing the game server
        //System.out.println("Updateing online");

        //check if the game is for online play
        if (GamePanel.getOnlineMode() != -1) {
            //if it is send the trade data to the server
            //also send the online player number
            GamePanel.getCatanClient().sendDomesticTradeToServer(GamePanel.getOnlineMode(), playerStartedDomestic, playerSelectedForTrade, domesticTradeMode,
                    tradeCardsGivePlayerStartedDomestic, tradeCardsReceivePlayerStartedDomestic, tradeCardsAlreadyHadPlayerStartedDomestic, tradeCardsAlreadyHadPlayerSelected);
        }
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
        initiatePlayerGivesLbl.setFont(headerFont);
        initiatePlayersStaticCardsLbl.setFont(headerFont);

        repaint();
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
     * Get the ID of the player that was selected for the domestic trade
     *
     * @return
     */
    public int getPlayerSelectedForTrade() {
        return playerSelectedForTrade;
    }

    /**
     * Set the ID of the player that was selected for the domestic trade
     *
     * @param playerSelectedForTrade
     */
    public void setPlayerSelectedForTrade(int playerSelectedForTrade) {
        this.playerSelectedForTrade = playerSelectedForTrade;
    }

    /**
     * Get the current mode of trade the panel is in.
     *
     * @return
     */
    public int getDomesticTradeMode() {
        return domesticTradeMode;
    }

    /**
     * Set the current mode of trade the panel is in.
     *
     * @param domesticTradeMode
     */
    public void setDomesticTradeMode(int domesticTradeMode) {
        this.domesticTradeMode = domesticTradeMode;
    }

    /**
     * Get the cards the player who started the trade already had when they
     * initiated said trade.
     *
     * @return
     */
    public ArrayList<Integer> getTradeCardsAlreadyHadPlayerStartedDomestic() {
        return tradeCardsAlreadyHadPlayerStartedDomestic;
    }

    /**
     * Set the cards the player who started the trade already had when they
     * initiated said trade.
     *
     * @param newTradeCardsAlreadyHadPlayerStartedDomestic
     */
    public void setTradeCardsAlreadyHadPlayerStartedDomestic(ArrayList<Integer> newTradeCardsAlreadyHadPlayerStartedDomestic) {
        this.tradeCardsAlreadyHadPlayerStartedDomestic = newTradeCardsAlreadyHadPlayerStartedDomestic;
    }

    /**
     * Get the cards the player who was selected for the trade already had when
     * they we selected for said trade.
     *
     * @return
     */
    public ArrayList<Integer> getTradeCardsAlreadyHadPlayerSelected() {
        return tradeCardsAlreadyHadPlayerSelected;
    }

    /**
     * Set the cards the player who was selected for the trade already had when
     * they we selected for said trade.
     *
     * @param newTradeCardsAlreadyHadPlayerSelected
     */
    public void setTradeCardsAlreadyHadPlayerSelected(ArrayList<Integer> newTradeCardsAlreadyHadPlayerSelected) {
        this.tradeCardsAlreadyHadPlayerSelected = newTradeCardsAlreadyHadPlayerSelected;
    }

    /**
     * Get the cards the player who started the trade will be giving away.
     *
     * @return
     */
    public ArrayList<Integer> getTradeCardsGivePlayerStartedDomestic() {
        return tradeCardsGivePlayerStartedDomestic;
    }

    /**
     * Set the cards the player who started the trade will be giving away.
     *
     * @param newTradeCardsGivePlayerStartedDomestic
     */
    public void setTradeCardsGivePlayerStartedDomestic(ArrayList<Integer> newTradeCardsGivePlayerStartedDomestic) {
        this.tradeCardsGivePlayerStartedDomestic = newTradeCardsGivePlayerStartedDomestic;
    }

    /**
     * Get the cards the player who started the trade will be receiving.
     *
     * @return
     */
    public ArrayList<Integer> getTradeCardsReceivePlayerStartedDomestic() {
        return tradeCardsGivePlayerStartedDomestic;
    }

    /**
     * Set the cards the player who started the trade will be receiving.
     *
     * @param newTradeCardsReceivePlayerStartedDomestic
     */
    public void setTradeCardsReceivePlayerStartedDomestic(ArrayList<Integer> newTradeCardsReceivePlayerStartedDomestic) {
        this.tradeCardsReceivePlayerStartedDomestic = newTradeCardsReceivePlayerStartedDomestic;
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

    /**
     * Returns whether or not a user is authorized to click on elements.
     *
     * @return
     */
    private boolean getAuthUser() {
        boolean authorizedUser;

        //decide if the user is authorized
        if (GamePanel.getOnlineMode() == -1) { //if in offline mode fully authed
            authorizedUser = true;
        } else if (domesticTradeMode != 2) { //else if the mode is in anything other than where the selected player is choosing cards
            authorizedUser = GamePanel.getOnlineMode() == playerStartedDomestic;
        } else { //else if it is mode 2 then the auth user is the one selected for trade
            authorizedUser = GamePanel.getOnlineMode() == playerSelectedForTrade;
        }

        return authorizedUser;
    }
}
