/*
 * Lukas Krampitz and Evan Kreutzwiser
 * Nov 8, 2020
 * The JPanel that is the main part of the game. Handels drawing and logic for the game.
 */
package krampitzkreutzwisersettlersofcatan.gui;

// <editor-fold defaultstate="collapsed" desc="Imports"> 
import krampitzkreutzwisersettlersofcatan.sockets.CatanClient;
import krampitzkreutzwisersettlersofcatan.worldObjects.Port;
import krampitzkreutzwisersettlersofcatan.worldObjects.NodeRoad;
import krampitzkreutzwisersettlersofcatan.worldObjects.NodeSettlement;
import krampitzkreutzwisersettlersofcatan.worldObjects.buttons.SettlerBtn;
import krampitzkreutzwisersettlersofcatan.worldObjects.buttons.SettlerLbl;
import krampitzkreutzwisersettlersofcatan.worldObjects.buttons.SettlerComponent;
import krampitzkreutzwisersettlersofcatan.worldObjects.buttons.SettlerRadioBtn;
import krampitzkreutzwisersettlersofcatan.worldObjects.Tile;
import krampitzkreutzwisersettlersofcatan.worldObjects.WorldObject;
import Audio.AudioRef;
import dataFiles.OldCode;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Stroke;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.File;
import javax.swing.filechooser.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import krampitzkreutzwisersettlersofcatan.Catan;
import krampitzkreutzwisersettlersofcatan.util.GlobalDataRecord;
import textures.ImageRef;
import static textures.ImageRef.*;
// </editor-fold>

/**
 * @author Evan
 * @author Tacitor
 */
public class GamePanel extends javax.swing.JPanel implements MouseMotionListener {

    // <editor-fold defaultstate="collapsed" desc="Attributes"> 
    private final GameFrame superFrame; //ref to the JFrame this kept in

    private static String saveAddress; //The path to where to save to
    private static JFileChooser saveFileChooser;
    private static int userSaveSelection;

    private final ArrayList<Tile> tiles; //All the data for the tiles in one convient place
    private final ArrayList<NodeSettlement> settlementNodes; // Every settlement node of the board
    private final ArrayList<Port> ports; //every trading port, its type, location, and orientation.
    private final ArrayList<NodeRoad> roadNodes; // Every road node of the board
    private int[] tileTypes = new int[]{1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 4, 4, 0, 4, 4, 5, 5, 5}; //the type of tile from left to right, and top to bottom
    //the old deflaut order                  {1, 3, 4, 2, 2, 5, 1, 4, 3, 0, 4, 2, 4, 5, 1, 2, 3, 3, 5}
    private final int[] tileHarvestRollNums = new int[]{5, 3, 8, 6, 4, 12, 11, 10, 3, 2, 5, 9, 10, 6, 9, 11, 2, 8, 4}; //the harvest roll num of the tile from left to right, and top to bottom    
    private final int[] tileDrawOrder = new int[]{7, 3, 0, 12, 8, 4, 1, 16, 13, 9, 5, 2, 17, 14, 10, 6, 18, 15, 11}; //the order tiles are drawin in, in 3d tile mode to account fot the over lap
    private final int[][] tilePos = new int[19 * 2][2]; //the x, y position to draw the tile images
    private ArrayList<Integer> playerTurnOrder; //the oder the players go in. index 0 is always the current player and index 1 is always the next up, etc.
    private ArrayList<Integer> canStealCardPlayers; //any ints in this ArrayList are the player number of players that have a house on a hex that the thief was JUST moved to. Most of the time this is empty.
    private boolean subPlayersHaveEnoughcards; //a boolean that is true is at least one of the players in canStealCardPlayers has 1 or more card.

    private boolean inSetup; // If the game is still being set up (players placing initiale buildings)
    private int setupRoundsLeft; //the number of setup rounds left. A normal game will start with 2
    private NodeSettlement newestSetupSettlment; //the most recent settlement to be build. Used to check is road placement is valid in setup and next to the just placed house.
    private boolean inbetweenTurns; // true during the period where the game is waiting for the next player to start their turn
    private boolean thiefIsStealing; //true when any player has more than the threshold of allowed cards and must select cards to remove
    private boolean thiefJustFinished; //true if the theif had just finished stealing
    private boolean thiefJustStarted; //true if the thief was just rolled
    private boolean showRoadHitbox;
    private boolean showCardHitbox; //used for picking which cards the thief steals and for trading resource cards
    private boolean showDevCardHitbox; //used for pick which development cards the user wants to use
    private boolean[] drawCardStacks; //controls the mode cards are drawn in. Stacked or full layout
    private boolean[] drawDevCardStacks; //controls the mode dev cards are drawn in. Stacked or full layout.
    private boolean showSettlementHitbox; //toggle for whether or not the hitboxes are shown.
    private boolean showTileHitbox; //      ^^^
    private boolean showResStackHitbox;
    private boolean showSubPlayerHitbox;
    private boolean turnBtnEnabled;
    private static boolean showDevCards; //stores whether dec cards or resource cards are shown; Is static because during online play the current player should not effect the card mode on the local machine
    private boolean userPlayedDevCard; //boolean to store whether or not the current player has used a dev card yet this round
    private int tradingMode; //the gamestate regarding trading. 0 for no trade, 1 for a 4:1, 2 for a 3:1, and 3 for a 2:1.
    private int tradeResource; //the resource type number that the player wants to trade to, 0 for none.
    private int minTradeCardsNeeded; //the number of cards needed for that tradingMode //the minimin amount of cards needed of one type to make a trade
    private boolean[][] playerHasPort; //main array is for players, index 0 is not used, index 1-4 are the player numbers. sub arrays are of length 6. inxed 0 for 3:1 port, indexes 1-5 for 2:1 of that type
    private int[] numCardType; //the number of cards the current player has of each type. valid indexes are 0-5, but 0 never contains useful data.
    private int[] cardTypeCount; //the count of how many cards of each type the current player has valid inxies are 0-4
    private int[] devCardTypeCount; //the count of how many dev cards of each type the current player has valid inxies are 0-4
    private int[] setupTurnOrder; //the order players take their turn during the setup phase
    private int setupTurnOrderIndex; //the position the game is currently on in the setupTurnOrder array 
    private int currentPlayer; // The player currently taking their turn
    private int playerRolled7; //the player who most recently rolled a seven
    private static int playerCount = 2; // The number of players in the game
    private static boolean giveStartingResources = true; // If players get startup resources
    private static boolean doSnakeRules = true; //make the setup phase of the game more fair follow normal order fist setup round, then reverse
    private static boolean showMenuBoarder = false; //will a boarder be drawn aorund the menus
    private static CatanClient onlineClient = null; //if this game is in online mode this is the client that talks to the server. Else it's null
    private static int onlineMode = -1; //if the user is just playing a local game this is -1. If they are in an online game this is the player ID of the player they control
    private final ArrayList<Integer> cards[]; // Holds each player's list of cards in an ArrayList
    private final ArrayList<Integer>[] devCards; //an Array of ArrayLists. Each player gets their own ArrayList for the dev cards they have.
    // ^^^ valid number are: 1 (knight), 2 (progress card road building), 3 (progress card monolpoy), 4 (progress card year of pleanty), 5, 6, 7, 8, 9 (5-9 are vp cards)
    private final ArrayList<Integer> availableDevCards; //a list of dev cards that are still in a pile and have not been drawn. 
    // ^^^ This ensures that the type distrobution is correct and also ensures that there will be a finite number of dev cards
    private ArrayList<NodeSettlement>[] portSettlements; //Contains settlments that are on ports. One arrayList for each of the 9 ports.
    private final int victoryPoints[];
    private final int totalCardsCollected[];
    private final int[] cardStackXPositions; //the x positions to draw cards when in stacked mode
    private final int[] devCardStackXPositions; //the x positions to draw dev cards when in stacked mode
    private final int[] resourceStackXPositions; //the x coords for the resource type stacks
    private int[] stealCardNum; //the number of cards to steal from each player
    private int cardStartPosition; //the xPos to start drawing cards at 
    private int devCardStartPosition; //the xPos to start drawing dev cards at 
    private int victoryPointsToWin;
    private int thiefMoveCounter;
    private int tileWithThief; // The index of the tile with the thief
    private int buildingObject; // Indicates if/what the user is building. 
    // 0 when not placing anything, 1 for roads, 2 for settlements, and 3 for upgrading
    private int usingDevCard; //Indecates if/what dev card the user is playing
    //-1 for no dev card, 0 for when the game is waiting for the player to select a dev card and 1-4 for the specifc card being played

    private static int harvestRollNumOffset; //the number of pixels the harvest roll is ofset from. This allows both single and double diget number to be centered

    private int roadWidth; //used in finding the hitbox
    private int roadHeight;
    private int playerSetupRoadsLeft; //number of roads to place
    private int playerSetupSettlementLeft; //number of settlements to place

    //var used for scaling
    private final int tileYOffset;
    private final int tileXOffset;
    public static double scaleFactor;
    private final int newTileWidth;
    private final int newTileHeight;
    private final int threeDTileOffset;
    private static int frameWidth; //the dimentions of the JFrame holding the gamePanel
    private static int frameHeight;

    //new dice roll lable
    private String[] diceRollVal;

    //Object containing the data about the longest road
    private GlobalDataRecord longestRoadData;
    private ArrayList<NodeRoad> alreadyCheckedRoad; //ArrayList containing roads that have already been check for logest road. Prevents infinit feedback loop.
    private ArrayList<NodeSettlement> alreadyCheckedSettlements;
    private int[] playerLongestRoadSegments; //the length of the longest road segment each player owns

    //Object for Largest Army
    private GlobalDataRecord largestArmyData;
    private int[] playerArmySize; //the size of each player's army

    //custom buttons
    //dev cards
    private SettlerBtn toggleCardBtn;
    private SettlerBtn buyDevCardBtn;
    private SettlerBtn useDevCardBtn;
    //turn switch
    private SettlerBtn turnSwitchBtn;
    //trade
    private SettlerBtn trade4to1Btn;
    private SettlerBtn trade3to1Btn;
    private SettlerBtn trade2to1Btn;
    private SettlerBtn tradeDomestic;
    //util buttons
    private SettlerBtn buildBtn;
    private SettlerBtn backNoSaveBtn;
    private SettlerBtn backBtn;

    //array of buttons for easy access
    private SettlerBtn[] settlerBtns;

    //custom lables
    //instructions
    private SettlerLbl instructionLbl;
    private SettlerLbl subInstructionLbl;
    private SettlerLbl instructionPromptLbl;
    //menu headers
    private SettlerLbl devCardMenuLbl;
    private SettlerLbl tradeMenuLbl;
    private SettlerLbl buildMenuLbl;
    //title
    private SettlerLbl titleLbl;
    //array of labels
    private SettlerLbl[] settlerLbls;

    //custom radio buttons
    private SettlerRadioBtn buildRoadRBtn;
    private SettlerRadioBtn buildSettlementSRBtn;
    private SettlerRadioBtn buildSettlementLRBtn;

    //array of the radio buttons
    private SettlerRadioBtn[] settlerRadioBtns;

    //array of Settler Componets
    private SettlerComponent[] settlerComponents;

    //fonts
    private final Font timesNewRoman;

    //mouse motion listener vars
    private int mouseMotionPosX; //acording to the MouseMotionListener where is the mouse located
    private int mouseMotionPosY;

    //tab selected Settler button
    private int tabSelectedButton; //the int of the index of the button that is tab selected    

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Creates new form NewGamePanel
     *
     * @param frame ref to the frame
     */
    public GamePanel(GameFrame frame) {

        // Initalize variables
        superFrame = frame; //save refernce
        frameWidth = superFrame.getWidth(); //save the dimentions to variabled eaisly accesed by other classes
        frameHeight = superFrame.getHeight();
        tiles = new ArrayList(); //init the master tile array list
        settlementNodes = new ArrayList(); // Init the settlement node array list
        ports = new ArrayList();
        roadNodes = new ArrayList(); // Init the road node array list
        inSetup = true;
        setupRoundsLeft = 2; //start up with two setup rounds
        newestSetupSettlment = null; //there has not been a settlement build yet
        inbetweenTurns = false;
        thiefIsStealing = false;
        thiefJustFinished = false;
        thiefJustStarted = false;
        currentPlayer = 1; // Player 1 starts
        setupTurnOrderIndex = 1; //set the "cursor" to the second position because player 1 was already set to the current player
        playerRolled7 = 0; //no player has rolled a 7 yet. So set the null player 0 to start
        cards = new ArrayList[playerCount + 1]; // Create the array of card lists
        // the +1 allows methods to use player IDs directly without subtracting 1
        devCards = new ArrayList[playerCount + 1]; // Create the array of develoment card lists
        // the +1 allows methods to use player IDs directly without subtracting 1
        victoryPoints = new int[playerCount + 1]; // Create the array of player's victory points
        // the +1 allows methods to use player IDs directly without subtracting 1
        playerLongestRoadSegments = new int[playerCount + 1];
        playerArmySize = new int[playerCount + 1];
        stealCardNum = new int[playerCount + 1]; //create the array of how many cards need to be stolen
        //the +1 allows methods to use player IDs directly without subtracting 1
        drawCardStacks = new boolean[playerCount + 1];//create the array of how to draw the cards for each player
        //the +1 allows methods to use player IDs directly without subtracting 1
        drawDevCardStacks = new boolean[playerCount + 1];//create the array of how to draw the dev cards for each player
        //the +1 allows methods to use player IDs directly without subtracting 1
        availableDevCards = new ArrayList<>(); //init the array for dev cards that will exist
        playerHasPort = new boolean[playerCount + 1][6]; //create the array keeping track of what player has acces to what ports
        //the +1 allows methods to use player IDs directly without subtracting 1
        //the 6 is for the six types of ports ^
        totalCardsCollected = new int[5];
        setupTurnOrder = new int[playerCount * setupRoundsLeft]; //accounds for all the players for every setup round there will be
        //calculate the positions to draw the cards bassed off of the water ring. One on each end, one in the middle and one at each quarter way point
        cardStackXPositions = new int[]{superFrame.getWidth() / 2 - getImgWidth(WATER_RING) / 2 - getImgWidth(CARD_CLAY) / 2,
            superFrame.getWidth() / 2 - getImgWidth(WATER_RING) / 4 - getImgWidth(CARD_CLAY) / 2,
            superFrame.getWidth() / 2 - getImgWidth(CARD_CLAY) / 2,
            superFrame.getWidth() / 2 + getImgWidth(WATER_RING) / 4 - getImgWidth(CARD_CLAY) / 2,
            superFrame.getWidth() / 2 + getImgWidth(WATER_RING) / 2 - getImgWidth(CARD_CLAY) / 2};
        //and now the dev cards
        devCardStackXPositions = new int[]{superFrame.getWidth() / 2 - getImgWidth(WATER_RING) / 2 - getImgWidth(DEV_CARD_KNIGHT) / 2,
            superFrame.getWidth() / 2 - getImgWidth(WATER_RING) / 4 - getImgWidth(DEV_CARD_KNIGHT) / 2,
            superFrame.getWidth() / 2 - getImgWidth(DEV_CARD_KNIGHT) / 2,
            superFrame.getWidth() / 2 + getImgWidth(WATER_RING) / 4 - getImgWidth(DEV_CARD_KNIGHT) / 2,
            superFrame.getWidth() / 2 + getImgWidth(WATER_RING) / 2 - getImgWidth(DEV_CARD_KNIGHT) / 2};
        //and simularly the resouce stacks
        resourceStackXPositions = new int[]{0, //have the first index be a position of 0
            superFrame.getWidth() / 2 - getImgWidth(WATER_RING) / 2,
            superFrame.getWidth() / 2 - getImgWidth(WATER_RING) / 4 - getImgWidth(RES_STACKS[2]) / 4,
            superFrame.getWidth() / 2 - getImgWidth(RES_STACKS[3]) / 2,
            superFrame.getWidth() / 2 + getImgWidth(WATER_RING) / 4 - ((getImgWidth(RES_STACKS[4]) / 4) * 3),
            superFrame.getWidth() / 2 + getImgWidth(WATER_RING) / 2 - getImgWidth(RES_STACKS[5])};

        buildingObject = 0;
        usingDevCard = -1; //set it to normal value for when no dev card is being used
        showRoadHitbox = false;
        showSettlementHitbox = false;
        showCardHitbox = false;
        showTileHitbox = false;
        showResStackHitbox = false;
        showSubPlayerHitbox = false;
        turnBtnEnabled = true;
        showDevCards = false;
        showDevCardHitbox = false;
        userPlayedDevCard = false;
        tradingMode = 0;
        tradeResource = 0;
        playerSetupRoadsLeft = 1;
        playerSetupSettlementLeft = 1;
        victoryPointsToWin = 10;
        thiefMoveCounter = 0;

        //init the longestRoadData
        longestRoadData = new GlobalDataRecord();
        //init the ArrayList holding roads that have already been checked for longest road
        alreadyCheckedRoad = new ArrayList<>();
        alreadyCheckedSettlements = new ArrayList<>();
        largestArmyData = new GlobalDataRecord();

        //init the playerTurnOrder
        initPlayerTurnOrder();

        //init the canStealCardPlayers
        canStealCardPlayers = new ArrayList<>();

        //set a default save path
        saveAddress = System.getProperty("user.home") + "\\Desktop\\SettlersOfCatan.catan";
        //initialize the filechooser
        saveFileChooser = new JFileChooser();
        //create a filter for catan save files
        FileFilter catanSaveFile = new FileFilter() {
            //add the description
            @Override
            public String getDescription() {
                return "Catan Save File (*.catan)";
            }

            //add the logic for the filter
            @Override
            public boolean accept(File f) {
                //if it's a directory ignor it
                if (f.isDirectory()) {
                    return true;
                } else { //if it's a file only show it if it's a .catan file
                    return f.getName().toLowerCase().endsWith(".catan");
                }
            }
        };
        //add the filter
        saveFileChooser.addChoosableFileFilter(catanSaveFile);
        //set the new filter as the default
        saveFileChooser.setFileFilter(catanSaveFile);
        //set the name of the window
        saveFileChooser.setDialogTitle("Select a file to save Settlers of Catan to:");

        // Fill the list of card ArrayLists with new ArrayLists and intialize
        // the victory point array (Both are the same size and can share a loop)
        // and drawCardStacks to set up all to draw the full layout
        for (int i = 0; i < cards.length; i++) {
            cards[i] = new ArrayList(); // Cards ArrayList
            devCards[i] = new ArrayList<>(); //dev cards arrayList
            victoryPoints[i] = 0; // Victory point counter
            drawCardStacks[i] = false;
            drawDevCardStacks[i] = false;
            playerLongestRoadSegments[i] = 0; //that players longest road segment
            playerArmySize[i] = 0;
        }

        //populate the ArrayList containing all remaining dev cards. As the game is in startup fully populate it
        //add 25 cards
        for (int i = 1; i < 26; i++) {
            //add 14 knights
            if (i <= 14) {
                availableDevCards.add(1);
            } else if (i <= 16) { //add two road building cards
                availableDevCards.add(2);
            } else if (i <= 18) { //add two monopoly cards
                availableDevCards.add(3);
            } else if (i <= 20) { //add two year of pleanty cards
                availableDevCards.add(4);
            } else if (i == 21) { //add one vp market card
                availableDevCards.add(5);
            } else if (i == 22) { //add one vp university card
                availableDevCards.add(6);
            } else if (i == 23) { //add one vp great hall card
                availableDevCards.add(7);
            } else if (i == 24) { //add one vp chapel card
                availableDevCards.add(8);
            } else if (i == 25) { //add one vp library card
                availableDevCards.add(9);
            }
        }

        //fill the playerHasPort 2D array with false for all players ecxept 0 and fill all the ports they have with falses because noone has ports yet
        //skip player 0
        for (int i = 1; i < playerHasPort.length; i++) {
            //go through the sub arrays and fill them with six falses
            for (int j = 0; j < playerHasPort[i].length; j++) {
                playerHasPort[i][j] = false;
            }
        }

        // Intialize the card counter array
        for (int i = 0; i < totalCardsCollected.length; i++) {
            totalCardsCollected[i] = 0; // Victory point counter
        }

        //Fill the setupOrder Array with the correct order players go in
        for (int i = 0; i < (setupRoundsLeft); i++) { //loop through each setup round

            //loop through all the players for only that round
            for (int j = 0; j < playerCount; j++) {
                //check if its a reverse round
                if (doSnakeRules && i % 2 == 1) {
                    setupTurnOrder[i * playerCount + j] = playerCount - j; //revese round (4,3,2,1)
                } else {
                    setupTurnOrder[i * playerCount + j] = j + 1; //regular round (1,2,3,4)
                }
            }

        }

        // Initialize the window and board
        initSettlerLbl();

        //randomize the board
        randomizeTiles();

        loadTilePos(); //read in the coodinates of where each of the 19 tiles goes
        loadTiles(); //load the ArrayList of tiles with position and type data
        loadNodes(); // Create and link all of the board's settlement and road nodes
        loadPorts(); //read in all the data about the ports and populate the Array List with Ports

        //port supporting vars
        portSettlements = new ArrayList[ports.size()]; //create the same amount of ArrayLists as there are ports

        //fill the array of ports settlements with new arrayLists
        for (int i = 0; i < portSettlements.length; i++) {
            portSettlements[i] = new ArrayList<>();
        }

        generatePortSettlements(); //Get a list of the settlements that allow access to each port

        //add a mouse motion listener for hovering over button
        addMouseMotionListener(this);

        // Add the mouse listener that calls the mouse click event handler
        addMouseListener(new MouseAdapter() {
            /**
             * Triggered when the user clicks on the game panel. Calls the game
             * panel's click event handler.
             *
             * @param evt The event representing the mouse click
             */
            @Override
            public final void mouseReleased(MouseEvent evt) {
                // Send the mouse event over to the game panel's click handlers
                mouseClick(evt);
            }
        });

        //the dimentions of a hex Tile after scaling. Saves making this calculation over and over again
        newTileWidth = getImgWidth(tiles.get(0).getImage());
        newTileHeight = getImgHeight(tiles.get(0).getImage());

        //set up the scaling vars
        //a constant offset used to correct the position of the tiles in cases where the height of the display is larger than the width
        //first get a centered frame of reference
        tileYOffset = (int) ((superFrame.getHeight() / 2 - getImgHeight(WATER_RING_OVERLAY) / 2
                //then account for the tile height
                + newTileHeight)
                //then subtract the current position to find the difference
                - getTileYPos(tiles.get(7).getYPos())
                //add some fine tuning for smaller resulutions
                + (superFrame.getHeight() / (float) superFrame.getWidth()));

        //same as the y offset but now for the x
        tileXOffset = (superFrame.getWidth() / 2 - getImgWidth(WATER_RING_OVERLAY) / 2
                + (int) (newTileWidth - (newTileWidth / 4))) //get the distance from the left most vertex to the center recangle of the hexagon
                - getTileXPos(tiles.get(0).getXPos());

        //set up the circle scaler
        if (superFrame.getWidth() <= superFrame.getHeight()) {
            scaleFactor = (1920.0 / superFrame.getWidth());
        } else {
            scaleFactor = (1080.0 / superFrame.getHeight());
        }

        //apply the scaling to the tiles
        scaleWorldObjectPos(tiles, 1);
        scaleWorldObjectPos(roadNodes, 1);
        scaleWorldObjectPos(settlementNodes, 0);
        scaleWorldObjectPos(ports, 0);
        updatePortPos();

        //get the fonts
        timesNewRoman = new Font("Times New Roman", Font.PLAIN, 18);

        //setup the SettlerBtns
        toggleCardBtn = new SettlerBtn(false, 0, 0); //cannot give a position yet because they need to be below the Swing buttons
        buyDevCardBtn = new SettlerBtn(false, 0, 1); //but as of right here the Swing btns do not have coords.
        useDevCardBtn = new SettlerBtn(false, 0, 2); //play a dev card and use it's abilities
        //turn btn
        turnSwitchBtn = new SettlerBtn(true, 0, 3);
        //trade buttons as of here (down) have mode 0 as cancel. Buttons above ^^^ have the last mode as cancel (usually)
        trade4to1Btn = new SettlerBtn(false, 1, 4); //trade at a 4:1 cost
        trade3to1Btn = new SettlerBtn(false, 1, 5); //trade at a 3:1 cost
        trade2to1Btn = new SettlerBtn(false, 1, 6); //trade at a 2:1 cost
        tradeDomestic = new SettlerBtn(false, 1, 7); //trade domestically with another player
        //util
        buildBtn = new SettlerBtn(false, 1, 8); //the build button
        backNoSaveBtn = new SettlerBtn(true, 1, 9); //the exit button and not saving
        backBtn = new SettlerBtn(true, 1, 10); //the exit button for saving

        //setup up the radio buttons
        buildRoadRBtn = new SettlerRadioBtn(true, false, 0); //road
        buildSettlementSRBtn = new SettlerRadioBtn(true, false, 1); //settlement
        buildSettlementLRBtn = new SettlerRadioBtn(true, false, 2); //city

        //setup the button array
        settlerBtns = new SettlerBtn[]{turnSwitchBtn, buildBtn, trade4to1Btn, trade3to1Btn, trade2to1Btn, tradeDomestic, toggleCardBtn, buyDevCardBtn, useDevCardBtn, backNoSaveBtn, backBtn};
        //setup label array
        settlerLbls = new SettlerLbl[]{titleLbl, instructionLbl, subInstructionLbl, instructionPromptLbl, buildMenuLbl, tradeMenuLbl, devCardMenuLbl};
        //setup the radio button array
        settlerRadioBtns = new SettlerRadioBtn[]{buildRoadRBtn, buildSettlementSRBtn, buildSettlementLRBtn};

        //set up the full array of both buttons and radio buttons
        settlerComponents = new SettlerComponent[settlerBtns.length + settlerRadioBtns.length - 2]; //-2 because the last 2 exit buttons should not be tabbable
        populateSettlerComponents();
        //set the first button as the default tab selected
        tabSelectedButton = 0;
        settlerComponents[tabSelectedButton].setTabSelected(true);

        //setup the custom radio buttons to go into the groups
        SettlerRadioBtn.setUpGroup(settlerRadioBtns);

        buildMenuLbl.setFont(new Font(timesNewRoman.getName(), timesNewRoman.getStyle(), (int) (timesNewRoman.getSize() / scaleFactor)));
        tradeMenuLbl.setFont(new Font(timesNewRoman.getName(), timesNewRoman.getStyle(), (int) (timesNewRoman.getSize() / scaleFactor)));
        devCardMenuLbl.setFont(new Font(timesNewRoman.getName(), timesNewRoman.getStyle(), (int) (timesNewRoman.getSize() / scaleFactor)));

        instructionPromptLbl.setFont(new Font(timesNewRoman.getName(), timesNewRoman.getStyle(), (int) ((timesNewRoman.getSize() + 5) / scaleFactor)));
        instructionLbl.setFont(new Font(timesNewRoman.getName(), timesNewRoman.getStyle(), (int) ((timesNewRoman.getSize() + 5) / scaleFactor)));
        subInstructionLbl.setFont(new Font(timesNewRoman.getName(), timesNewRoman.getStyle(), (int) ((timesNewRoman.getSize() + 1) / scaleFactor)));

        titleLbl.setFont(new Font(timesNewRoman.getName(), Font.BOLD, (int) ((40) / scaleFactor)));

        //initialize the dice roll value
        diceRollVal = new String[]{"0", "0", ""}; //the first two indexies are the rollecd values and the third is the sum

        //init the offset for the "3d" overlap tiles
        threeDTileOffset = (int) (-20 / scaleFactor);

        // Set the state of the builds buttons for the first player
        updateBuildButtons();

    }
    // </editor-fold>

    /**
     * Setup the custom JLable replacements
     */
    private void initSettlerLbl() {
        //setup main label
        instructionLbl = new SettlerLbl();
        //set up sub label
        subInstructionLbl = new SettlerLbl();
        //set up the prompt
        instructionPromptLbl = new SettlerLbl("Instructions:");

        devCardMenuLbl = new SettlerLbl("Development Card Menu:");
        tradeMenuLbl = new SettlerLbl("Trade Menu:");
        buildMenuLbl = new SettlerLbl("Build Menu:");
        titleLbl = new SettlerLbl("Settlers of Catan");

        //set up the colours
        instructionLbl.setForeground(new Color(255, 255, 225));
        subInstructionLbl.setForeground(new Color(255, 255, 225));
        instructionPromptLbl.setForeground(new Color(255, 255, 225));

        devCardMenuLbl.setForeground(new Color(255, 255, 225));
        tradeMenuLbl.setForeground(new Color(255, 255, 225));
        buildMenuLbl.setForeground(new Color(255, 255, 225));
        titleLbl.setForeground(new Color(255, 255, 225));

        //set the instructions to do line wrap
        instructionLbl.setLineWrap(true);
        subInstructionLbl.setLineWrap(true);
    }

    /**
     * Update the coordinates for the custom labels and buttons
     */
    private void settlerVarPos(Graphics2D g2d) {
        //calc the number of lines for the labels that will be multi line
        for (SettlerLbl lbl : settlerLbls) {
            if (lbl.getLineWrap()) {
                lbl.calcNumLines(g2d, this);
            }
        }

        //most buttons and labels aligned to the top
        //set the title
        titleLbl.setXPos(scaleInt(10));
        titleLbl.setYPos(scaleInt(35));
        //set the turn button to the correct location
        turnSwitchBtn.setXPos(titleLbl.getXPos());
        turnSwitchBtn.setYPos(titleLbl.getYPos() + scaleInt(15));

        instructionPromptLbl.setXPos(titleLbl.getXPos());
        instructionPromptLbl.setYPos(turnSwitchBtn.getYPos() + getImgHeight(turnSwitchBtn.getBaseImage()) + scaleInt(30));

        //get the length of the instruction prompt
        g2d.setFont(instructionPromptLbl.getFont());
        int stringWidth = g2d.getFontMetrics().stringWidth(instructionPromptLbl.getText());

        instructionLbl.setXPos(instructionPromptLbl.getXPos() + stringWidth + scaleInt(5));
        instructionLbl.setYPos(instructionPromptLbl.getYPos());

        subInstructionLbl.setXPos(instructionLbl.getXPos());
        subInstructionLbl.setYPos(instructionLbl.getYPos() + (scaleInt(22) * instructionLbl.getNumLines()));

        buildMenuLbl.setXPos(turnSwitchBtn.getXPos());
        buildMenuLbl.setYPos((int) (scaleInt(225) * ((1920.0 / 1080.0) - ((double) frameWidth / (double) frameHeight) + 1.0)));

        //set the radio build buttons
        buildRoadRBtn.setXPos(turnSwitchBtn.getXPos());
        buildRoadRBtn.setYPos(buildMenuLbl.getYPos() + scaleInt(10));

        //set the radio build buttons
        buildSettlementSRBtn.setXPos(turnSwitchBtn.getXPos());
        buildSettlementSRBtn.setYPos(buildRoadRBtn.getYPos() + getImgHeight(buildRoadRBtn.getBaseImage()) + scaleInt(6));

        //set the radio build buttons
        buildSettlementLRBtn.setXPos(turnSwitchBtn.getXPos());
        buildSettlementLRBtn.setYPos(buildSettlementSRBtn.getYPos() + getImgHeight(buildSettlementSRBtn.getBaseImage()) + scaleInt(6));

        //set the build button
        buildBtn.setXPos(turnSwitchBtn.getXPos());
        buildBtn.setYPos(buildSettlementLRBtn.getYPos() + getImgHeight(buildSettlementLRBtn.getBaseImage()) + scaleInt(6));

        tradeMenuLbl.setXPos(instructionPromptLbl.getXPos());
        tradeMenuLbl.setYPos(buildBtn.getYPos() + getImgHeight(buildBtn.getBaseImage()) + scaleInt(30));

        //set the trade buttons to the correct location
        trade4to1Btn.setXPos(tradeMenuLbl.getXPos());
        trade4to1Btn.setYPos(tradeMenuLbl.getYPos() + scaleInt(10));

        trade3to1Btn.setXPos(trade4to1Btn.getXPos() + getImgWidth(trade4to1Btn.getBaseImage()) + scaleInt(6));
        trade3to1Btn.setYPos(trade4to1Btn.getYPos());

        trade2to1Btn.setXPos(tradeMenuLbl.getXPos());
        trade2to1Btn.setYPos(trade4to1Btn.getYPos() + getImgHeight(trade4to1Btn.getBaseImage()) + scaleInt(6));

        tradeDomestic.setXPos(trade2to1Btn.getXPos() + getImgWidth(trade2to1Btn.getBaseImage()) + scaleInt(6));
        tradeDomestic.setYPos(trade2to1Btn.getYPos());

        devCardMenuLbl.setXPos(instructionPromptLbl.getXPos());
        devCardMenuLbl.setYPos(trade2to1Btn.getYPos() + getImgHeight(trade2to1Btn.getBaseImage()) + scaleInt(30));

        toggleCardBtn.setXPos(trade2to1Btn.getXPos());
        toggleCardBtn.setYPos(devCardMenuLbl.getYPos() + scaleInt(10));

        buyDevCardBtn.setXPos(toggleCardBtn.getXPos());
        buyDevCardBtn.setYPos((int) (toggleCardBtn.getYPos() + getImgHeight(toggleCardBtn.getBaseImage()) + (6 / scaleFactor)));

        useDevCardBtn.setXPos(toggleCardBtn.getXPos());
        useDevCardBtn.setYPos((int) (buyDevCardBtn.getYPos() + getImgHeight(buyDevCardBtn.getBaseImage()) + (6 / scaleFactor)));

        //the exit buttons aligned to the bottom
        backBtn.setXPos(turnSwitchBtn.getXPos());
        backBtn.setYPos(frameHeight - getImgHeight(backBtn.getBaseImage()) - scaleInt(6));

        backNoSaveBtn.setXPos(turnSwitchBtn.getXPos());
        backNoSaveBtn.setYPos(backBtn.getYPos() - getImgHeight(backNoSaveBtn.getBaseImage()) - scaleInt(6));
    }

    // <editor-fold defaultstate="collapsed" desc="Clicking and Mouse Detection"> 
    /**
     * Action to preform when a user wants to save and exit the game
     */
    private void backBtnClicked() {

        //get the location to save to
        userSaveSelection = saveFileChooser.showSaveDialog(this);

        //set the selected address
        if (userSaveSelection == JFileChooser.APPROVE_OPTION) {
            //store the file path the user selected            
            saveAddress = saveFileChooser.getSelectedFile().getPath();

            //append .catan if the user forgot to
            //first get where the .catan can be found in the String
            int catanExt = saveAddress.indexOf(".catan");

            //check if it is there at all
            if (catanExt == -1) {
                //since it is no where in the string at all append .catan
                saveAddress += ".catan";
            } else { //if there if .catan ANYwhere in the path check to see if it the actual extension
                //if it is not
                if (!(saveAddress.substring(saveAddress.length() - 6).equals(".catan"))) {
                    saveAddress += ".catan"; //add the file type
                }
            }

            //System.out.println(saveAddress);
            //save the game and only close if it is successful
            if (save()) {

                //Preform the opperations needed when leaving an online game
                networkingCloseOpertations();

                // Hide this window and show the main menu
                superFrame.getMainMenu().setVisible(true); //show the main menu
                superFrame.setVisible(false); //hide the parent frame 
            }
        }

    }

    /**
     * What to do when a player clicks the build button
     */
    private void buildBtnClicked() {
        // If a turn is in progress
        if (!inbetweenTurns) {
            //check to make sure there isn't already another building trying to be made
            if (buildingObject != 0) {
                cancelBuilding();
                // Redraw the board to remove hitbox outlines
                repaint();
                // Dont pick a new building to place
                return;
            }
            //Update the vars
            if (buildRoadRBtn.isSelected()) {
                buildingObject = 1;

                //check what mode it's in
                if (inSetup) {
                    // If the player has more roads to place
                    if (playerSetupRoadsLeft > 0) {
                        // Show the building hitboxes and redraw the baord to render them
                        showRoadHitbox = true;
                        repaint();
                    } else {
                        instructionLbl.setText("You're all done placing your setup roads. There are none left.");
                        subInstructionLbl.setText("");
                    }
                } else { // If the real game is in progress
                    // Show the road hitboxes
                    showRoadHitbox = true;
                    repaint();
                }

            } else if (buildSettlementSRBtn.isSelected()) {
                buildingObject = 2;

                //check the mode
                if (inSetup) {
                    // If the player has more settlements to place
                    if (playerSetupSettlementLeft > 0) {
                        // Show the building hitboxes and redraw the baord to render them
                        showSettlementHitbox = true;
                        repaint();
                    } else {
                        instructionLbl.setText("You're all done placing your setup settlements. There are none left.");
                        subInstructionLbl.setText("");
                        buildingObject = 0; // Dont build
                    }
                } else { // If the real game is in progress
                    // Show the settlement hitboxes
                    showSettlementHitbox = true;
                    repaint();
                }

            } else if (buildSettlementLRBtn.isSelected()) {
                buildingObject = 3;
                //make sure you're not in setup mode
                if (inSetup) {
                    instructionLbl.setText("Sorry you don't have any cities to place.");
                    subInstructionLbl.setText("You do still have " + playerSetupSettlementLeft + " settlement(s) left");
                    buildingObject = 0; // Dont build
                } else { // If the real game is in progress
                    // Show the settlement hitboxes
                    showSettlementHitbox = true;
                    repaint();
                }

            } else {
                buildingObject = -1;
                System.out.println("An error has occoured while building");
            }
            // Change the build button to a cancel button
            buildBtn.setMode(0);//set it to "Cancel"
        }
    }

    /**
     * End/start turn button click handling, for turn switching
     *
     */
    private void turnSwitchBtnClicked() {

        updateBuildButtons();
        repaint();

        // If the game is waiting to start the next player's turn
        if (inbetweenTurns) {

            // Change the button back to the End Turn button
            setTurnBtbTextEnd();

            // Begin the next turn
            inbetweenTurns = false; // No longer waiting to start a turn

            //update the instructions
            if (inSetup) {
                instructionLbl.setText("Place two roads and two settlements each to start. Only one of each per setup round.");
                subInstructionLbl.setText("Select a type, click build, and then click where it shoud go.");
            } else if (thiefIsStealing) { //check if the current mode is stealing cards

                //no longer in steal start mode because the lable would have already been updated
                if (thiefJustStarted) {
                    thiefJustStarted = false;
                }

                //check if the current player needs to be stolen from
                if (stealCardNum[currentPlayer] > 0) {

                    //hilight the cards so the player know they can click there
                    showCardHitbox = true;
                    repaint();

                    //disable the turn switch button so that players must give away cards until the quota is met
                    turnBtnEnabled = false;

                }

                repaint();

                int theifLeftToRob = 0; //how many players need to get robbed still

                //count how many still need to get robed
                for (int i = 0; i < stealCardNum.length; i++) {
                    if (stealCardNum[i] > 0) {
                        theifLeftToRob++;
                    }
                }

                //check if the thief is done stealing
                if (theifLeftToRob == 0) {
                    //set the vars
                    thiefIsStealing = false;
                    thiefJustFinished = true;
                    //set the player to the one that rolled the seven. That way when the turn end button 
                    //is clicked next time the player to roll the dice is the correct one (an not the one that did last (but only sometimes))
                    currentPlayer = playerRolled7;

                    //update the instructions for players that have valid card combos for building
                    instructionLbl.setText("The thief is done stealing");
                    subInstructionLbl.setText("You may resume regular play");

                    //now that the thief is done stealing check if the playerRolled7 can steal cards
                    //System.out.println(canStealCardPlayers.size());
                    if (canStealCardPlayers.size() > 0) {
                        //does atleast one of the targetable sub players have more than one card to steal
                        subPlayersHaveEnoughcards = false; //checks for this so that the game does not softlock when the current player need to choose but has 0 options

                        for (int i = 0; i < canStealCardPlayers.size(); i++) {
                            if (cards[canStealCardPlayers.get(i)].size() > 0) {
                                subPlayersHaveEnoughcards = true;
                            }
                        }

                        //if the playerRolled7 can steal
                        if (subPlayersHaveEnoughcards) {

                            //disable the turn switch button until the player has selected another player to steal from
                            turnBtnEnabled = false;

                            //update the lables for when the player can build
                            instructionLbl.setText("The thief is done stealing. But you are not!");
                            subInstructionLbl.setText("Select one of your fellow players to take one of their cards at random. got $$");
                        } else {
                            //if none of the sub players have any cards clear the canStealCardPlayers
                            canStealCardPlayers.clear();
                        }
                    }

                }
            } else { // If a turn of the real game is starting (not setup)

                // Roll the dice and display the rolled number to the user
                diceRoll();
                // The dice roll function calls the material collection method to
                // Ensure that all players get the materials they earned from the roll

                // Set the instruction labels to tell the user they can build
                instructionLbl.setText("Use your cards to build roads or settlements");
                subInstructionLbl.setText("Or end your turn to continue the game");

            }

            // Update the build buttons to reflect the current player's cards
            // (Or remaining setup buildings)
            updateBuildButtons();

            // Redraw the board to the next player can see their cards
            repaint();

            //if the thief had just finished set it false, they are done now
            thiefJustFinished = false;
        } else if (playerSetupRoadsLeft == 0 && playerSetupSettlementLeft == 0) { // If the end turn button was clicked
            //set the roll sum to 0. This is for the dice images. When the sum is "" then the blank dice are shown
            diceRollVal[2] = "";

            //reset the colour
            instructionLbl.setForeground(new java.awt.Color(255, 255, 225));
            //reset the font
            instructionLbl.setFont(new Font(timesNewRoman.getName(), timesNewRoman.getStyle(), (int) ((timesNewRoman.getSize() + 5) / scaleFactor)));

            // And the user is done placing setup buildinga
            // Check if the player has enough points to win
            checkForWin();

            // Now the game is waiting to start the next turn
            inbetweenTurns = true;

            //select the next player
            //if it is a normal round and the thief is not stealing use the regular turn progression
            if (!thiefIsStealing) {
                // Select the next player
                nextPlayer();
            } else { //if the thief is infact stealing
                //make sure they still have stealing to do
                int thiefLeftToRob = 0; //how many players need to get robbed still

                //count how many still need to get robed
                for (int i = 0; i < stealCardNum.length; i++) {
                    if (stealCardNum[i] > 0) {
                        thiefLeftToRob++;
                    }
                }

                //if there is still robbing to be done
                if (thiefLeftToRob > 0) {
                    //skip any player that is not personally getting robbed
                    while (stealCardNum[currentPlayer] <= 0) {
                        //debug turn skipping
                        //System.out.println("skipped player: " + currentPlayer);
                        nextPlayer();
                    }
                } else { //if stealing is going on but there is noone left to rob set the player who rolled the 7 as the current player
                    currentPlayer = playerRolled7;
                }
            }

            // If the game is still in setup, give the next player roads to place
            //set them to 1 to limit the amount they can build in each setup round
            if (inSetup) {
                playerSetupRoadsLeft = 1;
                playerSetupSettlementLeft = 1;
            }

            // If the game was waiting for the user to build
            if (buildingObject != 0) {
                // Cancel the building placement
                buildingObject = 0;
                showRoadHitbox = false; // Hide placement hitboxes
                showSettlementHitbox = false; // Hide placement hitboxes
                // Change the button back to the build button
                buildBtn.setMode(1);//set it to "Build"
            }

            /* Old code. This is done in the udpateBuildButton method now
            // Disable all of the building buttons
            buildBtn.setEnabled(false);
            buildRoadRBtn.setEnabled(false);
            buildSettlementSRBtn.setEnabled(false);
            buildSettlementLRBtn.setEnabled(false);
            trade4to1Btn.setEnabled(false);
            trade3to1Btn.setEnabled(false);
            trade2to1Btn.setEnabled(false);

            //disable all the SettlerBtns
            toggleCardBtn.setEnabled(false);
            buyDevCardBtn.setEnabled(false);
            useDevCardBtn.setEnabled(false);*/
            //reset the boolean to false because the turn just ended and the user hasn't used a card yet
            userPlayedDevCard = false;

            // Change the button to the Start Next Turn button
            setTurnBtnTextStart();

            updateBuildButtons();

            // Redraw the board so the next player doesnt see the other player's cards
            repaint();

            //create and auto save in the roaming directory
            try {
                //ensure the directory is there
                Files.createDirectories(Paths.get(System.getProperty("user.home") + File.separator + "AppData" + File.separator + "Roaming" + File.separator + "SettlerDevs" + File.separator + "Catan"));

                //make an auto save now that the turn is over
                if (!writeToFile(System.getProperty("user.home") + File.separator + "AppData" + File.separator + "Roaming" + File.separator + "SettlerDevs" + File.separator + "Catan" + File.separator + "autosave.catan")) {
                    //if there was an error
                    System.out.println("Error writing to autosave.");
                }
            } catch (FileNotFoundException ex) {
                System.out.println("Error creating autosave.");
            } catch (IOException ex) {
                Logger.getLogger(GamePanel.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else if (playerSetupRoadsLeft != 0) {
            //let the player know that they have more setup roads to place
            instructionLbl.setText("Make sure you place your " + playerSetupRoadsLeft + " remaining road(s).");
            subInstructionLbl.setText("Build them from the build menu below.");
        } else if (playerSetupSettlementLeft != 0) {
            //let the player know that they have more setup roads to place
            instructionLbl.setText("Make sure you place your " + playerSetupSettlementLeft + " remaining settlement(s).");
            subInstructionLbl.setText("Build them from the build menu below.");
        }

    }

    /**
     * Exit the gamePanel without saving
     */
    private void backNoSaveBtnClicked() {
        int overwrite;
        overwrite = JOptionPane.showConfirmDialog(null, "Are you sure you would like to exit without saving?\nAll your progess will be lost.", "Confim", 0, JOptionPane.ERROR_MESSAGE);
        //If the user really want to leave let them
        if (overwrite == 0) {

            //Preform the opperations needed when leaving an online game
            networkingCloseOpertations();

            // Hide this window and show the main menu
            superFrame.getMainMenu().setVisible(true); //show the main menu
            superFrame.setVisible(false); //hide the parent frame 
        }
    }

    /**
     * Tasks that need to be performed when closing out of game from online mode
     */
    private void networkingCloseOpertations() {
        //if there is networking active stop it
        if (onlineMode != -1) {
            onlineClient.sendStop(); //tell the server that this client disconected and to close the server

            //reset the game to the offline defaults
            onlineMode = -1;
            onlineClient = null;
        }
    }

    /**
     * Actions preformed when a player want to trade 3:1 cards
     */
    private void trade3to1BtnClicked() {
        //check what mode of trading the game is in for 3:1
        if (tradingMode != 0) {
            //if the user clicked the cancel button reenable the turnswitch button and update the button lable
            turnBtnEnabled = true;
            trade3to1Btn.setMode(1); //set to "Trade 3:1"
            //remove the intent to trade
            tradingMode = 0;
            minTradeCardsNeeded = 0;
            //clear the resource if there was one
            tradeResource = 0;
            //hide the hitboxes
            showResStackHitbox = false;
            showCardHitbox = false;

            //update the buildbuttons (should be renabeling them now)
            updateBuildButtons();
            repaint();
        } else {
            //if the user clicked the button in the inactive state activate it.
            //register the intent to trade
            tradingMode = 2;
            minTradeCardsNeeded = 3;
            //diable turn switching
            turnBtnEnabled = false;
            //update the text of the button
            trade3to1Btn.setMode(0); //set to "Cancel"
            //show the hitboxes
            showResStackHitbox = true;
            //canbel any building if there is any
            cancelBuilding();

            //update the buildbuttons (should be disables for trading mode)
            updateBuildButtons();
            instructionLbl.setText("Please select the resource you would like to receive");
            subInstructionLbl.setText("Click the port that corresponds to the resource you would like to end up with.");
            repaint();
        }
    }

    /**
     * Actions preformed when a player want to trade 4:1 cards
     */
    private void trade4to1BtnClicked() {
        //check what mode of trading the game is in for 4:1
        if (tradingMode != 0) {
            //if the user clicked the cancel button reenable the turnswitch button and update the button lable
            turnBtnEnabled = true;
            trade4to1Btn.setMode(1); //set to "Trade 4:1"
            //remove the intent to trade
            tradingMode = 0;
            minTradeCardsNeeded = 0;
            //clear the resource if there was one
            tradeResource = 0;
            //hide the hitboxes
            showResStackHitbox = false;
            showCardHitbox = false;

            //update the buildbuttons (should be renabeling them now)
            updateBuildButtons();
            repaint();
        } else {
            //if the user clicked the button in the inactive state activate it.
            //register the intent to trade
            tradingMode = 1;
            minTradeCardsNeeded = 4;
            //diable turn switching
            turnBtnEnabled = false;
            //update the text of the button
            trade4to1Btn.setMode(0); //set to "Cancel"
            //show the hitboxes
            showResStackHitbox = true;
            //canbel any building if there is any
            cancelBuilding();

            //update the buildbuttons (should be disables for trading mode)
            updateBuildButtons();
            instructionLbl.setText("Please select the resource you would like to receive");
            subInstructionLbl.setText("Click the port that corresponds to the resource you would like to end up with.");
            repaint();
        }
    }

    /**
     * Actions preformed when a player want to trade 2:1 cards
     */
    private void trade2to1BtnClicked() {
        //check what mode of trading the game is in for 2:1
        if (tradingMode != 0) {
            //if the user clicked the cancel button reenable the turnswitch button and update the button lable
            turnBtnEnabled = true;
            trade2to1Btn.setMode(1); //set to "Trade 2:1"
            //remove the intent to trade
            tradingMode = 0;
            minTradeCardsNeeded = 0;
            //clear the resource if there was one
            tradeResource = 0;
            //hide the hitboxes
            showResStackHitbox = false;
            showCardHitbox = false;

            //update the buildbuttons (should be renabeling them now)
            updateBuildButtons();
            repaint();
        } else {
            //if the user clicked the button in the inactive state activate it.
            //register the intent to trade
            tradingMode = 3;
            minTradeCardsNeeded = 2;
            //diable turn switching
            turnBtnEnabled = false;
            //update the text of the button
            trade2to1Btn.setMode(0); //set to "Cancel"
            //show the hitboxes
            showResStackHitbox = true;
            //canbel any building if there is any
            cancelBuilding();

            //update the buildbuttons (should be disables for trading mode)
            updateBuildButtons();
            instructionLbl.setText("Please select the resource you would like to receive");
            subInstructionLbl.setText("Click the port that corresponds to the resource you would like to end up with.");
            repaint();
        }
    }

    /**
     * What to do when the user clicks a key on their keyboard This will be
     * called by the GameFrame
     *
     * @param evt
     */
    public void keyPress(KeyEvent evt) {
        //get the key type
        //System.out.println(evt.getKeyCode());

        //if a tab key
        if (evt.getKeyCode() == 9) {
            //advance the tabed button
            //turn off the current one
            settlerComponents[tabSelectedButton].setTabSelected(false);
            //increment by 1
            tabSelectedButton++;
            //check for over flow
            if (tabSelectedButton == settlerComponents.length) {
                //reset it
                tabSelectedButton = 0;
            }
            //set the new one
            settlerComponents[tabSelectedButton].setTabSelected(true);
            repaint();

        } else //if it's an enter key or a space key
        if (evt.getKeyCode() == 10 || evt.getKeyCode() == 32) {
            //click that button
            //make a fake click event
            mouseClick(new MouseEvent(this, 502, System.currentTimeMillis(), 16,
                    settlerComponents[tabSelectedButton].getXPos() + scaleInt(10),
                    settlerComponents[tabSelectedButton].getYPos() + scaleInt(10), 1, false));
        }

    }

    private void mouseMoveAction() {

        //check if the player moved the mouse over one of the SettlerBtns
        //loop through all the custom buttons
        for (SettlerBtn btn : settlerBtns) {
            if (mouseMotionPosX > btn.getXPos()
                    && mouseMotionPosY > btn.getYPos()
                    && mouseMotionPosX < (btn.getXPos() + getImgWidth(btn.getBaseImage()))
                    && mouseMotionPosY < (btn.getYPos() + getImgHeight(btn.getBaseImage()))
                    && btn.isEnabled()) { //and that it is enabled

                //set the hover
                btn.setmouseHover(true);

            } else {

                //make suer there is no hover over that button
                btn.setmouseHover(false);
            }

        }

        //check if the player moved the mouse over one of the SettlerRadioBtns
        //loop through all the custom buttons
        for (SettlerRadioBtn Rbtn : settlerRadioBtns) {
            if (mouseMotionPosX > Rbtn.getXPos()
                    && mouseMotionPosY > Rbtn.getYPos()
                    && mouseMotionPosX < (Rbtn.getXPos() + getImgWidth(Rbtn.getBaseImage()))
                    && mouseMotionPosY < (Rbtn.getYPos() + getImgHeight(Rbtn.getBaseImage()))
                    && Rbtn.isEnabled()) { //and that it is enabled

                //set the hover
                Rbtn.setmouseHover(true);

            } else {

                //make suer there is no hover over that button
                Rbtn.setmouseHover(false);
            }

        }

        repaint();

    }

    /**
     * Handles mouse input, based on the state of the game
     *
     * @param event The event triggered by the mouse click
     */
    public void mouseClick(MouseEvent event) {

        boolean authorizedUser; //stores whether or not the click can from an autheroized user
        // debug click listener
        //System.out.println("Click recieved at clock: " + Catan.clock); 
        //check if the user is authorized
        //only run logic if the player that clicked is allowed to (stops online players from clicking when it's not their turn)
        authorizedUser = onlineMode == -1 || onlineMode == currentPlayer;

        //check if the player clicked on one of the SettlerBtns
        //loop through all the custom buttons
        for (SettlerBtn btn : settlerBtns) {
            if (event.getX() > btn.getXPos()
                    && event.getY() > btn.getYPos()
                    && event.getX() < (btn.getXPos() + getImgWidth(btn.getBaseImage()))
                    && event.getY() < (btn.getYPos() + getImgHeight(btn.getBaseImage()))
                    && btn.isEnabled()) { //and that it is enabled

                //check the button that was pressed
                if (btn.equals(toggleCardBtn)) { //if it was the toggle button
                    //check the mode
                    if (toggleCardBtn.getMode() == 0) { //if in mode 0 (switch TO dev cards)
                        toggleCardBtn.setMode(1);
                        showDevCards = true;
                    } else if (toggleCardBtn.getMode() == 1) { //if in mode to switch TO resource cards
                        toggleCardBtn.setMode(0);
                        showDevCards = false;
                    }

                    //tab select this button
                    tabSelectBtn(toggleCardBtn);

                    repaint();

                } else if (btn.equals(backNoSaveBtn)) { //if the user clicked to trade with a 3:1 ratio
                    backNoSaveBtnClicked();
                } else if (btn.equals(backBtn)) { //if the user clicked to trade with a 3:1 ratio
                    backBtnClicked();
                }

                //only check the other buttons if there is and authroiazed user
                if (authorizedUser) {

                    if (btn.equals(buyDevCardBtn)) { //if there was a click on the buy card button
                        //since it was already confirmed the player has the corret amount of cards by the update build buttons method remove them
                        cards[currentPlayer].remove(new Integer("3"));
                        cards[currentPlayer].remove(new Integer("4"));
                        cards[currentPlayer].remove(new Integer("5"));

                        //select a randome dev card to give the player
                        int randCardIndex = (int) (Math.random() * availableDevCards.size());

                        //give the player a dev card
                        devCards[currentPlayer].add(availableDevCards.get(randCardIndex));

                        //give the player a point if they got a vp card
                        if (availableDevCards.get(randCardIndex) > 4) { //greater than 4 is 5-9
                            victoryPoints[currentPlayer]++;
                        }

                        //remove it from the ArrayList as it is no longer available
                        availableDevCards.remove(randCardIndex);

                        //sort the dev cards
                        quickSortCards(devCards[currentPlayer], 0, devCards[currentPlayer].size() - 1);

                        updateBuildButtons();
                        repaint();
                    } else if (btn.equals(useDevCardBtn)) { //if the player clicked the use dev card btn
                        //check the mode
                        if (btn.getMode() == 0) { //if no card is currently being activated
                            //disable the turn switch so it can't be used
                            turnBtnEnabled = false;
                            useDevCardBtn.setMode(1); //change the mode
                            showDevCardHitbox = true;

                            //force show the cards
                            showDevCards = true;

                            //register that the player wan't to use a dev card
                            usingDevCard = 0;
                        } else if (btn.getMode() == 1) { //if the user clicked the cancel button
                            //disable the turn switch so it can't be used
                            turnBtnEnabled = true;
                            useDevCardBtn.setMode(0); //change the mode
                            showDevCardHitbox = false;
                            showTileHitbox = false;

                            //reset the dev card being used to no card
                            usingDevCard = -1;
                        }

                        updateBuildButtons();
                        repaint();
                    } else if (btn.equals(turnSwitchBtn)) { //if the user clicked to end/start their turn
                        turnSwitchBtnClicked();
                    } else if (btn.equals(trade4to1Btn)) { //if the user clicked to trade with a 4:1 ratio
                        trade4to1BtnClicked();
                    } else if (btn.equals(trade3to1Btn)) { //if the user clicked to trade with a 3:1 ratio
                        trade3to1BtnClicked();
                    } else if (btn.equals(trade2to1Btn)) { //if the user clicked to trade with a 3:1 ratio
                        trade2to1BtnClicked();
                    } else if (btn.equals(buildBtn)) { //if the user clicked to trade with a 3:1 ratio
                        buildBtnClicked();
                    }

                    //tab select this button
                    tabSelectBtn(btn);
                }
            }
        }

        //check if the player clicked on one of the Settler Radio Buttons
        //loop through all the custom radio buttons
        for (SettlerRadioBtn radioBtn : settlerRadioBtns) {
            if (event.getX() > radioBtn.getXPos()
                    && event.getY() > radioBtn.getYPos()
                    && event.getX() < (radioBtn.getXPos() + getImgWidth(radioBtn.getBaseImage()))
                    && event.getY() < (radioBtn.getYPos() + getImgHeight(radioBtn.getBaseImage()))
                    && radioBtn.isEnabled()) { //and that it is enabled

                //only check the other buttons if there is and authroiazed user
                if (authorizedUser) {

                    if (radioBtn.equals(buildRoadRBtn)) {
                        buildRoadRBtn.setSelected(true);
                    } else if (radioBtn.equals(buildSettlementSRBtn)) { //if the user clicked to trade with a 3:1 ratio
                        buildSettlementSRBtn.setSelected(true);
                    } else if (radioBtn.equals(buildSettlementLRBtn)) { //if the user clicked to trade with a 3:1 ratio
                        buildSettlementLRBtn.setSelected(true);
                    }

                    repaint();
                }
            }
        }

        //only check for a click on the rest of the board if the user is authoriazed
        if (authorizedUser) {

            //check if the player is building
            if (buildingObject != 0) {
                //check what they are building
                switch (buildingObject) {
                    case 1:
                        //roads

                        //check the distance to the nearest road using hitboxes and check if it is close enough
                        for (int i = 0; i < roadNodes.size() - 1; i++) {

                            //get the type of road and set the width and height //get this to not be hard coded if there is time
                            if (roadNodes.get(i).getOrientation() == 0) {
                                roadWidth = getImgWidth(RED_ROAD_H); //scale the road dimensions
                                roadHeight = getImgHeight(RED_ROAD_H);
                            } else {
                                roadWidth = getImgWidth(RED_ROAD_L);
                                roadHeight = getImgHeight(RED_ROAD_L);
                            }

                            //if the player click in a valid hitbox for a road
                            if (event.getX() > roadNodes.get(i).getXPos() - roadWidth / 2
                                    && event.getX() < roadNodes.get(i).getXPos() - roadWidth / 2 + roadWidth
                                    && event.getY() > roadNodes.get(i).getYPos() - roadHeight / 2
                                    && event.getY() < roadNodes.get(i).getYPos() - roadHeight / 2 + roadHeight) {
                                //debug road build detection
                                //System.out.println("road match");

                                //check that the road is unowned
                                if (roadNodes.get(i).getPlayer() == 0) {
                                    //check what mode the game is in
                                    if (inSetup && playerSetupRoadsLeft > 0) {

                                        //check if the player has placed a settlement yet
                                        if (newestSetupSettlment != null) {

                                            //check that the road is next to the just build settlement
                                            if (newestSetupSettlment.getRoad(1) == roadNodes.get(i)
                                                    || newestSetupSettlment.getRoad(2) == roadNodes.get(i)
                                                    || newestSetupSettlment.getRoad(3) == roadNodes.get(i)) {

                                                roadNodes.get(i).setPlayer(currentPlayer);
                                                playerSetupRoadsLeft--;
                                                // Update thwe build buttons to relfect the remaining setup buildings
                                                updateBuildButtons();
                                            } else { //display the error to the user
                                                instructionLbl.setText("Sorry but that is not a valid location.");
                                                subInstructionLbl.setText("Try building next to the settlement just built.");
                                            }
                                        } else { //display the error to the user
                                            instructionLbl.setText("Sorry but you must build a new settlement first.");
                                            subInstructionLbl.setText("Try building the road after.");
                                        }

                                    } // If the real game is in progress and the player can build there
                                    else if (canBuildRoad(roadNodes.get(i))) {

                                        //check if the user built roads given from the progress card
                                        if (usingDevCard == 2 && playerSetupRoadsLeft > 0) { //these aren't actually setup roads. They just dont require resources

                                            //cout a free road
                                            playerSetupRoadsLeft--;

                                            //check if the player is done now
                                            if (playerSetupRoadsLeft == 0) {
                                                resetUsingDevCards();
                                            }

                                        } else { //for normally bought roads

                                            // The card check has already been made, and the user has the right cards
                                            // Remove the cards from the player's deck
                                            // Remove 1 clay and 1 wood
                                            cards[currentPlayer].remove(new Integer(1));
                                            cards[currentPlayer].remove(new Integer(2));
                                        }

                                        // Set the road's player to the current player
                                        roadNodes.get(i).setPlayer(currentPlayer);
                                        /*
                                     *
                                     *=-=-=-=-=-=-=-=-=-=-=-=-= Longest Road Detedtion Start =-=-=-=-=-=-=-=-=-=-=-=-=\\
                                     *
                                         */

                                        //remove the points from who ever has them incase it changes
                                        if (longestRoadData.getPlayerNum() != 0 && longestRoadData.getSize() >= 5) { //only if the player wan't player 0
                                            victoryPoints[longestRoadData.getPlayerNum()] -= 2;
                                        }

                                        //loop through all the roads
                                        for (NodeRoad road : roadNodes) {

                                            //check to not use the null road
                                            if (road != null) {

                                                checkForLongestRoad(road, 0, currentPlayer); //pass the road and the current branch length

                                                //clear the array for checked roads
                                                alreadyCheckedRoad.clear();
                                                //also clear the settlments
                                                alreadyCheckedSettlements.clear();
                                            }

                                        }

                                        //add the points to who ever has the longest road
                                        //only if the player wan't player 0
                                        //also only if the length is greater than 5
                                        if (longestRoadData.getPlayerNum() != 0 && longestRoadData.getSize() >= 5) {
                                            victoryPoints[longestRoadData.getPlayerNum()] += 2;
                                        }

                                        //debug the longest road
                                        //System.out.println("\n" + longestRoadData);
                                        /*
                                     *
                                     *=-=-=-=-=-=-=-=-=-=-=-=-= Longest Road Detedtion End =-=-=-=-=-=-=-=-=-=-=-=-=\\
                                     *
                                         */
                                        // Update the building buttons to reflect the player's new list of cards
                                        updateBuildButtons();

                                    } // If the player could not build there
                                    else {
                                        // Print out why the player could not build there
                                        instructionLbl.setText("Sorry but you can't build a road there.");
                                        subInstructionLbl.setText("Try building adjacent to one of your exsisting buildings");
                                    }
                                } else {
                                    instructionLbl.setText("Sorry but you can't take a claimed road.");
                                    subInstructionLbl.setText("Try building where there isn't already another road");
                                }

                                // Stop building
                                buildingObject = 0;
                                showRoadHitbox = false;
                                // Change the button back to the build button
                                buildBtn.setMode(1);//set it to "Build"
                                // Redraw the board
                                repaint();
                            }
                        }
                        break;
                    case 2:
                        //small house

                        //check the distance to the nearest settlement node using hitboxes and check if it is close enough
                        for (int i = 0; i < settlementNodes.size(); i++) {

                            //if the player clicks in a valid hitbox for a settlement
                            if (event.getX() > settlementNodes.get(i).getXPos() - getImgWidth(RED_HOUSE_S) / 2
                                    && event.getX() < settlementNodes.get(i).getXPos() - getImgWidth(RED_HOUSE_S) / 2 + getImgWidth(RED_HOUSE_S)
                                    && event.getY() > settlementNodes.get(i).getYPos() - getImgHeight(RED_HOUSE_S) / 2
                                    && event.getY() < settlementNodes.get(i).getYPos() - getImgHeight(RED_HOUSE_S) / 2 + getImgHeight(RED_HOUSE_S)) {
                                //debug settlent build detection
                                //System.out.println("hitbox match");

                                //check that the settlement is unowned
                                if (settlementNodes.get(i).getPlayer() == 0) {

                                    // Check that the player can build there and update the instruction labels accordingly if hey cannot
                                    if (canBuildSettlement(settlementNodes.get(i), false)) {

                                        // If the game is in setup
                                        if (inSetup) { // In Setup
                                            // Decremeent the number of setup settlements since one is placed
                                            playerSetupSettlementLeft--;
                                        } // If the main game is in progress
                                        else {
                                            // The card check has already been made, and the user has the right cards

                                            // Remove the required cards from the player's deck
                                            // Remove 1 clay, 1 wood, 1 wheat, and 1 sheep
                                            cards[currentPlayer].remove(new Integer(1));
                                            cards[currentPlayer].remove(new Integer(2));
                                            cards[currentPlayer].remove(new Integer(3));
                                            cards[currentPlayer].remove(new Integer(4));
                                        }

                                        // Set the settlement's player to the current player
                                        settlementNodes.get(i).setPlayer(currentPlayer);

                                        //check to see if that settlment is on a port
                                        //loop thorugh the ports and see if the settlement just built is on a port
                                        for (int j = 0; j < ports.size(); j++) {

                                            //check to see if that port contains this settlemnt
                                            if (portSettlements[j].contains(settlementNodes.get(i))) {
                                                //save that the new settlement is on a port and which one
                                                playerHasPort[currentPlayer][ports.get(j).getType()] = true;
                                            }

                                        }

                                        //save the settelment just built
                                        newestSetupSettlment = settlementNodes.get(i);

                                        // Increment the player's victory point counter
                                        victoryPoints[currentPlayer]++;

                                        // Update the building buttons to reflect the player's new list of cards
                                        // or number of setup buildings
                                        updateBuildButtons();
                                    }

                                } else {
                                    instructionLbl.setText("Sorry but you can't take a claimed settlement.");
                                    subInstructionLbl.setText("Try building where there isn't already another settlement");
                                }

                                // Stop building and hide the hitboxes
                                buildingObject = 0;
                                showSettlementHitbox = false;
                                // Change the button back to the build button
                                buildBtn.setMode(1);//set it to "Build"
                                // Redraw the board
                                repaint();
                            }
                        }
                        break;
                    case 3:
                        //large house

                        //check the distance to the nearest settlement node using hitboxes and check if it is close enough
                        for (int i = 0; i < settlementNodes.size(); i++) {

                            //if the player clicks in a valid hitbox for a settlement
                            if (event.getX() > settlementNodes.get(i).getXPos() - getImgWidth(RED_HOUSE_S) / 2
                                    && event.getX() < settlementNodes.get(i).getXPos() - getImgWidth(RED_HOUSE_S) / 2 + getImgWidth(RED_HOUSE_S)
                                    && event.getY() > settlementNodes.get(i).getYPos() - getImgHeight(RED_HOUSE_S) / 2
                                    && event.getY() < settlementNodes.get(i).getYPos() - getImgHeight(RED_HOUSE_S) / 2 + getImgHeight(RED_HOUSE_S)) {

                                // Check that the current player owns settlement
                                if (settlementNodes.get(i).getPlayer() == currentPlayer) {

                                    // Check that the settlement isn't already large
                                    if (settlementNodes.get(i).isLarge() == false) {
                                        // The card check has already been made, and the user has the right cards

                                        // Remove the cards from the player's deck
                                        // Remove 2 wheat, and 3 ore
                                        cards[currentPlayer].remove(new Integer(3));
                                        cards[currentPlayer].remove(new Integer(3));
                                        cards[currentPlayer].remove(new Integer(5));
                                        cards[currentPlayer].remove(new Integer(5));
                                        cards[currentPlayer].remove(new Integer(5));

                                        // Make the settlement large
                                        settlementNodes.get(i).setLarge(true);

                                        // Increment the player's victory point counter
                                        victoryPoints[currentPlayer]++;

                                        // Update the building buttons to reflect the player's new list of cards
                                        updateBuildButtons();
                                    } else { // If the settlement is already large

                                        instructionLbl.setText("Sorry but settlement is already large.");
                                        subInstructionLbl.setText("Try upgrading a small settlement");
                                    }
                                } else { // If the settlement belongs to another player
                                    //check what player it is
                                    if (settlementNodes.get(i).getPlayer() == 0) {
                                        instructionLbl.setText("Sorry but you can't upgrade an unowned settlement.");
                                        subInstructionLbl.setText("Try upgrading your own settlement");
                                    } else {
                                        instructionLbl.setText("Sorry but you can't upgrade someone elses settlement.");
                                        subInstructionLbl.setText("Try upgrading your own settlement");
                                    }
                                }

                                // Stop building and hide the hitboxes
                                buildingObject = 0;
                                showSettlementHitbox = false;
                                // Change the button back to the build button
                                buildBtn.setMode(1);//set it to "Build"
                                // Redraw the board
                                repaint();
                            }
                        }
                        break;
                    default:
                        System.out.println("Yeah we've got an error here chief. Building in the mouse click event printed me");
                        break;
                }
            } else if (thiefIsStealing && stealCardNum[currentPlayer] > 0 && !thiefJustStarted) { //check if the user clicked to select a card and the thief didn't just start

                //get the y position for the cards
                int cardYPos = (int) (superFrame.getHeight() - (getImgHeight(CARD_CLAY) * 1.125));

                //check what mode the card drawing is in
                if (drawCardStacks[currentPlayer]) { //check for a click on a cards in the stacked mode

                    //loop though the 5 stacks
                    for (int i = 0; i < 5; i++) {
                        //check for a click
                        if (event.getX() > cardStackXPositions[i]
                                && event.getX() < (cardStackXPositions[i] + getImgWidth(CARD_CLAY))
                                && event.getY() > cardYPos
                                && event.getY() < (cardYPos + getImgHeight(CARD_CLAY))) {
                            //debug click detection
                            //System.out.println("Card Clicked!");
                            boolean removeSuccess; //crate var to save if the removal was succesful
                            removeSuccess = cards[currentPlayer].remove(new Integer(i + 1)); //remove the card

                            //check if that was a valid card removal
                            if (removeSuccess) {
                                stealCardNum[currentPlayer]--; //count the removal

                                //check if the player is done now
                                if (stealCardNum[currentPlayer] <= 0) {
                                    turnBtnEnabled = true;
                                    showCardHitbox = false;
                                }
                            }

                            updateBuildButtons();
                            repaint();
                        }
                    }

                } else { //check for a click on a card in the full layout mode

                    //check if the user clicked on any card
                    for (int i = 0; i < cards[currentPlayer].size(); i++) {
                        //get the x position for that card
                        int cardXPos = (cardStartPosition + (getImgWidth(CARD_CLAY) + 10) * i);

                        //check if there was a click on a card
                        if (event.getX() > cardXPos
                                && event.getY() > cardYPos
                                && event.getX() < (cardXPos + getImgWidth(CARD_CLAY))
                                && event.getY() < (cardYPos + getImgHeight(CARD_CLAY))) {
                            //debug click detection
                            //System.out.println("Card Clicked!");
                            cards[currentPlayer].remove(i); //remove the card
                            stealCardNum[currentPlayer]--; //count the removal

                            //check if the player is done now
                            if (stealCardNum[currentPlayer] <= 0) {
                                turnBtnEnabled = true;
                                showCardHitbox = false;
                            }

                            updateBuildButtons();
                            repaint();

                        }
                    }

                }

            } else if (showDevCardHitbox && usingDevCard == 0) { //check if the player clicked on a dev card to actiavet it and use it
                //get the y position for the cards
                int devCardYPos = (int) (superFrame.getHeight() - (getImgHeight(DEV_CARD_KNIGHT) * 1.125));

                //check what mode the card drawing is in
                if (drawDevCardStacks[currentPlayer]) { //check for a click on a cards in the stacked mode

                    //loop though four of the 5 stacks. They last one doesn't need to be checked as it isn't playable
                    for (int i = 0; i < 4; i++) {
                        //check for a click
                        if (event.getX() > devCardStackXPositions[i]
                                && event.getX() < (devCardStackXPositions[i] + getImgWidth(DEV_CARD_KNIGHT))
                                && event.getY() > devCardYPos
                                && event.getY() < (devCardYPos + getImgHeight(DEV_CARD_KNIGHT))) {
                            //debug click detection
                            //System.out.println("Stack Dev Card Clicked!");

                            usingDevCard = i + 1;

                            //make sure there is atleast one card in the stack
                            if (devCardTypeCount[i] > 0) {

                                //get the type of dev card clicked
                                switch (i) {
                                    case 0:
                                        //if knight
                                        clickedKnightCard();

                                        break;
                                    case 1:
                                        //if vp road
                                        clickedRoadCard();

                                        break;
                                    case 2:
                                        //if vp monopoly
                                        clickedMonopolyCard();

                                        break;
                                    case 3:
                                        //if vp YOP
                                        clickedYOPCard();

                                        break;
                                    default:
                                        //if anything else
                                        //System.out.println("Invalid card clicked");
                                        break;
                                }

                                //hide dev card hitbox
                                showDevCardHitbox = false;

                                updateBuildButtons();
                                repaint();
                            }
                        }
                    }

                } else { //check for a click on a card in the full layout mode

                    //check if the user clicked on any card
                    for (int i = 0; i < devCards[currentPlayer].size(); i++) {
                        //get the x position for that card
                        int cardXPos = (devCardStartPosition + (getImgWidth(DEV_CARD_KNIGHT) + 10) * i);

                        //check if there was a click on a card
                        if (event.getX() > cardXPos
                                && event.getY() > devCardYPos
                                && event.getX() < (cardXPos + getImgWidth(DEV_CARD_KNIGHT))
                                && event.getY() < (devCardYPos + getImgHeight(DEV_CARD_KNIGHT))) {
                            //debug click detection
                            //System.out.println("Dev Card Clicked!");

                            //save the card type
                            int devCardType = devCards[currentPlayer].get(i);

                            //make sure the dev card type is a useable one and not VP
                            if (devCardType < 5) {

                                usingDevCard = devCardType;

                                //get the type of dev card clicked
                                switch (devCardType) {
                                    case 1:
                                        //if knight
                                        clickedKnightCard();

                                        break;
                                    case 2:
                                        //if vp road
                                        clickedRoadCard();

                                        break;
                                    case 3:
                                        //if vp monopoly
                                        clickedMonopolyCard();

                                        break;
                                    case 4:
                                        //if vp YOP
                                        clickedYOPCard();

                                        break;
                                    default:
                                        //if anything else
                                        System.out.println("Invalid card clicked");
                                        break;
                                }

                                //hide dev card hitbox
                                showDevCardHitbox = false;

                            }

                            updateBuildButtons();
                            repaint();
                        }
                    }

                }

            } else if ((thiefIsStealing && thiefJustStarted && currentPlayer == playerRolled7) || (usingDevCard == 1 && showTileHitbox)) { //check if the player clicked on a Tile to move the thief
                // ^^^ either for when a 7 is rolled or when a knight card is used.

                //loop through the Tiles. Ignore the null Tile at the end
                for (int i = 0; i < tiles.size() - 1; i++) {

                    //get the x and y positions for that tile
                    int tilePosX = tiles.get(i).getXPos() + newTileWidth / 2 - ((int) (30 / scaleFactor) / 2);
                    int tilePosY = (int) (tiles.get(i).getYPos() + newTileHeight / 2 - ((30 / scaleFactor) / 2) + threeDTileOffset);

                    //check if there was a click on that tile.
                    if (event.getX() > tilePosX
                            && event.getY() > tilePosY
                            && event.getX() < (tilePosX + (int) (30 / scaleFactor))
                            && event.getY() < (tilePosY + (int) (30 / scaleFactor))) {
                        //debug click detection
                        //System.out.println("Got Click");

                        //check if that was a valid Tile to select. Eg. no thief on the tile
                        if (!tiles.get(i).hasThief()) {
                            //disabable the hitbox because the click was registered.
                            showTileHitbox = false;

                            //put the thief on that tile
                            tiles.get(i).setThief(true);

                            //remove the thief from the old one
                            tiles.get(tileWithThief).setThief(false);

                            //update the tileWithThief var
                            tileWithThief = i;

                            // Increment the thief movement counter
                            thiefMoveCounter++;

                            //check if the player who rolled the 7 is eligable to steal cards
                            //loop through all the settlements and see if they boarder the new thief tile and if it is owned
                            for (int j = 0; j < settlementNodes.size(); j++) {
                                //loop 3 times for the 3 potencial tile connections
                                for (int k = 1; k < 4; k++) {
                                    //check if the settlment is on the Tile and if the settlement is owned and if the owner is someone other than current player
                                    if (settlementNodes.get(j).getTile(k) == tiles.get(i) && settlementNodes.get(j).getPlayer() != 0 && settlementNodes.get(j).getPlayer() != currentPlayer) {
                                        //if so than the current player can steal cards from the player that owns that settlment
                                        canStealCardPlayers.add(settlementNodes.get(j).getPlayer());
                                    }
                                }
                            }

                            //System.out.println(canStealCardPlayers);
                            //decide how the rest will continue depending on if whether or not a knight card was used
                            if (usingDevCard == 1) {

                                //check if there are any players to steal from
                                if (canStealCardPlayers.size() > 0) {
                                    showSubPlayerHitbox = true;
                                } else {
                                    resetUsingDevCards();
                                }

                            } else {
                                //renable the turnSwitchBtn because the player has now succefully moved the theif and they can now move 
                                //onto slecting the cards they would like to discard if the requirements are met.
                                turnBtnEnabled = true;
                            }
                        }

                        updateBuildButtons();
                        repaint();
                    }
                }

            } else if ((canStealCardPlayers.size() > 0 && currentPlayer == playerRolled7 && !thiefIsStealing && !inbetweenTurns) || (showSubPlayerHitbox && usingDevCard == 1)) { //check if the player just clicked to select another player to seal from
                //debug
                //System.out.println("Got a steal click");

                int subPlayerPosY = superFrame.getHeight() - (int) (10 / scaleFactor) - getImgHeight(SMALL_PLAYER_RED);
                int subPlayerPosX;

                //loop through the subsequest players and see if there was a click on one of them. Skip the first player in the ArrayList because it is the current players
                for (int i = 1; i < playerTurnOrder.size(); i++) {

                    subPlayerPosX = superFrame.getWidth() - (getImgWidth(PLAYER_RED)) - (getImgWidth(SMALL_PLAYER_RED) * i);

                    //check if there was a click on one of the sub players
                    if (event.getX() > subPlayerPosX
                            && event.getY() > subPlayerPosY
                            && event.getX() < (subPlayerPosX + (getImgWidth(SMALL_PLAYER_RED)))
                            && event.getY() < (subPlayerPosY + (getImgHeight(SMALL_PLAYER_RED)))) {

                        //debug
                        //System.out.println("Yuh we got a click on player: " + playerTurnOrder.get(i));
                        //check if the player is allowed to steal from that players and that said player has atleast 1 card to steal
                        if (canStealCardPlayers.contains(playerTurnOrder.get(i)) && cards[playerTurnOrder.get(i)].size() > 0) {
                            //debug
                            //System.out.println("Yup valid removal");

                            //steal the card
                            //randomly select a card from the targets hand
                            int randomCard = (int) (Math.random() * cards[playerTurnOrder.get(i)].size());

                            //debug the stealing
                            //System.out.println("index of: " + randomCard);
                            //System.out.println("card type: " + cards[playerTurnOrder.get(i)].get(randomCard));
                            //give the card to the playerRolled7
                            cards[currentPlayer].add(cards[playerTurnOrder.get(i)].get(randomCard));
                            //remove said card from the original player
                            cards[playerTurnOrder.get(i)].remove(randomCard);

                            //reenable the turn switch button
                            turnBtnEnabled = true;

                            //clear the canStealCardPlayers ArrayList
                            canStealCardPlayers.clear();

                            //hide the hitbox again
                            showSubPlayerHitbox = false;

                            //update the instructions
                            instructionLbl.setText("You may now continue your turn.");
                            subInstructionLbl.setText("Building and trading is allowed assuming you have the correct cards.");

                            //since player stealing is now done now. Reset this to false
                            subPlayersHaveEnoughcards = false;

                            //sort the cards first
                            quickSortCards(cards[currentPlayer], 0, cards[currentPlayer].size() - 1);

                            //if this was tiggered by a dev knight card
                            if (usingDevCard == 1) {
                                resetUsingDevCards();
                            }

                            //redraw
                            updateBuildButtons();
                            repaint();
                        }

                    }

                }

                /*
            if (canStealCardPlayers.size() > 0 && currentPlayer == playerRolled7 && !thiefIsStealing && !inbetweenTurns) {
                g2d.setColor(Color.green);
                g2d.drawRect(superFrame.getWidth() - (getImgWidth(PLAYER_RED)) - ((getImgWidth(PLAYER_RED) / 2) * i), //put it in the corner with some padding space
                    superFrame.getHeight() - (int) (10 / scaleFactor) - getImgHeight(PLAYER_RED) / 2, //put it in the corner with some padding space
                    getImgWidth(PLAYER_RED) / 2, //scale the image
                    getImgHeight(PLAYER_RED) / 2);
                g2d.setColor(Color.black);
            }
                 */
            } //but it's not really a port. Now its a resource stack
            else if ((showResStackHitbox && tradingMode != 0 && tradeResource == 0) //check if the player is clicking a port to select a resource type to trade to
                    || ((usingDevCard == 4 || usingDevCard == 3) && showResStackHitbox)) {
                //or if the player clicked the port to select a resource type for a dev card

                int stackPosX;
                int stackPosY = 1;
                boolean validResource;

                //loop through the resource stacks
                for (int i = 1; i < 6; i++) {
                    stackPosX = resourceStackXPositions[i];
                    validResource = false;

                    //check if there was a click on a port
                    if (event.getX() > stackPosX
                            && event.getY() > stackPosY
                            && event.getX() < (stackPosX + getImgWidth(RES_STACKS[i]))
                            && event.getY() < (stackPosY + getImgHeight(RES_STACKS[i]))) {

                        //check if the player clicked the port for trading or dev card
                        switch (usingDevCard) {
                            case 4:
                                //YOP card

                                //give the player two of the resource
                                for (int j = 0; j < 2; j++) {
                                    cards[currentPlayer].add(i);
                                }

                                //sort the card
                                quickSortCards(cards[currentPlayer], 0, cards[currentPlayer].size() - 1);

                                //finish using the dev card
                                resetUsingDevCards();
                                updateBuildButtons();

                                //turn off the hitboxes
                                showResStackHitbox = false;

                                //update the screen
                                repaint();

                                break;
                            case 3:
                                //Monopoly card

                                //save the resource type the player wants
                                int resourceType = i;

                                //loop through all the cards.
                                //Go through each players ArrayList
                                for (ArrayList<Integer> cardDeck : cards) {

                                    //make sure the current players deck isn't searched
                                    //or the null player
                                    if (cardDeck != cards[currentPlayer] && cardDeck != cards[0]) {

                                        //now go through the indevidual cards
                                        for (Integer cardID : cardDeck) {
                                            //debug
                                            //System.out.println("Checking at: " + ", Val: " + cardID);

                                            //if that card type is the wanted card type remove it 
                                            if (cardID == resourceType) {

                                                //then give it to the player that used the monopoly card
                                                cards[currentPlayer].add(cardID);
                                            }
                                        }

                                        //now go through the player being stolen from and remove all the cards
                                        while (cardDeck.contains(resourceType)) {
                                            cardDeck.remove(new Integer(resourceType));
                                        }

                                    }

                                }

                                //sort the current players card
                                quickSortCards(cards[currentPlayer], 0, cards[currentPlayer].size() - 1);

                                //finish using the dev card
                                resetUsingDevCards();
                                updateBuildButtons();

                                //turn off the hitboxes
                                showResStackHitbox = false;

                                //update the screen
                                repaint();

                                break;
                            default:
                                //if its for trading

                                //also if clicking that port would leave the player with no options for cards to trade away
                                //split up into generic 4:1 or 3:1, and specialized 2:1 trades
                                //4:1 or 3:1 tradng
                                if ((tradingMode == 1 || tradingMode == 2) && canTradeTo(i, tradingMode)) {
                                    validResource = true;
                                } else if (tradingMode == 3 && canTradeSecializedTo(i)) { //2:1 tradng
                                    validResource = true;
                                }

                                //only make the selection if its a valid port selection
                                if (validResource) {
                                    //register the type the player want to trade TO
                                    tradeResource = i;

                                    //update the instructions for the next trading step
                                    instructionLbl.setText("Now select a card");
                                    subInstructionLbl.setText("This card should be of the type you would like to trade away");

                                    //show the card hitboxes
                                    showCardHitbox = true;

                                    //turn off the hitboxes
                                    showResStackHitbox = false;

                                    //update the screen
                                    repaint();
                                }

                                break;
                        }
                    }
                }

            } else if (showCardHitbox && tradingMode != 0 && tradeResource != 0) { //check if a player clicked a card for trading purposes
                //get the y position for the cards
                int cardYPos = (int) (superFrame.getHeight() - (getImgHeight(CARD_CLAY) * 1.125));
                boolean validCard;

                //check what mode the card drawing is in
                if (drawCardStacks[currentPlayer]) { //check for a click on a cards in the stacked mode

                    //loop though the 5 stacks
                    for (int i = 0; i < 5; i++) {
                        validCard = false;

                        //check for a click
                        if (event.getX() > cardStackXPositions[i]
                                && event.getX() < (cardStackXPositions[i] + getImgWidth(CARD_CLAY))
                                && event.getY() > cardYPos
                                && event.getY() < (cardYPos + getImgHeight(CARD_CLAY))) {

                            //check if the card is available to trade and that it is not the same type the payer would like to trade TO
                            //split the handeling for the 2:1 away from the others
                            if ((i + 1) != tradeResource) {

                                //check for a specialized trade
                                if (tradingMode == 3 && (playerHasPort[currentPlayer][i + 1]) && cardTypeCount[i] >= minTradeCardsNeeded) {
                                    validCard = true;
                                } else if ((tradingMode == 1 || tradingMode == 2) && cardTypeCount[i] >= minTradeCardsNeeded) { //if 4:1 or 3:1
                                    validCard = true;
                                }

                                //only make the selection if its a valid card selection
                                if (validCard) {
                                    //debug click detection
                                    //System.out.println("Card stack Clicked!");
                                    Integer typeToRemove = i + 1;

                                    //remove the required amount of that card type
                                    for (int j = 0; j < minTradeCardsNeeded; j++) {
                                        cards[currentPlayer].remove(typeToRemove);
                                    }

                                    //add the card the player wants
                                    cards[currentPlayer].add(tradeResource);

                                    //sort the cards so when the build button are updated they are in the correct order
                                    quickSortCards(cards[currentPlayer], 0, cards[currentPlayer].size() - 1);

                                    //turn off behavior as if the cancel button was pressed.
                                    turnBtnEnabled = true;
                                    trade4to1Btn.setMode(1); //set to "Trade 4:1"
                                    trade3to1Btn.setMode(1); //set to "Trade 3:1"
                                    trade2to1Btn.setMode(1); //set to "Trade 2:1"
                                    //remove the intent to trade
                                    tradingMode = 0;
                                    minTradeCardsNeeded = 0;
                                    //clear the resource if there was one
                                    tradeResource = 0;
                                    //hide the hitboxes
                                    showResStackHitbox = false;
                                    showCardHitbox = false;
                                    //update the buildbuttons (should be renabeling them now)
                                    updateBuildButtons();
                                    //give the instructions that the user can now do what ever they want
                                    instructionLbl.setText("Thank you for your trade. The trade vessels have already departed.");
                                    subInstructionLbl.setText("You may now continue your turn how ever you please.");
                                }
                            }
                        }
                    }

                } else { //check for a click on a card in the full layout mode

                    //check if the user clicked on any card
                    for (int i = 0; i < cards[currentPlayer].size(); i++) {
                        validCard = false;

                        //get the x position for that card
                        int cardXPos = (cardStartPosition + (getImgWidth(CARD_CLAY) + 10) * i);

                        //check if there was a click on a card
                        if (event.getX() > cardXPos
                                && event.getY() > cardYPos
                                && event.getX() < (cardXPos + getImgWidth(CARD_CLAY))
                                && event.getY() < (cardYPos + getImgHeight(CARD_CLAY))) {

                            //check if the card is available to trade and that it is not the same type the payer would like to trade TO
                            //split the 2:1 haneling
                            if (cards[currentPlayer].get(i) != tradeResource) {

                                //check for specialized
                                if (tradingMode == 3 && (playerHasPort[currentPlayer][cards[currentPlayer].get(i)]) && numCardType[cards[currentPlayer].get(i)] >= minTradeCardsNeeded) {
                                    validCard = true;
                                } else if ((tradingMode == 1 || tradingMode == 2) && numCardType[cards[currentPlayer].get(i)] >= minTradeCardsNeeded) { //if 4:1 or 3:1
                                    validCard = true;
                                }

                                //only make the selection if its a valid card selection
                                if (validCard) {
                                    //debug click detection
                                    //System.out.println("Card no stack Clicked!");
                                    Integer typeToRemove = cards[currentPlayer].get(i);

                                    //remove the required amount of that card type
                                    for (int j = 0; j < minTradeCardsNeeded; j++) {
                                        cards[currentPlayer].remove(typeToRemove);
                                    }

                                    //add the card the player wants
                                    cards[currentPlayer].add(tradeResource);

                                    //sort the cards so when the build button are updated they are in the correct order
                                    quickSortCards(cards[currentPlayer], 0, cards[currentPlayer].size() - 1);

                                    //turn off behavior as if the cancel button was pressed.
                                    turnBtnEnabled = true;
                                    trade4to1Btn.setMode(1); //set to "Trade 4:1"
                                    trade3to1Btn.setMode(1); //set to "Trade 3:1"
                                    trade2to1Btn.setMode(1); //set to "Trade 2:1"
                                    //remove the intent to trade
                                    tradingMode = 0;
                                    minTradeCardsNeeded = 0;
                                    //clear the resource if there was one
                                    tradeResource = 0;
                                    //hide the hitboxes
                                    showResStackHitbox = false;
                                    showCardHitbox = false;
                                    //update the buildbuttons (should be renabeling them now)
                                    updateBuildButtons();
                                    //give the instructions that the user can now do what ever they want
                                    instructionLbl.setText("Thank you for your trade. The trade vessels have already departed.");
                                    subInstructionLbl.setText("You may now continue your turn how ever you please.");
                                }
                            }

                        }
                    }

                }

                //update the screen
                repaint();
            }

            //update the server if online mode and the click came from an authroiazed user
            onlineUpdateServer();

        }

    }

    /**
     * Actions taken when a knight card is clicked
     */
    public void clickedKnightCard() {
        //if knight
        //System.out.println("Knight Clicked");

        //show the tile hitboxes to move robber
        showTileHitbox = true;
    }

    /**
     * Actions taken when a road building development card is clicked
     */
    public void clickedRoadCard() {
        //System.out.println("Road Clicked");

        //give the player two manditory roads
        playerSetupRoadsLeft = 2;

        //no longer able to cancel
        useDevCardBtn.setEnabled(false);
    }

    /**
     * Actions taken when a monopoly development card is clicked
     */
    public void clickedMonopolyCard() {
        //System.out.println("Monopoly Clicked");

        //show the port hitboxes so the player can select a resource
        showResStackHitbox = true;

    }

    /**
     * Actions taken when a year of plenty development card is clicked
     */
    public void clickedYOPCard() {
        //System.out.println("YOP Clicked");

        //show the port hitboxes so the player can select a resource
        showResStackHitbox = true;
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Saving and Loading"> 
    /**
     * Save game data to a file
     *
     * @return if the operation was successful
     */
    public boolean save() {
        boolean success = false;

        //try and create the save file
        try {
            //create a new empty file in the corect location
            File myObj = new File(saveAddress);
            //check if there is already a save file there and if it would be overwitten
            if (myObj.createNewFile()) {
                //if not inform of the success
                JOptionPane.showMessageDialog(null, "File created: " + myObj.getName(), "Save Success", JOptionPane.INFORMATION_MESSAGE);
                //then write the actual save data to the file
                success = writeToFile(saveAddress);
            } else { //ask the user if they want to save anyway
                int overwrite;
                overwrite = JOptionPane.showConfirmDialog(null, "File already exists.\nWould you like to overwrite it?", "Confim", 0, JOptionPane.ERROR_MESSAGE);
                //If they do want to overwrite it do so
                if (overwrite == 0) {
                    success = writeToFile(saveAddress);
                    JOptionPane.showMessageDialog(null, "File overwritten: " + myObj.getName(), "Save Success", JOptionPane.INFORMATION_MESSAGE);
                }
            }

            myObj.setReadOnly();
            myObj.setExecutable(true);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "An error occurred while trying to save the game.", "Saving Error", JOptionPane.ERROR_MESSAGE);
            success = false;
        }

        return success;
    }

    /**
     * Write to the save file
     *
     * @param writeAdress
     * @return
     * @throws FileNotFoundException
     */
    public boolean writeToFile(String writeAdress) throws FileNotFoundException {

        try {
            //try to create the file
            File file = new File(writeAdress);
            //ensure it is mutable
            file.setExecutable(true);
            file.setWritable(true);

            PrintWriter saveFile = new PrintWriter(file); //begin writting to the file
            saveFile.println("SettlersOfCatanSave" + Catan.SAVE_FILE_VER); //write a header to easily identify Settlers of Catan save files for loading
            saveFile.println("playerCount:");
            saveFile.println(playerCount);
            saveFile.println("thiefMoveCounter:");
            saveFile.println(thiefMoveCounter);
            saveFile.println("tileWithThief:");
            saveFile.println(tileWithThief);
            saveFile.println("victoryPointsToWin:");
            saveFile.println(victoryPointsToWin);
            saveFile.println("currentPlayer:");
            saveFile.println(currentPlayer);
            saveFile.println("giveStartingResources:");
            saveFile.println(giveStartingResources);
            saveFile.println("doSnakeRules:");
            saveFile.println(doSnakeRules);
            saveFile.println("setupTurnOrderIndex:");
            saveFile.println(setupTurnOrderIndex);
            saveFile.println("inSetup:");
            saveFile.println(inSetup);
            saveFile.println("inbetweenTurns:");
            saveFile.println(inbetweenTurns);
            saveFile.println("setupRoundsLeft:");
            saveFile.println(setupRoundsLeft);
            saveFile.println("newestSetupSettlmentRefNum:");
            //check if there is a newest settelment
            if (newestSetupSettlment != null) {
                saveFile.println(newestSetupSettlment.getRefNum());
            } else {
                saveFile.println("null");
            }
            saveFile.println("playerSetupRoadsLeft:");
            saveFile.println(playerSetupRoadsLeft);
            saveFile.println("playerSetupSettlementLeft:");
            saveFile.println(playerSetupSettlementLeft);

            saveFile.println("longestRoadData length:");
            saveFile.println(longestRoadData.getSize());
            saveFile.println("longestRoadData playerNum:");
            saveFile.println(longestRoadData.getPlayerNum());

            saveFile.println("largestArmyData size:");
            saveFile.println(largestArmyData.getSize());
            saveFile.println("largestArmyData playerNum:");
            saveFile.println(largestArmyData.getPlayerNum());

            saveFile.println("setupTurnOrder:");
            saveFile.println("length:");
            saveFile.println(setupTurnOrder.length);
            saveFile.println("order:");
            for (int i = 0; i < setupTurnOrder.length; i++) {
                saveFile.println(setupTurnOrder[i]);
            }

            saveFile.println();

            saveFile.println("Total cards collected:");
            for (int i = 0; i < totalCardsCollected.length; i++) {
                saveFile.println(totalCardsCollected[i]);
            }

            saveFile.println();

            saveFile.println("victoryPoints:");
            for (int i = 1; i < victoryPoints.length; i++) {
                saveFile.println(victoryPoints[i]);
            }

            saveFile.println("playerArmySize:");
            for (int i = 1; i < playerArmySize.length; i++) {
                saveFile.println(playerArmySize[i]);
            }

            saveFile.println();

            //add the card data
            saveFile.println("Cards:");
            for (int i = 1; i < cards.length; i++) {
                saveFile.println("Player: " + i);
                saveFile.println("size:");
                saveFile.println(cards[i].size());
                saveFile.println("cards:");
                for (int j = 0; j < cards[i].size(); j++) {
                    saveFile.println(cards[i].get(j));
                }
                saveFile.println();
            }

            //Add the tile data
            saveFile.println("Tiles:");
            for (int i = 0; i < tiles.size() - 1; i++) { //loop thorugh all the tiles and add it to the save file. Ignore the last null tile
                saveFile.println("Tile number:");
                saveFile.println(i);
                saveFile.println("Type:");
                saveFile.println(tiles.get(i).getType());
                saveFile.println("Has Thief:");
                saveFile.println(tiles.get(i).hasThief());
                saveFile.println("Harvesting Dice Roll:");
                saveFile.println(tiles.get(i).getHarvestRollNum());
                saveFile.println(); //add a line break below
            }

            //Add the road node data
            saveFile.println("NodeRoads:");
            for (int i = 0; i < roadNodes.size() - 1; i++) {
                saveFile.println("Road node number:");
                saveFile.println(i);
                saveFile.println("Player ID:");
                saveFile.println(roadNodes.get(i).getPlayer());
                saveFile.println(); //add a line break below
            }

            //Add the settlement node data
            saveFile.println("NodeSettlements:");
            for (int i = 0; i < settlementNodes.size(); i++) {
                saveFile.println("Settlement node number:");
                saveFile.println(i);
                saveFile.println("Player ID:");
                saveFile.println(settlementNodes.get(i).getPlayer());
                saveFile.println("Is Large:");
                saveFile.println(settlementNodes.get(i).isLarge());
                //add the age
                saveFile.println("Age:");
                saveFile.println(settlementNodes.get(i).getAge());
                saveFile.println(); //add a line break below
            }

            //add the Port data
            saveFile.println("Ports:");
            for (int i = 0; i < ports.size(); i++) { //loop thorugh all the ports and add it to the save file.
                saveFile.println("Port number:");
                saveFile.println(i);
                saveFile.println("LinkedTile:");
                saveFile.println(ports.get(i).getLinkedTile().getRefNum());
                saveFile.println("Orientation:");
                saveFile.println(ports.get(i).getOrientation());
                saveFile.println("Type:");
                saveFile.println(ports.get(i).getType());
                saveFile.println(); //add a line break below
            }

            //add the playerHasPort data
            saveFile.println("playerHasPort:");
            for (int i = 1; i < playerHasPort.length; i++) {
                saveFile.println("Player: " + (i));
                saveFile.println("hasPorts:");
                for (int j = 0; j < playerHasPort[i].length; j++) {
                    saveFile.println(playerHasPort[i][j]);
                }
                saveFile.println();
            }

            //add the userPlayedDevCard boolean
            saveFile.println("userPlayedDevCard:");
            saveFile.println(userPlayedDevCard);

            //add the usingDevCard int
            saveFile.println("usingDevCard:");
            saveFile.println(usingDevCard);
            saveFile.println(); //add line break

            //add the dev card data
            saveFile.println("Dev Cards:");
            for (int i = 1; i < devCards.length; i++) {
                saveFile.println("Player: " + i);
                saveFile.println("size:");
                saveFile.println(devCards[i].size());
                saveFile.println("dev cards:");
                for (int j = 0; j < devCards[i].size(); j++) {
                    saveFile.println(devCards[i].get(j));
                }
                saveFile.println();
            }

            //available dev card data
            saveFile.println("availableDevCards:");
            saveFile.println("size:");
            saveFile.println(availableDevCards.size());
            saveFile.println("available cards:");
            for (int i = 0; i < availableDevCards.size(); i++) {
                saveFile.println(availableDevCards.get(i));
            }

            //add all the show hitbox booleans
            saveFile.println("showRoadHitbox:");
            saveFile.println(showRoadHitbox);
            saveFile.println("showCardHitbox:");
            saveFile.println(showCardHitbox);
            saveFile.println("showDevCardHitbox:");
            saveFile.println(showDevCardHitbox);
            saveFile.println("showSettlementHitbox:");
            saveFile.println(showSettlementHitbox);
            saveFile.println("showTileHitbox:");
            saveFile.println(showTileHitbox);
            saveFile.println("showPortHitbox:");
            saveFile.println(showResStackHitbox);
            saveFile.println("showSubPlayerHitbox:");
            saveFile.println(showSubPlayerHitbox);

            //show the dev cards
            saveFile.println("showDevCards:");
            saveFile.println(showDevCards);

            //save the instructions
            saveFile.println("instructionLbl text:");
            saveFile.println(instructionLbl.getText());
            saveFile.println("subInstructionLbl text:");
            saveFile.println(subInstructionLbl.getText());

            //save the button states
            //turn switch
            saveFile.println("turnSwitchBtn.isEnabled");
            saveFile.println(turnSwitchBtn.isEnabled());
            //Settler buttons
            //toggleCardBtn
            saveFile.println("toggleCardBtn.getEnabled");
            saveFile.println(toggleCardBtn.isEnabled());
            saveFile.println("toggleCardBtn.getMode");
            saveFile.println(toggleCardBtn.getMode());
            //buyDevCardBtn
            saveFile.println("buyDevCardBtn.getEnabled");
            saveFile.println(buyDevCardBtn.isEnabled());
            //useDevCardBtn
            saveFile.println("useDevCardBtn.getEnabled");
            saveFile.println(useDevCardBtn.isEnabled());
            saveFile.println("useDevCardBtn.getMode");
            saveFile.println(useDevCardBtn.getMode());

            saveFile.println();//add linebreak

            //save the players that can be stolen from
            saveFile.println("canStealCardPlayers:");
            saveFile.println("size:");
            saveFile.println(canStealCardPlayers.size());
            saveFile.println("players:");
            for (Integer playerNum : canStealCardPlayers) {
                saveFile.println(playerNum);
            }

            saveFile.println();//add linebreak

            //save the building object
            saveFile.println("buildingObject:");
            saveFile.println(buildingObject);
            //save tradingMode
            saveFile.println("tradingMode:");
            saveFile.println(tradingMode);
            //save tradeResource
            saveFile.println("tradeResource:");
            saveFile.println(tradeResource);
            //save the minTradeCardsNeeded
            saveFile.println("minTradeCardsNeeded:");
            saveFile.println(minTradeCardsNeeded);

            //save the vars for thief stealing
            //save the thiefIsStealing
            saveFile.println("thiefIsStealing:");
            saveFile.println(thiefIsStealing);
            //save the thiefJustFinished
            saveFile.println("thiefJustFinished:");
            saveFile.println(thiefJustFinished);
            //save the thiefJustStarted
            saveFile.println("thiefJustStarted:");
            saveFile.println(thiefJustStarted);
            //save the player who rolled a 7
            saveFile.println("playerRolled7:");
            saveFile.println(playerRolled7);

            saveFile.println();//add linebreak

            //save the playerTurnOrder
            saveFile.println("playerTurnOrder:");
            saveFile.println("size:");
            saveFile.println(playerTurnOrder.size());
            saveFile.println("order:");
            for (int i = 0; i < playerTurnOrder.size(); i++) {
                saveFile.println(playerTurnOrder.get(i));
            }

            //save the stealCardNum
            saveFile.println("stealCardNum:");
            saveFile.println("size:");
            saveFile.println(stealCardNum.length);
            saveFile.println("players:");
            for (int i = 0; i < stealCardNum.length; i++) {
                saveFile.println(stealCardNum[i]);
            }

            //save the dice roll
            saveFile.println("diceRollVals:");
            saveFile.println("roll-1:");
            saveFile.println(diceRollVal[0]);
            saveFile.println("roll-2:");
            saveFile.println(diceRollVal[1]);
            saveFile.println("total:");
            saveFile.println(diceRollVal[2]);

            //add the close
            saveFile.close();
            return true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "The game is not able to save at this time. Invalid state\n" + e, "Saving Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /**
     * Load a game state from a data file
     *
     * @param filePathString
     * @return if the operation was successful
     */
    public boolean load(String filePathString) {
        //System.out.println("Yupp");

        int tempScannerVal;
        boolean thrownLoadError = false;

        //load the save file 
        try {
            File savefile = new File(filePathString);

            //ensure the correct permisions
            savefile.setExecutable(true);
            savefile.setWritable(true);
            savefile.setReadable(true);

            Scanner scanner = new Scanner(savefile);

            //check if it is valid (again)
            if (scanner.nextLine().equals("SettlersOfCatanSave" + Catan.SAVE_FILE_VER)) {
                //System.out.println("Yuppers");
            } else {
                thrownLoadError = throwLoadError(thrownLoadError);
            }

            if (scanner.nextLine().equals("playerCount:")) {
                playerCount = Integer.parseInt(scanner.nextLine());
                //System.out.println("Yuppers1");
            } else {
                thrownLoadError = throwLoadError(thrownLoadError);
            }

            if (scanner.nextLine().equals("thiefMoveCounter:")) {
                thiefMoveCounter = Integer.parseInt(scanner.nextLine());
                //System.out.println("Yuppers2");
            } else {
                thrownLoadError = throwLoadError(thrownLoadError);
            }

            if (scanner.nextLine().equals("tileWithThief:")) {
                tileWithThief = Integer.parseInt(scanner.nextLine());
                //System.out.println("Yuppers2.5");
            } else {
                thrownLoadError = throwLoadError(thrownLoadError);
            }

            if (scanner.nextLine().equals("victoryPointsToWin:")) {
                victoryPointsToWin = Integer.parseInt(scanner.nextLine());
                //System.out.println("Yuppers3");
            } else {
                thrownLoadError = throwLoadError(thrownLoadError);
            }

            if (scanner.nextLine().equals("currentPlayer:")) {
                currentPlayer = Integer.parseInt(scanner.nextLine());
                //System.out.println("Yuppers4");
            } else {
                thrownLoadError = throwLoadError(thrownLoadError);
            }

            if (scanner.nextLine().equals("giveStartingResources:")) {
                giveStartingResources = Boolean.parseBoolean(scanner.nextLine());
                //System.out.println("Yuppers4.5");
            } else {
                thrownLoadError = throwLoadError(thrownLoadError);
            }

            if (scanner.nextLine().equals("doSnakeRules:")) {
                doSnakeRules = Boolean.parseBoolean(scanner.nextLine());
                //System.out.println("Yuppers4.6");
            } else {
                thrownLoadError = throwLoadError(thrownLoadError);
            }

            if (scanner.nextLine().equals("setupTurnOrderIndex:")) {
                setupTurnOrderIndex = Integer.parseInt(scanner.nextLine());
                //System.out.println("Yuppers4.7");
            } else {
                thrownLoadError = throwLoadError(thrownLoadError);
            }

            if (scanner.nextLine().equals("inSetup:")) {
                inSetup = Boolean.parseBoolean(scanner.nextLine());
                //System.out.println("Yuppers5");
            } else {
                thrownLoadError = throwLoadError(thrownLoadError);
            }

            if (scanner.nextLine().equals("inbetweenTurns:")) {
                inbetweenTurns = Boolean.parseBoolean(scanner.nextLine());
                //System.out.println("Yuppers5");
            } else {
                thrownLoadError = throwLoadError(thrownLoadError);
            }

            if (scanner.nextLine().equals("setupRoundsLeft:")) {
                setupRoundsLeft = Integer.parseInt(scanner.nextLine());
                //System.out.println("YuppersSetupRounds");
            } else {
                thrownLoadError = throwLoadError(thrownLoadError);
            }

            if (scanner.nextLine().equals("newestSetupSettlmentRefNum:")) {
                //read in the line
                String newestSetupSettlmentRefNum = scanner.nextLine();
                //check if it is null of an int
                if (!newestSetupSettlmentRefNum.equals("null")) {
                    newestSetupSettlment = settlementNodes.get(Integer.parseInt(newestSetupSettlmentRefNum));
                    //System.out.println("YuppersNewestSetupSettlment");
                } else {
                    newestSetupSettlment = null;
                }
            } else {
                thrownLoadError = throwLoadError(thrownLoadError);
            }

            if (scanner.nextLine().equals("playerSetupRoadsLeft:")) {
                playerSetupRoadsLeft = Integer.parseInt(scanner.nextLine());
                //System.out.println("Yuppers6");
            } else {
                thrownLoadError = throwLoadError(thrownLoadError);
            }

            if (scanner.nextLine().equals("playerSetupSettlementLeft:")) {
                playerSetupSettlementLeft = Integer.parseInt(scanner.nextLine());
                //System.out.println("Yuppers7");
            } else {
                thrownLoadError = throwLoadError(thrownLoadError);
            }

            if (scanner.nextLine().equals("longestRoadData length:")) {
                longestRoadData.setSize(Integer.parseInt(scanner.nextLine()));
                //System.out.println("Yuppers7.1");
            } else {
                thrownLoadError = throwLoadError(thrownLoadError);
            }

            if (scanner.nextLine().equals("longestRoadData playerNum:")) {
                longestRoadData.setPlayerNum(Integer.parseInt(scanner.nextLine()));
                //System.out.println("Yuppers7.2");
            } else {
                thrownLoadError = throwLoadError(thrownLoadError);
            }

            //load in the data for largest army
            if (scanner.nextLine().equals("largestArmyData size:")) {
                largestArmyData.setSize(Integer.parseInt(scanner.nextLine()));
                //System.out.println("Yuppers7.3");
            } else {
                thrownLoadError = throwLoadError(thrownLoadError);
            }

            if (scanner.nextLine().equals("largestArmyData playerNum:")) {
                largestArmyData.setPlayerNum(Integer.parseInt(scanner.nextLine()));
                //System.out.println("Yuppers7.4");
            } else {
                thrownLoadError = throwLoadError(thrownLoadError);
            }

            if (scanner.nextLine().equals("setupTurnOrder:") && scanner.nextLine().equals("length:")) {
                //System.out.println("Yuppers7.5");

                int length = Integer.parseInt(scanner.nextLine());

                if (scanner.nextLine().equals("order:")) {

                    for (int i = 0; i < length; i++) {
                        setupTurnOrder[i] = Integer.parseInt(scanner.nextLine());
                    }

                }

            } else {
                thrownLoadError = throwLoadError(thrownLoadError);
            }

            scanner.nextLine();

            //get the total cards collected
            if (scanner.nextLine().equals("Total cards collected:")) {

                for (int i = 0; i < 5; i++) {
                    totalCardsCollected[i] = Integer.parseInt(scanner.nextLine());
                }

                //System.out.println("Yuppers8");
            } else {
                thrownLoadError = throwLoadError(thrownLoadError);
            }

            //get the VP collected
            //but first skip a line
            scanner.nextLine();
            if (scanner.nextLine().equals("victoryPoints:")) {

                for (int i = 0; i < playerCount; i++) {
                    victoryPoints[i + 1] = Integer.parseInt(scanner.nextLine());
                }

                //System.out.println("Yuppers8");
            } else {
                thrownLoadError = throwLoadError(thrownLoadError);
            }

            //get the playerArmySize
            if (scanner.nextLine().equals("playerArmySize:")) {

                for (int i = 0; i < playerCount; i++) {
                    playerArmySize[i + 1] = Integer.parseInt(scanner.nextLine());
                }

                //System.out.println("Yuppers8");
            } else {
                thrownLoadError = throwLoadError(thrownLoadError);
            }

            //get the resource cards collected
            //but first skip a line
            scanner.nextLine();
            if (scanner.nextLine().equals("Cards:")) {

                //System.out.println("Yuppers10");
                for (int i = 1; i < playerCount + 1; i++) {
                    if (scanner.nextLine().equals("Player: " + (i))) {
                        //System.out.println("Yuppers10.1");
                    } else {
                        thrownLoadError = throwLoadError(thrownLoadError);
                    }

                    if (scanner.nextLine().equals("size:")) {
                        tempScannerVal = Integer.parseInt(scanner.nextLine());
                        //System.out.println("Yuppers10.2");

                        //clear the arrayList
                        cards[i].clear();

                        if (scanner.nextLine().equals("cards:")) {

                            for (int j = 0; j < tempScannerVal; j++) {
                                cards[i].add(Integer.parseInt(scanner.nextLine()));
                            }

                            //skip a line
                            scanner.nextLine();

                            //System.out.println("Yuppers10.3");
                        } else {
                            thrownLoadError = throwLoadError(thrownLoadError);
                            //System.out.println("Its me");
                        }

                    } else {
                        thrownLoadError = throwLoadError(thrownLoadError);
                        //System.out.println("no me");
                    }

                }

            } else {
                thrownLoadError = throwLoadError(thrownLoadError);
            }

            if (scanner.nextLine().equals("Tiles:")) {
                //System.out.println("Yuppers11");

                int tileNum = 0;

                //loop through all the tiles
                for (int i = 0; i < 19; i++) {
                    if (scanner.nextLine().equals("Tile number:")) {
                        tileNum = Integer.parseInt(scanner.nextLine());
                    } else {
                        thrownLoadError = throwLoadError(thrownLoadError);
                    }

                    if (scanner.nextLine().equals("Type:")) {
                        tiles.get(tileNum).setType(Integer.parseInt(scanner.nextLine()));
                    } else {
                        thrownLoadError = throwLoadError(thrownLoadError);
                    }

                    if (scanner.nextLine().equals("Has Thief:")) {
                        tiles.get(tileNum).setThief(Boolean.parseBoolean(scanner.nextLine()));
                    } else {
                        thrownLoadError = throwLoadError(thrownLoadError);
                    }

                    if (scanner.nextLine().equals("Harvesting Dice Roll:")) {
                        tiles.get(tileNum).setHarvestRollNum(Integer.parseInt(scanner.nextLine()));
                    } else {
                        thrownLoadError = throwLoadError(thrownLoadError);
                    }

                    //skip a line
                    scanner.nextLine();
                }

            } else {
                thrownLoadError = throwLoadError(thrownLoadError);
            }

            if (scanner.nextLine().equals("NodeRoads:")) {
                //System.out.println("Yuppers12");

                int roadNodeNum = 0;

                //loop through all the tiles
                for (int i = 0; i < 72; i++) {
                    if (scanner.nextLine().equals("Road node number:")) {
                        roadNodeNum = Integer.parseInt(scanner.nextLine());
                    } else {
                        thrownLoadError = throwLoadError(thrownLoadError);
                    }

                    if (scanner.nextLine().equals("Player ID:")) {
                        roadNodes.get(roadNodeNum).setPlayer(Integer.parseInt(scanner.nextLine()));
                    } else {
                        thrownLoadError = throwLoadError(thrownLoadError);
                    }

                    //skip a line
                    scanner.nextLine();
                }

            } else {
                thrownLoadError = throwLoadError(thrownLoadError);
            }

            if (scanner.nextLine().equals("NodeSettlements:")) {
                //System.out.println("Yuppers13");

                int settlementNodeNum = 0;

                //loop through all the tiles
                for (int i = 0; i < 54; i++) {
                    if (scanner.nextLine().equals("Settlement node number:")) {
                        settlementNodeNum = Integer.parseInt(scanner.nextLine());
                    } else {
                        thrownLoadError = throwLoadError(thrownLoadError);
                    }

                    if (scanner.nextLine().equals("Player ID:")) {
                        settlementNodes.get(settlementNodeNum).setPlayer(Integer.parseInt(scanner.nextLine()));
                    } else {
                        thrownLoadError = throwLoadError(thrownLoadError);
                    }

                    if (scanner.nextLine().equals("Is Large:")) {
                        settlementNodes.get(settlementNodeNum).setLarge(Boolean.parseBoolean(scanner.nextLine()));
                    } else {
                        thrownLoadError = throwLoadError(thrownLoadError);
                    }

                    if (scanner.nextLine().equals("Age:")) {
                        settlementNodes.get(settlementNodeNum).setAge(Integer.parseInt(scanner.nextLine()));
                    } else {
                        thrownLoadError = throwLoadError(thrownLoadError);
                    }

                    //skip a line
                    scanner.nextLine();
                }

            } else {
                thrownLoadError = throwLoadError(thrownLoadError);
            }

            if (scanner.nextLine().equals("Ports:")) {
                //System.out.println("Yuppers13");

                int portNum = 0;

                //loop through all the ports
                for (int i = 0; i < ports.size(); i++) {
                    if (scanner.nextLine().equals("Port number:")) {
                        portNum = Integer.parseInt(scanner.nextLine());
                    } else {
                        thrownLoadError = throwLoadError(thrownLoadError);
                    }

                    if (scanner.nextLine().equals("LinkedTile:")) {
                        ports.get(portNum).setLinkedTile(tiles.get(Integer.parseInt(scanner.nextLine())));
                    } else {
                        thrownLoadError = throwLoadError(thrownLoadError);
                    }

                    if (scanner.nextLine().equals("Orientation:")) {
                        ports.get(portNum).setOrientation(Integer.parseInt(scanner.nextLine()));
                    } else {
                        thrownLoadError = throwLoadError(thrownLoadError);
                    }

                    if (scanner.nextLine().equals("Type:")) {
                        ports.get(portNum).setType(Integer.parseInt(scanner.nextLine()));
                    } else {
                        thrownLoadError = throwLoadError(thrownLoadError);
                    }

                    //skip a line
                    scanner.nextLine();

                    //make sure the coordiantes are up to date. (80% sure this is redundant)
                    ports.get(portNum).applyCoordinates();
                    ports.get(portNum).applyTypeImageCoordinates();
                }

            } else {
                thrownLoadError = throwLoadError(thrownLoadError);
            }

            //get the playerHasPort data
            if (scanner.nextLine().equals("playerHasPort:")) {

                //System.out.println("Yuppers14");
                for (int i = 1; i < playerCount + 1; i++) {

                    if (scanner.nextLine().equals("Player: " + (i))) {
                        //System.out.println("Yuppers10.1");
                    } else {
                        thrownLoadError = throwLoadError(thrownLoadError);
                    }

                    if (scanner.nextLine().equals("hasPorts:")) {

                        for (int j = 0; j < playerHasPort[i].length; j++) {
                            playerHasPort[i][j] = Boolean.parseBoolean(scanner.nextLine());
                        }

                        //skip a line
                        scanner.nextLine();

                        //System.out.println("Yuppers10.3");
                    } else {
                        thrownLoadError = throwLoadError(thrownLoadError);
                        //System.out.println("uh oh");
                    }

                }

            } else {
                thrownLoadError = throwLoadError(thrownLoadError);
            }

            //get the userPlayedDevCard data
            if (scanner.nextLine().equals("userPlayedDevCard:")) {
                //System.out.println("Yes");
                userPlayedDevCard = Boolean.parseBoolean(scanner.nextLine());
            } else {
                thrownLoadError = throwLoadError(thrownLoadError);
            }

            //get the usingDevCard data
            if (scanner.nextLine().equals("usingDevCard:")) {
                usingDevCard = Integer.parseInt(scanner.nextLine());
            } else {
                thrownLoadError = throwLoadError(thrownLoadError);
            }

            //get the dev cards owned
            //but first skip a line
            scanner.nextLine();
            if (scanner.nextLine().equals("Dev Cards:")) {

                //System.out.println("Yuppers10");
                for (int i = 1; i < playerCount + 1; i++) {
                    if (scanner.nextLine().equals("Player: " + (i))) {
                        //System.out.println("Yuppers10.1");
                    } else {
                        thrownLoadError = throwLoadError(thrownLoadError);
                    }

                    if (scanner.nextLine().equals("size:")) {
                        tempScannerVal = Integer.parseInt(scanner.nextLine());
                        //System.out.println("Yuppers10.2");

                        //clear the arrayList
                        devCards[i].clear();

                        if (scanner.nextLine().equals("dev cards:")) {

                            for (int j = 0; j < tempScannerVal; j++) {
                                devCards[i].add(Integer.parseInt(scanner.nextLine()));
                            }

                            //skip a line
                            scanner.nextLine();

                            //System.out.println("Yuppers10.3");
                        } else {
                            thrownLoadError = throwLoadError(thrownLoadError);
                            //System.out.println("Its me");
                        }

                    } else {
                        thrownLoadError = throwLoadError(thrownLoadError);
                        //System.out.println("no me");
                    }

                }

            } else {
                thrownLoadError = throwLoadError(thrownLoadError);
            }

            //get the availableDevCards data
            if (scanner.nextLine().equals("availableDevCards:")) {

                if (scanner.nextLine().equals("size:")) {

                    tempScannerVal = Integer.parseInt(scanner.nextLine());

                    //remove any cards in there
                    availableDevCards.clear();

                    if (scanner.nextLine().equals("available cards:")) {

                        //then add back the ones that are in the save file
                        for (int i = 0; i < tempScannerVal; i++) {
                            availableDevCards.add(Integer.parseInt(scanner.nextLine()));
                        }
                    } else {
                        thrownLoadError = throwLoadError(thrownLoadError);
                    }
                } else {
                    thrownLoadError = throwLoadError(thrownLoadError);
                }

            } else {
                thrownLoadError = throwLoadError(thrownLoadError);
            }

            //load the hitbox data
            //get the showRoadHitbox data
            if (scanner.nextLine().equals("showRoadHitbox:")) {
                //System.out.println("Yes1");
                showRoadHitbox = Boolean.parseBoolean(scanner.nextLine());
            } else {
                thrownLoadError = throwLoadError(thrownLoadError);
            }
            //get the showCardHitbox data
            if (scanner.nextLine().equals("showCardHitbox:")) {
                //System.out.println("Yes2");
                showCardHitbox = Boolean.parseBoolean(scanner.nextLine());
            } else {
                thrownLoadError = throwLoadError(thrownLoadError);
            }
            //get the showDevCardHitbox data
            if (scanner.nextLine().equals("showDevCardHitbox:")) {
                //System.out.println("Yes3");
                showDevCardHitbox = Boolean.parseBoolean(scanner.nextLine());
            } else {
                thrownLoadError = throwLoadError(thrownLoadError);
            }
            //get the showSettlementHitbox data
            if (scanner.nextLine().equals("showSettlementHitbox:")) {
                //System.out.println("Yes4");
                showSettlementHitbox = Boolean.parseBoolean(scanner.nextLine());
            } else {
                thrownLoadError = throwLoadError(thrownLoadError);
            }
            //get the showTileHitbox data
            if (scanner.nextLine().equals("showTileHitbox:")) {
                //System.out.println("Yes");
                showTileHitbox = Boolean.parseBoolean(scanner.nextLine());
            } else {
                thrownLoadError = throwLoadError(thrownLoadError);
            }
            //get the showPortHitbox data
            if (scanner.nextLine().equals("showPortHitbox:")) {
                //System.out.println("Yes");
                showResStackHitbox = Boolean.parseBoolean(scanner.nextLine());
            } else {
                thrownLoadError = throwLoadError(thrownLoadError);
            }
            //get the showSubPlayerHitbox data
            if (scanner.nextLine().equals("showSubPlayerHitbox:")) {
                //System.out.println("Yes");
                showSubPlayerHitbox = Boolean.parseBoolean(scanner.nextLine());
            } else {
                thrownLoadError = throwLoadError(thrownLoadError);
            }

            //get the showDevCards data
            //but only update the cards if in offline mode because the game will always show the online users cards if in online mode
            if (scanner.nextLine().equals("showDevCards:")) {
                //System.out.println("Yes");
                if (onlineMode == -1) {
                    showDevCards = Boolean.parseBoolean(scanner.nextLine());
                } else {
                    //skip a line
                    scanner.nextLine();
                }
            } else {
                thrownLoadError = throwLoadError(thrownLoadError);
            }

            //get the instructions
            //main
            if (scanner.nextLine().equals("instructionLbl text:")) {
                //System.out.println("Yes");
                instructionLbl.setText(scanner.nextLine());
            } else {
                thrownLoadError = throwLoadError(thrownLoadError);
            }
            //sub
            if (scanner.nextLine().equals("subInstructionLbl text:")) {
                //System.out.println("Yes");
                subInstructionLbl.setText(scanner.nextLine());
            } else {
                thrownLoadError = throwLoadError(thrownLoadError);
            }

            //get the button states
            //turn switch btn
            if (scanner.nextLine().equals("turnSwitchBtn.isEnabled")) {
                //System.out.println("Got it");
                turnSwitchBtn.setEnabled(Boolean.parseBoolean(scanner.nextLine()));
            } else {
                thrownLoadError = throwLoadError(thrownLoadError);
            }

            //SettlerBtns
            //toggleCardBtn
            if (scanner.nextLine().equals("toggleCardBtn.getEnabled")) {
                //System.out.println("Got it");

                if (onlineMode == -1) {

                    toggleCardBtn.setEnabled(Boolean.parseBoolean(scanner.nextLine()));

                } else {
                    //skip a line
                    scanner.nextLine();
                }
            } else {
                thrownLoadError = throwLoadError(thrownLoadError);
            }
            if (scanner.nextLine().equals("toggleCardBtn.getMode")) {
                //System.out.println("Got it");

                if (onlineMode == -1) {

                    toggleCardBtn.setMode(Integer.parseInt(scanner.nextLine()));
                } else {
                    //skip a line
                    scanner.nextLine();
                }
            } else {
                thrownLoadError = throwLoadError(thrownLoadError);
            }
            //buyDevCardBtn
            if (scanner.nextLine().equals("buyDevCardBtn.getEnabled")) {
                //System.out.println("Got it");
                buyDevCardBtn.setEnabled(Boolean.parseBoolean(scanner.nextLine()));
            } else {
                thrownLoadError = throwLoadError(thrownLoadError);
            }
            //useDevCardBtn
            if (scanner.nextLine().equals("useDevCardBtn.getEnabled")) {
                //System.out.println("Got it");
                useDevCardBtn.setEnabled(Boolean.parseBoolean(scanner.nextLine()));
            } else {
                thrownLoadError = throwLoadError(thrownLoadError);
            }
            if (scanner.nextLine().equals("useDevCardBtn.getMode")) {
                //System.out.println("Got it");
                useDevCardBtn.setMode(Integer.parseInt(scanner.nextLine()));
            } else {
                thrownLoadError = throwLoadError(thrownLoadError);
            }

            //skip line
            scanner.nextLine();

            //load in the players that can be stolen from
            if (scanner.nextLine().equals("canStealCardPlayers:")) {

                //clear the arrayList
                canStealCardPlayers.clear();

                if (scanner.nextLine().equals("size:")) {

                    tempScannerVal = Integer.parseInt(scanner.nextLine());

                    if (scanner.nextLine().equals("players:")) {

                        for (int i = 0; i < tempScannerVal; i++) {
                            canStealCardPlayers.add(Integer.parseInt(scanner.nextLine()));
                        }

                    } else {
                        thrownLoadError = throwLoadError(thrownLoadError);
                    }

                } else {
                    thrownLoadError = throwLoadError(thrownLoadError);
                }

            } else {
                thrownLoadError = throwLoadError(thrownLoadError);
            }

            //skip line
            scanner.nextLine();

            //get the buildingObject
            if (scanner.nextLine().equals("buildingObject:")) {
                //System.out.println("Got it");
                buildingObject = Integer.parseInt(scanner.nextLine());
            } else {
                thrownLoadError = throwLoadError(thrownLoadError);
            }

            //get the tradingMode
            if (scanner.nextLine().equals("tradingMode:")) {
                //System.out.println("Got it");
                tradingMode = Integer.parseInt(scanner.nextLine());
            } else {
                thrownLoadError = throwLoadError(thrownLoadError);
            }

            //get the tradeResource
            if (scanner.nextLine().equals("tradeResource:")) {
                //System.out.println("Got it");
                tradeResource = Integer.parseInt(scanner.nextLine());
            } else {
                thrownLoadError = throwLoadError(thrownLoadError);
            }

            //get the minTradeCardsNeeded
            if (scanner.nextLine().equals("minTradeCardsNeeded:")) {
                //System.out.println("Got it");
                minTradeCardsNeeded = Integer.parseInt(scanner.nextLine());
            } else {
                thrownLoadError = throwLoadError(thrownLoadError);
            }

            //get the thiefIsStealing
            if (scanner.nextLine().equals("thiefIsStealing:")) {
                //System.out.println("Got it1");
                thiefIsStealing = Boolean.parseBoolean(scanner.nextLine());
            } else {
                thrownLoadError = throwLoadError(thrownLoadError);
            }

            //get the thiefJustFinished
            if (scanner.nextLine().equals("thiefJustFinished:")) {
                //System.out.println("Got it2");
                thiefJustFinished = Boolean.parseBoolean(scanner.nextLine());
            } else {
                thrownLoadError = throwLoadError(thrownLoadError);
            }

            //get the thiefJustStarted
            if (scanner.nextLine().equals("thiefJustStarted:")) {
                //System.out.println("Got it3");
                thiefJustStarted = Boolean.parseBoolean(scanner.nextLine());
            } else {
                thrownLoadError = throwLoadError(thrownLoadError);
            }

            //get the playerRolled7
            if (scanner.nextLine().equals("playerRolled7:")) {
                //System.out.println("Got it4");
                playerRolled7 = Integer.parseInt(scanner.nextLine());
            } else {
                thrownLoadError = throwLoadError(thrownLoadError);
            }

            //skip line
            scanner.nextLine();

            //load in the playerTurnOrder
            if (scanner.nextLine().equals("playerTurnOrder:")) {

                //clear the current order
                playerTurnOrder.clear();

                if (scanner.nextLine().equals("size:")) {

                    tempScannerVal = Integer.parseInt(scanner.nextLine());

                    if (scanner.nextLine().equals("order:")) {

                        for (int i = 0; i < tempScannerVal; i++) {
                            playerTurnOrder.add(Integer.parseInt(scanner.nextLine()));
                        }

                    } else {
                        thrownLoadError = throwLoadError(thrownLoadError);
                    }

                } else {
                    thrownLoadError = throwLoadError(thrownLoadError);
                }

            } else {
                thrownLoadError = throwLoadError(thrownLoadError);
            }

            //load in the stealCardNum
            if (scanner.nextLine().equals("stealCardNum:")) {

                if (scanner.nextLine().equals("size:")) {

                    tempScannerVal = Integer.parseInt(scanner.nextLine());

                    if (scanner.nextLine().equals("players:")) {

                        for (int i = 0; i < tempScannerVal; i++) {
                            stealCardNum[i] = Integer.parseInt(scanner.nextLine());
                        }

                    } else {
                        thrownLoadError = throwLoadError(thrownLoadError);
                    }

                } else {
                    thrownLoadError = throwLoadError(thrownLoadError);
                }

            } else {
                thrownLoadError = throwLoadError(thrownLoadError);
            }

            //load in the dice roll values
            if (scanner.nextLine().equals("diceRollVals:")) {

                if (scanner.nextLine().equals("roll-1:")) {

                    diceRollVal[0] = scanner.nextLine();

                } else {
                    thrownLoadError = throwLoadError(thrownLoadError);
                }

                if (scanner.nextLine().equals("roll-2:")) {

                    diceRollVal[1] = scanner.nextLine();

                } else {
                    thrownLoadError = throwLoadError(thrownLoadError);
                }

                if (scanner.nextLine().equals("total:")) {

                    diceRollVal[2] = scanner.nextLine();

                } else {
                    thrownLoadError = throwLoadError(thrownLoadError);
                }

            } else {
                thrownLoadError = throwLoadError(thrownLoadError);
            }

            /*
            //save the dice roll
            saveFile.println("diceRollVals:");
            saveFile.println("roll-1:");
            saveFile.println(diceRollVal[0]);
            saveFile.println("roll-2:");
            saveFile.println(diceRollVal[1]);
            saveFile.println("total:");
            saveFile.println(diceRollVal[2]);
             */
            //close the scanner
            scanner.close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "There was an error handling the save file.\nError: " + e, "Loading Error", JOptionPane.ERROR_MESSAGE);
        }

        //reset the trade and build buttons if in online mode
        if (onlineMode != -1 && onlineMode != currentPlayer) {
            trade4to1Btn.setMode(1); //set to "Trade 4:1"
            trade3to1Btn.setMode(1); //set to "Trade 3:1"
            trade2to1Btn.setMode(1); //set to "Trade 2:1"
            buildBtn.setMode(1);//set it to "Build"
        }

        //update the instructions
        if (!inSetup) {
            // Set the instruction labels to tell the user they can build
            instructionLbl.setText("Use your cards to build roads or settlements");
            subInstructionLbl.setText("Or end your turn to continue the game");
        }

        //if doing snake rules update the sub plays to match what it's supposed to be
        if (doSnakeRules && inSetup) {
            //update the playerTurnOrder
            setupUpdatePlayerTurnOrder();
        }

        //sort the cards to ensure they have a good order
        quickSortCards(cards[currentPlayer], 0, cards[currentPlayer].size() - 1);

        //get the number of each card type the player has
        countCardTypes(cards[currentPlayer].size(), currentPlayer);
        countNumCardTypes(currentPlayer);

        //update the text of the Swing buttons
        //check if building
        if (buildingObject != 0) {
            buildBtn.setMode(0);//set it to "Cancel"
        }
        //check if trading
        if (tradingMode != 0) {
            //check which button needs to say cancel
            switch (tradingMode) {
                case 1:
                    trade4to1Btn.setMode(0); //set to "Cancel"
                    break;
                case 2:
                    trade3to1Btn.setMode(0); //set to "Cancel"
                    break;
                case 3:
                    trade2to1Btn.setMode(0); //set to "Cancel"
                    break;
                default:
                    break;
            }
        }

        //if it is inbetween turn make sure the button has the right text
        if (inbetweenTurns) {
            setTurnBtnTextStart();
        } else {
            setTurnBtbTextEnd();
        }

        //update the subPlayersHaveEnoughcards boolean
        if (canStealCardPlayers.size() > 0) {
            //does atleast one of the targetable sub players have more than one card to steal
            subPlayersHaveEnoughcards = false; //checks for this so that the game does not softlock when the current player need to choose but has 0 options

            for (int i = 0; i < canStealCardPlayers.size(); i++) {
                if (cards[canStealCardPlayers.get(i)].size() > 0) {
                    subPlayersHaveEnoughcards = true;
                }
            }
        }

        //update each player's road length
        for (int i = 1; i < playerCount + 1; i++) {

            //loop through all the roads
            for (NodeRoad road : roadNodes) {

                //check to not use the null road
                if (road != null) {

                    checkForLongestRoad(road, 0, i); //pass the road and the current branch length

                    //clear the array for checked roads
                    alreadyCheckedRoad.clear();
                    //also clear the settlments
                    alreadyCheckedSettlements.clear();
                }

            }
        }

        //play the turn beep if online and it's the user's turn
        if (onlineMode == currentPlayer) {
            AudioRef.playTurnBeep();
            //debug the sound
            //System.out.println("Played. Online Mode is: " + onlineMode);
        }

        //if in online mode also make the game visable
        if (onlineMode != -1) {
            superFrame.setVisible(true);
        }

        //enable the turn button (might be disabled again by updateBuildBtn method if online and not the correct player)
        turnBtnEnabled = true;

        //check for a wining game
        checkForWin();

        updateBuildButtons();
        repaint();

        return !thrownLoadError; //return the success of loading. If no error was thrown then the load was a success.
    }

    /**
     * Displays an error to the user about loading a bad file
     */
    private boolean throwLoadError(boolean hasThrownLoadError) {
        //check to see if a load error has been thrown
        if (!hasThrownLoadError) {
            JOptionPane.showMessageDialog(null, "Error loading the save file.\nThere may be file corruptions.", "Bad File", JOptionPane.ERROR_MESSAGE);
            hasThrownLoadError = true;
        }

        return hasThrownLoadError;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="updateBuildButtons()"> 
    /**
     * Update the state of the build buttons, enabling or disabling them based
     * on how many cards the current player has, or if the game is in setup, how
     * many setup buildings they have left to place
     */
    public void updateBuildButtons() {

        //reset the colour
        instructionLbl.setForeground(new java.awt.Color(255, 255, 225));
        //reset the font
        instructionLbl.setFont(new Font(timesNewRoman.getName(), timesNewRoman.getStyle(), (int) ((timesNewRoman.getSize() + 5) / scaleFactor)));

        boolean canBuildRoad; // If the user has enough cards to build these
        boolean canBuildSettlement;
        boolean canBuildCity;
        boolean canTrade4to;
        boolean canTrade3to;
        boolean canTrade2to;
        SettlerRadioBtn oldSelection; // The button selected before this update began

        //first check if the game is inbetween turns
        //of if it's online and the player is not the active one
        //in both cases turn everything off
        if (inbetweenTurns) {
            canBuildRoad = false;
            canBuildSettlement = false;
            canBuildCity = false;
            canTrade4to = false;
            canTrade3to = false;
            canTrade2to = false;

            //update the toggle card button to show the resource cards options for stealing
            toggleCardBtn.setEnabled(false);

            buyDevCardBtn.setEnabled(false);
            useDevCardBtn.setEnabled(false);

        } else if (onlineMode != -1 && onlineMode != currentPlayer) {
            canBuildRoad = false;
            canBuildSettlement = false;
            canBuildCity = false;
            canTrade4to = false;
            canTrade3to = false;
            canTrade2to = false;

            //update the toggle card button to show the resource cards options for stealing
            toggleCardBtn.setEnabled(true);

            buyDevCardBtn.setEnabled(false);
            useDevCardBtn.setEnabled(false);
        } // If the game is in setup
        else if (inSetup) {
            canBuildRoad = (playerSetupRoadsLeft > 0) && newestSetupSettlment != null;
            canBuildSettlement = (playerSetupSettlementLeft > 0);
            canBuildCity = false; // No settlement upgrades during setup
            canTrade4to = false;
            canTrade3to = false;
            canTrade2to = false;

            toggleCardBtn.setEnabled(true);
            buyDevCardBtn.setEnabled(false);
            useDevCardBtn.setEnabled(false);
        } //if the theif is stealing player's cards or the player is selecting another player to steal one card from.
        //or if a player is trading
        else if (thiefIsStealing || (thiefJustFinished && currentPlayer != playerRolled7) || canStealCardPlayers.size() > 0) {
            canBuildRoad = false;
            canBuildSettlement = false;
            canBuildCity = false;
            canTrade4to = false;
            canTrade3to = false;
            canTrade2to = false;

            //update the toggle card button to show the resource cards options for stealing
            toggleCardBtn.setEnabled(false);
            toggleCardBtn.setMode(0);
            showDevCards = false;

            buyDevCardBtn.setEnabled(false);
            useDevCardBtn.setEnabled(false);
        } //else if the player is currently trading
        else if (tradingMode != 0) {
            //diable all the building
            canBuildRoad = false;
            canBuildSettlement = false;
            canBuildCity = false;

            //update the toggle card button to show the resource cards for trading
            toggleCardBtn.setEnabled(false);
            toggleCardBtn.setMode(0);
            showDevCards = false;

            buyDevCardBtn.setEnabled(false);
            useDevCardBtn.setEnabled(false);
            //check the trading type
            switch (tradingMode) { //make sure the only button active is the current trade mode. This allows the user to cancel trading.
                case 1:
                    //if 4:1
                    canTrade4to = true;
                    canTrade3to = false;
                    canTrade2to = false;
                    break;
                case 2:
                    //if 3:1
                    canTrade4to = false;
                    canTrade3to = true;
                    canTrade2to = false;
                    break;
                case 3:
                    //if 2:1
                    canTrade4to = false;
                    canTrade3to = false;
                    canTrade2to = true;
                    break;
                default:
                    //if anything else
                    canTrade4to = false;
                    canTrade3to = false;
                    canTrade2to = false;
                    break;
            }

        } else if (usingDevCard > -1) { //if the game is in a mode for using dev cards
            // ^^^ -1 because that is the neutral/resting value. 0 is for when a card is being selected but its unkown which one
            canBuildRoad = (playerSetupRoadsLeft > 0); //if the player clicked the road building card allow them to build roads
            canBuildSettlement = false;
            canBuildCity = false;
            canTrade4to = false;
            canTrade3to = false;
            canTrade2to = false;

            //update the toggle card button to show the dev cards options for using
            toggleCardBtn.setEnabled(false);
            toggleCardBtn.setMode(1);
            showDevCards = true;

            buyDevCardBtn.setEnabled(false);
            //do not allow the user to cancel if they used a road building card
            //of if it's the knight card and the user is selecting a sub player
            useDevCardBtn.setEnabled(!(usingDevCard == 2 || (usingDevCard == 1 && showSubPlayerHitbox)));

        } else { // If the game is NOT in setup
            // Check if the player has enough cards to use the build buttons
            canBuildRoad = hasCards(0); // Roads
            canBuildSettlement = hasCards(1) && canBuildASettlment(); // Settlements
            canBuildCity = hasCards(2); // Cities
            canTrade4to = hasTradeCards(4, currentPlayer);
            canTrade3to = hasTradeCards(3, currentPlayer) && playerHasPort[currentPlayer][0]; //the player must have the cards and also own a port of type 0 or general 3:1
            canTrade2to = hasSpecializedPort(currentPlayer);

            toggleCardBtn.setEnabled(true);
            buyDevCardBtn.setEnabled(hasCards(3) && availableDevCards.size() > 0); //check if the player has the cards to make a dev card
            useDevCardBtn.setEnabled(hasDevCards() && !userPlayedDevCard); //only if the user has dev cards and hasn't already used oene this turn
        }

        //if in online mode and not the current player they should not be able to chick the turn switch button
        if (onlineMode != -1 && onlineMode != currentPlayer) {
            //if the game is online and the game should be disabled because it is waiting for progess from another player
            turnBtnEnabled = false;
        }
        //update the turn button
        turnSwitchBtn.setEnabled(turnBtnEnabled);
        //debug the game state
        //System.out.println("Setting to :" + turnBtnEnabled);

        //if the user can build tell them that. (may be overwitten by following instructions
        if (canBuildRoad || canBuildCity || canBuildSettlement || canTrade4to || canTrade3to || canTrade2to) {
            // Set the instruction labels to tell the user they can build
            instructionLbl.setText("Use your cards to trade or build roads or settlements");
            subInstructionLbl.setText("Or end your turn to continue the game");
        }

        //Update lables for when the player CAN build. (CANT build is below)
        //check for free roads from dev card
        if (usingDevCard == 2 && playerSetupRoadsLeft > 0) {
            instructionLbl.setText("You have " + playerSetupRoadsLeft + " free road(s) from the development card");
            subInstructionLbl.setText("Please build them in order to continue your turn.");

        }

        // Save what button was selected before this update began
        oldSelection = SettlerRadioBtn.getGroupSelection(settlerRadioBtns);

        // Select the first enabled button on the list
        if (canBuildRoad) {
            // Select the road button
            buildRoadRBtn.setSelected(true);
        } else if (canBuildSettlement) {
            // Select the settlement button
            buildSettlementSRBtn.setSelected(true);
        } else if (canBuildCity) {
            // Select the city button
            buildSettlementLRBtn.setSelected(true);
        } // If no buttons are selected and the game is not in setup
        else if (inSetup == false) {
            // If no buttons are enabled clear the selection
            if (SettlerRadioBtn.getGroupSelection(settlerRadioBtns) != null) {
                SettlerRadioBtn.getGroupSelection(settlerRadioBtns).setSelected(false);
            }

            if (inbetweenTurns) {
                //show this message for when not in set up (differnt code does this for when in setup)
                instructionLbl.setText("Please allow the next player to use the mouse");
                subInstructionLbl.setText("Then start the next turn");
            } else //set the instructions 
            if (thiefIsStealing) { //tell the player the theif is stealing
                // Set the instruction labels to tell the player that the thief will now be going around and stealing cards from eligble players
                instructionLbl.setForeground(new Color(255, 175, 175));
                instructionLbl.setFont(new Font(timesNewRoman.getName(), Font.BOLD, (int) ((timesNewRoman.getSize() + 5) / scaleFactor)));
                instructionLbl.setText("A Thief! Shortly they will go around steal cards. No other actions allowed");
                subInstructionLbl.setText("End your turn so the thief can decide the next person to steal from");

                //update the lables for the player if the thief is stealing their cards specifically. Do not show this if a 7 was JUST rolled
                if (stealCardNum[currentPlayer] > 0 && !thiefJustStarted) {
                    instructionLbl.setForeground(new Color(255, 175, 175));
                    instructionLbl.setFont(new Font(timesNewRoman.getName(), Font.BOLD, (int) ((timesNewRoman.getSize() + 5) / scaleFactor)));
                    instructionLbl.setText("The thief is stealing half your cards");
                    subInstructionLbl.setText("Select the " + stealCardNum[currentPlayer] + " you want to give them");
                } else if (showTileHitbox) { //if a 7 was JUST rolled and the current player needs to move the thief show a specific messgae.
                    instructionLbl.setForeground(new Color(255, 175, 175));
                    instructionLbl.setFont(new Font(timesNewRoman.getName(), Font.BOLD, (int) ((timesNewRoman.getSize() + 5) / scaleFactor)));
                    instructionLbl.setText("A Thief! They will steal cards. Select a hex to move the thief.");
                    subInstructionLbl.setText("Afterwards, you can then end your turn so the thief can decide the next person to steal from");
                }

            } else if (thiefJustFinished && subPlayersHaveEnoughcards) {
                // Set the instruction labels to tell the player that they need to select a play to steal from
                instructionLbl.setText("The thief is done stealing. But you are not!");
                subInstructionLbl.setText("Select one of your fellow players to take one of their cards at random");

            } else if (usingDevCard > -1) { // check if the player is using a dev card
                //check which dev card
                switch (usingDevCard) {
                    case 0:
                        //if there isn't actually a dev card yet and the player is just selecting
                        instructionLbl.setText("Please select a development card");
                        subInstructionLbl.setText("Hover over the card to get a description (coming soon)");
                        break;
                    case 1:
                        //knight card
                        instructionLbl.setText("Please select a new tile for the thief");
                        subInstructionLbl.setText("Then if available, select a player to steal 1 card from");
                        break;
                    case 3:
                        //monopoly card
                        instructionLbl.setText("Please select a resource type");
                        subInstructionLbl.setText("Any resource of that type owned by any player, will then be given to you");
                        break;
                    case 4:
                        //YOP card
                        instructionLbl.setText("Please select a resource type");
                        subInstructionLbl.setText("You will then receive 2 cards of that type.");
                        break;
                    default:
                        break;
                }

            } else if ((onlineMode != -1 && onlineMode != currentPlayer)) {
                //set the lables so the player knows that they need to wait
                instructionLbl.setText("You are player " + onlineMode + " please wait for your turn");
                subInstructionLbl.setText("The current player is: " + currentPlayer);
            } else {
                // Set the instruction labels to tell the player they dont have enough cards
                instructionLbl.setText("Sorry, you don't have enough cards to build anything");
                subInstructionLbl.setText("End your turn and collect more resources");
            }
        } // If no buttons are selected and the game IS in setup 
        else {
            // If no buttons are enabled clear the selection
            if (SettlerRadioBtn.getGroupSelection(settlerRadioBtns) != null) {
                SettlerRadioBtn.getGroupSelection(settlerRadioBtns).setSelected(false);
            }

            //check if the game is in online mode
            if (onlineMode == -1 || onlineMode == currentPlayer) {
                //if in offline mode then the player has no more buildings
                //also if in online mode and the current player is the online player
                //must not be inbetween turns
                if (inbetweenTurns) {
                    //show this message for when in setup (differnt code does this for when not in setup)
                    instructionLbl.setText("Please allow the next player to use the mouse");
                    subInstructionLbl.setText("Then start the next turn");
                } else //if not inbetween turns 
                {
                    // Set the instruction labels to tell the player they are out of setup buildings
                    instructionLbl.setText("You have placed all of your setup buildings");
                    subInstructionLbl.setText("End your turn to continue the game");
                }
            } else {
                //set the lables so the player knows that they need to wait
                instructionLbl.setText("You are player " + onlineMode + " please wait for your turn");
                subInstructionLbl.setText("The current player is: " + currentPlayer);
            }
        }

        // Change the enabled/disabled state of the buttons based on whether or 
        // not they can build the corresponding buildings
        buildRoadRBtn.setEnabled(canBuildRoad);              // Roads
        buildSettlementSRBtn.setEnabled(canBuildSettlement); // Settlements
        buildSettlementLRBtn.setEnabled(canBuildCity);       // Cities
        trade4to1Btn.setEnabled(canTrade4to);                //trade 4:1
        trade3to1Btn.setEnabled(canTrade3to);                //trade 3:1
        trade2to1Btn.setEnabled(canTrade2to);                //trade 2:1

        // If the button selected before this update is still enabled, select it
        // instead of the selection made in the if/else block above
        if (oldSelection != null && oldSelection.isEnabled()) { // Also make sure the saved button is not null
            oldSelection.setSelected(true);
        }
        // If any of the buttons are enabled, enable the build button
        // Otherwise disable it
        buildBtn.setEnabled(canBuildRoad || canBuildSettlement || canBuildCity);

    }// </editor-fold>

    /**
     * Determine if a the current player has enough cards to make a 2:1 trade
     * and if they have a port to support that.
     *
     * @return
     */
    private boolean hasSpecializedPort(int playerNumber) {
        boolean has2to1 = false;

        //create an array to store how many cards of each type the player has
        countNumCardTypes(playerNumber);

        for (int i = 1; i < 6; i++) { //loop thorugh indexes 1-5
            if (playerHasPort[currentPlayer][i] && numCardType[i] >= 2) { //check if the player has that port and atleast 2 cards of that type
                has2to1 = true;
            }
        }

        return has2to1;
    }

    /**
     * Determine if the current player has enough cards to begin a trade.
     *
     * @param tradingType Three valid options. "4" for a four to one, "3", and
     * "2" for their respective ratios
     * @return
     */
    private boolean hasTradeCards(int tradingType, int playerNumber) {
        boolean hasEnoughCards = false; //does the player have enough cards. Start false because no check has been made yet.

        //check if the input is valid
        if (!(tradingType >= 2 && tradingType <= 4)) {
            System.out.println("Error: hasTradeCards out of bounds with value: " + tradingType);
        } else { //check the current players cards
            //create an array to store how many cards of each type the player has
            countNumCardTypes(playerNumber);

            //check if there is the required amount of each type
            for (int i = 1; i < numCardType.length; i++) {
                //check each type
                if (numCardType[i] >= tradingType) {
                    hasEnoughCards = true;
                }
            }

        }

        return hasEnoughCards;
    }

    /**
     * Determines if the current player has the right development cards to
     * enable the use development cards button
     *
     * @param buildingType
     * @return
     */
    private boolean hasDevCards() {
        boolean hasCards = false;

        //if there are any cards
        if (devCards[currentPlayer].size() > 0) {

            //loop through the first few types and see if they are contained in the list
            for (int i = 1; i < 5; i++) { //go thorugh card types 1, 2, 3, 4
                if (devCards[currentPlayer].contains(i)) {
                    hasCards = true;
                }
            }

        }

        return hasCards;
    }

    /**
     * Determines if the current player has enough cards to build a type of
     * building This replaces the old findCards method, and is more efficient
     *
     * @param buildingType The building type represented as an integer (0 =
     * road, 1 = settlement, 2 = city / upgrade settlement, 3 = development
     * cards)
     * @return If the current player has enough cards
     */
    private boolean hasCards(int buildingType) {

        int listSize; // The number of cards the player has. Used to reduce calls to the size method
        int typeToFind; // What type of card is currently being searched for
        int amountFound;

        int findCards[] = new int[6]; // How many cards of each type must be found
        // Index 0 is unused. Numbers are stored in 1-5 to correspond to type IDs

        // Set how many of each card the user needs based on the building type
        switch (buildingType) {
            case 0: // Road
                // Roads require 1 clay and 1 wood
                findCards[1] = 1;
                findCards[2] = 1;
                break;
            case 1: // Settlement
                // Settlements require 1 clay, 1 wood, 1 wheat, and 1 sheep
                findCards[1] = 1;
                findCards[2] = 1;
                findCards[3] = 1;
                findCards[4] = 1;
                break;
            case 2: // City
                // Upgrading to a city requires 2 wheat and 3 ore
                findCards[3] = 2;
                findCards[5] = 3;
                break;
            case 3: // Development card
                // Making a development card requires 1 wheat, 1 sheep, and 1 ore
                findCards[3] = 1;
                findCards[4] = 1;
                findCards[5] = 1;
                break;
        }

        // Setup variables to begin searching
        typeToFind = 1;
        amountFound = 0;
        listSize = cards[currentPlayer].size(); // Get the number of cards the player has

        // Search for the cards
        for (int i = 0; i <= listSize; i++) {
            // If enough cards of this type have been found
            // (Repeat until the next type to search for is reached)
            while (findCards[typeToFind] <= amountFound) {
                // If the loop was searching for ore, then this means all of the 
                // required cards have been found
                if (typeToFind == 5) {
                    return true; // The player can build
                }
                // Move on to the type
                typeToFind++;
                // Reset the card counter
                amountFound = 0;
            }
            // If the end of the list has been passed and the player didnt have 
            // all the cards needed
            if (i == listSize) {
                // The player cannot build
                return false;
            }

            // If the card type matches
            if (cards[currentPlayer].get(i) == typeToFind) {
                // Increment the counter
                amountFound++;
            } // The list is sorted by type, so if the type ID is greater than the target, stop searching
            else if (cards[currentPlayer].get(i) > typeToFind) {
                return false; // The user does not have the cards
            }
        }

        // If the cards have not been found, the player cannot build
        return false;
    }

    /**
     * Sort a player's list of cards using a recursive quick sort algorithm
     *
     * @param player The player who's cards will be sorted
     * @param left The left bounds of the segment to sort (used to recursion,
     * set to 0)
     * @param right The right bounds of the segment to sort (used to recursion,
     * set to length of array - 1)
     * @return
     */
    private void quickSortCards(ArrayList<Integer> array, int left, int right) {

        Integer temp; // For swapping values
        // Get the player's ArrayList of cards
        //ArrayList<Integer> array = cards[player];

        // If the list bounds overlap 
        if (left >= right) {
            // Stop sorting this branch
            return;
        }

        // Initially set the pointers to the bounds of the array to sort
        int i = left;
        int j = right;
        // Get the value in the middle of the array as the pivot point
        int pivotValue = array.get((left + right) / 2);

        // Repeat until all of the numbers are on the correct side
        while (i < j) {
            // Skip over numbers that are already on the correct side
            // Move the pointers until they are on a value that's on the wrong side
            while (array.get(i) < pivotValue) {
                i++;
            }
            while (array.get(j) > pivotValue) {
                j--;
            }

            // If the pointers are still on the correct side
            if (i <= j) {
                // Swap the values on each side of the pivot point
                temp = array.get(i);
                array.set(i, array.get(j));
                array.set(j, temp);
                // Move both pointers
                i++;
                j--;
            }
        }

        // Recursively call the algorithm on the left side of the pivot point
        quickSortCards(array, left, j);
        // Recursively call the algorithm on the right side of the pivot point
        quickSortCards(array, i, right);
    }

    /**
     * Check if the player can build a road on the given node
     *
     * @param road The road node to check if the user can build on
     * @return If the player can build on it
     */
    private boolean canBuildRoad(NodeRoad road) {
        //check if the game is in setup mode
        if (inSetup) {
            //if yes the player can only build on the newest settlement
            if (road.getSettlement(1) != newestSetupSettlment && road.getSettlement(2) != newestSetupSettlment) {
                return false;
            }
        }

        // If the current player owns either of the settlements connected to this
        if (road.getSettlement(1).getPlayer() == currentPlayer
                || road.getSettlement(2).getPlayer() == currentPlayer) {
            // Then the player can build here
            return true;
        }

        // If the first settlement is not owned by another player
        if (road.getSettlement(1).getPlayer() == currentPlayer || road.getSettlement(1).getPlayer() == 0) {
            // Check the first settlement node for a road owned by the current player
            for (int i = 1; i <= 3; i++) {
                // Make sure the road exists
                if (road.getSettlement(1).getRoad(i) != null) {
                    // If one of the roads is owned by the player 
                    if (road.getSettlement(1).getRoad(i).getPlayer() == currentPlayer) {
                        return true;
                    }
                }
            }
        }

        // If the second settlement is not owned by another player
        if (road.getSettlement(2).getPlayer() == currentPlayer || road.getSettlement(2).getPlayer() == 0) {
            // Check the second settlement node for a road owned by the current player
            for (int i = 1; i <= 3; i++) {
                // Make sure the road exists
                if (road.getSettlement(2).getRoad(i) != null) {
                    // If one of the roads is owned by the player 
                    if (road.getSettlement(2).getRoad(i).getPlayer() == currentPlayer) {
                        return true;
                    }
                }
            }
        }

        // If the user cannot build here
        return false;
    }

    /**
     * Checks to see if the player can build an settlement at all
     *
     * @return
     */
    private boolean canBuildASettlment() {

        //loop thorugh all the settlments
        for (int i = 0; i < settlementNodes.size(); i++) {
            //if one of them matches return true with no need to check the others
            if (canBuildSettlement(settlementNodes.get(i), true)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check if the player can build a settlement on the given node. During
     * setup, will not check for connect to roads
     *
     * @param settlement The settlement node to check if the user can build on
     * @return If the player can build on it
     */
    private boolean canBuildSettlement(NodeSettlement settlement, boolean quietMode) {

        // Record how many of the 3 nodes are owned by other players
        // And if one of the connected roads belong to the current player
        int otherPlayerNumber = 0;
        boolean currentPlayerHasRoad = false; // If the current player has a road connected to this node
        NodeRoad road; // Stores the road currently being checked by the loop

        for (int i = 1; i <= 3; i++) {
            // Save a reference to the road
            road = settlement.getRoad(i);

            // If the road exists
            if (road != null) {
                // If the road belongs to the player, or the game is in setup (where road connections dont mater here)
                if (road.getPlayer() == currentPlayer || inSetup) {
                    // Then store that the user has a road connecting to this place
                    currentPlayerHasRoad = true;
                } // If the road belongs to a different player (and is owned)
                else if (road.getPlayer() > 0) {
                    // Check if this is the second time the same other player's road was found
                    if (road.getPlayer() == otherPlayerNumber) {
                        // Then the other player has a road going through and past this point,
                        // Blocking any other player from building a settlement there

                        // Set the labels to show why the player could not build there
                        instructionLbl.setText("Sorry but you can't build a settlement there.");
                        subInstructionLbl.setText("That position is blocked by another player");

                        return false; // Node is blocked, cannot build here
                    }
                    // Record the road's user ID
                    otherPlayerNumber = road.getPlayer();
                }

                // Check that neither of the connected roads' settlement nodes are taken
                // Settlements cannot be built next to other settlements, even if there is no road between them
                // (Follow the distance rule)
                if (road.getSettlement(1).getPlayer() != 0 || road.getSettlement(2).getPlayer() != 0) {
                    // The settlement is too close to another

                    if (!quietMode) {
                        // Print out why the player could not build there
                        instructionLbl.setText("Sorry but you can't build a settlement there.");
                        subInstructionLbl.setText("Try building farther away from exsisting buildings");
                    }

                    return false; // Cannot build here
                }

            }
        }

        // Let the user know if they could not build due to no roads being connected
        if (!currentPlayerHasRoad) {
            instructionLbl.setText("Sorry but you can't build a settlement there.");
            subInstructionLbl.setText("Try building on one of your exsisting roads");
        }

        // If the code above didnt find any issues, then the user can build here 
        // as long as they have a connecting road. (Or are in setup)
        return currentPlayerHasRoad;
    }

    /**
     * Roll both of the 6 sided dice and act according to the roll. 7 Will
     * trigger thief movement, and other values give resources. The roll is done
     * as 2 1d6 rolls to create the same number rarity as 2 dice give
     */
    private void diceRoll() {
        int roll; // Holds the dice roll

        // Roll the first dice
        roll = (int) (Math.random() * 6) + 1;

        // Display the number rolled to the user
        //updates the var that displays the roll. updates every time the draw() method is run
        //save the first roll
        diceRollVal[0] = (Integer.toString(roll));

        // Roll the second dice and add to the total
        roll += (int) (Math.random() * 6) + 1;

        // Display the number rolled to the user
        //updates the var that displays the roll. updates every time the draw() method is run
        //save the second roll. Subtract the first roll from the sum to get the specific value of the second roll
        diceRollVal[1] = (Integer.toString(roll - Integer.parseInt(diceRollVal[0])));

        //combine into a sum
        diceRollVal[2] = (Integer.toString(roll));

        //This code might not ever run. There may be no senario where this test is true
        //check what the value is
        if (Integer.parseInt(diceRollVal[2]) == 0) { //if the sum is 0 replace it with an empty String
            diceRollVal[2] = "zero";

        }

        // Act on the dice roll
        if (roll == 7) { // Move the thief on a 7

            /*
            Old Code. This is now handeled in MouseClick when the player clicks the Tile they would like to move the thief to.
            
            // Pick a random tile to move the thief to
            int random = (int) (Math.random() * 19);
            // Remove the thief from the tile it was on before
            tiles.get(tileWithThief).setThief(false);
            // Place a thief on the randomly selected tile
            tiles.get(random).setThief(true);
            // Store the tile index of the thief's new location
            tileWithThief = random;
            // Increment the thief movement counter
            thiefMoveCounter++;
             */
            //prevent the player from switching away from their resource cards
            showDevCards = false; //force show the resource cards
            toggleCardBtn.setMode(0); //make sure the mode and the text don't ge out of sync

            //steal the cards and allow the lables to update
            thiefIsStealing = true;
            thiefJustStarted = true;

            //show the tile hitboxes to the player who rolled the 7. This allows them to move the thief to the Tile of thier choosing.
            showTileHitbox = true;
            //disable the turn switch button so that this cannot be skiped
            turnBtnEnabled = false;

            //save the player who just rolled a 7
            playerRolled7 = currentPlayer;

            //get the number of cards each players has over seven
            //loop through the players
            for (int i = 0; i < cards.length; i++) {
                //check if the player has more than the allowed seven
                if (cards[i].size() > 7) {
                    //get the number of cards that need to be stolen
                    stealCardNum[i] = (int) Math.floor(cards[i].size() / 2.0);
                    //debug card stealing
                    //System.out.println("I will steal " + stealCardNum[i] + " cards from player " + i);

                    /*
                    Old randomization of card stealing
                    for (int j = stealCardNum[i]; j > 0; j--) {
                        cards[i].remove( (int) (Math.random()*cards[i].size()) );
                    }
                     */
                }
            }

        } else { // Otherwise collect materials
            // Search for tiles with the number rolled as their harvest number,
            // And give players the materials
            collectMaterials(roll);
        }
    }

    /**
     * Collect resources from tiles with the passed harvest roll number and give
     * the collected resources to the owner of the settlements collecting them
     *
     * @param harvestNumber The harvesting roll that selects which tiles to
     * harvest from. A value of 0 will collect from every tile regardless of the
     * tiles' harvest numbers.
     */
    private void collectMaterials(int harvestNumber) {
        NodeSettlement settlement; // Hold the settlement being searched
        int player; // The id of the settlement's owner

        // Check every settlement to see if a player owns it and if any 
        // adjacent tiles have the harvesting number
        for (int i = 0; i < 54; i++) {
            // Store the node
            settlement = settlementNodes.get(i);

            // Make sure the settlement is owned
            player = settlement.getPlayer();
            if (player != 0) {
                // Search the tiles around the node
                for (int j = 1; j <= 3; j++) {
                    // If the tile is not null
                    if (settlement.getTile(j) != null) {
                        // If the tile has the same harvest number
                        // And doesnt have the thief on it
                        //and isn't the desert tile (because it has no resources)
                        if (settlement.getTile(j).getType() != 0 && ((settlement.getTile(j).getHarvestRollNum() == harvestNumber
                                && settlement.getTile(j).hasThief() == false)
                                // Or if every tile is to be harvested from (passed harvest number is 0)
                                || harvestNumber == 0)) {
                            // Give the player the tile's resource
                            cards[player].add(settlement.getTile(j).getType());
                            // Add the collected card to the card counter
                            totalCardsCollected[settlement.getTile(j).getType() - 1]++;
                            // If the settlement was a city (City), the player earns twice the resources
                            if (settlement.isLarge()) {
                                // Give the player a second card from the tile
                                cards[player].add(settlement.getTile(j).getType());
                                // Add the second collected card to the card counter
                                totalCardsCollected[settlement.getTile(j).getType() - 1]++;
                            }
                        }
                    }
                }
            }
        }

        // Sort every player's cards
        for (int i = 1; i < cards.length; i++) {
            // Sort the player's cards using a quick sort algorithm
            quickSortCards(cards[i], 0, cards[i].size() - 1);
        }

    }

    /**
     * End the game, generating a JOptionPane showing the winner and round
     * statistics
     */
    private void endGame() {

        // Create a string for the output with html formating 
        String msg = "<html><body>";

        // Find the player with the most victory points
        int winningPlayer = 0;
        for (int i = 1; i <= playerCount; i++) { // Check every player
            // If the player has a larger number of victory points store them as the winner
            if (victoryPoints[winningPlayer] < victoryPoints[i]) {
                winningPlayer = i;
            }
        }

        // Add the line showing the winning player
        msg += "<h1>Player " + winningPlayer + " wins!</h1>";

        // Print a table showing the players' victory points
        msg += "<h3>Victory Points:</h3><h4>";
        // Add a list of every player's points in smaller text
        for (int i = 1; i <= playerCount; i++) {
            if (i != 1) {
                msg += "<br>";
            }
            // Add rhe player's points to the output
            msg += " - Player " + i + ": " + victoryPoints[i];
        }
        msg += "</h4>";

        // Print a table showing the number of each card collected and the total
        msg += "<h3>Total Cards Collected:</h3>";
        // Add a list of the card counters
        msg += "<h4> - Clay:  " + totalCardsCollected[0];
        msg += "<br> - Wood:  " + totalCardsCollected[1];
        msg += "<br> - Wheat: " + totalCardsCollected[2];
        msg += "<br> - Sheep: " + totalCardsCollected[3];
        msg += "<br> - Ore:   " + totalCardsCollected[4];
        msg += "<br> - Total: " + (totalCardsCollected[0]
                + totalCardsCollected[1] + totalCardsCollected[2]
                + totalCardsCollected[3] + totalCardsCollected[4]) + "</h4>";

        msg += "<h3>Thief moved " + thiefMoveCounter + " times</h3>";

        // Close off the html tags
        msg += "</body></html>";

        // Display the output in a JOptionPane
        JOptionPane.showMessageDialog(this, msg, "Game Over", JOptionPane.PLAIN_MESSAGE);

        // Close the game panel
        // Hide this window and show the main menu
        superFrame.getMainMenu().setVisible(true); //show the main menu        
        superFrame.setVisible(false); //hide the parent frame 
    }

    //overrides paintComponent in JPanel class
    //performs custom painting
    /**
     * This does the set up for the drawing in 2d graphics
     *
     * @param g
     */
    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);//does the necessary work to prepare the panel for drawing
        draw(g); //add the custom drawing (the game)
    }

    // <editor-fold defaultstate="collapsed" desc="Drawing"> 
    /**
     * Draw the game.
     *
     * @param g
     */
    private void draw(Graphics g) {
        //start local vars
        int rightDrawMargin = superFrame.getWidth() - getImgWidth(MATERIAL_KEY) - (int) (10 / scaleFactor);
        boolean drawSpecificHitbox; //local var to hold the value deciding if a specifc Port hitbox should be drawn. Depending on they type of trading mode.

        //end local vars
        //the Graphics2D class is the class that handles all the drawing
        //must be casted from older Graphics class in order to have access to some newer methods
        Graphics2D g2d = (Graphics2D) g;
        //draw a string on the panel        
        g2d.setFont(new Font("Times New Roman", Font.BOLD, (int) (40 / scaleFactor)));
        //old title. Replaced by JLabel
        //g2d.drawString("Settlers of Catan", (int) (10 / scaleFactor), (int) (50 / scaleFactor)); //(text, x, y)        }

        //draw the background
        g2d.drawImage(WOOD_BACKGROUND,
                0,
                0,
                getImgWidth(WOOD_BACKGROUND),
                getImgHeight(WOOD_BACKGROUND), null);

        //draw the ring of water
        //also scale it to the current monitor. Coords are to center it relative to the display center
        g2d.drawImage(WATER_RING,
                superFrame.getWidth() / 2 - getImgWidth(WATER_RING) / 2,
                superFrame.getHeight() / 2 - getImgHeight(WATER_RING) / 2,
                getImgWidth(WATER_RING),
                getImgHeight(WATER_RING), null);

        //debug the game pannel
        //System.out.println("GamePannel draw function called"); //and indecation of how many times the draw function runs
        //draw the building material costs key
        g2d.drawImage(MATERIAL_KEY,
                rightDrawMargin, //put it in the corner with some padding space
                (int) (10 / scaleFactor), //just a little bit from the top
                getImgWidth(MATERIAL_KEY), //scale the image
                getImgHeight(MATERIAL_KEY),
                null);

        //draw the longest road tile if the current player has it
        if (longestRoadData.getPlayerNum() == currentPlayer && longestRoadData.getSize() >= 5 && !inbetweenTurns) {
            g2d.drawImage(LONGEST_ROAD,
                    (int) (rightDrawMargin - getImgWidth(LONGEST_ROAD) - (30 / scaleFactor)),
                    (int) (10 / scaleFactor),
                    getImgWidth(LONGEST_ROAD),
                    getImgHeight(LONGEST_ROAD),
                    null);
        }

        //draw the largest army tile if the current player has it
        if (largestArmyData.getPlayerNum() == currentPlayer && largestArmyData.getSize() >= 3 && !inbetweenTurns) {
            g2d.drawImage(LARGEST_ARMY,
                    (int) (rightDrawMargin - getImgWidth(LARGEST_ARMY) - scaleInt(30)),
                    getImgHeight(MATERIAL_KEY) + scaleInt(10) - getImgHeight(LARGEST_ARMY),
                    getImgWidth(LARGEST_ARMY),
                    getImgHeight(LARGEST_ARMY),
                    null);
        }

        Image currentPlayerImage = getPlayerImage(currentPlayer, false);

        //draw the current player icon
        g2d.drawImage(currentPlayerImage,
                superFrame.getWidth() - getImgWidth(PLAYER_RED) - (int) (10 / scaleFactor), //put it in the corner with some padding space
                superFrame.getHeight() - getImgHeight(PLAYER_RED) - (int) (10 / scaleFactor), //put it in the corner with some padding space
                getImgWidth(PLAYER_RED), //scale the image
                getImgHeight(PLAYER_RED),
                null);

        //draw the current player icon header
        g2d.setFont(new Font("Times New Roman", Font.BOLD, (int) (20 / scaleFactor)));
        g2d.setColor(new java.awt.Color(255, 255, 225));
        g2d.drawString("Current player:",
                superFrame.getWidth() - getImgWidth(PLAYER_RED) - (int) (10 / scaleFactor),
                superFrame.getHeight() - getImgHeight(PLAYER_RED) - (int) (20 / scaleFactor));

        Image subsequentPlayerImage;

        //draw the subsequent other players but smaller
        for (int i = 1; i < playerTurnOrder.size(); i++) {
            //only show the sub players if the thief is not stealing. Otherwise it gets confusing.
            if (thiefIsStealing) {
                subsequentPlayerImage = SMALL_PLAYER_NONE;
            } else {
                subsequentPlayerImage = getPlayerImage(playerTurnOrder.get(i), true);
            }

            g2d.drawImage(subsequentPlayerImage,
                    superFrame.getWidth() - (getImgWidth(PLAYER_RED)) - ((getImgWidth(SMALL_PLAYER_RED)) * i), //put it in the corner with some padding space
                    superFrame.getHeight() - (int) (10 / scaleFactor) - getImgHeight(SMALL_PLAYER_RED), //put it in the corner with some padding space
                    getImgWidth(SMALL_PLAYER_RED), //scale the image
                    getImgHeight(SMALL_PLAYER_RED),
                    null);

            //draw the hitbox for the subsequent players
            //make sure the criteria is met before drawing.
            if ((canStealCardPlayers.size() > 0 && currentPlayer == playerRolled7 && !thiefIsStealing && !inbetweenTurns && subPlayersHaveEnoughcards) || showSubPlayerHitbox) {

                drawSpecificHitbox = cards[playerTurnOrder.get(i)].size() > 0 && canStealCardPlayers.contains(playerTurnOrder.get(i));

                //only draw the the hitbox around that specific player if they have more than 0 cards and if they are on the steal list
                if (drawSpecificHitbox) {
                    //draw the high light
                    g2d.setColor(new java.awt.Color(255, 255, 225, 128));
                    g2d.fillRect(superFrame.getWidth() - (getImgWidth(PLAYER_RED)) - (getImgWidth(SMALL_PLAYER_RED) * i),
                            superFrame.getHeight() - (int) (10 / scaleFactor) - getImgHeight(SMALL_PLAYER_RED),
                            getImgWidth(SMALL_PLAYER_RED),
                            getImgHeight(SMALL_PLAYER_RED));
                    //draw the boarder
                    g2d.setColor(new java.awt.Color(255, 255, 225));
                    g2d.drawRect(superFrame.getWidth() - (getImgWidth(PLAYER_RED)) - (getImgWidth(SMALL_PLAYER_RED) * i),
                            superFrame.getHeight() - (int) (10 / scaleFactor) - getImgHeight(SMALL_PLAYER_RED),
                            getImgWidth(SMALL_PLAYER_RED),
                            getImgHeight(SMALL_PLAYER_RED));
                }
            }

        }

        //draw the sub player header
        g2d.setColor(new java.awt.Color(255, 255, 225));
        g2d.drawString("Next player:",
                superFrame.getWidth() - (getImgWidth(PLAYER_RED)) - (getImgWidth(SMALL_PLAYER_RED)),
                superFrame.getHeight() - (int) (20 / scaleFactor) - getImgHeight(SMALL_PLAYER_RED));

        Image PORT_RESOURCE = new ImageIcon(ImageRef.class.getResource("port/wildcard.png")).getImage();

        //draw the ports
        for (int i = 0; i < ports.size(); i++) {
            g2d.drawImage(ports.get(i).getImage(),
                    ports.get(i).getXPos(),
                    ports.get(i).getYPos(),
                    getImgWidth(ports.get(i).getImage()),
                    getImgHeight(ports.get(i).getImage()),
                    null);

            //draw the recource type on top
            g2d.drawImage(ports.get(i).getTypeImage(),
                    ports.get(i).getTypePosX(),
                    ports.get(i).getTypePosY(),
                    getImgWidth(ports.get(i).getTypeImage()),
                    getImgHeight(ports.get(i).getTypeImage()),
                    null);
        }

        //draw the board using the new way. the coordinates inside the tile objects come from the old way of drawing the baord
        int tileID;
        for (int i = 0; i < 19; i++) {
            tileID = tileDrawOrder[i];
            //tileID = i;

            //check if it is the new type or old size
            if (tiles.get(tileID).getImage().getHeight(null) == 150) {
                //draw the tile

                g2d.drawImage(tiles.get(tileID).getImage(),
                        tiles.get(tileID).getXPos(),
                        (int) (tiles.get(tileID).getYPos() - (20 / scaleFactor)),
                        getImgWidth(tiles.get(tileID).getImage()),
                        getImgHeight(tiles.get(tileID).getImage()), null);

            } else {

                //draw the tile
                g2d.drawImage(tiles.get(tileID).getImage(),
                        tiles.get(tileID).getXPos(),
                        tiles.get(tileID).getYPos(),
                        getImgWidth(tiles.get(tileID).getImage()),
                        getImgHeight(tiles.get(tileID).getImage()), null);
            }

            //draw the resource harvest number only if it is not a desert
            if (tiles.get(tileID).getType() != 0) {
                g2d.setColor(Color.DARK_GRAY);
                g2d.fillOval(tiles.get(tileID).getXPos() + newTileWidth / 2 - ((int) (30 / scaleFactor) / 2),
                        (int) (tiles.get(tileID).getYPos() + newTileHeight / 2 - ((30 / scaleFactor) / 2) + threeDTileOffset),
                        (int) (30 / scaleFactor),
                        (int) (30 / scaleFactor));

                //check if the colour of the number
                if (tiles.get(tileID).getHarvestRollNum() == 8 || tiles.get(tileID).getHarvestRollNum() == 6) {
                    g2d.setColor(Color.red);
                } else {
                    g2d.setColor(Color.white);
                }

                //set the offset based on the number of digits
                if (tiles.get(tileID).getHarvestRollNum() > 9) {
                    harvestRollNumOffset = (int) (10 / scaleFactor);
                } else {
                    harvestRollNumOffset = (int) (4 / scaleFactor);
                }

                //draw the harvest roll num
                g2d.setFont(new Font("Times New Roman", Font.BOLD, (int) (20 / scaleFactor)));
                g2d.drawString(Integer.toString(tiles.get(tileID).getHarvestRollNum()),
                        tiles.get(tileID).getXPos() + newTileWidth / 2 - harvestRollNumOffset,
                        (int) (tiles.get(tileID).getYPos() + newTileHeight / 2 + (6 / scaleFactor) + threeDTileOffset));
            }

            //check where the thief is and draw it there
            if (tiles.get(tileID).hasThief()) {

                int imageWidth = getImgWidth(THIEF);
                int imageHeight = getImgHeight(THIEF);

                //draw the thief
                g2d.drawImage(THIEF,
                        tiles.get(tileID).getXPos() + newTileWidth / 2 - (imageWidth / 2),
                        tiles.get(tileID).getYPos() + newTileHeight / 2 - (imageHeight / 2) + threeDTileOffset,
                        imageWidth,
                        imageHeight,
                        null);
            }

            //draw the hitbox for the tile
            //make sure the hex fits the criteria. Thief cannot be moved to the tile where they already are.
            if (showTileHitbox && !tiles.get(tileID).hasThief()) {
                //draw the high light
                g2d.setColor(new java.awt.Color(255, 255, 225, 64));
                g2d.fillRect(tiles.get(tileID).getXPos() + newTileWidth / 2 - ((int) (30 / scaleFactor) / 2),
                        (int) (tiles.get(tileID).getYPos() + newTileHeight / 2 - ((30 / scaleFactor) / 2) + threeDTileOffset),
                        (int) (30 / scaleFactor),
                        (int) (30 / scaleFactor));
                //draw the boarder
                g2d.setColor(new java.awt.Color(255, 255, 225));
                g2d.drawRect(tiles.get(tileID).getXPos() + newTileWidth / 2 - ((int) (30 / scaleFactor) / 2),
                        (int) (tiles.get(tileID).getYPos() + newTileHeight / 2 - ((30 / scaleFactor) / 2) + threeDTileOffset),
                        (int) (30 / scaleFactor),
                        (int) (30 / scaleFactor));
            }
        } //end tile drawing loop

        //draw the resouce stacks at the top of the board
        for (int i = 1; i < 6; i++) { //loop through 1-5 for teh 5 resouce types
            g2d.drawImage(RES_STACKS[i],
                    resourceStackXPositions[i],
                    1, //since 
                    getImgWidth(RES_STACKS[i]),
                    getImgHeight(RES_STACKS[i]),
                    null);

            //draw the hitbox
            if (showResStackHitbox) {
                //decide if that specif box should be drawn
                if (onlineMode != -1 && onlineMode != currentPlayer) {
                    drawSpecificHitbox = false;
                } else if (tradingMode == 1 || tradingMode == 2) {
                    drawSpecificHitbox = canTradeTo(i, tradingMode);
                } else if (tradingMode == 3) {
                    //if its a specialized 2:1
                    drawSpecificHitbox = canTradeSecializedTo(i);
                } else {
                    drawSpecificHitbox = usingDevCard == 4 || usingDevCard == 3; //if the player is selecting a resource type for a YOP or Monopoly dev card
                }

                //check if that one should be drawn
                if (drawSpecificHitbox) {
                    g2d.setColor(new java.awt.Color(255, 255, 225, 128));
                    //draw the highlight
                    g2d.fillRect(resourceStackXPositions[i],
                            1,
                            getImgWidth(RES_STACKS[i]),
                            getImgHeight(RES_STACKS[i]));
                    //draw the boarder
                    g2d.setColor(new java.awt.Color(255, 255, 225));
                    g2d.drawRect(resourceStackXPositions[i],
                            1,
                            getImgWidth(RES_STACKS[i]),
                            getImgHeight(RES_STACKS[i]));
                }
            }
        }

        //set the font for the dice roll indecator
        g2d.setFont(new Font("Times New Roman", Font.PLAIN, (int) (20 / scaleFactor)));
        g2d.setColor(new java.awt.Color(255, 255, 225));
        //show what number the user rolled
        g2d.drawString("You rolled a: " + diceRollVal[2],
                rightDrawMargin,
                (int) (450 / scaleFactor));
        //draw the dice
        //but only if not in setup
        if (!inSetup) {
            //draw the non rolled dice if there is no roll
            if (diceRollVal[2].equals("")) {

                g2d.drawImage(DICE_IMAGES[0],
                        rightDrawMargin,
                        (int) (450 / scaleFactor),
                        getImgWidth(DICE_IMAGES[0]),
                        getImgHeight(DICE_IMAGES[0]),
                        null);
            } else { //else draw the dice that go with the roll
                g2d.drawImage(DICE_IMAGES[Integer.parseInt(diceRollVal[0])],
                        rightDrawMargin,
                        (int) (450 / scaleFactor),
                        getImgWidth(DICE_IMAGES[1]),
                        getImgHeight(DICE_IMAGES[1]),
                        null);

                g2d.drawImage(DICE_IMAGES[Integer.parseInt(diceRollVal[1])],
                        rightDrawMargin + getImgWidth(DICE_IMAGES[1]),
                        (int) (450 / scaleFactor),
                        getImgWidth(DICE_IMAGES[1]),
                        getImgHeight(DICE_IMAGES[1]),
                        null);

            }
        }

        int playerNumOffset;
        if (playerCount > 3) {
            playerNumOffset = 0;
        } else {
            playerNumOffset = scaleInt(60);
        }

        //draw the VP and resource card start table
        //draw the player header
        g2d.drawString("Player Colour:",
                rightDrawMargin - scaleInt(60) + playerNumOffset,
                (int) (600 / scaleFactor));
        //draw the victory points header
        g2d.drawString("Victory Points:",
                rightDrawMargin - scaleInt(60) + playerNumOffset,
                scaleInt(635));
        //draw the Resource Cards header
        g2d.drawString("Resource Cards:",
                rightDrawMargin - scaleInt(60) + playerNumOffset,
                scaleInt(670));
        //draw playerLongestRoadSegments header
        g2d.drawString("Road Length:",
                rightDrawMargin - scaleInt(60) + playerNumOffset,
                scaleInt(705));
        //draw playerArmySize header
        g2d.drawString("Size of Army:",
                rightDrawMargin - scaleInt(60) + playerNumOffset,
                scaleInt(740));

        //loop in all the data for the players
        for (int i = 1; i < playerCount + 1; i++) {
            //draw the player's indecator dot
            g2d.drawImage(PLAYER_DOTS[i],
                    rightDrawMargin + scaleInt(65 * i) + scaleInt(5) + playerNumOffset,
                    scaleInt(580),
                    getImgWidth(PLAYER_DOTS[i]),
                    getImgHeight(PLAYER_DOTS[i]), null);
            //draw the players VPs
            g2d.drawString("" + victoryPoints[i],
                    rightDrawMargin + scaleInt(15) + scaleInt(65 * i) + playerNumOffset,
                    scaleInt(635));
            //draw the players number of resource cards
            g2d.drawString("" + cards[i].size(),
                    rightDrawMargin + scaleInt(15) + scaleInt(65 * i) + playerNumOffset,
                    scaleInt(670));
            //draw the players playerLongestRoadSegment
            g2d.drawString("" + playerLongestRoadSegments[i],
                    rightDrawMargin + scaleInt(15) + scaleInt(65 * i) + playerNumOffset,
                    scaleInt(705));
            //draw the players playerArmySize
            g2d.drawString("" + playerArmySize[i],
                    rightDrawMargin + scaleInt(15) + scaleInt(65 * i) + playerNumOffset,
                    scaleInt(740));
        }

        // Draw the 72 road nodes
        NodeRoad road;
        Image image;
        for (int i = 0; i < 72; i++) {
            road = roadNodes.get(i);
            switch (road.getOrientation()) {
                case 0: // Horizontal road ( -- )
                    // Store the road image for the player's color
                    switch (road.getPlayer()) {
                        case 0:
                            image = BLANK_ROAD_H;
                            break;
                        case 1:
                            image = RED_ROAD_H;
                            break;
                        case 2:
                            image = BLUE_ROAD_H;
                            break;
                        case 3:
                            image = ORANGE_ROAD_H;
                            break;
                        case 4:
                            image = WHITE_ROAD_H;
                            break;
                        default:
                            image = RED_ROAD_L;
                            break;
                    }
                    break;
                case 1: // Road pointing to the top left ( \ )
                    // Store the road image for the player's color
                    switch (road.getPlayer()) {
                        case 0:
                            image = BLANK_ROAD_V;
                            break;
                        case 1:
                            image = RED_ROAD_L;
                            break;
                        case 2:
                            image = BLUE_ROAD_L;
                            break;
                        case 3:
                            image = ORANGE_ROAD_L;
                            break;
                        case 4:
                            image = WHITE_ROAD_L;
                            break;
                        default:
                            image = RED_ROAD_H;
                            break;
                    }
                    break;
                case 2: // Road pointing to the top right ( / )
                    // Store the road image for the player's color
                    switch (road.getPlayer()) {
                        case 0:
                            image = BLANK_ROAD_V;
                            break;
                        case 1:
                            image = RED_ROAD_R;
                            break;
                        case 2:
                            image = BLUE_ROAD_R;
                            break;
                        case 3:
                            image = ORANGE_ROAD_R;
                            break;
                        case 4:
                            image = WHITE_ROAD_R;
                            break;
                        default:
                            image = RED_ROAD_H;
                            break;
                    }
                    break;
                default: // Make the compiler happy and error handling
                    image = RED_ROAD_H; // Just add an image so theres something to render
                    break;
            }

            // Draw the road image saved above, at the node's position
            g2d.drawImage(image,
                    road.getXPos() - getImgWidth(image) / 2,
                    road.getYPos() - getImgHeight(image) / 2,
                    getImgWidth(image),
                    getImgHeight(image),
                    null);

            //draw the hit box for the road.
            //check if it meets the can build criteria and that it is also currently not owned
            if (showRoadHitbox && canBuildRoad(road) && road.getPlayer() == 0) {
                //draw the high light
                g2d.setColor(new java.awt.Color(255, 255, 225, 128));
                g2d.fillRect(road.getXPos() - getImgWidth(image) / 2, road.getYPos() - getImgHeight(image) / 2, getImgWidth(image), getImgHeight(image));
                //draw the boarder
                g2d.setColor(new java.awt.Color(255, 255, 225));
                g2d.drawRect(road.getXPos() - getImgWidth(image) / 2, road.getYPos() - getImgHeight(image) / 2, getImgWidth(image), getImgHeight(image));
            }
        }

        // Draw the 54 settlement nodes
        NodeSettlement settlement;
        // Reuse the image variable;
        for (int i = 0; i < 54; i++) {
            // Get the settlement node from the ArrayList
            settlement = settlementNodes.get(i);

            // If the settlement is unowned use the blank image
            if (settlement.getPlayer() == 0) {
                image = BLANK_HOUSE;
            } // Otherwise, check the size of the settlement to see which image to use
            else if (settlement.isLarge() == false) { // settlement
                // Store the settlement image for the player's color
                switch (settlement.getPlayer()) {
                    // Player 1: Red
                    case 1:
                        image = RED_HOUSE_S;
                        break;
                    // Player 2: Blue
                    case 2:
                        image = BLUE_HOUSE_S;
                        break;
                    // Player 3: Orange
                    case 3:
                        image = ORANGE_HOUSE_S;
                        break;
                    // Player 4: White
                    case 4:
                        image = WHITE_HOUSE_S;
                        break;
                    default:
                        image = RED_HOUSE_L;
                        break;
                }
            } else { // city
                // Store the city image for the player's color
                switch (settlement.getPlayer()) {
                    // Player 1: Red
                    case 1:
                        image = RED_HOUSE_L;
                        break;
                    // Player 2: Blue
                    case 2:
                        image = BLUE_HOUSE_L;
                        break;
                    // Player 3: Orange
                    case 3:
                        image = ORANGE_HOUSE_L;
                        break;
                    // Player 4: White
                    case 4:
                        image = WHITE_HOUSE_L;
                        break;
                    default:
                        image = RED_HOUSE_S;
                        break;
                }
            }

            // Draw the settlement image saved above, at the node's position
            g2d.drawImage(image,
                    settlement.getXPos() - getImgWidth(image) / 2,
                    settlement.getYPos() - getImgHeight(image) / 2,
                    getImgWidth(image),
                    getImgHeight(image),
                    null);

            //draw the hit box for the settlements.
            if (showSettlementHitbox) {
                //new var for daring the hitbox for that settlemnt
                boolean drawHitBox = false;

                //check what build mode is active
                switch (buildingObject) {
                    case 2:
                        //check for new settlment
                        //check if a new settlment can go there
                        drawHitBox = canBuildSettlement(settlement, true);
                        break;
                    case 3:
                        //check for upgrading to city
                        //check if an upgrade can be made
                        if ((!settlement.isLarge()) && settlement.getPlayer() == currentPlayer) {
                            drawHitBox = true;
                        }
                        break;
                    default:
                        //error
                        instructionLbl.setText("Error drawing settlment hitboxes");
                        subInstructionLbl.setText("Please contact the developer");
                        break;
                }

                //draw the hitbox
                if (drawHitBox) {
                    //draw the high light
                    g2d.setColor(new java.awt.Color(255, 255, 225, 128));
                    g2d.fillRect(settlement.getXPos() - getImgWidth(image) / 2, settlement.getYPos() - getImgHeight(image) / 2, getImgWidth(image), getImgHeight(image));
                    //draw the boarder
                    g2d.setColor(new java.awt.Color(255, 255, 225));
                    g2d.drawRect(settlement.getXPos() - getImgWidth(image) / 2, settlement.getYPos() - getImgHeight(image) / 2, getImgWidth(image), getImgHeight(image));
                }
            }
        }
        /*
        //temp draw settlemt ports
        for (int i = 0; i < portSettlements.length; i++) {

            for (NodeSettlement node : portSettlements[i]) {

                g2d.drawRect(node.getXPos() - getImgWidth(RED_HOUSE_S) / 2,
                        node.getYPos() - getImgHeight(RED_HOUSE_S) / 2,
                        getImgWidth(RED_HOUSE_S),
                        getImgHeight(RED_HOUSE_S));

                g2d.drawString(Integer.toString(i), node.getXPos(), node.getYPos());

            }

        }*/

        // If a turn is currently going on, render the current player's cards
        if (!inbetweenTurns) {

            int playerID;

            //if online then always show the local player's cards
            if (onlineMode != -1) {
                playerID = onlineMode;
            } else { //show the current players cards
                playerID = currentPlayer;
            }

            //decide which cards to draw: development or resource
            if (!showDevCards) {

                // Get the number of cards the player has
                int listSize = cards[playerID].size();

                //get the number of each card type the player has
                countCardTypes(listSize, playerID);

                // Calculate where the first card must go to center the list
                cardStartPosition = (int) ((superFrame.getWidth() / 2) - (listSize * getImgWidth(CARD_CLAY) + (listSize - 1) * (10 / scaleFactor)) / 2);

                //check if the cards would go off the screen
                //by checking if the start pos of the cards would be past the ending of the exit button
                if (cardStartPosition < (backNoSaveBtn.getXPos() + getImgWidth(backNoSaveBtn.getBaseImage()))) {
                    drawCardStacks[playerID] = true;

                    //draw the number of cards the payer has of each type
                    //change the font
                    Font tempFont = g2d.getFont(); //save the current stroke
                    g2d.setFont(new Font("Times New Roman", Font.BOLD, (int) (40 / scaleFactor))); //overwrite it      
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
                                (int) (superFrame.getHeight() - (getImgHeight(image) * 1.125)),
                                getImgWidth(image),
                                getImgHeight(image),
                                null);

                        //draw the number of cards of that type
                        g2d.drawString("x" + cardTypeCount[i],
                                cardStackXPositions[i] + getImgWidth(image), //align the number to the right edge of the card
                                (int) (superFrame.getHeight() - (getImgHeight(image) * 1.125) + getImgHeight(image) / 2));

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
                                        (int) (superFrame.getHeight() - (getImgHeight(image) * 1.125)),
                                        getImgWidth(image),
                                        getImgHeight(image));
                                //draw the boarder
                                g2d.setColor(new java.awt.Color(102, 62, 38));
                                Stroke tempStroke = g2d.getStroke();
                                g2d.setStroke(new BasicStroke((float) (5 / scaleFactor)));
                                g2d.drawRect(cardStackXPositions[i],
                                        (int) (superFrame.getHeight() - (getImgHeight(image) * 1.125)),
                                        getImgWidth(image),
                                        getImgHeight(image));
                                g2d.setStroke(tempStroke);
                                g2d.setColor(new java.awt.Color(255, 255, 225));
                            }
                        }

                    }

                    //restore the old font
                    g2d.setFont(tempFont);

                } else { //if the cards would NOT go off the screen
                    drawCardStacks[playerID] = false;

                    // Draw the player's cards
                    // Reuse the image variable
                    int type;
                    for (int i = 0; i < listSize; i++) {
                        // Get the card type
                        type = cards[playerID].get(i);
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
                                (cardStartPosition + (getImgWidth(CARD_CLAY) + 10) * i),
                                (int) (superFrame.getHeight() - (getImgHeight(image) * 1.125)),
                                getImgWidth(image),
                                getImgHeight(image),
                                null);

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
                                g2d.fillRect((cardStartPosition + (getImgWidth(CARD_CLAY) + 10) * i),
                                        (int) (superFrame.getHeight() - (getImgHeight(image) * 1.125)),
                                        getImgWidth(image),
                                        getImgHeight(image));
                                //draw the boarder
                                g2d.setColor(new java.awt.Color(102, 62, 38));
                                Stroke tempStroke = g2d.getStroke();
                                g2d.setStroke(new BasicStroke((float) (5 / scaleFactor)));
                                g2d.drawRect((cardStartPosition + (getImgWidth(CARD_CLAY) + 10) * i),
                                        (int) (superFrame.getHeight() - (getImgHeight(image) * 1.125)),
                                        getImgWidth(image),
                                        getImgHeight(image));
                                g2d.setStroke(tempStroke);
                            }
                        }

                    }
                }
            } else { //if the dev cards are being drawn instead

                // Get the number of dev cards the player has
                int listSize = devCards[playerID].size();

                //get the number of each dev card type the player has
                //setup an array to hold the results
                devCardTypeCount = new int[9];

                //loop thorugh and populate the array
                for (int i = 0; i < listSize; i++) {

                    devCardTypeCount[devCards[playerID].get(i) - 1]++;
                }

                // Calculate where the first card must go to center the list
                devCardStartPosition = (int) ((superFrame.getWidth() / 2) - (listSize * getImgWidth(DEV_CARD_KNIGHT) + (listSize - 1) * (10 / scaleFactor)) / 2);

                //check if the cards would go off the screen
                //by checking if the start pos of the cards would be past the ending of the exit button
                if ((devCardStartPosition < (backNoSaveBtn.getXPos() + getImgWidth(backNoSaveBtn.getBaseImage())))) {
                    drawDevCardStacks[playerID] = true;

                    //draw the number of cards the payer has of each type
                    //change the font
                    Font tempFont = g2d.getFont(); //save the current stroke
                    g2d.setFont(new Font("Times New Roman", Font.BOLD, (int) (40 / scaleFactor))); //overwrite it      
                    g2d.setColor(new java.awt.Color(255, 255, 225));

                    //loop through and draw the stacked cards
                    for (int i = 0; i < 5; i++) {

                        switch (i) {
                            case 0:
                                image = DEV_CARD_KNIGHT;
                                break;
                            case 1:
                                image = DEV_CARD_PROGRESS_ROAD;
                                break;
                            case 2:
                                image = DEV_CARD_PROGRESS_MONO;
                                break;
                            case 3:
                                image = DEV_CARD_PROGRESS_YOP;
                                break;
                            case 4:
                                image = DEV_CARD_VP_MARKET;
                                break;
                            default:
                                image = ERROR_IMAGE;
                                break;
                        }

                        //draw one each of the 5 card types
                        //draw the card image
                        g2d.drawImage(image,
                                devCardStackXPositions[i], //align the card to the left edge of the water ring 
                                (int) (superFrame.getHeight() - (getImgHeight(DEV_CARD_KNIGHT) * 1.125)),
                                getImgWidth(image),
                                getImgHeight(image),
                                null);

                        String devCardNum; //the number of dev cards the player has of that catagory

                        //get the number to write next to the card
                        if (i < 4) {
                            devCardNum = Integer.toString(devCardTypeCount[i]);
                        } else if (i == 4) {
                            devCardNum = Integer.toString(devCardTypeCount[4] + devCardTypeCount[5] + devCardTypeCount[6] + devCardTypeCount[7] + devCardTypeCount[8]);
                        } else {
                            devCardNum = "Error";
                        }

                        //draw the number of cards of that type
                        g2d.drawString("x" + devCardNum,
                                devCardStackXPositions[i] + getImgWidth(image), //align the number to the right edge of the card
                                (int) (superFrame.getHeight() - (getImgHeight(image) * 1.125) + getImgHeight(image) / 2));

                        //draw the hitbox but only if there are cards availible to be taken. No hitbox around a stack that has 0 cards.
                        if (showDevCardHitbox && devCardTypeCount[i] > 0) {
                            //decide if to draw this one in the loop
                            if (playerID != currentPlayer) { //if the cards being drawn don't match the current player don't show hitboxes
                                drawSpecificHitbox = false;
                            } else { //make sure the card is an action type card
                                drawSpecificHitbox = i < 4;
                            }

                            if (drawSpecificHitbox) {
                                //draw the high light
                                g2d.setColor(new java.awt.Color(255, 255, 225, 128));
                                g2d.fillRect(devCardStackXPositions[i],
                                        (int) (superFrame.getHeight() - (getImgHeight(image) * 1.125)),
                                        getImgWidth(image),
                                        getImgHeight(image));
                                //draw the boarder
                                g2d.setColor(new java.awt.Color(102, 62, 38));
                                Stroke tempStroke = g2d.getStroke();
                                g2d.setStroke(new BasicStroke((float) (5 / scaleFactor)));
                                g2d.drawRect(devCardStackXPositions[i],
                                        (int) (superFrame.getHeight() - (getImgHeight(image) * 1.125)),
                                        getImgWidth(image),
                                        getImgHeight(image));
                                g2d.setStroke(tempStroke);
                                g2d.setColor(new java.awt.Color(255, 255, 225));
                            }
                        }

                    }

                    //restore the old font
                    g2d.setFont(tempFont);

                } else { //if the dev cards would NOT go off screen
                    drawDevCardStacks[playerID] = false;

                    // Draw the player's dev cards
                    // Reuse the image variable
                    int type;
                    for (int i = 0; i < listSize; i++) {
                        // Get the card type
                        type = devCards[playerID].get(i);
                        // Get the image for that card
                        switch (type) {
                            case 1: //knight card
                                image = DEV_CARD_KNIGHT;
                                break;
                            case 2: //road building card
                                image = DEV_CARD_PROGRESS_ROAD;
                                break;
                            case 3: //monopoly card
                                image = DEV_CARD_PROGRESS_MONO;
                                break;
                            case 4: //year of pleanty card
                                image = DEV_CARD_PROGRESS_YOP;
                                break;
                            case 5: //VP card market
                                image = DEV_CARD_VP_MARKET;
                                break;
                            case 6: //VP card university
                                image = DEV_CARD_VP_UNI;
                                break;
                            case 7: //VP card great hall
                                image = DEV_CARD_VP_HALL;
                                break;
                            case 8: //VP card chapel
                                image = DEV_CARD_VP_CHAPEL;
                                break;
                            case 9: //VP card library
                                image = DEV_CARD_VP_LIBRARY;
                                break;
                            default: //error image
                                image = ERROR_IMAGE;
                                break;
                        }

                        // Draw the card
                        g2d.drawImage(image,
                                (devCardStartPosition + (getImgWidth(DEV_CARD_KNIGHT) + 10) * i),
                                (int) (superFrame.getHeight() - (getImgHeight(image) * 1.125)),
                                getImgWidth(image),
                                getImgHeight(image),
                                null);

                        //draw the hitbox
                        if (showDevCardHitbox) {
                            //decide if to draw this one in the loop
                            if (playerID != currentPlayer) { //if the cards being drawn don't match the current player don't show hitboxes
                                drawSpecificHitbox = false;
                            } else {
                                drawSpecificHitbox = type < 5; //make sure the card is an action type card
                            }

                            if (drawSpecificHitbox) {
                                //draw the high light
                                g2d.setColor(new java.awt.Color(255, 255, 225, 128));
                                g2d.fillRect((devCardStartPosition + (getImgWidth(DEV_CARD_KNIGHT) + 10) * i),
                                        (int) (superFrame.getHeight() - (getImgHeight(image) * 1.125)),
                                        getImgWidth(image),
                                        getImgHeight(image));
                                //draw the boarder
                                g2d.setColor(new java.awt.Color(102, 62, 38));
                                Stroke tempStroke = g2d.getStroke();
                                g2d.setStroke(new BasicStroke((float) (5 / scaleFactor)));
                                g2d.drawRect((devCardStartPosition + (getImgWidth(DEV_CARD_KNIGHT) + 10) * i),
                                        (int) (superFrame.getHeight() - (getImgHeight(image) * 1.125)),
                                        getImgWidth(image),
                                        getImgHeight(image));
                                g2d.setStroke(tempStroke);
                            }
                        }

                    }

                }
            }
        }

        /*
         * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= Start SetterBtn Drawing =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
         *
         */
        //make sure the buttons are all in the right spot
        settlerVarPos(g2d); //update the positions of the custon labels because some buttons are bassed off of them

        //draw the custom SettlerBtns
        //loop through the buttons
        for (SettlerBtn btn : settlerBtns) {
            //draw the base        
            drawSettlerBtn(g2d, btn.getBaseImage(), btn, 0);

            //draw the text
            drawSettlerBtn(g2d, btn.getTextImage(), btn, 0);

            //draw the tabSelected overlay if required
            if (btn.isTabSelected()) {
                //draw the left
                g2d.drawImage(btn.getTabSelectionImage()[0],
                        btn.getXPos() - GamePanel.scaleInt(5),
                        btn.getYPos() - GamePanel.scaleInt(5),
                        getImgWidth(btn.getTabSelectionImage()[0]),
                        getImgHeight(btn.getTabSelectionImage()[0]), null);
                //draw the right
                g2d.drawImage(btn.getTabSelectionImage()[1],
                        btn.getXPos() + getImgWidth(btn.getBaseImage()) + GamePanel.scaleInt(5) - getImgWidth(btn.getTabSelectionImage()[1]),
                        btn.getYPos() - GamePanel.scaleInt(5),
                        getImgWidth(btn.getTabSelectionImage()[1]),
                        getImgHeight(btn.getTabSelectionImage()[1]), null);
            }

            //draw the disabled overlay if required
            if (!btn.isEnabled()) {
                drawSettlerBtn(g2d, btn.getDisabledImage(), btn, 0);
            }
            //draw the mouseHover overlay if required
            if (btn.isMouseHover()) {
                drawSettlerBtn(g2d, btn.getHoverImage(), btn, 1);
            }

        }

        /*
         * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= End SetterBtn Drawing =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
         * 
         * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= Start SetterRadioBtn Drawing =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
         *
         */
        settlerVarPos(g2d); //update the positions of the custon labels
        //go through and draw all the labels
        for (SettlerRadioBtn settlerRadioBtn : settlerRadioBtns) {
            settlerRadioBtn.draw(g2d, this);
        }

        /*
         * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= End SetterRadioBtn Drawing =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
         *
         *
         * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= Start SetterLbl Drawing =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
         *
         */
        settlerVarPos(g2d); //update the positions of the custon labels
        //go through and draw all the labels
        for (SettlerLbl settlerLbl : settlerLbls) {
            settlerLbl.draw(g2d);
        }

        //reset the font
        g2d.setFont(new Font("Times New Roman", Font.PLAIN, (int) (20 / scaleFactor)));

        /*
         * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= End SetterLbl Drawing =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
         *
         */
        //draw the separator boxes around the UI elements
        //only if the user want to
        if (showMenuBoarder) {
            g2d.setStroke(new BasicStroke(scaleInt(2))); //make the stroke a little thicker
            //the trade box
            g2d.drawRect(tradeMenuLbl.getXPos() - scaleInt(5),
                    tradeMenuLbl.getYPos() - scaleInt(17),
                    (trade3to1Btn.getXPos() + getImgWidth(trade3to1Btn.getBaseImage()) + scaleInt(10)) - (tradeMenuLbl.getXPos()),
                    (trade2to1Btn.getYPos() + getImgHeight(trade2to1Btn.getBaseImage())) - tradeMenuLbl.getYPos() + scaleInt(22));
            //the build box
            g2d.drawRect(buildMenuLbl.getXPos() - scaleInt(5),
                    buildMenuLbl.getYPos() - scaleInt(17),
                    getImgWidth(buildRoadRBtn.getBaseImage()) + scaleInt(10),
                    (buildBtn.getYPos() + getImgHeight(buildBtn.getBaseImage())) - buildMenuLbl.getYPos() + scaleInt(22));
            //the dev card box
            g2d.drawRect(devCardMenuLbl.getXPos() - scaleInt(5),
                    devCardMenuLbl.getYPos() - scaleInt(17),
                    getImgWidth(toggleCardBtn.getBaseImage()) + scaleInt(10),
                    (useDevCardBtn.getYPos() + getImgHeight(useDevCardBtn.getBaseImage())) - devCardMenuLbl.getYPos() + scaleInt(22));
            //the dice menu box
            g2d.drawRect(rightDrawMargin - scaleInt(5),
                    scaleInt(435) - scaleInt(5),
                    getImgWidth(MATERIAL_KEY) + scaleInt(5),
                    (scaleInt(450) + getImgHeight(DICE_GRAY)) - scaleInt(435) + scaleInt(10));
            //the score board box
            g2d.drawRect(rightDrawMargin - scaleInt(60) + playerNumOffset - scaleInt(5),
                    scaleInt(580) - scaleInt(5),
                    getImgWidth(MATERIAL_KEY) + (60 - playerNumOffset) + scaleInt(5),
                    (scaleInt(740)) - scaleInt(580) + scaleInt(10));
        }

        //draw the game version info
        g2d.setColor(new java.awt.Color(255, 255, 225));
        g2d.drawString(Catan.GAME_VER, rightDrawMargin, scaleInt(30) + getImgHeight(MATERIAL_KEY));
        //draw the online mode
        String appendText; //the test to append
        //find the text to append
        if (onlineMode == -1) { //if in ofline mode say that
            appendText = "Offline";
        } else {
            appendText = "Online";
        }
        g2d.drawString("Online mode: " + appendText + " with ID of \'" + onlineMode + "\'", rightDrawMargin, scaleInt(50) + getImgHeight(MATERIAL_KEY));

        // Add alignment lines
        //g2d.drawLine(superFrame.getWidth() / 2, 0, superFrame.getWidth() / 2, superFrame.getHeight());
        //g2d.drawLine(0, superFrame.getHeight() / 2, superFrame.getWidth(), superFrame.getHeight() / 2);

        /*
        //draw the boarder overlay
        g2d.drawImage(WATER_RING_OVERLAY, 
                superFrame.getWidth() / 2 - getImgWidth(WATER_RING_OVERLAY) / 2,
                superFrame.getHeight() / 2 - getImgHeight(WATER_RING_OVERLAY) / 2, 
                getImgWidth(WATER_RING_OVERLAY), 
                getImgHeight(WATER_RING_OVERLAY), null);
         */
        //draw a test cursor for a custom ingame one
        /*g2d.drawImage(trade2to1Btn.getBaseImage(),
        mouseMotionPosX,
        mouseMotionPosY,
        getImgWidth(trade2to1Btn.getBaseImage()),
        getImgHeight(trade2to1Btn.getBaseImage()), null);*/
    }

    // </editor-fold>
    public void fire() {
        //debugs. Proof the game is actually redrawing.
        //showRoadHitbox = true;
        repaint();
    }

    /**
     * Return the Image object of a player turn indicator
     *
     * @param playerID the player number associated with the image
     * @param smallImage should the image be the small version
     * @return
     */
    private Image getPlayerImage(int playerID, boolean smallImage) {
        Image playerImage;

        if (smallImage) {
            if (inbetweenTurns) {
                playerImage = SMALL_PLAYER_NONE;
            } else if (playerID == 1) {
                playerImage = SMALL_PLAYER_RED;
            } else if (playerID == 2) {
                playerImage = SMALL_PLAYER_BLUE;
            } else if (playerID == 3) {
                playerImage = SMALL_PLAYER_ORANGE;
            } else if (playerID == 4) {
                playerImage = SMALL_PLAYER_WHITE;
            } else {
                playerImage = SMALL_PLAYER_NONE;
            }
        } else {
            if (inbetweenTurns) {
                playerImage = PLAYER_NONE;
            } else if (playerID == 1) {
                playerImage = PLAYER_RED;
            } else if (playerID == 2) {
                playerImage = PLAYER_BLUE;
            } else if (playerID == 3) {
                playerImage = PLAYER_ORANGE;
            } else if (playerID == 4) {
                playerImage = PLAYER_WHITE;
            } else {
                playerImage = PLAYER_NONE;
            }
        }

        return playerImage;
    }

    /**
     * Calculates the y position BEFORE realignment of a Tile to account for the
     * spacing add between from aspect ratio locked scaling.
     *
     * @param yPos
     * @return
     */
    public int getTileYPos(int yPos) {
        return (int) (yPos / 1080.0 * superFrame.getHeight()
                / //calculate the distorted position. This has spacing between that is an artifact of locking the aspect ratio when scaling
                //find the correct spacing factor based off a linear ration between the new apsect ratio and the internal 1080p one. 
                //This now leaves the tiles in the incorrect position but that can be later corrected by adding a constant value to all of the tiles
                (1.8 * ((float) superFrame.getHeight() / (float) superFrame.getWidth())));
    }

    /**
     * Calculates the x position BEFORE realignment of a Tile to account for the
     * spacing add between from aspect ratio locked scaling.
     *
     * @param tile
     * @return
     */
    private int getTileXPos(int xPos) {
        return (int) (xPos / 1920.0 * superFrame.getWidth()
                / //calculate the distorted position. This has spacing between that is an artifact of locking the aspect ratio when scaling
                //find the correct spacing factor based off a linear ration between the new apsect ratio and the internal 1080p one. 
                //This now leaves the tiles in the incorrect position but that can be later corrected by adding a constant value to all of the tiles
                (0.5625 * ((float) superFrame.getWidth() / (float) superFrame.getHeight())));
    }

    /**
     * Calculates the new scaled width of an image with a locked aspect ratio.
     *
     * @param image
     * @return
     */
    public final int getImgWidth(Image image) {

        if (superFrame.getWidth() > superFrame.getHeight()) {
            return (int) (getImgHeight(image) * ((float) image.getWidth(null) / image.getHeight(null)));
        } else {
            return (int) (image.getWidth(null) / 1920.0 * superFrame.getWidth());
        }

    }

    /**
     * Calculates the new scaled height of an image with a locked aspect ratio.
     *
     * @param image
     * @return
     */
    public final int getImgHeight(Image image) {
        if (superFrame.getWidth() > superFrame.getHeight()) {
            return (int) (image.getHeight(null) / 1080.0 * superFrame.getHeight());
        } else {
            return (int) (getImgWidth(image) / ((float) image.getWidth(null) / image.getHeight(null)));
        }
    }

    /**
     * Scale a number to match the resolution of the screen
     *
     * @param num
     * @return
     */
    public static int scaleInt(int num) {
        return (int) (num / scaleFactor);
    }

    /**
     * Load the data into the mater Tile ArrayList
     */
    private void loadTiles() {
        Tile newTile;
        for (int i = 0; i < 19; i++) {
            //int r = (int) (Math.random() * 6);
            newTile = new Tile(tilePos[i][0], tilePos[i][1], tileTypes[i], i); //set the position and a type based on the text file

            //add the harvest roll num
            newTile.setHarvestRollNum(tileHarvestRollNums[i]);

            //check for the desert tile and start the thief off there
            if (newTile.getType() == 0) { //type 0 is the desert type
                // Place a thief on this dessert tile
                newTile.setThief(true);
                // Record this tile as the one with the thief
                tileWithThief = i;
            }
            tiles.add(newTile);
        }
        // Add a null entry for the settlement noed file to reference where there isnt a connection
        tiles.add(null);
    }

    /**
     * Create the board's node network of settlements and roads. All of the node
     * data is read from files
     */
    private void loadNodes() {

        // Declare node attribute arrays
        // Settlement attribute arrayLists
        int settlementX;
        int settlementY;
        int settlementLinkToRoad1[] = new int[54];
        int settlementLinkToRoad2[] = new int[54];
        int settlementLinkToRoad3[] = new int[54];
        int settlementLinkToHex1[] = new int[54];
        int settlementLinkToHex2[] = new int[54];
        int settlementLinkToHex3[] = new int[54];
        // Road attribute arrayLists
        int roadX;
        int roadY;
        int roadRotation;
        int roadLinkToSettlement1[] = new int[72];
        int roadLinkToSettlement2[] = new int[72];

        // Declare variables
        Scanner fileReader;
        InputStream settlementFile = OldCode.class.getResourceAsStream("settlementData.txt");
        InputStream roadFile = OldCode.class.getResourceAsStream("roadData.txt");

        // Try to read the settlement file
        try {
            // Create the scanner to read the file
            fileReader = new Scanner(settlementFile);

            // Read the entire file into an array
            for (int i = 0; i < 54; i++) {
                // Read the position data
                settlementX = fileReader.nextInt();
                settlementY = fileReader.nextInt();
                // Read the road connection data
                settlementLinkToRoad1[i] = fileReader.nextInt();
                settlementLinkToRoad2[i] = fileReader.nextInt();
                settlementLinkToRoad3[i] = fileReader.nextInt();
                // Read the tile connection data
                settlementLinkToHex1[i] = fileReader.nextInt();
                settlementLinkToHex2[i] = fileReader.nextInt();
                settlementLinkToHex3[i] = fileReader.nextInt();

                // Add an unlinked settlement
                settlementNodes.add(new NodeSettlement(settlementX, settlementY, i));

                // Blank line is skipped by int reader
            }
        } catch (Exception e) {
            // Output the jsvs error to the standard output
            System.out.println("Error reading settlement data file: " + e);
        }

        // Try to read the road file
        try {
            // Create the scanner to read the file
            fileReader = new Scanner(roadFile);

            // Read the entire file into an array
            for (int i = 0; i < 72; i++) {
                // Read the position data
                roadX = fileReader.nextInt();
                roadY = fileReader.nextInt();
                // Read the rotation
                roadRotation = fileReader.nextInt();
                // Read the road connection data
                roadLinkToSettlement1[i] = fileReader.nextInt();
                roadLinkToSettlement2[i] = fileReader.nextInt();

                // Add a road linked with the settlements created above 
                roadNodes.add(new NodeRoad(roadX, roadY, roadRotation,
                        settlementNodes.get(roadLinkToSettlement1[i]),
                        settlementNodes.get(roadLinkToSettlement2[i])));

                // Blank line is skipped by int reader
            }
            // Add a null entry for the file to reference where there isnt a connection
            roadNodes.add(null);
        } catch (Exception e) {
            // Output the jsvs error to the standard output
            System.out.println("Error reading settlement data file: " + e);
        }

        // Link the settlements to the tiles and roads
        for (int i = 0; i < 54; i++) {
            // For every settlement

            // Set the roads of the settlement to the roads at the index store in the arrays
            // There are 3 arrays of roads to link to the 54 different settlements
            settlementNodes.get(i).setRoad(1, roadNodes.get(settlementLinkToRoad1[i]));
            settlementNodes.get(i).setRoad(2, roadNodes.get(settlementLinkToRoad2[i]));
            settlementNodes.get(i).setRoad(3, roadNodes.get(settlementLinkToRoad3[i]));

            // Repeat to link the 3 hexagons to the settlement
            settlementNodes.get(i).setTile(1, tiles.get(settlementLinkToHex1[i]));
            settlementNodes.get(i).setTile(2, tiles.get(settlementLinkToHex2[i]));
            settlementNodes.get(i).setTile(3, tiles.get(settlementLinkToHex3[i]));

        }

    }

    /**
     * Change the position of the WorldObject to match the scaled image
     *
     * @param arrayList The List of WorldObjects to go through and change the
     * positions
     * @param arrayCuttoff The number of indexes to ignore from the back. Used
     * to account for List with and without null components at the end.
     */
    private void scaleWorldObjectPos(ArrayList<? extends WorldObject> arrayList, int arrayCuttoff) {

        // Loop through all the tiles
        for (int i = 0; i < arrayList.size() - arrayCuttoff; i++) {

            //pick a way to add the corrdinates for scaling
            //choose a drawing type. One corrects for width and one corrects for height
            if (superFrame.getWidth() <= superFrame.getHeight()) {
                //set the x
                arrayList.get(i).setXPos((int) (arrayList.get(i).getXPos() / 1920.0 * superFrame.getWidth()));
                //set the y
                arrayList.get(i).setYPos((getTileYPos(arrayList.get(i).getYPos()) + tileYOffset));
            } else {
                //set the x
                arrayList.get(i).setXPos((getTileXPos(arrayList.get(i).getXPos()) + tileXOffset));
                //set the y
                arrayList.get(i).setYPos(((int) (arrayList.get(i).getYPos() / 1080.0 * superFrame.getHeight())));
            }
        }
    }

    /**
     * Updated the image positions of the type images for the ports. This only
     * brings them to where they need to be before major scaling point
     * re-mapping.
     */
    private void updatePortPos() {
        //loop through the ports
        for (int i = 0; i < ports.size(); i++) {
            ports.get(i).applyCoordinates();
            ports.get(i).applyTypeImageCoordinates();
        }
    }

    /**
     * read in the tile positions
     */
    private void loadTilePos() {

        // Declare variables
        Scanner fileReader;
        InputStream file = OldCode.class.getResourceAsStream("tilePos.txt");

        // Try to read the file
        try {
            // Create the scanner to read the file
            fileReader = new Scanner(file);

            // Read the entire file into an array
            for (int i = 0; i < 19; i++) {

                //old assignment. Not using scaling. Scaling is added after this coords are loaded in a different method
                for (int j = 0; j < 2; j++) {
                    // Read the line of the file into the next index
                    tilePos[i][j] = Integer.parseInt(fileReader.nextLine());
                }

                //skip the next blank line but only if the scanner is not at the end
                if (i < 18) {
                    fileReader.nextLine();
                }
            }
        } catch (Exception e) {
            // Output the jsvs error to the standard output
            System.out.println("Error reading Tile Position file: " + e);
        }
    }

    /**
     * read in the port positions and types. Also populate the Array List
     */
    private void loadPorts() {

        Port newPort; //the latest port being read in

        // Declare variables
        Scanner fileReader;
        InputStream file = OldCode.class.getResourceAsStream("portData.txt");

        // Try to read the file
        try {
            // Create the scanner to read the file
            fileReader = new Scanner(file);

            // Read the entire file in and create the Ports
            for (int i = 0; i < 9; i++) {

                //create the new Port
                newPort = new Port(tiles.get(Integer.parseInt(fileReader.nextLine())),
                        Integer.parseInt(fileReader.nextLine()),
                        Integer.parseInt(fileReader.nextLine()));

                //add it to the Array List
                ports.add(newPort);

                //skip the next blank line but only if the scanner is not at the end
                if (i < 8) {
                    fileReader.nextLine();
                }
            }
        } catch (Exception e) {
            // Output the jsvs error to the standard output
            System.out.println("Error reading trading port Position file: " + e);
        }
    }

    /**
     * Go the the next player for their turn. Also make sure to loop back to the
     * first player
     */
    private void nextPlayer() {

        newestSetupSettlment = null; //remove the most recent built house as the turn just changed

        //check if the game is in setup
        if (inSetup) {

            try { //try progressing the setup phase
                currentPlayer = setupTurnOrder[setupTurnOrderIndex]; //set the current player to the next one on the sequence

                //progress the "cursor"
                setupTurnOrderIndex++;

                setupUpdatePlayerTurnOrder();

            } catch (ArrayIndexOutOfBoundsException e) { //if there are no more prescribed turns that means setup is over

                //ensure that it's the setupTurnOrder that is out of bounds
                if (setupTurnOrderIndex == setupTurnOrder.length) {
                    inSetup = false;
                    initPlayerTurnOrder(); //reset the order of the sub player in the even that they got messed up
                    currentPlayer = 1; //make sure that player 1 is starting
                    // If enabled. give everyone their starting resources
                    if (giveStartingResources) {
                        NodeSettlement.giveStartingRes(settlementNodes, cards, totalCardsCollected); //give each player 1 of each resource from the second setup settlement
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Error changing players during settup\n" + e, "Turn Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error changing players during settup\n" + e, "Turn Error", JOptionPane.ERROR_MESSAGE);
                superFrame.setVisible(false);
                superFrame.getMainMenu().setVisible(true);
            }

        } else { //for regular turn changing

            currentPlayer++; //switch to the next player
            progressPlayerTurnOrder(); //step the ArrayList to reflect the new current player

            // And go back to player 1 if the number exceeds the total number of players
            if (currentPlayer > playerCount) {
                currentPlayer = 1;
            }
        }
    }

    /**
     * Step the playerTurnOrder by one. Moves index 0 to back, 1 to 0, etc.
     */
    private void progressPlayerTurnOrder() {
        //check if the thief is active
        if (!thiefIsStealing) {
            //save the value in index 0
            int holdVal = playerTurnOrder.get(0);
            //remove it from index 0
            playerTurnOrder.remove(0);
            //add it back to the end
            playerTurnOrder.add(holdVal);
        }
    }

    /**
     * Setup the playerTurnOrder ArrayList
     */
    private void initPlayerTurnOrder() {
        playerTurnOrder = new ArrayList<>();
        //load the playerTurnOrder
        for (int i = 0; i < playerCount; i++) {
            playerTurnOrder.add(i + 1);
        }
    }

    private void randomizeTiles() {
        //randomly select a number of times to shuffle the board
        int numShuffle/* = (int) (Math.random() * 15) + 25*/;
        numShuffle = 1000;
        int tempNumHold; //the value that is being swapped
        int numSlot1; //the index being swaped from
        int numSlot2; //the index being swapped to

        //shuffle the board types
        for (int i = 0; i < numShuffle; i++) {
            //select the first slot
            numSlot1 = (int) (Math.random() * tileTypes.length);

            //save its value for the tile type
            tempNumHold = tileTypes[numSlot1];

            //select the second slot
            numSlot2 = (int) (Math.random() * tileTypes.length);

            //overwrite the first value with the second
            tileTypes[numSlot1] = tileTypes[numSlot2];

            //now overwrite the second with what used to be in the first
            tileTypes[numSlot2] = tempNumHold;
        }

        //shuffle the board harvest numbers
        for (int i = 0; i < numShuffle; i++) {
            //select the first slot
            numSlot1 = (int) (Math.random() * tileHarvestRollNums.length);

            //save its value for the tile type
            tempNumHold = tileHarvestRollNums[numSlot1];

            //select the second slot
            numSlot2 = (int) (Math.random() * tileHarvestRollNums.length);

            //overwrite the first value with the second
            tileHarvestRollNums[numSlot1] = tileHarvestRollNums[numSlot2];

            //now overwrite the second with what used to be in the first
            tileHarvestRollNums[numSlot2] = tempNumHold;
        }
        //tileTypes = new int[]{1, 3, 4, 2, 2, 5, 1, 4, 3, 0, 4, 2, 4, 5, 1, 2, 3, 3, 5};
        //tileTypes = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    }

    /**
     * Decides if a trade is valid. This check prevents selecting a clay trade
     * but not having more than 4 cards or something other than clay
     *
     * @param resourceType
     * @param tradeType
     * @return
     */
    private boolean canTradeTo(int resourceType, int tradeType) {
        boolean showPort = false;

        //loop through all the card types
        for (int i = 0; i < cardTypeCount.length; i++) {
            //check if that port can be used
            if (cardTypeCount[i] >= minTradeCardsNeeded && (i + 1) != resourceType) {
                showPort = true;
            }
        }

        return showPort;
    }

    /**
     * Decides if a Port's hit-box should be drawn if is 2:1 mode
     *
     * @param resourceType
     * @return
     */
    private boolean canTradeSecializedTo(int resourceType) {
        boolean showPort = false;

        //create an array to store how many cards of each type the player has
        countNumCardTypes(currentPlayer);

        //do not show the port if it is the 2:1 port that the player has. This prevents trading wood for wood.
        if (!playerHasPort[currentPlayer][resourceType]) {
            showPort = true;
        } else { //if the player does have that port check if they have another 2:1
            for (int i = 1; i < playerHasPort[currentPlayer].length; i++) {
                if (playerHasPort[currentPlayer][i] && numCardType[i] >= 2 && i != resourceType) { //if the player owns that 2:1 port check if they have enough cards to use it and that it is a differnt port
                    showPort = true;
                }
            }
        }

        return showPort;
    }

    /**
     * Reset variables to their neutral state when there is no building
     */
    private void cancelBuilding() {
        //if there is turn off any building mode currently
        buildingObject = 0;
        showRoadHitbox = false;
        showSettlementHitbox = false;
        // Change the button back to the build button
        buildBtn.setMode(1);//set it to "Build"
    }

    /**
     * Update the playerTurnOrder, to represent the arrays determining the setup
     * order in setup mode
     */
    private void setupUpdatePlayerTurnOrder() {
        //get the sub players to reflect the next few turns
        playerTurnOrder.clear(); //reset the ArrayList

        int nextPlayerIfEndOfSetup = 1; //the next player to display in the sub player lineup if the setup phase is ending.

        //add the amount of elements equal to the number of players minus 1
        for (int i = 0; i < playerCount; i++) {

            //check if the inxes exists
            if ((setupTurnOrderIndex - 1) + i < setupTurnOrder.length) { //subtract 1 from setupTurnOrderIndex to the current turn, instead of where the cursor is for ready for next turn
                playerTurnOrder.add(setupTurnOrder[(setupTurnOrderIndex - 1) + i]);
            } else {
                //if that index does not exist that means the setup phase is nearing its end
                //for that reason show the standard order of players starting with player 1
                playerTurnOrder.add(nextPlayerIfEndOfSetup);
                nextPlayerIfEndOfSetup++; //show the next player next time
            }
        }
    }

    /**
     * Recursively check the length of a branch of roads. If a branch is found
     * to be longer than the current longest road overwrite the data to match
     * the new long longest road.
     *
     * @param road
     * @param branchLength
     */
    private void checkForLongestRoad(NodeRoad road, int branchLength, int playerNum) {

        ArrayList<NodeRoad> roadsToCheck = new ArrayList<>(); //List of roads to check with recusion

        //check if the given branch length is larger than the current longest road
        if (branchLength > longestRoadData.getSize()) {
            //if it is over write the data
            //the length
            longestRoadData.setSize(branchLength);
            //set the new player 
            longestRoadData.setPlayerNum(playerNum);
        }

        //check if this branch is longer than the specifica players longest road
        if (branchLength > playerLongestRoadSegments[playerNum]) {
            playerLongestRoadSegments[playerNum] = branchLength;
        }

        //for each of the two settlments on the end of the road 
        for (int i = 1; i < 3; i++) {

            //make sure the settlment node hasn't already been check but a call higher up on the Stack
            if (!alreadyCheckedSettlements.contains(road.getSettlement(i))) {

                //check the three roads on that settlment node
                for (int j = 1; j < 4; j++) {

                    //System.out.println(road.getSettlement(i).getRoad(j));
                    //System.out.println(alreadyCheckedRoad.contains(road));
                    //System.out.println("");
                    //make sure that it isn't a null road
                    //also ensure that the new road being checked is owned by the same player
                    //also ensure that the new road being checked is not the road passed as a perameter
                    //also ensure that this road has not been checked before
                    if (road.getSettlement(i).getRoad(j) != null
                            && road.getSettlement(i).getRoad(j).getPlayer() == playerNum
                            && !road.equals(road.getSettlement(i).getRoad(j))
                            && !alreadyCheckedRoad.contains(road)) {

                        roadsToCheck.add(road.getSettlement(i).getRoad(j));
                    }
                }
            }

            //add the settlemnt to the list of already check one. This prevets directional backtracking
            alreadyCheckedSettlements.add(road.getSettlement(i));
        }

        //save this road so that it cannot be checked again
        alreadyCheckedRoad.add(road);

        //now loop through all the roads needed to be checked and check them
        for (int i = 0; i < roadsToCheck.size(); i++) {
            checkForLongestRoad(roadsToCheck.get(i), branchLength + 1, playerNum);
        }
    }

    /**
     * Draw a SettlerBtn with Graphics 2D
     *
     * @param g2d
     * @param btnImage
     * @param btn
     */
    private void drawSettlerBtn(Graphics2D g2d, Image btnImage, SettlerBtn btn, int drawMode) {
        //get the width and height of the image
        int width;
        int height;
        if (drawMode == 0) { //draw mode is image dimensions
            width = getImgWidth(btnImage);
            height = getImgHeight(btnImage);
        } else { //1 is button dimensions
            width = getImgWidth(btn.getBaseImage());
            height = getImgHeight(btn.getBaseImage());
        }

        g2d.drawImage(btnImage,
                btn.getXPos(),
                btn.getYPos(),
                width,
                height, null);
    }

    /**
     * Return all variables using in controlling development card usage to
     * resting state.
     */
    private void resetUsingDevCards() {
        //re-enable the turn switch button
        turnBtnEnabled = true;

        //hide the hitbox again
        showSubPlayerHitbox = false;

        useDevCardBtn.setMode(0);

        //remove a dev card
        devCards[currentPlayer].remove(new Integer(usingDevCard));

        //if the dev card that was just remove was a knight card
        if (usingDevCard == 1) {
            //add to the player's army size
            playerArmySize[currentPlayer]++;

            //check if that addition changed the player with the largest army
            if (playerArmySize[currentPlayer] > largestArmyData.getSize() && playerArmySize[currentPlayer] >= 3) { //must also have 3 or more cards
                //remove point from the old player who had the largest army
                if (largestArmyData.getPlayerNum() != 0) { //don't remove point from player 0
                    victoryPoints[largestArmyData.getPlayerNum()] -= 2;
                }

                //update the player with the largest army
                largestArmyData.setPlayerNum(currentPlayer);
                largestArmyData.setSize(playerArmySize[currentPlayer]);

                //give that player 2 VP
                victoryPoints[currentPlayer] += 2;
            }
        }

        //reset all the use dev card vars
        usingDevCard = -1;

        //register the user using a dev card
        userPlayedDevCard = true;
    }

    /**
     * Initialize cardTypeCount and assign it values.
     *
     * @param listSize
     */
    private void countCardTypes(int listSize, int playerNum) {
        //setup an array to hold the results
        cardTypeCount = new int[5];

        //loop thorugh and populate the array
        for (int i = 0; i < listSize; i++) {
            cardTypeCount[cards[playerNum].get(i) - 1]++;
        }
    }

    /**
     * Initialize numCardType and assign it values.
     */
    private void countNumCardTypes(int playerNum) {
        numCardType = new int[6]; //index 0 is empty and index 1-5 correspond to the card type

        //sum up the cards of each type
        for (int i = 0; i < cards[playerNum].size(); i++) {
            numCardType[cards[playerNum].get(i)]++;
        }
    }

    /**
     * Get a list of the settlements that allow access to each port.
     */
    private void generatePortSettlements() {

        ArrayList<Tile> portTiles = new ArrayList<>();
        boolean onCoast;
        boolean onPortTile;
        int portNum;
        int altPortNum; //if there is another portnum

        //loop through the ports and get a list of their Tiles
        for (Port port : ports) {
            portTiles.add(port.getLinkedTile());
        }

        //do a first rough pass getting every node it should but also some more
        //loop through the settlements
        for (NodeSettlement node : settlementNodes) {

            //reset the bool vars
            onCoast = false;
            onPortTile = false;

            //reset the ints
            portNum = 0;
            altPortNum = 0;

            //loop through the 3 tile the settlement is on use int range 1-3
            for (int k = 1; k < 4; k++) {
                //check if this tile is the same as a port's tile
                if (portTiles.contains(node.getTile(k))) {
                    onPortTile = true;

                    //save the port num
                    //only use the alt if the main is already used
                    if (portNum == 0) {
                        portNum = portTiles.indexOf(node.getTile(k));
                    } else {
                        altPortNum = portTiles.indexOf(node.getTile(k));
                    }
                }

                //also check if this tile is the null tile meaning the settlemnt is on the coast
                if (node.getTile(k) == null) {
                    onCoast = true;
                }
            }

            //only add that node it is meets the first round of selection
            //also only add it if there are less than 3 settlements. Limitiing a port to having max 3
            if (onCoast && onPortTile) {

                //check how many settlemtnts that port already has
                //only add it to a port if that port has less than 2 nodes
                if (portSettlements[portNum].size() < 3) {

                    //save that the new settlement is on a port and which one
                    portSettlements[portNum].add(node);
                } else if (portSettlements[altPortNum].size() < 3) {
                    portSettlements[altPortNum].add(node);
                }
            }

        }

        //of the max three settlements elimite the one that is not for that port
        //only run the emimination if that port has three
        for (int i = 0; i < portSettlements.length; i++) {

            if (portSettlements[i].size() > 3) {
                System.out.println("ERROR SETTING UP PORT SETTLEMENTS. More than 3 settlemtns");
                instructionLbl.setText("ERROR SETTING UP PORT SETTLEMENTS. More than 3 settlemtns");
            } else if (portSettlements[i].size() == 3) {

                //get the port orientation
                int orientation = ports.get(i).getOrientation();
                NodeSettlement safe1 = null;
                NodeSettlement safe2 = null; // ^^ the two nodes that are safe and the ones for that port
                NodeSettlement markedForRemoval = null; //the node to remove

                //for these since they are at the top and bottom the x have to be differant and the y have to be the same
                //loop through the nodes
                for (NodeSettlement node1 : portSettlements[i]) {

                    //go through them again to match everyone against every other one
                    for (NodeSettlement node2 : portSettlements[i]) {

                        //decide which method to use to find the one to elimate
                        switch (orientation) {
                            case 0:
                            case 3:
                                //for top and bottom

                                //preform the check
                                if (node1.getXPos() != node2.getXPos() && node1.getYPos() == node2.getYPos()) {
                                    safe1 = node1;
                                    safe2 = node2;
                                }
                                break;
                            case 1:
                                //side
                                //just select the first two
                                safe1 = portSettlements[i].get(0);
                                safe2 = portSettlements[i].get(1);
                                break;
                            case 2:
                                //side
                                //preform the check
                                if (node1.getXPos() > node2.getXPos() && node1.getYPos() < node2.getYPos()) {
                                    safe1 = node1;
                                    safe2 = node2;
                                }
                                break;
                            case 4:
                                //side
                                //preform the check
                                if (node1.getXPos() < node2.getXPos() && node1.getYPos() < node2.getYPos()) {
                                    safe1 = node1;
                                    safe2 = node2;
                                }
                                break;
                            case 5:
                                //side
                                //just select the first two
                                safe1 = portSettlements[i].get(0);
                                safe2 = portSettlements[i].get(1);
                                break;
                            default:
                                break;
                        }

                    }

                }

                if (safe1 != null) {
                    //elimate the incorrect one
                    for (NodeSettlement node : portSettlements[i]) {
                        if (node != safe1 && node != safe2) {
                            markedForRemoval = node;
                        }
                    }
                }
                portSettlements[i].remove(markedForRemoval);
            } else if (portSettlements[i].size() < 2) { //if it's less than 2
                System.out.println("ERROR SETTING UP PORT SETTLEMENTS. Less than 2 settlemtns. Port: " + i);
                instructionLbl.setText("ERROR SETTING UP PORT SETTLEMENTS. Less than 2 settlemtns");
            }

        }

    }

    private void populateSettlerComponents() {
        //copy over the same first turn button
        settlerComponents[0] = turnSwitchBtn;
        //add the radio buttons
        System.arraycopy(settlerRadioBtns, 0, settlerComponents, 1, settlerRadioBtns.length);
        //add the rest of the regular buttons
        System.arraycopy(settlerBtns, 1, settlerComponents, settlerRadioBtns.length + 1, settlerBtns.length - 3);
        //-3 because it leaves out the first turn button (already added), 
        //and the two exit buttons (not added)
    }

    /**
     * Given a SettlerComponent, set it to the one that is tab selected
     * (focused)
     *
     * @param btn
     */
    private void tabSelectBtn(SettlerComponent btn) {
        //focus this button with the tab selection
        settlerComponents[tabSelectedButton].setTabSelected(false);
        //loop through the settlerComponents to get the index
        for (int i = 0; i < settlerComponents.length; i++) {
            if (settlerComponents[i] == btn) {
                tabSelectedButton = i;
            }
        }
        //set it selected
        settlerComponents[tabSelectedButton].setTabSelected(true);
    }

    /**
     * If the game is in online mode update the server with the current game.
     */
    private void onlineUpdateServer() {
        //debug statemtn for updateing the game server
        //System.out.println("Updateing online");

        //check if the game is for online play
        if (onlineMode != -1) {
            //if it is send the save file to the server
            onlineClient.sendGameToServer();
        }

        //and check for a win
        checkForWin();
    }

    /**
     * Checks the game state if the current player wins the game
     */
    private void checkForWin() {
        // Check if the player has enough points to win
        if (victoryPoints[currentPlayer] >= victoryPointsToWin) {
            // If they have a winning amount of points end the game
            endGame();
        }
    }

    /**
     * Set the turn button text to show the "End player turn"
     */
    private void setTurnBtbTextEnd() {
        turnSwitchBtn.setMode(0); //set mode to "End Current Player's Turn"
    }

    /**
     * Set the turn button text to show the "Start player turn"
     */
    private void setTurnBtnTextStart() {
        turnSwitchBtn.setMode(currentPlayer);//set mode to "Start Player " + currentPlayer + "'s Turn"
    }

    /**
     * Accessor for the super Frame reference
     *
     * @return
     */
    public GameFrame getSuperFrame() {
        return superFrame;
    }

    // <editor-fold defaultstate="collapsed" desc="Static Accessors and Mutators"> 
    /**
     * Set the number of players playing the game
     *
     * @param playerCount
     */
    public static void setPlayerCount(int playerCount) {
        GamePanel.playerCount = playerCount;
    }

    /**
     * Get the number of players playing the game
     *
     * @return
     */
    public static int getPlayerCount() {
        return playerCount;
    }

    /**
     * Set whether or not the players receive startup resource cards when the
     * game leaves setup mode
     *
     * @param giveStartingResources
     */
    public static void setgiveStartingResources(boolean giveStartingResources) {
        GamePanel.giveStartingResources = giveStartingResources;
    }

    /**
     *
     * @return
     */
    public static boolean getgiveStartingResources() {
        return giveStartingResources;
    }

    /**
     * Set whether or not the the game will draw borders around the menus
     * grouping them together
     *
     * @param showMenuBoarder
     */
    public static void setShowMenuBoarder(boolean showMenuBoarder) {
        GamePanel.showMenuBoarder = showMenuBoarder;
        System.out.println(GamePanel.showMenuBoarder);
    }

    /**
     * Get whether or not the the game will draw borders around the menus
     * grouping them together
     *
     * @return
     */
    public static boolean getShowMenuBoarder() {
        return showMenuBoarder;
    }

    /**
     * Get the CatanClient responsible for interfacing with the server.
     *
     * @return
     */
    public static CatanClient getCatanClient() {
        return onlineClient;
    }

    /**
     * Set the CatanClient responsible for interfacing with the server.
     *
     * @param onlineClient
     */
    public static void setCatanClient(CatanClient onlineClient) {
        GamePanel.onlineClient = onlineClient;
    }

    /**
     * Set the mode the game is in regarding online play
     *
     * @param onlineMode
     */
    public static void setOnlineMode(int onlineMode) {
        GamePanel.onlineMode = onlineMode;
    }

    /**
     * Get the mode the game is in regarding online play
     *
     * @return
     */
    public static int getOnlineMode() {
        return onlineMode;
    }

    /**
     * Get the width of the JFrame holding the GamePanel
     *
     * @return
     */
    public static int getFrameWidth() {
        return frameWidth;
    }

    /**
     * Set the width of the JFrame holding the GamePanel
     *
     * @param frameWidth
     */
    public static void setFrameWidth(int frameWidth) {
        GamePanel.frameWidth = frameWidth;
    }

    /**
     * Get the height of the JFrame holding the GamePanel
     *
     * @return
     */
    public static int getFrameHeight() {
        return frameHeight;
    }

    /**
     * Set the height of the JFrame holding the GamePanel
     *
     * @param frameHeight
     */
    public static void setFrameHeight(int frameHeight) {
        GamePanel.frameHeight = frameHeight;
    }

    /**
     * Get whether or not the game will doSnakeRules
     *
     * @return
     */
    public static boolean getDoSnakeRules() {
        return doSnakeRules;
    }

    /**
     * Set whether or not the game will doSnakeRules
     *
     * @param doSnakeRules
     */
    public static void setDoSnakeRules(boolean doSnakeRules) {
        GamePanel.doSnakeRules = doSnakeRules;
    }

    // </editor-fold>
    @Override
    public void mouseDragged(MouseEvent e) {
        //System.out.println("Dragged");
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        //System.out.println("Moved: " + e.getX() + ", an " + e.getY());
        mouseMotionPosX = e.getX();
        mouseMotionPosY = e.getY();
        mouseMoveAction();
    }
}
