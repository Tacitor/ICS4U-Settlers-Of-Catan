/*
 * Lukas Krampitz and Evan Kreutzwiser
 * Nov 8, 2020
 * The JPanel that is the main part of the game. Handels drawing and logic.
 */
package krampitzkreutzwisersettlersofcatan;

import dataFiles.OldCode;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
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
    private final ArrayList<NodeRoad> roadNodes; // Every road node of the board
    private final int[] tileTypes = new int[]{1, 3, 4, 2, 2, 5, 1, 4, 3, 0, 4, 2, 4, 5, 1, 2, 3, 3, 5}; //the type of tile from left to right, and top to bottom
    private final int[] tileHarvestRollNums = new int[]{5, 3, 8, 6, 4, 12, 11, 10, 3, 0, 5, 9, 10, 6, 9, 11, 2, 8, 4}; //the harvest roll num of the tile from left to right, and top to bottom
    private final int[][] tilePos = new int[19 * 2][2]; //the x, y position to draw the tile images

    private boolean inSetup; // If the game is still being set up (players placing initiale buildings)
    private boolean inbetweenTurns; // true during the period where the game is waiting for the next player to start their turn
    private boolean showRoadHitbox;
    private boolean showSettlementHitbox;
    private int currentPlayer; // The player currently taking their turn
    private final int playerCount; // The number of players in the game
    private final boolean giveStartingResources; // If players get startup resources
    private final ArrayList<Integer> cards[]; // Holds each player's list of cards in an ArrayList
    private final int victoryPoints[];
    private final int totalCardsCollected[];
    private final int victoryPointsToWin;
    private int thiefMoveCounter;
    private int tileWithThief; // The index of the tile with the thief
    private int buildingObject; // Indicates if/what the user is building. 
    // 0 when not placing anything, 1 for roads, 2 for settlements, and 3 for upgrading

    //images for the cards
    private final static Image CARD_CLAY = new ImageIcon(ImageRef.class.getResource("cardClay.png")).getImage();
    private final static Image CARD_WHEAT = new ImageIcon(ImageRef.class.getResource("cardWheat.png")).getImage();
    private final static Image CARD_ORE = new ImageIcon(ImageRef.class.getResource("cardOre.png")).getImage();
    private final static Image CARD_SHEEP = new ImageIcon(ImageRef.class.getResource("cardSheep.png")).getImage();
    private final static Image CARD_WOOD = new ImageIcon(ImageRef.class.getResource("cardWood.png")).getImage();

    //images for the roads
    private final static Image RED_ROAD_H = new ImageIcon(ImageRef.class.getResource("redRoadH.png")).getImage(); //horizontal road
    private final static Image BLUE_ROAD_H = new ImageIcon(ImageRef.class.getResource("blueRoadH.png")).getImage();
    private final static Image RED_ROAD_R = new ImageIcon(ImageRef.class.getResource("redRoadR.png")).getImage(); //diagonal to the right (refernce point is the top of the road)
    private final static Image BLUE_ROAD_R = new ImageIcon(ImageRef.class.getResource("blueRoadR.png")).getImage();
    private final static Image RED_ROAD_L = new ImageIcon(ImageRef.class.getResource("redRoadL.png")).getImage(); //diagonal to the left
    private final static Image BLUE_ROAD_L = new ImageIcon(ImageRef.class.getResource("blueRoadL.png")).getImage();
    private final static Image BLANK_ROAD_H = new ImageIcon(ImageRef.class.getResource("blankRoadH.png")).getImage(); // Blank images for horizontal
    private final static Image BLANK_ROAD_V = new ImageIcon(ImageRef.class.getResource("blankRoadV.png")).getImage(); // and non horizontal roads

    //images for the settlements
    private final static Image BLUE_HOUSE_L = new ImageIcon(ImageRef.class.getResource("blueHouseL.png")).getImage();
    private final static Image RED_HOUSE_L = new ImageIcon(ImageRef.class.getResource("redHouseL.png")).getImage();
    private final static Image BLUE_HOUSE_S = new ImageIcon(ImageRef.class.getResource("blueHouseS.png")).getImage();
    private final static Image RED_HOUSE_S = new ImageIcon(ImageRef.class.getResource("redHouseS.png")).getImage();
    private final static Image BLANK_HOUSE = new ImageIcon(ImageRef.class.getResource("blankHouse.png")).getImage(); // Blank image for unowned settlement nodes 

    //image for the thief
    private final static Image THIEF = new ImageIcon(ImageRef.class.getResource("thief.png")).getImage();

    //the image for the water ring
    private final static Image WATER_RING = new ImageIcon(ImageRef.class.getResource("waterRing.png")).getImage();

    //the image for the building materials
    private final static Image MATERIAL_KEY = new ImageIcon(ImageRef.class.getResource("buildKey.png")).getImage();

    private static int harvestRollNumOffset; //the number of pixels the harvest roll is ofset from. This allows both single and double diget number to be centered

    private int roadWidth; //used in finding the hitbox
    private int roadHeight;
    private int playerSetupRoadsLeft; //number of roads to place
    private int playerSetupSettlementLeft; //number of settlements to place

    //private Graphics awtGraphics;
    /**
     * Creates new form NewGamePanel
     *
     * @param frame ref to the frame
     */
    public GamePanel(GameFrame frame) {

        // Initialize settings variables
        giveStartingResources = true;        
        playerCount = 2; // 2 Player game

        // Initalize variables
        superFrame = frame; //save refernce
        tiles = new ArrayList(); //init the master tile array list
        settlementNodes = new ArrayList(); // Init the settlement node array list
        roadNodes = new ArrayList(); // Init the road node array list
        inSetup = true;
        inbetweenTurns = false;
        currentPlayer = 1; // Player 1 starts
        cards = new ArrayList[playerCount + 1]; // Create the array of card lists
        // the +1 allows methods to use player IDs directly without subtracting 1
        victoryPoints = new int[playerCount + 1]; // Create the array of player's victory points
        // the +1 allows methods to use player IDs directly without subtracting 1
        totalCardsCollected = new int[5];
        buildingObject = 0;
        showRoadHitbox = false;
        showSettlementHitbox = false;
        playerSetupRoadsLeft = 2;
        playerSetupSettlementLeft = 2;
        victoryPointsToWin = 10;
        thiefMoveCounter = 0;

        //set a deflaut save path
        saveAddress = System.getProperty("user.home") + "\\Desktop\\SettlersOfCatan.save";
        //initialize the filechooser
        saveFileChooser = new JFileChooser();
        saveFileChooser.setDialogTitle("Select a file to save Settlers of Catan to:");

        // Fill the list of card ArrayLists with new ArrayLists and intialize
        // the victory point array (Both are the same size and can share a loop)
        for (int i = 0; i < cards.length; i++) {
            cards[i] = new ArrayList(); // Cards ArrayList
            victoryPoints[i] = 0; // Victory point counter
        }

        // Intialize the card counter array
        for (int i = 0; i < totalCardsCollected.length; i++) {
            totalCardsCollected[i] = 0; // Victory point counter
        }

        // Initialize the window and board
        initComponents(); //add the buttons and other Swing elements

        loadTilePos(); //read in the coodinates of where each of the 19 tiles goes
        loadTiles(); //load the ArrayList of tiles with position and type data
        loadNodes(); // Create and link all of the board's settlement and road nodes

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
        diceRollLbl = new javax.swing.JLabel();
        diceRollPromptLbl1 = new javax.swing.JLabel();

        setMaximumSize(new java.awt.Dimension(1920, 1080));
        setMinimumSize(new java.awt.Dimension(1920, 1080));
        setPreferredSize(new java.awt.Dimension(1920, 1080));

        backBtn.setText("< Save and Exit");
        backBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backBtnActionPerformed(evt);
            }
        });

        turnSwitchBtn.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        turnSwitchBtn.setText("End Current Player's Turn");
        turnSwitchBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                turnSwitchBtnActionPerformed(evt);
            }
        });

        instructionPromptLbl.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        instructionPromptLbl.setText("Instructions:");

        instructionLbl.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        instructionLbl.setText("Place two roads and two small settlements each to start.");

        buildMenuLbl.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        buildMenuLbl.setText("Build Menu:");

        buildBtnGroup.add(buildSettlementSRBtn);
        buildSettlementSRBtn.setText("Small Settlement");

        buildBtnGroup.add(buildSettlementLRBtn);
        buildSettlementLRBtn.setText("Large Settlement");

        buildBtnGroup.add(buildRoadRBtn);
        buildRoadRBtn.setSelected(true);
        buildRoadRBtn.setText("Road");

        buildBtn.setFont(new java.awt.Font("Dialog", 1, 13)); // NOI18N
        buildBtn.setText("Build");
        buildBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buildBtnActionPerformed(evt);
            }
        });

        subInstructionLbl.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        subInstructionLbl.setText("Select a type, click build, and then click where it shoud go.");

        diceRollLbl.setFont(new java.awt.Font("Times New Roman", 0, 24)); // NOI18N
        diceRollLbl.setText(" ");

        diceRollPromptLbl1.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        diceRollPromptLbl1.setText("You rolled a: ");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(diceRollPromptLbl1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(diceRollLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(142, 142, 142))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(buildSettlementSRBtn)
                            .addComponent(backBtn)
                            .addComponent(turnSwitchBtn)
                            .addComponent(buildMenuLbl)
                            .addComponent(buildRoadRBtn)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(instructionPromptLbl)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(instructionLbl)
                                    .addComponent(subInstructionLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 645, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(buildBtn, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(buildSettlementLRBtn, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addContainerGap(1163, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(70, 70, 70)
                .addComponent(turnSwitchBtn)
                .addGap(29, 29, 29)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(instructionPromptLbl)
                    .addComponent(instructionLbl))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(subInstructionLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(buildMenuLbl)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(buildRoadRBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buildSettlementSRBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buildSettlementLRBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buildBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(diceRollPromptLbl1)
                    .addComponent(diceRollLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 676, Short.MAX_VALUE)
                .addComponent(backBtn)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void backBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backBtnActionPerformed

        //get the location to save to
        userSaveSelection = saveFileChooser.showSaveDialog(this);

        //set the selected address
        if (userSaveSelection == JFileChooser.APPROVE_OPTION) {
            saveAddress = saveFileChooser.getSelectedFile().getPath();
            System.out.println(saveAddress);
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

        // If the game is waiting to start the next player's turn
        if (inbetweenTurns) {

            // Change the button back to the End Turn button
            turnSwitchBtn.setText("End Current Player's Turn");

            // Begin the next turn
            inbetweenTurns = false; // No longer waiting to start a turn

            //update the instructions
            if (inSetup) {
                instructionLbl.setText("Place two roads and two small settlements each to start.");
                subInstructionLbl.setText("Select a type, click build, and then click where it shoud go.");
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
        } else if (playerSetupRoadsLeft == 0 && playerSetupSettlementLeft == 0) { // If the end turn button was clicked
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
            currentPlayer++;

            // And go back to player 1 if the number exceeds the total number of players
            if (currentPlayer > playerCount) {
                currentPlayer = 1;
                // If the game was in setup, all of the turns have ended now and the normal game can begin
                if (inSetup) {
                    inSetup = false;
                    // If enabled. give everyone their starting resources
                    if (giveStartingResources) {
                    collectMaterials(0); // 0 makes it collect everything possible
                    }
                }
            }

            // If the game is still in setup, give the next player roads to place
            if (inSetup) {
                playerSetupRoadsLeft = 2;
                playerSetupSettlementLeft = 2;
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
                        roadWidth = 60;
                        roadHeight = 8;
                    } else {
                        roadWidth = 38;
                        roadHeight = 56;
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
                                roadNodes.get(i).setPlayer(currentPlayer);
                                playerSetupRoadsLeft--;
                                // Update thwe build buttons to relfect the remaining setup buildings
                                updateBuildButtons();
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
                            instructionLbl.setText("Sorry but you can't take someone elses road.");
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
                    if (event.getX() > settlementNodes.get(i).getXPos() - RED_HOUSE_S.getWidth(null) / 2
                            && event.getX() < settlementNodes.get(i).getXPos() - RED_HOUSE_S.getWidth(null) / 2 + RED_HOUSE_S.getWidth(null)
                            && event.getY() > settlementNodes.get(i).getYPos() - RED_HOUSE_S.getHeight(null) / 2
                            && event.getY() < settlementNodes.get(i).getYPos() - RED_HOUSE_S.getHeight(null) / 2 + RED_HOUSE_S.getHeight(null)) {
                        //debug settlent build detection
                        //System.out.println("hitbox match");

                        //check that the settlement is unowned
                        if (settlementNodes.get(i).getPlayer() == 0) {
                            //check what mode the game is in 
                            if (inSetup && playerSetupSettlementLeft > 0) { // In Setup
                                // Place one of the player's setup settlements
                                settlementNodes.get(i).setPlayer(currentPlayer);
                                playerSetupSettlementLeft--;
                                // Increment the player's victory point counter
                                victoryPoints[currentPlayer]++;
                                // Update thwe build buttons to relfect the remaining setup buildings
                                updateBuildButtons();
                            } // If the main game is in progress and the player can build the settlement there
                            else if (canBuildSettlement(settlementNodes.get(i))) {
                                // The card check has already been made, and the user has the right cards
                                
                                // Remove the cards from the player's deck
                                // Remove 1 clay, 1 wood, 1 wheat, and 1 sheep
                                cards[currentPlayer].remove(new Integer(1));
                                cards[currentPlayer].remove(new Integer(2));
                                cards[currentPlayer].remove(new Integer(3));
                                cards[currentPlayer].remove(new Integer(4));

                                // Set the settlement's player to the current player
                                settlementNodes.get(i).setPlayer(currentPlayer);

                                // Increment the player's victory point counter
                                victoryPoints[currentPlayer]++;

                                // Update the building buttons to reflect the player's new list of cards
                                updateBuildButtons();
                            } // If the player could not build there
                            else {
                                // Print out why the player could not build there
                                instructionLbl.setText("Sorry but you can't build a settlement there.");
                                subInstructionLbl.setText("Try building adjacent to one of your exsisting buildings");
                            }
                        } else {
                            instructionLbl.setText("Sorry but you can't take someone elses settlements.");
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
                    if (event.getX() > settlementNodes.get(i).getXPos() - RED_HOUSE_S.getWidth(null) / 2
                            && event.getX() < settlementNodes.get(i).getXPos() - RED_HOUSE_S.getWidth(null) / 2 + RED_HOUSE_S.getWidth(null)
                            && event.getY() > settlementNodes.get(i).getYPos() - RED_HOUSE_S.getHeight(null) / 2
                            && event.getY() < settlementNodes.get(i).getYPos() - RED_HOUSE_S.getHeight(null) / 2 + RED_HOUSE_S.getHeight(null)) {
                     
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
                            instructionLbl.setText("Sorry but you can't upgrade someone elses settlements.");
                            subInstructionLbl.setText("Try upgrading your own settlement");
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
            System.out.println("An error occurred while trying to save the game.");
            e.printStackTrace();
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
    public boolean writeToFile(String writeAdress) throws FileNotFoundException {
        try {
            PrintWriter saveFile = new PrintWriter(writeAdress); //begin writting to the file
            saveFile.println("SettlersOfCatanSave"); //write a header to easily identify Settlers of Catan save files for loading
            saveFile.println("thiefMoveCounter:");
            saveFile.println(thiefMoveCounter);
            saveFile.println("victoryPointsToWin:");
            saveFile.println(victoryPointsToWin);
            
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
                for (int j = 0; j < cards[i].size(); j++) {
                    saveFile.println(cards[i].get(j));
                }
                saveFile.println();
            }

            //Add the tile data
            saveFile.println("Tiles:");
            for (int i = 0; i < tiles.size() - 1; i++) { //loop thorugh all the tiles and add it to the save file. Ignore the last null tile
                saveFile.println("Tile number");
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
            for (int i = 0; i < settlementNodes.size() - 1; i++) {
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
            return false;
        }
    }

    /**
     * Update the state of the build buttons, enabling or disabling them based
     * on how many cards the current player has, or if the game is in setup, how
     * many setup buildings they have left to place
     */
    private void updateBuildButtons() {
    
        boolean canBuildRoad; // If the user has enough cards to build these
        boolean canBuildSettlement;
        boolean canBuildCity;
        ButtonModel oldSelection; // The button selected before this update began

        // If the game is in setup
        if (inSetup) {
            canBuildRoad = (playerSetupRoadsLeft > 0);
            canBuildSettlement = (playerSetupSettlementLeft > 0);
            canBuildCity = false; // No settlement upgrades during setup
        } // If the game is NOT in setup
        else {
            // Check if the player has enough cards to use the build buttons
            canBuildRoad =       hasCards(0); // Roads
            canBuildSettlement = hasCards(1); // Settlements
            canBuildCity =       hasCards(2); // Cities
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
            // Set the instruction labels to tell the player they dont have enough cards
            instructionLbl.setText("Sorry, you don't have enough cards to build anything");
            subInstructionLbl.setText("End your turn and collect more resources");
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
     * Determines if the current player has enough 
     * cards to build a type of building
     * This replaces the old findCards method, and is more efficient
     * @param buildingType The building type represented as an integer 
     * (0 = road, 1 = settlement, 2 = city / upgrade settlement, 3 = development cards)
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
     * @param player The player who's cards will be sorted
     * @param left The left bounds of the segment to sort (used to recursion, set to 0)
     * @param right The right bounds of the segment to sort (used to recursion, set to length of array - 1)
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
        int pivotValue = array.get((left+right) / 2);
        
        // Repeat until all of the numbers are on the correct side
        while (i<j) {
            // Skip over numbers that are already on the correct side
            // Move the pointers until they are on a value that's on the wrong side
            while (array.get(i) < pivotValue) {i++;}
            while (array.get(j) > pivotValue) {j--;}
           
            // If the pointers are still on the correct side
            if (i <= j) {
                // Swap the values on each side of the pivot point
                temp = array.get(i);
                array.set(i, array.get(j));
                array.set(j ,temp);
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
     * Check if the player can build a settlement on the given node
     *
     * @param settlement The settlement node to check if the user can build on
     * @return If the player can build on it
     */
    private boolean canBuildSettlement(NodeSettlement settlement) {

        // Record how many of the 3 nodes are owned by other players
        // And if one of the connected roads belong to the current player
        int otherPlayerNumber = 0;
        boolean currentPlayerHasRoad = false; // If the current player has a road connected to this node
        for (int i = 1; i <= 3; i++) {
            // If the road exists
            if (settlement.getRoad(i) != null) {
                // If the road belongs to the player
                if (settlement.getRoad(i).getPlayer() == currentPlayer) {
                    // Then store that the user has a road connecting to this place
                    currentPlayerHasRoad = true;
                } // If the road belongs to a different player (and is owned)
                else if (settlement.getRoad(i).getPlayer() > 0) {
                    // Check if this is the second time the same other player's road was found
                    if (settlement.getRoad(i).getPlayer() == otherPlayerNumber) {
                        // Then the other player has a road going through and past this point,
                        // Blocking any other player from building a settlement there
                        return false; // Node is blocked, cannot build here
                    }
                    // Record the number
                    otherPlayerNumber = settlement.getRoad(i).getPlayer();
                }
            }
        }

        // If the current player owns a connected road, and the code didnt return above,
        if (currentPlayerHasRoad) {
            // Then the player can build here
            return true;
        }

        // If the user cannot build here
        return false;
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
        // Roll the second dice and add to the total
        roll += (int) (Math.random() * 6) + 1;

        // Display the number rolled to the user
        diceRollLbl.setText(Integer.toString(roll));

        // Act on the dice roll
        if (roll == 7) { // Move the thief on a 7
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
                        if ((settlement.getTile(j).getHarvestRollNum() == harvestNumber
                                && settlement.getTile(j).hasThief() == false)
                                // Or if every tile is to be harvested from (passed harvest number is 0)
                                || harvestNumber == 0) {
                            // Give the player the tile's resource
                            cards[player].add(settlement.getTile(j).getType());
                            // Add the collected card to the card counter
                            totalCardsCollected[settlement.getTile(j).getType() - 1]++;
                        }
                    }
                }
            }
        }

        // Sort every player's cards
        for (int i = 1; i < cards.length; i++) {
            // Sort the player's cards using a quick sort algorithm
            quickSortCards(i, 0, cards[i].size()-1);
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
        //the Graphics2D class is the class that handles all the drawing
        //must be casted from older Graphics class in order to have access to some newer methods
        Graphics2D g2d = (Graphics2D) g;
        //draw a string on the panel        
        g2d.setFont(new Font("Times New Roman", Font.BOLD, 40));
        g2d.drawString("Settlers of Catan", 10, 50); //(text, x, y)        }

        //debug the game pannel
        //System.out.println("GamePannel draw function called"); //and indecation of how many times the draw function runs
        //draw the building material costs key
        g2d.drawImage(MATERIAL_KEY, 1920 - 330, 10, null);

        //draw the ring of water
        g2d.drawImage(WATER_RING, 1920 / 2 - WATER_RING.getWidth(null) / 2, 1080 / 2 - WATER_RING.getHeight(null) / 2, null);

        //draw the board using the new way. the coordinates inside the tile objects come from the old way of drawing the baord
        for (int i = 0; i < 19; i++) {
            g2d.drawImage(tiles.get(i).getImage(), tiles.get(i).getXPos(), tiles.get(i).getYPos(), null);

            //draw the resource harvest number only if it is not a desert
            if (tiles.get(i).getType() != 0) {
                g2d.setColor(Color.DARK_GRAY);
                g2d.fillOval(tiles.get(i).getXPos() + 150 / 2 - 15, tiles.get(i).getYPos() + 130 / 2 - 15, 30, 30);

                //check if the colour of the number
                if (tiles.get(i).getHarvestRollNum() == 8 || tiles.get(i).getHarvestRollNum() == 6) {
                    g2d.setColor(Color.red);
                } else {
                    g2d.setColor(Color.white);
                }

                //set the offset based on the number of digits
                if (tiles.get(i).getHarvestRollNum() > 9) {
                    harvestRollNumOffset = 10;
                } else {
                    harvestRollNumOffset = 4;
                }

                //draw the harvest roll num
                g2d.setFont(new Font("Times New Roman", Font.BOLD, 20));
                g2d.drawString(Integer.toString(tiles.get(i).getHarvestRollNum()), tiles.get(i).getXPos() + 150 / 2 - harvestRollNumOffset, tiles.get(i).getYPos() + 130 / 2 + 5);
                g2d.setColor(Color.black);
            }

            //check where the thief is and draw it there
            if (tiles.get(i).hasThief()) {

                //draw the thief
                g2d.drawImage(THIEF, tiles.get(i).getXPos() + 150 / 2 - 12, tiles.get(i).getYPos() + 130 / 2 - 56 / 2, null);
            }
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
                    } else {
                        image = BLUE_ROAD_H;
                    }
                    break;
                case 1: // Road pointing to the top left ( \ ) 
                    // Store the road image for the player's color
                    if (road.getPlayer() == 0) {
                        image = BLANK_ROAD_V;
                    } else if (road.getPlayer() == 1) {
                        image = RED_ROAD_L;
                    } else {
                        image = BLUE_ROAD_L;
                    }
                    break;
                case 2: // Road pointing to the top right ( / ) 
                    // Store the road image for the player's color
                    if (road.getPlayer() == 0) {
                        image = BLANK_ROAD_V;
                    } else if (road.getPlayer() == 1 || road.getPlayer() == 0) {
                        image = RED_ROAD_R;
                    } else {
                        image = BLUE_ROAD_R;
                    }
                    break;
                default: // Make the compiler happy and error handling
                    image = RED_ROAD_H; // Just add an image so theres something to render
                    break;
            }

            // Draw the road image saved above, at the node's position
            g2d.drawImage(image, road.getXPos() - image.getWidth(null) / 2,
                    road.getYPos() - image.getHeight(null) / 2, null);

            //draw the hit box for the road.
            if (showRoadHitbox) {
                g2d.setColor(Color.green);
                g2d.drawRect(road.getXPos() - image.getWidth(null) / 2, road.getYPos() - image.getHeight(null) / 2, image.getWidth(null), image.getHeight(null));
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
                else {
                    image = BLUE_HOUSE_S;
                } // Player 2: Blue
            } else { // Large settlement
                // Store the large settlement image for the player's color
                if (settlement.getPlayer() == 1) {
                    image = RED_HOUSE_L;
                } // Player 1: Red
                else {
                    image = BLUE_HOUSE_L;
                } // Player 2: Blue
            }

            // Draw the settlement image saved above, at the node's position
            g2d.drawImage(image, settlement.getXPos() - image.getWidth(null) / 2,
                    settlement.getYPos() - image.getHeight(null) / 2, null);

            //draw the hit box for the settlements.
            if (showSettlementHitbox) {
                g2d.setColor(Color.green);
                g2d.drawRect(settlement.getXPos() - image.getWidth(null) / 2, settlement.getYPos() - image.getHeight(null) / 2, image.getWidth(null), image.getHeight(null));
                g2d.setColor(Color.black);
            }
        }

        // If a turn is currently going on, render the current player's cards
        if (!inbetweenTurns) {
            // Get the number of cards the player has
            int listSize = cards[currentPlayer].size();
            // Calculate where the first card must go to center the list
            int startPosition = 960 - (listSize * CARD_CLAY.getWidth(null) + (listSize - 1) * 10) / 2;
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
                    default: // 5: Ore card
                        image = CARD_ORE;
                        break;
                }
                // Draw the card
                g2d.drawImage(image, startPosition + (CARD_CLAY.getWidth(null) + 10) * i, 930, null);
            }
        }
        // Add alignment lines
        // g2d.drawLine(1920 / 2, 0, 1920 / 2, 1080);
        // g2d.drawLine(0, 1080 / 2, 1920, 1080 / 2);
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
    public final void loadNodes() {

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
                settlementNodes.add(new NodeSettlement(settlementX, settlementY));

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
     * read in the tile positions
     */
    public final void loadTilePos() {

        // Declare variables
        Scanner fileReader;
        InputStream file = OldCode.class.getResourceAsStream("tilePos.txt");

        // Try to read the file
        try {
            // Create the scanner to read the file
            fileReader = new Scanner(file);

            // Read the entire file into an array
            for (int i = 0; i < 19; i++) {
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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton backBtn;
    private javax.swing.JButton buildBtn;
    private javax.swing.ButtonGroup buildBtnGroup;
    private javax.swing.JLabel buildMenuLbl;
    private javax.swing.JRadioButton buildRoadRBtn;
    private javax.swing.JRadioButton buildSettlementLRBtn;
    private javax.swing.JRadioButton buildSettlementSRBtn;
    private javax.swing.JLabel diceRollLbl;
    private javax.swing.JLabel diceRollPromptLbl1;
    private javax.swing.JLabel instructionLbl;
    private javax.swing.JLabel instructionPromptLbl;
    private javax.swing.JLabel subInstructionLbl;
    private javax.swing.JButton turnSwitchBtn;
    // End of variables declaration//GEN-END:variables
}
