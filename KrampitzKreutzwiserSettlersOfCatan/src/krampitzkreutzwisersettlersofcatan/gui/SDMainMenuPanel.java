/*
 * Lukas Krampitz
 * Jan 1, 2023
 * The JPanel for the Main Menu now using Settler Dev Buttons.
 */
package krampitzkreutzwisersettlersofcatan.gui;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import krampitzkreutzwisersettlersofcatan.Catan;
import krampitzkreutzwisersettlersofcatan.worldObjects.buttons.SettlerBtn;
import textures.ImageRef;

/**
 *
 * @author Tacitor
 */
public class SDMainMenuPanel extends javax.swing.JPanel implements MouseMotionListener, SDScaleImageResizeable {

    //Ref to frame this is held in
    private SDMenuFrame sDMenuFrame;

    //ref to the other Menu elements
    private final SDCreditsPanel sDCreditsPanel; //the new credits menu
    private final GameFrame gameJFrame; //ref to the game JFrame
    private final SDClientSettings sDClientSettings;
    private SDOnlineGamePanel sDOnlineGamePanel;
    private SDOfflineGamePanel sDOfflineGamePanel;

    private SDNewGameSettingsPanel sDNewGameSettingsPanel;
    private SDJoinLobbyPanel sDJoinLobbyPanel;
    private SDColourSelectPanel sDColourSelectPanel;

    //Attributes
    private static double localScaleFactor; //The factor to scale this panel by when drawing elemets
    private int mouseMotionPosX; //acording to the MouseMotionListener where is the mouse located
    private int mouseMotionPosY;

    //Settler Compoments
    private SettlerBtn onlineGameBtn, offlineGameBtn, optionsBtn, creditsBtn, userManualBtn, exitMainMenuBtn;
    //The array for the buttons
    private SettlerBtn[] settlerBtns;

    //Fonts
    public Font COMPASS_GOLD;

    /**
     * Main Constructor
     *
     * @param sDFrame
     */
    public SDMainMenuPanel(SDMenuFrame sDFrame) {
        sDMenuFrame = sDFrame;

        COMPASS_GOLD = sDMenuFrame.setUpCompassGoldFont();

        sDCreditsPanel = new SDCreditsPanel(sDMenuFrame);

        gameJFrame = new GameFrame(this);

        sDClientSettings = new SDClientSettings(sDMenuFrame);

        sDOnlineGamePanel = new SDOnlineGamePanel(sDMenuFrame);
        sDOfflineGamePanel = new SDOfflineGamePanel(sDMenuFrame);

        //init the newGameSettingsPanel
        resetSDNewGameSettingsPanel();
        //init the join lobby panel
        resetSDJoinLobbyPanel();
        //init the colour selection panel
        resetSDColourSelectPanel();

        //add the mouse motion listener
        addMouseMotionListener(this);

        //add a mouse listener that call the mouse click event handler
        addMouseListener(new MouseAdapter() {
            /**
             * Triggered when the user clicks on the SDMainMenuPanel. Calls the
             * main menu panel's click event method.
             *
             * @param event
             */
            @Override
            public final void mouseReleased(MouseEvent event) {
                //send the mouse event to the main menu panel click handler
                mouseClick(event);
            }
        });

        //setup the buttons
        onlineGameBtn = new SettlerBtn(true, 0, 29);
        offlineGameBtn = new SettlerBtn(true, 0, 30);
        optionsBtn = new SettlerBtn(true, 0, 20);
        creditsBtn = new SettlerBtn(true, 0, 21);
        userManualBtn = new SettlerBtn(false, 0, 22);
        exitMainMenuBtn = new SettlerBtn(true, 0, 18);

        //add them to the array
        settlerBtns = new SettlerBtn[]{onlineGameBtn, offlineGameBtn, optionsBtn, creditsBtn, userManualBtn, exitMainMenuBtn};

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
     * Draw the UI for the Main Menu
     *
     * @param g
     */
    private void draw(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;

        //update the scale factor
        localScaleFactor = sDMenuFrame.calcScaleFactor(this);

        //update the button positions
        settlerVarPos();

        //draw the background image
        g2d.drawImage(ImageRef.WOOD_BACKGROUND,
                0,
                0,
                this.getWidth(),
                this.getHeight(), this);

        g2d.setFont(new Font(COMPASS_GOLD.getName(), Font.PLAIN, localScaleInt(200)));
        g2d.setColor(DomesticTradePanel.BEIGE_COLOUR);

        //Draw the Title
        g2d.drawString("Settlers of Catan",
                (this.getWidth() / 2) - (g2d.getFontMetrics().stringWidth("Settlers of Catan") / 2),
                localScaleInt(180));

        g2d.setFont(new Font(COMPASS_GOLD.getName(), Font.PLAIN, localScaleInt(40)));
        g2d.drawString(Catan.GAME_VER, 10, this.getHeight() - localScaleInt(20));

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
    }

    /**
     * Update the positions of the SD Components
     */
    private void settlerVarPos() {
        int menuPackingHeight = SDMenuFrame.MENU_PACKING_HEIGHT;

        onlineGameBtn.setXPos(this.getWidth() / 2 - sDMenuFrame.getImgWidthLocal(exitMainMenuBtn.getBaseImage(), this) / 2);
        onlineGameBtn.setYPos(localScaleInt(250));

        offlineGameBtn.setXPos(onlineGameBtn.getXPos());
        offlineGameBtn.setYPos(onlineGameBtn.getYPos() + localScaleInt(menuPackingHeight) + sDMenuFrame.getImgHeightLocal(offlineGameBtn.getBaseImage(), this));

        optionsBtn.setXPos(offlineGameBtn.getXPos());
        optionsBtn.setYPos(offlineGameBtn.getYPos() + localScaleInt(menuPackingHeight) + sDMenuFrame.getImgHeightLocal(offlineGameBtn.getBaseImage(), this));

        creditsBtn.setXPos(onlineGameBtn.getXPos());
        creditsBtn.setYPos(optionsBtn.getYPos() + localScaleInt(menuPackingHeight) + sDMenuFrame.getImgHeightLocal(creditsBtn.getBaseImage(), this));

        userManualBtn.setXPos(creditsBtn.getXPos() + localScaleInt(12) + sDMenuFrame.getImgWidthLocal(userManualBtn.getBaseImage(), this));
        userManualBtn.setYPos(creditsBtn.getYPos());

        exitMainMenuBtn.setXPos(onlineGameBtn.getXPos());
        //Old Height
        //this.getHeight() - localScaleInt(20) - sDMenuFrame.getImgHeightLocal(exitMainMenuBtn.getBaseImage(), this)
        exitMainMenuBtn.setYPos(userManualBtn.getYPos() + localScaleInt(menuPackingHeight) + sDMenuFrame.getImgHeightLocal(exitMainMenuBtn.getBaseImage(), this));
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
                if (btn.equals(exitMainMenuBtn)) { //if it was the exit game button

                    exitBtnActionPerformed();

                } else if (btn.equals(onlineGameBtn)) {
                    onlineGameBtnActionPerformed();
                } else if (btn.equals(offlineGameBtn)) {
                    offlineGameBtnActionPerformed();
                } else if (btn.equals(optionsBtn)) {
                    optionBtnActionPerformed();
                } else if (btn.equals(creditsBtn)) {
                    creditsBtnActionPerformed();
                }
            }
        }
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

    /**
     * Closed the game
     */
    @Override
    public void exitBtnActionPerformed() {
        System.exit(0);
    }

    /**
     * Open the online game settings
     */
    private void onlineGameBtnActionPerformed() {
        onlineGameBtn.setmouseHover(false);
        sDMenuFrame.switchPanel(this, sDOnlineGamePanel);
    }

    /**
     * Open the offline game settings
     */
    private void offlineGameBtnActionPerformed() {
        offlineGameBtn.setmouseHover(false);
        sDMenuFrame.switchPanel(this, sDOfflineGamePanel);
    }

    /**
     * Show the Credits
     */
    private void creditsBtnActionPerformed() {
        // Hide this window and show the credits
        creditsBtn.setmouseHover(false);
        sDMenuFrame.switchPanel(this, sDCreditsPanel);
    }

    /**
     * Show the Options
     */
    private void optionBtnActionPerformed() {
        // Hide this window and show the settings
        optionsBtn.setmouseHover(false);
        sDMenuFrame.switchPanel(this, sDClientSettings);
    }

    /**
     * Return the game frame
     *
     * @return
     */
    public GameFrame getGameFrame() {
        return gameJFrame;
    }

    /**
     * Accessor for the sDnewGameSettingsPanel
     *
     * @return
     */
    public SDNewGameSettingsPanel getSDNewGameSettingsPanel() {
        return sDNewGameSettingsPanel;
    }

    /**
     * Reset method for sDnewGameSettingsPanel
     */
    public void resetSDNewGameSettingsPanel() {
        sDNewGameSettingsPanel = new SDNewGameSettingsPanel(sDMenuFrame);
    }

    /**
     * Accessor for sDjoinLobbyPanel
     *
     * @return
     */
    public SDJoinLobbyPanel getSDJoinLobbyPanel() {
        return sDJoinLobbyPanel;
    }

    /**
     * Reset method for sDjoinLobbyPanel
     */
    public void resetSDJoinLobbyPanel() {
        sDJoinLobbyPanel = new SDJoinLobbyPanel(sDMenuFrame);
        sDJoinLobbyPanel.setVisible(false);
    }

    /**
     * Accessor for sDColourSelectPanel
     *
     * @return
     */
    public SDColourSelectPanel getSDColourSelectPanel() {
        return sDColourSelectPanel;
    }

    /**
     * Reset method for sDColourSelectPanel
     */
    public void resetSDColourSelectPanel() {
        sDColourSelectPanel = new SDColourSelectPanel(sDMenuFrame);
        sDColourSelectPanel.setVisible(false);
    }

    public int getExitMainMenuBtnYPos() {
        settlerVarPos(); //update the positions real quick
        return exitMainMenuBtn.getYPos();
    }

    public SDMenuFrame getSDMenuFrame() {
        return sDMenuFrame;
    }

    /**
     * What to do when the user clicks a key on their keyboard This will be
     * called by the SDMenuFrame
     *
     * @param evt
     */
    public void keyPress(KeyEvent evt) {

        //pass on the KeyEvent to the right panel.
        //Check if the SDClientSettings is active
        if (sDClientSettings != null && sDClientSettings.isVisible()) {
            //pass it on
            sDClientSettings.keyPress(evt);
        }
    }

    @Override
    public int getLocalImgWidth(Image image) {
        throw new UnsupportedOperationException("Not supported. Call through SDMenuFrame");
    }

    @Override
    public int getLocalImgHeight(Image image) {
        throw new UnsupportedOperationException("Not supported. Call through SDMenuFrame");
    }

}
