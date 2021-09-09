/*
 * Lukas Krampitz
 * Jul 4, 2021
 * A modified String that is meant to replace Swing JLabes
 *      Created for allow margin formatting
 */
package krampitzkreutzwisersettlersofcatan.worldObjects.buttons;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import krampitzkreutzwisersettlersofcatan.gui.GamePanel;
import krampitzkreutzwisersettlersofcatan.worldObjects.WorldObject;

/**
 *
 * @author Tacitor
 */
public class SettlerLbl extends WorldObject {

    //attributes
    private String text;
    private java.awt.Font font;
    private java.awt.Color foregroundColour;
    private boolean doLineWrap;
    private int numLines;
    private double spaceForText;
    private int linewrapSpace;

    /**
     * Basic Constructor
     */
    public SettlerLbl() {
        text = "";

        //set the defaults
        doLineWrap = false;
        numLines = 1;
        spaceForText = 100; //default value (probably never used)
        linewrapSpace = 22; //default 
    }

    /**
     * Constructor with starting text
     *
     * @param text
     */
    public SettlerLbl(String text) {
        this();
        this.text = text;
    }

    /**
     * Mutator for text attribute
     *
     * @param newText
     */
    public void setText(String newText) {
        text = newText;
    }

    /**
     * Accessor for text attribute
     *
     * @return
     */
    public String getText() {
        return text;
    }

    /**
     * Mutator for spaceForText
     *
     * @param spaceForText
     */
    public void setSpaceForText(double spaceForText) {
        this.spaceForText = spaceForText;
    }

    /**
     * Accessor for spaceForText
     *
     * @return
     */
    public double getSpaceForText() {
        return spaceForText;
    }

    /**
     * Mutator for text attribute
     *
     * @param newFont
     */
    public void setFont(Font newFont) {
        font = newFont;
    }

    /**
     * Accessor for text attribute
     *
     * @return
     */
    public Font getFont() {
        return font;
    }

    /**
     * Mutator for doLineWrap
     *
     * @param doLineWrap
     */
    public void setLineWrap(boolean doLineWrap) {
        this.doLineWrap = doLineWrap;
    }

    /**
     * Accessor for doLineWrap
     *
     * @return
     */
    public boolean getLineWrap() {
        return doLineWrap;
    }

    /**
     * Accessor for numLines
     *
     * @return
     */
    public int getNumLines() {
        return numLines;
    }

    /**
     * Accessor for linewrapSpace
     *
     * @return
     */
    public int getLinewrapSpace() {
        return linewrapSpace;
    }

    /**
     * Mutator for linewrapSpace
     *
     * @param linewrapSpace
     */
    public void setLinewrapSpace(int linewrapSpace) {
        this.linewrapSpace = linewrapSpace;
    }

    /**
     * Mutator for text attribute
     *
     * @param foregroundColour
     */
    public void setForeground(Color foregroundColour) {
        this.foregroundColour = foregroundColour;
    }

    /**
     * Accessor for text attribute
     *
     * @return
     */
    public Color getForeground() {
        return foregroundColour;
    }

    /**
     * Draw the Label
     *
     * @param g2d
     */
    public void draw(Graphics2D g2d) {

        //g2d.setFont(font);
        g2d.setColor(foregroundColour);
        g2d.setFont(font);

        //special actios for line wrapping
        if (doLineWrap) {

            String text = this.text; //make a copy of the text for cutting and making substrings

            //now draw the text
            int endChar; //the index of the char to end the sub string at
            for (int i = 0; i < numLines; i++) {
                //get the ending
                endChar = getEndingChar(text, (int) spaceForText, g2d);

                //System.out.println(endChar);
                g2d.drawString(text.substring(0, endChar), xPos, yPos + (GamePanel.scaleInt(linewrapSpace) * i));

                //remove the part of the string already displayed so the next line will pick up where the previous left off
                //only if this is not the last operation
                if (i != (numLines - 1)) {
                    text = text.substring(endChar + 1); //remove the space
                }
            }

            //debug how much room there is to work with
            //g2d.drawRect(xPos, yPos, (int)spaceForText, 10);
        } else {

            //just draw the text
            g2d.drawString(text, xPos, yPos);
        }

    }

    /**
     * Preform the calculation needed to predict how many lines a given string
     * will need when displayed as an instruction
     *
     * @param g2d
     * @param gamePanel
     */
    public void calcNumLines(Graphics2D g2d, GamePanel gamePanel) {
        //calculate the number of lines needed
        //spaceForText //the number of pixels there are to work with from edge of the prompt to the edge of the board starts
        //spaceForText = (gamePanel.getSuperFrame().getWidth() / 2 - gamePanel.getImgWidth(WATER_RING) / 2 /*dist from left wall to baord*/) - (xPos);
        // ^^^ this got moved to GamePanel.java because new types of setter labels need line wrap but not to this line

        double lineNum; //the number of lines the the text needs to be displayed over
        g2d.setFont(font);
        lineNum = Math.ceil(g2d.getFontMetrics().stringWidth(text) / spaceForText);
        numLines = (int) lineNum; //update the number of lines

        //debug the number of lines needed
        //System.out.println(lineNum);
    }

    /**
     * Calculate the character a string needs to be cut off at to fit within the
     * given area. Sticks to whole words.
     *
     * @param text
     * @param spaceAvail
     * @param g2d
     * @return
     */
    private int getEndingChar(String text, int spaceAvail, Graphics2D g2d) {

        //check if the string is short enough
        if (g2d.getFontMetrics().stringWidth(text) < spaceAvail) {
            return text.length();
        } else { //remove the last word and try again
            int lastSpace = text.lastIndexOf(" ");

            text = text.substring(0, lastSpace);

            return getEndingChar(text, spaceAvail, g2d);
        }

    }

    @Override
    public WorldObject clone() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
