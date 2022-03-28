/*
 * Lukas Krampitz
 * Mar 25, 2022
 * A JFrame that the user will interact with to trade domestically with other users/players
 */
package krampitzkreutzwisersettlersofcatan.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import javax.swing.JPanel;
import krampitzkreutzwisersettlersofcatan.worldObjects.buttons.SettlerBtn;
import textures.ImageRef;
import static krampitzkreutzwisersettlersofcatan.gui.GamePanel.scaleInt;
import krampitzkreutzwisersettlersofcatan.worldObjects.buttons.SettlerLbl;

/**
 *
 * @author Tacitor
 */
public class DomesticTradePanel extends JPanel implements MouseMotionListener {

    private GameFrame gameFrame;
    private GamePanel theGamePanel;

    private int mouseMotionPosX;
    private int mouseMotionPosY;

    //Settler Components
    private SettlerBtn cancelTradeBtn;
    //The array for the buttons
    private SettlerBtn[] settlerBtns;
    //Settler Lable
    private SettlerLbl titleLbl;
    //array for the labels
    private SettlerLbl[] settlerLbls;

    /**
     * Domestic trade Constructor
     *
     * @param frame
     */
    public DomesticTradePanel(GameFrame frame) {

        gameFrame = frame;
        theGamePanel = gameFrame.getGamePanel();

        initSettlerLbl();

        //add in the motion listener for hovering
        addMouseMotionListener(this);

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

        //setup the buttons
        cancelTradeBtn = new SettlerBtn(true, 1, 9);
        //add then to the array
        settlerBtns = new SettlerBtn[]{cancelTradeBtn};

        //setup the labels
        settlerLbls = new SettlerLbl[]{titleLbl};

    }

    /**
     * Setup the custom Labels
     */
    private void initSettlerLbl() {
        //setup the label and text
        titleLbl = new SettlerLbl("Domestic Trade");

        //setup the colour
        titleLbl.setForeground(new Color(255, 255, 225)); //beige
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

        panelSizeDependantCalculations();

        Graphics2D g2d = (Graphics2D) g;

        g2d.drawImage(ImageRef.WOOD_BACKGROUND,
                0,
                0,
                gameFrame.getWidth(),
                gameFrame.getHeight(), this);

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
        //go through and draw all the labels
        for (SettlerLbl settlerLbl : settlerLbls) {
            settlerLbl.draw(g2d);
        }
    }

    private void settlerVarPos() {
        //Align the components
        //top to bottom
        titleLbl.setXPos(scaleInt(10));
        titleLbl.setYPos(scaleInt(35));

        cancelTradeBtn.setXPos(titleLbl.getXPos());
        cancelTradeBtn.setYPos(gameFrame.getHeight() - theGamePanel.getImgHeight(cancelTradeBtn.getBaseImage()) - scaleInt(6));
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

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseMotionPosX = e.getX();
        mouseMotionPosY = e.getY();
        mouseMoveAction();
    }

    private void mouseMoveAction() {
        //check if the player moved the mouse over one of the SettlerBtns
        //loop through all the custom buttons
        for (SettlerBtn btn : settlerBtns) {
            if (mouseMotionPosX > btn.getXPos()
                    && mouseMotionPosY > btn.getYPos()
                    && mouseMotionPosX < (btn.getXPos() + theGamePanel.getImgWidth(btn.getBaseImage()))
                    && mouseMotionPosY < (btn.getYPos() + theGamePanel.getImgHeight(btn.getBaseImage()))
                    && btn.isEnabled()) { //and that it is enabled

                //set the hover
                btn.setmouseHover(true);

            } else {

                //make suer there is no hover over that button
                btn.setmouseHover(false);
            }

        }

        repaint();
    }

    /**
     * Make calculations and update variables that have to do with scaling and
     * depend on knowing the width or height of the gamePanel
     */
    private void panelSizeDependantCalculations() {
        //Settler Label Font Size
        titleLbl.setFont(new Font(GamePanel.TIMES_NEW_ROMAN.getName(), Font.BOLD, scaleInt(40)));
    }
}
