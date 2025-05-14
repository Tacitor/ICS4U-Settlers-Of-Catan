/*
 * Lukas Krampitz
 * August 4, 2024
 * The JPanel for the Join Lobby panel using Settler Dev Buttons.
 */
package krampitzkreutzwisersettlersofcatan.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import krampitzkreutzwisersettlersofcatan.sockets.CatanClient;
import krampitzkreutzwisersettlersofcatan.util.GenUtil;
import krampitzkreutzwisersettlersofcatan.worldObjects.buttons.SettlerBtn;
import krampitzkreutzwisersettlersofcatan.worldObjects.buttons.SettlerLbl;
import textures.ImageRef;

/**
 *
 * @author Tacitor
 */
public class SDJoinLobbyPanel extends javax.swing.JPanel implements MouseMotionListener, SDScaleImageResizeable, ComponentListener {

    private SDMenuFrame sDMenuFrame;
    private static double localScaleFactor; //The factor to scale this panel by when drawing elemets
    private int mouseMotionPosX; //acording to the MouseMotionListener where is the mouse located
    private int mouseMotionPosY;

    //Settler Compoments
    private SettlerBtn lobby1Btn, lobby2Btn, lobby3Btn, lobby4Btn, exitBtn;
    //Settler Labels
    private SettlerLbl lobby1NameLbl, lobby2NameLbl, lobby3NameLbl, lobby4NameLbl, lobby1StatLbl, lobby2StatLbl, lobby3StatLbl, lobby4StatLbl, instructionLbl;
    //The array for the buttons
    private SettlerBtn[] settlerBtns;
    //The array for the labels
    //NOTE: Assume the lobbyStatLbls are in the same order as the lobby buttons are in settlerBtns. Also assume that all the stat lables are in the second half of lables
    private SettlerLbl[] settlerLbls;

    private CatanClient catanClient;
    private ClientSideConnection csc; //the socket type var to hold the connection to the lobby aggregation server

    //NOTE: Assume the buttons are in the same order in settlerBtns as they are in lobbyStats
    private int[][] lobbyStats;

    private boolean justMadeNewGame;

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

        //Add the component listener to itself to listen for being made visible
        addComponentListener(this);

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
        lobby1Btn = new SettlerBtn(false, 1, 31); //set the mode to 1 for lobby 1,a nd type to 31 for a lobby button
        lobby2Btn = new SettlerBtn(false, 2, 31); //set the mode to 2 for lobby 2,a nd type to 31 for a lobby button
        lobby3Btn = new SettlerBtn(false, 3, 31); //set the mode to 3 for lobby 3,a nd type to 31 for a lobby button
        lobby4Btn = new SettlerBtn(false, 4, 31); //set the mode to 4 for lobby 4,a nd type to 31 for a lobby button

        //add them to the array
        settlerBtns = new SettlerBtn[]{lobby1Btn, lobby2Btn, lobby3Btn, lobby4Btn, exitBtn};
        //set up the labels
        lobby1NameLbl = new SettlerLbl("Lobby 1");
        lobby1NameLbl.setForeground(GenUtil.BUTTON_TEXT_BROWN);
        lobby2NameLbl = new SettlerLbl("Lobby 2");
        lobby2NameLbl.setForeground(GenUtil.BUTTON_TEXT_BROWN);
        lobby3NameLbl = new SettlerLbl("Lobby 3");
        lobby3NameLbl.setForeground(GenUtil.BUTTON_TEXT_BROWN);
        lobby4NameLbl = new SettlerLbl("Lobby 4");
        lobby4NameLbl.setForeground(GenUtil.BUTTON_TEXT_BROWN);
        lobby1StatLbl = new SettlerLbl("N/A");
        lobby1StatLbl.setForeground(GenUtil.BUTTON_TEXT_BROWN);
        lobby2StatLbl = new SettlerLbl("N/A");
        lobby2StatLbl.setForeground(GenUtil.BUTTON_TEXT_BROWN);
        lobby3StatLbl = new SettlerLbl("N/A");
        lobby3StatLbl.setForeground(GenUtil.BUTTON_TEXT_BROWN);
        lobby4StatLbl = new SettlerLbl("N/A");
        lobby4StatLbl.setForeground(GenUtil.BUTTON_TEXT_BROWN);
        instructionLbl = new SettlerLbl("The quick brown fox jumps over the lazy dog");
        instructionLbl.setForeground(new Color(255, 175, 175));
        resetLobbyLabels();

        //add them to the array
        //NOTE: Assume the lobbyStatLbls are in the same order as the lobby buttons are in settlerBtns. Also assume that all the stat lables are in the second half of lables
        settlerLbls = new SettlerLbl[]{lobby1NameLbl, lobby2NameLbl, lobby3NameLbl, lobby4NameLbl, lobby1StatLbl, lobby2StatLbl, lobby3StatLbl, lobby4StatLbl, instructionLbl};

        //init the lobby stats
        /**
         * There are 4 arrays of size 5. Each of the 4 arrays is for 1 of the
         * lobbies. Each lobby had the 0th index for the total number of players
         * allowed. And each index is a binary 0 or 1 for if the corresponding
         * player colour is present
         */
        //NOTE: Assume the buttons are in the same order in settlerBtns as they are in lobbyStats
        lobbyStats = new int[4][5];

        //temp values
        lobbyStats[0][0] = 2;

        lobbyStats[1][0] = 2; //set lobby 2 to have a max of 2 playres
        lobbyStats[1][2] = 1; //set lobby 2 to have the blue player present

        lobbyStats[2][0] = -1; //set lobby 3 to have connection issues to the server

        lobbyStats[3][0] = 4; //set lobby 4 to have a max of 4 playres
        lobbyStats[3][1] = 1; //set lobby 2 to have the red player present
        lobbyStats[3][2] = 1; //set lobby 2 to have the blue player present
        lobbyStats[3][3] = 1; //set lobby 2 to have the orange player present
        lobbyStats[3][4] = 1; //set lobby 2 to have the white player present
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

            //add player dot indicators to show what colours are being used in a given lobby
            if (btn.getType() == 31) {
                for (int i = 1; i < 4 + 1; i++) {

                    //see if this specific player dot should be drawn for a given lobby
                    if (lobbyStats[btn.getMode() - 1][i] == 1) {

                        //draw the player's indecator dot
                        g2d.drawImage(ImageRef.PLAYER_DOTS[i],
                                btn.getXPos() + localScaleInt(250) + localScaleInt(40 * i),
                                btn.getYPos() + (getLocalImgHeight(btn.getBaseImage()) / 8 * 3),
                                getLocalImgWidth(ImageRef.PLAYER_DOTS[i]),
                                getLocalImgHeight(ImageRef.PLAYER_DOTS[i]), null);
                    }
                }
            }

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
        instructionLbl.setFont(new Font(COMPASS_GOLD.getName(), Font.PLAIN, localScaleInt(50)));

        instructionLbl.setXPos(100);
        instructionLbl.setYPos(150);

        lobby1Btn.setXPos(this.getWidth() / 2 - getLocalImgWidth(lobby1Btn.getBaseImage()) / 2);
        lobby1Btn.setYPos(instructionLbl.getYPos() + localScaleInt(menuPackingHeight + 20));

        lobby1NameLbl.setXPos(lobby1Btn.getXPos() + localScaleInt(20));
        lobby1NameLbl.setYPos(lobby1Btn.getYPos() + getLocalImgHeight(lobby1Btn.getBaseImage()) * 4 / 6);

        lobby1StatLbl.setXPos(lobby1NameLbl.getXPos() + localScaleInt(450));
        lobby1StatLbl.setYPos(lobby1NameLbl.getYPos());

        lobby2Btn.setXPos(lobby1Btn.getXPos());
        lobby2Btn.setYPos(lobby1Btn.getYPos() + localScaleInt(menuPackingHeight) + getLocalImgHeight(lobby1Btn.getBaseImage()));

        lobby2NameLbl.setXPos(lobby2Btn.getXPos() + localScaleInt(20));
        lobby2NameLbl.setYPos(lobby2Btn.getYPos() + getLocalImgHeight(lobby2Btn.getBaseImage()) * 4 / 6);

        lobby2StatLbl.setXPos(lobby2NameLbl.getXPos() + localScaleInt(450));
        lobby2StatLbl.setYPos(lobby2NameLbl.getYPos());

        lobby3Btn.setXPos(lobby2Btn.getXPos());
        lobby3Btn.setYPos(lobby2Btn.getYPos() + localScaleInt(menuPackingHeight) + getLocalImgHeight(lobby2Btn.getBaseImage()));

        lobby3NameLbl.setXPos(lobby3Btn.getXPos() + localScaleInt(20));
        lobby3NameLbl.setYPos(lobby3Btn.getYPos() + getLocalImgHeight(lobby3Btn.getBaseImage()) * 4 / 6);

        lobby3StatLbl.setXPos(lobby3NameLbl.getXPos() + localScaleInt(450));
        lobby3StatLbl.setYPos(lobby3NameLbl.getYPos());

        lobby4Btn.setXPos(lobby3Btn.getXPos());
        lobby4Btn.setYPos(lobby3Btn.getYPos() + localScaleInt(menuPackingHeight) + getLocalImgHeight(lobby3Btn.getBaseImage()));

        lobby4NameLbl.setXPos(lobby4Btn.getXPos() + localScaleInt(20));
        lobby4NameLbl.setYPos(lobby4Btn.getYPos() + getLocalImgHeight(lobby4Btn.getBaseImage()) * 4 / 6);

        lobby4StatLbl.setXPos(lobby4NameLbl.getXPos() + localScaleInt(450));
        lobby4StatLbl.setYPos(lobby4NameLbl.getYPos());

        exitBtn.setXPos(this.getWidth() / 2 - getLocalImgWidth(exitBtn.getBaseImage()) / 2);
        //Line this up with the exit button from the SDMainMenuPanel.java
        exitBtn.setYPos(localScaleInt(250) + ((localScaleInt(SDMenuFrame.MENU_PACKING_HEIGHT) + getLocalImgHeight(exitBtn.getBaseImage())) * 6));

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
        
        closeCSC();
    }
    
    /**
     * TODO:
     */
    private void closeCSC() {
        //reset the csc so that the next attempt will re-estbish a new connection and the old one won't go stale
        csc.requestStop();
        csc = null;
        resetLobbyLabels();
    }

    /**
     * Connect to the 1st lobby
     */
    private void lobbyBtnActionPerformed(int lobbyNum) {
        if (lobbyNum == 1 || lobbyNum == 2) {
            System.out.println("Lobby 1/2");

            //prime the colour selection
            sDMenuFrame.getSDMainMenuPanel().resetSDColourSelectPanel();
            //set the params for Lobby 1
            sDMenuFrame.getSDMainMenuPanel().getSDColourSelectPanel().setLobbyIP(/*"www.lkrampitz.net"*/"localhost");
            sDMenuFrame.getSDMainMenuPanel().getSDColourSelectPanel().setLobbyPort(25571);
            //pass the justMadeNewGame state
            sDMenuFrame.getSDMainMenuPanel().getSDColourSelectPanel().setJustMadeNewGame(justMadeNewGame);
            //show it
            sDMenuFrame.switchPanel(this, sDMenuFrame.getSDMainMenuPanel().getSDColourSelectPanel());

            //start the connection
            sDMenuFrame.getSDMainMenuPanel().getSDColourSelectPanel().startFindServer();
            
            //Terminate connection with the lobby aggregation server
            closeCSC();

        } else {
            System.out.println("Lobby" + lobbyNum);
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

    @Override
    public void componentResized(ComponentEvent e) {
        //Do nothing
    }

    @Override
    public void componentMoved(ComponentEvent e) {
        //Do nothing
    }

    @Override
    public void componentShown(ComponentEvent e) {

        //Debug component detection
        //System.out.println("JoinLobbyPanel Shown!");

        updateLobbyData();

    }

    @Override
    public void componentHidden(ComponentEvent e) {
        //Do nothing
    }

    /**
     * Used to updated the enabled status of lobby buttons and to update the
     * status of their labels too. Can be used for other similar tasks in the
     * future too.
     */
    private void updateLobbyData() {
        //update the instructions
        if (justMadeNewGame) {
            instructionLbl.setText("Please select an empty lobby for your new game.");
        } else {
            instructionLbl.setText("Join a started lobby. To join an empty lobby please make a new game.");
        }

        //open a connection to lobby aggregation server is there is one
        if (csc == null) {
            csc = new ClientSideConnection(/*"www.lkrampitz.net"*/"localhost", 25570);
            //if the connection worked then start the recieve process
            if (csc.isSuccessfulConnect()) {
                csc.beginRecieve();
            }
        }

        int lobbyPop;

        //loop through all the buttons
        for (SettlerBtn btn : settlerBtns) {

            //look for lobby buttons
            if (btn.getType() == 31) {
                /**
                 * Set it to disabled by default. There should always be
                 * positive confirmation on if a lobby can be joined.
                 */
                btn.setEnabled(false);

                lobbyPop = 0; //the #of players in a given lobby

                //find the population of the lobby
                for (int i = 1; i < 5; i++) {
                    if (lobbyStats[btn.getMode() - 1][i] == 1) {
                        lobbyPop++;

                    }
                }

                //update the text status of the lobby
                //also check for special conditions
                switch (lobbyStats[btn.getMode() - 1][0]) {
                    case 0:
                        //if empty lobby
                        settlerLbls[lobbyStats.length + btn.getMode() - 1].setText("empty");
                        break;
                    case -1:
                        //if error state
                        settlerLbls[lobbyStats.length + btn.getMode() - 1].setText("error");
                        break;
                    default:
                        //the standard case
                        settlerLbls[lobbyStats.length + btn.getMode() - 1].setText(lobbyPop + "/" + lobbyStats[btn.getMode() - 1][0]);
                        break;
                }

                //enable the lobbies if a new game was just made
                if (justMadeNewGame) {

                    //if a given lobby is empty it can be joined/setup
                    //Assume the buttons are in the same order in settlerBtns as they are in lobbyStats
                    if (lobbyStats[btn.getMode() - 1][0] == 0) {
                        btn.setEnabled(true);
                    }

                } else { //enble the lobbies if a new game was not just made and the user is just joining
                    //check if the lobby is initialized and has a cound of the amount of max players
                    //checks for non empty and non errored lobbies

                    //enable the lobby if the population is less than the max
                    if (lobbyPop < lobbyStats[btn.getMode() - 1][0]) {
                        btn.setEnabled(true);
                    }

                }
            }
        }
    }

    private void resetLobbyLabels() {
        lobby1StatLbl.setText("");
        lobby2StatLbl.setText("");
        lobby3StatLbl.setText("");
        lobby4StatLbl.setText("");
        instructionLbl.setText("Connecting... Please wait...");
    }

    private class ClientSideConnection {

        private Socket socket;
        private DataInputStream dataIn;
        private DataOutputStream dataOut;
        private boolean successfulConnect;
        private boolean cscStopRequested;

        public ClientSideConnection(String ip, int port) {
            try {
                //establic connection
                socket = new Socket(ip, port);
                dataIn = new DataInputStream(socket.getInputStream());
                dataOut = new DataOutputStream(socket.getOutputStream());

                System.out.println("[Client] " + "Success when connecting to lobby agregation service");
                //if everything else was able to be done save the success
                successfulConnect = true;
            } catch (IOException e) {
                System.out.println("[Client] " + "IOException from CSC contructor while connecting to lobby agregation service");

                //save the failed connection
                successfulConnect = false;
            }
        }

        private void regularRecive() {
            while (!cscStopRequested) {
                //recieving
                System.out.println("Hello");
                
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    System.out.println("InterruptedException in regularRecive() in SDJoinLobbyPanel");
                }
            }
        }

        public void beginRecieve() {
            //wait for a message to come through
            Thread t = new Thread(() -> {
                //never stop listening unless told
                while (!cscStopRequested) {
                    regularRecive();
                }
            });
            t.setName("beginRecieve() in SDJoinLobbyPanel");
            t.start();
        }

        public boolean reciveLobbyStats() {
            boolean bool = false;

            try {
                bool = dataIn.readBoolean();
            } catch (IOException ex) {
                System.out.println("[Client] " + "IOException from CSC reciveBoolean()");
            }

            return bool;
        }

        public boolean isSuccessfulConnect() {
            return successfulConnect;
        }

        public void requestStop() {
            this.cscStopRequested = true;

            //TODO: Send the stop and disconnect request to the server and SSC too.
        }
    }
}
