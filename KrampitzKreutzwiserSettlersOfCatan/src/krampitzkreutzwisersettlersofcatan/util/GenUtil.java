/*
 * Lukas Krampitz
 * Aug 23, 2021
 * A GENeric utility class
 */
package krampitzkreutzwisersettlersofcatan.util;

import java.awt.Color;
import java.awt.Image;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import krampitzkreutzwisersettlersofcatan.Catan;
import krampitzkreutzwisersettlersofcatan.gui.GamePanel;
import krampitzkreutzwisersettlersofcatan.gui.SDMenuFrame;
import krampitzkreutzwisersettlersofcatan.gui.SDScaleImageResizeable;

/**
 *
 * @author Tacitor
 */
public class GenUtil {

    //Static Vars
    private static int[] remainingRoadPieces; //an the size of the number of players (plus 1 for the 0th player). Hold the number of reads that each player has left to build.
    private static int[] remainingSettlementPieces;
    private static int[] remainingCityPieces;

    public static Color BUTTON_TEXT_BROWN = new Color(57, 39, 32);

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

    /**
     * Decrement a given players count of a given game piece type. Check to make
     * sure the player has at least 1 of that type.
     *
     * @param pieceType
     * @param playerNum
     */
    public static void decrementPlayerPiece(int pieceType, int playerNum) {
        //check to make sure there are any left
        if (getRemainingPlayerPieces(pieceType, playerNum) > 0) {
            getPieceArray(pieceType)[playerNum]--;
        }
    }

    /**
     * Increment a given players count of a given game piece type.
     *
     * @param pieceType
     * @param playerNum
     */
    public static void incrementPlayerPiece(int pieceType, int playerNum) {
        //check to make sure there are some to add too
        //this check is to make sure a player is not taken out of infinite mode
        if (getRemainingPlayerPieces(pieceType, playerNum) > -1) {
            getPieceArray(pieceType)[playerNum]++;
        }

    }

    /**
     * This method is used to make other methods interoperable with deprecated
     * called from the gamePanel and calls from other classes.
     *
     * @param image
     * @param parent
     * @return
     */
    public static int interoperableGetImgWidth(Image image, JComponent parent) {

        //check if need to use gamepanel
        if (parent instanceof GamePanel) {
            return ((GamePanel) parent).getImgWidth(image);
        } else if (parent instanceof SDScaleImageResizeable) {
            return ((SDScaleImageResizeable) parent).getLocalImgWidth(image);
        } else {
            System.out.println("ERROR: interoperableGetImgWidth() in SettlerRadioBtn does not have a case for this type of JComponent");
            return 50;
        }
    }

    /**
     * This method is used to make other methods interoperable with deprecated
     * called from the gamePanel and calls from other classes.
     *
     * @param image
     * @param parent
     * @return
     */
    public static int interoperableGetImgHeight(Image image, JComponent parent) {

        //check if need to use gamepanel
        if (parent instanceof GamePanel) {
            return ((GamePanel) parent).getImgHeight(image);

        } else if (parent instanceof SDScaleImageResizeable) {
            return ((SDScaleImageResizeable) parent).getLocalImgHeight(image);
        } else {
            System.out.println("ERROR: interoperableGetImgHeight() in SettlerRadioBtn does not have a case for this type of JComponent");
            return 50;
        }
    }

    /**
     * This method is used to make other methods interoperable with deprecated
     * called from the gamePanel and calls from other classes.
     *
     * @param num
     * @param parent
     * @return
     */
    public static int interoperableScaleInt(int num, JComponent parent) {

        //check if need to use gamepanel
        if (parent instanceof GamePanel) {
            return GamePanel.scaleInt(num);

        } else if (parent instanceof SDScaleImageResizeable) {
            return ((SDScaleImageResizeable) parent).localScaleInt(num);
        } else {
            System.out.println("ERROR: interoperableScaleInt() in SettlerRadioBtn does not have a case for this type of JComponent");
            return 50;
        }
    }

    /**
     * Given a filePath load this save game to the gamePanel. Has all the common
     * functionality that is need for any SDMenuPanel that tries to load a save
     * file.
     *
     * @param sender
     * @param sDMenuFrame
     * @param filePath
     * @param offlineLoad
     */
    public static void catanSaveFileLoad(JPanel sender, SDMenuFrame sDMenuFrame, String filePath, boolean offlineLoad) {
        //test if it is a vailid save file
        try {
            File savefile = new File(filePath);
            Scanner scanner = new Scanner(savefile);

            //reset the game                            
            sDMenuFrame.getSDMainMenuPanel().getGameFrame().resetGamePanel();

            //check if it is a vailid game save
            if (!scanner.nextLine().equals("SettlersOfCatanSave" + Catan.SAVE_FILE_VER)) {
                String appendMsg = offlineLoad ? "\nA new game was started instead" : "";

                JOptionPane.showMessageDialog(null, "The selected file is not a Settlers of Catan "
                        + Catan.SAVE_FILE_VER + " save file." + appendMsg, "Loading Error", JOptionPane.ERROR_MESSAGE);
            } else { //if it is a real save file
                //check if the next line hold the player count
                if (scanner.nextLine().equals("playerCount:")) {
                    //set the player count
                    GamePanel.setPlayerCount(Integer.parseInt(scanner.nextLine()));
                    sDMenuFrame.getSDMainMenuPanel().getGameFrame().resetGamePanel();

                    sDMenuFrame.getSDMainMenuPanel().getGameFrame().loadFromFile(filePath);

                    if (!offlineLoad) {
                        //let the lobby selector to know not to reset certain Objects
                        sDMenuFrame.getSDMainMenuPanel().getSDJoinLobbyPanel().setJustMadeNewGame(true);

                        //switch over to the lobby selection. Enforce needing an empty lobby
                        sDMenuFrame.switchPanel(sender, sDMenuFrame.getSDMainMenuPanel().getSDJoinLobbyPanel());
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "The selected file does not contain the required player count data.", "Loading Error", JOptionPane.ERROR_MESSAGE);
                }
            }

            if (offlineLoad) {
                //switch back to the main menu for when ever the game terminates
                sDMenuFrame.switchPanel(sender, sDMenuFrame.getSDMainMenuPanel());
                // Hide the SDMenuFrame window and show the game
                sDMenuFrame.setVisible(false);

                //show the game                
                sDMenuFrame.getSDMainMenuPanel().getGameFrame().setVisible(true);
            }

        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, "There was an error loading the save file:\n" + e, "Loading Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}
