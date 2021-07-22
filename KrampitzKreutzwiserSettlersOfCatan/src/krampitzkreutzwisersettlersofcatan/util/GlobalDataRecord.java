/*
 * Lukas Krampitz
 * Mar 11, 2021
 * Object that contains all the nessiarry information about any global datums. Can be used for storeing the longest road or largets army
 */
package krampitzkreutzwisersettlersofcatan.util;

/**
 *
 * @author Tacitor
 */
public class GlobalDataRecord {

    //attributes
    private int playerNum; //the id number of the player that owns the longest road
    private int size; //the number of road nodes in the longest road

    /**
     * Blank default constructor.
     */
    public GlobalDataRecord() {
        playerNum = 0; //set the player to the unowned player
        size = 0; //set the road to size 0
    }

    /**
     * Full constructor containing all attributes.
     *
     * @param playerNum
     * @param size
     */
    public GlobalDataRecord(int playerNum, int size) {
        this.playerNum = playerNum;
        this.size = size;
    }

    //Accesors and Mutators
    /**
     * Mutate the id number of the player owning the longest road
     *
     * @param playerNum the player id number
     */
    public void setPlayerNum(int playerNum) {
        this.playerNum = playerNum;
    }

    /**
     * Access the id number of the player owning the longest road
     *
     * @return the player id number
     */
    public int getPlayerNum() {
        return playerNum;
    }

    /**
     * Mutate the stored size of the longest road
     *
     * @param size
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * Access the stored size of the longest road
     *
     * @return the size
     */
    public int getSize() {
        return size;
    }

    /**
     * Create an exact copy of a GlobalDataRecord
     *
     * @return the copy
     */
    @Override
    public GlobalDataRecord clone() {
        return new GlobalDataRecord(playerNum, size);
    }

    /**
     * Check equality of two GlobalDataRecord objects
     *
     * @param other
     * @return the boolean result
     */
    public boolean equals(GlobalDataRecord other) {
        return (other.size == this.size && other.playerNum == this.playerNum);
    }

    /**
     * Create a string representation of the GlobalDataRecord
     *
     * @return the string
     */
    @Override
    public String toString() {
        return "GlobalDataRecord:\n" + 
                "PlayerNum:" + playerNum + 
                "\nSize: " + size;
    }

}
