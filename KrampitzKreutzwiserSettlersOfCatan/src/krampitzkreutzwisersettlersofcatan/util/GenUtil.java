/*
 * Lukas Krampitz
 * Aug 23, 2021
 * A GENeric utility class
 */
package krampitzkreutzwisersettlersofcatan.util;

/**
 *
 * @author Tacitor
 */
public class GenUtil {

    /**
     * Given the online mode and the current player, determines which player
     * Number should be used for displays.
     *
     * @param onlineMode
     * @param currentPlayer
     * @return
     */
    public static int getDisplayUserNum(int onlineMode, int currentPlayer) {

        if (onlineMode == -1) { //if in offline mode use the current player
            return currentPlayer;
        } else {
            return onlineMode;
        }

    }

}
