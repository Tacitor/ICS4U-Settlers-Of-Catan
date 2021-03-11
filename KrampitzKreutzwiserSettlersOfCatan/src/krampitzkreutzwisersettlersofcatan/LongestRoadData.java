/*
 * Lukas Krampitz
 * Mar 11, 2021
 * Object that contains all the nessiarry information about the logest road in a game.
 */
package krampitzkreutzwisersettlersofcatan;

/**
 *
 * @author Tacitor
 */
public class LongestRoadData {

    //attributes
    private int playerNum; //the id number of the player that owns the longest road
    private int length; //the number of road nodes in the longest road

    /**
     * Blank default constructor.
     */
    public LongestRoadData() {
        playerNum = 0; //set the player to the unowned player
        length = 0; //set the road to length 0
    }

    /**
     * Full constructor containing all attributes.
     *
     * @param playerNum
     * @param length
     */
    public LongestRoadData(int playerNum, int length) {
        this.playerNum = playerNum;
        this.length = length;
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
     * Mutate the stored length of the longest road
     *
     * @param length the length
     */
    public void setLength(int length) {
        this.length = length;
    }

    /**
     * Access the stored length of the longest road
     *
     * @return the length
     */
    public int getLength() {
        return length;
    }

    /**
     * Create an exact copy of a LongestRoadData
     *
     * @return the copy
     */
    @Override
    public LongestRoadData clone() {
        return new LongestRoadData(playerNum, length);
    }

    /**
     * Check equality of two LongestRoadData objects
     *
     * @param other
     * @return the boolean result
     */
    public boolean equals(LongestRoadData other) {
        return (other.length == this.length && other.playerNum == this.playerNum);
    }

    /**
     * Create a string representation of the LongestRoadData
     *
     * @return the string
     */
    @Override
    public String toString() {
        return "LongestRoadData:\n" + 
                "PlayerNum:" + playerNum + 
                "\nLength: " + length;
    }

}
