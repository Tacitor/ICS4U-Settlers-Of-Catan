/*
 * Lukas Krampitz
 * Feb 16, 2022
 * A data storing class that holds all the variables for animating NodeSettlements
 * This class is created as a way to save the current animation type data from being 
 * reset after saving and loading an online game when changing turns.
 */
package animation;

/**
 *
 * @author Tacitor
 */
public class TileAnimationData {

    //Atributes
    //animation related
    private int frameTimeSheep; //the time in miliseconds each frame for sheep eating should be displayed for
    private int frameTimeOffset; //the time in milliseconds the frame times will be offset by 
    private long lastFrameStart; //the system time (in miliseconds) when the previous frame started displaying 
    private int currentFrameIndex; //the index within the image array the 

    //Constructors
    /**
     * Default constructor
     */
    public TileAnimationData() {
        frameTimeSheep = 3000; //the default frame time should be 3 seconds
        lastFrameStart = 0; //the last frame has never (yet) been displayed so set it to 0
        currentFrameIndex = 0;
    }

    /**
     * Get the time in milliseconds each frame is displayed for
     *
     * @return
     */
    public int getFrameTimeSheep() {
        return frameTimeSheep;
    }

    /**
     * Set the time in milliseconds each frame is displayed for
     *
     * @param frameTimeSheep
     */
    public void setFrameTimeSheep(int frameTimeSheep) {
        this.frameTimeSheep = frameTimeSheep;
    }

    /**
     * Get the system time when the previous frame began displaying
     *
     * @return
     */
    public long getLastFrameStart() {
        return lastFrameStart;
    }

    /**
     * Set the system time when the previous frame began displaying
     *
     * @param lastFrameStart
     */
    public void setLastFrameStart(long lastFrameStart) {
        this.lastFrameStart = lastFrameStart;
    }

    /**
     * Get the offset in milliseconds each frame is displayed for
     *
     * @return
     */
    public int getFrameTimeOffset() {
        return frameTimeOffset;
    }

    /**
     * Set the offset in milliseconds each frame is displayed for
     *
     * @param frameTimeOffset
     */
    public void setFrameTimeOffset(int frameTimeOffset) {
        this.frameTimeOffset = frameTimeOffset;
    }

    /**
     * Get the index of the current frame being displayed
     *
     * @return
     */
    public int getCurrentFrameIndex() {
        return currentFrameIndex;
    }

    /**
     * Set the index of the current frame being displayed
     *
     * @param currentFrameIndex
     */
    public void setCurrentFrameIndex(int currentFrameIndex) {
        this.currentFrameIndex = currentFrameIndex;
    }

    /**
     * Save the data from the gamePanel regarding the animation data of the
     * Tiles.
     *
     * @param gamePanel
     */
    /*public static void saveTileAnimationData(GamePanel gamePanel) {
        //Store the ArrayList of tiles
        ArrayList<Tile> tiles = gamePanel.getTiles();

        //Init the array list
        tileAnimationData = new ArrayList<>();

        //go through and add all the animation data from the tiles
        for (Tile tile : tiles) {
            tileAnimationData.add(tile.getTileAnimationData().clone());
        }
    }*/
    /**
     * Restore the animation data into the Tiles from the gamePanel
     *
     * @param gamePanel
     */
    /*public static void restoreTileAnimationData(GamePanel gamePanel) {
        //Store the ArrayList of tiles
        ArrayList<Tile> tiles = gamePanel.getTiles();

        //go through and restore all the animation data from the tiles
        for (int i = 0; i < tiles.size(); i++) {
            tiles.get(i).setTileAnimationData(tileAnimationData.get(i));
        }

        //then write the new ArrayList of NodeSettlemtns back to the gamePanel
        gamePanel.setTiles(tiles);
    }*/
    /**
     * Set it to a String
     *
     * @return
     */
    @Override
    public String toString() {
        return "frameTimeSheep: " + frameTimeSheep
                + "\nframeTimeOffset: " + frameTimeOffset
                + "\nlastFrameStart: " + lastFrameStart
                + "\ncurrentFrameIndex: " + currentFrameIndex;
    }

    /**
     * Return an exact copy
     *
     * @return
     */
    @Override
    public TileAnimationData clone() {
        //create a new Object
        TileAnimationData copy = new TileAnimationData();

        copy.frameTimeSheep = frameTimeSheep;
        copy.frameTimeOffset = frameTimeOffset;
        copy.lastFrameStart = lastFrameStart;
        copy.currentFrameIndex = currentFrameIndex;

        return copy;
    }

}
