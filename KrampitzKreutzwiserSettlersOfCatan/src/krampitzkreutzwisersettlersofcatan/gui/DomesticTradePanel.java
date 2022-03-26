/*
 * Lukas Krampitz
 * Mar 25, 2022
 * A JFrame that the user will interact with to trade domestically with other users/players
 */
package krampitzkreutzwisersettlersofcatan.gui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

/**
 *
 * @author Tacitor
 */
public class DomesticTradePanel extends JPanel {

    /**
     * Domestic trade Constructor
     */
    public DomesticTradePanel() {

    }

    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        draw(g);

    }

    /**
     * Draw the trade UI
     *
     * @param g
     */
    private void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        
        g2d.drawString("Java 2D", 50, 50);
    }

}
