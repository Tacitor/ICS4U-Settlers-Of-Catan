/*
 * Lukas Krampitz
 * Jan 1, 2023
 * The JPanel for the Main Menu now using Settler Dev Buttons.
 */
package krampitzkreutzwisersettlersofcatan.gui;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import krampitzkreutzwisersettlersofcatan.Catan;
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
    private final SDCreditsPanel sDCreditsPanel; //the new credits menu
    private final GameFrame gameJFrame; //ref to the game JFrame
    private final SDNewGameSettingsPanel sDNewGameSettingsPanel;
    private final SDClientSettings sDClientSettings;
    private SDNewOnlineGameMenu sDnewOnlineGameMenu;
    private SDJoinOnlineGameMenu sDJoinOnlineGameMenu;
    private SDLoadOnlineGameMenu sDloadOnlineGameMenu;

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

        COMPASS_GOLD = sDMenuFrame.setUpCompassGoldFont();

        sDCreditsPanel = new SDCreditsPanel(sDMenuFrame);
        gameJFrame = new GameFrame(this);
        sDClientSettings = new SDClientSettings(sDMenuFrame);
        sDNewGameSettingsPanel = new SDNewGameSettingsPanel(sDMenuFrame);

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
        userManualBtn = new SettlerBtn(false, 0, 22);
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

                } else if (btn.equals(newGameBtn)) {
                    newGameBtnActionPerformed();
                } else if (btn.equals(loadAutosaveBtn)) {
                    loadAutosaveBtnActionPerformed();
                } else if (btn.equals(loadGameBtn)) {
                    loadGameBtnActionPerformed();
                } else if (btn.equals(joinOnlineGameBtn)) {
                    joinOnlineBtnActionPerformed();
                } else if (btn.equals(loadGameToOnlineModeBtn)) {
                    loadToOnlineBtnActionPerformed();
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
     * Open the new game settings
     */
    private void newGameBtnActionPerformed() {
        // Hide this window and show the New Game Settings
        newGameBtn.setmouseHover(false);
        sDMenuFrame.switchPanel(this, sDNewGameSettingsPanel);
    }

    /**
     * Load in a previous game from a save file
     */
    private void loadGameBtnActionPerformed() {
        JFileChooser saveFileLoader = new JFileChooser(); //make a new file chooser

        //create a filter for catan save files
        FileFilter catanSaveFile = new FileFilter() {
            //add the description
            @Override
            public String getDescription() {
                return "Catan Save File (*.catan)";
            }

            //add the logic for the filter
            @Override
            public boolean accept(File f) {
                //if it's a directory ignor it
                if (f.isDirectory()) {
                    return true;
                } else { //if it's a file only show it if it's a .catan file
                    return f.getName().toLowerCase().endsWith(".catan");
                }
            }
        };

        //set up the file choose and call it
        saveFileLoader.setDialogTitle("Select a Save File to Open:");
        saveFileLoader.addChoosableFileFilter(catanSaveFile);
        saveFileLoader.setFileFilter(catanSaveFile);
        int userLoadSelection = saveFileLoader.showOpenDialog(this);

        //check if the user selected a file
        if (userLoadSelection == JFileChooser.APPROVE_OPTION) {

            //test if it is a vailid save file
            try {
                File savefile = new File(saveFileLoader.getSelectedFile().getPath());
                Scanner scanner = new Scanner(savefile);

                // Hide this window and reset the game
                this.setVisible(false);
                gameJFrame.resetGamePanel();

                //check if it is a vailid game save
                if (!scanner.nextLine().equals("SettlersOfCatanSave" + Catan.SAVE_FILE_VER)) {
                    JOptionPane.showMessageDialog(null, "The selected file is not a Settlers of Catan " + Catan.SAVE_FILE_VER + " save file.\nA new game was started instead", "Loading Error", JOptionPane.ERROR_MESSAGE);
                } else { //if it is a real save file
                    //check if the next line hold the player count
                    if (scanner.nextLine().equals("playerCount:")) {
                        //set the player count
                        GamePanel.setPlayerCount(Integer.parseInt(scanner.nextLine()));
                        gameJFrame.resetGamePanel();

                        gameJFrame.loadFromFile(saveFileLoader.getSelectedFile().getPath());

                    } else {
                        JOptionPane.showMessageDialog(null, "The selected file does not contain the required player count data.", "Loading Error", JOptionPane.ERROR_MESSAGE);
                    }
                }

                //show the game                
                gameJFrame.setVisible(true);

            } catch (FileNotFoundException e) {
                JOptionPane.showMessageDialog(null, "There was an error loading the save file:\n" + e, "Loading Error", JOptionPane.ERROR_MESSAGE);
            }

        } else { //if there was so file selected
            JOptionPane.showMessageDialog(null, "There was no file selected.", "Loading Error", JOptionPane.ERROR_MESSAGE);
        }
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
     * Load the Auto-save if there is one
     */
    private void loadAutosaveBtnActionPerformed() {
        String autosaveLocation = System.getProperty("user.home") + File.separator + "AppData" + File.separator + "Roaming" + File.separator + "SettlerDevs" + File.separator + "Catan" + File.separator + "autosave.catan";

        //test if it is a vailid autosave file
        try {
            //use the predetermined auto save file location
            File savefile = new File(autosaveLocation);
            Scanner scanner = new Scanner(savefile);

            // Hide this window and reset the game
            this.setVisible(false);
            gameJFrame.resetGamePanel();

            //check if it is a vailid game save
            if (!scanner.nextLine().equals("SettlersOfCatanSave" + Catan.SAVE_FILE_VER)) {
                JOptionPane.showMessageDialog(null, "The selected file is not a Settlers of Catan " + Catan.SAVE_FILE_VER + " save file.\nA new game was started instead", "Loading Error", JOptionPane.ERROR_MESSAGE);
            } else { //if it is a real save file
                //check if the next line hold the player count
                if (scanner.nextLine().equals("playerCount:")) {
                    //set the player count
                    GamePanel.setPlayerCount(Integer.parseInt(scanner.nextLine()));
                    gameJFrame.resetGamePanel();

                    gameJFrame.loadFromFile(autosaveLocation);

                } else {
                    JOptionPane.showMessageDialog(null, "The selected file does not contain the required player count data.", "Loading Error", JOptionPane.ERROR_MESSAGE);
                }
            }

            //show the game                
            gameJFrame.setVisible(true);

        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, "There was no autosave file detected:\n" + e, "No Autosave", JOptionPane.ERROR_MESSAGE);
        }
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
     * Show the menu to join an Online Game
     */
    private void joinOnlineBtnActionPerformed() {
        //create a new game joining window
        joinOnlineGameBtn.setmouseHover(false);
        sDJoinOnlineGameMenu = new SDJoinOnlineGameMenu(sDMenuFrame);
        sDMenuFrame.switchPanel(this, sDJoinOnlineGameMenu);
    }

    /**
     * Show the menu to load a save file to Online Mode
     */
    private void loadToOnlineBtnActionPerformed() {
        //make a new loading from a save file windows        
        loadGameToOnlineModeBtn.setmouseHover(false);
        sDloadOnlineGameMenu = new SDLoadOnlineGameMenu(sDMenuFrame);
        sDMenuFrame.switchPanel(this, sDloadOnlineGameMenu);
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
    public SDNewOnlineGameMenu getNewOnlineGameMenu() {
        return sDnewOnlineGameMenu;
    }

    /**
     * Mutator for the newOnlineGameMenu
     *
     * @param sDnewOnlineGameMenu
     */
    public void setNewOnlineGameMenu(SDNewOnlineGameMenu sDnewOnlineGameMenu) {
        this.sDnewOnlineGameMenu = sDnewOnlineGameMenu;
    }

    /**
     * Return the newOnlineGameMenu
     *
     * @return
     */
    public SDLoadOnlineGameMenu getLoadOnlineGameMenu() {
        return sDloadOnlineGameMenu;
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
        //Check if the SDJoinOnlineGameMenu is active
        if (sDJoinOnlineGameMenu != null && sDJoinOnlineGameMenu.isVisible()) {
            //pass it on
            sDJoinOnlineGameMenu.keyPress(evt);
        } else if (sDloadOnlineGameMenu != null && sDloadOnlineGameMenu.isVisible()) { //Check if the SDLoadOnlineGameMenu is active
            //pass it on
            sDloadOnlineGameMenu.keyPress(evt);
        } else if (sDnewOnlineGameMenu != null && sDnewOnlineGameMenu.isVisible()) { //Check if the sDnewOnlineGameMenu is active
            //pass it on
            sDnewOnlineGameMenu.keyPress(evt);
        }

    }

}
