/*
 * Lukas Krampitz and Evan Kreutwiser
 * Oct 23, 2020, November 4, 2020, and Nov 6 2020
 * Show the User manual so that the usercan play the game
 */
package krampitzkreutzwisersettlersofcatan.gui;

import dataFiles.OldCode;
import java.io.InputStream; // File reference
import java.util.Scanner; // File reading
import textures.ImageRef;

/**
 *
 * @author Tacitor
 * @author Evan
 */
@Deprecated
public class UserManualUI extends javax.swing.JFrame {

    private final SDMainMenuPanel mainMenuFrame;

    /**
     * Creates new frame UserManualUI
     *
     * @param m The main menu JFrame this returns to on exit
     */
    public UserManualUI(SDMainMenuPanel m) {
        setIcon();

        initComponents();

        this.setLocationRelativeTo(null);

        mainMenuFrame = m;

        // Load and display the User Manual file
        loadMaterial();
    }

    /**
     * Set the icon for the JFRame
     */
    private void setIcon() {
        this.setIconImage(ImageRef.ICON);
    }

    /**
     * Read and display the user manual in the data file
     */
    public final void loadMaterial() {

        // Declare variables
        Scanner fileReader;
        InputStream file = OldCode.class.getResourceAsStream("userManual.txt");
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
            fileContents = "Error: User manual file could not be read.";
            // Output the jsvs error to the standard output
            System.out.println("Error reading User manual file: " + e);
        }

        // Display the file's contents from the string
        userManualTxtAr.setText(fileContents);

        // Move the invisble caret to the begining of the text to scroll to the top
        userManualTxtAr.setCaretPosition(0);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        backBtn = new javax.swing.JButton();
        titleLbl = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        userManualTxtAr = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        backBtn.setText("< Back");
        backBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backBtnActionPerformed(evt);
            }
        });

        titleLbl.setText("User Manual:");

        userManualTxtAr.setEditable(false);
        userManualTxtAr.setColumns(20);
        userManualTxtAr.setLineWrap(true);
        userManualTxtAr.setRows(5);
        userManualTxtAr.setText("Read carefully, this is important to play the game:");
        userManualTxtAr.setWrapStyleWord(true);
        userManualTxtAr.setAutoscrolls(false);
        jScrollPane1.setViewportView(userManualTxtAr);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 526, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(backBtn)
                            .addComponent(titleLbl))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(titleLbl)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 392, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton backBtn;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel titleLbl;
    private javax.swing.JTextArea userManualTxtAr;
    // End of variables declaration//GEN-END:variables
}
