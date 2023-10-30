/*
 * Lukas Krampitz
 * Oct 8, 2023
 * A data storing class that holds all the variables for animating Text Boxes and their cursors
 * Not nesssisary for text boxes, but This class architecture is created as a way to save the current animation type data from being 
 * reset after saving and loading an online game when changing turns.
 * Since mainly just for a blinking text cursor it won't get saved or loaded really
 */
package animation;

/**
 *
 * @author Tacitor
 */
public class SettlerBtnAnimationData {

    //Atributes
    //animation related
    private int frameTime; //the time in miliseconds each frame of the cursor blink sequence should be displayed for
    private long lastFrameStart; //the system time (in miliseconds) when the previous frame started displaying 
    private int currentFrameIndex; //the index within the image array the 

    //Constructors
    /**
     * Default constructor
     */
    public SettlerBtnAnimationData() {
        frameTime = 200; //the default frame time should be 0.2 seconds
        lastFrameStart = 0; //the last frame has never (yet) been displayed so set it to 0
        currentFrameIndex = 0;
    }

    /**
     * Get the time in milliseconds each frame is displayed for
     *
     * @return
     */
    public int getFrameTime() {
        return frameTime;
    }

    /**
     * Set the time in milliseconds each frame is displayed for
     *
     * @param frameTime
     */
    public void setFrameTime(int frameTime) {
        this.frameTime = frameTime;
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
     * Set it to a String
     *
     * @return
     */
    @Override
    public String toString() {
        return "frameTime: " + frameTime
                + "\nlastFrameStart: " + lastFrameStart
                + "\ncurrentFrameIndex: " + currentFrameIndex;
    }

    /**
     * Return an exact copy
     *
     * @return
     */
    @Override
    public SettlerBtnAnimationData clone() {
        //create a new Object
        SettlerBtnAnimationData copy = new SettlerBtnAnimationData();

        copy.frameTime = frameTime;
        copy.lastFrameStart = lastFrameStart;
        copy.currentFrameIndex = currentFrameIndex;

        return copy;
    }

}
