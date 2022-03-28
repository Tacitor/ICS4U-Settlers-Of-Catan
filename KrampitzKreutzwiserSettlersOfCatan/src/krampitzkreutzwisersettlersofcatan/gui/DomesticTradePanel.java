/*
 * Lukas Krampitz
 * Mar 25, 2022
 * A JFrame that the user will interact with to trade domestically with other users/players
 */
package krampitzkreutzwisersettlersofcatan.gui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JPanel;
import krampitzkreutzwisersettlersofcatan.worldObjects.buttons.SettlerBtn;
import textures.ImageRef;

/**
 *
 * @author Tacitor
 */
public class DomesticTradePanel extends JPanel {

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

        //add a mouse listener that call the mouse click even handler
        addMouseListener(new MouseAdapter() {
            /**
             * Triggered when the user clicks on the trade panel. Calls the
             * trade panel's click event method.
             *
             * @param event
             */
            @Override
            public final void mouseReleased(MouseEvent event) {
                //send the mouse event to the trade panel click handler
                tradeMouseClicked(event);
            }
        });

        cancelTradeBtn = new SettlerBtn(true, 1, 9);
        settlerBtns = new SettlerBtn[]{cancelTradeBtn};

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
        //update the gamePanel ref
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

            //draw the text
            theGamePanel.drawSettlerBtn(g2d, btn.getTextImage(), btn, 0);

            //draw the disabled overlay if required
            if (!btn.isEnabled()) {
                theGamePanel.drawSettlerBtn(g2d, btn.getDisabledImage(), btn, 0);
            }
            //draw the mouseHover overlay if required
            if (btn.isMouseHover()) {
                theGamePanel.drawSettlerBtn(g2d, btn.getHoverImage(), btn, 1);
            }
        }

        //=-=-=-=-=-=-=-=-=-= End the drawing of Settlerbuttons =-=-=-=-=-=-=-=-=-=
    }

    private void settlerVarPos() {
        cancelTradeBtn.setXPos(200);
        cancelTradeBtn.setYPos(200);
    }

    private void cancelTradeBtnPressed() {
        gameFrame.switchToTrade(false);
    }

    /**
     * Actions to take when the user clicks on the trade panel
     *
     * @param evt
     */
    public void tradeMouseClicked(MouseEvent evt) {
        //Loop through all the Buttons
        for (SettlerBtn btn : settlerBtns) {
            if (evt.getX() > btn.getXPos()
                    && evt.getY() > btn.getYPos()
                    && evt.getX() < (btn.getXPos() + theGamePanel.getImgWidth(btn.getBaseImage()))
                    && evt.getY() < (btn.getYPos() + theGamePanel.getImgHeight(btn.getBaseImage()))
                    && btn.isEnabled()) { //and that it is enabled
                
                //Check what button was pressed
                if (btn.equals(cancelTradeBtn)) {
                    //if it's the cancel button
                    //close the trade menu
                    cancelTradeBtnPressed();
                }
            }
        }
    }
}
