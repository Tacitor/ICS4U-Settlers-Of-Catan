/*
 * Lukas Krampitz
 * Mar 25 2021
 * Allow the user to select settings that are constant across all games.
 */
package krampitzkreutzwisersettlersofcatan;

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
    
    private int portNum = 25570;

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
        
        /*
         * TODO
         * 
         * Auto send the save file to the server when all clients have connected.
         * Add option to the main menu to join a server
         */
        
    }

    /**
     * Create the local server
     */
    private void serverStartUp() {
        server = new CatanServer(GamePanel.getPlayerCount(), portNum);

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
        //create the new client and request to be the red player
        client = new CatanClient(700, 200, "localhost", mainMenuFrame.getGameFrame(), portNum);
        client.connectToServer();
        client.setUpGUI();
        client.setUpButton();
        
        //request the player colour
        client.requestColour(1); //request the red player
        
        //wait for the response to come through
        while (client.getClientColour() == 0) {
            //while there is no assinged colour do nothing and just wait
        }
        
        //once the client has been set up save it to the game panel
        GamePanel.setOnlineMode(client.getClientColour());
        GamePanel.setCatanClient(client);

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
        createBtn = new javax.swing.JButton();
        portLbl = new javax.swing.JLabel();
        portTxtFld = new javax.swing.JTextField();

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
        jTextArea1.setText("Select a port to create your game server.\n\nYou have created a new Online Catan Game! A server has been started on you local machine and the first client has automatically connected you as player 1 (Red). For the other players to connect you must open port 25570 to you local machine. Then the players can connect over your IP using port 25570.");
        jTextArea1.setWrapStyleWord(true);
        jTextArea1.setEnabled(false);
        jTextArea1.setOpaque(false);
        jScrollPane1.setViewportView(jTextArea1);

        createBtn.setFont(new java.awt.Font("MV Boli", 0, 16)); // NOI18N
        createBtn.setText("Create Server");
        createBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createBtnActionPerformed(evt);
            }
        });

        portLbl.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        portLbl.setText("Catan Game Server Port: (25570 recomended)");

        portTxtFld.setText("25570");
        portTxtFld.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                portTxtFldActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(backBtn)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(titleLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(portLbl, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                            .addComponent(portTxtFld, javax.swing.GroupLayout.PREFERRED_SIZE, 266, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(createBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1))
                .addGap(10, 10, 10))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(titleLbl)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(createBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(portLbl)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(portTxtFld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
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

    private void createBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createBtnActionPerformed

        boolean validInput = false;
        String input = portTxtFld.getText();
        
        //make sure the input is good
        //if a blank feild
        if (!input.equals("")) {
            
            //if the feild is not blank check if it's and integer
            try {
                int portNum = Integer.parseInt(input);
                
                //make sure no important ports
                if (portNum != 80 && portNum != 443) {
                    
                    this.portNum = portNum;
                    runSetup();
                } else {
                    createBtn.setText("No HTTP port! Try again");
                }
                
            } catch (NumberFormatException e) {
                createBtn.setText("No port num! Try again");
            }
            
        } else {
            createBtn.setText("Must have port Num! Try again");
        }

    }//GEN-LAST:event_createBtnActionPerformed

    private void portTxtFldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_portTxtFldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_portTxtFldActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton backBtn;
    private javax.swing.ButtonGroup borderGrp;
    private javax.swing.JButton createBtn;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JLabel portLbl;
    private javax.swing.JTextField portTxtFld;
    private javax.swing.JLabel titleLbl;
    private javax.swing.ButtonGroup windowedGrp;
    // End of variables declaration//GEN-END:variables

}
