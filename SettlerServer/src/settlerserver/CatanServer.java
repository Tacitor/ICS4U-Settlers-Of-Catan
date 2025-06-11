/*
 * Lukas Krampitz
 * Mar 27, 2021
 * A modified copy of the orginonal server code from the main project and package
 */
package settlerserver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author Tacitor
 */
public class CatanServer {

    /**
     * A static counter that ticks up with each CatanServer construction. Used
     * to assign an ID to a CatanServer.
     */
    private static int latestServerID = 0;

    //The reciving socket
    private ServerSocket serverSocket;
    private int catanServerID;
    //the number of clients what have connected
    private int numClients;
    private int maxClients; //the number of clients that will connect
    private boolean stopRequested;
    private ArrayList<Integer> availableColours;

    //The array of clients
    private ServerSideConnection[] clients;

    /**
     * Constructs a dummy server with no socket and a max players of 0. With a
     * player max of 0 the game can detect it as empty. Later if a player
     * selects it there will come the request to restart the server with the
     * requested max player count.
     */
    public CatanServer() {
        maxClients = 0;
        catanServerID = getNextID();

        System.out.println("[Server " + catanServerID + "] Settting up server for " + maxClients + " players. This is an empty dummy server.");

        serverSocket = null;
        numClients = 0;
        stopRequested = true;
        availableColours = new ArrayList<>();
        clients = new ServerSideConnection[maxClients];
    }

    private static int getNextID() {
        return ++latestServerID;
    }

    public void acceptConnections() {
        try {
            System.out.println("[Server " + catanServerID + "] Waiting for connections...");
            //wait until all the clients have connected
            while (numClients < maxClients) {
                //create a reciving socket on the server side
                Socket s = serverSocket.accept();

                //It means that there has been a call to requestStop()
                //and a dummy socket was made to break out of `serverSocket.accept()` above.
                if (!stopRequested) {
                    int newClientIndex = findFirstNullClient(clients);

                    //test if we truly have room for a new client
                    if (newClientIndex != -1) {
                        //count it as a client
                        numClients++;
                        System.out.println("[Server " + catanServerID + "] Client #" + (newClientIndex + 1) + " has connected");
                        //create a new SSC for to keep track of that incoming socket
                        ServerSideConnection ssc = new ServerSideConnection(s, (newClientIndex + 1));

                        clients[newClientIndex] = ssc;

                        Thread t = new Thread(ssc);
                        t.setName("[Server " + catanServerID + ": SSC" + numClients + "]");
                        t.start();

                        /**
                         * Allow the LA server to update any LA clients. This
                         * will give the most up to date server statistics to
                         * any player with the SDJoinLobbyPanel open.
                         */
                        SettlerServer.propagateCatanServerChange();
                    } else {
                        ColourPrint.printRed("[Server " + catanServerID + "] ERROR: clients array has no index of null value");
                    }

                } else {
                    System.out.println("[Server " + catanServerID + "] Accepted and discarded an extra socket");
                }
            }
            ColourPrint.printGreen("[Server " + catanServerID + "] We now have " + maxClients + " players. No more connections will be accepted.");
        } catch (IOException e) {
            ColourPrint.printRed("[Server " + catanServerID + "] IOException from acceptConnections\n" + e);
        }
    }

    /**
     *
     */
    public void requestStop() {
        //early return for servers that have already had a stop requested
        if (stopRequested) {
            System.out.println("[Server " + catanServerID + "] Stop recieved, and skipped: already stopped.");
            return;
        }

        System.out.println("[Server " + catanServerID + "] Stop recieved");
        stopRequested = true;

        //Only create a dummy socket if we need to break out of the serverSocket.accept()
        if (numClients < maxClients) {
            maxClients = 0;

            try {
                Socket dummy = new Socket("localhost", serverSocket.getLocalPort());
                dummy.close();
            } catch (IOException e) {
                ColourPrint.printRed("[Server " + catanServerID + "] IOException from dummy creation in requestStop() in CatanServer");
            }
        }

        try {
            //close the server socket so another can later be created
            serverSocket.close();
        } catch (IOException ex) {
            ColourPrint.printRed("[Server " + catanServerID + "] IOException from closing serverSocket in requestStop() in CatanServer");
        }

        stopSSCClients();
    }

    /**
     * This restart this CatanServer object with a specific number of players.
     *
     * @param maxClients Must be values 2-4 or 0
     * @return readyToAcceptConnections is true if it makes sense to call
     * acceptConnections()
     */
    public boolean requestRestart(int maxClients) {
        boolean readyToAcceptConnections = false;

        if (maxClients >= 2 && maxClients <= 4) {
            requestStop();

            try {
                //Wait until the stop has fully propogated before attempting a restart
                Thread.sleep(200);

                int port = SettlerServer.LOBBY_AGGREGATION_PORT_NUM + catanServerID;
                System.out.println("[Server " + catanServerID + "] Settting up server for " + maxClients + " players on port: " + port);

                //no clients have connected yet
                numClients = 0;
                //save the number of clients that will connect
                this.maxClients = maxClients;

                stopRequested = false;

                //create the list of available colours
                availableColours = new ArrayList<>();
                for (int i = 1; i < maxClients + 1; i++) {
                    availableColours.add(i);
                }

                //initialize the array
                clients = new ServerSideConnection[maxClients];

                //create the socket to listen
                serverSocket = new ServerSocket(port);

                readyToAcceptConnections = true;
            } catch (InterruptedException e) {
                ColourPrint.printRed("[Server " + catanServerID + "] InterruptedException from server requestRestart()");
            } catch (IOException e) {
                ColourPrint.printRed("[Server " + catanServerID + "] IOException from server requestRestart()");
            }

        } else if (maxClients == 0) {
            //This will set stopRequested to true, so there is no need to set it in the try block
            requestStop();

            try {
                //Wait until the stop has fully propogated before attempting a restart
                Thread.sleep(200);

                this.maxClients = maxClients;
                System.out.println("[Server " + catanServerID + "] Settting up server for " + maxClients + " players. This is an empty dummy server.");

                serverSocket = null;
                numClients = 0;
                availableColours = new ArrayList<>();
                clients = new ServerSideConnection[maxClients];

                //Spread the news. The user will want to see this on the lobby selection right away.
                SettlerServer.propagateCatanServerChange();

                readyToAcceptConnections = false;
            } catch (InterruptedException e) {
                ColourPrint.printRed("[Server " + catanServerID + "] InterruptedException from server requestRestart()");
            }
        } else {
            ColourPrint.printRed("[Server " + catanServerID + "] ERROR: Invalid input for requestRestart(). The maxClients value of: " + maxClients + " is not within 2-4 or 0.");
        }

        return readyToAcceptConnections;
    }

    /**
     * Loop through the clients array and send out a command #6 over the SSC
     * clients to the CSCs. The CSC will get this boolean of true with a command
     * of #6 and will echo the boolean back under the same command #6. This will
     * get every connected SSC to break out of dataIn.readInt() and be able to
     * exit the while loop.
     */
    private void stopSSCClients() {
        for (ServerSideConnection ssc : clients) {
            if (ssc != null && !ssc.stopRequested) {
                ssc.sendBoolean(true, 6);
            }
        }
    }

    /**
     *
     * @return
     */
    public int getMaxClients() {
        return maxClients;
    }

    /**
     *
     * @return
     */
    public int getCatanServerID() {
        return catanServerID;
    }

    /**
     *
     * @return
     */
    public int[] getColoursTaken() {
        int[] taken = new int[numClients];
        int iTaken = 0;

        for (ServerSideConnection client : clients) {
            if (client != null) {
                taken[iTaken++] = client.clientColour;
            }
        }

        return taken;
    }

    /**
     * Returns true if even one SSC has a stop requested.
     *
     * @return
     */
    public boolean hasStoppedClient() {
        boolean hasStoppedClient = false;

        for (ServerSideConnection ssc : clients) {
            if (ssc != null && ssc.stopRequested) {
                hasStoppedClient = true;
            }
        }

        return hasStoppedClient;
    }

    /**
     *
     */
    public static void printDebugLnBr() {
        if (SettlerServer.DEBUG_OUTPUT) {
            System.out.print("\n");
        }
    }

    /**
     * Iterate over the given clients. Return the index of the first null in the
     * clients array
     *
     * @param clients
     * @returns -1 if no null ServerSideConnection found in clients
     */
    private static int findFirstNullClient(ServerSideConnection[] clients) {
        int i = 0;

        while (i < clients.length) {
            if (clients[i++] == null) {
                return i - 1;
            }
        }

        return -1;
    }

    private class ServerSideConnection implements Runnable {

        private Socket socket; //the socket that this client connected with
        private DataInputStream dataIn;
        private DataOutputStream dataOut;
        private int clientID;
        private int clientColour;

        //has the thread been reqested to stop?
        private boolean stopRequested = false;

        /**
         * Constructor
         *
         * @param socket
         * @param id
         */
        public ServerSideConnection(Socket socket, int id) {
            this.socket = socket;
            clientID = id;
            //setup the data streams
            try {
                dataIn = new DataInputStream(socket.getInputStream());
                dataOut = new DataOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                ColourPrint.printRed("[Server " + catanServerID + "] IOException from SSC constuctor for client#" + id);
            }
        }

        public int getID() {
            return clientID;
        }

        public void requestStop() {
            stopRequested = true;
        }

        @Override
        public void run() {
            try {
                //when a client first connects send it's ID and the chat in it's current state
                dataOut.writeInt(clientID);
                dataOut.writeInt(maxClients); //the the client how many clients there will be
                dataOut.flush(); //send it

                //loop state after all startup business is complete
                while (!stopRequested) {
                    //accept a message
                    //TODO: There is a bug with stopping the CatanServer when there is one player connected after requesting a colour. LA stops and main CS stop but the CS SSC lives on at this line.
                    //Related to this bug is when a client may disconnect under the same conditons this has an IOException from SSC run()
                    int type = dataIn.readInt(); //get the type of transmision
                    //if the client sent a chat message
                    switch (type) {
                        case 2:
                            //if the client sent a file
                            //read in the length from the socket
                            int fileLength = dataIn.readInt();
                            //read in the file name and extension
                            String fileName = dataIn.readUTF();
                            //create byte array to store the file
                            byte[] fileAsStream = new byte[fileLength];
                            int count = 0;
                            while (count < fileLength) {
                                int bytesRead = dataIn.read(fileAsStream, count, fileAsStream.length - count);
                                //debug reciving the file
                                //System.out.println("[Server " + catanServerID + "] bytesRead: " + bytesRead);
                                if (bytesRead == -1) {
                                    ColourPrint.printRed("[Server " + catanServerID + "] didn't get a complete file");
                                }
                                count += bytesRead;
                            }   //debug the file that was sent
                            //System.out.println("[Server " + catanServerID + "] " + Arrays.toString(fileAsStream));

                            //recive the dice roll boolean
                            boolean justRolledDice = dataIn.readBoolean();

                            //check if a stop is requested
                            if (!stopRequested) {
                                //send the new chat and file out to all the clients
                                for (ServerSideConnection client : clients) {
                                    //debug how many times it was sent
                                    //System.out.println("[CatanServer] " +"Sent it");
                                    //System.out.println("[CatanServer] " +"\nCurrent chat is :\n" + chat);
                                    client.sendFile(fileAsStream, fileName, justRolledDice);

                                }
                            }
                            break;
                        //if the server is getting a colour request
                        case 3:

                            //read int the requested colour
                            int colourRequest = dataIn.readInt();
                            boolean hasColour; //is that colour avaiable to take

                            //check if the client gave a specific colour
                            if (colourRequest != 0) {

                                hasColour = availableColours.contains(colourRequest);

                                //if that colour is in the list take it out
                                if (hasColour) {
                                    availableColours.remove(new Integer(Integer.toString(colourRequest)));
                                }
                            } else {

                                hasColour = true;

                                //give them the first next colour
                                colourRequest = availableColours.get(0);

                                //then remove it from the list
                                availableColours.remove(0);
                            }

                            //send the client the result of the request
                            if (hasColour) {
                                clients[clientID - 1].sendColourResponse(colourRequest); //message type 3: colour request
                                clients[clientID - 1].clientColour = colourRequest;

                                /**
                                 * Allow the LA server to update any LA clients.
                                 * This will give the most up to date server
                                 * statistics to any player with the
                                 * SDJoinLobbyPanel open.
                                 */
                                SettlerServer.propagateCatanServerChange();
                            } else {
                                clients[clientID - 1].sendColourResponse(-1);
                            }

                            //check if this is the last client to select their colour
                            if (availableColours.isEmpty()) {
                                //then tell the first client to begin
                                clients[0].sendBoolean(true, 4); //incluse the messagy type 4 (startup command)

                                //debug the data coming in
                                System.out.println("[Server " + catanServerID + "] Send begin command to Client 1");
                                printDebugLnBr();
                            }

                            break;
                        //if the server is getting a stop command
                        case 4:
                            boolean stopAll = dataIn.readBoolean();

                            if (stopAll || availableColours.size() < 1) {
                                System.out.println("[Server " + catanServerID + "] Stop ALL request command #4 in SSC run() for ID#" + clientID);
                                stopSSCClients();
                            } else {
                                System.out.println("[Server " + catanServerID + "] Stop SIGNLE request command #4 in SSC run() for ID#" + clientID);

                                //Null out this client ID
                                clients[clientID - 1] = null;

                                //Bounce a stop request to break this SSC out of the readInt();
                                this.sendBoolean(true, 6);

                                //make room for another client.
                                numClients--;

                                if (this.clientColour > 0) {
                                    availableColours.add(this.clientColour);
                                }

                                //If we now have 0 or less players (god I hope not less), restrt the CS
                                if (numClients <= 0) {
                                    requestRestart(0);
                                } else if ((numClients + 1) == maxClients) {
                                    Thread t = new Thread(() -> {
                                        acceptConnections();
                                    });
                                    t.setName("[Server " + catanServerID + "]");
                                    t.start();
                                }

                                SettlerServer.propagateCatanServerChange();
                            }
                            break;
                        //if the server is getting the domestic trading data
                        case 5:

                            //read in the online player ID of the sender
                            int onlineModeOfSender = dataIn.readInt();

                            //read in the playerStartedDomestic
                            int playerStartedDomestic = dataIn.readInt();
                            //read in the playerSelectedForTrade
                            int playerSelectedForTrade = dataIn.readInt();

                            //read in the domesticTradeMode
                            int domesticTradeMode = dataIn.readInt();

                            //read int the length of the tradeCardsGivePlayerStartedDomestic
                            int tradeCardsGivePlayerStartedDomesticLength = dataIn.readInt();
                            //create an array for that
                            int[] tradeCardsGivePlayerStartedDomestic = new int[tradeCardsGivePlayerStartedDomesticLength];
                            //read in the rest of the tradeCardsGivePlayerStartedDomestic ArrayList
                            for (int i = 0; i < tradeCardsGivePlayerStartedDomesticLength; i++) {
                                tradeCardsGivePlayerStartedDomestic[i] = dataIn.readInt();
                            }

                            //read int the length of the tradeCardsGivePlayerStartedDomestic
                            int tradeCardsReceivePlayerStartedDomesticLength = dataIn.readInt();
                            //create an array for that
                            int[] tradeCardsReceivePlayerStartedDomestic = new int[tradeCardsReceivePlayerStartedDomesticLength];
                            //read in the rest of the tradeCardsGivePlayerStartedDomestic ArrayList
                            for (int i = 0; i < tradeCardsReceivePlayerStartedDomesticLength; i++) {
                                tradeCardsReceivePlayerStartedDomestic[i] = dataIn.readInt();
                            }

                            //read the already had arrays
                            //read int the length of the tradeCardsGivePlayerStartedDomestic
                            int tradeCardsAlreadyHadPlayerStartedDomesticLength = dataIn.readInt();
                            //create an array for that
                            int[] tradeCardsAlreadyHadPlayerStartedDomestic = new int[tradeCardsAlreadyHadPlayerStartedDomesticLength];
                            //read in the rest of the tradeCardsGivePlayerStartedDomestic ArrayList
                            for (int i = 0; i < tradeCardsAlreadyHadPlayerStartedDomesticLength; i++) {
                                tradeCardsAlreadyHadPlayerStartedDomestic[i] = dataIn.readInt();
                            }

                            //read int the length of the tradeCardsGivePlayerStartedDomestic
                            int tradeCardsAlreadyHadPlayerSelectedLength = dataIn.readInt();
                            //create an array for that
                            int[] tradeCardsAlreadyHadPlayerSelected = new int[tradeCardsAlreadyHadPlayerSelectedLength];
                            //read in the rest of the tradeCardsGivePlayerStartedDomestic ArrayList
                            for (int i = 0; i < tradeCardsAlreadyHadPlayerSelectedLength; i++) {
                                tradeCardsAlreadyHadPlayerSelected[i] = dataIn.readInt();
                            }

                            //send the data back out to all the clients
                            //except the sender
                            for (ServerSideConnection client : clients) {
                                if (client.clientColour != onlineModeOfSender) {
                                    client.sendDomesticTradeData(onlineModeOfSender, playerStartedDomestic, playerSelectedForTrade, domesticTradeMode, tradeCardsGivePlayerStartedDomestic, tradeCardsReceivePlayerStartedDomestic, tradeCardsAlreadyHadPlayerStartedDomestic, tradeCardsAlreadyHadPlayerSelected);
                                }
                            }

                            break;
                        //if the server is getting an update that a stop has been requested
                        case 6:
                            System.out.println("[Server " + catanServerID + "] Stop request command #6 in SSC run() for ID#" + clientID);
                            this.requestStop(); // set this to false to drop out of the while loop
                            break;
                        default:
                            break;
                    }
                }

                dataIn.close();
                dataOut.close();
                socket.close();

                System.out.println("[Server " + catanServerID + "] End reached in SSC run() for ID#" + clientID);
            } catch (IOException e) {
                ColourPrint.printRed("[Server " + catanServerID + "] IOException from SSC run() for ID#" + clientID + "\n" + e);
            }
        }

        /**
         * Send a string to the client
         *
         * @param msg
         */
        public void sendNewString(String msg) {
            try {
                dataOut.writeInt(1); //tell the client they are reciving a chat message
                dataOut.writeUTF(msg);
                dataOut.flush();
            } catch (IOException e) {
                ColourPrint.printRed("[Server " + catanServerID + "] IOException from SSC sendNewString()");
            }
        }

        /**
         * Send a file to the client. Also update their chat so they know to
         * check for a new file
         *
         * @param msg
         * @param fileData
         * @param fileName
         * @param justRolledDice
         */
        public void sendFile(byte[] fileData, String fileName, boolean justRolledDice) {
            try {
                dataOut.writeInt(2); //tell the client they are reciving a file and chat message
                dataOut.writeInt(fileData.length); //send the length of the file
                dataOut.writeUTF(fileName); //send the file name
                dataOut.write(fileData, 0, fileData.length); //send the file
                dataOut.writeBoolean(justRolledDice); //send whether or not the dice animation needs to be set
                dataOut.flush();
            } catch (IOException e) {
                ColourPrint.printRed("[Server " + catanServerID + "] IOException from SSC sendNewString()");
            }
        }

        public void sendDomesticTradeData(int onlineModeOfSender, int playerStartedDomestic, int playerSelectedForTrade, int domesticTradeMode,
                int[] tradeCardsGivePlayerStartedDomestic, int[] tradeCardsReceivePlayerStartedDomestic,
                int[] tradeCardsAlreadyHadPlayerStartedDomestic, int[] tradeCardsAlreadyHadPlayerSelected) {

            try {
                dataOut.writeInt(5); //tell the client they are reciving domestic trade data
                dataOut.writeInt(onlineModeOfSender); //tell the recipeient who send the trade data

                //send the player data
                dataOut.writeInt(playerStartedDomestic);
                dataOut.writeInt(playerSelectedForTrade);

                //send the domesticTradeMode
                dataOut.writeInt(domesticTradeMode);

                //send the size of the tradeCardsGivePlayerStartedDomestic
                dataOut.writeInt(tradeCardsGivePlayerStartedDomestic.length);
                //send the tradeCardsGivePlayerStartedDomestic ArrayList
                for (int i = 0; i < tradeCardsGivePlayerStartedDomestic.length; i++) {
                    dataOut.writeInt(tradeCardsGivePlayerStartedDomestic[i]);
                }

                //send the size of the tradeCardsReceivePlayerStartedDomestic
                dataOut.writeInt(tradeCardsReceivePlayerStartedDomestic.length);
                //send the tradeCardsReceivePlayerStartedDomestic ArrayList
                for (int i = 0; i < tradeCardsReceivePlayerStartedDomestic.length; i++) {
                    dataOut.writeInt(tradeCardsReceivePlayerStartedDomestic[i]);
                }

                //send the size of the tradeCardsAlreadyHadPlayerStartedDomestic
                dataOut.writeInt(tradeCardsAlreadyHadPlayerStartedDomestic.length);
                //send the tradeCardsAlreadyHadPlayerStartedDomestic ArrayList
                for (int i = 0; i < tradeCardsAlreadyHadPlayerStartedDomestic.length; i++) {
                    dataOut.writeInt(tradeCardsAlreadyHadPlayerStartedDomestic[i]);
                }

                //send the size of the tradeCardsAlreadyHadPlayerSelected
                dataOut.writeInt(tradeCardsAlreadyHadPlayerSelected.length);
                //send the tradeCardsAlreadyHadPlayerSelected ArrayList
                for (int i = 0; i < tradeCardsAlreadyHadPlayerSelected.length; i++) {
                    dataOut.writeInt(tradeCardsAlreadyHadPlayerSelected[i]);
                }

                dataOut.flush();
            } catch (IOException e) {
                ColourPrint.printRed("[Server " + catanServerID + "] IOException from SSC sendDomesticTradeData()");
            }
        }

        /**
         * Send a boolean to the client
         *
         * @param msg
         */
        public void sendBoolean(boolean msg, int msgType) {
            try {
                dataOut.writeInt(msgType); //tell the client what type of message they are reciving
                dataOut.writeBoolean(msg);
                dataOut.flush();
            } catch (IOException e) {
                ColourPrint.printRed("[Server " + catanServerID + "] IOException from SSC sendBoolean()");
            }
        }

        /**
         * Send an integer colour response to the client
         *
         * @param msg
         */
        public void sendColourResponse(int msg) {
            try {
                dataOut.writeInt(3); //tell the client a type 3: colour response
                dataOut.writeInt(msg);
                dataOut.flush();
            } catch (IOException e) {
                ColourPrint.printRed("[Server " + catanServerID + "] IOException from SSC sendColourResponse()");
            }
        }

        /**
         *
         * @return
         */
        @Override
        public String toString() {
            return "ClientID: " + clientID;
        }

    }

}
