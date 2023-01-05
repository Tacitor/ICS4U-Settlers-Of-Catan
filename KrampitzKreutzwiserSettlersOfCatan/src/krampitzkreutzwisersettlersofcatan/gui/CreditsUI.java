/*
 * Lukas Krampitz and Evan Kreutwiser
 * Oct 23, 2020, November 4, 2020, and Nov 6 2020
 * Show the credits
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
public class CreditsUI extends javax.swing.JFrame {
    
    private final SDMainMenuPanel mainMenuFrame;

    /**
     * Creates new form CreditsUI
     * @param m The main menu JFrame this returns to on exit
     */
    public CreditsUI(SDMainMenuPanel m) {
        setIcon();
        
        initComponents();

        this.setLocationRelativeTo(null);

        mainMenuFrame = m;
        
        // Load and display the credits file
        loadMaterial();
    }

    /**
     * Read and display the credits in the data file
     */
    public final void loadMaterial() {
        
        // Declare variablesD
        Scanner fileReader;
        InputStream file = OldCode.class.getResourceAsStream("credits.txt");
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
        }
        catch (Exception e) {
            // Set the sring to be displayed to an error message
            fileContents = "Error: credits file could not be read.";
            // Output the jsvs error to the standard output
            System.out.println("Error reading credits file: " + e);
        }
        
        // Display the file's contents from the string
        creditslTxtAr.setText(fileContents);
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

        backBtn = new javax.swing.JButton();
        titleLbl = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        creditslTxtAr = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        backBtn.setText("< Back");
        backBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backBtnActionPerformed(evt);
            }
        });

        titleLbl.setText("Credits:");

        creditslTxtAr.setEditable(false);
        creditslTxtAr.setColumns(20);
        creditslTxtAr.setLineWrap(true);
        creditslTxtAr.setRows(5);
        creditslTxtAr.setText("Read carefully, this is important to play the game:");
        creditslTxtAr.setWrapStyleWord(true);
        creditslTxtAr.setAutoscrolls(false);
        jScrollPane1.setViewportView(creditslTxtAr);

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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(backBtn)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * The event for when the back button is pressed. Returns to the main menu.
     * @param evt The event generated by the button click (Unused)
     */
    private void backBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backBtnActionPerformed
        // Hide this window and show the main menu
        this.setVisible(false);
        mainMenuFrame.setVisible(true);
    }//GEN-LAST:event_backBtnActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton backBtn;
    private javax.swing.JTextArea creditslTxtAr;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel titleLbl;
    // End of variables declaration//GEN-END:variables
}
