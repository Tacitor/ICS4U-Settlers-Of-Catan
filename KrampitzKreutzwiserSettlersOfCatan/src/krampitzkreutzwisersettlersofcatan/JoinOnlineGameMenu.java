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
public class JoinOnlineGameMenu extends javax.swing.JFrame {

    private final MainMenu mainMenuFrame;
    private CatanServer server;
    private CatanClient client;
    private int failCounter; //keeps track of how many failed connection attempts there have been

    /**
     * Creates new form CreditsUI
     *
     * @param m The main menu JFrame this returns to on exit
     */
    public JoinOnlineGameMenu(MainMenu m) {
        setIcon();

        initComponents();

        this.setLocationRelativeTo(null);

        mainMenuFrame = m;

        //no failed attempts yet
        failCounter = 0;

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

        colourSelectBtnGrp = new javax.swing.ButtonGroup();
        backBtn = new javax.swing.JButton();
        connectBtn = new javax.swing.JButton();
        titleLbl = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        ipLbl = new javax.swing.JLabel();
        ipTxtFld = new javax.swing.JTextField();
        portTxtFld = new javax.swing.JTextField();
        portLbl = new javax.swing.JLabel();
        colourSelectLbl = new javax.swing.JLabel();
        colourRedRbtn = new javax.swing.JRadioButton();
        colourBlueRbtn = new javax.swing.JRadioButton();
        colourOrangeRbtn = new javax.swing.JRadioButton();
        colourWhiteRbtn = new javax.swing.JRadioButton();
        requestColourBtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        backBtn.setText("< Back");
        backBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backBtnActionPerformed(evt);
            }
        });

        connectBtn.setFont(new java.awt.Font("MV Boli", 0, 16)); // NOI18N
        connectBtn.setText("Connect");
        connectBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connectBtnActionPerformed(evt);
            }
        });

        titleLbl.setFont(new java.awt.Font("MV Boli", 0, 24)); // NOI18N
        titleLbl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titleLbl.setText("Join Online Game Server");

        jTextArea1.setColumns(20);
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jTextArea1.setText("To join an online game please ensure the folowing:\n 1) You have a connection to the Internet\n 2) You have another player hosting the server on their local machine and they also have a connection to the Internet.\n     2.5) This player must also port forward port number 25570 using TCP\n\nThen connect to the Catan server using the public IP of the host and the port 25570.\n\nOnce all the players have connected the game will begin.");
        jTextArea1.setWrapStyleWord(true);
        jTextArea1.setEnabled(false);
        jTextArea1.setOpaque(false);
        jScrollPane1.setViewportView(jTextArea1);

        ipLbl.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        ipLbl.setText("Destination IP Address or server URL:");

        ipTxtFld.setText("donau.ca");
        ipTxtFld.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ipTxtFldActionPerformed(evt);
            }
        });

        portTxtFld.setText("25570");
        portTxtFld.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                portTxtFldActionPerformed(evt);
            }
        });

        portLbl.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        portLbl.setText("Connection port:");

        colourSelectLbl.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        colourSelectLbl.setText("Select the colour you would like to play as:");

        colourSelectBtnGrp.add(colourRedRbtn);
        colourRedRbtn.setSelected(true);
        colourRedRbtn.setText("Red");
        colourRedRbtn.setEnabled(false);

        colourSelectBtnGrp.add(colourBlueRbtn);
        colourBlueRbtn.setText("Blue");
        colourBlueRbtn.setEnabled(false);

        colourSelectBtnGrp.add(colourOrangeRbtn);
        colourOrangeRbtn.setText("Orange");
        colourOrangeRbtn.setEnabled(false);

        colourSelectBtnGrp.add(colourWhiteRbtn);
        colourWhiteRbtn.setText("White");
        colourWhiteRbtn.setEnabled(false);

        requestColourBtn.setFont(new java.awt.Font("MV Boli", 0, 16)); // NOI18N
        requestColourBtn.setText("Request Colour");
        requestColourBtn.setEnabled(false);
        requestColourBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                requestColourBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(titleLbl, javax.swing.GroupLayout.DEFAULT_SIZE, 615, Short.MAX_VALUE)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(ipLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(ipTxtFld))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(portLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(portTxtFld, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(connectBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 293, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(colourSelectLbl)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(colourRedRbtn)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(colourBlueRbtn)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(colourOrangeRbtn)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(colourWhiteRbtn))
                            .addComponent(backBtn))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(requestColourBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 293, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(titleLbl)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(ipLbl)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ipTxtFld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(portLbl)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(portTxtFld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(connectBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(colourSelectLbl)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(colourRedRbtn)
                            .addComponent(colourBlueRbtn)
                            .addComponent(colourOrangeRbtn)
                            .addComponent(colourWhiteRbtn))
                        .addGap(18, 18, 18)
                        .addComponent(backBtn))
                    .addComponent(requestColourBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

    private void ipTxtFldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ipTxtFldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ipTxtFldActionPerformed

    private void connectBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_connectBtnActionPerformed

        connectBtn.setEnabled(false);
        connectBtn.setText("Connecting...");

        FindServerRunnable findServerRunnable = new FindServerRunnable();
        findServerRunnable.setDaemon(true);
        findServerRunnable.start();


    }//GEN-LAST:event_connectBtnActionPerformed

    private void portTxtFldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_portTxtFldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_portTxtFldActionPerformed

    private void requestColourBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_requestColourBtnActionPerformed
        requestColourBtn.setText("Requesting...");

        int colourRequest; //store the colour to request

        //get the colour the user wants
        if (colourRedRbtn.isSelected()) {
            colourRequest = 1; //request red
        } else if (colourBlueRbtn.isSelected()) {
            colourRequest = 2; //request blue
        } else if (colourOrangeRbtn.isSelected()) {
            colourRequest = 3; //request orange
        } else if (colourWhiteRbtn.isSelected()) {
            colourRequest = 4; //request white
        } else {
            colourRequest = 0; //default to what every the server want to give me
        }

        //request the player colour
        client.requestColour(colourRequest); //request any colour

        //System.out.println("coluour: " + client.getClientColour());
        //wait for the response to come through
        while (client.getClientColour() == 0) {
            try {
                //while there is no assinged colour do nothing and just wait
                //System.out.println("coluour still: " + client.getClientColour());
                Thread.sleep(200);
            } catch (InterruptedException ex) {
                System.out.println("Error requesing colour in JoinOnlineGameMenu");
            }
        }

        //if the colour request was successfule tell the game
        if (client.getClientColour() == colourRequest) {

            //once the client has been set up save it to the game panel
            GamePanel.setOnlineMode(client.getClientColour());

            requestColourBtn.setText("Done");
            requestColourBtn.setEnabled(false);

            //now hide this window
            this.setVisible(false);
        } else { //the the user that it failed
            requestColourBtn.setText("Not available. Try Again");
        }
    }//GEN-LAST:event_requestColourBtnActionPerformed

    private void findServer() {
        if (client == null) {
            //create a new client and don't request any colour
            client = new CatanClient(700, 200, "localhost", mainMenuFrame.getGameFrame(), 25570);
        }
        //create a CatanClient and connect to the ip specified
        try {

            String input = portTxtFld.getText();

            //=-=-=-=-=-=-=-=-=-=-=Port Check Start=-=-=-=-=-=-=-=-=-=-=
            //make sure the port input is good
            //if a blank feild
            if (!input.equals("")) {

                //if the feild is not blank check if it's and integer
                try {
                    int portNum = Integer.parseInt(input);

                    //make sure no important ports
                    if (portNum != 80 && portNum != 443) {

                        client.setPort(portNum);
                    } else {
                        connectBtn.setText("No HTTP port! Try again");
                    }

                } catch (NumberFormatException e) {
                    connectBtn.setText("No port num! Try again");
                }

            } else {
                connectBtn.setText("Must have port Num! Try again");
            }

            //=-=-=-=-=-=-=-=-=-=-=Port Check End=-=-=-=-=-=-=-=-=-=-=
            //update the ip
            client.setIp(ipTxtFld.getText());

            //try to connect
            boolean succesfulConnect = client.connectToServer();

            if (succesfulConnect) {
                connectBtn.setText("Connection Success");

                int colour;

                client.setUpGUI();
                client.setUpButton();

                //save the client and the max number of players now because the colour request could finish first
                GamePanel.setCatanClient(client);
                GamePanel.setPlayerCount(client.getMaxClients());

                //enable all the colour buttons
                colourBlueRbtn.setEnabled(true);
                colourOrangeRbtn.setEnabled(true);
                colourRedRbtn.setEnabled(true);
                colourWhiteRbtn.setEnabled(true);
                requestColourBtn.setEnabled(true);

            } else {
                //count a fail
                failCounter++;

                connectBtn.setText("Failed (" + failCounter + ")! Try again");
                connectBtn.setEnabled(true);
            }
        } catch (Exception e) {
            System.out.println("Error connecting to server: \n" + e);
        }
    }

    private class FindServerRunnable extends Thread implements Runnable {

        private boolean stopRequested = false;

        public synchronized void requestStop() {
            stopRequested = true;
        }

        @Override
        public void run() {
            //debug the life of the thread and how long it lives for
            //System.out.println("Started connectio attempt");

            //check if this thread should stop
            while (!stopRequested) {
                //try to connect
                findServer();
                //only run once
                stopRequested = true;
            }

            //System.out.println("done connection attempt");
        }

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton backBtn;
    private javax.swing.JRadioButton colourBlueRbtn;
    private javax.swing.JRadioButton colourOrangeRbtn;
    private javax.swing.JRadioButton colourRedRbtn;
    private javax.swing.ButtonGroup colourSelectBtnGrp;
    private javax.swing.JLabel colourSelectLbl;
    private javax.swing.JRadioButton colourWhiteRbtn;
    private javax.swing.JButton connectBtn;
    private javax.swing.JLabel ipLbl;
    private javax.swing.JTextField ipTxtFld;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JLabel portLbl;
    private javax.swing.JTextField portTxtFld;
    private javax.swing.JButton requestColourBtn;
    private javax.swing.JLabel titleLbl;
    // End of variables declaration//GEN-END:variables

}
