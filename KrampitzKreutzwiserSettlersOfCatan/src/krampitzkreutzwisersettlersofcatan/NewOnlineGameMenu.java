/*
 * Lukas Krampitz
 * Mar 25 2021
 * Allow the user to select settings that are constant across all games.
 */
package krampitzkreutzwisersettlersofcatan;

import javax.swing.JOptionPane;
import textures.ImageRef;

/**
 *
 * @author Tacitor
 * @author Evan
 */
public class NewOnlineGameMenu extends javax.swing.JFrame {

    private final MainMenu mainMenuFrame;
    private CatanServer server;
    private CatanClient client;

    /**
     * Creates new form CreditsUI
     *
     * @param m The main menu JFrame this returns to on exit
     */
    public NewOnlineGameMenu(MainMenu m) {
        setIcon();

        initComponents();
        mainMenuFrame = m;

    }

    /**
     * Sets everything up for other player to join over a network
     */
    public void runSetup() {
        serverStartUp();
        createFirstClient();
    }

    /**
     * Create the local server
     */
    private void serverStartUp() {
        server = new CatanServer(GamePanel.getPlayerCount());

        //create a new thread for the server
        Thread t = new Thread(() -> {
            server.acceptConnections();
        });

        //start running the server
        t.start();
    }

    /**
     * Creates a client to connect to the local server
     */
    private void createFirstClient() {

        client = new CatanClient(700, 200, "localhost", mainMenuFrame.getGameFrame());
        client.connectToServer();
        client.setUpGUI();
        client.setUpButton();

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
        titleLbl = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        backBtn.setText("< Back");
        backBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backBtnActionPerformed(evt);
            }
        });

        titleLbl.setFont(new java.awt.Font("MV Boli", 0, 24)); // NOI18N
        titleLbl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titleLbl.setText("New Online Game");

        jTextArea1.setColumns(20);
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jTextArea1.setText("You have created a new Online Catan Game! A server has been started on you local machine and the first client has automatically connected you as player 1 (Red). For the other players to connect you must open port 25570 to you local machine. Then the players can connect over your IP using port 25570.");
        jTextArea1.setWrapStyleWord(true);
        jScrollPane1.setViewportView(jTextArea1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(titleLbl, javax.swing.GroupLayout.DEFAULT_SIZE, 615, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(backBtn)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(titleLbl)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(106, 106, 106)
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
    private javax.swing.ButtonGroup borderGrp;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JLabel titleLbl;
    private javax.swing.ButtonGroup windowedGrp;
    // End of variables declaration//GEN-END:variables

}
