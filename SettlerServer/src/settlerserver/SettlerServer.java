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

    //The recieving Socket
    private ServerSocket leSocket;
    //the number of clients currently connected
    private int numClients;
    private boolean stopRequested;
    //An array of all the clients
    private ArrayList<ServerSideConnection> aggregationClients;
    private ArrayList<CatanServer> serverList;

    private static final int LOBBY_AGGREGATION_PORT_NUM = 25570;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //add a method here that open up the lobby aggreagetion sevrer
        startLobbyAggregation();

    }

    /**
     *
     */
    private static void startLobbyAggregation() {
        //call to SettlerServer constuctor to make the aggregation server
        SettlerServer lobbyAggregation = new SettlerServer();

        //TODO: add a method here that spins up the 4 catanServer lobbies
        lobbyAggregation.serverStartUp(2, 25571);

        //start it in a new thread
        lobbyAggregation.acceptConnections();
    }

    /**
     *
     */
    public SettlerServer() {
        System.out.println("[Lobby Aggregation] Creating service");
        //no clients yet
        numClients = 0;

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
     * TEMP: Create a single lobby by default
     */
    private void serverStartUp(int numPlayers, int port) {
        //@OUTDATED spin up a main host server. This server will always be accepting connections (limit to 6 from one IP)
        //@OUTDATED In this server on port 25570 it will provide status updates on the lobbies.
        //@OUTDATED The catan user end will disconnect from this server end once exiting the join lobby menue or once entered into a game.

        System.out.println("Settting up server for " + numPlayers + " players");

        CatanServer leServer = new CatanServer(numPlayers, port);
        serverList.add(leServer);

        //create a new thread for the server
        Thread t = new Thread(() -> {
            leServer.acceptConnections();
        });
        t.setName("[Server " + port + "]");

        //start running the server
        t.start();
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
                    numClients++;

                    System.out.println("[Lobby Aggregation] " + "Client #" + numClients + " has connected with an IP of: " + s.getInetAddress());
                    //create a new SSC for to keep track of that incoming socket
                    ServerSideConnection ssc = new ServerSideConnection(s);

                    //save that new ssc to the ArrayList of clients
                    aggregationClients.add(ssc);

                    //start a new thread just for that one client
                    Thread t_ssc = new Thread(ssc);
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
                } catch (IOException ex) {
                    System.out.println("[Lobby Aggregation] IOException from SSC scannInput() on /stop");
                }

                stopClients();
                stopCatanServers();
            }
        }
    }

    /**
     *
     */
    private void stopClients() {
        for (ServerSideConnection leSSC : aggregationClients) {
            leSSC.requestStop();
        }
    }

    /**
     *
     */
    private void stopCatanServers() {
        for (CatanServer cs : serverList) {
            cs.requestStop();
        }
    }

    /**
     *
     */
    private class ServerSideConnection implements Runnable {

        private Socket socket;
        private DataInputStream dataIn;
        private DataOutputStream dataOut;

        //has the thread been reqested to stop?
        private boolean stopRequested = false;

        /**
         *
         * @param socket
         */
        public ServerSideConnection(Socket socket) {
            this.socket = socket;

            //setup the data streams
            try {
                dataIn = new DataInputStream(socket.getInputStream());
                dataOut = new DataOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                System.out.println("[Lobby Aggregation] IOException from SSC constuctor for client with an IP of " + this.socket.getInetAddress());
            }
        }

        /**
         *
         */
        public void requestStop() {
            stopRequested = true;

            try {
                dataIn.close();
                dataOut.close();

                //TODO: Remove the ssc from the aggregationClients list and decriment the numClients counter.
                System.out.println("requestStop()");
            } catch (IOException ex) {
                System.out.println("[Lobby Aggregation] IOException from SSC requestStop() for client with an IP of " + this.socket.getInetAddress());
            }
        }

        /**
         *
         */
        @Override
        public void run() {
            //TODO: Add a csc on the JoinLobbyPanel
            System.out.println("[Lobby Aggregation] Done from SSC run() for client with an IP of " + this.socket.getInetAddress());
        }

        /**
         *
         * @return
         */
        @Override
        public String toString() {
            return "ServerSideConnection: " + socket.getInetAddress();
        }
    }

}
