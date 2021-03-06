/*
 * Lukas Krampitz
 * Mar 25 2021
 * Allow the user to select settings that are constant across all games.
 */
package krampitzkreutzwisersettlersofcatan;

import java.awt.Toolkit;
import textures.ImageRef;

/**
 *
 * @author Tacitor
 * @author Evan
 */
public class ClientSettings extends javax.swing.JFrame {

    private final MainMenu mainMenuFrame;

    /**
     * Creates new form CreditsUI
     *
     * @param m The main menu JFrame this returns to on exit
     */
    public ClientSettings(MainMenu m) {
        setIcon();

        initComponents();

        this.setLocationRelativeTo(null);

        mainMenuFrame = m;
    }

    /**
     * Set the icon for the JFRame
     */
    private void setIcon() {
        this.setIconImage(ImageRef.ICON);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        borderGrp = new javax.swing.ButtonGroup();
        windowedGrp = new javax.swing.ButtonGroup();
        backBtn = new javax.swing.JButton();
        saveBtn = new javax.swing.JButton();
        titleLbl = new javax.swing.JLabel();
        boarderLbl = new javax.swing.JLabel();
        boarderFalseRbtn = new javax.swing.JRadioButton();
        boarderTrueRbtn = new javax.swing.JRadioButton();
        boarderLbl1 = new javax.swing.JLabel();
        displayFullRbtn = new javax.swing.JRadioButton();
        displayWindowRbtn = new javax.swing.JRadioButton();
        resCmbBox = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        backBtn.setText("< Back");
        backBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backBtnActionPerformed(evt);
            }
        });

        saveBtn.setFont(new java.awt.Font("MV Boli", 0, 16)); // NOI18N
        saveBtn.setText("Save");
        saveBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveBtnActionPerformed(evt);
            }
        });

        titleLbl.setFont(new java.awt.Font("MV Boli", 0, 24)); // NOI18N
        titleLbl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titleLbl.setText("Game Client Settings");

        boarderLbl.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        boarderLbl.setText("Show Menu boarders:");

        borderGrp.add(boarderFalseRbtn);
        boarderFalseRbtn.setSelected(true);
        boarderFalseRbtn.setText("No");
        boarderFalseRbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boarderFalseRbtnActionPerformed(evt);
            }
        });

        borderGrp.add(boarderTrueRbtn);
        boarderTrueRbtn.setText("Yes");

        boarderLbl1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        boarderLbl1.setText("Display Mode:");

        windowedGrp.add(displayFullRbtn);
        displayFullRbtn.setSelected(true);
        displayFullRbtn.setText("Fullscreen Borderless");
        displayFullRbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                displayFullRbtnActionPerformed(evt);
            }
        });

        windowedGrp.add(displayWindowRbtn);
        displayWindowRbtn.setText("Windowed");
        displayWindowRbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                displayWindowRbtnActionPerformed(evt);
            }
        });

        resCmbBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "3840 x 2160", "1920 x 1080", "1280 x 720", "800 x 600" }));
        resCmbBox.setSelectedIndex(1);
        resCmbBox.setToolTipText("");
        resCmbBox.setEnabled(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(titleLbl, javax.swing.GroupLayout.DEFAULT_SIZE, 526, Short.MAX_VALUE)
                            .addComponent(saveBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(backBtn)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(boarderLbl)
                            .addComponent(boarderFalseRbtn)
                            .addComponent(boarderTrueRbtn))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(boarderLbl1)
                            .addComponent(displayFullRbtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(displayWindowRbtn)
                            .addComponent(resCmbBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(86, 86, 86))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(titleLbl)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(boarderLbl)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(boarderFalseRbtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(boarderTrueRbtn))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(boarderLbl1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(displayFullRbtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(displayWindowRbtn)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(resCmbBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(88, 88, 88)
                .addComponent(saveBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
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

    private void saveBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveBtnActionPerformed
        //get the online play mode

        //get and set the giveStartingResources
        if (boarderTrueRbtn.isSelected()) {
            GamePanel.setShowMenuBoarder(true);
        } else if (boarderFalseRbtn.isSelected()) {
            GamePanel.setShowMenuBoarder(false);
        }

        //get the windowd mode
        if (displayFullRbtn.isSelected()) {
            //fullscreeen
            mainMenuFrame.getGameFrame().setVisible(false);
            mainMenuFrame.getGameFrame().dispose();
            mainMenuFrame.getGameFrame().setSize(Toolkit.getDefaultToolkit().getScreenSize());
            mainMenuFrame.getGameFrame().setLocationRelativeTo(null);
            mainMenuFrame.getGameFrame().setUndecorated(true);
        } else if (displayWindowRbtn.isSelected()) {
            //get the specific res
            String resString = resCmbBox.getItemAt(resCmbBox.getSelectedIndex());
            String resWidth = resString.substring(0, resString.indexOf(" x"));
            String resHight = resString.substring(resString.indexOf("x ") + 2);

            mainMenuFrame.getGameFrame().setVisible(false);
            //set decorated
            if (mainMenuFrame.getGameFrame().isUndecorated()) {
                mainMenuFrame.getGameFrame().dispose();
                mainMenuFrame.getGameFrame().setUndecorated(false);
            }
            //set size
            mainMenuFrame.getGameFrame().setSize(Integer.parseInt(resWidth), Integer.parseInt(resHight));
        }

    }//GEN-LAST:event_saveBtnActionPerformed

    private void boarderFalseRbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_boarderFalseRbtnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_boarderFalseRbtnActionPerformed

    private void displayFullRbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_displayFullRbtnActionPerformed
        //Disable the combo box
        resCmbBox.setEnabled(false);
    }//GEN-LAST:event_displayFullRbtnActionPerformed

    private void displayWindowRbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_displayWindowRbtnActionPerformed
        //enable the combo box
        resCmbBox.setEnabled(true);
    }//GEN-LAST:event_displayWindowRbtnActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton backBtn;
    private javax.swing.JRadioButton boarderFalseRbtn;
    private javax.swing.JLabel boarderLbl;
    private javax.swing.JLabel boarderLbl1;
    private javax.swing.JRadioButton boarderTrueRbtn;
    private javax.swing.ButtonGroup borderGrp;
    private javax.swing.JRadioButton displayFullRbtn;
    private javax.swing.JRadioButton displayWindowRbtn;
    private javax.swing.JComboBox<String> resCmbBox;
    private javax.swing.JButton saveBtn;
    private javax.swing.JLabel titleLbl;
    private javax.swing.ButtonGroup windowedGrp;
    // End of variables declaration//GEN-END:variables
}
