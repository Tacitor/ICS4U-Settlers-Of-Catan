/*
 * Lukas Krampitz
 * Sep 2, 2023
 * A custom text box the fits the test of the Settlers of Catan Theme
 */
package krampitzkreutzwisersettlersofcatan.worldObjects.buttons;

import krampitzkreutzwisersettlersofcatan.worldObjects.WorldObject;

/**
 *
 * @author Tacitor
 */
public class SettlerTxtBx extends SettlerComponent {

    //attibutes
    protected int cursorPos; //the position of the cursor. This is where text gets added and removed from
    protected int startDisplayPos; //the position of where to start displaying the text withing the box.
    protected char[] chars; //the char array that will contain the text of the text box.

    @Override
    public void updateButtonImages() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateText() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public WorldObject clone() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
