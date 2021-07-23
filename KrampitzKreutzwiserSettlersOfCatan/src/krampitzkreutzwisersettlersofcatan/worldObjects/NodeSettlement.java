/*
 * Evan Kreutzwiser
 * Nov 8, 2020
 * A class representing the settlement placements of the board, and some of the
 * board node structure
 */
package krampitzkreutzwisersettlersofcatan.worldObjects;

import java.util.ArrayList;

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
}
