/*
 * Evan Kreutzwiser
 * Nov 6, 2020
 * A class representing the hexagonal world tiles of the game board.
 */
package krampitzkreutzwisersettlersofcatan;

import java.awt.Image;
import javax.swing.ImageIcon;


public class Tile extends WorldObject {

    // World tile attributes
    private boolean hasThief; // If the thief is currently on this tile
    private int type; // The material type. Values are staticly available in the GamePanel class
    private int harvestRollNum; // The number that must be rolled to collect from this tile
    private Image image; //the image of the Tile, based off the type
    
    //image files
    private final static Image WOOD_TILE = new ImageIcon("src\\textures\\wood.png").getImage(); 
    private final static Image WHEAT_TILE = new ImageIcon("src\\textures\\wheat.png").getImage(); 
    private final static Image SHEEP_TILE = new ImageIcon("src\\textures\\sheep.png").getImage(); 
    private final static Image ORE_TILE = new ImageIcon("src\\textures\\ore.png").getImage(); 
    private final static Image DESERT_TILE = new ImageIcon("src\\textures\\desert.png").getImage(); 
    private final static Image CLAY_TILE = new ImageIcon("src\\textures\\clay.png").getImage(); 
    private final static Image WATER_TILE = new ImageIcon("src\\textures\\water.png").getImage(); 
    
    /**
     * Constructor for a blank tile
     */
    public Tile() {
        // Initialize the position
        xPos = 0;
        yPos = 0;
        
        // Initialize tile attributes
        hasThief = false; // Thief is not on this tile
        type = 0;
        harvestRollNum = 0; // Cannot harvest from here without setup
        image = applyImage();
    }
    
    /**
     * Constructor to create a new tile at a specific position
     * @param xPos The new tile's X position
     * @param yPos The new tile's Y position
     */
    public Tile(int xPos, int yPos) {
        // Initialize the tile
        this(); // With constructor chaining
        
        // Set the position to the passed values
        this.xPos = xPos;
        this.yPos = yPos;
    }
    
    /**
     * Constructor to create a new tile with the given type and position
     * @param xPos The new tile's X position
     * @param yPos The new tile's Y position
     * @param type The type of resource is creates
     */
    public Tile(int xPos, int yPos, int type) {
        // Initialize the tile and set the position
        this(xPos, yPos); // With constructor chaining
    
        // Set the tile's type
        this.type = type;
    }
    
    /**
     * Constructor to create a new tile with the given harvesting number, type, and position
     * @param xPos The new tile's X position
     * @param yPos The new tile's Y position
     * @param type The type of resource is creates
     * @param harvestRollNum The number that must be rolled to collect the associated resource type
     */
    public Tile(int xPos, int yPos, int type, int harvestRollNum) {
        // Initialize the tile and set the position and type
        this(xPos, yPos, type); // With constructor chaining
    
        // Set the tile's harvesting number
        this.harvestRollNum = harvestRollNum;
    }
    
    
    /**
     * Get the resource type of the tile
     * @return The type of resource harvestable from the tile
     */
    public int getType() {
        return type;
    }

    /**
     * Set the resource type of the tile
     * @param type The tile's new resource type
     */
    public void setType(int type) {
        this.type = type;
    }
    
    /**
     * Get the image of the tile
     * @return 
     */
    public Image getImage() {
        return image;
    }
    
    /**
     * set the image for the tile
     * @param i 
     */
    public void setImage(Image i) {
        image = i;
    }
    
    /**
     * Dynamically set the image based on the type 
     * @return 
     */
    private Image applyImage() {
        switch (type) {
            case 0:
                return DESERT_TILE;
            case 1:
                return CLAY_TILE;
            case 2:
                return WOOD_TILE;
            case 3:
                return WHEAT_TILE;
            case 4:
                return SHEEP_TILE;
            case 5:
                return ORE_TILE;
            default:
                return WATER_TILE;
        }
    }
    
    /**
     * If the tile currently has a thief on it
     * @return A boolean representing the presence of a thief
     */
    public boolean hasThief() {
        return hasThief;
    }

    /**
     * Set whether or not a thief is currently on the tile
     * @param hasThief A boolean representing the presence of a thief
     */
    public void setThief(boolean hasThief) {
        this.hasThief = hasThief;
    }
    
    /**
     * Get the number that must be rolled to collect this tile's resource
     * @return The number that must be rolled
     */
    public int getHarvestRollNum() {
        return harvestRollNum;
    }

    /**
     * Set whether or not a thief is currently on the tile
     * @param harvestRollNum A boolean representing the presence of a thief
     */
    public void setHarvestRollNum(int harvestRollNum) {
        this.harvestRollNum = harvestRollNum;
    }
    
    
    /**
     * Create an identical copy of the world tile
     * @return the new Tile instance
     */
    @Override
    public Tile clone() {
        // Create a new instance with the same properties
        Tile copy = new Tile(xPos, yPos, type, harvestRollNum);
        copy.hasThief = hasThief;
        // Return the copy
        return copy;
    }
    
    /**
     * Compare this tile to another to check if they are the same
     * @param other The tile to compare to
     * @return If the tiles are equal or not
     */
    public boolean equals(Tile other) {
        // Compare all of the properties of the tiles
        return super.equals(other) 
                && type == other.type
                && hasThief == other.hasThief
                && harvestRollNum == other.harvestRollNum;
    }
    
    /**
     * Create a string representations of the tile
     * @return The tile's attributes as a string
     */
    @Override
    public String toString(){
        // Create a String out of the tile's attributes
        return "World Tile:\n"
                + super.toString() + "\n"
                + "Type: " + type + "\n"
                + "Has Thief: " + hasThief + "\n"
                + "Harvesting Dice Roll: " + harvestRollNum + "\n";
    }
}
