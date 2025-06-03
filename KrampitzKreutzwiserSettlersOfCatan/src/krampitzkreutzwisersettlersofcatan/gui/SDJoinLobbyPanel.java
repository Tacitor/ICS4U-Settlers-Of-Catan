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
import java.util.Arrays;
import krampitzkreutzwisersettlersofcatan.Catan;
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
    private LobbyStats[] lobbyStats;

    private boolean justMadeNewGame;

    //Fonts
    public Font COMPASS_GOLD;
    public Font COMPASS_GOLD_45;

    public static String CATAN_SERVER_URL;
    public static final int CATAN_SERVER_PORT = 25570;

    /**
     * Main Constructor
     *
     * @param sDFrame
     */
    public SDJoinLobbyPanel(SDMenuFrame sDFrame) {
        CATAN_SERVER_URL = Catan.DEBUG_SETTLER_SERVER ? "localhost" : "www.lkrampitz.net";

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
        resetLobbyElements();

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
        lobbyStats = new LobbyStats[4];
        //init all the lobby stats to empty lobbies with connection issues by default
        for (int i = 0; i < lobbyStats.length; i++) {
            lobbyStats[i] = new LobbyStats();
        }
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

                //see if this specific player dot should be drawn for a given lobby
                for (int playerNum : lobbyStats[btn.getMode() - 1].getColoursTaken()) {

                    //draw the player's indecator dot
                    g2d.drawImage(ImageRef.PLAYER_DOTS[playerNum],
                            btn.getXPos() + localScaleInt(250) + localScaleInt(40 * playerNum),
                            btn.getYPos() + (getLocalImgHeight(btn.getBaseImage()) / 8 * 3),
                            getLocalImgWidth(ImageRef.PLAYER_DOTS[playerNum]),
                            getLocalImgHeight(ImageRef.PLAYER_DOTS[playerNum]), null);

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

    @Override
    public void exitBtnActionPerformed() {
        //reset having just made the game in the case it is still active
        justMadeNewGame = false;

        exitBtn.setmouseHover(false);
        sDMenuFrame.switchPanel(this, sDMenuFrame.getSDMainMenuPanel());

        closeCSC();
    }

    /**
     *
     */
    public void closeCSC() {
        //reset the csc so that the next attempt will re-estbish a new connection and the old one won't go stale
        csc.requestStop();
        csc = null;
        resetLobbyElements();
    }

    /**
     * Connect to the 1st lobby
     */
    private void lobbyBtnActionPerformed(int lobbyNum) {
        /**
         * Check if justMadeNewGame, and restart the CatanServer if so with the
         * new maxPlayers. We can safely assume the is open an valid otherwise
         * there would have not been a click on a button.
         */
        if (justMadeNewGame) {

            if (lobbyStats[lobbyNum - 1].getMaxClients() == 0) {
                //Assume that SDNewGameSettings has written the updated player setting by this point.
                csc.requestServerRestart(lobbyNum, GamePanel.getPlayerCount());
            } else {
                System.out.println("[LA Client] ERROR: The selected lobby is not empty after creating a new game.");
                return;
            }
        }

        System.out.println("[LA Client] Connecting to Lobby " + lobbyNum + "...");

        //prime the colour selection
        sDMenuFrame.getSDMainMenuPanel().resetSDColourSelectPanel();
        //set the params for the selected lobby 
        sDMenuFrame.getSDMainMenuPanel().getSDColourSelectPanel().setLobbyIP(CATAN_SERVER_URL);
        //Assumes the lobbies have the ports sequencial starting at CATAN_SERVER_PORT
        sDMenuFrame.getSDMainMenuPanel().getSDColourSelectPanel().setLobbyPort(CATAN_SERVER_PORT + lobbyNum);
        //pass the justMadeNewGame state
        sDMenuFrame.getSDMainMenuPanel().getSDColourSelectPanel().setJustMadeNewGame(justMadeNewGame);
        //pass it the lobby stats
        sDMenuFrame.getSDMainMenuPanel().getSDColourSelectPanel().setLobbyStats(lobbyStats);
        //show it
        sDMenuFrame.switchPanel(this, sDMenuFrame.getSDMainMenuPanel().getSDColourSelectPanel());

        //start the connection
        sDMenuFrame.getSDMainMenuPanel().getSDColourSelectPanel().startFindServer();
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
        requestLobbyData();

    }

    @Override
    public void componentHidden(ComponentEvent e) {
        //Do nothing
    }

    /**
     *
     */
    private void requestLobbyData() {
        //open a connection to lobby aggregation server if there isn't one.
        if (csc == null) {
            csc = new ClientSideConnection(CATAN_SERVER_URL, CATAN_SERVER_PORT);
            //if the connection worked then start the recieve process
            if (csc.isSuccessfulConnect()) {
                csc.beginRecieve();
            }
        }

        //Send the first requestLobbyStats()
        csc.requestLobbyStats();
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

        //loop through all the buttons
        for (SettlerBtn btn : settlerBtns) {

            //look for lobby buttons
            if (btn.getType() == 31) {
                /**
                 * Set it to disabled by default. There should always be
                 * positive confirmation on if a lobby can be joined.
                 */
                btn.setEnabled(false);

                int numClients = lobbyStats[btn.getMode() - 1].getNumClients(); //the #of players in a given lobby

                //update the text status of the lobby
                //also check for special conditions
                switch (lobbyStats[btn.getMode() - 1].getMaxClients()) {
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
                        settlerLbls[lobbyStats.length + btn.getMode() - 1].setText(numClients + "/" + lobbyStats[btn.getMode() - 1].getMaxClients());
                        break;
                }

                //enable the lobbies if a new game was just made
                if (justMadeNewGame) {

                    //if a given lobby is empty it can be joined/setup
                    //Assume the buttons are in the same order in settlerBtns as they are in lobbyStats
                    if (lobbyStats[btn.getMode() - 1].getMaxClients() == 0) {
                        btn.setEnabled(true);
                    }

                } else { //enble the lobbies if a new game was not just made and the user is just joining
                    //check if the lobby is initialized and has a cound of the amount of max players
                    //checks for non empty and non errored lobbies

                    //enable the lobby if the population is less than the max
                    if (numClients < lobbyStats[btn.getMode() - 1].getMaxClients()) {
                        btn.setEnabled(true);
                    }

                }
            }
        }
    }

    private void resetLobbyElements() {
        lobby1StatLbl.setText("");
        lobby2StatLbl.setText("");
        lobby3StatLbl.setText("");
        lobby4StatLbl.setText("");
        instructionLbl.setText("Connecting... Please wait...");

        for (SettlerBtn btn : settlerBtns) {
            if (!btn.equals(exitBtn)) {
                btn.setEnabled(false);
            }
        }
    }

    public class LobbyStats {

        private int catanServerID;
        private int maxClients;
        private int numClients;
        private int[] coloursTaken;

        private LobbyStats() {
            catanServerID = -1;
            maxClients = -1;
            numClients = 0;
            coloursTaken = new int[0];
        }

        public LobbyStats(int catanServerID) {
            this();

            this.catanServerID = catanServerID;
        }

        public int getCatanServerID() {
            return catanServerID;
        }

        public int getMaxClients() {
            return maxClients;
        }

        public void setMaxClients(int maxClients) {
            this.maxClients = maxClients;
        }

        public int getNumClients() {
            return numClients;
        }

        /**
         * ...Note: changing this value will reset the coloursTaken.
         *
         * @param numClients
         */
        public void setNumClients(int numClients) {
            this.numClients = numClients;

            //reset the taken colours
            coloursTaken = new int[numClients];
        }

        public int[] getColoursTaken() {
            return coloursTaken;
        }

        public boolean setColoursTaken(int[] coloursTaken) {
            boolean success = false;

            if (coloursTaken.length == this.coloursTaken.length && coloursTaken.length == numClients) {
                this.coloursTaken = coloursTaken;
                success = true;
            }

            return success;
        }

        @Override
        public String toString() {
            return "LobbyStats: \ncatanServerID: " + catanServerID + "\nmaxClients: " + maxClients + "\nnumClients: " + numClients
                    + "\ncoloursTaken: " + Arrays.toString(coloursTaken);
        }
    }

    private class ClientSideConnection {

        private int laID;
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

                //now that a connection has been establichsed get the number for this client
                laID = dataIn.readInt();

                System.out.println("[LA Client " + laID + "] Success when connecting to lobby aggregation service");
                //if everything else was able to be done save the success
                successfulConnect = true;
            } catch (IOException e) {
                System.out.println("[LA Client " + laID + "] IOException from CSC contructor while connecting to lobby aggregation service");

                //save the failed connection
                successfulConnect = false;
            }
        }

        private void receive() {
            System.out.println("[LA Client " + laID + "] Starting receive()");

            while (!cscStopRequested) {
                //receiving
                int type = csc.reciveType();

                switch (type) {
                    case 1:
                        lobbyStats = reciveLobbyStats();
                        updateLobbyData();
                        
                        sDMenuFrame.getSDMainMenuPanel().getSDColourSelectPanel().setLobbyStats(lobbyStats);
                        break;
                    case 4: //for a SSC close
                        cscStopRequested = true;

                        sendStopCommand(4);
                        break;
                }

            }

            System.out.println("[LA Client " + laID + "] End reached for receive()\n");
        }

        public void beginRecieve() {
            Thread t = new Thread(() -> {
                receive();

            });
            t.setName("receive() in SDJoinLobbyPanel");
            t.start();
        }

        public int reciveType() {
            int msg = 0;

            try {
                msg = dataIn.readInt();
            } catch (IOException ex) {
                System.out.println("[LA Client " + laID + "] IOException from CSC reciveType():\n\t" + ex);

                //request a stop
                cscStopRequested = true;
            }

            return msg;
        }

        /**
         *
         * @param catanServerID
         * @param maxClients
         */
        private void requestServerRestart(int catanServerID, int maxClients) {
            try {
                dataOut.writeInt(2); //tell the server it is reveiving a request to restart a CatanServer #2
                dataOut.writeInt(catanServerID);
                dataOut.writeInt(maxClients);
                dataOut.flush();
            } catch (IOException e) {
                System.out.println("[LA Client " + laID + "] IOException from CSC requestServerRestart()");
            }

        }

        public void requestLobbyStats() {
            try {
                dataOut.writeInt(1); //tell the server it is reveiving a request for lobby stats #1
                dataOut.flush();

            } catch (IOException e) {
                System.out.println("[LA Client " + laID + "] IOException from CSC requestLobbyStats()");
            }
        }

        public LobbyStats[] reciveLobbyStats() {
            LobbyStats lb[] = null;
            int tempInt;
            boolean success;

            try {
                tempInt = dataIn.readInt(); //read in the number of lobbies.
                lb = new LobbyStats[tempInt];
                int[] colours;

                for (int i = 0; i < lb.length; i++) {
                    tempInt = dataIn.readInt(); //read in the catanServerID
                    lb[i] = new LobbyStats(tempInt);

                    tempInt = dataIn.readInt(); //read in the max clients
                    lb[i].setMaxClients(tempInt);

                    tempInt = dataIn.readInt(); //read in the number of connected clients
                    colours = new int[tempInt];
                    lb[i].setNumClients(tempInt);

                    for (int j = 0; j < lb[i].getNumClients(); j++) {
                        colours[j] = dataIn.readInt();
                    }
                    success = lb[i].setColoursTaken(colours);

                    if (!success) {
                        throw new IllegalArgumentException("False result from setColoursTaken()");
                    }

                }
            } catch (IllegalArgumentException ex) {
                System.out.println("[LA Client " + laID + "] IllegalArgumentException from CSC reciveLobbyStats() \n\t" + ex);
            } catch (IOException ex) {
                System.out.println("[LA Client " + laID + "] IOException from CSC reciveLobbyStats()");
            }

            return lb;
        }

        public boolean isSuccessfulConnect() {
            return successfulConnect;
        }

        public void requestStop() {
            //send the requestion
            if (!cscStopRequested) {
                csc.sendStopCommand(3);
            }
        }

        /**
         * Valid only for type 3 or 4
         *
         * @param type
         */
        public void sendStopCommand(int type) {

            if (type == 3 || type == 4) {
                try {
                    dataOut.writeInt(type); //tell the server it is reveiving a LA stop command #3 or #4
                    dataOut.flush();

                } catch (IOException e) {
                    System.out.println("[LA Client " + laID + "] IOException from CSC sendStopCommand()\n" + e);
                }
            } else {
                throw new IllegalArgumentException("[LA Client " + laID + "] Invalid type specified: " + type + ". Must be either 3 or 4.");
            }
        }

    }
}
