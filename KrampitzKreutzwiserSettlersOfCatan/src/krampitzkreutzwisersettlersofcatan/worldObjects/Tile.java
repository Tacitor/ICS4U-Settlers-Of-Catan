/*
 * Evan Kreutzwiser & Lukas Krampitz
 * Nov 6, 2020
 * A class representing the hexagonal world tiles of the game board.
 */
package krampitzkreutzwisersettlersofcatan.worldObjects;

import animation.TileAnimationData;
import java.awt.Graphics2D;
import java.awt.Image;
import javax.swing.ImageIcon;
import krampitzkreutzwisersettlersofcatan.gui.GamePanel;
import static krampitzkreutzwisersettlersofcatan.gui.GamePanel.scaleFactor;
import textures.ImageRef;

public class Tile extends WorldObject {

    // World tile attributes
    private boolean hasThief; // If the thief is currently on this tile
    private int type; // The material type. Values are staticly available in the GamePanel class
    private int harvestRollNum; // The number that must be rolled to collect from this tile
    private Image image; //the image of the Tile, based off the type
    private int refNum; //the index number this will have in the ArrayList
    private TileAnimationData tileAnimationData;

    //image files
    private final static Image WOOD_TILE = new ImageIcon(ImageRef.class.getResource("tiles/wood.png")).getImage();
    private final static Image WHEAT_TILE = new ImageIcon(ImageRef.class.getResource("tiles/wheat.png")).getImage();
    private final static Image SHEEP_TILE = new ImageIcon(ImageRef.class.getResource("tiles/sheep.png")).getImage();
    private final static Image ORE_TILE = new ImageIcon(ImageRef.class.getResource("tiles/ore.png")).getImage();
    private final static Image DESERT_TILE = new ImageIcon(ImageRef.class.getResource("tiles/desert.png")).getImage();
    private final static Image CLAY_TILE = new ImageIcon(ImageRef.class.getResource("tiles/clay.png")).getImage();

    //animation files
    //sheep animation frames
    private final static Image SHEEP_LAYER_0 = new ImageIcon(ImageRef.class.getResource("animation/sheepLayers/sheep0.png")).getImage();
    private final static Image SHEEP_LAYER_1 = new ImageIcon(ImageRef.class.getResource("animation/sheepLayers/sheep1.png")).getImage();
    private final static Image SHEEP_LAYER_2 = new ImageIcon(ImageRef.class.getResource("animation/sheepLayers/sheep2.png")).getImage();
    private final static Image SHEEP_LAYER_3 = new ImageIcon(ImageRef.class.getResource("animation/sheepLayers/sheep3.png")).getImage();
    private final static Image[] SHEEP_LAYER_IMAGES = {SHEEP_LAYER_0, SHEEP_LAYER_1, SHEEP_LAYER_2, SHEEP_LAYER_3};

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

        tileAnimationData = new TileAnimationData();

        randomizeSmokeAnimation();
    }

    /**
     * Constructor to create a new tile at a specific position
     *
     * @param xPos The new tile's X position
     * @param yPos The new tile's Y position
     */
    public Tile(int xPos, int yPos) {
        // Initialize the tile
        this(); // With constructor chaining

        // Set the position to the passed values
        this.xPos = xPos;
        this.yPos = yPos;
        unscaledXPos = xPos;
        unscaledYPos = yPos;
    }

    /**
     * Constructor to create a new tile with the given type and position
     *
     * @param xPos The new tile's X position
     * @param yPos The new tile's Y position
     * @param type The type of resource is creates
     * @param refNum
     */
    public Tile(int xPos, int yPos, int type, int refNum) {
        // Initialize the tile and set the position
        this(xPos, yPos); // With constructor chaining

        // Set the tile's type
        this.type = type;
        this.refNum = refNum;

        image = applyImage();
    }

    /**
     * Constructor to create a new tile with the given harvesting number, type,
     * and position
     *
     * @param xPos The new tile's X position
     * @param yPos The new tile's Y position
     * @param type The type of resource is creates
     * @param harvestRollNum The number that must be rolled to collect the
     * associated resource type
     * @param refNum
     */
    public Tile(int xPos, int yPos, int type, int harvestRollNum, int refNum) {
        // Initialize the tile and set the position and type
        this(xPos, yPos, type, refNum); // With constructor chaining

        // Set the tile's harvesting number
        this.harvestRollNum = harvestRollNum;
    }

    /**
     * Get the resource type of the tile
     *
     * @return The type of resource harvestable from the tile
     */
    public int getType() {
        return type;
    }

    /**
     * Set the resource type of the tile
     *
     * @param type The tile's new resource type
     */
    public void setType(int type) {
        this.type = type;
        image = applyImage();
    }

    /**
     * Get the image of the tile
     *
     * @return
     */
    public Image getImage() {
        return image;
    }

    /**
     * set the image for the tile
     *
     * @param i
     */
    public void setImage(Image i) {
        image = i;
    }

    /**
     * Dynamically set the image based on the type
     *
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
                return ImageRef.ERROR_IMAGE;
        }
    }

    /**
     * If the tile currently has a thief on it
     *
     * @return A boolean representing the presence of a thief
     */
    public boolean hasThief() {
        return hasThief;
    }

    /**
     * Set whether or not a thief is currently on the tile
     *
     * @param hasThief A boolean representing the presence of a thief
     */
    public void setThief(boolean hasThief) {
        this.hasThief = hasThief;
    }

    /**
     * Get the number that must be rolled to collect this tile's resource
     *
     * @return The number that must be rolled
     */
    public int getHarvestRollNum() {
        return harvestRollNum;
    }

    /**
     * Set whether or not a thief is currently on the tile
     *
     * @param harvestRollNum A boolean representing the presence of a thief
     */
    public void setHarvestRollNum(int harvestRollNum) {
        this.harvestRollNum = harvestRollNum;
    }

    /**
     * Get the reference number of the Tile. This is the spot in the ArrayList
     * it will occupy
     *
     * @return
     */
    public int getRefNum() {
        return refNum;
    }

    /**
     * Set the reference number of the Tile. This is the spot in the ArrayList
     * it will occupy
     *
     * @param refNum
     */
    public void setRefNum(int refNum) {
        this.refNum = refNum;
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

        //the array of images to pull from
        Image[] imageArray;
        //the index of the array that contains the current frame of animation
        int frameIndex;

        // Check the type
        if (type == 4) { //for all the sheep tiles

            imageArray = SHEEP_LAYER_IMAGES;
            //pick one of the frame times
            frameTime = tileAnimationData.getFrameTimeSheep();

            //decide if a new frame needs to be displayed or if the current one is still the one it should be on
            if (System.currentTimeMillis() - (tileAnimationData.getLastFrameStart() + tileAnimationData.getFrameTimeOffset()) > frameTime) {
                //yes it is time for a new frame

                //debug frame times
                //System.out.println("Frame time: " + (System.currentTimeMillis() - lastFrameStart));
                //calculate the index the frame needs to be pulled from
                frameIndex = tileAnimationData.getCurrentFrameIndex() + 1; //the new frame will just be one after the current one

                //and make a check that it won't be out of bounds
                if (frameIndex >= imageArray.length) {
                    frameIndex = 0; //reset it to the beginning
                }

                //get the new frame
                image = imageArray[frameIndex];

                //update the time
                tileAnimationData.setLastFrameStart(System.currentTimeMillis());

                //update the frame index
                tileAnimationData.setCurrentFrameIndex(frameIndex);

            } else { //if the minimum frame has not yet passed pass the current frame again
                image = imageArray[tileAnimationData.getCurrentFrameIndex()];
            }

        } else {
            image = ImageRef.ERROR_IMAGE;
        }

        return image;
    }

    /**
     * The code for drawing what needs to be drawn when animating the Tile
     *
     * @param g2d
     * @param gamePanel
     */
    public void drawAnimationLayer(Graphics2D g2d, GamePanel gamePanel) {
        //based on the type of Tile decide what kind of animation it should get
        if (type == 4) { //for the sheep tile

            //save the image that will be drawn
            Image sheepLayerImage = getAnimationFrame();

            //draw the frame of animation with the sheep
            //draw code coppied from the draw() method from GamePanel.java
            g2d.drawImage(sheepLayerImage,
                    xPos,
                    /**
                     * Move the Tile 20 pixels higher because the coordinates
                     * for the tiles were derived from hexagons with .png files
                     * 150x130 in size. Now after Alex Eckardt has made new art
                     * with layering making the map look more 3D, the files
                     * sizes are and extra 20 pixels higher (150 x 150).
                     */
                    (int) (yPos - (20 / scaleFactor)),
                    gamePanel.getImgWidth(sheepLayerImage),
                    gamePanel.getImgHeight(sheepLayerImage), null);

        }
    }

    /**
     * Create an identical copy of the world tile
     *
     * @return the new Tile instance
     */
    @Override
    public Tile clone() {
        // Create a new instance with the same properties
        Tile copy = new Tile(xPos, yPos, type, harvestRollNum);
        copy.hasThief = hasThief;
        copy.refNum = refNum;
        // Return the copy
        return copy;
    }

    /**
     * Compare this tile to another to check if they are the same
     *
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
     *
     * @return The tile's attributes as a string
     */
    @Override
    public String toString() {
        // Create a String out of the tile's attributes
        return "World Tile:\n"
                + super.toString() + "\n"
                + "Type: " + type + "\n"
                + "Has Thief: " + hasThief + "\n"
                + "Harvesting Dice Roll: " + harvestRollNum + "\n";
    }

    /**
     * Set the animation offset values based on the smoke animation for the
     * Settlements
     */
    private void randomizeSmokeAnimation() {
        //set the animation radomizer values
        //as of right now the Node is set to a small size, thefore base this off of the smoke animation
        tileAnimationData.setFrameTimeOffset((int) (Math.random() * tileAnimationData.getFrameTimeSheep())); //set it to a random value between 0-500ms. This will shift around when the frames will change in comparison to eachother
        tileAnimationData.setCurrentFrameIndex((int) (Math.random() * SHEEP_LAYER_IMAGES.length)); //pick a random number of frames to offset the animation by
    }
}
