/*
 * Lukas Krampitz
 * Jul 30, 2024
 * A secondary project for the dedicated serverside software to run the catan server
 */
package settlerserver;

/**
 *
 * @author Tacitor
 */
public class SettlerServer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        System.out.println("Settting up server for 2 players");
        serverStartUp(2, 25570);

    }

    /**
     * Create the local server using the old @Depricated class. This going
     * forward only the server in the new SettlerServer project and package is
     * maintained.
     */
    private static void serverStartUp(int numPlayers, int port) {
        CatanServer leServer = new CatanServer(numPlayers, port);

        //create a new thread for the server
        Thread t = new Thread(() -> {
            leServer.acceptConnections();
        });

        //start running the server
        t.start();
    }

}
