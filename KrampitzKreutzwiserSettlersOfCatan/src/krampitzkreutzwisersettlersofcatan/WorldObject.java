/*
 * 
 * Evan Kreutzwiser
 * 2020
 */
package krampitzkreutzwisersettlersofcatan;

/**
 *
 * @author Evan
 */
public abstract class WorldObject {

    private int xPos;
    private int yPos;


    /**
     * Get the X position of the world object
     * @return The object's current X position
     */
    public final int getXPos() {
        return xPos;
    }

    /**
     * Set the X position of the world object
     * @param xPos The object's new X position
     */
    public final void setXPos(int xPos) {
        this.xPos = xPos;
    }

    /**
     * Get the Y position of the world object
     * @return The object's current Y position
     */
    public final int getYPos() {
        return yPos;
    }

    /**
     * Set the Y position of the world object
     * @param yPos The object's new Y position
     */
    public final void setYPos(int yPos) {
        this.yPos = yPos;
    }
}
