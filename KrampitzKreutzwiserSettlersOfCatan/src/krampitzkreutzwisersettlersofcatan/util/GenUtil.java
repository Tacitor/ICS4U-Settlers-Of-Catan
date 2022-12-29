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

    //Static Vars
    private static int[] remainingRoadPieces; //an the size of the number of players (plus 1 for the 0th player). Hold the number of reads that each player has left to build.
    private static int[] remainingSettlementPieces;
    private static int[] remainingCityPieces;

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

    /**
     * Get the number of pieces a given player has of a given type. Given
     * pieceType = 1, and playerNum = 2, then the method will return the number
     * of remaining roads player 2 may build.
     *
     * @param pieceType
     * @param playerNum
     * @return
     */
    public static int getRemainingPlayerPieces(int pieceType, int playerNum) {
        int[] pieceArray;
        switch (pieceType) {
            case 1: //if the number of roads is neeed
                pieceArray = remainingRoadPieces;
                break;
            case 2: //if the number of settlements is neeed
                pieceArray = remainingSettlementPieces;
                break;
            case 3: //if the number of cities is neeed
                pieceArray = remainingCityPieces;
                break;
            default:
                pieceArray = new int[]{-1};
        }

        return pieceArray[playerNum];

    }

    /**
     * Set the number of pieces a given player has of a given type. Given
     * pieceType = 1, and playerNum = 2, then the method will set the number
     * of remaining roads player 2 may build.
     *
     * @param pieceType
     * @param playerNum
     * @param remainingPiecesNum
     */
    public static void setRemainingPlayerPieces(int pieceType, int playerNum, int remainingPiecesNum) {
        int[] pieceArray;

        switch (pieceType) {
            case 1: //if the number of roads is neeed
                pieceArray = remainingRoadPieces;
                break;
            case 2: //if the number of settlements is neeed
                pieceArray = remainingSettlementPieces;
                break;
            case 3: //if the number of cities is neeed
                pieceArray = remainingCityPieces;
                break;
            default:
                System.out.println("ERROR: Invalid pieceType specified");
                pieceArray = new int[]{-1};
        }

        pieceArray[playerNum] = remainingPiecesNum;

    }

}
