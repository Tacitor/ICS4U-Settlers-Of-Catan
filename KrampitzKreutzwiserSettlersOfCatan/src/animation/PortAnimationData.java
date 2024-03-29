/*
 * Lukas Krampitz
 * Mar 7, 2022
 * Used to store the the data needed for the port animations
 */
package animation;

/**
 *
 * @author Tacitor
 */
public class PortAnimationData {

    //Attributes 
    private int posTimeShip; //the time in miliseconds it will hold the position of the ship
    private long lastPosStart; //the time in miliseconds of when the previous position was assumed by the ship
    private double movePosIncrement; //the amount of pixels the ship will move by every pos update
    private int incrementCyclesPassed; //The number of times the ships's pos has been moved. Gets reset to 0 when the full animation cycle resets.
    private double shipAnimationX;
    private double shipAnimationY; //the x and y offsets from the ships resting/docked position
    private boolean outToSea; //a boolean that controls whether or not the ship is sailing out to see or coming back to port
    private boolean hasBeenRandomized; //has this PortAnimationData object had it's position randomized yet?

    //Constructors
    /**
     * Default constructor
     */
    public PortAnimationData() {
        posTimeShip = 100;
        lastPosStart = 0; //The last pos has never (yet) been displayed so set it to 0
        movePosIncrement = 0.5;
        incrementCyclesPassed = 0;
        shipAnimationX = 0;
        shipAnimationY = 0;
        outToSea = true;
        hasBeenRandomized = false;
    }

    //Accessors and Mutators
    /**
     * Get the posTimeShip
     *
     * @return
     */
    public int getPosTimeShip() {
        return posTimeShip;
    }

    /**
     * Set the posTimeShip
     *
     * @param posTimeShip
     */
    public void setPosTimeShip(int posTimeShip) {
        this.posTimeShip = posTimeShip;
    }

    /**
     * Get the lastPosStart
     *
     * @return
     */
    public long getLastPosStart() {
        return lastPosStart;
    }

    /**
     * Set the lastPosStart
     *
     * @param lastPosStart
     */
    public void setLastPosStart(long lastPosStart) {
        this.lastPosStart = lastPosStart;
    }

    /**
     * Get the movePosIncrement
     *
     * @return
     */
    public double getMovePosIncrement() {
        return movePosIncrement;
    }

    /**
     * Set the movePosIncrement
     *
     * @param movePosIncrement
     */
    public void setMovePosIncrement(double movePosIncrement) {
        this.movePosIncrement = movePosIncrement;
    }

    /**
     * Get the incrementCyclesPassed
     *
     * @return
     */
    public int getIncrementCyclesPassed() {
        return incrementCyclesPassed;
    }

    /**
     * Set the incrementCyclesPassed
     *
     * @param incrementCyclesPassed
     */
    public void setIncrementCyclesPassed(int incrementCyclesPassed) {
        this.incrementCyclesPassed = incrementCyclesPassed;
    }

    /**
     * Get the shipAnimationX
     *
     * @return
     */
    public double getShipAnimationX() {
        return shipAnimationX;
    }

    /**
     * Set the shipAnimationX
     *
     * @param shipAnimationX
     */
    public void setShipAnimationX(double shipAnimationX) {
        this.shipAnimationX = shipAnimationX;
    }

    /**
     * Get the shipAnimationY
     *
     * @return
     */
    public double getShipAnimationY() {
        return shipAnimationY;
    }

    /**
     * Set the shipAnimationY
     *
     * @param shipAnimationY
     */
    public void setShipAnimationY(double shipAnimationY) {
        this.shipAnimationY = shipAnimationY;
    }

    /**
     * Is the ship sailing outToSea?
     *
     * @return
     */
    public boolean isOutToSea() {
        return outToSea;
    }

    /**
     * Set whether or not the ship is sailing outToSea
     *
     * @param outToSea
     */
    public void setOutToSea(boolean outToSea) {
        this.outToSea = outToSea;
    }

    /**
     * Has the position of the ship been randomized?
     *
     * @return
     */
    public boolean getHasBeenRandomized() {
        return hasBeenRandomized;
    }

    /**
     * Set whether or not the position of the ship been randomized
     *
     * @param hasBeenRandomized
     */
    public void setHasBeenRandomized(boolean hasBeenRandomized) {
        this.hasBeenRandomized = hasBeenRandomized;
    }
}
