/*
 * Lukas Krampitz
 * Jul 30, 2024
 * A secondary project for the dedicated serverside software to run the catan server
 */
package settlerserver;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author Tacitor
 */
public class SettlerServer {

    //The recieving Socket
    private ServerSocket leSocket;
    //the number of clients currently connected
    private int latestClient;
    private boolean stopRequested;
    private static boolean startSettlerServer = false;
    //An array of all the clients
    private ArrayList<ServerSideConnection> aggregationClients;
    private ArrayList<CatanServer> serverList;

    public static final int LOBBY_AGGREGATION_PORT_NUM = 25570;
    private static SettlerServer lobbyAggregation;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ColourPrint.printPurple("Please run command \"/start\" to begin.");
        System.out.println("If not started SettlerServer will exit in 10 seconds.");
        long startTime = System.currentTimeMillis();

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        
        String s;

        while (!startSettlerServer && (System.currentTimeMillis() - startTime) < 10000) {

            //check in periodically
            try {
                Thread.sleep(100);

                if (bufferedReader.ready()) {
                    s = bufferedReader.readLine();

                    if (s.equalsIgnoreCase("")) {
                        //Do nothing if the input is an empty String
                    } else if (s.equalsIgnoreCase("/start")) {
                        startSettlerServer = true;
                    } else {
                        ColourPrint.printRed("[Lobby Aggregation] The command " + s + " is not valid during start up.");
                    }
                }

            } catch (InterruptedException e) {
                ColourPrint.printRed("[Lobby Aggregation] InterruptedException from main()\n" + e);
            } catch (IOException e) {
                ColourPrint.printRed("[Lobby Aggregation] IOException from main()\n" + e);
            }

        }

        if (startSettlerServer) {
            //call to SettlerServer constuctor to make the aggregation server
            lobbyAggregation = new SettlerServer();

            lobbyAggregation.serverStartUp();
            lobbyAggregation.acceptConnections();
        } else {
            System.out.println("SettlerServer not started. Goodbye.");
        }

    }

    /**
     * Will propagate any statistic change in the CatanServer list. This will
     * send the statistics to all the Lobby aggregation CSC.
     */
    public static void propagateCatanServerChange() {
        lobbyAggregation.updateClientStats();
    }

    /**
     * Main constructor. Initializes internal fields and ArrayLists. Opens the
     * ServerSocket needed for future connections.
     */
    public SettlerServer() {
        System.out.println("[Lobby Aggregation] Creating service");
        //no clients yet
        latestClient = 0;

        //no stop request
        stopRequested = false;

        //init the client arrayList
        aggregationClients = new ArrayList<>();
        serverList = new ArrayList<>();

        //create the socket to listen 
        try {
            leSocket = new ServerSocket(LOBBY_AGGREGATION_PORT_NUM);
        } catch (IOException e) {
            ColourPrint.printRed("[Lobby Aggregation] IOException from SettlerServer constructor on socket creation \n" + e);
        }

    }

    /**
     * Creates four new CatanServer instances.
     */
    private void serverStartUp() {
        for (int i = 0; i < 4; i++) {
            serverList.add(new CatanServer());
        }
    }

    /**
     * Restart a given CatanServer. With the specific catanServerID, and the new
     * max number of clients a CatanServer is restarted gracefully. If
     * successful it is made ready to accept connections once again.
     *
     * @param catanServerID
     * @param maxClients
     */
    private void serverRestart(int catanServerID, int maxClients) {
        CatanServer cs = null;

        for (CatanServer c : serverList) {
            if (c.getCatanServerID() == catanServerID) {
                cs = c;
            }
        }

        if (cs != null) {
            boolean readyToAcceptConnections = cs.requestRestart(maxClients);

            if (readyToAcceptConnections) {
                //create a new thread for the server
                Thread t = new Thread(cs::acceptConnections);
                t.setName("[Server " + cs.getCatanServerID() + "]");

                //start running the server
                t.start();
            }
        } else {
            ColourPrint.printRed("[Lobby Aggregation] ERROR: Could not find a matching server with a catanServerID of: " + catanServerID);
        }

    }

    /**
     * Listens and accepts new connections from sockets.
     */
    private void acceptConnections() {
        try {
            System.out.println("[Lobby Aggregation] Listening for connections...");

            //wait cli input
            Thread t = new Thread(() -> {
                scannInput();
            });
            t.setName("[Lobby Aggregation] scannInput()");
            t.start();

            while (!stopRequested) {
                //create a reciving socket on the server side
                Socket s = leSocket.accept();

                //only add the socket if there is no stop request
                if (!stopRequested) {
                    //count it as a client
                    latestClient++;

                    System.out.println("[Lobby Aggregation] Client #" + latestClient + " has connected with an IP of: " + s.getInetAddress());
                    //create a new SSC for to keep track of that incoming socket
                    ServerSideConnection ssc = new ServerSideConnection(s, latestClient);

                    //save that new ssc to the ArrayList of clients
                    aggregationClients.add(ssc);

                    //start a new thread just for that one client
                    Thread t_ssc = new Thread(ssc);
                    t_ssc.setName("[Lobby Aggregation: SSC" + latestClient + "]");
                    t_ssc.start();
                } else {
                    System.out.println("[Lobby Aggregation] Accepted and discarded an extra socket");
                }

            }
            ColourPrint.printGreen("[Lobby Aggregation] Stop was requested. Closing service.");

            //close the server socket so another can later be created
            leSocket.close();
        } catch (IOException e) {
            ColourPrint.printRed("[Lobby Aggregation] IOException from acceptConnections \n" + e);
        }
    }

    /**
     * Listens for input over the CLI and runs matching commands.
     */
    private void scannInput() {
        Scanner scanner = new Scanner(System.in);
        String[] s;

        while (!stopRequested) {
            s = scanner.nextLine().split(" ");

            if (s[0].equalsIgnoreCase("/stop")) {
                stopRequested = true;
                System.out.println("[Lobby Aggregation] Stop recieved");

                try {
                    Socket dummy = new Socket("localhost", LOBBY_AGGREGATION_PORT_NUM);
                    dummy.close();
                } catch (IOException e) {
                    ColourPrint.printRed("[Lobby Aggregation] IOException from SSC scannInput() on /stop");
                }

                stopClients();
                stopCatanServers();
            } else if (s[0].equalsIgnoreCase("/list")) {
                ColourPrint.printPurple("[Lobby Aggregation] aggregationClients: " + aggregationClients);
            } else if (s[0].equalsIgnoreCase("/restart") || s[0].equalsIgnoreCase("/r")) {
                try {
                    int lobbyID = Integer.parseInt(s[1]);
                    int maxClients = Integer.parseInt(s[2]);

                    serverRestart(lobbyID, maxClients);
                } catch (NumberFormatException e) {
                    ColourPrint.printRed("[Lobby Aggregation] NumberFormatException from SSC scannInput() on /restart\n" + e);
                } catch (ArrayIndexOutOfBoundsException e) {
                    ColourPrint.printRed("[Lobby Aggregation] ArrayIndexOutOfBoundsException from SSC scannInput() on /restart\n" + e
                            + "\nPlease incluse a catanServerID and a max number of clients seperated by a space.");
                }
            } else if (s[0].equalsIgnoreCase("/help")) {
                ColourPrint.printPurple("[Lobby Aggregation] Valid commands are: \"/stop\", \"/list\", \"/restart\" (or \"/r\"), \"/update\", and \"/help\"");
            } else if (s[0].equalsIgnoreCase("/update")) {
                propagateCatanServerChange();
            } else if (s[0].equalsIgnoreCase("/start")) {
                ColourPrint.printRed("[Lobby Aggregation] The command \"/start\" is ONLY valid during start up.");
            } else if (s[0].equalsIgnoreCase("")) {
                //Do nothing if the input is an empty String
            } else {
                ColourPrint.printRed("[Lobby Aggregation] The command " + s[0] + " is not recognised. Use \"/help\" for a list of valid commands.");
            }
        }

        scanner.close();
    }

    /**
     * Triggers request to terminate all Lobby Aggregation clients.
     */
    private void stopClients() {
        for (ServerSideConnection leSSC : aggregationClients) {
            if (!leSSC.stopRequested) {
                leSSC.requestStop();
                leSSC.sendSscStop();
            }
        }
    }

    /**
     * Triggers request to terminate all CatanServers.
     */
    private void stopCatanServers() {
        for (CatanServer cs : serverList) {
            if (cs != null) {
                cs.requestStop();
            }
        }
    }

    /**
     * Propagates the latest lobby statistics of the CatanServers to any Lobby
     * Aggregation clients that are connected.
     */
    private void updateClientStats() {
        System.out.println("[Lobby Aggregation] call to updateClientStats()");
        for (ServerSideConnection leSSC : aggregationClients) {
            if (!leSSC.stopRequested) {
                leSSC.sendLobbyStats();
            }
        }
    }

    /**
     * When lobby statistics are requested AFTER they are sent
     * monitorCompletedServers() can be called. This will search and find any
     * CatanServer that has a non-zero max player count, with at least one CSC
     * that has had a stop requested. If such a CatanServer exists it will be
     * restarted to the empty state
     */
    private void checkCompletedServers() {
        for (CatanServer cs : serverList) {
            if (cs.getMaxClients() != 0 && cs.hasStoppedClient()) {
                cs.requestRestart(0);
            }
        }
    }

    /**
     * The instance of a socket on the Lobby Aggregation side of the server.
     */
    private class ServerSideConnection implements Runnable {

        private int laID;
        private Socket socket;
        private DataInputStream dataIn;
        private DataOutputStream dataOut;

        //has the thread been reqested to stop?
        private boolean stopRequested = false;

        /**
         * Main constructor
         *
         * @param socket
         */
        public ServerSideConnection(Socket socket, int laID) {
            this.socket = socket;
            this.laID = laID;

            //setup the data streams
            try {
                dataIn = new DataInputStream(socket.getInputStream());
                dataOut = new DataOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                ColourPrint.printRed("[Lobby Aggregation] Client #" + laID + " had IOException from SSC constuctor for client with an IP of: " + this.socket.getInetAddress());
            }
        }

        /**
         * Sets flag for termination of the SSC
         */
        public void requestStop() {
            stopRequested = true;
        }

        /**
         * Main process for the SSC. Will continue to loop until a stop has been
         * requested. Deals with incoming data over the DataInputStream and
         * takes action.
         */
        @Override
        public void run() {
            try {
                //when a client first connects send it's ID
                dataOut.writeInt(laID);

                //loop state after all startup business is complete
                while (!stopRequested) {

                    //accept a new message
                    int type = dataIn.readInt(); //get the type of transmision

                    switch (type) {
                        case 1: //if the client sent a lobby stats request
                            //System.out.println("[Lobby Aggregation] Lobby stats request command #1 in SSC run() for ID#" + laID);
                            sendLobbyStats();
                            break;

                        case 2: //if the client sent a request to restart an empty server and change the number of players
                            //read in the catanServerID
                            int catanServerID = dataIn.readInt();
                            //read in the maxClients
                            int maxClients = dataIn.readInt();

                            serverRestart(catanServerID, maxClients);
                            break;

                        case 3: //for a CSC triggerd termination
                            System.out.println("[Lobby Aggregation] Stop request command #3 in SSC run() for ID#" + laID);

                            stopRequested = true;
                            sendSscStop();
                            break;
                        case 4: //for a SSC close.
                            System.out.println("[Lobby Aggregation] Stop request command #4 in SSC run() for ID#" + laID);
                            break;
                    }
                }

                //We're done here, wrap this up
                dataIn.close();
                dataOut.close();
                socket.close();

                aggregationClients.remove(this);

                System.out.println("[Lobby Aggregation] Client #" + laID + " is done SSC run() for client with an IP of: " + this.socket.getInetAddress() + "\n");

            } catch (IOException e) {
                ColourPrint.printRed("[Lobby Aggregation] IOException from SSC run() for ID#" + laID + "\n" + e);
            }
        }

        /**
         * A simplified string representation that displays the IPv4 address of
         * this socket.
         *
         * @return
         */
        @Override
        public String toString() {
            return "ServerSideConnection: " + socket.getInetAddress();
        }

        /**
         * Trigger termination with a #4 stop command to the CSC
         */
        private void sendSscStop() {
            try {
                dataOut.writeInt(4); //tell the client what type of message they are reciving
                dataOut.flush();
            } catch (IOException e) {
                ColourPrint.printRed("[Lobby Aggregation] IOException from SSC sendSscStop() for ID#" + laID + "\n" + e);
            }
        }

        /**
         * Send all the statistics about the game servers to the CSC.
         *
         * @param msg
         */
        public void sendLobbyStats() {
            int[] colours;

            try {
                dataOut.writeInt(1); //tell the client what type of message they are reciving

                dataOut.writeInt(serverList.size());
                for (CatanServer cs : serverList) {
                    colours = cs.getColoursTaken();

                    dataOut.writeInt(cs.getCatanServerID());
                    dataOut.writeInt(cs.getMaxClients());
                    dataOut.writeInt(colours.length);
                    for (int i = 0; i < colours.length; i++) {
                        dataOut.writeInt(colours[i]);
                    }

                }

                dataOut.flush();
            } catch (IOException e) {
                ColourPrint.printRed("[Lobby Aggregation] IOException from SSC sendLobbyStats() for ID#" + laID + "\n" + e);
            }

            checkCompletedServers();
        }
    }

}
