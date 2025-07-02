/*
 * Lukas Krampitz
 * Mar 27, 2021
 * 
 */
package krampitzkreutzwisersettlersofcatan.sockets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import javax.swing.*;
import krampitzkreutzwisersettlersofcatan.gui.GameFrame;
import textures.ImageRef;

/**
 *
 * @author Tacitor
 */
public class CatanClient extends JFrame {

    public static final String ONLINE_SAVE_LOCATION = System.getProperty("user.home")
            + File.separator + "AppData" + File.separator + "Roaming" + File.separator + "SettlerDevs" + File.separator + "Catan";
    public static final String ONLINE_SAVE_NAME = File.separator + "latestOnline";
    public static final String ONLINE_SAVE_TYPE = ".catan";
    ;

    private int clientID;
    private int clientColour; //the colour of the player the client will be speaking for. Often but not always the same as the client ID. 0 for no request and 1-4 for that colour
    private int totalClientNum; //the number of total clients that will be connected to the server
    private String ip; //the ip adress the client will try to connect to
    private int port;
    private boolean successfulConnect = false; //did this client successfully connect to the server
    private GameFrame theGameFrame;

    private boolean justPressedSend = false; //if this client waiting for the first transmision from the server
    private boolean firstFileRecieve; //is the client waiting for it's first catan file recive and waiting to set up the game
    private boolean firstClientGotStatup = false; //only used if this is client #1. Stores if the startup command has been recived yet

    private boolean cscStopRequested = false; //has the client been asked to stop

    private ClientSideConnection csc; //the socket type var to hold the connection for this CatanClient

    private boolean justRolledDice; //did one of the players just roll the dice and should they be animated

    /**
     * Constructor
     *
     * @param ip
     * @param gameFrame
     * @param port
     */
    public CatanClient(String ip, GameFrame gameFrame, int port) {
        this.ip = ip;
        this.port = port;
        theGameFrame = gameFrame;
        firstFileRecieve = true;
        clientColour = 0; //default to no colour
    }

    /**
     * Update the port for when the Client will attempt a connection to the
     * server
     *
     * @param port
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Access the client ID
     *
     * @return
     */
    public int getClientID() {
        return clientID;
    }

    /**
     * Access the colour of the client
     *
     * @return
     */
    public int getClientColour() {
        return clientColour;
    }

    /**
     * Sends a colour request to the server and returns whether or not the
     * colour was successfully assigned.
     *
     * @param colour
     */
    public void requestColour(int colour) {

        //reset a failed request
        clientColour = 0;

        //send it
        csc.requestColour(colour);

    }

    /**
     * Send a stopping command to the server through the CSC
     *
     * @param stopAll tell the sever to stop all clients. True for stopping all.
     */
    public void sendStop(boolean stopAll) {

        //send the request if not already done so
        if (!cscStopRequested) {
            csc.sendStopCommand4(stopAll);
        }

    }

    /**
     * Has this CSC been requested to stop.
     *
     * @return
     */
    public boolean isCscStopRequested() {
        return cscStopRequested;
    }

    /**
     * Access the total number of Clients on the server
     *
     * @return
     */
    public int getMaxClients() {
        return totalClientNum;
    }

    /**
     * Set the icon for the JFRame
     */
    private void setIcon() {
        this.setIconImage(ImageRef.ICON);
    }

    public void startClientThread() {
        //specific behaviour for the client numbers
        if (clientID == 1) {
            //go ahead and wait for the server to send the startup signal
            Thread t = new Thread(() -> {
                while (!firstClientGotStatup) {
                    startUpClient1();
                }
            });
            t.setName("CatanClient");
            t.start();
        } else {
            //wait for a message to come through
            Thread t = new Thread(() -> {
                //never stop listening unless told
                while (!cscStopRequested) {
                    regularRecive();
                }
                System.out.println("[Client " + clientID + "] End reached for regularRecive()");
            });
            t.setName("CatanClient");
            t.start();
        }
    }

    public boolean connectToServer() {
        //set up the socket
        csc = new ClientSideConnection();

        return successfulConnect;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * Get the boolean value for whether or not the dice have just been rolled.
     * And if they should be animated.
     *
     * @param justRolledDice
     */
    public void setJustRolledDice(boolean justRolledDice) {
        this.justRolledDice = justRolledDice;
    }

    /**
     * Set the boolean value for whether or not the dice have just been rolled.
     * And if they should be animated.
     *
     * @return
     */
    public boolean getJustRolledDice() {
        return justRolledDice;
    }

    /**
     * Enables the button for client 1 when the server sends the signal
     */
    public void startUpClient1() {
        int type = csc.reciveType();

        switch (type) {
            case 3:
                incomingColourResponse();

                firstClientGotStatup = false;
                break;
            case 4: //make sure this is for a type == 4 startup command
                //place to store the boolean
                //and assign it to the value the server sends
                boolean recivedBoolean = csc.reciveBoolean();

                //update the build buttons ingame
                theGameFrame.getGamePanel().updateBuildButtons();

                //make it visible
                theGameFrame.setVisible(true);

                closeNetInit();

                //send the save file
                sendGameToServer();

                firstClientGotStatup = true;

                //send it to the server
                //start listening
                //never stop listening unless told
                while (!cscStopRequested) {
                    regularRecive();
                }
                System.out.println("[Client 1] End reached for regularRecive()");
                break;
            case 6: //If the SSC send out an early stop command.
                //This kind of stop before colour selection was probaby triggered on the CSC side as a SINGLE #4 stop request
                firstClientGotStatup = true; //set this to true only to break out of the while loop that calls startUpClient1()
                cscStopRequested = true;

                //waste the dummy bool
                csc.reciveBoolean();
                csc.sendStopCommand6();

                System.out.println("[Client " + clientID + "] Early stop command #6 in startUpClient1()");
                break;
            default:
                break;
        }
    }

    /**
     * Deal with the server responding to a colour change request
     */
    private void incomingColourResponse() {
        //read in that boolean
        int recivedColourResponse = csc.reciveType(); //read in the int

        //debug the response
        System.out.println("[Client " + clientID + "] Recieved a colour change response: " + recivedColourResponse);

        clientColour = recivedColourResponse;

        /**
         * Update the gamePanel right here right now. This needs to be done
         * before another loop of startUpClient1() or regularRecive() can start.
         * As an example if startUpClient1() reads in a start up command before
         * the gamePanel gets the updated player colour then a lot of sate issue
         * occur. Calling completeColourRequestTransaction() allows for the
         * gamePanel to get the colour in time.
         */
        theGameFrame.getMainMenu().getSDColourSelectPanel().completeColourRequestTransaction();

    }

    public void sendGameToServer() {
        try {

            //ensure there is a directory there
            Files.createDirectories(Paths.get(ONLINE_SAVE_LOCATION));

            //write the game to the save file
            theGameFrame.getGamePanel().writeToFile(ONLINE_SAVE_LOCATION + ONLINE_SAVE_NAME + clientID + ONLINE_SAVE_TYPE);

            //once it has been saved send it to the server
            sendFile(ONLINE_SAVE_LOCATION + ONLINE_SAVE_NAME + clientID + ONLINE_SAVE_TYPE);

        } catch (FileNotFoundException ex) {
            System.out.println("FileNotFoundException while saving the initial game:\n" + ex);
        } catch (IOException ex) {
            System.out.println("IOException while saving the initial game:\n" + ex);
        }
    }

    /**
     * Send the data of the domestic trade that is underway to the server.
     *
     * @param onlineMode
     * @param playerStartedDomestic
     * @param playerSelectedForTrade
     * @param domesticTradeMode
     * @param tradeCardsGivePlayerStartedDomestic
     * @param tradeCardsReceivePlayerStartedDomestic
     * @param tradeCardsAlreadyHadPlayerStartedDomestic
     * @param tradeCardsAlreadyHadPlayerSelected
     */
    public void sendDomesticTradeToServer(int onlineMode, int playerStartedDomestic, int playerSelectedForTrade, int domesticTradeMode,
            ArrayList<Integer> tradeCardsGivePlayerStartedDomestic, ArrayList<Integer> tradeCardsReceivePlayerStartedDomestic,
            ArrayList<Integer> tradeCardsAlreadyHadPlayerStartedDomestic, ArrayList<Integer> tradeCardsAlreadyHadPlayerSelected) {

        csc.sendDomesticTradeData(onlineMode, playerStartedDomestic, playerSelectedForTrade, domesticTradeMode,
                tradeCardsGivePlayerStartedDomestic, tradeCardsReceivePlayerStartedDomestic, tradeCardsAlreadyHadPlayerStartedDomestic, tradeCardsAlreadyHadPlayerSelected);

    }

    private void sendFile(String fileLocation) {
        //test if it is a vailid save file
        try {
            String filePath = fileLocation;
            File file = new File(filePath);
            FileInputStream fileStream = new FileInputStream(file);

            int fileLength = (int) file.length();

            String fileName = filePath.substring(filePath.lastIndexOf(File.separator) + 1);

            //debug file name and length
            //System.out.println(fileName);
            //System.out.println(fileLength);
            byte fileBytes[] = new byte[fileLength];
            fileStream.read(fileBytes, 0, fileLength);

            //debug the stream
            //System.out.println(Arrays.toString(fileBytes));
            csc.sendFileStream(fileBytes, fileName, justRolledDice); //send the file

            justPressedSend = true;

            fileStream.close();
            file.setReadOnly();
            file.setExecutable(true);

        } catch (FileNotFoundException exception) {
            JOptionPane.showMessageDialog(null, "There was an error loading the save file:\n" + exception, "Loading Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException exception) {
            JOptionPane.showMessageDialog(null, "There was an IOException loading the save file:\n" + exception, "Loading Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void regularRecive() {
        int type = csc.reciveType();

        switch (type) {
            case 2:
                //else if type is 2
                //recive the file
                FileTypeRecieve fileTypeRecieve = csc.recieveFile();
                //header.setText("D " + justPressedSend); //debug the turn detection

                //now only actually save the file if THIS client didn't send it
                if (!justPressedSend) {
                    //get the sate of the dice roll animation
                    justRolledDice = fileTypeRecieve.getJustRolledDice();

                    //get just the name of the file
                    String fileName = fileTypeRecieve.getFileName();
                    //debug the file and how it was recived
                    //System.out.println("Regular Got file:\n" + Arrays.toString(fileTypeRecieve.getFile()));
                    //write the file
                    try {
                        //ensure the directory is there
                        Files.createDirectories(Paths.get(ONLINE_SAVE_LOCATION));

                        //create a file to save it to
                        File file = new File(ONLINE_SAVE_LOCATION + ONLINE_SAVE_NAME + clientID + ONLINE_SAVE_TYPE);

                        //take read and write acess
                        file.setExecutable(true);
                        file.setReadable(true);
                        file.setWritable(true);

                        //Create and output stream at the directory
                        FileOutputStream fos = new FileOutputStream(file);

                        //write the file
                        fos.write(fileTypeRecieve.getFile(), 0, fileTypeRecieve.getFile().length);

                        //close it
                        fos.close();

                        //check if this was the first file recival
                        if (firstFileRecieve) {
                            //setup the game that will be played
                            theGameFrame.resetGamePanel();

                            //save that a file was recived
                            firstFileRecieve = false;

                            closeNetInit();
                        }

                        //load the save from the other client in the online game
                        theGameFrame.getGamePanel().load(ONLINE_SAVE_LOCATION + ONLINE_SAVE_NAME + clientID + ONLINE_SAVE_TYPE);
                    } catch (FileNotFoundException exception) {
                        JOptionPane.showMessageDialog(null, "There was an error loading the save file:\n" + exception, "Loading Error", JOptionPane.ERROR_MESSAGE);
                        System.out.println("[Client " + clientID + "] There was an error loading the save file:\n" + exception);
                    } catch (IOException exception) {
                        JOptionPane.showMessageDialog(null, "There was an IOException loading the save file:\n" + exception, "Loading Error", JOptionPane.ERROR_MESSAGE);
                    }

                }
                break;
            case 3:
                incomingColourResponse();
                break;

            case 5:
                DomesticTradeTypeReceive domesticTradeTypeReceive = csc.receiveDomesticTradeData();

                //debug the sending of data for domestic trading
                //System.out.println("Got domestic trade mode: " + domesticTradeTypeReceive.getOnlineModeOfSender());
                //reset and show the tradepanel
                if (!theGameFrame.getShowTrade()) { //but only if it has not already
                    theGameFrame.switchToTrade(true);
                }

                //update the trade panel
                theGameFrame.getDomesticTradePanel().setPlayerStartedDomestic(domesticTradeTypeReceive.getPlayerStartedDomestic());
                theGameFrame.getDomesticTradePanel().setPlayerSelectedForTrade(domesticTradeTypeReceive.getPlayerSelectedForTrade());

                theGameFrame.getDomesticTradePanel().setDomesticTradeMode(domesticTradeTypeReceive.getDomesticTradeMode());

                theGameFrame.getDomesticTradePanel().setTradeCardsGivePlayerStartedDomestic(domesticTradeTypeReceive.getTradeCardsGivePlayerStartedDomestic());
                theGameFrame.getDomesticTradePanel().setTradeCardsReceivePlayerStartedDomestic(domesticTradeTypeReceive.getTradeCardsReceivePlayerStartedDomestic());

                theGameFrame.getDomesticTradePanel().setTradeCardsAlreadyHadPlayerStartedDomestic(domesticTradeTypeReceive.getTradeCardsAlreadyHadPlayerStartedDomestic());
                theGameFrame.getDomesticTradePanel().setTradeCardsAlreadyHadPlayerSelected(domesticTradeTypeReceive.getTradeCardsAlreadyHadPlayerSelected());

                //check to see if the trade is done/cancled
                if (domesticTradeTypeReceive.getDomesticTradeMode() == -1) {
                    theGameFrame.switchToTrade(false);
                } else { //if not cancelled
                    theGameFrame.getDomesticTradePanel().updateComponentState();
                    theGameFrame.getDomesticTradePanel().repaint();
                }

                break;

            //If the SSC send out a stop command
            case 6:
                cscStopRequested = true;

                //waste the dummy bool
                csc.reciveBoolean();

                csc.sendStopCommand6();

                break;
            default:
                break;
        }

        //if there was a special case for when a client sent the mesage just recived its not needed anymore
        if (justPressedSend) {
            justPressedSend = false;
        }
    }

    /**
     * Close out any loose ends from initializing the networking required for
     * online play. Common to both regularRecive() and startUpClient1()
     */
    private void closeNetInit() {
        //ensure that the main menu is no longer visibled
        if (theGameFrame.getMainMenu().getSDJoinLobbyPanel() != null && theGameFrame.getMainMenu().getSDJoinLobbyPanel().isVisible()) {
            //switch back to the main menu for when ever the game terminates
            theGameFrame.getMainMenu().getSDMenuFrame().switchPanel(theGameFrame.getMainMenu().getSDJoinLobbyPanel(), theGameFrame.getMainMenu());

            //if this was still visible close the LA CSC
            //Terminate connection with the lobby aggregation server
            theGameFrame.getMainMenu().getSDJoinLobbyPanel().closeCSC();
        }

        if (theGameFrame.getMainMenu().getSDColourSelectPanel() != null && theGameFrame.getMainMenu().getSDColourSelectPanel().isVisible()) {
            theGameFrame.getMainMenu().getSDMenuFrame().switchPanel(theGameFrame.getMainMenu().getSDColourSelectPanel(), theGameFrame.getMainMenu());

            //if this was still visible close the LA CSC
            //Terminate connection with the lobby aggregation server
            theGameFrame.getMainMenu().getSDJoinLobbyPanel().closeCSC();
        }

        //hide the main menu frame
        theGameFrame.getMainMenu().getSDMenuFrame().setVisible(false);
    }

    /**
     * An object just to hold multiple data types that get returned when a CSC
     * receives a domestic trading update from a server.
     */
    private class DomesticTradeTypeReceive {

        //Attributes
        int onlineModeOfSender;
        int playerStartedDomestic;
        int playerSelectedForTrade;
        int domesticTradeMode;
        ArrayList<Integer> tradeCardsGivePlayerStartedDomestic;
        ArrayList<Integer> tradeCardsReceivePlayerStartedDomestic;
        ArrayList<Integer> tradeCardsAlreadyHadPlayerStartedDomestic;
        ArrayList<Integer> tradeCardsAlreadyHadPlayerSelected;

        /**
         * Constructor
         *
         * @param onlineMode
         * @param playerStartedDomestic
         * @param playerSelectedForTrade
         * @param domesticTradeMode
         * @param tradeCardsGivePlayerStartedDomestic
         * @param tradeCardsReceivePlayerStartedDomestic
         * @param tradeCardsAlreadyHadPlayerStartedDomestic
         * @param tradeCardsAlreadyHadPlayerSelected
         */
        public DomesticTradeTypeReceive(int onlineMode, int playerStartedDomestic, int playerSelectedForTrade, int domesticTradeMode,
                ArrayList<Integer> tradeCardsGivePlayerStartedDomestic, ArrayList<Integer> tradeCardsReceivePlayerStartedDomestic,
                ArrayList<Integer> tradeCardsAlreadyHadPlayerStartedDomestic, ArrayList<Integer> tradeCardsAlreadyHadPlayerSelected) {

            onlineModeOfSender = onlineMode;
            this.playerStartedDomestic = playerStartedDomestic;
            this.playerSelectedForTrade = playerSelectedForTrade;
            this.domesticTradeMode = domesticTradeMode;
            this.tradeCardsGivePlayerStartedDomestic = tradeCardsGivePlayerStartedDomestic;
            this.tradeCardsReceivePlayerStartedDomestic = tradeCardsReceivePlayerStartedDomestic;
            this.tradeCardsAlreadyHadPlayerStartedDomestic = tradeCardsAlreadyHadPlayerStartedDomestic;
            this.tradeCardsAlreadyHadPlayerSelected = tradeCardsAlreadyHadPlayerSelected;

        }

        /**
         * Get the cards the player who started the trade will be receiving.
         *
         * @return
         */
        public ArrayList<Integer> getTradeCardsReceivePlayerStartedDomestic() {
            return tradeCardsReceivePlayerStartedDomestic;
        }

        /**
         * Get the cards the player who started the trade will be giving away.
         *
         * @return
         */
        public ArrayList<Integer> getTradeCardsGivePlayerStartedDomestic() {
            return tradeCardsGivePlayerStartedDomestic;
        }

        /**
         * Get the cards the player who started the trade already had when they
         * initiated said trade.
         *
         * @return
         */
        public ArrayList<Integer> getTradeCardsAlreadyHadPlayerStartedDomestic() {
            return tradeCardsAlreadyHadPlayerStartedDomestic;
        }

        /**
         * Get the cards the player who was selected for the trade already had
         * when they we selected for said trade.
         *
         * @return
         */
        public ArrayList<Integer> getTradeCardsAlreadyHadPlayerSelected() {
            return tradeCardsAlreadyHadPlayerSelected;
        }

        /**
         * Get the ID of the player that was selected for the domestic trade
         *
         * @return
         */
        public int getPlayerSelectedForTrade() {
            return playerSelectedForTrade;
        }

        /**
         * Get the ID of the player that initiated the domestic trade
         *
         * @return
         */
        public int getPlayerStartedDomestic() {
            return playerStartedDomestic;
        }

        /**
         * Get the mode of online play the sender of the data is in. (So this
         * will be the player ID/colour of the player that sent the trade data)
         *
         * @return
         */
        public int getOnlineModeOfSender() {
            return onlineModeOfSender;
        }

        /**
         * The mode of trade the panel is in.
         *
         * @return
         */
        public int getDomesticTradeMode() {
            return domesticTradeMode;
        }

    }

    private class FileTypeRecieve {

        private byte[] file;
        private String fileName;
        private boolean justRolledDice;

        public FileTypeRecieve() {
            this.file = new byte[1];
        }

        public FileTypeRecieve(byte[] file, String fileName, boolean justRolledDice) {
            this.file = file;
            this.fileName = fileName;
            this.justRolledDice = justRolledDice;
        }

        public byte[] getFile() {
            return file;
        }

        public String getFileName() {
            return fileName;
        }

        /**
         * Return the value of the boolean controlling dice roll animation. This
         * boolean was received through a Socket boolean receive.
         *
         * @return
         */
        public boolean getJustRolledDice() {
            return justRolledDice;
        }

    }

    //client connection inner class
    private class ClientSideConnection {

        private Socket socket;
        private DataInputStream dataIn;
        private DataOutputStream dataOut;

        public ClientSideConnection() {
            System.out.println("----Client----");
            try {
                //establic connection
                socket = new Socket(ip, port);
                dataIn = new DataInputStream(socket.getInputStream());
                dataOut = new DataOutputStream(socket.getOutputStream());
                //now that a connection has been establichsed get the number for this client
                clientID = dataIn.readInt();
                //the the totalClientNum
                totalClientNum = dataIn.readInt();
                System.out.println("[Client " + clientID + "] Connected to a server as Client #" + clientID);

                //if everything else was able to be done save the success
                successfulConnect = true;

            } catch (IOException e) {
                System.out.println("[Client " + clientID + "] IOException from CSC contructor\n" + e);

                //save the failed connection
                successfulConnect = false;
            }
        }

        public void sendNewString(String mesg) {
            try {
                dataOut.writeInt(1); //tell the server that is it recieving a chat message
                dataOut.writeUTF(mesg);
                dataOut.flush();
            } catch (IOException e) {
                System.out.println("[Client " + clientID + "] IOException from CSC sendNewString()");
            }
        }

        public void sendFileStream(byte[] fileStream, String fileName, boolean justRolledDice) {
            try {
                dataOut.writeInt(2); //tell the server that is it recieving a file
                dataOut.writeInt(fileStream.length); //send the length of the file
                dataOut.writeUTF(fileName); //send the name of the file including the extension
                dataOut.write(fileStream, 0, fileStream.length);
                dataOut.writeBoolean(justRolledDice);
                dataOut.flush();
            } catch (IOException e) {
                System.out.println("[Client " + clientID + "] IOException from CSC sendFileStream()");

                JOptionPane.showMessageDialog(null, "[Client " + clientID + "] "
                        + "IOException from CSC sendFileStream()", "CatanServer connection error", JOptionPane.ERROR_MESSAGE);
            }
        }

        public void requestColour(int colour) {

            try {
                dataOut.writeInt(3); //tell the server it is reciving a colour request
                dataOut.writeInt(colour); //send the colour the client would like
                dataOut.flush();
            } catch (IOException e) {
                System.out.println("[Client " + clientID + "] IOException from CSC sendColourRequest():\n" + e);
            }
        }

        /**
         * Start a CSC triggered stop. This might come from the GamePanel game
         * ending. Might also come from a player disconnecting prematurely. Will
         * trigger a command #6 stop once it reached the CatanServer. Must
         * specify if just this CSC is disconnecting or if all SSCs should be
         * stopped too. A single CSC disconnection can only happened before all
         * players have selected their colour.
         *
         * @param stopAll
         */
        public void sendStopCommand4(boolean stopAll) {
            System.out.println("[Client " + clientID + "] Sending Stop Command #4");

            try {
                dataOut.writeInt(4); //tell the server it is reveiving a stop command #4
                dataOut.writeBoolean(stopAll); //tell the sever to stop all clients
                dataOut.flush();

            } catch (IOException e) {
                System.out.println("[Client " + clientID + "] IOException from CSC sendStopCommand4():\n" + e);
            }
        }

        /**
         * A SSC triggered stop. This is send to the SSC to break it out of a
         * dataIn.readInt(). The run() will end since the SSC has already had
         * its stop requested. It only needs a command code to break it out of
         * the dataOutputStream.
         */
        public void sendStopCommand6() {
            System.out.println("[Client " + clientID + "] Sending Stop Command #6");

            try {
                dataOut.writeInt(6); //tell the server it is reveiving a stop command #6
                dataOut.flush();

                dataIn.close();
                dataOut.close();
                socket.close();

            } catch (IOException e) {
                System.out.println("[Client " + clientID + "] IOException from CSC sendStopCommand6():\n" + e);
            }
        }

        /**
         * Send all the data related to domestic trading to the server
         *
         * @param onlineMode
         * @param playerStartedDomestic
         * @param playerSelectedForTrade
         * @param domesticTradeMode
         * @param tradeCardsGivePlayerStartedDomestic
         * @param tradeCardsReceivePlayerStartedDomestic
         * @param tradeCardsAlreadyHadPlayerStartedDomestic
         * @param tradeCardsAlreadyHadPlayerSelected
         */
        public void sendDomesticTradeData(int onlineMode, int playerStartedDomestic, int playerSelectedForTrade, int domesticTradeMode,
                ArrayList<Integer> tradeCardsGivePlayerStartedDomestic, ArrayList<Integer> tradeCardsReceivePlayerStartedDomestic,
                ArrayList<Integer> tradeCardsAlreadyHadPlayerStartedDomestic, ArrayList<Integer> tradeCardsAlreadyHadPlayerSelected) {

            //debugg the data sending
            //System.out.println("Sending data!");
            try {
                dataOut.writeInt(5); //tell the server that it is reveiving the data of a domestic trade
                dataOut.writeInt(onlineMode); //tell the server what player is sending this so it doens't send it back

                //send the player data
                dataOut.writeInt(playerStartedDomestic);
                dataOut.writeInt(playerSelectedForTrade);

                //send the mode
                dataOut.writeInt(domesticTradeMode);

                //send the size of the tradeCardsGivePlayerStartedDomestic
                dataOut.writeInt(tradeCardsGivePlayerStartedDomestic.size());
                //send the tradeCardsGivePlayerStartedDomestic ArrayList
                for (int i = 0; i < tradeCardsGivePlayerStartedDomestic.size(); i++) {
                    dataOut.writeInt(tradeCardsGivePlayerStartedDomestic.get(i));
                }

                //send the size of the tradeCardsReceivePlayerStartedDomestic
                dataOut.writeInt(tradeCardsReceivePlayerStartedDomestic.size());
                //send the tradeCardsReceivePlayerStartedDomestic ArrayList
                for (int i = 0; i < tradeCardsReceivePlayerStartedDomestic.size(); i++) {
                    dataOut.writeInt(tradeCardsReceivePlayerStartedDomestic.get(i));
                }

                //send the already had arrays
                //send the size of the tradeCardsAlreadyHadPlayerStartedDomestic
                dataOut.writeInt(tradeCardsAlreadyHadPlayerStartedDomestic.size());
                //send the tradeCardsAlreadyHadPlayerStartedDomestic ArrayList
                for (int i = 0; i < tradeCardsAlreadyHadPlayerStartedDomestic.size(); i++) {
                    dataOut.writeInt(tradeCardsAlreadyHadPlayerStartedDomestic.get(i));
                }

                //send the size of the tradeCardsAlreadyHadPlayerSelected
                dataOut.writeInt(tradeCardsAlreadyHadPlayerSelected.size());
                //send the tradeCardsAlreadyHadPlayerSelected ArrayList
                for (int i = 0; i < tradeCardsAlreadyHadPlayerSelected.size(); i++) {
                    dataOut.writeInt(tradeCardsAlreadyHadPlayerSelected.get(i));
                }

                //send out the data
                dataOut.flush();

            } catch (IOException e) {
                System.out.println("[Client " + clientID + "] IOException from CSC sendDomesticTradeData()");
            }
        }

        public FileTypeRecieve recieveFile() {
            byte[] file = new byte[1];
            String fileName = "";
            boolean justRolledDice = false;

            try {
                //get the file length
                file = new byte[dataIn.readInt()];
                //get the fileName
                fileName = dataIn.readUTF();

                int count = 0;
                while (count < file.length) {
                    int bytesRead = dataIn.read(file, count, file.length - count);
                    //debug reading the file
                    //System.out.println("[Client " + clientID + "] bytesRead: " + bytesRead);
                    if (bytesRead == -1) {
                        System.out.println("[Client " + clientID + "] didn't get a complete file");
                    }
                    count += bytesRead;
                }

                //read in the dice roll animation
                justRolledDice = dataIn.readBoolean();

            } catch (IOException ex) {
                System.out.println("[Client " + clientID + "] IOException from CSC recieveFile()");
            }

            return new FileTypeRecieve(file, fileName, justRolledDice);
        }

        /**
         * Receive all the data related to domestic trading to the server
         *
         * @return
         */
        public DomesticTradeTypeReceive receiveDomesticTradeData() {
            int onlineModeOfSender = -1;
            int playerStartedDomestic = -1;
            int playerSelectedForTrade = -1;
            int domesticTradeMode = -1;
            ArrayList<Integer> tradeCardsGivePlayerStartedDomestic = new ArrayList<>();
            ArrayList<Integer> tradeCardsReceivePlayerStartedDomestic = new ArrayList<>();
            ArrayList<Integer> tradeCardsAlreadyHadPlayerStartedDomestic = new ArrayList<>();
            ArrayList<Integer> tradeCardsAlreadyHadPlayerSelected = new ArrayList<>();

            try {

                onlineModeOfSender = dataIn.readInt();

                //read in the playerStartedDomestic
                playerStartedDomestic = dataIn.readInt();
                //read in the playerSelectedForTrade
                playerSelectedForTrade = dataIn.readInt();

                //read in the domesticTradeMode
                domesticTradeMode = dataIn.readInt();

                //read int the length of the tradeCardsGivePlayerStartedDomestic
                int tradeCardsGivePlayerStartedDomesticLength = dataIn.readInt();
                //read in the rest of the tradeCardsGivePlayerStartedDomestic ArrayList
                for (int i = 0; i < tradeCardsGivePlayerStartedDomesticLength; i++) {
                    tradeCardsGivePlayerStartedDomestic.add(dataIn.readInt());
                }

                //read int the length of the tradeCardsGivePlayerStartedDomestic
                int tradeCardsReceivePlayerStartedDomesticLength = dataIn.readInt();
                //read in the rest of the tradeCardsGivePlayerStartedDomestic ArrayList
                for (int i = 0; i < tradeCardsReceivePlayerStartedDomesticLength; i++) {
                    tradeCardsReceivePlayerStartedDomestic.add(dataIn.readInt());
                }

                //read in the already hads
                //read int the length of the tradeCardsGivePlayerStartedDomestic
                int tradeCardsAlreadyHadPlayerStartedDomesticLength = dataIn.readInt();
                //read in the rest of the tradeCardsGivePlayerStartedDomestic ArrayList
                for (int i = 0; i < tradeCardsAlreadyHadPlayerStartedDomesticLength; i++) {
                    tradeCardsAlreadyHadPlayerStartedDomestic.add(dataIn.readInt());
                }

                //read int the length of the tradeCardsGivePlayerStartedDomestic
                int tradeCardsAlreadyHadPlayerSelectedLength = dataIn.readInt();
                //read in the rest of the tradeCardsGivePlayerStartedDomestic ArrayList
                for (int i = 0; i < tradeCardsAlreadyHadPlayerSelectedLength; i++) {
                    tradeCardsAlreadyHadPlayerSelected.add(dataIn.readInt());
                }

            } catch (IOException ex) {
                System.out.println("[Client " + clientID + "] IOException from CSC receiveDomesticTradeData()");
            }

            return new DomesticTradeTypeReceive(onlineModeOfSender, playerStartedDomestic, playerSelectedForTrade, domesticTradeMode,
                    tradeCardsGivePlayerStartedDomestic, tradeCardsReceivePlayerStartedDomestic, tradeCardsAlreadyHadPlayerStartedDomestic, tradeCardsAlreadyHadPlayerSelected);
        }

        public String reciveNewString() {
            String msg = "";

            try {
                msg = dataIn.readUTF();
            } catch (IOException ex) {
                System.out.println("[Client " + clientID + "] IOException from CSC reciveNewString()");
            }

            return msg;
        }

        public int reciveType() {
            int msg = 0;

            try {
                msg = dataIn.readInt();
            } catch (IOException ex) {
                System.out.println("[Client " + clientID + "] IOException from CSC reciveType():\n" + ex);

                //request a stop
                cscStopRequested = true;
            }

            return msg;
        }

        public boolean reciveBoolean() {
            boolean bool = false;

            try {
                bool = dataIn.readBoolean();
            } catch (IOException ex) {
                System.out.println("[Client " + clientID + "] IOException from CSC reciveBoolean()");
            }

            return bool;
        }
    }
}
