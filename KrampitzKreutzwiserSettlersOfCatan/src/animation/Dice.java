/*
 * Lukas Krampitz
 * Mar 19, 2022
 * Display and calculate the information required to display the dice.
 */
package animation;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import javax.swing.ImageIcon;
import krampitzkreutzwisersettlersofcatan.gui.GamePanel;
import static krampitzkreutzwisersettlersofcatan.gui.GamePanel.scaleFactor;
import textures.ImageRef;

/**
 *
 * @author Tacitor
 */
public class Dice {

    //Attributes
    private String[] diceRollVal;
    private int rollFrame; //The index of the frame of animation currently displaying
    private int frameTime; //the time in miliseconds each frame should be displayed for.
    private long lastRollTime; //the time in miliseconds of what the system time was when the frame was last updated
    private boolean justRolled; //whether or not the dice have just been rolled and need to be animated.

    //=-=-=-=-=-=-=-=-=-= image vars =-=-=-=-=-=-=-=-=-=
    //dice images
    private final static Image DIE_IMAGE_1 = new ImageIcon(ImageRef.class.getResource("dice/die1.png")).getImage();
    private final static Image DIE_IMAGE_2 = new ImageIcon(ImageRef.class.getResource("dice/die2.png")).getImage();
    private final static Image DIE_IMAGE_3 = new ImageIcon(ImageRef.class.getResource("dice/die3.png")).getImage();
    private final static Image DIE_IMAGE_4 = new ImageIcon(ImageRef.class.getResource("dice/die4.png")).getImage();
    private final static Image DIE_IMAGE_5 = new ImageIcon(ImageRef.class.getResource("dice/die5.png")).getImage();
    private final static Image DIE_IMAGE_6 = new ImageIcon(ImageRef.class.getResource("dice/die6.png")).getImage();
    private final static Image DICE_GRAY = new ImageIcon(ImageRef.class.getResource("dice/diceGray.png")).getImage();

    private final static Image[] DICE_IMAGES = new Image[]{
        DICE_GRAY, DIE_IMAGE_1, DIE_IMAGE_2, DIE_IMAGE_3, DIE_IMAGE_4, DIE_IMAGE_5, DIE_IMAGE_6};

    //animation frames
    private final static Image DICE_ROLL_0 = new ImageIcon(ImageRef.class.getResource("dice/diceRoll0.png")).getImage();
    private final static Image DICE_ROLL_1 = new ImageIcon(ImageRef.class.getResource("dice/diceRoll1.png")).getImage();
    private final static Image DICE_ROLL_2 = new ImageIcon(ImageRef.class.getResource("dice/diceRoll2.png")).getImage();
    private final static Image DICE_ROLL_3 = new ImageIcon(ImageRef.class.getResource("dice/diceRoll3.png")).getImage();
    private final static Image DICE_ROLL_4 = new ImageIcon(ImageRef.class.getResource("dice/diceRoll4.png")).getImage();

    private final static Image[] DICE_ROLL_IMAGES = new Image[]{
        DICE_ROLL_0, DICE_ROLL_1, DICE_ROLL_2, DICE_ROLL_3, DICE_ROLL_4};

    //=-=-=-=-=-=-=-=-=-= end of image vars =-=-=-=-=-=-=-=-=-=
    //Constrcutors
    /**
     * Main blank constructor
     */
    public Dice() {
        diceRollVal = new String[]{"0", "0", ""}; //the first two indexies are the rollecd values and the third is the sum

        //init the animation values
        rollFrame = 0; //start on frame 0
        frameTime = 500; //the time each frame will be displayed for
        lastRollTime = 0; //Never been rolled before
        justRolled = false;
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

    /**
     * Get a specific image of a die or dice. Accepts 7 indices: 0, 1, 2, 3, 4,
     * 5, 6. Index 0 if the grayed out dice. 1-6 are images of a single die
     * rolled to the number of the index.
     *
     * @param index
     * @return
     */
    public static Image getDiceImage(int index) {
        return DICE_IMAGES[index];
    }

    /**
     * Get whether or not the dice have just been rolled and need to be animated
     *
     * @return
     */
    public boolean getJustRolled() {
        return justRolled;
    }

    /**
     * Set whether or not the dice have just been rolled and need to be animated
     *
     * @param justRolled
     */
    public void setJustRolled(boolean justRolled) {
        this.justRolled = justRolled;
    }

    public void draw(Graphics2D g2d, int rightDrawMargin, boolean inSetup, GamePanel gamePanel) {
        //set the font for the dice roll indecator
        g2d.setFont(new Font("Times New Roman", Font.PLAIN, (int) (20 / scaleFactor)));
        g2d.setColor(new java.awt.Color(255, 255, 225));
        //show what number the user rolled
        g2d.drawString("You rolled a: " + diceRollVal[2],
                rightDrawMargin,
                (int) (440 / scaleFactor));
        //draw the dice
        //but only if not in setup
        if (!inSetup) {

            //Now check to see if the dice have just been rolled.
            //If yes run through the animation
            if (justRolled) {

                long sysTime = System.currentTimeMillis(); //The current system time

                g2d.drawImage(DICE_ROLL_IMAGES[rollFrame],
                        rightDrawMargin,
                        (int) (435 / scaleFactor),
                        (int) (gamePanel.getImgWidth(DICE_IMAGES[0]) * 1.5),
                        (int) (gamePanel.getImgHeight(DICE_IMAGES[0]) * 1.5),
                        null);

                //check to see if the frame needs to be updated
                if (sysTime - lastRollTime > frameTime) {

                    if (rollFrame == 0 && lastRollTime == 0) {
                        lastRollTime = sysTime;
                    } else {

                        //increment roll frame
                        rollFrame++;

                        //save the time when the frame was updated
                        lastRollTime = sysTime;

                        //check for overflow
                        if (rollFrame == DICE_ROLL_IMAGES.length) {
                            rollFrame = 0;

                            //if we are rolling over also stop the animation
                            justRolled = false;

                            //and reset the clock
                            lastRollTime = 0;
                        }

                    }
                }

            } else {

                //draw the non rolled dice if there is no roll
                if (diceRollVal[2].equals("")) {

                    g2d.drawImage(DICE_IMAGES[0],
                            rightDrawMargin,
                            (int) (435 / scaleFactor),
                            (int) (gamePanel.getImgWidth(DICE_IMAGES[0]) * 1.5),
                            (int) (gamePanel.getImgHeight(DICE_IMAGES[0]) * 1.5),
                            null);
                } else { //else draw the dice that go with the roll
                    g2d.drawImage(DICE_IMAGES[Integer.parseInt(diceRollVal[0])],
                            rightDrawMargin,
                            (int) (435 / scaleFactor),
                            (int) (gamePanel.getImgWidth(DICE_IMAGES[1]) * 1.5),
                            (int) (gamePanel.getImgHeight(DICE_IMAGES[1]) * 1.5),
                            null);

                    g2d.drawImage(DICE_IMAGES[Integer.parseInt(diceRollVal[1])],
                            rightDrawMargin + (int) (gamePanel.getImgWidth(DICE_IMAGES[1]) * 1.5),
                            (int) (435 / scaleFactor),
                            (int) (gamePanel.getImgWidth(DICE_IMAGES[1]) * 1.5),
                            (int) (gamePanel.getImgHeight(DICE_IMAGES[1]) * 1.5),
                            null);

                }
            }
        }
    }

}
