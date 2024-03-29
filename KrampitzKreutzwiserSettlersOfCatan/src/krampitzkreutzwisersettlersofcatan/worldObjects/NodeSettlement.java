/*
 * Evan Kreutzwiser
 * Nov 8, 2020
 * A class representing the settlement placements of the board, and some of the
 * board node structure
 */
package krampitzkreutzwisersettlersofcatan.worldObjects;

import animation.SettlementAnimationData;
import java.awt.Image;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import textures.ImageRef;

public class NodeSettlement extends WorldObject {

    //statics vars
    private static int ageCounter = 0; //keeps track of the age the next settlement should have

    // Attributes
    private int player;
    private boolean large;
    private int age; //the age of the settlement in the order that it was built
    private int refNum; //the index number this will have in the ArrayList
    // References to the connected roads
    private NodeRoad road1;
    private NodeRoad road2;
    private NodeRoad road3;
    // References to adjacent world tiles
    private Tile hex1;
    private Tile hex2;
    private Tile hex3;
    //animation related
    private SettlementAnimationData settlementAnimationData;

    //Images for the settlements
    public final static Image BLANK_HOUSE = new ImageIcon(ImageRef.class.getResource("playerPieces/blankHouse.png")).getImage(); // Blank image for unowned settlement nodes 
    //the red ones
    private final static Image RED_HOUSE_L0 = new ImageIcon(ImageRef.class.getResource("playerPieces/redHouseLights0.png")).getImage();
    private final static Image RED_HOUSE_L1 = new ImageIcon(ImageRef.class.getResource("playerPieces/redHouseLights1.png")).getImage();
    private final static Image RED_HOUSE_L2 = new ImageIcon(ImageRef.class.getResource("playerPieces/redHouseLights2.png")).getImage();
    private final static Image RED_HOUSE_L3 = new ImageIcon(ImageRef.class.getResource("playerPieces/redHouseLights3.png")).getImage();
    private final static Image RED_HOUSE_S0 = new ImageIcon(ImageRef.class.getResource("playerPieces/redHouseSmoke0.png")).getImage();
    private final static Image RED_HOUSE_S1 = new ImageIcon(ImageRef.class.getResource("playerPieces/redHouseSmoke1.png")).getImage();
    private final static Image RED_HOUSE_S2 = new ImageIcon(ImageRef.class.getResource("playerPieces/redHouseSmoke2.png")).getImage();
    private final static Image RED_HOUSE_S3 = new ImageIcon(ImageRef.class.getResource("playerPieces/redHouseSmoke3.png")).getImage();
    private final static Image RED_HOUSE_S4 = new ImageIcon(ImageRef.class.getResource("playerPieces/redHouseSmoke4.png")).getImage();
    public final static Image[] RED_HOUSES_S = new Image[]{
        RED_HOUSE_S0, RED_HOUSE_S1, RED_HOUSE_S2, RED_HOUSE_S3, RED_HOUSE_S4, RED_HOUSE_S3, RED_HOUSE_S2, RED_HOUSE_S1};
    public final static Image[] RED_HOUSES_L = new Image[]{
        RED_HOUSE_L0, RED_HOUSE_L1, RED_HOUSE_L0, RED_HOUSE_L2, RED_HOUSE_L0, RED_HOUSE_L3};
    //blue
    private final static Image BLUE_HOUSE_L0 = new ImageIcon(ImageRef.class.getResource("playerPieces/blueHouseLights0.png")).getImage();
    private final static Image BLUE_HOUSE_L1 = new ImageIcon(ImageRef.class.getResource("playerPieces/blueHouseLights1.png")).getImage();
    private final static Image BLUE_HOUSE_L2 = new ImageIcon(ImageRef.class.getResource("playerPieces/blueHouseLights2.png")).getImage();
    private final static Image BLUE_HOUSE_L3 = new ImageIcon(ImageRef.class.getResource("playerPieces/blueHouseLights3.png")).getImage();
    private final static Image BLUE_HOUSE_S0 = new ImageIcon(ImageRef.class.getResource("playerPieces/blueHouseSmoke0.png")).getImage();
    private final static Image BLUE_HOUSE_S1 = new ImageIcon(ImageRef.class.getResource("playerPieces/blueHouseSmoke1.png")).getImage();
    private final static Image BLUE_HOUSE_S2 = new ImageIcon(ImageRef.class.getResource("playerPieces/blueHouseSmoke2.png")).getImage();
    private final static Image BLUE_HOUSE_S3 = new ImageIcon(ImageRef.class.getResource("playerPieces/blueHouseSmoke3.png")).getImage();
    private final static Image BLUE_HOUSE_S4 = new ImageIcon(ImageRef.class.getResource("playerPieces/blueHouseSmoke4.png")).getImage();
    public final static Image[] BLUE_HOUSES_S = new Image[]{
        BLUE_HOUSE_S0, BLUE_HOUSE_S1, BLUE_HOUSE_S2, BLUE_HOUSE_S3, BLUE_HOUSE_S4, BLUE_HOUSE_S3, BLUE_HOUSE_S2, BLUE_HOUSE_S1};
    public final static Image[] BLUE_HOUSES_L = new Image[]{
        BLUE_HOUSE_L0, BLUE_HOUSE_L1, BLUE_HOUSE_L0, BLUE_HOUSE_L2, BLUE_HOUSE_L0, BLUE_HOUSE_L3};
    //white
    private final static Image WHITE_HOUSE_L0 = new ImageIcon(ImageRef.class.getResource("playerPieces/whiteHouseLights0.png")).getImage();
    private final static Image WHITE_HOUSE_L1 = new ImageIcon(ImageRef.class.getResource("playerPieces/whiteHouseLights1.png")).getImage();
    private final static Image WHITE_HOUSE_L2 = new ImageIcon(ImageRef.class.getResource("playerPieces/whiteHouseLights2.png")).getImage();
    private final static Image WHITE_HOUSE_L3 = new ImageIcon(ImageRef.class.getResource("playerPieces/whiteHouseLights3.png")).getImage();
    private final static Image WHITE_HOUSE_S0 = new ImageIcon(ImageRef.class.getResource("playerPieces/whiteHouseSmoke0.png")).getImage();
    private final static Image WHITE_HOUSE_S1 = new ImageIcon(ImageRef.class.getResource("playerPieces/whiteHouseSmoke1.png")).getImage();
    private final static Image WHITE_HOUSE_S2 = new ImageIcon(ImageRef.class.getResource("playerPieces/whiteHouseSmoke2.png")).getImage();
    private final static Image WHITE_HOUSE_S3 = new ImageIcon(ImageRef.class.getResource("playerPieces/whiteHouseSmoke3.png")).getImage();
    private final static Image WHITE_HOUSE_S4 = new ImageIcon(ImageRef.class.getResource("playerPieces/whiteHouseSmoke4.png")).getImage();
    public final static Image[] WHITE_HOUSES_S = new Image[]{
        WHITE_HOUSE_S0, WHITE_HOUSE_S1, WHITE_HOUSE_S2, WHITE_HOUSE_S3, WHITE_HOUSE_S4, WHITE_HOUSE_S3, WHITE_HOUSE_S2, WHITE_HOUSE_S1};
    public final static Image[] WHITE_HOUSES_L = new Image[]{
        WHITE_HOUSE_L0, WHITE_HOUSE_L1, WHITE_HOUSE_L0, WHITE_HOUSE_L2, WHITE_HOUSE_L0, WHITE_HOUSE_L3};
    //orange
    private final static Image ORANGE_HOUSE_L0 = new ImageIcon(ImageRef.class.getResource("playerPieces/orangeHouseLights0.png")).getImage();
    private final static Image ORANGE_HOUSE_L1 = new ImageIcon(ImageRef.class.getResource("playerPieces/orangeHouseLights1.png")).getImage();
    private final static Image ORANGE_HOUSE_L2 = new ImageIcon(ImageRef.class.getResource("playerPieces/orangeHouseLights2.png")).getImage();
    private final static Image ORANGE_HOUSE_L3 = new ImageIcon(ImageRef.class.getResource("playerPieces/orangeHouseLights3.png")).getImage();
    private final static Image ORANGE_HOUSE_S0 = new ImageIcon(ImageRef.class.getResource("playerPieces/orangeHouseSmoke0.png")).getImage();
    private final static Image ORANGE_HOUSE_S1 = new ImageIcon(ImageRef.class.getResource("playerPieces/orangeHouseSmoke1.png")).getImage();
    private final static Image ORANGE_HOUSE_S2 = new ImageIcon(ImageRef.class.getResource("playerPieces/orangeHouseSmoke2.png")).getImage();
    private final static Image ORANGE_HOUSE_S3 = new ImageIcon(ImageRef.class.getResource("playerPieces/orangeHouseSmoke3.png")).getImage();
    private final static Image ORANGE_HOUSE_S4 = new ImageIcon(ImageRef.class.getResource("playerPieces/orangeHouseSmoke4.png")).getImage();
    public final static Image[] ORANGE_HOUSES_S = new Image[]{
        ORANGE_HOUSE_S0, ORANGE_HOUSE_S1, ORANGE_HOUSE_S2, ORANGE_HOUSE_S3, ORANGE_HOUSE_S4, ORANGE_HOUSE_S3, ORANGE_HOUSE_S2, ORANGE_HOUSE_S1};
    public final static Image[] ORANGE_HOUSES_L = new Image[]{
        ORANGE_HOUSE_L0, ORANGE_HOUSE_L1, ORANGE_HOUSE_L0, ORANGE_HOUSE_L2, ORANGE_HOUSE_L0, ORANGE_HOUSE_L3};

    /**
     * Constructor to create a new blank settlement node
     */
    public NodeSettlement() {
        // Initialize the position
        xPos = 0;
        yPos = 0;

        // Initialize settlement attributes
        player = 0;
        large = false;
        road1 = null; // No connected roads
        road2 = null;
        road3 = null;
        age = -1; //player is set to 0, so age is -1, it has no age

        //init the Object conaining the data for animating the Settlement Node
        settlementAnimationData = new SettlementAnimationData();

        //Set random values for the variables within settlementAnimationData, and have it based on a small settlement with smoke
        randomizeSmokeAnimation();
    }

    /**
     * Constructor to create a new settlement node at a specific position
     *
     * @param xPos The new node's X position
     * @param yPos The new node's Y position
     * @param refNum
     */
    public NodeSettlement(int xPos, int yPos, int refNum) {
        // Initialize the node
        this(); // With constructor chaining

        // Set the position to the passed values
        this.xPos = xPos;
        this.yPos = yPos;
        unscaledXPos = xPos;
        unscaledYPos = yPos;
        this.refNum = refNum;
    }

    /**
     * Constructor to create a new settlement node with road connections
     *
     * @param xPos The new node's X position
     * @param yPos The new node's Y position
     * @param road1 One of the connected roads
     * @param road2 Another connected road
     * @param road3 A third connected road
     * @param refNum
     */
    public NodeSettlement(int xPos, int yPos, NodeRoad road1, NodeRoad road2, NodeRoad road3, int refNum) {
        // Initialize the node
        this(xPos, yPos, refNum); // With constructor chaining

        // Set the road references
        this.road1 = road1;
        this.road2 = road2;
        this.road3 = road3;
    }

    /**
     * Constructor to create a new settlement node with world tile references
     *
     * @param xPos The new node's X position
     * @param yPos The new node's Y position
     * @param hex1 One of the adjacent tiles
     * @param hex2 Another tiles
     * @param hex3 A third tiles
     * @param refNum
     */
    public NodeSettlement(int xPos, int yPos, Tile hex1, Tile hex2, Tile hex3, int refNum) {
        // Initialize the node
        this(xPos, yPos, refNum); // With constructor chaining

        // Set the tile references
        this.hex1 = hex1;
        this.hex2 = hex2;
        this.hex3 = hex3;
    }

    /**
     * Constructor to create a new settlement node with world tile references
     *
     * @param xPos The new node's X position
     * @param yPos The new node's Y position
     * @param road1 One of the connected roads
     * @param road2 Another connected road
     * @param road3 A third connected road
     * @param hex1 One of the adjacent tiles
     * @param hex2 Another tiles
     * @param hex3 A third tiles
     * @param refNum
     */
    public NodeSettlement(int xPos, int yPos, NodeRoad road1, NodeRoad road2,
            NodeRoad road3, Tile hex1, Tile hex2, Tile hex3, int refNum) {
        // Initialize the node and set road references
        this(xPos, yPos, road1, road2, road3, refNum); // With constructor chaining

        // Set the tile references
        this.hex1 = hex1;
        this.hex2 = hex2;
        this.hex3 = hex3;
    }

    /**
     * Get the player ID of this settlement's owner
     *
     * @return The player ID, or 0 if not built upon yet
     */
    public int getPlayer() {
        return player;
    }

    /**
     * Set the player ID of this settlement's owner This should only be called
     * once, to avoid violating game rules
     *
     * @param player The new player ID
     */
    public void setPlayer(int player) {
        this.player = player;

        //update the age
        if (player == 0) { //is the settlment ownership is being taken away re-set the age
            age = -1;
        } else {
            //set the age to the next one
            age = ageCounter;
            //increment the counter
            ageCounter++;
        }
    }

    /**
     * Get the size of this settlement
     *
     * @return If the settlement is a large settlement
     */
    public boolean isLarge() {
        return large;
    }

    /**
     * Set the size of the settlement This should only be called once, to
     * increase the size, to avoid violating game rules
     *
     * @param isLarge The new size, as a boolean representing if it is a large
     * settlement
     */
    public void setLarge(boolean isLarge) {

        //reset the randomized animation vars for the Node based on the new size
        //but only if the size is acctually changing
        if (isLarge != isLarge()) {
            if (isLarge) {
                randomizeLightAnimation();
            } else {
                randomizeSmokeAnimation();
            }
        }

        this.large = isLarge;
    }

    /**
     * Get the reference number of the settlement. This is the spot in the
     * ArrayList it will occupy
     *
     * @return
     */
    public int getRefNum() {
        return refNum;
    }

    /**
     * Set the reference number of the settlement. This is the spot in the
     * ArrayList it will occupy
     *
     * @param refNum
     */
    public void setRefNum(int refNum) {
        this.refNum = refNum;
    }

    /**
     * Get the age of the settlement.
     *
     * @return age
     */
    public int getAge() {
        return age;
    }

    /**
     * Set the age of the settlement.
     *
     * @param age
     */
    public void setAge(int age) {
        this.age = age;
    }

    /**
     * Get one of the connected roads
     *
     * @param id Which road to get (1-3)
     * @return The road object. road IDs outside the 1-3 range will return null
     */
    public NodeRoad getRoad(int id) {
        // Return a road (or null) based on the ID given by the caller
        switch (id) {
            case 1: // Road 1
                return road1;

            case 2: // Road 2
                return road2;

            case 3: // Road 3
                return road3;

            default: // invalid id
                return null;
        }
    }

    /**
     * Set one of the connected roads
     *
     * @param id Which road to set (1-3) will do nothing if outside the 1-3
     * range
     * @param road The road to save a reference to
     */
    public void setRoad(int id, NodeRoad road) {
        // Set a road based on the ID given by the caller
        switch (id) {
            case 1: // Road 1
                this.road1 = road;
                break;

            case 2: // Road 2
                this.road2 = road;
                break;

            case 3: // Road 3
                this.road3 = road;
                break;
        }
        // other values have no effect
    }

    /**
     * Get one of the connected world tiles
     *
     * @param id Which tile to get (1-3)
     * @return The tile object. tile IDs outside the 1-3 range will return null
     */
    public Tile getTile(int id) {
        // Return a tile (or null) based on the ID given by the caller
        switch (id) {
            case 1: // Tile 1
                return hex1;

            case 2: // Tile 2
                return hex2;

            case 3: // Tile 3
                return hex3;

            default: // invalid id
                return null;
        }
    }

    /**
     * Set one of the connected tiles
     *
     * @param id Which tile to set (1-3) will do nothing if outside the 1-3
     * range
     * @param hex The tile to save a reference to
     */
    public void setTile(int id, Tile hex) {
        // Set a tile based on the ID given by the caller
        switch (id) {
            case 1: // Tile 1
                this.hex1 = hex;
                break;

            case 2: // Tile 2
                this.hex2 = hex;
                break;

            case 3: // Tile 3
                this.hex3 = hex;
                break;
        }
        // other values have no effect
    }

    /**
     * Check which road reference the passed road is stored in (1-3), or 0 if
     * this does not contain a reference to the road
     *
     * @param road The road to get the reference id of
     * @return The reference ID from 1-3, or 0 if the road is not referenced by
     * this settlement
     */
    public int getRoadNumber(NodeRoad road) {
        // Return the id of the reference
        // Looking for object references, not attribute equality
        if (road == road1) {
            return 1;
        } // Matches road 1
        else if (road == road2) {
            return 2;
        } // Matches road 2
        else if (road == road3) {
            return 3;
        } // Matches road 3
        else {
            return 0;
        } // Road not referenced
    }

    /**
     * Get the data for the current state of animations. The goal of this is to
     * save them so they can be loaded after a client receives a new update from
     * the Catan server and needs to have consistent animations.
     *
     * @return
     */
    public SettlementAnimationData getSettlementAnimationData() {
        return settlementAnimationData;
    }

    /**
     * Get the data for the current state of animations. The goal of this is to
     * save them so they can be loaded after a client receives a new update from
     * the Catan server and needs to have consistent animations.
     *
     * @param settlementAnimationData
     */
    public void setSettlementAnimationData(SettlementAnimationData settlementAnimationData) {
        this.settlementAnimationData = settlementAnimationData;

        //System.out.println("Local: " + this.settlementAnimationData.toString());
        //System.out.println("\nGiven: " + settlementAnimationData.toString() + "\n\n");
    }

    /**
     * Create an identical settlement node with the same attributes
     *
     * @return The new settlement node
     */
    @Override
    public NodeSettlement clone() {
        // Create a copy of the settlement with the same attributes
        NodeSettlement copy = new NodeSettlement(xPos, yPos, road1, road2, road3, hex1, hex2, hex3, refNum);
        // Set the player id
        copy.player = player;
        // Set the size
        copy.large = large;
        return copy;
    }

    /**
     * Compare this road to another to check if they are the same
     *
     * @param other The road node to compare to
     * @return If the roads are the same or not
     */
    public boolean equals(NodeSettlement other) {
        // Compare all of the properties of the settlement nodes
        return super.equals(other)
                && player == other.player
                && large == other.large
                && road1 == other.road1
                && road2 == other.road2
                && road3 == other.road3;
    }

    /**
     * Create a string representations of the road node
     *
     * @return The road's attributes as a string
     */
    @Override
    public String toString() {
        // Create a String out of the tile's attributes
        return "Road Node:\n"
                + super.toString() + "\n"
                + "Player ID: " + player + "\n"
                + "Is Large: " + large + "\n";
    }

    /**
     * Based off an ArrayList of settlementNodes give the cards of the oldest
     * settlements to the player's card array
     *
     * @param settlementNodes
     * @param cards
     * @param totalCardsCollected
     */
    public static void giveStartingRes(ArrayList<NodeSettlement> settlementNodes, ArrayList<Integer>[] cards, int[] totalCardsCollected) {

        //new array for the index of the oldest settlment for each player
        int[] oldestSettlementIndex = new int[cards.length];
        //init that array to all -1's
        for (int i = 0; i < oldestSettlementIndex.length; i++) {
            oldestSettlementIndex[i] = -1;
        }

        //loop through all settlements
        for (NodeSettlement settlement : settlementNodes) {

            //check the owner of said node
            if (settlement.getPlayer() != 0) {
                //compare if this settlemtns age is older than the one already saved
                //or if there is none saved, save this one
                if (oldestSettlementIndex[settlement.getPlayer()] == -1 || (settlementNodes.get(oldestSettlementIndex[settlement.getPlayer()])).age < settlement.age) {
                    oldestSettlementIndex[settlement.getPlayer()] = settlement.getRefNum();
                }
            }

        }

        //now give the user the cards from their newest settlment
        for (int playerID = 1; playerID < oldestSettlementIndex.length; playerID++) {
            //loop through the 3 tiles
            for (int tileNum = 1; tileNum < 4; tileNum++) {

                Tile tile = settlementNodes.get(oldestSettlementIndex[playerID]).getTile(tileNum);

                //don't check if the tile is a null tile
                if (tile != null) {

                    //get the type
                    int type = tile.getType();

                    //if the type isn't a desert
                    if (type != 0) {

                        //give them the first res
                        cards[playerID].add(type);
                    }
                }
            }
        }

    }

    /**
     * Get the current frame of the animation
     *
     * @return
     */
    public Image getAnimationFrame() {
        //the image the method will return
        Image image;
        int frameTime;

        // If the settlement is unowned use the blank image
        if (player == 0) {
            image = NodeSettlement.BLANK_HOUSE;
        } else { //for all the owned settlements

            //the array of images to pull from
            Image[] imageArray;
            //the index of the array that contains the current frame of animation
            int frameIndex;

            //decide if to grab the cities or the settlements
            if (large) { //cities
                //decide which array of images to grab
                switch (player) {
                    case 1:
                        imageArray = RED_HOUSES_L;
                        break;
                    case 2:
                        imageArray = BLUE_HOUSES_L;
                        break;
                    case 3:
                        imageArray = ORANGE_HOUSES_L;
                        break;
                    case 4:
                        imageArray = WHITE_HOUSES_L;
                        break;
                    default:
                        imageArray = ImageRef.ERROR_IMAGES;
                        break;
                }
                //pick one of the frame times
                frameTime = settlementAnimationData.getFrameTimeLight();
            } else { //settlements

                //decide which array of images to grab
                switch (player) {
                    case 1:
                        imageArray = RED_HOUSES_S;
                        break;
                    case 2:
                        imageArray = BLUE_HOUSES_S;
                        break;
                    case 3:
                        imageArray = ORANGE_HOUSES_S;
                        break;
                    case 4:
                        imageArray = WHITE_HOUSES_S;
                        break;
                    default:
                        imageArray = ImageRef.ERROR_IMAGES;
                        break;
                }
                //pick one of the frame times
                frameTime = settlementAnimationData.getFrameTimeSmoke();
            }

            //decide if a new frame needs to be displayed or if the current one is still the one it should be on
            if (System.currentTimeMillis() - (settlementAnimationData.getLastFrameStart() + settlementAnimationData.getFrameTimeOffset()) > frameTime) {
                //yes it is time for a new frame

                //debug frame times
                //System.out.println("Frame time: " + (System.currentTimeMillis() - lastFrameStart));
                //calculate the index the frame needs to be pulled from
                frameIndex = settlementAnimationData.getCurrentFrameIndex() + 1; //the new frame will just be one after the current one

                //and make a check that it won't be out of bounds
                if (frameIndex >= imageArray.length) {
                    frameIndex = 0; //reset it to the beginning
                }

                //get the new frame
                image = imageArray[frameIndex];

                //update the time
                settlementAnimationData.setLastFrameStart(System.currentTimeMillis());

                //update the frame index
                settlementAnimationData.setCurrentFrameIndex(frameIndex);

            } else { //if the minimum frame has not yet passed pass the current frame again
                image = imageArray[settlementAnimationData.getCurrentFrameIndex()];
            }

        }

        return image;
    }

    /**
     * Set the animation offset values based on the smoke animation for the
     * Settlements
     */
    private void randomizeSmokeAnimation() {
        //set the animation radomizer values
        //as of right now the Node is set to a small size, thefore base this off of the smoke animation
        settlementAnimationData.setFrameTimeOffset((int) (Math.random() * settlementAnimationData.getFrameTimeSmoke())); //set it to a random value between 0-500ms. This will shift around when the frames will change in comparison to eachother
        settlementAnimationData.setCurrentFrameIndex((int) (Math.random() * RED_HOUSES_S.length)); //pick a random number of frames to offset the animation by
    }

    /**
     * Set the animation offset values based on the light animation for the
     * Cities
     */
    private void randomizeLightAnimation() {
        //set the animation radomizer values
        settlementAnimationData.setFrameTimeOffset((int) (Math.random() * settlementAnimationData.getFrameTimeLight())); //set it to a random value between 0-500ms. This will shift around when the frames will change in comparison to eachother
        for (int i = 0; i < 50; i++) { //seems to increase the random ness a little more
            settlementAnimationData.setCurrentFrameIndex((int) (Math.random() * RED_HOUSES_L.length)); //pick a random number of frames to offset the animation by
        }
    }
}
