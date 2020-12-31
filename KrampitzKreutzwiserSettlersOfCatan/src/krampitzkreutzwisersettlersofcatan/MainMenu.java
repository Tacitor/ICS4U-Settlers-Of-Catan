/*
 * Evan Kreutzwiser & Lukas Krampiz ProjectManagementProject
 * Nov 06, 2020
 * The main menu for the game Settlers of Catan
 */
package krampitzkreutzwisersettlersofcatan;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author Tacitor
 * @author Evan
 */
public class MainMenu extends javax.swing.JFrame {    
    
    private final UserManualUI userManualUIFrame; //referance to the user manual
    private final CreditsUI creditsUIFrame; //referance to the user credits JFrame
    private final GameFrame gameJFrame; //ref to the game JFrame

    /**
     * Creates new form MainMenu
     */
    public MainMenu() {
        initComponents();
        
        userManualUIFrame = new UserManualUI(this);
        creditsUIFrame = new CreditsUI(this);
        gameJFrame = new GameFrame(this);
        
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

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Software Development Life Cycle");
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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(35, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(rulesBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 473, Short.MAX_VALUE)
                    .addComponent(newGameBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(loadGameBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(creditsBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(exitBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 473, Short.MAX_VALUE)
                    .addComponent(titleLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(30, 30, 30))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(titleLbl)
                .addGap(18, 18, 18)
                .addComponent(newGameBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(loadGameBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(creditsBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(rulesBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(exitBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(25, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void exitBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitBtnActionPerformed
        System.exit(0); //close the program with 0 errors/normal conditions
    }//GEN-LAST:event_exitBtnActionPerformed

    private void newGameBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newGameBtnActionPerformed
        // Hide this window and show the game
        this.setVisible(false);
        gameJFrame.resetGamePanel();
        gameJFrame.setVisible(true);
    }//GEN-LAST:event_newGameBtnActionPerformed

    private void loadGameBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadGameBtnActionPerformed
        JFileChooser saveFileLoader = new JFileChooser(); //make a new file chooser
        
        //set up the file choose and call it
        saveFileLoader.setDialogTitle("Select a Save File to Open:");
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
                if (!scanner.nextLine().equals("SettlersOfCatanSaveV4")) {
                    JOptionPane.showMessageDialog(null, "The selected file is not a Settlers of Catan V4 save file.\nA new game was started instead", "Loading Error", JOptionPane.ERROR_MESSAGE);
                } else { //if it is a real save file
                    gameJFrame.loadFromFile(saveFileLoader);
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

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Windows 10 look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
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
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainMenu().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton creditsBtn;
    private javax.swing.JButton exitBtn;
    private javax.swing.JButton loadGameBtn;
    private javax.swing.JButton newGameBtn;
    private javax.swing.JButton rulesBtn;
    private javax.swing.JLabel titleLbl;
    // End of variables declaration//GEN-END:variables
}
