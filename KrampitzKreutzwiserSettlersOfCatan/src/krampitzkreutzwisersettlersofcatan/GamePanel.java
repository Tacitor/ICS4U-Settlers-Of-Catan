/*
 * Lukas Krampitz and Evan Kreutzwiser
 * Nov 8, 2020
 * The JPanel that is the main part of the game. Handels drawing and logic for the game.
 */
package krampitzkreutzwisersettlersofcatan;

import dataFiles.OldCode;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import javax.swing.filechooser.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.ButtonModel;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import textures.ImageRef;
import static textures.ImageRef.*;

/**
 * @author Evan
 * @author Tacitor
 */
public class GamePanel extends javax.swing.JPanel {

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
    private boolean showCardHitbox; //used for picking which cards the thief steals
    private boolean[] drawCardStacks; //controls the mode cards are drown in. Stacked or fully layout
    private boolean showSettlementHitbox;
    private boolean showTileHitbox;
    private boolean showPortHitbox;
    private int tradingMode; //the gamestate regarding trading. 0 for no trade, 1 for a 4:1, 2 for a 3:1, and 3 for a 2:1.
    private int tradeResource; //the resource type number that the player wants to trade to, 0 for none.
    private int[] numCardType;
    private int[] cardTypeCount; //the count of how many cards of each type the current player has valid inxies are 0-4
    private int currentPlayer; // The player currently taking their turn
    private int playerRolled7; //the player who most recently rolled a seven
    private static int playerCount = 2; // The number of players in the game
    private static boolean giveStartingResources = true; // If players get startup resources
    private final ArrayList<Integer> cards[]; // Holds each player's list of cards in an ArrayList
    private final int victoryPoints[];
    private final int totalCardsCollected[];
    private final int[] cardStackXPositions; //the x positions to draw cardss when in stacked mode
    private int[] stealCardNum; //the number of cards to steal from each player
    private int cardStartPosition; //the xPos to start drawing cards at 
    private int victoryPointsToWin;
    private int thiefMoveCounter;
    private int tileWithThief; // The index of the tile with the thief
    private int buildingObject; // Indicates if/what the user is building. 
    // 0 when not placing anything, 1 for roads, 2 for settlements, and 3 for upgrading

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

    //fonts
    private final Font timesNewRoman;
    private final Font tahoma;
    private final Font dialog;

    //private Graphics awtGraphics;
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
        playerRolled7 = 0; //no player has rolled a 7 yet. So set the null player 0 to start
        cards = new ArrayList[playerCount + 1]; // Create the array of card lists
        // the +1 allows methods to use player IDs directly without subtracting 1
        victoryPoints = new int[playerCount + 1]; // Create the array of player's victory points
        // the +1 allows methods to use player IDs directly without subtracting 1
        stealCardNum = new int[playerCount + 1]; //create the array of how many cards need to be stolen
        //the +1 allows methods to use player IDs directly without subtracting 1
        drawCardStacks = new boolean[playerCount + 1];//create the array of how to draw the cards for each player
        //the +1 allows methods to use player IDs directly without subtracting 1
        totalCardsCollected = new int[5];
        //calculate the positions to draw the cards bassed off of the water ring. One on each end, one in the middle and one at each quarter way point
        cardStackXPositions = new int[]{superFrame.getWidth() / 2 - getImgWidth(WATER_RING) / 2 - getImgWidth(CARD_CLAY) / 2,
            superFrame.getWidth() / 2 - getImgWidth(WATER_RING) / 4 - getImgWidth(CARD_CLAY) / 2,
            superFrame.getWidth() / 2 - getImgWidth(CARD_CLAY) / 2,
            superFrame.getWidth() / 2 + getImgWidth(WATER_RING) / 4 - getImgWidth(CARD_CLAY) / 2,
            superFrame.getWidth() / 2 + getImgWidth(WATER_RING) / 2 - getImgWidth(CARD_CLAY) / 2};
        buildingObject = 0;
        showRoadHitbox = false;
        showSettlementHitbox = false;
        showCardHitbox = false;
        showTileHitbox = false;
        showPortHitbox = false;
        tradingMode = 0;
        tradeResource = 0;
        playerSetupRoadsLeft = 1;
        playerSetupSettlementLeft = 1;
        victoryPointsToWin = 10;
        thiefMoveCounter = 0;

        //init the playerTurnOrder
        playerTurnOrder = new ArrayList<>();
        //load the playerTurnOrder
        for (int i = 0; i < playerCount; i++) {
            playerTurnOrder.add(i + 1);
        }

        //init the canStealCardPlayers
        canStealCardPlayers = new ArrayList<>();

        //set a default save path
        saveAddress = System.getProperty("user.home") + "\\Desktop\\SettlersOfCatan.save";
        //initialize the filechooser
        saveFileChooser = new JFileChooser();
        //create a filter for catan save files
        FileFilter catanSaveFile = new FileFilter() {
            //add the description
            public String getDescription() {
                return "Catan Save File (*.catan)";
            }

            //add the logic for the filter
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
            victoryPoints[i] = 0; // Victory point counter
            drawCardStacks[i] = false;
        }

        // Intialize the card counter array
        for (int i = 0; i < totalCardsCollected.length; i++) {
            totalCardsCollected[i] = 0; // Victory point counter
        }

        // Initialize the window and board
        initComponents(); //add the buttons and other Swing elements

        //randomize the board
        randomizeTiles();

        loadTilePos(); //read in the coodinates of where each of the 19 tiles goes
        loadTiles(); //load the ArrayList of tiles with position and type data
        loadNodes(); // Create and link all of the board's settlement and road nodes
        loadPorts(); //read in all the data about the ports and populate the Array List with Ports

        // Add the mouse listener that calls the mouse click event handler
        addMouseListener(new MouseAdapter() {
            /**
             * Triggered when the user clicks on the game panel. Calls the game
             * panel's click event handler.
             *
             * @param evt The event representing the mouse click
             */
            @Override
            public final void mouseClicked(MouseEvent evt) {
                // Send the mouse event over to the game panel's click handlers
                mouseClick(evt);
            }
        });

        // Set the state of the builds buttons for the first player
        updateBuildButtons();

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
        updatePortTypePos();
        //remapPortTypePos();

        //get the fonts
        timesNewRoman = instructionLbl.getFont();
        tahoma = buildRoadRBtn.getFont();
        dialog = buildBtn.getFont();

        //scale the Swing elements
        buildRoadRBtn.setFont(new Font(tahoma.getName(), tahoma.getStyle(), (int) (tahoma.getSize() / scaleFactor)));
        buildSettlementSRBtn.setFont(new Font(tahoma.getName(), tahoma.getStyle(), (int) (tahoma.getSize() / scaleFactor)));
        buildSettlementLRBtn.setFont(new Font(tahoma.getName(), tahoma.getStyle(), (int) (tahoma.getSize() / scaleFactor)));

        buildBtn.setFont(new Font(dialog.getName(), dialog.getStyle(), (int) (dialog.getSize() / scaleFactor)));

        buildMenuLbl.setFont(new Font(timesNewRoman.getName(), timesNewRoman.getStyle(), (int) (timesNewRoman.getSize() / scaleFactor)));

        instructionPromptLbl.setFont(new Font(timesNewRoman.getName(), timesNewRoman.getStyle(), (int) (timesNewRoman.getSize() / scaleFactor)));
        instructionLbl.setFont(new Font(timesNewRoman.getName(), timesNewRoman.getStyle(), (int) (timesNewRoman.getSize() / scaleFactor)));
        subInstructionLbl.setFont(new Font(timesNewRoman.getName(), timesNewRoman.getStyle(), (int) ((timesNewRoman.getSize() - 4) / scaleFactor)));

        turnSwitchBtn.setFont(new Font(timesNewRoman.getName(), timesNewRoman.getStyle(), (int) (timesNewRoman.getSize() / scaleFactor)));

        titleLbl.setFont(new Font(timesNewRoman.getName(), Font.BOLD, (int) ((40) / scaleFactor)));

        //initialize the dice roll value
        diceRollVal = new String[]{"0", "0", ""}; //the first two indexies are the rollecd values and the third is the sum

        //init the offset for the "3d" overlap tiles
        threeDTileOffset = (int) (-20 / scaleFactor);

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buildBtnGroup = new javax.swing.ButtonGroup();
        backBtn = new javax.swing.JButton();
        turnSwitchBtn = new javax.swing.JButton();
        instructionPromptLbl = new javax.swing.JLabel();
        instructionLbl = new javax.swing.JLabel();
        buildMenuLbl = new javax.swing.JLabel();
        buildSettlementSRBtn = new javax.swing.JRadioButton();
        buildSettlementLRBtn = new javax.swing.JRadioButton();
        buildRoadRBtn = new javax.swing.JRadioButton();
        buildBtn = new javax.swing.JButton();
        subInstructionLbl = new javax.swing.JLabel();
        backNoSaveBtn = new javax.swing.JButton();
        titleLbl = new javax.swing.JLabel();
        trade3to1Btn = new javax.swing.JButton();
        trade4to1Btn = new javax.swing.JButton();
        trade2to1Btn = new javax.swing.JButton();

        setMaximumSize(new java.awt.Dimension(1920, 1080));
        setMinimumSize(new java.awt.Dimension(1920, 1080));
        setPreferredSize(new java.awt.Dimension(1920, 1080));

        backBtn.setBackground(new java.awt.Color(102, 62, 38));
        backBtn.setText("< Save and Exit");
        backBtn.setFocusable(false);
        backBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backBtnActionPerformed(evt);
            }
        });

        turnSwitchBtn.setBackground(new java.awt.Color(102, 62, 38));
        turnSwitchBtn.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        turnSwitchBtn.setText("End Current Player's Turn");
        turnSwitchBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                turnSwitchBtnActionPerformed(evt);
            }
        });

        instructionPromptLbl.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        instructionPromptLbl.setForeground(new java.awt.Color(255, 255, 225));
        instructionPromptLbl.setText("Instructions:");

        instructionLbl.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        instructionLbl.setForeground(new java.awt.Color(255, 255, 225));
        instructionLbl.setText("Place two roads and two small settlements each to start. Only one of each per setup round.");

        buildMenuLbl.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        buildMenuLbl.setForeground(new java.awt.Color(255, 255, 225));
        buildMenuLbl.setText("Build Menu:");

        buildBtnGroup.add(buildSettlementSRBtn);
        buildSettlementSRBtn.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        buildSettlementSRBtn.setForeground(new java.awt.Color(255, 255, 225));
        buildSettlementSRBtn.setSelected(true);
        buildSettlementSRBtn.setText("Small Settlement");
        buildSettlementSRBtn.setOpaque(false);

        buildBtnGroup.add(buildSettlementLRBtn);
        buildSettlementLRBtn.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        buildSettlementLRBtn.setForeground(new java.awt.Color(255, 255, 225));
        buildSettlementLRBtn.setText("Large Settlement");
        buildSettlementLRBtn.setOpaque(false);

        buildBtnGroup.add(buildRoadRBtn);
        buildRoadRBtn.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        buildRoadRBtn.setForeground(new java.awt.Color(255, 255, 225));
        buildRoadRBtn.setText("Road");
        buildRoadRBtn.setEnabled(false);
        buildRoadRBtn.setOpaque(false);

        buildBtn.setBackground(new java.awt.Color(102, 62, 38));
        buildBtn.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        buildBtn.setText("Build");
        buildBtn.setToolTipText("");
        buildBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buildBtnActionPerformed(evt);
            }
        });

        subInstructionLbl.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        subInstructionLbl.setForeground(new java.awt.Color(255, 255, 225));
        subInstructionLbl.setText("Select a type, click build, and then click where it shoud go.");

        backNoSaveBtn.setBackground(new java.awt.Color(102, 62, 38));
        backNoSaveBtn.setText("< Exit without saving");
        backNoSaveBtn.setFocusable(false);
        backNoSaveBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backNoSaveBtnActionPerformed(evt);
            }
        });

        titleLbl.setFont(new java.awt.Font("Times New Roman", 1, 40)); // NOI18N
        titleLbl.setForeground(new java.awt.Color(255, 255, 225));
        titleLbl.setText("Settlers of Catan");

        trade3to1Btn.setBackground(new java.awt.Color(102, 62, 38));
        trade3to1Btn.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        trade3to1Btn.setText("Trade 3:1");
        trade3to1Btn.setToolTipText("");
        trade3to1Btn.setEnabled(false);
        trade3to1Btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                trade3to1BtnActionPerformed(evt);
            }
        });

        trade4to1Btn.setBackground(new java.awt.Color(102, 62, 38));
        trade4to1Btn.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        trade4to1Btn.setText("Trade 4:1");
        trade4to1Btn.setToolTipText("");
        trade4to1Btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                trade4to1BtnActionPerformed(evt);
            }
        });

        trade2to1Btn.setBackground(new java.awt.Color(102, 62, 38));
        trade2to1Btn.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        trade2to1Btn.setText("Specialized Trade 2:1");
        trade2to1Btn.setToolTipText("");
        trade2to1Btn.setEnabled(false);
        trade2to1Btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                trade2to1BtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(buildSettlementSRBtn)
                            .addComponent(turnSwitchBtn)
                            .addComponent(buildMenuLbl)
                            .addComponent(buildRoadRBtn)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(instructionPromptLbl)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(instructionLbl)
                                    .addComponent(subInstructionLbl)))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(buildBtn, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(buildSettlementLRBtn, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addContainerGap(1157, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(trade3to1Btn, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(backBtn, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(backNoSaveBtn, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(titleLbl)
                            .addComponent(trade4to1Btn, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(trade2to1Btn, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(titleLbl)
                .addGap(18, 18, 18)
                .addComponent(turnSwitchBtn)
                .addGap(29, 29, 29)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(instructionPromptLbl)
                    .addComponent(instructionLbl))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(subInstructionLbl)
                .addGap(21, 21, 21)
                .addComponent(buildMenuLbl)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(buildRoadRBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buildSettlementSRBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buildSettlementLRBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(buildBtn)
                .addGap(29, 29, 29)
                .addComponent(trade4to1Btn, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(trade3to1Btn, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(trade2to1Btn, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 462, Short.MAX_VALUE)
                .addComponent(backNoSaveBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(backBtn)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void backBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backBtnActionPerformed

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

                // Hide this window and show the main menu
                superFrame.getMainMenu().setVisible(true); //show the main menu
                superFrame.setVisible(false); //hide the parent frame 
            }
        }

    }//GEN-LAST:event_backBtnActionPerformed

    private void buildBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buildBtnActionPerformed
        // If a turn is in progress
        if (!inbetweenTurns) {
            //check to make sure there isn't already another building trying to be made
            if (buildingObject != 0) {
                //if there is turn off any building mode currently
                buildingObject = 0;
                showRoadHitbox = false;
                showSettlementHitbox = false;
                // Change the button back to the build button
                buildBtn.setText("Build");
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
                    instructionLbl.setText("Sorry you don't have any large settlements to place.");
                    subInstructionLbl.setText("You do still have " + playerSetupSettlementLeft + " small settlement(s) left");
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
            buildBtn.setText("Cancel");
        }
    }//GEN-LAST:event_buildBtnActionPerformed

    /**
     * End/start turn button click handling, for turn switching
     *
     * @param evt The event generated by the button press
     */
    private void turnSwitchBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_turnSwitchBtnActionPerformed

        updateBuildButtons();
        repaint();

        // If the game is waiting to start the next player's turn
        if (inbetweenTurns) {

            // Change the button back to the End Turn button
            turnSwitchBtn.setText("End Current Player's Turn");

            // Begin the next turn
            inbetweenTurns = false; // No longer waiting to start a turn

            //update the instructions
            if (inSetup) {
                instructionLbl.setText("Place two roads and two small settlements each to start. Only one of each per setup round.");
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
                    turnSwitchBtn.setEnabled(false);

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
                            turnSwitchBtn.setEnabled(false);

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

            // And the user is done placing setup buildinga
            // Check if the player has enough points to win
            if (victoryPoints[currentPlayer] >= victoryPointsToWin) {
                // If they have a winning amount of points end the game
                endGame();
                return;
            }

            // Now the game is waiting to start the next turn
            inbetweenTurns = true;

            // Select the next player
            nextPlayer();

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
                buildBtn.setText("Build");
            }

            // Disable all of the building buttons
            buildBtn.setEnabled(false);
            buildRoadRBtn.setEnabled(false);
            buildSettlementSRBtn.setEnabled(false);
            buildSettlementLRBtn.setEnabled(false);
            trade4to1Btn.setEnabled(false);

            // Change the button to the Start Next Turn button
            turnSwitchBtn.setText("Start Player " + currentPlayer + "'s Turn");

            //update the instruction
            instructionLbl.setText("Please allow the next player to use the mouse");
            subInstructionLbl.setText("");

            // Redraw the board so the next player doesnt see the other player's cards
            repaint();
        } else if (playerSetupRoadsLeft != 0) {
            //let the player know that they have more setup roads to place
            instructionLbl.setText("Make sure you place your " + playerSetupRoadsLeft + " remaining road(s).");
            subInstructionLbl.setText("Build them from the build menu below.");
        } else if (playerSetupSettlementLeft != 0) {
            //let the player know that they have more setup roads to place
            instructionLbl.setText("Make sure you place your " + playerSetupSettlementLeft + " remaining small settlment(s).");
            subInstructionLbl.setText("Build them from the build menu below.");
        }

    }//GEN-LAST:event_turnSwitchBtnActionPerformed

    /**
     * Exit the gamePanel without saving
     *
     * @param evt
     */
    private void backNoSaveBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backNoSaveBtnActionPerformed
        int overwrite;
        overwrite = JOptionPane.showConfirmDialog(null, "Are you sure you would like to exit without saving?\nAll your progess will be lost.", "Confim", 0, JOptionPane.ERROR_MESSAGE);
        //If the user really want to leave let them
        if (overwrite == 0) {
            // Hide this window and show the main menu
            superFrame.getMainMenu().setVisible(true); //show the main menu
            superFrame.setVisible(false); //hide the parent frame 
        }
    }//GEN-LAST:event_backNoSaveBtnActionPerformed

    private void trade3to1BtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_trade3to1BtnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_trade3to1BtnActionPerformed

    private void trade4to1BtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_trade4to1BtnActionPerformed
        // TODO add your handling code here:
        //check what mode of trading the game is in
        if (tradingMode != 0) {
            //if the user clicked the cancel button reenable the turnswitch button and update the button lable
            turnSwitchBtn.setEnabled(true);
            trade4to1Btn.setText("Trade 4:1");
            //remove the intent to trade
            tradingMode = 0;
            //clear the resource if there was one
            tradeResource = 0;
            //hide the hitboxes
            showPortHitbox = false;
            showCardHitbox = false;
            //update the buildbuttons (should be renabeling them now)
            updateBuildButtons();
            repaint();
        } else {
            //if the user clicked the button in the inactive state activate it.
            //register the intent to trade
            tradingMode = 1;
            //diable turn switching
            turnSwitchBtn.setEnabled(false);
            //update the text of the button
            trade4to1Btn.setText("Cancel");
            //show the hitboxes
            showPortHitbox = true;
            //update the buildbuttons (should be disables for trading mode)
            updateBuildButtons();
            instructionLbl.setText("Please select the resource you would like to recive");
            subInstructionLbl.setText("Click the port that corresponds to the resource you would like to end up with.");
            repaint();
        }
    }//GEN-LAST:event_trade4to1BtnActionPerformed

    private void trade2to1BtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_trade2to1BtnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_trade2to1BtnActionPerformed

    /**
     * Handles mouse input, based on the state of the game
     *
     * @param event The event triggered by the mouse click
     */
    public void mouseClick(MouseEvent event) {
        // debug click listener
        //System.out.println("Click recieved");

        //check if the player is building
        if (buildingObject != 0) {
            //check what they are building
            if (buildingObject == 1) { //roads

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
                                // The card check has already been made, and the user has the right cards

                                // Remove the cards from the player's deck
                                // Remove 1 clay and 1 wood
                                cards[currentPlayer].remove(new Integer(1));
                                cards[currentPlayer].remove(new Integer(2));

                                // Set the road's player to the current player
                                roadNodes.get(i).setPlayer(currentPlayer);

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
                        buildBtn.setText("Build");
                        // Redraw the board
                        repaint();
                    }
                }
            } else if (buildingObject == 2) { //small house

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

                                //save the settelment just built
                                newestSetupSettlment = settlementNodes.get(i);

                                // Increment the player's victory point counter
                                victoryPoints[currentPlayer]++;

                                // Update the building buttons to reflect the player's new list of cards
                                // or number of setup buildings
                                updateBuildButtons();
                            }

                        } else {
                            instructionLbl.setText("Sorry but you can't take a claimed settlements.");
                            subInstructionLbl.setText("Try building where there isn't already another settlements");
                        }

                        // Stop building and hide the hitboxes
                        buildingObject = 0;
                        showSettlementHitbox = false;
                        // Change the button back to the build button
                        buildBtn.setText("Build");
                        // Redraw the board
                        repaint();
                    }
                }

            } else if (buildingObject == 3) { //large house

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
                        buildBtn.setText("Build");
                        // Redraw the board
                        repaint();
                    }
                }
            } else {
                System.out.println("Yeah we've got an error here chief. Building in the mouse click event printed me");
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
                                turnSwitchBtn.setEnabled(true);
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
                            turnSwitchBtn.setEnabled(true);
                            showCardHitbox = false;
                        }

                        updateBuildButtons();
                        repaint();

                    }
                }

            }

        } else if (thiefIsStealing && thiefJustStarted && currentPlayer == playerRolled7) { //check if the player clicked on a Tile to move the thief
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
                        //renable the turnSwitchBtn because the player has now succefully moved the theif and they can now move 
                        //onto slecting the cards they would like to discard if the requirements are met.
                        turnSwitchBtn.setEnabled(true);
                    }

                    updateBuildButtons();
                    repaint();
                }
            }

        } else if (canStealCardPlayers.size() > 0 && currentPlayer == playerRolled7 && !thiefIsStealing && !inbetweenTurns) { //check if the player just clicked to select another player to seal from
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
                        turnSwitchBtn.setEnabled(true);

                        //clear the canStealCardPlayers ArrayList
                        canStealCardPlayers.clear();

                        //update the instructions
                        instructionLbl.setText("You may now continue your turn.");
                        subInstructionLbl.setText("Building and trading is allowed assuming you have the correct cards.");
                        
                        //sort the cards first
                        quickSortCards(currentPlayer, 0, cards[currentPlayer].size() - 1);

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
        } else if (showPortHitbox && tradingMode != 0 && tradeResource == 0) { //check if the player is clicking a port to select a resource type to trade to
            
            int portPosX;
            int portPosY;
            
            //loop through the ports
            for (int i = 0; i < ports.size(); i++) {
                portPosX = ports.get(i).getTypePosX();
                portPosY = ports.get(i).getTypePosY();
                
                //check if there was a click on a port
                if (event.getX() > portPosX &&
                        event.getY() > portPosY &&
                        event.getX() < (portPosX + getImgWidth(ports.get(i).getTypeImage())) &&
                        event.getY() < (portPosY + getImgHeight(ports.get(i).getTypeImage()))) {
                    
                    //check if its a non general port
                    if (ports.get(i).getType() != 0) {
                        
                        //register the type the player want to trade TO
                        tradeResource = ports.get(i).getType();
                        
                        //turn off the hitboxes
                        showPortHitbox = false;
                        
                        //update the instructions for the next trading step
                        instructionLbl.setText("Now select a card");
                        subInstructionLbl.setText("This card should be of the type you would like to trade away");
                        
                        //show the card hitboxes
                        showCardHitbox = true;
                        
                        //update the screen
                        repaint();
                    }
                }
                
            }
            
        } else if (showCardHitbox && tradingMode != 0 && tradeResource != 0) {
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
                        
                        //check if the card is available to trade
                        if (cardTypeCount[i] >= 4) {
                            //debug click detection
                            //System.out.println("Card stack Clicked!");
                            Integer typeToRemove = i+1;
                            
                            //remove 4 of that card type
                            for (int j = 0; j < 4; j++) {
                                cards[currentPlayer].remove(typeToRemove);
                            }
                        }
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
                        
                        //check if the card is available to trade
                        if (numCardType[cards[currentPlayer].get(i)] >= 4) {
                            //debug click detection
                            //System.out.println("Card no stack Clicked!");
                            Integer typeToRemove = cards[currentPlayer].get(i);
                            
                            //remove 4 of that card type
                            for (int j = 0; j < 4; j++) {
                                cards[currentPlayer].remove(typeToRemove);
                            }
                        }

                    }
                }

            }
            
            //update the screen
            repaint();
        }
    }

    /**
     * Save game data to a file
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

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "An error occurred while trying to save the game.", "Saving Error", JOptionPane.ERROR_MESSAGE);
            success = false;
        }

        return success;
    }

    /**
     * Write to the save file
     *
     * @param writeAdress
     * @throws FileNotFoundException
     */
    private boolean writeToFile(String writeAdress) throws FileNotFoundException {

        try {
            PrintWriter saveFile = new PrintWriter(writeAdress); //begin writting to the file
            saveFile.println("SettlersOfCatanSaveV6"); //write a header to easily identify Settlers of Catan save files for loading
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
            saveFile.println("inSetup:");
            saveFile.println(inSetup);
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

            saveFile.println("Total cards collected:");
            for (int i = 0; i < totalCardsCollected.length; i++) {
                saveFile.println(totalCardsCollected[i]);
            }

            saveFile.println();

            saveFile.println("victoryPoints:");
            for (int i = 1; i < victoryPoints.length; i++) {
                saveFile.println(victoryPoints[i]);
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
                saveFile.println(); //add a line break below
            }

            //add the
            saveFile.close();
            return true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "The game is not able to save at this time. Invalid state\n", "Saving Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public void load(JFileChooser loadFileChooser) {
        //System.out.println("Yupp");

        int tempScannerVal;

        //load the save file 
        try {
            File savefile = new File(loadFileChooser.getSelectedFile().getPath());
            Scanner scanner = new Scanner(savefile);

            //check if it is valid (again)
            if (scanner.nextLine().equals("SettlersOfCatanSaveV6")) {
                //System.out.println("Yuppers");
            } else {
                throwLoadError();
            }

            if (scanner.nextLine().equals("playerCount:")) {
                playerCount = Integer.parseInt(scanner.nextLine());
                //System.out.println("Yuppers1");
            } else {
                throwLoadError();
            }

            if (scanner.nextLine().equals("thiefMoveCounter:")) {
                thiefMoveCounter = Integer.parseInt(scanner.nextLine());
                //System.out.println("Yuppers2");
            } else {
                throwLoadError();
            }

            if (scanner.nextLine().equals("tileWithThief:")) {
                tileWithThief = Integer.parseInt(scanner.nextLine());
                //System.out.println("Yuppers2.5");
            } else {
                throwLoadError();
            }

            if (scanner.nextLine().equals("victoryPointsToWin:")) {
                victoryPointsToWin = Integer.parseInt(scanner.nextLine());
                //System.out.println("Yuppers3");
            } else {
                throwLoadError();
            }

            if (scanner.nextLine().equals("currentPlayer:")) {
                currentPlayer = Integer.parseInt(scanner.nextLine());
                //System.out.println("Yuppers4");
            } else {
                throwLoadError();
            }

            if (scanner.nextLine().equals("giveStartingResources:")) {
                giveStartingResources = Boolean.parseBoolean(scanner.nextLine());
                //System.out.println("Yuppers4.5");
            } else {
                throwLoadError();
            }

            if (scanner.nextLine().equals("inSetup:")) {
                inSetup = Boolean.parseBoolean(scanner.nextLine());
                //System.out.println("Yuppers5");
            } else {
                throwLoadError();
            }

            if (scanner.nextLine().equals("setupRoundsLeft:")) {
                setupRoundsLeft = Integer.parseInt(scanner.nextLine());
                //System.out.println("YuppersSetupRounds");
            } else {
                throwLoadError();
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
                throwLoadError();
            }

            if (scanner.nextLine().equals("playerSetupRoadsLeft:")) {
                playerSetupRoadsLeft = Integer.parseInt(scanner.nextLine());
                //System.out.println("Yuppers6");
            } else {
                throwLoadError();
            }

            if (scanner.nextLine().equals("playerSetupSettlementLeft:")) {
                playerSetupSettlementLeft = Integer.parseInt(scanner.nextLine());
                //System.out.println("Yuppers7");
            } else {
                throwLoadError();
            }

            //get the total cards collected
            if (scanner.nextLine().equals("Total cards collected:")) {

                for (int i = 0; i < 5; i++) {
                    totalCardsCollected[i] = Integer.parseInt(scanner.nextLine());
                }

                //System.out.println("Yuppers8");
            } else {
                throwLoadError();
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
                throwLoadError();
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
                        throwLoadError();
                    }

                    if (scanner.nextLine().equals("size:")) {
                        tempScannerVal = Integer.parseInt(scanner.nextLine());
                        //System.out.println("Yuppers10.2");

                        if (scanner.nextLine().equals("cards:")) {

                            for (int j = 0; j < tempScannerVal; j++) {
                                cards[i].add(Integer.parseInt(scanner.nextLine()));
                            }

                            //skip a line
                            scanner.nextLine();

                            //System.out.println("Yuppers10.3");
                        } else {
                            throwLoadError();
                            System.out.println("Its me");
                        }

                    } else {
                        throwLoadError();
                        System.out.println("no me");
                    }

                }

            } else {
                throwLoadError();
            }

            if (scanner.nextLine().equals("Tiles:")) {
                //System.out.println("Yuppers11");

                int tileNum = 0;

                //loop through all the tiles
                for (int i = 0; i < 19; i++) {
                    if (scanner.nextLine().equals("Tile number:")) {
                        tileNum = Integer.parseInt(scanner.nextLine());
                    } else {
                        throwLoadError();
                    }

                    if (scanner.nextLine().equals("Type:")) {
                        tiles.get(tileNum).setType(Integer.parseInt(scanner.nextLine()));
                    } else {
                        throwLoadError();
                    }

                    if (scanner.nextLine().equals("Has Thief:")) {
                        tiles.get(tileNum).setThief(Boolean.parseBoolean(scanner.nextLine()));
                    } else {
                        throwLoadError();
                    }

                    if (scanner.nextLine().equals("Harvesting Dice Roll:")) {
                        tiles.get(tileNum).setHarvestRollNum(Integer.parseInt(scanner.nextLine()));
                    } else {
                        throwLoadError();
                    }

                    //skip a line
                    scanner.nextLine();
                }

            } else {
                throwLoadError();
            }

            if (scanner.nextLine().equals("NodeRoads:")) {
                //System.out.println("Yuppers12");

                int roadNodeNum = 0;

                //loop through all the tiles
                for (int i = 0; i < 72; i++) {
                    if (scanner.nextLine().equals("Road node number:")) {
                        roadNodeNum = Integer.parseInt(scanner.nextLine());
                    } else {
                        throwLoadError();
                    }

                    if (scanner.nextLine().equals("Player ID:")) {
                        roadNodes.get(roadNodeNum).setPlayer(Integer.parseInt(scanner.nextLine()));
                    } else {
                        throwLoadError();
                    }

                    //skip a line
                    scanner.nextLine();
                }

            } else {
                throwLoadError();
            }

            if (scanner.nextLine().equals("NodeSettlements:")) {
                //System.out.println("Yuppers13");

                int settlementNodeNum = 0;

                //loop through all the tiles
                for (int i = 0; i < 54; i++) {
                    if (scanner.nextLine().equals("Settlement node number:")) {
                        settlementNodeNum = Integer.parseInt(scanner.nextLine());
                    } else {
                        throwLoadError();
                    }

                    if (scanner.nextLine().equals("Player ID:")) {
                        settlementNodes.get(settlementNodeNum).setPlayer(Integer.parseInt(scanner.nextLine()));
                    } else {
                        throwLoadError();
                    }

                    if (scanner.nextLine().equals("Is Large:")) {
                        settlementNodes.get(settlementNodeNum).setLarge(Boolean.parseBoolean(scanner.nextLine()));
                    } else {
                        throwLoadError();
                    }

                    //skip a line
                    scanner.nextLine();
                }

            } else {
                throwLoadError();
            }

        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, "There was an error handling the save file.\nPlease try again.", "Loading Error", JOptionPane.ERROR_MESSAGE);
        }

        //update the instructions
        if (!inSetup) {
            // Set the instruction labels to tell the user they can build
            instructionLbl.setText("Use your cards to build roads or settlements");
            subInstructionLbl.setText("Or end your turn to continue the game");
        }

        //bring the playerTurnOrder Array to match the state it was when saving
        while (playerTurnOrder.get(0) != currentPlayer) {
            progressPlayerTurnOrder();
        }

        repaint();
        updateBuildButtons();
    }

    /**
     * Displays an error to the user about loading a bad file
     */
    private void throwLoadError() {
        JOptionPane.showMessageDialog(null, "Error loading the save file.\nThere may be file corruptions.", "Bad File", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Update the state of the build buttons, enabling or disabling them based
     * on how many cards the current player has, or if the game is in setup, how
     * many setup buildings they have left to place
     */
    private void updateBuildButtons() {

        //reset the colour
        instructionLbl.setForeground(new java.awt.Color(255, 255, 225));

        boolean canBuildRoad; // If the user has enough cards to build these
        boolean canBuildSettlement;
        boolean canBuildCity;
        boolean canTrade4to;
        ButtonModel oldSelection; // The button selected before this update began

        // If the game is in setup
        if (inSetup) {
            canBuildRoad = (playerSetupRoadsLeft > 0) && newestSetupSettlment != null;
            canBuildSettlement = (playerSetupSettlementLeft > 0);
            canBuildCity = false; // No settlement upgrades during setup
            canTrade4to = false;
        } //if the theif is stealing player's cards or the player is selecting another player to steal one card from.
        //or if a player is trading
        else if (thiefIsStealing || (thiefJustFinished && currentPlayer != playerRolled7) || canStealCardPlayers.size() > 0) {
            canBuildRoad = false;
            canBuildSettlement = false;
            canBuildCity = false;
            canTrade4to = false;
        } //else if the player is currently trading
        else if (tradingMode != 0) {
            //diable all the building
            canBuildRoad = false;
            canBuildSettlement = false;
            canBuildCity = false;
            
            //check the trading type
            switch (tradingMode) {
                case 1:
                    //if 4:1
                    canTrade4to = true;
                    break;
                case 2:
                    //if 3:1
                    canTrade4to = false;
                    break;
                case 3:
                    //if 2:1
                    canTrade4to = false;
                    break;
                default:
                    //if anything else
                    canTrade4to = false;
                    break;
            }

        } else { // If the game is NOT in setup
            // Check if the player has enough cards to use the build buttons
            canBuildRoad = hasCards(0); // Roads
            canBuildSettlement = hasCards(1); // Settlements
            canBuildCity = hasCards(2); // Cities
            canTrade4to = hasTradeCards(4);
        }

        // Save what button was selected before this update began
        oldSelection = buildBtnGroup.getSelection();

        // Select the first enabled button on the list
        if (canBuildRoad) {
            // Select the road button
            buildBtnGroup.setSelected(buildRoadRBtn.getModel(), true);
        } else if (canBuildSettlement) {
            // Select the settlement button
            buildBtnGroup.setSelected(buildSettlementSRBtn.getModel(), true);
        } else if (canBuildCity) {
            // Select the city button
            buildBtnGroup.setSelected(buildSettlementLRBtn.getModel(), true);
        } // If no buttons are selected and the game is not in setup
        else if (inSetup == false) {
            // If no buttons are enabled clear the selection
            buildBtnGroup.clearSelection();

            //set the instructions 
            if (thiefIsStealing) { //tell the player the theif is stealing
                // Set the instruction labels to tell the player that the thief will now be going around and stealing cards from eligble players
                instructionLbl.setForeground(new Color(255, 100, 100));
                instructionLbl.setText("A Thief! Shortly they will go around steal cards. No other actions allowed");
                subInstructionLbl.setText("End your turn so the thief can decide the next person to steal from");

                //update the lables for the player if the thief is stealing their cards specifically. Do not show this if a 7 was JUST rolled
                if (stealCardNum[currentPlayer] > 0 && !thiefJustStarted) {
                    instructionLbl.setForeground(new Color(255, 100, 100));
                    instructionLbl.setText("The thief is stealing half your cards");
                    subInstructionLbl.setText("Select the " + stealCardNum[currentPlayer] + " you want to give them");
                } else if (showTileHitbox) { //if a 7 was JUST rolled and the current player needs to move the thief show a specific messgae.
                    instructionLbl.setForeground(new Color(255, 100, 100));
                    instructionLbl.setText("A Thief! They will steal cards. Select a hex to move the thief.");
                    subInstructionLbl.setText("Afterwards, you can then end your turn so the thief can decide the next person to steal from");
                }

            } else if (thiefJustFinished && subPlayersHaveEnoughcards) {
                // Set the instruction labels to tell the player that they need to select a play to steal from
                instructionLbl.setText("The thief is done stealing. But you are not!");
                subInstructionLbl.setText("Select one of your fellow players to take one of their cards at random");

            } else {
                // Set the instruction labels to tell the player they dont have enough cards
                instructionLbl.setText("Sorry, you don't have enough cards to build anything");
                subInstructionLbl.setText("End your turn and collect more resources");
            }
        } // If no buttons are selected and the game IS in setup 
        else {
            // If no buttons are enabled clear the selection
            buildBtnGroup.clearSelection();
            // Set the instruction labels to tell the player they are out of setup buildings
            instructionLbl.setText("You have placed all of your setup buildings");
            subInstructionLbl.setText("End your turn to continue the game");
        }

        // Change the enabled/disabled state of the buttons based on whether or 
        // not they can build the corresponding buildings
        buildRoadRBtn.setEnabled(canBuildRoad);              // Roads
        buildSettlementSRBtn.setEnabled(canBuildSettlement); // Settlements
        buildSettlementLRBtn.setEnabled(canBuildCity);       // Cities
        trade4to1Btn.setEnabled(canTrade4to);                // Trade

        //update the colours of the radio buttons to reflect weather or not they are enabled. The stoped being done automatically when the default forground colour was changed.
        if (canBuildRoad) {
            buildRoadRBtn.setForeground(new java.awt.Color(255, 255, 225));
        } else {
            buildRoadRBtn.setForeground(new java.awt.Color(30, 30, 30));
        }

        if (canBuildSettlement) {
            buildSettlementSRBtn.setForeground(new java.awt.Color(255, 255, 225));
        } else {
            buildSettlementSRBtn.setForeground(new java.awt.Color(30, 30, 30));
        }

        if (canBuildCity) {
            buildSettlementLRBtn.setForeground(new java.awt.Color(255, 255, 225));
        } else {
            buildSettlementLRBtn.setForeground(new java.awt.Color(30, 30, 30));
        }

        // If the button selected before this update is still enabled, select it
        // instead of the selection made in the if/else block above
        if (oldSelection != null && oldSelection.isEnabled()) { // Also make sure the saved button is not null
            buildBtnGroup.setSelected(oldSelection, true);
        }
        // If any of the buttons are enabled, enable the build button
        // Otherwise disable it
        buildBtn.setEnabled(canBuildRoad || canBuildSettlement || canBuildCity);
    }
    
    /**
     * Determine if the current player has enough cards to begin a trade.
     * 
     * @param tradingType Three valid options. "4" for a four to one, "3", and "2" for their respective ratios
     * @return 
     */
    private boolean hasTradeCards(int tradingType) {
        boolean hasEnoughCards = false; //does the player have enough cards. Start false because no check has been made yet.
        
        //check if the input is valid
        if (!(tradingType >= 2 && tradingType <= 4)) {
            System.out.println("Error: hasTradeCards out of bounds with value: " + tradingType);
        } else { //check the current players cards
            //create an array to store how many cards of each type the player has
            numCardType = new int[6]; //index 0 is empty and index 1-5 correspond to the card type
            
            //sum up the cards of each type
            for (int i = 0; i < cards[currentPlayer].size(); i++) {
                numCardType[cards[currentPlayer].get(i)] ++;
            }
            
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
                findCards[2] = 1;
                findCards[3] = 1;
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
    private void quickSortCards(int player, int left, int right) {

        Integer temp; // For swapping values
        // Get the player's ArrayList of cards
        ArrayList<Integer> array = cards[player];

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
        quickSortCards(player, left, j);
        // Recursively call the algorithm on the right side of the pivot point
        quickSortCards(player, i, right);
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
            //steal the cards and allow the lables to update
            thiefIsStealing = true;
            thiefJustStarted = true;

            //show the tile hitboxes to the player who rolled the 7. This allows them to move the thief to the Tile of thier choosing.
            showTileHitbox = true;
            //disable the turn switch button so that this cannot be skiped
            turnSwitchBtn.setEnabled(false);

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
                            // If the settlement was a large settlement (City), the player earns twice the resources
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
            quickSortCards(i, 0, cards[i].size() - 1);
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

    /**
     * Draw the game.
     *
     * @param g
     */
    private void draw(Graphics g) {
        //start local vars
        int rightDrawMargin = superFrame.getWidth() - getImgWidth(MATERIAL_KEY) - (int) (10 / scaleFactor);

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
            if (canStealCardPlayers.size() > 0 && currentPlayer == playerRolled7 && !thiefIsStealing && !inbetweenTurns && subPlayersHaveEnoughcards) {

                //only draw the the hitbox around that specific player if they have more than 0 cards and if they are on the steal list
                if (cards[playerTurnOrder.get(i)].size() > 0 && canStealCardPlayers.contains(playerTurnOrder.get(i))) {
                    g2d.setColor(Color.green);
                    g2d.drawRect(superFrame.getWidth() - (getImgWidth(PLAYER_RED)) - (getImgWidth(SMALL_PLAYER_RED) * i),
                            superFrame.getHeight() - (int) (10 / scaleFactor) - getImgHeight(SMALL_PLAYER_RED),
                            getImgWidth(SMALL_PLAYER_RED),
                            getImgHeight(SMALL_PLAYER_RED));
                    g2d.setColor(Color.black);
                }
            }

        }

        //draw the sub player header
        g2d.setColor(new java.awt.Color(255, 255, 225));
        g2d.drawString("Next player:",
                superFrame.getWidth() - (getImgWidth(PLAYER_RED)) - (getImgWidth(SMALL_PLAYER_RED)),
                superFrame.getHeight() - (int) (20 / scaleFactor) - getImgHeight(SMALL_PLAYER_RED));
        
        Image PORT_RESOURCE = new ImageIcon(ImageRef.class.getResource("wildcard.png")).getImage(); 
        
        //draw the ports
        boolean drawSpecificHitbox = false; //local var to hold the value deciding if a specifc Port hitbox should be drawn. Depending on they type of trading mode.
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
            
            //draw the hitbox
            if (showPortHitbox) {
                //decide if that specif box should be drawn
                if (ports.get(i).getType() == 0) {
                    drawSpecificHitbox = false;
                } else if (tradingMode == 1 || tradingMode == 2) {
                    drawSpecificHitbox = true;
                } else if (tradingMode == 3) {
                    //check if the current player is on any of the 2:1 ports
                }
                
                //check if that one should be drawn
                if (drawSpecificHitbox) {
                    g2d.drawRect(ports.get(i).getTypePosX(), 
                        ports.get(i).getTypePosY(),  
                        getImgWidth(ports.get(i).getTypeImage()),
                        getImgHeight(ports.get(i).getTypeImage()));
                }
            }
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
                        tiles.get(tileID).getYPos() + newTileHeight / 2 + 5 + threeDTileOffset);
                g2d.setColor(Color.black);
            }

            //check where the thief is and draw it there
            if (tiles.get(tileID).hasThief()) {

                int imageWidth = getImgWidth(THIEF);
                int imageHeight = getImgHeight(THIEF);

                //draw the thief
                g2d.drawImage(THIEF,
                        tiles.get(tileID).getXPos() + newTileWidth / 2 - (int) (imageWidth / scaleFactor) / 2,
                        tiles.get(tileID).getYPos() + newTileHeight / 2 - (int) (imageHeight / scaleFactor) / 2 + threeDTileOffset,
                        (int) (imageWidth / scaleFactor),
                        (int) (imageHeight / scaleFactor),
                        null);
            }

            //draw the hitbox for the tile
            //make sure the hex fits the criteria. Thief cannot be moved to the tile where they already are.
            if (showTileHitbox && !tiles.get(tileID).hasThief()) {
                g2d.setColor(Color.green);
                g2d.drawRect(tiles.get(tileID).getXPos() + newTileWidth / 2 - ((int) (30 / scaleFactor) / 2),
                        (int) (tiles.get(tileID).getYPos() + newTileHeight / 2 - ((30 / scaleFactor) / 2) + threeDTileOffset),
                        (int) (30 / scaleFactor),
                        (int) (30 / scaleFactor));
                g2d.setColor(Color.black);
            }
        } //end tile drawing loop

        //set the font for the dice roll indecator
        g2d.setFont(new Font("Times New Roman", Font.PLAIN, (int) (20 / scaleFactor)));
        g2d.setColor(new java.awt.Color(255, 255, 225));
        //show what number the user rolled
        g2d.drawString("You rolled a: " + diceRollVal[2],
                rightDrawMargin,
                (int) (400 / scaleFactor));
        //draw the dice
        //but only if not in setup
        if (!inSetup) {
            //draw the non rolled dice if there is no roll
            if (diceRollVal[2].equals("")) {

                g2d.drawImage(DICE_IMAGES[0],
                        rightDrawMargin,
                        (int) (400 / scaleFactor),
                        getImgWidth(DICE_IMAGES[0]),
                        getImgHeight(DICE_IMAGES[0]),
                        null);
            } else { //else draw the dice that go with the roll
                g2d.drawImage(DICE_IMAGES[Integer.parseInt(diceRollVal[0])],
                        rightDrawMargin,
                        (int) (400 / scaleFactor),
                        getImgWidth(DICE_IMAGES[1]),
                        getImgHeight(DICE_IMAGES[1]),
                        null);

                g2d.drawImage(DICE_IMAGES[Integer.parseInt(diceRollVal[1])],
                        rightDrawMargin + getImgWidth(DICE_IMAGES[1]),
                        (int) (400 / scaleFactor),
                        getImgWidth(DICE_IMAGES[1]),
                        getImgHeight(DICE_IMAGES[1]),
                        null);

            }
        }

        //basic turn indecator
        String currentPlayerString;

        if (inbetweenTurns) {
            currentPlayerString = "none";
        } else if (this.currentPlayer == 1) {
            currentPlayerString = "1, Red";
        } else if (this.currentPlayer == 2) {
            currentPlayerString = "2, Blue";
        } else if (this.currentPlayer == 3) {
            currentPlayerString = "3, Orange";
        } else if (this.currentPlayer == 4) {
            currentPlayerString = "4, White";
        } else {
            currentPlayerString = "0, Error";
        }

        g2d.drawString("Current player: " + currentPlayerString,
                rightDrawMargin,
                (int) (550 / scaleFactor));

        //draw the VP and resource card start table
        //draw the player header
        g2d.drawString("Player:",
                rightDrawMargin,
                (int) (600 / scaleFactor));
        //draw the victory points header
        g2d.drawString("VP:",
                rightDrawMargin + (int) (100 / scaleFactor),
                (int) (600 / scaleFactor));
        //draw the Resource Cards header
        g2d.drawString("Resource Cards:",
                rightDrawMargin + (int) (180 / scaleFactor),
                (int) (600 / scaleFactor));
        //loop in all the data for the players
        for (int i = 1; i < playerCount + 1; i++) {
            //draw the player number
            g2d.drawString("" + i,
                    rightDrawMargin,
                    (int) ((600 + (30 * i)) / scaleFactor));
            //draw the players VPs
            g2d.drawString("" + victoryPoints[i],
                    rightDrawMargin + (int) (100 / scaleFactor),
                    (int) ((600 + (30 * i)) / scaleFactor));
            //draw the players number of resource cards
            g2d.drawString("" + cards[i].size(),
                    rightDrawMargin + (int) (180 / scaleFactor),
                    (int) ((600 + (30 * i)) / scaleFactor));
        }

        // Draw the 72 road nodes
        NodeRoad road;
        Image image;
        for (int i = 0; i < 72; i++) {
            road = roadNodes.get(i);
            switch (road.getOrientation()) {
                case 0: // Horizontal road ( -- )
                    // Store the road image for the player's color
                    if (road.getPlayer() == 0) {
                        image = BLANK_ROAD_H;
                    } else if (road.getPlayer() == 1) {
                        image = RED_ROAD_H;
                    } else if (road.getPlayer() == 2) {
                        image = BLUE_ROAD_H;
                    } else if (road.getPlayer() == 3) {
                        image = ORANGE_ROAD_H;
                    } else if (road.getPlayer() == 4) {
                        image = WHITE_ROAD_H;
                    } else {
                        image = RED_ROAD_L;
                    }
                    break;
                case 1: // Road pointing to the top left ( \ ) 
                    // Store the road image for the player's color
                    if (road.getPlayer() == 0) {
                        image = BLANK_ROAD_V;
                    } else if (road.getPlayer() == 1) {
                        image = RED_ROAD_L;
                    } else if (road.getPlayer() == 2) {
                        image = BLUE_ROAD_L;
                    } else if (road.getPlayer() == 3) {
                        image = ORANGE_ROAD_L;
                    } else if (road.getPlayer() == 4) {
                        image = WHITE_ROAD_L;
                    } else {
                        image = RED_ROAD_H;
                    }
                    break;
                case 2: // Road pointing to the top right ( / ) 
                    // Store the road image for the player's color
                    if (road.getPlayer() == 0) {
                        image = BLANK_ROAD_V;
                    } else if (road.getPlayer() == 1) {
                        image = RED_ROAD_R;
                    } else if (road.getPlayer() == 2) {
                        image = BLUE_ROAD_R;
                    } else if (road.getPlayer() == 3) {
                        image = ORANGE_ROAD_R;
                    } else if (road.getPlayer() == 4) {
                        image = WHITE_ROAD_R;
                    } else {
                        image = RED_ROAD_H;
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
                g2d.setColor(Color.green);
                g2d.drawRect(road.getXPos() - getImgWidth(image) / 2, road.getYPos() - getImgHeight(image) / 2, getImgWidth(image), getImgHeight(image));
                g2d.setColor(Color.black);
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
            else if (settlement.isLarge() == false) { // Small settlement
                // Store the small settlement image for the player's color
                if (settlement.getPlayer() == 1) {
                    image = RED_HOUSE_S;
                } // Player 1: Red
                else if (settlement.getPlayer() == 2) {
                    image = BLUE_HOUSE_S;
                } // Player 2: Blue
                else if (settlement.getPlayer() == 3) {
                    image = ORANGE_HOUSE_S;
                } // Player 3: Orange
                else if (settlement.getPlayer() == 4) {
                    image = WHITE_HOUSE_S;
                } // Player 4: White
                else {
                    image = RED_HOUSE_L;
                }
            } else { // Large settlement
                // Store the large settlement image for the player's color
                if (settlement.getPlayer() == 1) {
                    image = RED_HOUSE_L;
                } // Player 1: Red
                else if (settlement.getPlayer() == 2) {
                    image = BLUE_HOUSE_L;
                } // Player 2: Blue
                else if (settlement.getPlayer() == 3) {
                    image = ORANGE_HOUSE_L;
                } // Player 3: Orange
                else if (settlement.getPlayer() == 4) {
                    image = WHITE_HOUSE_L;
                } // Player 4: White
                else {
                    image = RED_HOUSE_S;
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
                if (buildingObject == 2) { //check for new settlment
                    //check if a new settlment can go there
                    drawHitBox = canBuildSettlement(settlement, true);

                } else if (buildingObject == 3) { //check for upgrading to city
                    //check if an upgrade can be made
                    if ((!settlement.isLarge()) && settlement.getPlayer() == currentPlayer) {
                        drawHitBox = true;
                    }

                } else { //error
                    instructionLbl.setText("Error drawing settlment hitboxes");
                    subInstructionLbl.setText("Please contact the developer");
                }

                //draw the hitbox
                if (drawHitBox) {
                    g2d.setColor(Color.green);
                    g2d.drawRect(settlement.getXPos() - getImgWidth(image) / 2, settlement.getYPos() - getImgHeight(image) / 2, getImgWidth(image), getImgHeight(image));
                    g2d.setColor(Color.black);
                }
            }
        }

        // If a turn is currently going on, render the current player's cards
        if (!inbetweenTurns) {
            // Get the number of cards the player has
            int listSize = cards[currentPlayer].size();
            
            drawSpecificHitbox = false; //reset the var
            
            // Calculate where the first card must go to center the list
            cardStartPosition = (int) ((superFrame.getWidth() / 2) - (listSize * getImgWidth(CARD_CLAY) + (listSize - 1) * (10 / scaleFactor)) / 2);

            //check if the cards would go off the screen
            if ((cardStartPosition + (getImgWidth(CARD_CLAY) + 10) * listSize) > (superFrame.getWidth() - (getImgWidth(CARD_CLAY)))) {
                drawCardStacks[currentPlayer] = true;

                //get the number of each card type the player has
                //setup an array to hold the results
                cardTypeCount = new int[5];

                //loop thorugh and populate the array
                for (int i = 0; i < listSize; i++) {
                    cardTypeCount[cards[currentPlayer].get(i) - 1]++;
                }

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
                        if (tradingMode == 0 && tradeResource == 0) { //if not trading draw it for theif discarding
                            drawSpecificHitbox = true;
                        } else if (cardTypeCount[i] >= 4) { //if it is for trading purpous do some more checks
                            drawSpecificHitbox = true;
                        } else {
                            drawSpecificHitbox = false;
                        }
                        
                        if (drawSpecificHitbox) {
                            g2d.setColor(Color.green);
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
                drawCardStacks[currentPlayer] = false;

                // Draw the player's cards
                // Reuse the image variable
                int type;
                for (int i = 0; i < listSize; i++) {
                    // Get the card type
                    type = cards[currentPlayer].get(i);
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
                        if (tradingMode == 0 && tradeResource == 0) { //if not trading draw it for theif discarding
                            drawSpecificHitbox = true;
                        } else if (numCardType[type] >= 4) { //if it is for trading purpous do some more checks
                            drawSpecificHitbox = true;
                        } else {
                            drawSpecificHitbox = false;
                        }
                        
                        if (drawSpecificHitbox) {
                            g2d.setColor(Color.green);
                            Stroke tempStroke = g2d.getStroke();
                            g2d.setStroke(new BasicStroke((float) (5 / scaleFactor)));
                            g2d.drawRect((cardStartPosition + (getImgWidth(CARD_CLAY) + 10) * i),
                                    (int) (superFrame.getHeight() - (getImgHeight(image) * 1.125)),
                                    getImgWidth(image),
                                    getImgHeight(image));
                            g2d.setStroke(tempStroke);
                            g2d.setColor(Color.black);
                        }
                    }

                }
            }
        }

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
     * Load the data into the mater Tile ArrayList
     */
    private void loadTiles() {
        Tile newTile;
        for (int i = 0; i < 19; i++) {
            //int r = (int) (Math.random() * 6);
            newTile = new Tile(tilePos[i][0], tilePos[i][1], tileTypes[i]); //set the position and a type based on the text file

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
    
    private void remapPortTypePos() {

        // Loop through all the ports
        for (int i = 0; i < ports.size(); i++) {

            //pick a way to add the corrdinates for scaling
            //choose a drawing type. One corrects for width and one corrects for height
            if (superFrame.getWidth() <= superFrame.getHeight()) {
                //set the x
                ports.get(i).setTypePosX((int) (ports.get(i).getTypePosX()/ 1920.0 * superFrame.getWidth()));
                //set the y
                ports.get(i).setTypePosY((getTileYPos(ports.get(i).getTypePosY()) + tileYOffset));
            } else {
                //set the x
                ports.get(i).setTypePosY((getTileXPos(ports.get(i).getTypePosY()) + tileXOffset));
                //set the y
                ports.get(i).setTypePosY(((int) (ports.get(i).getTypePosY() / 1080.0 * superFrame.getHeight())));
            }
        }
    }
    
    /**
     * Updated the image positions of the type images for the ports. This only brings them to where they need to be before major scaling point re-mapping.
     */
    private void updatePortTypePos() {
        //loop through the ports
        for (int i = 0; i < ports.size(); i++) {
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
        currentPlayer++; //switch to the next player
        newestSetupSettlment = null; //remove the most recent built house as the turn just changed
        progressPlayerTurnOrder(); //step the ArrayList to reflect the new current player

        // And go back to player 1 if the number exceeds the total number of players
        if (currentPlayer > playerCount) {
            currentPlayer = 1;
            // If the game was in setup, all of the turns have ended now and the normal game can begin
            //therefore count down a setup round to get closer to that normal game
            if (inSetup) {
                //count the completion of a setup round
                setupRoundsLeft--;

                //check if all setup rounds have been played
                if (setupRoundsLeft < 1) {
                    inSetup = false;
                    // If enabled. give everyone their starting resources
                    if (giveStartingResources) {
                        collectMaterials(0); // 0 makes it collect everything possible
                    }
                }
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

    private void randomizeTiles() {
        //randomly select a number of times to shuffle the board
        int numShuffle = (int) (Math.random() * 15) + 25;
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
     * Set the number of players playing the game
     * @param playerCount
     */
    public static void setPlayerCount(int playerCount) {
        GamePanel.playerCount = playerCount;
    }

    /**
     * Get the number of players playing the game
     * @return
     */
    public static int getPlayerCount() {
        return playerCount;
    }

    /**
     * Set weather or not the players receive startup resource cards when the game leaves setup mode
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
     * Get the width of the JFrame holding the GamePanel
     * @return 
     */
    public static int getFrameWidth() {
        return frameWidth;
    }
    
    /**
     * Set the width of the JFrame holding the GamePanel
     * @param frameWidth 
     */
    public static void setFrameWidth(int frameWidth) {
        GamePanel.frameWidth = frameWidth;
    }
    
    /**
     * Get the height of the JFrame holding the GamePanel
     * @return 
     */
    public static int getFrameHeight() {
        return frameHeight;
    }
    
    /**
     * Set the height of the JFrame holding the GamePanel
     * @param frameHeight
     */
    public static void setFrameHeight(int frameHeight) {
        GamePanel.frameHeight = frameHeight;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton backBtn;
    private javax.swing.JButton backNoSaveBtn;
    private javax.swing.JButton buildBtn;
    private javax.swing.ButtonGroup buildBtnGroup;
    private javax.swing.JLabel buildMenuLbl;
    private javax.swing.JRadioButton buildRoadRBtn;
    private javax.swing.JRadioButton buildSettlementLRBtn;
    private javax.swing.JRadioButton buildSettlementSRBtn;
    private javax.swing.JLabel instructionLbl;
    private javax.swing.JLabel instructionPromptLbl;
    private javax.swing.JLabel subInstructionLbl;
    private javax.swing.JLabel titleLbl;
    private javax.swing.JButton trade2to1Btn;
    private javax.swing.JButton trade3to1Btn;
    private javax.swing.JButton trade4to1Btn;
    private javax.swing.JButton turnSwitchBtn;
    // End of variables declaration//GEN-END:variables

}
