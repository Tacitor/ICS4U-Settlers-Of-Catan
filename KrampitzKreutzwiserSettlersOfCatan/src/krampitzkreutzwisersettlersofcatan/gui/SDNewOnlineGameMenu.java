/*
 * Lukas Krampitz
 * October 23, 2023
 * The JPanel for the New Online Game Menu now using Settler Dev Buttons.
 */
package krampitzkreutzwisersettlersofcatan.gui;

import dataFiles.OldCode;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.InputStream;
import java.util.Scanner;
import krampitzkreutzwisersettlersofcatan.sockets.CatanClient;
import krampitzkreutzwisersettlersofcatan.sockets.CatanServer;
import krampitzkreutzwisersettlersofcatan.worldObjects.buttons.SettlerBtn;
import krampitzkreutzwisersettlersofcatan.worldObjects.buttons.SettlerLbl;
import krampitzkreutzwisersettlersofcatan.worldObjects.buttons.SettlerTxtBx;
import textures.ImageRef;

/**
 *
 * @author Tacitor
 */
public class SDNewOnlineGameMenu extends javax.swing.JPanel implements MouseMotionListener, SDScaleImageResizeable {

    private final SDMenuFrame sDMenuFrame;

    private CatanClient client;
    private CatanServer server;
    private int portNum;

    private static double localScaleFactor; //The factor to scale this panel by when drawing elemets
    private int mouseMotionPosX; //acording to the MouseMotionListener where is the mouse located
    private int mouseMotionPosY;

    //Settler Compoments
    private final SettlerBtn exitBtn, createServerBtn;
    //The array for the buttons
    private final SettlerBtn[] settlerBtns;
    //Settler Labels
    private final SettlerLbl mainDesc, connectionPortLbl;
    //The array for the buttons
    private final SettlerLbl[] settlerLbls;
    //settler text boxes
    private final SettlerTxtBx connectionPortTxtBx;
    //the array for the text boxes
    private final SettlerTxtBx[] settlerTxtBxs;

    //Fonts
    public Font COMPASS_GOLD;

    /**
     * Main Constructor
     *
     * @param sDFrame
     */
    public SDNewOnlineGameMenu(SDMenuFrame sDFrame) {
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
        createServerBtn = new SettlerBtn(true, 0, 28);
        //add them to the array
        settlerBtns = new SettlerBtn[]{exitBtn, createServerBtn};
        //Setup the labels
        mainDesc = new SettlerLbl("To join an online game please ensure the folowing:");
        mainDesc.setForeground(DomesticTradePanel.BEIGE_COLOUR);
        mainDesc.setLinewrapSpace(40);
        mainDesc.setUseNewLineChar(true);
        connectionPortLbl = new SettlerLbl("Connection port:");
        connectionPortLbl.setForeground(DomesticTradePanel.BEIGE_COLOUR);

        //add them to the array
        settlerLbls = new SettlerLbl[]{mainDesc, connectionPortLbl};

        //setup the text boxes
        connectionPortTxtBx = new SettlerTxtBx(true, 0);
        connectionPortTxtBx.setTextStr("25570");
        //add it to the array
        settlerTxtBxs = new SettlerTxtBx[]{connectionPortTxtBx};

        loadMaterial();

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
        g2d.drawString("New Online Game",
                (this.getWidth() / 2) - (g2d.getFontMetrics().stringWidth("New Online Game") / 2),
                localScaleInt(100));

        g2d.setFont(new Font(COMPASS_GOLD.getName(), Font.PLAIN, localScaleInt(70)));

        //=-=-=-=-=-=-=-=-=-= Draw the Settlerbuttons =-=-=-=-=-=-=-=-=-=
        for (SettlerBtn btn : settlerBtns) {
            btn.updateButtonImages();
            btn.updateText();

            //draw the base        
            sDMenuFrame.drawSettlerBtn(g2d, btn.getBaseImage(), btn, 0, this);

            //draw the text
            sDMenuFrame.drawSettlerBtn(g2d, btn.getTextImage(), btn, 0, this);

            //draw the disabled overlay if required
            if (!btn.isEnabled()) {
                sDMenuFrame.drawSettlerBtn(g2d, btn.getDisabledImage(), btn, 0, this);
            }
            //draw the mouseHover overlay if required
            if (btn.isMouseHover()) {
                sDMenuFrame.drawSettlerBtn(g2d, btn.getHoverImage(), btn, 1, this);
            }

        }

        //=-=-=-=-=-=-=-=-=-= END OF the drawing of Settlerbuttons =-=-=-=-=-=-=-=-=-=
        //go through and draw all the labels
        for (SettlerLbl settlerLbl : settlerLbls) {
            settlerLbl.draw(g2d, localScaleFactor);
        }

        //draw all the text boxes
        for (SettlerTxtBx txtBx : settlerTxtBxs) {
            txtBx.draw(g2d, this);
        }
    }

    /**
     * Update the positions of the SD Components
     */
    private void settlerVarPos(Graphics2D g2d) {
        //Label Loop
        mainDesc.setFont(new Font(COMPASS_GOLD.getName(), Font.PLAIN, localScaleInt(40)));
        mainDesc.setSpaceForText(localScaleInt(1500));
        mainDesc.calcNumLinesCarriageReturn();
        connectionPortLbl.setFont(new Font(COMPASS_GOLD.getName(), Font.PLAIN, localScaleInt(50)));

        //set positions
        mainDesc.setXPos(localScaleInt(50)); //line up with the title
        mainDesc.setYPos(localScaleInt(170));

        connectionPortLbl.setXPos(mainDesc.getXPos());
        connectionPortLbl.setYPos(mainDesc.getYPos() + ((mainDesc.getNumLines() + 1) * localScaleInt(mainDesc.getLinewrapSpace())));

        connectionPortTxtBx.setXPos(connectionPortLbl.getXPos());
        connectionPortTxtBx.setYPos(connectionPortLbl.getYPos() + localScaleInt(15));

        exitBtn.setXPos(this.getWidth() / 2 - sDMenuFrame.getImgWidthLocal(exitBtn.getBaseImage(), this) / 2);
        //Line this up with the exit button from the SDMainMenuPanel.java
        exitBtn.setYPos(localScaleInt(250) + ((localScaleInt(SDMenuFrame.MENU_PACKING_HEIGHT) + sDMenuFrame.getImgHeightLocal(exitBtn.getBaseImage(), this)) * 6));

        createServerBtn.setXPos(exitBtn.getXPos());
        createServerBtn.setYPos(exitBtn.getYPos() + sDMenuFrame.getImgHeightLocal(exitBtn.getBaseImage(), this) + localScaleInt(SDMenuFrame.MENU_PACKING_HEIGHT));

    }

    /**
     * Handles click releases from mouse input.
     *
     * @param evt
     */
    public void mouseClick(MouseEvent evt) {

        //deselect all the text boxes and then later enable one if there was a click on it
        connectionPortTxtBx.setSelected(false);

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
                } else if (btn.equals(createServerBtn)) { //if it was the connectBtn button
                    createServerBtnActionPerformed();
                }
            }
        }

        //check if the player clicked on one of the SettlerTxtBxs
        for (SettlerTxtBx bx : settlerTxtBxs) {
            if (evt.getX() > bx.getXPos()
                    && evt.getY() > bx.getYPos()
                    && evt.getX() < (bx.getXPos() + sDMenuFrame.getImgWidthLocal(bx.getBaseImage(), this))
                    && evt.getY() < (bx.getYPos() + sDMenuFrame.getImgHeightLocal(bx.getBaseImage(), this))
                    && bx.isEnabled()) { //and that it is enabled

                //select the box
                bx.setSelected(true);
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

        //check if the player moved the mouse over one of the SettlerTxtBxs
        for (SettlerTxtBx bx : settlerTxtBxs) {
            if (mouseMotionPosX > bx.getXPos()
                    && mouseMotionPosY > bx.getYPos()
                    && mouseMotionPosX < (bx.getXPos() + sDMenuFrame.getImgWidthLocal(bx.getBaseImage(), this))
                    && mouseMotionPosY < (bx.getYPos() + sDMenuFrame.getImgHeightLocal(bx.getBaseImage(), this))
                    && bx.isEnabled()) { //and that it is enabled

                //set the type of cursor to display for the mouse pointer
                this.setCursor(new Cursor(Cursor.TEXT_CURSOR));

            } else {
                //check if there is a non default cursor
                if (this.getCursor().getType() != Cursor.DEFAULT_CURSOR) {
                    this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
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
        exitBtn.setmouseHover(false);
        sDMenuFrame.switchPanel(this, sDMenuFrame.getSDMainMenuPanel());
    }

    private void createServerBtnActionPerformed() {
        String input = connectionPortTxtBx.getTextStr();

        //make sure the input is good
        //if a blank feild
        if (!input.equals("")) {

            //if the feild is not blank check if it's and integer
            try {
                int portNum = Integer.parseInt(input);

                //make sure no important ports
                if (portNum != 80 && portNum != 443) {

                    this.portNum = portNum;

                    //disable the button
                    createServerBtn.setEnabled(false);

                    //display a message showing that the server is being set up
                    createServerBtn.setMode(2);

                    //run the set up
                    runSetup();
                } else {
                    createServerBtn.setMode(1);
                }

            } catch (NumberFormatException e) {
                createServerBtn.setMode(1);
            }

        } else {
            createServerBtn.setMode(1);
        }
    }

    /**
     * Sets everything up for other player to join over a network
     */
    public void runSetup() {
        //reset the game panel
        sDMenuFrame.getSDMainMenuPanel().getGameFrame().resetGamePanel();

        serverStartUp();
        createFirstClient();

        //show that the setup is complete by resetting the button to the default state
        createServerBtn.setMode(0);

    }

    /**
     * Create the local server
     */
    private void serverStartUp() {
        server = new CatanServer(GamePanel.getPlayerCount(), portNum);

        //create a new thread for the server
        Thread t = new Thread(() -> {
            server.acceptConnections();
        });

        //start running the server
        t.start();
    }

    /**
     * Creates a client to connect to the local server
     */
    private void createFirstClient() {
        //create the new client and request to be the red player
        client = new CatanClient(700, 200, "localhost", sDMenuFrame.getSDMainMenuPanel().getGameFrame(), portNum);
        client.connectToServer();
        client.setUpGUI();
        client.setUpButton();

        //request the player colour
        client.requestColour(1); //request the red player

        //wait for the response to come through
        while (client.getClientColour() == 0) {
            //while there is no assinged colour do nothing and just wait
        }

        //once the client has been set up save it to the game panel
        GamePanel.setOnlineMode(client.getClientColour());
        GamePanel.setCatanClient(client);

    }

    /**
     * Read the instructions from in the data file
     */
    public final void loadMaterial() {

        // Declare variablesD
        Scanner fileReader;
        InputStream file = OldCode.class.getResourceAsStream("loadOnlineGame.txt");
        String fileContents = "";

        // Try to read the file
        try {
            // Create the scanner to read the file
            fileReader = new Scanner(file);

            // Read the entire file into a string
            while (fileReader.hasNextLine()) {
                // Read the line of the file into a line of the string
                fileContents += fileReader.nextLine() + "\n";
            }
        } catch (Exception e) {
            // Set the sring to be displayed to an error message
            fileContents = "Error: credits file could not be read.";
            // Output the jsvs error to the standard output
            System.out.println("Error reading credits file: " + e);
        }

        // Display the file's contents from the string
        mainDesc.setText(fileContents);
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
     * What to do when the user clicks a key on their keyboard This will be
     * called by the SDMainMenuPanel which was called by the SDMenuFrame
     *
     * @param evt
     */
    public void keyPress(KeyEvent evt) {

        //check if there is a selected text box ready for input
        for (SettlerTxtBx bx : settlerTxtBxs) {
            if (bx.isSelected() && bx.isEnabled()) { //and that it is enabled

                bx.keyPress(evt);
            }
        }
    }

}
