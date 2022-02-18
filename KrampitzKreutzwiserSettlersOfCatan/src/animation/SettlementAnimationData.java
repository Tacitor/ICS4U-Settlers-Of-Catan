/*
 * Lukas Krampitz
 * Feb 16, 2022
 * A data storing class that holds all the variables for animating NodeSettlements
 * This class is created as a way to save the current animation type data from being 
 * reset after saving and loading an online game when changing turns.
 */
package animation;

import java.util.ArrayList;
import krampitzkreutzwisersettlersofcatan.gui.GamePanel;
import krampitzkreutzwisersettlersofcatan.worldObjects.NodeSettlement;

/**
 *
 * @author Tacitor
 */
public class SettlementAnimationData {

    //Atributes
    //Static
    private static ArrayList<SettlementAnimationData> settlementNodesAnimationData; //the local (to this class) copy of the the NodeSettlements animation data
    //animation related
    private int frameTimeSmoke; //the time in miliseconds each frame for chimney smoke should be displayed for
    private int frameTimeLight; //the time in miliseconds each frame for house lights should be displayed for
    private int frameTimeOffset; //the time in milliseconds the frame times will be offset by 
    private long lastFrameStart; //the system time (in miliseconds) when the previous frame started displaying 
    private int currentFrameIndex; //the index within the image array the 

    //Constructors
    /**
     * Default constructor
     */
    public SettlementAnimationData() {
        frameTimeSmoke = 500; //the default frame time should be 500ms
        frameTimeLight = 2000; //the lights should change state after 2 seconds
        lastFrameStart = 0; //the last frame has never (yet) been displayed so set it to 0
        currentFrameIndex = 0;
    }

    /**
     * Get the time in milliseconds each frame is displayed for
     *
     * @return
     */
    public int getFrameTimeSmoke() {
        return frameTimeSmoke;
    }

    /**
     * Set the time in milliseconds each frame is displayed for
     *
     * @param frameTimeSmoke
     */
    public void setFrameTimeSmoke(int frameTimeSmoke) {
        this.frameTimeSmoke = frameTimeSmoke;
    }

    /**
     * Get the time in milliseconds each frame is displayed for
     *
     * @return
     */
    public int getFrameTimeLight() {
        return frameTimeLight;
    }

    /**
     * Set the time in milliseconds each frame is displayed for
     *
     * @param frameTimeLight
     */
    public void setFrameTimeLight(int frameTimeLight) {
        this.frameTimeLight = frameTimeLight;
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
     * Settlements.
     *
     * @param gamePanel
     */
    public static void saveNodeSettlmentAnimationData(GamePanel gamePanel) {
        //Store the ArrayList of settlements
        ArrayList<NodeSettlement> settlementNodes = gamePanel.getSettlementNodes();

        //Init the array list
        settlementNodesAnimationData = new ArrayList<>();

        //go through and add all the animation data from the settlements
        for (NodeSettlement node : settlementNodes) {
            settlementNodesAnimationData.add(node.getSettlementAnimationData().clone());
        }
        
        //System.out.println("Given1: " + gamePanel.getSettlementNodes().get(2).getSettlementAnimationData().toString());
        //System.out.println("Local: " + settlementNodesAnimationData.get(2).toString());

    }

    /**
     * Restore the animation data into the NodeSettlements from the gamePanel
     *
     * @param gamePanel
     */
    public static void restoreNodeSettlmentAnimationData(GamePanel gamePanel) {
        //Store the ArrayList of settlements
        ArrayList<NodeSettlement> settlementNodes = gamePanel.getSettlementNodes();

        //System.out.println("\nBefore: " + settlementNodes.get(2).getSettlementAnimationData().toString());

        //go through and restore all the animation data from the settlements
        for (int i = 0; i < settlementNodes.size(); i++) {
            settlementNodes.get(i).setSettlementAnimationData(settlementNodesAnimationData.get(i));
        }

        //then write the new ArrayList of NodeSettlemtns back to the gamePanel
        gamePanel.setSettlementNodes(settlementNodes);

        //System.out.println("\nAfter: " + settlementNodes.get(2).getSettlementAnimationData().toString());
        //System.out.println("\nGiven: " + settlementNodesAnimationData.get(2).toString() + "\n\n");
    }

    /**
     * Set it to a String
     *
     * @return
     */
    @Override
    public String toString() {
        return "frameTimeSmoke: " + frameTimeSmoke
                + "\nframeTimeLight: " + frameTimeLight
                + "\nframeTimeOffset: " + frameTimeOffset
                + "\nlastFrameStart: " + lastFrameStart
                + "\ncurrentFrameIndex: " + currentFrameIndex;
    }
    
    /**
     * Return an exact copy
     * @return 
     */
    @Override
    public SettlementAnimationData clone() {
        //create a new Object
        SettlementAnimationData copy = new SettlementAnimationData();
        
        copy.frameTimeSmoke = frameTimeSmoke;
        copy.frameTimeLight = frameTimeLight;
        copy.frameTimeOffset = frameTimeOffset;
        copy.lastFrameStart = lastFrameStart;
        copy.currentFrameIndex = currentFrameIndex;
        
        return copy;
    }

}
