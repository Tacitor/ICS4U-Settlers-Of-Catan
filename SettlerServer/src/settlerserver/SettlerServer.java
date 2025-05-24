/*
 * Lukas Krampitz
 * Jul 30, 2024
 * A secondary project for the dedicated serverside software to run the catan server
 */
package settlerserver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author Tacitor
 */
public class SettlerServer {

    public static final boolean DEBUG_OUTPUT = true;

    //The recieving Socket
    private ServerSocket leSocket;
    //the number of clients currently connected
    private int latestClient;
    private boolean stopRequested;
    //An array of all the clients
    private ArrayList<ServerSideConnection> aggregationClients;
    private ArrayList<CatanServer> serverList;

    private static final int LOBBY_AGGREGATION_PORT_NUM = 25570;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //call to SettlerServer constuctor to make the aggregation server
        SettlerServer lobbyAggregation = new SettlerServer();

        //TODO: add a method here that spins up the 4 catanServer lobbies
        lobbyAggregation.serverStartUp(2, 25571);

        lobbyAggregation.acceptConnections();

    }

    /**
     *
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
            System.out.println("[Lobby Aggregation] " + "IOException from SettlerServer constructor on socket creation \n" + e);
        }

    }

    /**
     *
     */
    private void serverStartUp(int numPlayers, int port) {
        //@OUTDATED spin up a main host server. This server will always be accepting connections (limit to 6 from one IP)
        //@OUTDATED In this server on port 25570 it will provide status updates on the lobbies.
        //@OUTDATED The catan user end will disconnect from this server end once exiting the join lobby menue or once entered into a game.
        CatanServer leServer = new CatanServer(numPlayers, port);
        serverList.add(leServer);

        //create a new thread for the server
        Thread t = new Thread(() -> {
            leServer.acceptConnections();
        });
        t.setName("[Server " + port + "]");

        //start running the server
        t.start();

        //Add some empty servers
        serverList.add(new CatanServer());
        serverList.add(new CatanServer());
        serverList.add(new CatanServer());
    }

    /**
     *
     */
    private void acceptConnections() {
        try {
            System.out.println("[Lobby Aggregation] " + "Listening for connections...");

            //wait cli input
            Thread t = new Thread(() -> {
                scannInput();
            });
            t.setName("[Lobby Aggregation] scannInput()");
            t.start();

            //wait until all the clients have connected
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
            System.out.println("[Lobby Aggregation] " + "Stop was requested. Closing service.");

            //close the server socket so another can later be created
            leSocket.close();
        } catch (IOException e) {
            System.out.println("[Lobby Aggregation] " + "IOException from acceptConnections \n" + e);
        }
    }

    /**
     *
     */
    private void scannInput() {
        Scanner scanner = new Scanner(System.in);
        String s;

        while (!stopRequested) {
            s = scanner.nextLine();

            if (s.equalsIgnoreCase("/stop")) {
                stopRequested = true;
                System.out.println("[Lobby Aggregation] Stop recieved");

                try {
                    Socket dummy = new Socket("localhost", LOBBY_AGGREGATION_PORT_NUM);
                    dummy.close();
                } catch (IOException e) {
                    System.out.println("[Lobby Aggregation] IOException from SSC scannInput() on /stop");
                }

                stopClients();
                stopCatanServers();
            } else if (s.equalsIgnoreCase("/list")) {
                System.out.println("[Lobby Aggregation] aggregationClients: " + aggregationClients);
            } else if (s.equalsIgnoreCase("")) {
                //Do nothing if the input is an empty String
            } else {
                System.out.println("[Lobby Aggregation] The command " + s + " is not recognised");
            }
        }
    }

    /**
     *
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
     *
     */
    private void stopCatanServers() {
        for (CatanServer cs : serverList) {
            if (cs != null) {
                cs.requestStop();
            }
        }
    }

    /**
     *
     */
    private class ServerSideConnection implements Runnable {

        private int laID;
        private Socket socket;
        private DataInputStream dataIn;
        private DataOutputStream dataOut;

        //has the thread been reqested to stop?
        private boolean stopRequested = false;

        /**
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
                System.out.println("[Lobby Aggregation] Client #" + latestClient + " had IOException from SSC constuctor for client with an IP of: " + this.socket.getInetAddress());
            }
        }

        /**
         *
         */
        public void requestStop() {
            stopRequested = true;
        }

        /**
         *
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
                            System.out.println("[Lobby Aggregation] Lobby stats request command #1 in SSC run() for ID#" + laID);
                            sendLobbyStats();
                            break;

                        case 2: //if the client sent a request to restart an empty server and change the number of players
                            // tell the client when it is done
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

                System.out.println("[Lobby Aggregation] Client #" + latestClient + " is done SSC run() for client with an IP of: " + this.socket.getInetAddress() + "\n");

            } catch (IOException e) {
                System.out.println("[Lobby Aggregation] IOException from SSC run() for ID#" + laID + "\n" + e);
            }
        }

        /**
         *
         * @return
         */
        @Override
        public String toString() {
            return "ServerSideConnection: " + socket.getInetAddress();
        }

        /**
         *
         */
        private void sendSscStop() {
            try {
                dataOut.writeInt(4); //tell the client what type of message they are reciving
                dataOut.flush();
            } catch (IOException e) {
                System.out.println("[Lobby Aggregation] IOException from SSC sendSscStop() for ID#" + laID + "\n" + e);
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

                    dataOut.writeInt(cs.getSocketPort());
                    dataOut.writeInt(cs.getMaxClients());
                    dataOut.writeInt(colours.length);
                    for (int i = 0; i < colours.length; i++) {
                        dataOut.writeInt(colours[i]);
                    }

                }

                dataOut.flush();
            } catch (IOException e) {
                System.out.println("[Lobby Aggregation] IOException from SSC sendLobbyStats() for ID#" + laID + "\n" + e);
            }
        }
    }

}
