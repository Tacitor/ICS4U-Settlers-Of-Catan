/*
 * Lukas Krampitz
 * Jul 26, 2021
 * A utility class for Resource and Development Cards
 */
package krampitzkreutzwisersettlersofcatan.util;

import dataFiles.DevCardToolTips;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Collections;
import krampitzkreutzwisersettlersofcatan.gui.GamePanel;
import krampitzkreutzwisersettlersofcatan.worldObjects.buttons.SettlerLbl;
import textures.ImageRef;
import static textures.ImageRef.CARD_CLAY;
import static textures.ImageRef.DEV_CARD_KNIGHT;

/**
 *
 * @author Tacitor
 */
public class CardUtil {

    //static vars
    //card vars
    private static ArrayList<Integer> newlyBoughtDevCards = new ArrayList<>(); //an ArrayList containing the cards that cannot be played this round because they were just bought
    //time and hover vars
    private static boolean showDevCardToolTip; //whether or not the game is showing a tool tip right now
    public static int toolTipDevCardIndex = -1; //the index of the dev cards that is getting the tool tip
    private static int prevMouseX = -1; //the previous X position of the mouse, used to see if it moved since the last check
    private static int prevMouseY = -1;
    private static long timeOfMousePosTaken = 0; //the time in miliseconds when the current mouse position was taken. Any time the mouse position is not the same as the time of the last check this set to the System.currentTimeMillis()

    /**
     * Determines whether or not a development card from a given set of
     * development cards is able to be played with regards other development
     * cards that may have been bought this round.
     *
     * @param playersDevCards
     * @param devCardType
     * @return
     */
    public static boolean canUseDevCard(ArrayList<Integer> playersDevCards, int devCardType) {

        boolean canUse;
        int boughtNum; //the number of that type that is newly bought
        int hasNum; //the number of the type of card that the player has

        //count how many of the card the player has
        boughtNum = Collections.frequency(newlyBoughtDevCards, devCardType);
        //debug card nums
        //System.out.println("Bought: " + boughtNum);

        //count the number of cards of the type the player owns
        hasNum = Collections.frequency(playersDevCards, devCardType);
        //debug card nums
        //System.out.println("Has: " + hasNum);

        //if the user has more of a card type than they bought that round, let them use it
        canUse = (hasNum - boughtNum) > 0;

        return canUse;
    }

    /**
     * Determines if a given player has the right development cards to enable
     * the use development cards button. Also takes into account cards that were
     * bought this round
     *
     * @param playersDevCards
     * @return
     */
    public static boolean hasDevCards(ArrayList<Integer> playersDevCards) {
        boolean hasCards = false;

        //if there are any cards
        if (playersDevCards.size() > 0) {

            //loop through the first few types and see if they are contained in the list
            //and also if the user has not bought this card this round
            for (int i = 1; i < 5; i++) { //go thorugh card types 1, 2, 3, 4
                if (playersDevCards.contains(i) && canUseDevCard(playersDevCards, i)) {
                    hasCards = true;
                }
            }

        }

        return hasCards;
    }

    /**
     * Add a new card to the list of cards that cannot be used this round
     * because it is a new development card
     *
     * @param cardType
     */
    public static void addNewlyBoughtDevCard(int cardType) {
        newlyBoughtDevCards.add(cardType);
    }

    /**
     * Remove all elements from the ArrayList
     */
    public static void clearNewlyBoughtDevCard() {
        newlyBoughtDevCards.clear();
    }

    /**
     * Return the newlyBoughtDevCards is a state that is ready to be added to
     * the save File
     *
     * @return
     */
    public static String newlyBoughtDevCardsToString() {
        String output = "newlyBoughtDevCards:";
        output += "\nsize:\n" + newlyBoughtDevCards.size();
        output += "\ncardTypes:\n";

        //add in the types
        for (int i = 0; i < newlyBoughtDevCards.size(); i++) {
            output += newlyBoughtDevCards.get(i) + "\n";
        }

        return output;

    }

    /**
     * Determine if the value to display for a player's victory points. Accounts
     * for if the game is online or offline. Always gives the real value when
     * offline. When online it also only gives the real value when it is the the
     * player's client. Everyone else in online mode get the value with the
     * point awarded from development cards removed.
     *
     * @param onlineMode Is the game online of offline? If online what is the
     * player for this game?
     * @param playerFor What player are these points for?
     * @param trueVPCount The correct and full count of victory points
     * @param devCards A full ArrayList of the developments cards the playerFor
     * has to count how many VP card points need to be hidden
     * @param victoryPointsToWin
     * @return The number of VPs to display for the playerFor
     */
    public static int concealedVictoryPointCount(int onlineMode, int playerFor, int trueVPCount, ArrayList<Integer> devCards, int victoryPointsToWin) {

        //check for offline play
        if (onlineMode == -1) {
            return trueVPCount;
        } else { //else in online mode

            //check if this is for the same player's online client
            //and that they have less than the point threshold for winning
            if (onlineMode == playerFor || trueVPCount >= victoryPointsToWin) {

                return trueVPCount;
            } else { //compute the fake value
                return calcConcealedVictoryPointCount(trueVPCount, devCards);
            }

        }
    }

    /**
     * Given a player's Victory points and their set of Development cards,
     * computer how many of those points are from VP development cards. Remove
     * said VP card points and return the new concealed value. If the player has
     * no VP development cards the concealed victory point count is the same as
     * the true one.
     *
     * @param thetrueVPCount
     * @param theDevCards
     * @return
     */
    private static int calcConcealedVictoryPointCount(int thetrueVPCount, ArrayList<Integer> theDevCards) {

        int numDevVPCard = 0; //count how many of the dev cards are vps

        //loop through the dev cards
        for (Integer num : theDevCards) {
            //check if it's a vp card
            //vp cards are 5, 6, 7, 8, 9
            if (num >= 5 && num <= 9) {
                numDevVPCard++;
            } else if (num > 9) {
                System.out.println("ERROR WITH DEV CARDS. VALUE \'9\' EXEEDED with: " + num + "\n\t\tFrom CardUtil class");
            }
        }

        //remove the number of VP card points from the total VP count
        return thetrueVPCount - numDevVPCard;

    }

    /**
     * Check if a tool tip should be drawn for a Development card. Given the
     * current mouse position and the development cards that are shown on
     * screen.
     *
     * @param theDevCards
     * @param mouseX
     * @param mouseY
     * @param gamePanel
     * @param drawStacks
     */
    public static void checkForDevCardTooltip(ArrayList<Integer> theDevCards, int mouseX, int mouseY, GamePanel gamePanel, boolean drawStacks) {

        //step 1 is to check if a new tool tip needs to be created
        //therfore see if the mouse is staying in the same place for a while
        if (!showDevCardToolTip) {

            //start with pos
            if (mouseX == prevMouseX && mouseY == prevMouseY) {
                //System.out.println("yes");

                //since it's in the same place now also check if it has been there for atleast 1 second
                //and make sure it ignore the startup state
                if (timeOfMousePosTaken != 0 && (System.currentTimeMillis() - timeOfMousePosTaken) >= 1000) {
                    //System.out.println("yes");
                    //now check if this resting state is ontop of a devcard

                    int listIndex = getIndexOfListMouseIsOn(theDevCards, mouseX, mouseY, gamePanel, drawStacks);

                    //update the results of the querry
                    if (listIndex != -1) {
                        //System.out.println("yes");
                        showDevCardToolTip = true;
                        toolTipDevCardIndex = listIndex;
                    }

                }

            } else {
                //System.out.println("no");
                //since the mouse is not in the same place it means it has moved
                //thefore update the time it took this new position
                timeOfMousePosTaken = System.currentTimeMillis();
            }

            //then update the mouse positions for the next check
            prevMouseX = mouseX;
            prevMouseY = mouseY;

        } //step two is to check in an existing tool tip needs to be removed
        else { //if there is already a tool tip being drawn
            //check if the mouse is still anywhere on the card where the tool tip is being drawn

            //get the index the mouse is on
            int index = getIndexOfListMouseIsOn(theDevCards, mouseX, mouseY, gamePanel, drawStacks);

            //check if it's the same card
            if (index != toolTipDevCardIndex) {
                //if it's not turn off the tool tip
                showDevCardToolTip = false;
                toolTipDevCardIndex = -1;
            }
        }
    }

    /**
     * Calculate the X position on screen of where the cards will begin. This is
     * only for the full layout and not stacked. Uses the gamePanel to determine
     * this with account to the game resolution for scaling.
     *
     * @param type
     * @param listSize
     * @param gamePanel
     * @return
     */
    public static int getCardStartPosition(int type, int listSize, GamePanel gamePanel) {

        Image[] typeImageList = new Image[]{CARD_CLAY, DEV_CARD_KNIGHT};

        int cardStartPosition;

        cardStartPosition = (int) ((GamePanel.getPanelWidth() / 2) - (listSize * gamePanel.getImgWidth(typeImageList[type]) + (listSize - 1) * GamePanel.scaleInt(10)) / 2);

        return cardStartPosition;

    }

    /**
     * Given a list of Development cards and the mouse position find the index
     * of the list the mouse is on. Also find if the mouse is even on a card to
     * begin with. If not it returns a value if -1.
     *
     * @param theDevCards
     * @param mouseX
     * @param mouseY
     * @param gamePanel
     * @param drawStacks
     * @return
     */
    public static int getIndexOfListMouseIsOn(ArrayList<Integer> theDevCards, int mouseX, int mouseY, GamePanel gamePanel, boolean drawStacks) {

        //set up needed vars
        int devCardYPos = (int) (gamePanel.getHeight() - (gamePanel.getImgHeight(DEV_CARD_KNIGHT) * 1.125));
        int devCardXPos;
        int returnVal = -1; //the index of the card or of the stack
        int indexOfFind = -1;

        //different check for differnt layouts
        if (!drawStacks) { //for full layout

            boolean foundOnCard = false; //is the cursor found on any card at all

            //loop through every card
            for (int i = 0; i < theDevCards.size(); i++) {

                //get the x pos of that card
                devCardXPos = (getCardStartPosition(1, theDevCards.size(), gamePanel) + (gamePanel.getImgWidth(DEV_CARD_KNIGHT) + GamePanel.scaleInt(10)) * i);

                //check if the user has their mouse over a dev card
                if (mouseX > devCardXPos
                        && mouseY > devCardYPos
                        && mouseX < (devCardXPos + gamePanel.getImgWidth(DEV_CARD_KNIGHT))
                        && mouseY < (devCardYPos + gamePanel.getImgHeight(DEV_CARD_KNIGHT))) {

                    foundOnCard = true;
                    indexOfFind = i;

                }
            }

            if (foundOnCard) {
                returnVal = indexOfFind;
            } else {
                returnVal = -1;
            }

        }

        return returnVal;
    }

    /**
     * Actually draw the Development card too tip. Gets called by the draw
     * method in GamePanel.
     *
     * @param g2d
     * @param theDevCards
     * @param theGamePanel
     * @param drawStacks
     */
    public static void drawDevCardTooltip(Graphics2D g2d, ArrayList<Integer> theDevCards, GamePanel theGamePanel, boolean drawStacks) {

        //check if there is a tool tip to draw
        if (showDevCardToolTip) {

            int devCardYPos = (int) (theGamePanel.getHeight() - (theGamePanel.getImgHeight(DEV_CARD_KNIGHT) * 1.125));
            int devCardXPos;
            String textForTooltip;

            //check what mode the cards are being drawn in
            if (!drawStacks) {

                //find the card to draw it own
                devCardXPos = (getCardStartPosition(1, theDevCards.size(), theGamePanel) + (theGamePanel.getImgWidth(DEV_CARD_KNIGHT) + GamePanel.scaleInt(10)) * toolTipDevCardIndex);

                //draw
                g2d.drawImage(ImageRef.TOOLTIP_DEV_CARD_BGD,
                        devCardXPos,
                        devCardYPos - GamePanel.scaleInt(5) - theGamePanel.getImgHeight(ImageRef.TOOLTIP_DEV_CARD_BGD),
                        theGamePanel.getImgWidth(ImageRef.TOOLTIP_DEV_CARD_BGD),
                        theGamePanel.getImgHeight(ImageRef.TOOLTIP_DEV_CARD_BGD),
                        theGamePanel);

                //find the text for the card
                textForTooltip = DevCardToolTips.getDevCardTooltips()[theDevCards.get(toolTipDevCardIndex) - 1];

                //add the text to the tool tip
                SettlerLbl tooltipText = new SettlerLbl(textForTooltip);
                tooltipText.setForeground(new Color(57, 39, 32));

                tooltipText.setFont(new Font("Calibri", Font.BOLD, GamePanel.scaleInt(16)));
                tooltipText.setXPos(devCardXPos + GamePanel.scaleInt(13));
                tooltipText.setYPos(devCardYPos - theGamePanel.getImgHeight(ImageRef.TOOLTIP_DEV_CARD_BGD) + GamePanel.scaleInt(18));

                //set the line wrap                
                tooltipText.setLineWrap(true);
                tooltipText.setSpaceForText((double) (theGamePanel.getImgWidth(ImageRef.TOOLTIP_DEV_CARD_BGD) - GamePanel.scaleInt(20)));

                //System.out.println("Card: " + theGamePanel.getImgWidth(ImageRef.TOOLTIP_DEV_CARD_BGD));
                //System.out.println("Spacer: " + GamePanel.scaleInt(20));
                //System.out.println("Space: " + tooltipText.getSpaceForText());
                tooltipText.setLinewrapSpace(16);
                tooltipText.calcNumLines(g2d, theGamePanel);

                tooltipText.draw(g2d);
            }
        }

    }

    //Accessors and Mutators    
    /**
     * Access showDevCardToolTip
     *
     * @return
     */
    public static boolean getShowDevCardToolTip() {
        return showDevCardToolTip;
    }

    /**
     * Access toolTipDevCardIndex
     *
     * @return
     */
    public static int getToolTipDevCardIndex() {
        return toolTipDevCardIndex;
    }

}
