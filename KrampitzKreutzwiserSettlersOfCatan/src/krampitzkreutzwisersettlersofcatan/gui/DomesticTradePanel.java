/*
 * Lukas Krampitz
 * Mar 25, 2022
 * A JFrame that the user will interact with to trade domestically with other users/players
 */
package krampitzkreutzwisersettlersofcatan.gui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JPanel;
import krampitzkreutzwisersettlersofcatan.worldObjects.buttons.SettlerBtn;
import textures.ImageRef;

/**
 *
 * @author Tacitor
 */
public class DomesticTradePanel extends JPanel {

    private JButton testBtn;
    private GameFrame gameFrame;
    private GamePanel theGamePanel;

    //Settler Components
    private SettlerBtn cancelTradeBtn;
    //The array for the buttons
    private SettlerBtn[] settlerBtns;

    /**
     * Domestic trade Constructor
     *
     * @param frame
     */
    public DomesticTradePanel(GameFrame frame) {

        gameFrame = frame;
        theGamePanel = gameFrame.getGamePanel();

        cancelTradeBtn = new SettlerBtn(true, 1, 9);
        settlerBtns = new SettlerBtn[]{cancelTradeBtn};

    }

    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        initComponent();
        draw(g);

    }

    /**
     * Draw the trade UI
     *
     * @param g
     */
    private void draw(Graphics g) {
        //update the gamePnael ref
        theGamePanel = gameFrame.getGamePanel();
        
        Graphics2D g2d = (Graphics2D) g;

        g2d.drawImage(ImageRef.WOOD_BACKGROUND,
                0,
                0,
                gameFrame.getWidth(),
                gameFrame.getHeight(), this);

        g2d.drawString("Java 2D", 50, 50);

        //=-=-=-=-=-=-=-=-=-= Draw the Settlerbuttons =-=-=-=-=-=-=-=-=-=
        settlerVarPos(); //update the positions

        for (SettlerBtn btn : settlerBtns) {
            btn.updateButtonImages();
            btn.updateText();
            
            //draw the base        
            theGamePanel.drawSettlerBtn(g2d, btn.getBaseImage(), btn, 0);
            /*
            //draw the text
            theGamePanel.drawSettlerBtn(g2d, btn.getTextImage(), btn, 0);

            //draw the disabled overlay if required
            if (!btn.isEnabled()) {
                theGamePanel.drawSettlerBtn(g2d, btn.getDisabledImage(), btn, 0);
            }
            //draw the mouseHover overlay if required
            if (btn.isMouseHover()) {
                theGamePanel.drawSettlerBtn(g2d, btn.getHoverImage(), btn, 1);
            }*/
        }

        //=-=-=-=-=-=-=-=-=-= End the drawing of Settlerbuttons =-=-=-=-=-=-=-=-=-=
    }

    private void settlerVarPos() {
        cancelTradeBtn.setXPos(200);
        cancelTradeBtn.setYPos(200);
    }

    private void initComponent() {
        testBtn = new javax.swing.JButton();

        testBtn.setText("< Back");
        testBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                testBtnPressed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(testBtn)
                                                .addGap(0, 0, Short.MAX_VALUE)))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(testBtn)
                                .addContainerGap())
        );

    }

    private void testBtnPressed(ActionEvent ent) {
        gameFrame.switchToTrade(false);
    }

}
