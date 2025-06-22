/*
 * Lukas Krampitz
 * August 2, 2024
 * The JPanel for the Online Game panel using Settler Dev Buttons.
 */
package krampitzkreutzwisersettlersofcatan.gui;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
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
public class SDOnlineGamePanel extends javax.swing.JPanel implements MouseMotionListener, SDScaleImageResizeable {

    private SDMenuFrame sDMenuFrame;
    private static double localScaleFactor; //The factor to scale this panel by when drawing elemets
    private int mouseMotionPosX; //acording to the MouseMotionListener where is the mouse located
    private int mouseMotionPosY;

    //Settler Compoments
    private SettlerBtn newGameBtn, joinLobbyBtn, loadGameBtn, exitBtn;
    //The array for the buttons
    private SettlerBtn[] settlerBtns;

    //Fonts
    public Font COMPASS_GOLD;

    /**
     * Main Constructor
     *
     * @param sDFrame
     */
    public SDOnlineGamePanel(SDMenuFrame sDFrame) {
        sDMenuFrame = sDFrame;

        COMPASS_GOLD = sDMenuFrame.setUpCompassGoldFont();

        //add the mouse motion listener
        addMouseMotionListener(this);

        //add a mouse listener that call the mouse click event handler
        addMouseListener(new MouseAdapter() {
            /**
             * Triggered when the user clicks on the SDOnlineGamePanel. Calls the
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
        newGameBtn = new SettlerBtn(true, 0, 14);
        joinLobbyBtn = new SettlerBtn(true, 0, 16);
        loadGameBtn = new SettlerBtn(true, 0, 15);
        //add them to the array
        settlerBtns = new SettlerBtn[]{exitBtn, newGameBtn, joinLobbyBtn, loadGameBtn};

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
        g2d.drawString("Online Game",
                (this.getWidth() / 2) - (g2d.getFontMetrics().stringWidth("Online Game") / 2),
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
    }

    /**
     * Update the positions of the SD Components
     */
    private void settlerVarPos(Graphics2D g2d) {
        int menuPackingHeight = SDMenuFrame.MENU_PACKING_HEIGHT;

        newGameBtn.setXPos(this.getWidth() / 2 - sDMenuFrame.getImgWidthLocal(newGameBtn.getBaseImage(), this) / 2);
        newGameBtn.setYPos(localScaleInt(250));

        joinLobbyBtn.setXPos(newGameBtn.getXPos());
        joinLobbyBtn.setYPos(newGameBtn.getYPos() + localScaleInt(menuPackingHeight) + sDMenuFrame.getImgHeightLocal(joinLobbyBtn.getBaseImage(), this));

        loadGameBtn.setXPos(joinLobbyBtn.getXPos());
        loadGameBtn.setYPos(joinLobbyBtn.getYPos() + localScaleInt(menuPackingHeight) + sDMenuFrame.getImgHeightLocal(loadGameBtn.getBaseImage(), this));

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
                } else if (btn.equals(newGameBtn)) {
                    newGameBtnActionPerformed();
                } else if (btn.equals(joinLobbyBtn)) {
                    joinLobbyBtnActionPerformed();
                } else if (btn.equals(loadGameBtn)) {
                    loadGameBtnActionPerformed();
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

    /**
     * Exit this panel and return to the main menu
     */
    @Override
    public void exitBtnActionPerformed() {
        exitBtn.setmouseHover(false);
        sDMenuFrame.switchPanel(this, sDMenuFrame.getSDMainMenuPanel());
    }

    /**
     * Open the JPanel for setting the parameters for a new game
     */
    private void newGameBtnActionPerformed() {
        //set the new game settings to know the new game will be online
        sDMenuFrame.getSDMainMenuPanel().getSDNewGameSettingsPanel().setNewGameOffline(false);

        // Hide this window and show the New Game Settings
        newGameBtn.setmouseHover(false);
        sDMenuFrame.switchPanel(this, sDMenuFrame.getSDMainMenuPanel().getSDNewGameSettingsPanel());
    }

    /**
     * Open the JPanel to allow a user to select a lobby for online play.
     */
    private void joinLobbyBtnActionPerformed() {
        // Hide this window and show the New Game Settings
        joinLobbyBtn.setmouseHover(false);
        sDMenuFrame.switchPanel(this, sDMenuFrame.getSDMainMenuPanel().getSDJoinLobbyPanel());
    }

    /**
     * Prompt the user for a save file and load it to the gamePanel. Once
     * complete the user may select a lobby.
     */
    private void loadGameBtnActionPerformed() {
        //=-=-=-=-=-=-=-=-=Save file Start=-=-=-=-=-=-=-=-=
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

                //reset the game                            
                sDMenuFrame.getSDMainMenuPanel().getGameFrame().resetGamePanel();

                //check if it is a vailid game save
                if (!scanner.nextLine().equals("SettlersOfCatanSave" + Catan.SAVE_FILE_VER)) {
                    JOptionPane.showMessageDialog(null, "The selected file is not a Settlers of Catan " + Catan.SAVE_FILE_VER + " save file.", "Loading Error", JOptionPane.ERROR_MESSAGE);
                } else { //if it is a real save file
                    //check if the next line hold the player count
                    if (scanner.nextLine().equals("playerCount:")) {
                        //set the player count
                        GamePanel.setPlayerCount(Integer.parseInt(scanner.nextLine()));
                        sDMenuFrame.getSDMainMenuPanel().getGameFrame().resetGamePanel();

                        sDMenuFrame.getSDMainMenuPanel().getGameFrame().loadFromFile(saveFileLoader.getSelectedFile().getPath());

                        //let the lobby selector to know not to reset certain Objects
                        sDMenuFrame.getSDMainMenuPanel().getSDJoinLobbyPanel().setJustMadeNewGame(true);

                        //switch over to the lobby selection. Enforce needing an empty lobby
                        sDMenuFrame.switchPanel(this, sDMenuFrame.getSDMainMenuPanel().getSDJoinLobbyPanel());
                    } else {
                        JOptionPane.showMessageDialog(null, "The selected file does not contain the required player count data.", "Loading Error", JOptionPane.ERROR_MESSAGE);
                    }
                }

            } catch (FileNotFoundException e) {
                JOptionPane.showMessageDialog(null, "There was an error loading the save file:\n" + e, "Loading Error", JOptionPane.ERROR_MESSAGE);
            }

        } else { //if there was so file selected
            JOptionPane.showMessageDialog(null, "There was no file selected.", "Loading Error", JOptionPane.ERROR_MESSAGE);
        }
        //=-=-=-=-=-=-=-=-=Save file End=-=-=-=-=-=-=-=-=
    }

    @Override
    public int getLocalImgWidth(Image image) {
        return sDMenuFrame.getImgWidthLocal(image, this);
    }

    @Override
    public int getLocalImgHeight(Image image) {
        return sDMenuFrame.getImgHeightLocal(image, this);
    }
}
