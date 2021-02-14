/*
 * Lukas Krampitz and Evan Kreutwiser
 * Oct 23, 2020, November 4, 2020, and Nov 6 2020
 * Show the credits
 */
package krampitzkreutzwisersettlersofcatan;

/**
 *
 * @author Tacitor
 * @author Evan
 */
public class NewGameSettings extends javax.swing.JFrame {

    private final MainMenu mainMenuFrame;
    private final GameFrame gameFrame;

    /**
     * Creates new form CreditsUI
     *
     * @param m The main menu JFrame this returns to on exit
     */
    public NewGameSettings(MainMenu m, GameFrame g) {
        initComponents();
        mainMenuFrame = m;
        gameFrame = g;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        playerNumGrp = new javax.swing.ButtonGroup();
        onlineModeGrp = new javax.swing.ButtonGroup();
        startResGrp = new javax.swing.ButtonGroup();
        startResourcesTxtAr1 = new javax.swing.JTextArea();
        backBtn = new javax.swing.JButton();
        startGameBtn = new javax.swing.JButton();
        titleLbl = new javax.swing.JLabel();
        playerNumLbl = new javax.swing.JLabel();
        playerNum2Rbtn = new javax.swing.JRadioButton();
        playerNum4Rbtn = new javax.swing.JRadioButton();
        playerNum3Rbtn = new javax.swing.JRadioButton();
        onlinLbl = new javax.swing.JLabel();
        onlineFalseRbtn = new javax.swing.JRadioButton();
        onlineTrueRbtn = new javax.swing.JRadioButton();
        startResourcesLbl = new javax.swing.JLabel();
        startResourcesTxtAr = new javax.swing.JTextArea();
        startResFalseRbtn = new javax.swing.JRadioButton();
        startResTrueRbtn = new javax.swing.JRadioButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        startResourcesTxtAr1.setBackground(new java.awt.Color(240, 240, 240));
        startResourcesTxtAr1.setColumns(20);
        startResourcesTxtAr1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        startResourcesTxtAr1.setLineWrap(true);
        startResourcesTxtAr1.setRows(5);
        startResourcesTxtAr1.setText("Have a house rule you want added as an option? Add and issue to the GitHub or find any other way you want to contact me.\n");
        startResourcesTxtAr1.setWrapStyleWord(true);
        startResourcesTxtAr1.setEnabled(false);

        backBtn.setText("< Back");
        backBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backBtnActionPerformed(evt);
            }
        });

        startGameBtn.setFont(new java.awt.Font("MV Boli", 0, 16)); // NOI18N
        startGameBtn.setText("Start Game");
        startGameBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startGameBtnActionPerformed(evt);
            }
        });

        titleLbl.setFont(new java.awt.Font("MV Boli", 0, 24)); // NOI18N
        titleLbl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titleLbl.setText("New Game Settings");

        playerNumLbl.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        playerNumLbl.setText("Number of players:");

        playerNumGrp.add(playerNum2Rbtn);
        playerNum2Rbtn.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        playerNum2Rbtn.setSelected(true);
        playerNum2Rbtn.setText("2");

        playerNumGrp.add(playerNum4Rbtn);
        playerNum4Rbtn.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        playerNum4Rbtn.setText("4");

        playerNumGrp.add(playerNum3Rbtn);
        playerNum3Rbtn.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        playerNum3Rbtn.setText("3");

        onlinLbl.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        onlinLbl.setText("Multiplayer Mode:");

        onlineModeGrp.add(onlineFalseRbtn);
        onlineFalseRbtn.setSelected(true);
        onlineFalseRbtn.setText("Local Shared Machine");

        onlineModeGrp.add(onlineTrueRbtn);
        onlineTrueRbtn.setText("Online");
        onlineTrueRbtn.setEnabled(false);

        startResourcesLbl.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        startResourcesLbl.setText("Starting Resources:");

        startResourcesTxtAr.setBackground(new java.awt.Color(240, 240, 240));
        startResourcesTxtAr.setColumns(20);
        startResourcesTxtAr.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        startResourcesTxtAr.setLineWrap(true);
        startResourcesTxtAr.setRows(5);
        startResourcesTxtAr.setText("Give each player one harvest from each conected hex for each settlement. Only once on startup.");
        startResourcesTxtAr.setWrapStyleWord(true);
        startResourcesTxtAr.setEnabled(false);

        startResGrp.add(startResFalseRbtn);
        startResFalseRbtn.setText("No");

        startResGrp.add(startResTrueRbtn);
        startResTrueRbtn.setSelected(true);
        startResTrueRbtn.setText("Yes");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(backBtn)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(titleLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(startGameBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(playerNumLbl)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(playerNum2Rbtn)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(playerNum3Rbtn)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(playerNum4Rbtn))
                            .addComponent(startResourcesLbl)
                            .addComponent(startResourcesTxtAr, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(startResTrueRbtn)
                                .addGap(10, 10, 10)
                                .addComponent(startResFalseRbtn)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 89, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(onlinLbl)
                            .addComponent(onlineFalseRbtn)
                            .addComponent(onlineTrueRbtn)
                            .addComponent(startResourcesTxtAr1, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(47, 47, 47))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(titleLbl)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(playerNumLbl)
                    .addComponent(onlinLbl))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(playerNum2Rbtn)
                    .addComponent(playerNum3Rbtn)
                    .addComponent(playerNum4Rbtn)
                    .addComponent(onlineFalseRbtn))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(onlineTrueRbtn)
                .addGap(5, 5, 5)
                .addComponent(startResourcesLbl)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(startResourcesTxtAr1, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(startResourcesTxtAr, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(startResTrueRbtn)
                            .addComponent(startResFalseRbtn))))
                .addGap(13, 13, 13)
                .addComponent(startGameBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(backBtn)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * The event for when the back button is pressed. Returns to the main menu.
     *
     * @param evt The event generated by the button click (Unused)
     */
    private void backBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backBtnActionPerformed
        // Hide this window and show the main menu
        this.setVisible(false);
        mainMenuFrame.setVisible(true);
    }//GEN-LAST:event_backBtnActionPerformed

    private void startGameBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startGameBtnActionPerformed
        //get the online play mode
        if (onlineFalseRbtn.isSelected()) {
            
            //get and set the player number selected
            if (playerNum2Rbtn.isSelected()) {
                GamePanel.setPlayerCount(2);
            } else if (playerNum3Rbtn.isSelected()) {
                GamePanel.setPlayerCount(3);
            } else if (playerNum4Rbtn.isSelected()) {
                GamePanel.setPlayerCount(4);
            }
            
            //get and set the giveStartingResources
            if (startResTrueRbtn.isSelected()) {
                GamePanel.setgiveStartingResources(true);
            } else if (startResFalseRbtn.isSelected()) {
                GamePanel.setgiveStartingResources(false);
            }
            
            
        }

        // Hide this window and show the game
        this.setVisible(false);
        gameFrame.resetGamePanel();
        gameFrame.setVisible(true);
    }//GEN-LAST:event_startGameBtnActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton backBtn;
    private javax.swing.JLabel onlinLbl;
    private javax.swing.JRadioButton onlineFalseRbtn;
    private javax.swing.ButtonGroup onlineModeGrp;
    private javax.swing.JRadioButton onlineTrueRbtn;
    private javax.swing.JRadioButton playerNum2Rbtn;
    private javax.swing.JRadioButton playerNum3Rbtn;
    private javax.swing.JRadioButton playerNum4Rbtn;
    private javax.swing.ButtonGroup playerNumGrp;
    private javax.swing.JLabel playerNumLbl;
    private javax.swing.JButton startGameBtn;
    private javax.swing.JRadioButton startResFalseRbtn;
    private javax.swing.ButtonGroup startResGrp;
    private javax.swing.JRadioButton startResTrueRbtn;
    private javax.swing.JLabel startResourcesLbl;
    private javax.swing.JTextArea startResourcesTxtAr;
    private javax.swing.JTextArea startResourcesTxtAr1;
    private javax.swing.JLabel titleLbl;
    // End of variables declaration//GEN-END:variables
}
