/*
 * Lukas Krampitz
 * Mar 19, 2022
 * Display and calculate the information required to display the dice.
 */
package animation;

/**
 *
 * @author Tacitor
 */
public class Dice {

    //Attributes
    private String[] diceRollVal;

    //Constrcutors
    /**
     * Main blank constructor
     */
    public Dice() {
        diceRollVal = new String[]{"0", "0", ""}; //the first two indexies are the rollecd values and the third is the sum
    }

    //Accessors and Mutators
    /**
     * Get the whole array of Strings for the dice roll
     *
     * @return
     */
    public String[] getDiceRollVal() {
        return diceRollVal;
    }

    /**
     * Set the whole array of Strings for the dice roll
     *
     * @param newArray
     */
    public void setDiceRollVall(String[] newArray) {
        diceRollVal = new String[newArray.length];
        diceRollVal = newArray;
    }

    /**
     * Get a specific value of the dice roll
     *
     * @param index
     * @return
     */
    public String getDiceRollVal(int index) {
        return diceRollVal[index];
    }

    /**
     * Set a specific value of the dice roll
     *
     * @param index
     * @param value
     */
    public void setDiceRollVal(int index, String value) {
        diceRollVal[index] = value;
    }

}
