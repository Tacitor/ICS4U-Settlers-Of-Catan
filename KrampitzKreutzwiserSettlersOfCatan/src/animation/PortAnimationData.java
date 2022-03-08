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
    private long lastPosStart;
    private int movePosIncrement; //the amount of pixels the ship will move by every pos update
    private int incrementCyclesPassed; //The number of times the ships's pos has been moved. Gets reset to 0 when the full animation cycle resets.

    //Constructors
    /**
     * Default constructor
     */
    public PortAnimationData() {
        posTimeShip = 500;
        lastPosStart = 0; //The last pos has never (yet) been displayed so set it to 0
        movePosIncrement = 10;
        incrementCyclesPassed = 0;
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
    public int getMovePosIncrement() {
        return movePosIncrement;
    }

    /**
     * Set the movePosIncrement
     *
     * @param movePosIncrement
     */
    public void setMovePosIncrement(int movePosIncrement) {
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
}
