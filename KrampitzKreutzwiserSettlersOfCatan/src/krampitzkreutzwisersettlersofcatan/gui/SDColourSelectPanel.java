/*
 * Lukas Krampitz
 * August 14, 2024
 * The JPanel for after the Join Lobby panel using Settler Dev Buttons that lets a player select their colour.
 */
package krampitzkreutzwisersettlersofcatan.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import krampitzkreutzwisersettlersofcatan.sockets.CatanClient;
import krampitzkreutzwisersettlersofcatan.worldObjects.buttons.SettlerBtn;
import krampitzkreutzwisersettlersofcatan.worldObjects.buttons.SettlerLbl;
import krampitzkreutzwisersettlersofcatan.worldObjects.buttons.SettlerRadioBtn;
import textures.ImageRef;

/**
 *
 * @author Tacitor
 */
public class SDColourSelectPanel extends javax.swing.JPanel implements MouseMotionListener, SDScaleImageResizeable {

    private SDMenuFrame sDMenuFrame;
    private static double localScaleFactor; //The factor to scale this panel by when drawing elemets
    private int mouseMotionPosX; //acording to the MouseMotionListener where is the mouse located
    private int mouseMotionPosY;

    //Settler Compoments
    private SettlerBtn colourRequestBtn, exitBtn;
    //Settler Labels
    private SettlerLbl instructionLbl;
    //The array for the buttons
    private SettlerBtn[] settlerBtns;
    //The array for the labels
    //NOTE: Assume the lobbyStatLbls are in the same order as the lobby buttons are in settlerBtns. Also assume that all the stat lables are in the second half of lables
    private SettlerLbl[] settlerLbls;
    //Settler Radio Buttons
    private SettlerRadioBtn colourSelectRedRBtn, colourSelectBlueRBtn, colourSelectOrangeRBtn, colourSelectWhiteRBtn;
    //arry for each group of radio buttons
    private SettlerRadioBtn[] settlerRadioColourSelectBtns;
    //main array for all the radio buttons groups
    private SettlerRadioBtn[][] settlerRadioBtnGroups;

    private CatanClient catanClient;
    private boolean justMadeNewGame;
    private String lobbyIP;
    private int lobbyPort;

    //Fonts
    public Font COMPASS_GOLD;

    /**
     * Main Constructor
     *
     * @param sDFrame
     */
    public SDColourSelectPanel(SDMenuFrame sDFrame) {
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
        colourRequestBtn = new SettlerBtn(true, 0, 27);

        //add them to the array
        settlerBtns = new SettlerBtn[]{colourRequestBtn, exitBtn};
        //set up the labels
        instructionLbl = new SettlerLbl("Please select a colour. The game will not start until all players have done so.");
        instructionLbl.setForeground(new Color(255, 175, 175));

        //add them to the array
        //NOTE: Assume the lobbyStatLbls are in the same order as the lobby buttons are in settlerBtns. Also assume that all the stat lables are in the second half of lables
        settlerLbls = new SettlerLbl[]{instructionLbl};

        //setup the radio buttons        
        colourSelectRedRBtn = new SettlerRadioBtn(true, true, 18);
        colourSelectBlueRBtn = new SettlerRadioBtn(true, false, 19);
        colourSelectOrangeRBtn = new SettlerRadioBtn(true, false, 20);
        colourSelectWhiteRBtn = new SettlerRadioBtn(true, false, 21);

        //add them to the group array
        settlerRadioColourSelectBtns = new SettlerRadioBtn[]{colourSelectRedRBtn, colourSelectBlueRBtn, colourSelectOrangeRBtn, colourSelectWhiteRBtn};

        //add the group to the main array
        settlerRadioBtnGroups = new SettlerRadioBtn[1][];
        settlerRadioBtnGroups[0] = settlerRadioColourSelectBtns;

        //setup the custom radio buttons to go into the groups
        for (SettlerRadioBtn[] grp : settlerRadioBtnGroups) {
            SettlerRadioBtn.setUpGroup(grp);
        }

        justMadeNewGame = false;

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
        g2d.drawString("Pick a Colour",
                (this.getWidth() / 2) - (g2d.getFontMetrics().stringWidth("Pick a Colour") / 2),
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
        //draw the radio buttons
        //itterate over the groups
        for (SettlerRadioBtn[] settlerRadioBtnGroup : settlerRadioBtnGroups) {
            for (SettlerRadioBtn settlerRadioBtn : settlerRadioBtnGroup) {
                settlerRadioBtn.draw(g2d, this);
            }
        }

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
        instructionLbl.setFont(new Font(COMPASS_GOLD.getName(), Font.PLAIN, localScaleInt(50)));

        instructionLbl.setXPos(localScaleInt(100));
        instructionLbl.setYPos(localScaleInt(240));

        colourSelectRedRBtn.setXPos(instructionLbl.getXPos());
        colourSelectRedRBtn.setYPos(instructionLbl.getYPos() + localScaleInt(15));

        colourSelectBlueRBtn.setXPos(colourSelectRedRBtn.getXPos() + getLocalImgWidth(colourSelectRedRBtn.getBaseImage()) + localScaleInt(6));
        colourSelectBlueRBtn.setYPos(colourSelectRedRBtn.getYPos());

        colourSelectOrangeRBtn.setXPos(colourSelectBlueRBtn.getXPos() + getLocalImgWidth(colourSelectBlueRBtn.getBaseImage()) + localScaleInt(6));
        colourSelectOrangeRBtn.setYPos(colourSelectBlueRBtn.getYPos());

        colourSelectWhiteRBtn.setXPos(colourSelectOrangeRBtn.getXPos() + getLocalImgWidth(colourSelectOrangeRBtn.getBaseImage()) + localScaleInt(6));
        colourSelectWhiteRBtn.setYPos(colourSelectOrangeRBtn.getYPos());

        exitBtn.setXPos(this.getWidth() / 2 - getLocalImgWidth(exitBtn.getBaseImage()) / 2);
        //Line this up with the exit button from the SDMainMenuPanel.java
        exitBtn.setYPos(localScaleInt(250) + ((localScaleInt(SDMenuFrame.MENU_PACKING_HEIGHT) + getLocalImgHeight(exitBtn.getBaseImage())) * 6));

        colourRequestBtn.setXPos(exitBtn.getXPos());
        colourRequestBtn.setYPos(exitBtn.getYPos() + getLocalImgHeight(exitBtn.getBaseImage()) + localScaleInt(menuPackingHeight));

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
                    && evt.getX() < (btn.getXPos() + getLocalImgWidth(btn.getBaseImage()))
                    && evt.getY() < (btn.getYPos() + getLocalImgHeight(btn.getBaseImage()))
                    && btn.isEnabled()) { //and that it is enabled

                //check the button that was pressed
                if (btn.equals(exitBtn)) { //if it was the exit game button
                    exitBtnActionPerformed();
                } else if (btn.equals(colourRequestBtn)) { //if it was the colourRequestBtn button
                    colourRequestBtnActionPerformed();
                }
            }
        }

        //check if the player clicked on one of the SettlerRadionBtns
        for (SettlerRadioBtn[] rbtnGroup : settlerRadioBtnGroups) {
            for (SettlerRadioBtn radioBtn : rbtnGroup) {
                if (evt.getX() > radioBtn.getXPos()
                        && evt.getY() > radioBtn.getYPos()
                        && evt.getX() < (radioBtn.getXPos() + getLocalImgWidth(radioBtn.getBaseImage()))
                        && evt.getY() < (radioBtn.getYPos() + getLocalImgHeight(radioBtn.getBaseImage()))
                        && radioBtn.isEnabled()) { //and that it is enabled

                    radioBtn.setSelected(true);
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
                    && mouseMotionPosX < (btn.getXPos() + getLocalImgWidth(btn.getBaseImage()))
                    && mouseMotionPosY < (btn.getYPos() + getLocalImgHeight(btn.getBaseImage()))
                    && btn.isEnabled()) { //and that it is enabled

                //set the hover
                btn.setmouseHover(true);

            } else {

                //make suer there is no hover over that button
                btn.setmouseHover(false);
            }

        }

        //check if the player moved the mouse over one of the SettlerRadionBtns
        for (SettlerRadioBtn[] rbtnGroup : settlerRadioBtnGroups) {
            for (SettlerRadioBtn radioBtn : rbtnGroup) {
                if (mouseMotionPosX > radioBtn.getXPos()
                        && mouseMotionPosY > radioBtn.getYPos()
                        && mouseMotionPosX < (radioBtn.getXPos() + getLocalImgWidth(radioBtn.getBaseImage()))
                        && mouseMotionPosY < (radioBtn.getYPos() + getLocalImgHeight(radioBtn.getBaseImage()))
                        && radioBtn.isEnabled()) { //and that it is enabled

                    //set the hover
                    radioBtn.setmouseHover(true);

                } else {

                    //make suer there is no hover over that button
                    radioBtn.setmouseHover(false);
                }

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
        //reset having just made the game in the case it is still active
        justMadeNewGame = false;

        exitBtn.setmouseHover(false);
        sDMenuFrame.switchPanel(this, sDMenuFrame.getSDMainMenuPanel());
    }

    private void colourRequestBtnActionPerformed() {
        colourRequestBtn.setMode(3); //mode 3 for requesting a colour

        int colourRequest; //store the colour to request

        //get the colour the user wants
        if (colourSelectRedRBtn.isSelected()) {
            colourRequest = 1; //request red
        } else if (colourSelectBlueRBtn.isSelected()) {
            colourRequest = 2; //request blue
        } else if (colourSelectOrangeRBtn.isSelected()) {
            colourRequest = 3; //request orange
        } else if (colourSelectWhiteRBtn.isSelected()) {
            colourRequest = 4; //request white
        } else {
            colourRequest = 0; //default to whatever the server want to give me
        }

        //request the player colour
        catanClient.requestColour(colourRequest); //request any colour

        //System.out.println("coluour: " + client.getClientColour());
        //wait for the response to come through
        while (catanClient.getClientColour() == 0) {
            try {
                //while there is no assinged colour do nothing and just wait
                //System.out.println("coluour still: " + catanClient.getClientColour());
                Thread.sleep(200);
            } catch (InterruptedException ex) {
                System.out.println("Error requesing colour in JoinOnlineGameMenu");
            }
        }

        System.out.println("colourRequest: " + colourRequest);
        System.out.println("catanClient.getClientColour(): " + catanClient.getClientColour() + "\n");

        //if the colour request was successfule tell the game
        if (catanClient.getClientColour() == colourRequest) {

            //once the client has been set up save it to the game panel
            GamePanel.setOnlineMode(catanClient.getClientColour());

            colourRequestBtn.setMode(1);
            colourRequestBtn.setEnabled(false);

            colourSelectRedRBtn.setEnabled(false);
            colourSelectBlueRBtn.setEnabled(false);
            colourSelectOrangeRBtn.setEnabled(false);
            colourSelectWhiteRBtn.setEnabled(false);

        } else { //the the user that it failed
            colourRequestBtn.setMode(2);
        }
    }

    /**
     * Method to trigger the connection process to the lobby.
     */
    public void startFindServer() {
        FindServerRunnable findServerRunnable = new FindServerRunnable();
        findServerRunnable.setDaemon(true);
        findServerRunnable.start();

    }

    private void findServer() {
        catanClient = new CatanClient(700, 200, lobbyIP, sDMenuFrame.getSDMainMenuPanel().getGameFrame(), lobbyPort);

        try {

            //try to connect
            boolean succesfulConnect = catanClient.connectToServer();

            if (succesfulConnect) {
                System.out.println("connected to the Lobby");

                catanClient.setUpGUI();

                //save the client and the max number of players now because the colour request could finish first
                GamePanel.setCatanClient(catanClient);

                //if the player has JUST made a new game they do not need this reset as it WILL otherwise remove the PieceArrays in GenUtil
                if (!justMadeNewGame) {
                    GamePanel.setPlayerCount(catanClient.getMaxClients());
                }

                //now wait and let the player select a colour
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

    /**
     * Accessor for the IP of the lobby
     *
     * @return
     */
    public String getLobbyIP() {
        return lobbyIP;
    }

    /**
     * Mutator for the IP of the lobby
     *
     * @param lobbyIP
     */
    public void setLobbyIP(String lobbyIP) {
        this.lobbyIP = lobbyIP;
    }

    /**
     * Accessor for the Port of the lobby
     *
     * @return
     */
    public int getLobbyPort() {
        return lobbyPort;
    }

    /**
     * Mutator for the Port of the lobby
     *
     * @param lobbyPort
     */
    public void setLobbyPort(int lobbyPort) {
        this.lobbyPort = lobbyPort;
    }

    /**
     * Accessor for justMadeNewGame. Set to true if the player just submitted
     * the settings for a new game.
     *
     * @return
     */
    public boolean isJustMadeNewGame() {
        return justMadeNewGame;
    }

    /**
     * Mutator for justMadeNewGame. Set to true if the player just submitted the
     * settings for a new game.
     *
     * @param justMadeNewGame
     */
    public void setJustMadeNewGame(boolean justMadeNewGame) {
        this.justMadeNewGame = justMadeNewGame;
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
