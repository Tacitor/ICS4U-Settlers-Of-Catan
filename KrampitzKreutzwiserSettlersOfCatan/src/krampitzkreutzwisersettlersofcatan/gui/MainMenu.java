/*
 * Evan Kreutzwiser & Lukas Krampiz ProjectManagementProject
 * Nov 06, 2020
 * The main menu for the game Settlers of Catan
 */
package krampitzkreutzwisersettlersofcatan.gui;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import krampitzkreutzwisersettlersofcatan.Catan;
import textures.ImageRef;

/**
 *
 * @author Tacitor
 * @author Evan
 */
public class MainMenu extends javax.swing.JFrame {

    private final UserManualUI userManualUIFrame; //referance to the user manual
    private final CreditsUI creditsUIFrame; //referance to the user credits JFrame
    private final GameFrame gameJFrame; //ref to the game JFrame
    private final NewGameSettings newGameSettingsFrame;
    private final ClientSettings clientSettings;
    private NewOnlineGameMenu newOnlineGameMenu;
    private JoinOnlineGameMenu joinOnlineGameMenu;
    private LoadOnlineGameMenu loadOnlineGameMenu;

    /**
     * Creates new form MainMenu
     */
    public MainMenu() {

        initComponents();
        
        this.setLocationRelativeTo(null);

        setIcon();

        userManualUIFrame = new UserManualUI(this);
        creditsUIFrame = new CreditsUI(this);
        gameJFrame = new GameFrame(this);
        clientSettings = new ClientSettings(this);
        newGameSettingsFrame = new NewGameSettings(this, gameJFrame, newOnlineGameMenu);
        loadOnlineGameMenu = new LoadOnlineGameMenu(this);
        newOnlineGameMenu = new NewOnlineGameMenu(this);
        
        //set the game version
        gameVerLbl.setText(Catan.GAME_VER);

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        titleLbl = new javax.swing.JLabel();
        exitBtn = new javax.swing.JButton();
        newGameBtn = new javax.swing.JButton();
        loadGameBtn = new javax.swing.JButton();
        creditsBtn = new javax.swing.JButton();
        rulesBtn = new javax.swing.JButton();
        loadAutosaveBtn = new javax.swing.JButton();
        optionBtn = new javax.swing.JButton();
        joinOnlineBtn = new javax.swing.JButton();
        loadToOnlineBtn = new javax.swing.JButton();
        gameVerLbl = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Settlers of Catan - ICS4U Edition");
        setResizable(false);

        titleLbl.setFont(new java.awt.Font("MV Boli", 0, 24)); // NOI18N
        titleLbl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titleLbl.setText("Settlers of Catan");

        exitBtn.setFont(new java.awt.Font("MV Boli", 0, 16)); // NOI18N
        exitBtn.setText("Exit");
        exitBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitBtnActionPerformed(evt);
            }
        });

        newGameBtn.setFont(new java.awt.Font("MV Boli", 0, 16)); // NOI18N
        newGameBtn.setText("New Game");
        newGameBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newGameBtnActionPerformed(evt);
            }
        });

        loadGameBtn.setFont(new java.awt.Font("MV Boli", 0, 16)); // NOI18N
        loadGameBtn.setText("Load Game");
        loadGameBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadGameBtnActionPerformed(evt);
            }
        });

        creditsBtn.setFont(new java.awt.Font("MV Boli", 0, 16)); // NOI18N
        creditsBtn.setText("Credits");
        creditsBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                creditsBtnActionPerformed(evt);
            }
        });

        rulesBtn.setFont(new java.awt.Font("MV Boli", 0, 16)); // NOI18N
        rulesBtn.setText("User Manual");
        rulesBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rulesBtnActionPerformed(evt);
            }
        });

        loadAutosaveBtn.setFont(new java.awt.Font("MV Boli", 0, 16)); // NOI18N
        loadAutosaveBtn.setText("Load Autosave");
        loadAutosaveBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadAutosaveBtnActionPerformed(evt);
            }
        });

        optionBtn.setFont(new java.awt.Font("MV Boli", 0, 16)); // NOI18N
        optionBtn.setText("Options");
        optionBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optionBtnActionPerformed(evt);
            }
        });

        joinOnlineBtn.setFont(new java.awt.Font("MV Boli", 0, 16)); // NOI18N
        joinOnlineBtn.setText("Join Online Game");
        joinOnlineBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                joinOnlineBtnActionPerformed(evt);
            }
        });

        loadToOnlineBtn.setFont(new java.awt.Font("MV Boli", 0, 16)); // NOI18N
        loadToOnlineBtn.setText("Load Game to Online Mode");
        loadToOnlineBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadToOnlineBtnActionPerformed(evt);
            }
        });

        gameVerLbl.setText("GAME VERSION");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(titleLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 473, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(loadAutosaveBtn, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(joinOnlineBtn, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 241, Short.MAX_VALUE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(loadGameBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(loadToOnlineBtn)))
                            .addComponent(newGameBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(optionBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(creditsBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(rulesBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(exitBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(gameVerLbl)))
                .addContainerGap(40, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(titleLbl)
                .addGap(18, 18, 18)
                .addComponent(newGameBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(loadAutosaveBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(loadGameBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(joinOnlineBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(loadToOnlineBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(optionBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(creditsBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(rulesBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(exitBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(gameVerLbl)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void exitBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitBtnActionPerformed
        System.exit(0); //close the program with 0 errors/normal conditions
    }//GEN-LAST:event_exitBtnActionPerformed

    private void newGameBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newGameBtnActionPerformed
        // Hide this window and show the settings
        this.setVisible(false);
        newGameSettingsFrame.setVisible(true);
        //gameJFrame.resetGamePanel();
        //gameJFrame.setVisible(true);
    }//GEN-LAST:event_newGameBtnActionPerformed

    private void loadGameBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadGameBtnActionPerformed
        JFileChooser saveFileLoader = new JFileChooser(); //make a new file chooser

        //create a filter for catan save files
        FileFilter catanSaveFile = new FileFilter() {
            //add the description
            public String getDescription() {
                return "Catan Save File (*.catan)";
            }

            //add the logic for the filter
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
    }//GEN-LAST:event_loadGameBtnActionPerformed

    private void creditsBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_creditsBtnActionPerformed
        // Hide this window and show the credits
        this.setVisible(false);
        creditsUIFrame.setVisible(true);
    }//GEN-LAST:event_creditsBtnActionPerformed

    private void rulesBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rulesBtnActionPerformed
        // Hide this window and show the user manual
        this.setVisible(false);
        userManualUIFrame.setVisible(true);
    }//GEN-LAST:event_rulesBtnActionPerformed

    private void loadAutosaveBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadAutosaveBtnActionPerformed
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
    }//GEN-LAST:event_loadAutosaveBtnActionPerformed

    private void optionBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optionBtnActionPerformed
        // Hide this window and show the settings
        this.setVisible(false);
        clientSettings.setVisible(true);
    }//GEN-LAST:event_optionBtnActionPerformed

    private void joinOnlineBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_joinOnlineBtnActionPerformed
        //create a new game joining window
        joinOnlineGameMenu = new JoinOnlineGameMenu(this);
        joinOnlineGameMenu.setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_joinOnlineBtnActionPerformed

    private void loadToOnlineBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadToOnlineBtnActionPerformed
        //make a new loading from a save file window
        loadOnlineGameMenu = new LoadOnlineGameMenu(this);
        loadOnlineGameMenu.setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_loadToOnlineBtnActionPerformed

    /**
     * @param args the command line arguments
     */
    /*
    public static void main(String args[]) {
        /* Set the Windows 10 look and feel */
    //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
    /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
     */
 /*
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
 /*
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainMenu().setVisible(true);
            }
        });

    }
     */
    /**
     * Set the icon for the JFRame
     */
    private void setIcon() {
        this.setIconImage(ImageRef.ICON);
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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton creditsBtn;
    private javax.swing.JButton exitBtn;
    private javax.swing.JLabel gameVerLbl;
    private javax.swing.JButton joinOnlineBtn;
    private javax.swing.JButton loadAutosaveBtn;
    private javax.swing.JButton loadGameBtn;
    private javax.swing.JButton loadToOnlineBtn;
    private javax.swing.JButton newGameBtn;
    private javax.swing.JButton optionBtn;
    private javax.swing.JButton rulesBtn;
    private javax.swing.JLabel titleLbl;
    // End of variables declaration//GEN-END:variables
}
