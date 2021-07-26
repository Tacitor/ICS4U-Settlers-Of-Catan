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

    public static boolean canUseDevCard(ArrayList<Integer> playersDevCards, int devCardType) {

        boolean canUse = false;
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

}
