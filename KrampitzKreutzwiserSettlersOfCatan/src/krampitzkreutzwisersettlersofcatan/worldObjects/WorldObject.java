/*
 * Evan Kreutzwiser
 * Nov 4, 2020
 * An abstract class extended by the world objects (Settlement and Road nodes and Tiles). 
 * This class handles the position attributes of the objects making up the game board.
 */
package krampitzkreutzwisersettlersofcatan.worldObjects;

/**
 *
 * @author Evan
 */
public abstract class WorldObject {

    // The object's x and y postion on the screen
    protected int xPos;
    protected int yPos;
    protected int unscaledXPos;
    protected int unscaledYPos;

    /**
     * Get the X position of the world object
     *
     * @return The object's current X position
     */
    public final int getXPos() {
        return xPos;
    }

    /**
     * Set the X position of the world object
     *
     * @param xPos The object's new X position
     */
    public final void setXPos(int xPos) {
        this.xPos = xPos;
    }

    /**
     * Get the Y position of the world object
     *
     * @return The object's current Y position
     */
    public final int getYPos() {
        return yPos;
    }

    /**
     * Set the Y position of the world object
     *
     * @param yPos The object's new Y position
     */
    public final void setYPos(int yPos) {
        this.yPos = yPos;
    }

    /**
     * Get the X position of the world object before any scaling. Native to
     * 1080p
     *
     * @return The object's current X position
     */
    public final int getUnscaledXPos() {
        return unscaledXPos;
    }

    /**
     * Set the X position of the world object before any scaling. Native to
     * 1080p
     *
     * @param unscaledXPos The object's new X position
     */
    private final void setUnscaledXPos(int unscaledXPos) {
        this.unscaledXPos = unscaledXPos;
    }

    /**
     * Get the Y position of the world object before any scaling. Native to
     * 1080p
     *
     * @return The object's current Y position
     */
    public final int getUnscaledYPos() {
        return unscaledYPos;
    }

    /**
     * Set the Y position of the world object before any scaling. Native to
     * 1080p
     *
     * @param unscaledYPos The object's new Y position
     */
    private final void setUnscaledYPos(int unscaledYPos) {
        this.unscaledYPos = unscaledYPos;
    }

    /**
     * Create an identical copy of the object
     *
     * @return The copy of the world object
     */
    @Override
    public abstract WorldObject clone();

    /**
     * Check if this object's attributes are equal to another object's
     *
     * @param other The Object to compare to
     * @return Whether or not the objects are the same
     */
    public boolean equals(WorldObject other) {
        // Return if the position of the objects are equal or not
        return xPos == other.xPos && yPos == other.yPos;
    }

    /**
     * Create a string representations of the world object
     *
     * @return The object as a string
     */
    @Override
    public String toString() {
        // Create a String out of the position of the object
        return "Position (X, Y): (" + xPos + ", " + yPos + ")";
    }

}
