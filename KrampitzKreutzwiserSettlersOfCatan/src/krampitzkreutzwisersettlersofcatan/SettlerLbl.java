/*
 * Lukas Krampitz
 * Jul 4, 2021
 * A modified String that is meant to replace Swing JLabes
 *      Created for allow margin formatting
 */
package krampitzkreutzwisersettlersofcatan;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

/**
 *
 * @author Tacitor
 */
public class SettlerLbl extends WorldObject {

    //attributes
    private String text;
    private java.awt.Font font;
    private java.awt.Color foregroundColour;

    /**
     * Basic Constructor
     */
    public SettlerLbl() {
        text = "";
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
        
        g2d.drawString(text, xPos, yPos);

    }

    @Override
    public WorldObject clone() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
