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
    private boolean useNewLineChar; //does the new line character actually make a new line
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
     * Mutator for useNewLineChar
     *
     * @param useNewLineChar
     */
    public void setUseNewLineChar(boolean useNewLineChar) {
        this.useNewLineChar = useNewLineChar;
    }

    /**
     * Accessor for useNewLineChar
     *
     * @return
     */
    public boolean getUseNewLineChar() {
        return useNewLineChar;
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
     * @param scaleFactor
     */
    public void draw(Graphics2D g2d, double scaleFactor) {

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
                g2d.drawString(text.substring(0, endChar), xPos, yPos + (((int) (linewrapSpace / scaleFactor)) * i));

                //remove the part of the string already displayed so the next line will pick up where the previous left off
                //only if this is not the last operation
                if (i != (numLines - 1)) {
                    text = text.substring(endChar + 1); //remove the space
                }
            }

            //debug how much room there is to work with
            //g2d.drawRect(xPos, yPos, (int)spaceForText, 10);
        } else if (useNewLineChar) {
            String text = this.text; //make a copy of the text for cutting and making substrings
            int endChar; //the index of the char to end the sub string at

            for (int i = 0; i < numLines; i++) {
                endChar = text.indexOf(10);

                g2d.drawString(text.substring(0, endChar), xPos, yPos + (((int) (linewrapSpace / scaleFactor)) * i));

                //remove the part of the string already displayed so the next line will pick up where the previous left off
                //only if this is not the last operation
                if (i < (numLines - 1)) {
                    text = text.substring(endChar + 2); //remove the space
                }

            }
        } else {

            //just draw the text
            g2d.drawString(text, xPos, yPos);
        }

    }

    /**
     * Calculate the number of Lines a string will take up. Counts the number of
     * new line characters (line feed or carriage return characters).
     */
    public void calcNumLinesCarriageReturn() {
        char textArr[] = text.toCharArray();
        int lineCount = 0;

        //loop through the array
        for (int i = 0; i < textArr.length; i++) {
            //keeps track of a previous char. Check for strange windows double linefeed
            if (textArr[i] == 10 && textArr[i - 1] != 10) {
                lineCount++;
            }
        }

        numLines = lineCount;

    }

    /**
     * Preform the calculation needed to predict how many lines a given string
     * will need when displayed as an instruction
     *
     * @param g2d
     */
    public void calcNumLines(Graphics2D g2d) {
        //calculate the number of lines needed
        //spaceForText //the number of pixels there are to work with from edge of the prompt to the edge of the board starts
        //spaceForText = (gamePanel.getSuperFrame().getWidth() / 2 - gamePanel.getImgWidth(WATER_RING) / 2 /*dist from left wall to baord*/) - (xPos);
        // ^^^ this got moved to GamePanel.java because new types of setter labels need line wrap but not to this line

        int lineNum = 0; //the number of lines the the text needs to be displayed over
        String text = this.text;
        g2d.setFont(font);

        int endChar;

        //itterate through the string to find how many lines are needed
        while (!text.equals("")) {

            //find the ending
            endChar = getEndingChar(text, (int) spaceForText, g2d);

            //if this is the last line
            if (endChar != text.length()) {

                //take off that segment
                text = text.substring(endChar + 1);
            } else { //if there are no spaces left

                text = ""; //take off the last remaining word
            }

            lineNum++;
        }

        numLines = lineNum; //update the number of lines

        //debug the number of lines needed
        //System.out.println("Step 2: " + lineNum + "\n" + this.text + "\n\n");
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
