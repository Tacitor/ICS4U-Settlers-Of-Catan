/*
 * Lukas Krampitz
 * August 4, 2024
 * The JPanel for the Join Lobby panel using Settler Dev Buttons.
 */
package krampitzkreutzwisersettlersofcatan.gui;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import javax.swing.JOptionPane;
import krampitzkreutzwisersettlersofcatan.sockets.CatanClient;
import krampitzkreutzwisersettlersofcatan.util.GenUtil;
import krampitzkreutzwisersettlersofcatan.worldObjects.buttons.SettlerBtn;
import krampitzkreutzwisersettlersofcatan.worldObjects.buttons.SettlerLbl;
import textures.ImageRef;

/**
 *
 * @author Tacitor
 */
public class SDJoinLobbyPanel extends javax.swing.JPanel implements MouseMotionListener, SDScaleImageResizeable {

    private SDMenuFrame sDMenuFrame;
    private static double localScaleFactor; //The factor to scale this panel by when drawing elemets
    private int mouseMotionPosX; //acording to the MouseMotionListener where is the mouse located
    private int mouseMotionPosY;

    //Settler Compoments
    private SettlerBtn refreshBtn, lobby1Btn, lobby2Btn, lobby3Btn, lobby4Btn, exitBtn;
    //Settler Labels
    private SettlerLbl lobby1NameLbl, lobby2NameLbl, lobby3NameLbl, lobby4NameLbl, lobby1StatLbl, lobby2StatLbl, lobby3StatLbl, lobby4StatLbl;
    //The array for the buttons
    private SettlerBtn[] settlerBtns;
    //The array for the labels
    private SettlerLbl[] settlerLbls;

    private CatanClient catanClient;

    //Fonts
    public Font COMPASS_GOLD;
    public Font COMPASS_GOLD_45;

    /**
     * Main Constructor
     *
     * @param sDFrame
     */
    public SDJoinLobbyPanel(SDMenuFrame sDFrame) {
        sDMenuFrame = sDFrame;

        COMPASS_GOLD = sDMenuFrame.setUpCompassGoldFont();

        //add the mouse motion listener
        addMouseMotionListener(this);

        //add a mouse listener that call the mouse click event handler
        addMouseListener(new MouseAdapter() {
            /**
             * Triggered when the user clicks on the SDCreditsPanel. Calls the
             * menu panel's click event method.
             *
             * @param event
             */
            @Override
            public final void mouseReleased(MouseEvent event) {
                //send the mouse event to the menu panel click handler
                mouseClick(event);
            }
        });

        //setup the buttons        
        exitBtn = new SettlerBtn(true, 0, 23);
        refreshBtn = new SettlerBtn(false, 0, 32);
        lobby1Btn = new SettlerBtn(true, 0, 31);
        lobby2Btn = new SettlerBtn(true, 0, 31);
        lobby3Btn = new SettlerBtn(true, 0, 31);
        lobby4Btn = new SettlerBtn(false, 0, 31);

        //add them to the array
        settlerBtns = new SettlerBtn[]{refreshBtn, lobby1Btn, lobby2Btn, lobby3Btn, lobby4Btn, exitBtn};
        //set up the labels
        lobby1NameLbl = new SettlerLbl("Lobby 1");
        lobby1NameLbl.setForeground(GenUtil.BUTTON_TEXT_BROWN);
        lobby2NameLbl = new SettlerLbl("Lobby 2");
        lobby2NameLbl.setForeground(GenUtil.BUTTON_TEXT_BROWN);
        lobby3NameLbl = new SettlerLbl("Lobby 3");
        lobby3NameLbl.setForeground(GenUtil.BUTTON_TEXT_BROWN);
        lobby4NameLbl = new SettlerLbl("Lobby 4");
        lobby4NameLbl.setForeground(GenUtil.BUTTON_TEXT_BROWN);
        lobby1StatLbl = new SettlerLbl("empty");
        lobby1StatLbl.setForeground(GenUtil.BUTTON_TEXT_BROWN);
        lobby2StatLbl = new SettlerLbl("1/2");
        lobby2StatLbl.setForeground(GenUtil.BUTTON_TEXT_BROWN);
        lobby3StatLbl = new SettlerLbl("empty");
        lobby3StatLbl.setForeground(GenUtil.BUTTON_TEXT_BROWN);
        lobby4StatLbl = new SettlerLbl("4/4");
        lobby4StatLbl.setForeground(GenUtil.BUTTON_TEXT_BROWN);

        //add them to the array
        settlerLbls = new SettlerLbl[]{lobby1NameLbl, lobby2NameLbl, lobby3NameLbl, lobby4NameLbl, lobby1StatLbl, lobby2StatLbl, lobby3StatLbl, lobby4StatLbl};

    }

    /**
     * Override the JPanel default paintComponent() method.
     */
    @Override
    public void paintComponent(Graphics g) {

        //call the paintCompnent from the super class
        super.paintComponent(g);
        //call the custom layer
        draw(g);
    }

    /**
     * Draw the UI for the SDPanel
     *
     * @param g
     */
    private void draw(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;

        //update the scale factor
        localScaleFactor = sDMenuFrame.calcScaleFactor(this);

        //update the button positions
        settlerVarPos(g2d);

        //draw the background image
        g2d.drawImage(ImageRef.WOOD_BACKGROUND,
                0,
                0,
                this.getWidth(),
                this.getHeight(), this);

        g2d.setFont(new Font(COMPASS_GOLD.getName(), Font.PLAIN, localScaleInt(120)));
        g2d.setColor(DomesticTradePanel.BEIGE_COLOUR);

        //Draw the Title
        g2d.drawString("Join Lobby",
                (this.getWidth() / 2) - (g2d.getFontMetrics().stringWidth("Join Lobby") / 2),
                localScaleInt(100));

        g2d.setFont(new Font(COMPASS_GOLD.getName(), Font.PLAIN, localScaleInt(70)));

        //=-=-=-=-=-=-=-=-=-= Draw the Settlerbuttons Bases & Text ONLY =-=-=-=-=-=-=-=-=-=
        for (SettlerBtn btn : settlerBtns) {
            btn.updateButtonImages();
            btn.updateText();

            //draw the base        
            sDMenuFrame.drawSettlerBtn(g2d, btn.getBaseImage(), btn, 0, this);

            //draw the text
            sDMenuFrame.drawSettlerBtn(g2d, btn.getTextImage(), btn, 0, this);
        }

        //=-=-=-=-=-=-=-=-=-= END OF the drawing of Settlerbuttons =-=-=-=-=-=-=-=-=-=
        //go through and draw all the labels
        for (SettlerLbl settlerLbl : settlerLbls) {
            settlerLbl.draw(g2d, localScaleFactor);
        }

        //Now go back and drawn the layers that do over the text
        for (SettlerBtn btn : settlerBtns) {

            //draw the disabled overlay if required
            if (!btn.isEnabled()) {
                sDMenuFrame.drawSettlerBtn(g2d, btn.getDisabledImage(), btn, 0, this);
            }
            //draw the mouseHover overlay if required
            if (btn.isMouseHover()) {
                sDMenuFrame.drawSettlerBtn(g2d, btn.getHoverImage(), btn, 1, this);
            }

        }
    }

    /**
     * Update the positions of the SD Components
     */
    private void settlerVarPos(Graphics2D g2d) {
        int menuPackingHeight = SDMenuFrame.MENU_PACKING_HEIGHT;

        //lable sizes
        COMPASS_GOLD_45 = new Font(COMPASS_GOLD.getName(), Font.BOLD, localScaleInt(45));
        lobby1NameLbl.setFont(COMPASS_GOLD_45);
        lobby2NameLbl.setFont(COMPASS_GOLD_45);
        lobby3NameLbl.setFont(COMPASS_GOLD_45);
        lobby4NameLbl.setFont(COMPASS_GOLD_45);
        lobby1StatLbl.setFont(COMPASS_GOLD_45);
        lobby2StatLbl.setFont(COMPASS_GOLD_45);
        lobby3StatLbl.setFont(COMPASS_GOLD_45);
        lobby4StatLbl.setFont(COMPASS_GOLD_45);

        refreshBtn.setXPos(localScaleInt(100));
        refreshBtn.setYPos(localScaleInt(150));

        lobby1Btn.setXPos(this.getWidth() / 2 - sDMenuFrame.getImgWidthLocal(lobby1Btn.getBaseImage(), this) / 2);
        lobby1Btn.setYPos(refreshBtn.getYPos() + localScaleInt(menuPackingHeight) + sDMenuFrame.getImgHeightLocal(refreshBtn.getBaseImage(), this));

        lobby1NameLbl.setXPos(lobby1Btn.getXPos() + localScaleInt(20));
        lobby1NameLbl.setYPos(lobby1Btn.getYPos() + sDMenuFrame.getImgHeightLocal(lobby1Btn.getBaseImage(), this) * 4 / 6);

        lobby1StatLbl.setXPos(lobby1NameLbl.getXPos() + localScaleInt(450));
        lobby1StatLbl.setYPos(lobby1NameLbl.getYPos());

        lobby2Btn.setXPos(lobby1Btn.getXPos());
        lobby2Btn.setYPos(lobby1Btn.getYPos() + localScaleInt(menuPackingHeight) + sDMenuFrame.getImgHeightLocal(lobby1Btn.getBaseImage(), this));

        lobby2NameLbl.setXPos(lobby2Btn.getXPos() + localScaleInt(20));
        lobby2NameLbl.setYPos(lobby2Btn.getYPos() + sDMenuFrame.getImgHeightLocal(lobby2Btn.getBaseImage(), this) * 4 / 6);

        lobby2StatLbl.setXPos(lobby2NameLbl.getXPos() + localScaleInt(450));
        lobby2StatLbl.setYPos(lobby2NameLbl.getYPos());

        lobby3Btn.setXPos(lobby2Btn.getXPos());
        lobby3Btn.setYPos(lobby2Btn.getYPos() + localScaleInt(menuPackingHeight) + sDMenuFrame.getImgHeightLocal(lobby2Btn.getBaseImage(), this));

        lobby3NameLbl.setXPos(lobby3Btn.getXPos() + localScaleInt(20));
        lobby3NameLbl.setYPos(lobby3Btn.getYPos() + sDMenuFrame.getImgHeightLocal(lobby3Btn.getBaseImage(), this) * 4 / 6);

        lobby3StatLbl.setXPos(lobby3NameLbl.getXPos() + localScaleInt(450));
        lobby3StatLbl.setYPos(lobby3NameLbl.getYPos());

        lobby4Btn.setXPos(lobby3Btn.getXPos());
        lobby4Btn.setYPos(lobby3Btn.getYPos() + localScaleInt(menuPackingHeight) + sDMenuFrame.getImgHeightLocal(lobby3Btn.getBaseImage(), this));

        lobby4NameLbl.setXPos(lobby4Btn.getXPos() + localScaleInt(20));
        lobby4NameLbl.setYPos(lobby4Btn.getYPos() + sDMenuFrame.getImgHeightLocal(lobby4Btn.getBaseImage(), this) * 4 / 6);

        lobby4StatLbl.setXPos(lobby4NameLbl.getXPos() + localScaleInt(450));
        lobby4StatLbl.setYPos(lobby4NameLbl.getYPos());

        exitBtn.setXPos(this.getWidth() / 2 - sDMenuFrame.getImgWidthLocal(exitBtn.getBaseImage(), this) / 2);
        //Line this up with the exit button from the SDMainMenuPanel.java
        exitBtn.setYPos(localScaleInt(250) + ((localScaleInt(SDMenuFrame.MENU_PACKING_HEIGHT) + sDMenuFrame.getImgHeightLocal(exitBtn.getBaseImage(), this)) * 6));

    }

    /**
     * Handles click releases from mouse input.
     *
     * @param evt
     */
    public void mouseClick(MouseEvent evt) {

        //check if the player clicked on one of the SettlerBtns
        //loop through all the custom buttons
        for (SettlerBtn btn : settlerBtns) {
            if (evt.getX() > btn.getXPos()
                    && evt.getY() > btn.getYPos()
                    && evt.getX() < (btn.getXPos() + sDMenuFrame.getImgWidthLocal(btn.getBaseImage(), this))
                    && evt.getY() < (btn.getYPos() + sDMenuFrame.getImgHeightLocal(btn.getBaseImage(), this))
                    && btn.isEnabled()) { //and that it is enabled

                //check the button that was pressed
                if (btn.equals(exitBtn)) { //if it was the exit game button
                    exitBtnActionPerformed();
                } else if (btn.equals(refreshBtn)) {
                    JOptionPane.showMessageDialog(null, "Hi Seb this button doesn't do anything yet.");
                } else if (btn.equals(lobby1Btn)) {
                    lobbyBtnActionPerformed(1);
                } else if (btn.equals(lobby2Btn)) {
                    lobbyBtnActionPerformed(2);
                } else if (btn.equals(lobby3Btn)) {
                    lobbyBtnActionPerformed(3);
                } else if (btn.equals(lobby4Btn)) {
                    lobbyBtnActionPerformed(4);
                }
            }
        }

        //repaint();
    }

    /**
     * Update the positions of the Mouse Pointer
     *
     * @param e
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        //System.out.println("Moved: " + e.getX() + ", an " + e.getY());
        mouseMotionPosX = e.getX();
        mouseMotionPosY = e.getY();
        mouseMoveAction();
    }

    /**
     * Handles mouse pointer movement.
     */
    private void mouseMoveAction() {

        //check if the player moved the mouse over one of the SettlerBtns
        //loop through all the custom buttons
        for (SettlerBtn btn : settlerBtns) {
            if (mouseMotionPosX > btn.getXPos()
                    && mouseMotionPosY > btn.getYPos()
                    && mouseMotionPosX < (btn.getXPos() + sDMenuFrame.getImgWidthLocal(btn.getBaseImage(), this))
                    && mouseMotionPosY < (btn.getYPos() + sDMenuFrame.getImgHeightLocal(btn.getBaseImage(), this))
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
     * Scale a number to match the resolution of the screen
     *
     * @param num
     * @return
     */
    @Override
    public int localScaleInt(int num) {
        return (int) (num / localScaleFactor);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        //System.out.println("Mouse Dragged");
    }

    private void exitBtnActionPerformed() {
        exitBtn.setmouseHover(false);
        sDMenuFrame.switchPanel(this, sDMenuFrame.getSDMainMenuPanel());
    }

    /**
     * Connect to the 1st lobby
     */
    private void lobbyBtnActionPerformed(int lobbyNum) {
        if (lobbyNum == 1) {

            FindServerRunnable findServerRunnable = new FindServerRunnable();
            findServerRunnable.setDaemon(true);
            findServerRunnable.start();
        } else {
            System.out.println("Lobby" + lobbyNum);
        }
    }

    private void findServer() {
        catanClient = new CatanClient(700, 200, "www.lkrampitz.net", sDMenuFrame.getSDMainMenuPanel().getGameFrame(), 25570);

        try {

            //try to connect
            boolean succesfulConnect = catanClient.connectToServer();

            if (succesfulConnect) {
                System.out.println("connected to the Lobby");

                catanClient.setUpGUI();

                //save the client and the max number of players now because the colour request could finish first
                GamePanel.setCatanClient(catanClient);
                GamePanel.setPlayerCount(catanClient.getMaxClients());

                //get the first avaibale colour
                catanClient.requestColour(0);

                while (catanClient.getClientColour() == 0) {
                    try {
                        //while there is no assinged colour do nothing and just wait
                        //System.out.println("coluour still: " + client.getClientColour());
                        Thread.sleep(500);
                    } catch (InterruptedException ex) {
                        System.out.println("Error requesing colour in SDJoinLobbyPanel");
                    }
                }

                // once the client has been set up save it to the game panel
                GamePanel.setOnlineMode(catanClient.getClientColour());

            } else {
                System.out.println("Error connecting to the lobby");
            }
        } catch (Exception ex) {
            System.out.println("Error: " + ex);
        }
    }

    @Override
    public int getLocalImgWidth(Image image) {
        return sDMenuFrame.getImgWidthLocal(image, this);
    }

    @Override
    public int getLocalImgHeight(Image image) {
        return sDMenuFrame.getImgHeightLocal(image, this);
    }

    private class FindServerRunnable extends Thread implements Runnable {

        private boolean stopRequested = false;

        public synchronized void requestStop() {
            stopRequested = true;
        }

        @Override
        public void run() {
            //debug the life of the thread and how long it lives for
            //System.out.println("Started connectio attempt");

            //check if this thread should stop
            while (!stopRequested) {
                //try to connect
                findServer();
                //only run once
                stopRequested = true;
            }

            //System.out.println("done connection attempt");
        }

    }
}
