/*
 * Lukas Krampitz
 * Jan 1, 2023
 * The JPanel for the Main Menu now using Settler Dev Buttons.
 */
package krampitzkreutzwisersettlersofcatan.gui;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import krampitzkreutzwisersettlersofcatan.worldObjects.buttons.SettlerBtn;
import textures.ImageRef;

/**
 *
 * @author Tacitor
 */
public class SDMainMenuPanel extends javax.swing.JPanel implements MouseMotionListener {

    //Ref to frame this is held in
    private SDMenuFrame sDMenuFrame;
    
    //ref to the other Menu elements
    private final UserManualUI userManualUIFrame; //referance to the user manual
    private final CreditsUI creditsUIFrame; //referance to the user credits JFrame
    private final GameFrame gameJFrame; //ref to the game JFrame
    private final NewGameSettings newGameSettingsFrame;
    private final ClientSettings clientSettings;
    private NewOnlineGameMenu newOnlineGameMenu;
    private JoinOnlineGameMenu joinOnlineGameMenu;
    private LoadOnlineGameMenu loadOnlineGameMenu;
    
    //Attributes
    private static double localScaleFactor; //The factor to scale this panel by when drawing elemets
    private int mouseMotionPosX; //acording to the MouseMotionListener where is the mouse located
    private int mouseMotionPosY;

    //Settler Compoments
    private SettlerBtn newGameBtn, loadGameBtn, loadAutosaveBtn, optionsBtn, joinOnlineGameBtn, loadGameToOnlineModeBtn, creditsBtn, userManualBtn, exitMainMenuBtn;
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

        COMPASS_GOLD = setUpCompassGoldFont();
        
        userManualUIFrame = new UserManualUI(this);
        creditsUIFrame = new CreditsUI(this);
        gameJFrame = new GameFrame(this);
        clientSettings = new ClientSettings(this);
        newGameSettingsFrame = new NewGameSettings(this, gameJFrame, newOnlineGameMenu);
        loadOnlineGameMenu = new LoadOnlineGameMenu(this);
        newOnlineGameMenu = new NewOnlineGameMenu(this);

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
        newGameBtn = new SettlerBtn(true, 0, 14);
        loadGameBtn = new SettlerBtn(true, 0, 15);
        loadAutosaveBtn = new SettlerBtn(true, 0, 19);
        optionsBtn = new SettlerBtn(true, 0, 20);
        joinOnlineGameBtn = new SettlerBtn(true, 0, 16);
        loadGameToOnlineModeBtn = new SettlerBtn(true, 0, 17);
        creditsBtn = new SettlerBtn(true, 0, 21);
        userManualBtn = new SettlerBtn(true, 0, 22);
        exitMainMenuBtn = new SettlerBtn(true, 0, 18);

        //add them to the array
        settlerBtns = new SettlerBtn[]{newGameBtn, loadGameBtn, loadAutosaveBtn, optionsBtn, joinOnlineGameBtn, loadGameToOnlineModeBtn, creditsBtn, userManualBtn, exitMainMenuBtn};

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
        int menuPackingHeight = 12;
        
        newGameBtn.setXPos(this.getWidth() / 2 - sDMenuFrame.getImgWidthLocal(exitMainMenuBtn.getBaseImage(), this) / 2);
        newGameBtn.setYPos(localScaleInt(250));

        loadGameBtn.setXPos(newGameBtn.getXPos());
        loadGameBtn.setYPos(newGameBtn.getYPos() + localScaleInt(menuPackingHeight) + sDMenuFrame.getImgHeightLocal(loadGameBtn.getBaseImage(), this));

        loadAutosaveBtn.setXPos(newGameBtn.getXPos());
        loadAutosaveBtn.setYPos(loadGameBtn.getYPos() + localScaleInt(menuPackingHeight) + sDMenuFrame.getImgHeightLocal(optionsBtn.getBaseImage(), this));

        optionsBtn.setXPos(loadAutosaveBtn.getXPos() + localScaleInt(12) + sDMenuFrame.getImgWidthLocal(optionsBtn.getBaseImage(), this));
        optionsBtn.setYPos(loadAutosaveBtn.getYPos());

        joinOnlineGameBtn.setXPos(newGameBtn.getXPos());
        joinOnlineGameBtn.setYPos(optionsBtn.getYPos() + localScaleInt(menuPackingHeight) + sDMenuFrame.getImgHeightLocal(joinOnlineGameBtn.getBaseImage(), this));

        loadGameToOnlineModeBtn.setXPos(newGameBtn.getXPos());
        loadGameToOnlineModeBtn.setYPos(joinOnlineGameBtn.getYPos() + localScaleInt(menuPackingHeight) + sDMenuFrame.getImgHeightLocal(loadGameToOnlineModeBtn.getBaseImage(), this));

        creditsBtn.setXPos(newGameBtn.getXPos());
        creditsBtn.setYPos(loadGameToOnlineModeBtn.getYPos() + localScaleInt(menuPackingHeight) + sDMenuFrame.getImgHeightLocal(creditsBtn.getBaseImage(), this));

        userManualBtn.setXPos(creditsBtn.getXPos() + localScaleInt(12) + sDMenuFrame.getImgWidthLocal(userManualBtn.getBaseImage(), this));
        userManualBtn.setYPos(creditsBtn.getYPos());

        exitMainMenuBtn.setXPos(newGameBtn.getXPos());
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

                    exitMainMenuBtnPressed();

                }
            }
        }
    }

    /**
     * Return the Compass Gold font. Setup and load the TrueType font from the
     * file system for use in game.
     *
     * @return
     */
    private Font setUpCompassGoldFont() {

        Font tempFont = null;

        try {
            //System.out.println(url.getPath());
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getClassLoader().getResourceAsStream("font/CompassGold.ttf")));

            tempFont = new Font("CompassGold", Font.PLAIN, 20);

        } catch (FontFormatException ex) {
            System.out.println("FontFormatException: " + ex);
        } catch (IOException ex) {
            System.out.println("IOException: " + ex);
        }

        return tempFont;
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
    public static int localScaleInt(int num) {
        return (int) (num / localScaleFactor);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        //System.out.println("Mouse Dragged");
    }

    /**
     * Closed the game
     */
    private void exitMainMenuBtnPressed() {
        System.exit(0);
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
     * Return the newOnlineGameMenu
     *
     * @return
     */
    public NewOnlineGameMenu getNewOnlineGameMenu() {
        return newOnlineGameMenu;
    }

    /**
     * Mutator for the newOnlineGameMenu
     *
     * @param newOnlineGameMenu
     */
    public void setNewOnlineGameMenu(NewOnlineGameMenu newOnlineGameMenu) {
        this.newOnlineGameMenu = newOnlineGameMenu;
    }

    /**
     * Return the newOnlineGameMenu
     *
     * @return
     */
    public LoadOnlineGameMenu getLoadOnlineGameMenu() {
        return loadOnlineGameMenu;
    }

    /**
     * Return the newOnlineGameMenu
     *
     * @return
     */
    public JoinOnlineGameMenu getJoinOnlineGameMenu() {
        return joinOnlineGameMenu;
    }
    
    /**
     * Mutator for the joinOnlineGameMenu
     *
     * @param joinOnlineGameMenu
     */
    public void setJoinOnlineGameMenu(JoinOnlineGameMenu joinOnlineGameMenu) {
        this.joinOnlineGameMenu = joinOnlineGameMenu;
    }

}
