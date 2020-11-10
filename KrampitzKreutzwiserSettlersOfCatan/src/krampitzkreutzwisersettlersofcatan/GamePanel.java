/*
 * Lukas Krampitz and Evan Kreutzwiser
 * Nov 8, 2020
 * The JPanel that is the main part of the game
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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.ImageIcon;
import textures.ImageRef;

/**
 * @author Evan
 * @author Tacitor
 */
public class GamePanel extends javax.swing.JPanel {

    private final GameFrame superFrame; //ref to the JFrame this kept in

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
    private final ArrayList<Integer> cards[]; // Holds each player's list of cards in an ArrayList
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
    private final static Image BLANK_ROAD_H = new ImageIcon(ImageRef.class.getResource("blankRoadH.png")).getImage();
    private final static Image RED_ROAD_R = new ImageIcon(ImageRef.class.getResource("redRoadR.png")).getImage(); //diagonal to the right (refernce point is the top of the road)
    private final static Image BLUE_ROAD_R = new ImageIcon(ImageRef.class.getResource("blueRoadR.png")).getImage();
    private final static Image BLANK_ROAD_R = new ImageIcon(ImageRef.class.getResource("blankRoadR.png")).getImage();
    private final static Image RED_ROAD_L = new ImageIcon(ImageRef.class.getResource("redRoadL.png")).getImage(); //diagonal to the left
    private final static Image BLUE_ROAD_L = new ImageIcon(ImageRef.class.getResource("blueRoadL.png")).getImage();
    private final static Image BLANK_ROAD_L = new ImageIcon(ImageRef.class.getResource("blankRoadL.png")).getImage();

    //images for the settlements
    private final static Image BLUE_HOUSE_L = new ImageIcon(ImageRef.class.getResource("blueHouseL.png")).getImage();
    private final static Image RED_HOUSE_L = new ImageIcon(ImageRef.class.getResource("redHouseL.png")).getImage();
    private final static Image BLANK_HOUSE_L = new ImageIcon(ImageRef.class.getResource("blankHouseL.png")).getImage();
    private final static Image BLUE_HOUSE_S = new ImageIcon(ImageRef.class.getResource("blueHouseS.png")).getImage();
    private final static Image RED_HOUSE_S = new ImageIcon(ImageRef.class.getResource("redHouseS.png")).getImage();
    private final static Image BLANK_HOUSE_S = new ImageIcon(ImageRef.class.getResource("blankHouseS.png")).getImage();

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
        // Initalize variables
        superFrame = frame; //save refernce
        tiles = new ArrayList(); //init the master tile array list
        settlementNodes = new ArrayList(); // Init the settlement node array list
        roadNodes = new ArrayList(); // Init the road node array list
        inSetup = true;
        inbetweenTurns = false;
        playerCount = 2; // 2 Player game
        currentPlayer = 1; // Player 1 starts
        cards = new ArrayList[playerCount]; // Create the array of card lists
        buildingObject = 0;
        showRoadHitbox = false;
        showSettlementHitbox = false;
        playerSetupRoadsLeft = 2;
        playerSetupSettlementLeft = 2;

        // Fill the list of card ArrayLists with new ArrayLists ()
        for (ArrayList list : cards) {
            list = new ArrayList();
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
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        backBtn = new javax.swing.JButton();
        turnSwitchBtn = new javax.swing.JButton();
        instructionPromptLbl = new javax.swing.JLabel();
        instructionLbl = new javax.swing.JLabel();
        buildMenuLbl = new javax.swing.JLabel();
        buildSettlementSRBtn = new javax.swing.JRadioButton();
        buildSettlementLRBtn = new javax.swing.JRadioButton();
        buildRoadRbtn = new javax.swing.JRadioButton();
        buildBtn = new javax.swing.JButton();
        subInstructionLbl = new javax.swing.JLabel();

        setMaximumSize(new java.awt.Dimension(1920, 1080));
        setMinimumSize(new java.awt.Dimension(1920, 1080));
        setPreferredSize(new java.awt.Dimension(1920, 1080));

        backBtn.setText("<  Exit");
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

        buttonGroup1.add(buildSettlementSRBtn);
        buildSettlementSRBtn.setText("Small Settlement");

        buttonGroup1.add(buildSettlementLRBtn);
        buildSettlementLRBtn.setText("Large Settlement");

        buttonGroup1.add(buildRoadRbtn);
        buildRoadRbtn.setSelected(true);
        buildRoadRbtn.setText("Road");

        buildBtn.setText("Build");
        buildBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buildBtnActionPerformed(evt);
            }
        });

        subInstructionLbl.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        subInstructionLbl.setText("Select a type, click build, and then click where it shoud go.");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(buildSettlementSRBtn)
                    .addComponent(backBtn)
                    .addComponent(turnSwitchBtn)
                    .addComponent(buildMenuLbl)
                    .addComponent(buildRoadRbtn)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(buildBtn, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(buildSettlementLRBtn, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(instructionPromptLbl)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(subInstructionLbl)
                            .addComponent(instructionLbl))))
                .addContainerGap(1408, Short.MAX_VALUE))
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
                .addComponent(subInstructionLbl)
                .addGap(18, 18, 18)
                .addComponent(buildMenuLbl)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(buildRoadRbtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buildSettlementSRBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buildSettlementLRBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buildBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 735, Short.MAX_VALUE)
                .addComponent(backBtn)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void backBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backBtnActionPerformed
        // Hide this window and show the main menu
        superFrame.getMainMenu().setVisible(true); //show the main menu
        superFrame.setVisible(false); //hide the parent frame 
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
            }
            //Update the vars
            if (buildRoadRbtn.isSelected()) {
                buildingObject = 1;

                // If the player has more roads to place
                if (playerSetupRoadsLeft > 0) {
                    // Show the building hitboxes and redraw the baord to render them
                    showRoadHitbox = true;
                    repaint();
                } else {
                    instructionLbl.setText("You're all done placing your setup roads. There are none left.");
                    subInstructionLbl.setText("");
                }

            } else if (buildSettlementSRBtn.isSelected()) {
                buildingObject = 2;

                // If the player has more settlements to place
                if (playerSetupSettlementLeft > 0) {
                    // Show the building hitboxes and redraw the baord to render them
                    showSettlementHitbox = true;
                    repaint();
                } else {
                    instructionLbl.setText("You're all done placing your setup settlements. There are none left.");
                    subInstructionLbl.setText("");
                }

            } else if (buildSettlementLRBtn.isSelected()) {
                buildingObject = 3;
                //make sure you're not in setup mode
                if (inSetup) {
                    instructionLbl.setText("Sorry you don't have any large settlements to place.");
                    subInstructionLbl.setText("You do still have " + playerSetupSettlementLeft + " small settlement(s) left");
                }
            } else {
                buildingObject = -1;
                System.out.println("An error has occoured while building");
            }
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
            }

            // Redraw the board to the next player can see their cards
            repaint();
        } else if (playerSetupRoadsLeft == 0 && playerSetupSettlementLeft == 0) { // If the end turn button was clicked
            // And the user is done placing setup roads
            // Now the game is waiting to start the next turn
            inbetweenTurns = true;

            // Select the next player
            currentPlayer++;

            // And go back to player 1 if the number exceeds the total number of players
            if (currentPlayer > playerCount) {
                currentPlayer = 1;
                // If the game was in setup, all of the turns have ended now and the normal game can begin
                inSetup = false;
            }

            // If the game is still in setup, give the next player roads to place
            if (inSetup) {
                playerSetupRoadsLeft = 2;
                playerSetupSettlementLeft = 2;
            }

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
                                buildingObject = 0;
                                showRoadHitbox = false;
                                playerSetupRoadsLeft--;
                                repaint();

                            }
                        } else {
                            instructionLbl.setText("Sorry but you can't take someone elses road.");
                            subInstructionLbl.setText("Try building where there isn't already another road");
                        }

                    }
                }
            } else if (buildingObject == 2) { //small house

                //check the distance to the nearest settlement node using hitboxes and check if it is close enough 
                for (int i = 0; i < settlementNodes.size() - 1; i++) {

                    //if the player clicks in a valid hitbox for a settlement
                    if (event.getX() > settlementNodes.get(i).getXPos() - RED_HOUSE_S.getWidth(null) / 2
                            && event.getX() < settlementNodes.get(i).getXPos() - RED_HOUSE_S.getWidth(null) / 2 + RED_HOUSE_S.getWidth(null)
                            && event.getY() > settlementNodes.get(i).getYPos() - RED_HOUSE_S.getHeight(null) / 2
                            && event.getY() < settlementNodes.get(i).getYPos() - RED_HOUSE_S.getHeight(null) / 2 + RED_HOUSE_S.getHeight(null)) {
                        //debug settlent build detection
                        //System.out.println("hitbox match");
                        //g2d.drawRect(settlement.getXPos() - image.getWidth(null) / 2, settlement.getYPos() - image.getHeight(null) / 2, image.getWidth(null), image.getHeight(null));

                        //check that the road is unowned
                        if (settlementNodes.get(i).getPlayer() == 0) {
                            //check what mode the game is in 
                            if (inSetup && playerSetupSettlementLeft > 0) {
                                settlementNodes.get(i).setPlayer(currentPlayer);
                                buildingObject = 0;
                                showSettlementHitbox = false;
                                playerSetupSettlementLeft--;
                                repaint();

                            }
                        } else {
                            instructionLbl.setText("Sorry but you can't take someone elses settlements.");
                            subInstructionLbl.setText("Try building where there isn't already another settlements");
                        }

                    }
                }

            } else if (buildingObject == 3) { //large house

            } else {
                System.out.println("Yeah we've got an error here chief. Building in the mouse click event printed me");
            }
        }
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

        System.out.println("Rolled a " + roll);

        // Act on the dice roll
        if (roll == 7) { // Move the thief on a 7
            // TODO: Allow player to move thief
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
     * harvest from
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
                    // If the tile has the same harvest number
                    // And doesnt have the thief on it
                    if (settlement.getTile(j).getHarvestRollNum() == harvestNumber
                            && settlement.getTile(j).hasThief() == false) {
                        // Give the player the tile's resource
                        cards[player].add(settlement.getTile(j).getType());
                        System.out.println("Gave player " + player + " a type "
                                + settlement.getTile(j).getType() + " resource");
                    }
                }
            }
        }
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

        //draw testing art
        //cards
        g2d.drawImage(CARD_CLAY, 100, 1080 - 125, null); //space them 100 pixels apart and align the hight to 2 from the bottom
        g2d.drawImage(CARD_ORE, 200, 1080 - 125, null);
        g2d.drawImage(CARD_WHEAT, 300, 1080 - 125, null);
        g2d.drawImage(CARD_WOOD, 400, 1080 - 125, null);
        g2d.drawImage(CARD_SHEEP, 500, 1080 - 125, null);

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
                        image = BLANK_ROAD_L;
                    } else if (road.getPlayer() == 1) {
                        image = RED_ROAD_L;
                    } else {
                        image = BLUE_ROAD_L;
                    }
                    break;
                case 2: // Road pointing to the top right ( / ) 
                    // Store the road image for the player's color
                    if (road.getPlayer() == 0) {
                        image = BLANK_ROAD_R;
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

            // Check the size of the settlement to see which image to use
            if (settlement.isLarge() == false) { // Small settlement
                // Store the small settlement image for the player's color
                if (settlement.getPlayer() == 0) {
                    image = BLANK_HOUSE_S;
                } else if (settlement.getPlayer() == 1) {
                    image = RED_HOUSE_S;
                } // Player 1: Red
                else {
                    image = BLUE_HOUSE_S;
                } // Player 2: Blue
            } else { // Large settlement
                // Store the large settlement image for the player's color
                if (settlement.getPlayer() == 0) {
                    image = BLANK_HOUSE_L;
                } else if (settlement.getPlayer() == 1) {
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
                newTile.setThief(true);
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
    private javax.swing.JLabel buildMenuLbl;
    private javax.swing.JRadioButton buildRoadRbtn;
    private javax.swing.JRadioButton buildSettlementLRBtn;
    private javax.swing.JRadioButton buildSettlementSRBtn;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel instructionLbl;
    private javax.swing.JLabel instructionPromptLbl;
    private javax.swing.JLabel subInstructionLbl;
    private javax.swing.JButton turnSwitchBtn;
    // End of variables declaration//GEN-END:variables
}
