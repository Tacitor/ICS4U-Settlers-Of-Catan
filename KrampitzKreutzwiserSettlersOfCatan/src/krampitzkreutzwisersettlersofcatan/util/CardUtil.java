/*
 * Lukas Krampitz
 * Jul 26, 2021
 * A utility class for Resource and Development Cards
 */
package krampitzkreutzwisersettlersofcatan.util;

import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author Tacitor
 */
public class CardUtil {

    private static ArrayList<Integer> newlyBoughtDevCards = new ArrayList<>(); //an ArrayList containing the cards that cannot be played this round because they were just bought

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

}
