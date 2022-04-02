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
import java.util.ArrayList;
import javax.swing.JPanel;
import krampitzkreutzwisersettlersofcatan.worldObjects.buttons.SettlerBtn;
import textures.ImageRef;
import static krampitzkreutzwisersettlersofcatan.gui.GamePanel.scaleInt;
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
    private SettlerBtn cancelTradeBtn, lockInitiatePlayerReceiveTradeBtn;
    //The array for the buttons
    private SettlerBtn[] settlerBtns;
    //Settler Lable
    private SettlerLbl titleLbl, playerSelectLbl, initiatePlayerReceivesLbl; //receives
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
    private int playerStartedDomestic; //the player ID of the user the clicked the domestic trade button
    private int playerCount; //the number of players in the game
    private int[] nonInitiatePlayers; //the list of the remaining player(s) that did not start the trade
    private ArrayList<Integer> tradeReceivePlayerStartedDomestic; //The cards that the player who started the trade will receive 

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
        cancelTradeBtn = new SettlerBtn(true, 1, 9);
        lockInitiatePlayerReceiveTradeBtn = new SettlerBtn(true, 1, 10);
        //add then to the array
        settlerBtns = new SettlerBtn[]{cancelTradeBtn, lockInitiatePlayerReceiveTradeBtn};

        //setup the labels
        settlerLbls = new SettlerLbl[]{titleLbl, playerSelectLbl, initiatePlayerReceivesLbl};

        tradeReceivePlayerStartedDomestic = new ArrayList<>();
        tradeReceivePlayerStartedDomestic.add(1);
        tradeReceivePlayerStartedDomestic.add(1);
        tradeReceivePlayerStartedDomestic.add(1);
        tradeReceivePlayerStartedDomestic.add(2);
        tradeReceivePlayerStartedDomestic.add(2);
        tradeReceivePlayerStartedDomestic.add(2);
        tradeReceivePlayerStartedDomestic.add(3);
        tradeReceivePlayerStartedDomestic.add(3);
        tradeReceivePlayerStartedDomestic.add(3);
        tradeReceivePlayerStartedDomestic.add(3);
        tradeReceivePlayerStartedDomestic.add(3);
        tradeReceivePlayerStartedDomestic.add(4);
        tradeReceivePlayerStartedDomestic.add(4);
        tradeReceivePlayerStartedDomestic.add(4);
        tradeReceivePlayerStartedDomestic.add(5);
        tradeReceivePlayerStartedDomestic.add(5);
        tradeReceivePlayerStartedDomestic.add(5);
        tradeReceivePlayerStartedDomestic.add(5);
        tradeReceivePlayerStartedDomestic.add(5);
        tradeReceivePlayerStartedDomestic.add(5);
        tradeReceivePlayerStartedDomestic.add(5);
        tradeReceivePlayerStartedDomestic.add(5);
        tradeReceivePlayerStartedDomestic.add(5);

        tradeReceivePlayerStartedDomestic.add(5);

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

        //draw the cards the initation player HAS
        drawCards(g2d, 0, theGamePanel.getResourceCards()[playerStartedDomestic], playerStartedDomestic);

        //draw the cards the intiation player will receive
        drawCards(g2d, 1, tradeReceivePlayerStartedDomestic, playerStartedDomestic);

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

        lockInitiatePlayerReceiveTradeBtn.setXPos(CardUtil.getCardStackXPositions(theGamePanel)[4] //align the button with the last stack of resource cards
                - (theGamePanel.getImgWidth(lockInitiatePlayerReceiveTradeBtn.getBaseImage()) / 4)); //move it over by a quarter so it doesnot stic out so much
        lockInitiatePlayerReceiveTradeBtn.setYPos(getCardPosY(1, CARD_CLAY) + theGamePanel.getImgHeight(CARD_CLAY) + scaleInt(20)); //set it to mode 1 because this is the button for init receive
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
    private void drawCards(Graphics2D g2d, int cardMode, ArrayList<Integer> playerCards, int IDofPlayer) {
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
        if (cardMode == 0) { //if this is the cards that the player has right now
            cardPosY = (int) (this.getHeight() - (theGamePanel.getImgHeight(image) * 1.125));
        } else if (cardMode == 1) { //if these are the cards that the initiation player receives
            cardPosY = initiatePlayerReceivesLbl.getYPos() + scaleInt(20);
        }

        return cardPosY;
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
