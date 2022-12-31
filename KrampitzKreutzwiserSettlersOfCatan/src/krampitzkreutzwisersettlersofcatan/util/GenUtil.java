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
     * Initialize the arrays for the remaining number of game pieces to the
     * number of players plus 1.
     *
     * @param playerCount
     */
    public static void initPieceArrays(int playerCount) {
        remainingRoadPieces = new int[playerCount + 1];
        remainingSettlementPieces = new int[playerCount + 1];
        remainingCityPieces = new int[playerCount + 1];
    }

    /**
     * Given a build number (or a pieceType value) return the respective array
     * for said building piece. Dec 30th 2022
     *
     * @param pieceType
     * @return
     */
    private static int[] getPieceArray(int pieceType) {

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

        return pieceArray;
    }

    /**
     * Check if a given player has at least 1 of a given game piece remaining to
     * build with. Or if infinite building mode is selected. Dec 30th 2022
     *
     * @param playerNum
     * @param pieceType
     * @return
     */
    public static boolean playerHasAPieceRemaining(int playerNum, int pieceType) {
        return (getPieceArray(pieceType)[playerNum] == -1 || getPieceArray(pieceType)[playerNum] > 0);
    }

    /**
     * Get the number of pieces a given player has of a given type. Given
     * pieceType = 1, and playerNum = 2, then the method will return the number
     * of remaining roads player 2 may build. Dec 29th 2022
     *
     * @param pieceType
     * @param playerNum
     * @return
     */
    public static int getRemainingPlayerPieces(int pieceType, int playerNum) {

        return getPieceArray(pieceType)[playerNum];

    }

    /**
     * Set the number of pieces a given player has of a given type. Given
     * pieceType = 1, and playerNum = 2, then the method will set the number of
     * remaining roads player 2 may build. Dec 29th 2022
     *
     * @param pieceType
     * @param playerNum
     * @param remainingPiecesNum
     */
    public static void setRemainingPlayerPieces(int pieceType, int playerNum, int remainingPiecesNum) {
        getPieceArray(pieceType)[playerNum] = remainingPiecesNum;

    }

    public static void decrementPlayerPiece(int pieceType, int playerNum) {
        //check to make sure there are any left
        if (getRemainingPlayerPieces(pieceType, playerNum) > 0) {
            getPieceArray(pieceType)[playerNum]--;
        }
    }

    public static void incrementPlayerPiece(int pieceType, int playerNum) {
        getPieceArray(pieceType)[playerNum]++;

    }

}
