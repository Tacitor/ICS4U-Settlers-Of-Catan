/*
 * Lukas Krampitz
 * Mar 27, 2021
 * 
 */
package krampitzkreutzwisersettlersofcatan.sockets;

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

    //The reciving socket
    private ServerSocket serverSocket;
    //the number of clients what have connected
    private int numClients;
    private int maxClients; //the number of clients that will connect
    private ArrayList<Integer> availableColours;

    //The array of clients
    private ServerSideConnection[] clients;

    //A starting String
    private String chat = "Hello from server\n";

    /**
     * The constructor
     *
     * @param maxClients
     * @param port
     */
    public CatanServer(int maxClients, int port) {
        //no clients have connected yet
        numClients = 0;
        //save the number of clients that will connect
        this.maxClients = maxClients;

        //create the list of available colours
        availableColours = new ArrayList<>();
        for (int i = 1; i < maxClients + 1; i++) {
            availableColours.add(i);
        }

        //initialize the array
        clients = new ServerSideConnection[maxClients];

        //create the socket to listen 
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println("[Server] " + "IOException from server contructor");
        }
    }

    public void acceptConnections() {
        try {
            System.out.println("[Server] " + "Waiting for connections...");
            //wait until all the clients have connected
            while (numClients < maxClients) {
                //create a reciving socket on the server side
                Socket s = serverSocket.accept();
                //count it as a client
                numClients++;
                System.out.println("[Server] " + "Client #" + numClients + " has connected");
                //create a new SSC for to keep track of that incoming socket
                ServerSideConnection ssc = new ServerSideConnection(s, numClients);

                //save that new ssc to the list of clients
                clients[numClients - 1] = ssc;

                //start a new thread just for that one client
                Thread t = new Thread(ssc);
                t.start();
            }
            System.out.println("[Server] " + "We now have " + maxClients + " players. No more connections will be accepted.");

            //close the server socket so another can later be created
            serverSocket.close();
        } catch (IOException e) {
            System.out.println("[Server] " + "IOException from acceptConnections");
        }
    }

    private void updateChat(String newMsg) {
        chat += newMsg;
    }

    private void clearChat() {
        chat = "";
    }

    private class ServerSideConnection implements Runnable {

        private Socket socket; //the socket that this client connected with
        private DataInputStream dataIn;
        private DataOutputStream dataOut;
        private int clientID;

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
                System.out.println("[Server] " + "IOException from SSC constuctor for client#" + id);
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
                dataOut.writeUTF(chat);
                dataOut.flush(); //send it

                //loop state after all startup business is complete
                while (!stopRequested) {
                    //accept a message
                    int type = dataIn.readInt(); //get the type of transmision
                    //if the client sent a chat message
                    switch (type) {
                        case 1:
                            //read the chat message
                            String newMsg = dataIn.readUTF();
                            //check if a user wants to clear the chat
                            if (newMsg.equals("/clear")) {
                                clearChat();
                                updateChat("[Server] Client #" + clientID + " cleared chat\n");

                            } else { //else add the new string

                                //if a message come from client 1
                                updateChat("Client #" + clientID + ": " + newMsg + "\n");
                            }   //send the new chat out to all the clients
                            for (ServerSideConnection client : clients) {
                                client.sendNewString(chat);
                            }

                            //debug the chat
                            //System.out.println("[CatanServer] " +"Chat is now: \"\n" + chat + "\" chat end.");
                            break;
                        case 2:
                            //if the client sent a file

                            //tell all the clients about the file
                            updateChat("[Server] Client #" + clientID + " has sent a file to everyone's SettlerDevs folder\n");
                            //and read in the length from the socket
                            int fileLength = dataIn.readInt();
                            //read in the file name and extension
                            String fileName = dataIn.readUTF();
                            //create byte array to store the file
                            byte[] fileAsStream = new byte[fileLength];
                            int count = 0;
                            while (count < fileLength) {
                                int bytesRead = dataIn.read(fileAsStream, count, fileAsStream.length - count);
                                //debug reciving the file
                                //System.out.println("[Server] " + "bytesRead: " + bytesRead);
                                if (bytesRead == -1) {
                                    System.out.println("[Server] " + "didn't get a complete file");
                                }
                                count += bytesRead;
                            }   //debug the file that was sent
                            //System.out.println("[Server] " + Arrays.toString(fileAsStream));

                            //recive the dice roll boolean
                            boolean justRolledDice = dataIn.readBoolean();

                            //check if a stop is requested
                            if (!stopRequested) {
                                //send the new chat and file out to all the clients
                                for (ServerSideConnection client : clients) {
                                    //debug how many times it was sent
                                    //System.out.println("[CatanServer] " +"Sent it");
                                    //System.out.println("[CatanServer] " +"\nCurrent chat is :\n" + chat);
                                    client.sendFile(chat, fileAsStream, fileName, justRolledDice);

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
                            } else {
                                clients[clientID - 1].sendColourResponse(-1);
                            }

                            //check if this is the last client to select their colour
                            if (availableColours.isEmpty()) {
                                //then tell the first client to begin
                                clients[0].sendBoolean(true, 4); //incluse the messagy type 4 (startup command)

                                //debug the data coming in
                                //System.out.println("[Server] " + "Send begin command to Client 1");
                            }

                            break;
                        //if the server is getting a stop command
                        case 4:

                            //debug the stop reqesting
                            //System.out.println("[Server] got stop");
                            //tell all the threads to die
                            for (ServerSideConnection ssc : clients) {
                                ssc.requestStop();
                                //System.out.println("[Server SSC-" + ssc.clientID + "] stopping");
                            }

                            //TODO: What is causeing the GameFrame to be visable after stopping
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
                                if (client.clientID != onlineModeOfSender) {
                                    client.sendDomesticTradeData(onlineModeOfSender, playerStartedDomestic, playerSelectedForTrade, domesticTradeMode, tradeCardsGivePlayerStartedDomestic, tradeCardsReceivePlayerStartedDomestic, tradeCardsAlreadyHadPlayerStartedDomestic, tradeCardsAlreadyHadPlayerSelected);
                                }
                            }

                            break;
                        default:
                            break;
                    }
                }
            } catch (IOException e) {
                System.out.println("[Server] " + "IOException from SSC run() for ID#" + clientID);
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
                System.out.println("[Server] " + "IOException from SSC sendNewString()");
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
        public void sendFile(String msg, byte[] fileData, String fileName, boolean justRolledDice) {
            try {
                dataOut.writeInt(2); //tell the client they are reciving a file and chat message
                dataOut.writeUTF(msg);
                dataOut.writeInt(fileData.length); //send the length of the file
                dataOut.writeUTF(fileName); //send the file name
                dataOut.write(fileData, 0, fileData.length); //send the file
                dataOut.writeBoolean(justRolledDice); //send whether or not the dice animation needs to be set
                dataOut.flush();
            } catch (IOException e) {
                System.out.println("[Server] " + "IOException from SSC sendNewString()");
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
                System.out.println("[Server] " + "IOException from SSC sendDomesticTradeData()");
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
                System.out.println("[Server] " + "IOException from SSC sendBoolean()");
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
                System.out.println("[Server] " + "IOException from SSC sendColourResponse()");
            }
        }

    }

}
