/*
 * Evan Kreutzwiser
 * Nov 8, 2020
 * The class representing the road placement areas of the board, and the connections
 * between settlement nodes, forming the node web of the board.
 */
package krampitzkreutzwisersettlersofcatan.worldObjects;


public class NodeRoad extends WorldObject {

    // Attributes
    private int player;
    private int orientation; // 0-2, represents the rotation of the road
    private NodeSettlement settlement1;
    private NodeSettlement settlement2;
    
    
    /**
     * Constructor for a blank tile
     */
    public NodeRoad() {
        // Initialize the position
        xPos = 0;
        yPos = 0;
        
        // Initialize road attributes
        player = 0;
        orientation = 0;
        settlement1 = null;
        settlement2 = null;
    }
    
    /**
     * Constructor to create a new road node at a specific position
     * @param xPos The new node's X position
     * @param yPos The new node's Y position
     */
    public NodeRoad(int xPos, int yPos) {
        // Initialize the node
        this(); // With constructor chaining
        
        // Set the position to the passed values
        this.xPos = xPos;
        this.yPos = yPos;
        unscaledXPos = xPos;
        unscaledYPos = yPos;
    }
    
    /**
     * Constructor to create a new road node with the given rotation and position
     * @param xPos The new node's X position
     * @param yPos The new node's Y position
     * @param orientation The new node's rotation id, from 0 to 2. if outside the range the closest value will be used
     */
    public NodeRoad(int xPos, int yPos, int orientation) {
        // Initialize the node and positon
        this(xPos, yPos); // With constructor chaining
        
        // Set the orientation with range bounds enforcement
        this.orientation = Math.min(2, Math.max(0, orientation));
    }
    
    /**
     * Constructor to create a new road node with references to connected settlements
     * @param xPos The new node's X position
     * @param yPos The new node's Y position
     * @param orientation The new node's rotation id, from 0 to 2. if outside the range the closest value will be used
     * @param settlement1 A connected settlement node
     * @param settlement2 A connected settlement node
     */
    public NodeRoad(int xPos, int yPos, int orientation, NodeSettlement settlement1, NodeSettlement settlement2) {
        // Initialize the node, positon, and rotation
        this(xPos, yPos, orientation); // With constructor chaining
        
        // Set the settlement references
        this.settlement1 = settlement1;
        this.settlement2 = settlement2;
    }
    
    
    /**
     * Get the player ID of this road's owner
     * @return The player ID, or 0 if not built upon yet
     */
    public int getPlayer() {
        return player;
    }

    /**
     * Set the player ID of this road's owner
     * This should only be called once, to avoid violating game rules
     * @param player The new player ID
     */
    public void setPlayer(int player) {
        this.player = player;
    }
    
    /**
     * Get the rotation id of this road
     * @return The rotation id from 0 to 2
     */
    public int getOrientation() {
        return orientation;
    }

    /**
     * Get the rotation id of this road
     * @param orientation The new rotation id, from 0 to 2. if outside the range the closest value will be used
     */
    public void setOrientation(int orientation) {
        // Set the orientation with range bounds enforcement
        this.orientation = Math.min(2, Math.max(0, orientation));
    }
    
    /**
     * Get one of the connected settlement nodes
     * @param id Which node to get (1-2)
     * @return The settlement. settlement IDs outside the 1-2 range will return null
     */
    public NodeSettlement getSettlement(int id) {
        // Return a settlement node (or null) based on the ID given by the caller
        switch (id) {
            case 1: // Settlement 1
                return settlement1;
                
            case 2: // Settlement 2
                return settlement2;
                
            default: // invalid id
                return null;
        }
    }
    
    /**
     * Set one of the connected settlement references
     * @param id Which settlement to set (1-2). will do nothing if outside the 1-3 range
     * @param settlement The settlement to save a reference to
     */
    public void setSettlement(int id, NodeSettlement settlement) {
        // Set a settlement node based on the ID given by the caller
        switch (id) {
            case 1: // Tile 1
                this.settlement1 = settlement;
                break;
                
            case 2: // Tile 2
                this.settlement2 = settlement;
                break;
        }
        // other values have no effect
    }
    
    @Override
    public NodeRoad clone() {    
        // Create a copy of the road with the same attributes
        NodeRoad copy = new NodeRoad(xPos, yPos, orientation, settlement1, settlement2);
        // Set the player id
        copy.player = player;
        return copy;
    }
    
    /**
     * Compare this road to another to check if they are the same
     * @param other The road node to compare to
     * @return If the roads are the same or not
     */
    public boolean equals(NodeRoad other) {
        // Compare all of the properties of the road nodes
        return super.equals(other) 
                && player == other.player
                && orientation == other.orientation
                && settlement1 == other.settlement1
                && settlement2 == other.settlement2;
    }
    
    /**
     * Create a string representations of the road node
     * @return The road's attributes as a string
     */
    @Override
    public String toString(){
        // Create a String out of the tile's attributes
        return "Road Node:\n"
                + super.toString() + "\n"
                + "Player ID: " + player + "\n"
                + "Road Orientatin: " + orientation + "\n";
    }
}
