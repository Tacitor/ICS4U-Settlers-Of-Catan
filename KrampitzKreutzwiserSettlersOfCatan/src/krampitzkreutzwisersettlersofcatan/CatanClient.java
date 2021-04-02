/*
 * Lukas Krampitz
 * Mar 27, 2021
 * 
 */
package krampitzkreutzwisersettlersofcatan;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.*;
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

    private int width;
    private int height;
    private Container contentPane;
    private JTextArea header;
    private JTextArea messageRecived;
    private JTextArea messageToSend;
    private JButton sendBtn;
    private JButton fileBtn;

    private int clientID;
    private int totalClientNum; //the number of total clients that will be connected to the server
    private String ip; //the ip adress the client will try to connect to
    private boolean successfulConnect = false; //did this client successfully connect to the server
    private GameFrame theGameFrame;

    private String chat;
    private boolean buttonEnabled;
    private boolean justPressedSend = false; //if this client waiting for the first transmision from the server

    private ClientSideConnection csc; //the socket type var to hold the connection for this CatanClient

    /**
     * Constructor
     *
     * @param width
     * @param height
     * @param ip
     * @param gameFrame
     */
    public CatanClient(int width, int height, String ip, GameFrame gameFrame) {
        /* Set the Windows 10 look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            System.out.println("[Client] " + "Error loading Windows Look and feel");
        }

        this.width = width;
        this.height = height;
        contentPane = this.getContentPane();
        header = new JTextArea();
        messageRecived = new JTextArea();
        messageToSend = new JTextArea();
        sendBtn = new JButton();
        fileBtn = new JButton();
        this.ip = ip;
        theGameFrame = gameFrame;
    }

    /**
     * Set the icon for the JFRame
     */
    private void setIcon() {
        this.setIconImage(ImageRef.ICON);
    }

    public void setUpGUI() {
        //get up the GUI
        this.setSize(width, height);
        this.setTitle("Catan Socket Test - Client #" + clientID);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        contentPane.setLayout(new GridLayout(1, 5, 10, 10));
        contentPane.add(header);
        contentPane.add(messageRecived);
        contentPane.add(messageToSend);
        contentPane.add(sendBtn);
        contentPane.add(fileBtn);
        header.setText("Most recent message: ");
        header.setWrapStyleWord(true);
        header.setLineWrap(true);
        header.setEditable(false);
        header.setFont(new Font("Arial", Font.PLAIN, 12));
        messageRecived.setWrapStyleWord(true);
        messageRecived.setLineWrap(true);
        messageRecived.setEditable(false);
        messageRecived.setFont(new Font("Arial", Font.PLAIN, 12));
        messageToSend.setText("Type here...");
        messageToSend.setWrapStyleWord(true);
        messageToSend.setLineWrap(true);
        messageToSend.setEditable(true);
        messageToSend.setFont(new Font("Arial", Font.PLAIN, 12));
        sendBtn.setText("Send Chat");
        fileBtn.setText("Send File");
        contentPane.setForeground(Color.green);
        contentPane.setBackground(Color.gray);
        setIcon();

        //specific behaviour for the client numbers
        if (clientID == 1) {
            header.setText("You are client number 1. Please wait for the rest of the clients to connect before starting\n\nMost recent message: -->");
            //go ahead and wait for the server to send the startup signal
            Thread t = new Thread(() -> {
                startUpClient1();
            });
            t.start();
        } else {
            header.setText("You are client number " + clientID + ". Please wait for client#1 to begin after the rest of the clients have connected\n\nMost recent message: -->");
            //wait for a message to come through
            Thread t = new Thread(() -> {
                //never stop listening
                while (true) {
                    regularRecive();
                }
            });
            t.start();
        }

        buttonEnabled = false;
        updateButtons();
        this.setVisible(true);
    }

    public boolean connectToServer() {
        //set up the socket
        csc = new ClientSideConnection();

        return successfulConnect;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setUpButton() {
        //create action listener for when the button is clicked to send a message
        ActionListener al = (ActionEvent e) -> {
            JButton button = (JButton) e.getSource();
            String buttonString = button.getText();

            //if the player sends a chat
            if (buttonString.equals("Send Chat")) {

                justPressedSend = true;

                System.out.println("[Client " + clientID + "] " + "Sending the message: " + messageToSend.getText());

                updateButtons();

                //send the message
                csc.sendNewString(messageToSend.getText());

                //clear the chat field
                messageToSend.setText("");

            } else if (buttonString.equals("Send File")) {

                JFileChooser saveFileLoader = new JFileChooser();
                //set up the file choose and call it
                saveFileLoader.setDialogTitle("Select a Save File to Open:");
                int userLoadSelection = saveFileLoader.showOpenDialog(null);

                if (userLoadSelection == JFileChooser.APPROVE_OPTION) {

                    updateButtons();

                    sendFile(saveFileLoader.getSelectedFile().getPath());

                }
            }
        };

        sendBtn.addActionListener(al);
        fileBtn.addActionListener(al);
    }

    public void updateButtons() {
        sendBtn.setEnabled(buttonEnabled);
        fileBtn.setEnabled(buttonEnabled);
    }

    /**
     * Enables the button for client 1 when the server sends the signal
     */
    public void startUpClient1() {

        //place to store the boolean
        //and assign it to the value the server sends
        boolean recivedBoolean = csc.reciveBoolean();

        //set the button to the value
        buttonEnabled = recivedBoolean;
        updateButtons();

        //setup the game that will be played
        theGameFrame.resetGamePanel();
        //make it visible
        theGameFrame.setVisible(true);
        theGameFrame.getMainMenu().getNewOnlineGameMenu().setVisible(false);

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

        //send it to the server
        //start listening
        //never stop listening
        while (true) {
            regularRecive();
        }
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
            csc.sendFileStream(fileBytes, fileName); //send the file

            //clear the chat field
            messageToSend.setText("");

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
            case 1:
                //wait for newest message from other client
                String msg = csc.reciveNewString();
                messageRecived.setText(msg);
                //header.setText("C " + justPressedSend); //debug the turn detection
                buttonEnabled = true;
                break;
            case 2:
                //else if type is 2
                //recive the file
                FileTypeRecieve fileTypeRecieve = csc.recieveFile();
                messageRecived.setText(fileTypeRecieve.getChat());
                //header.setText("D " + justPressedSend); //debug the turn detection

                //now only actually save the file if THIS client didn't send it
                if (!justPressedSend) {

                    //get just the name of the file
                    String fileName = fileTypeRecieve.getFileName();
                    //debug the file and how it was recived
                    //System.out.println("Regular Got file:\n" + Arrays.toString(fileTypeRecieve.getFile()));
                    //write the file
                    try {
                        String saveToPath = System.getProperty("user.home")
                                + File.separator + "AppData" + File.separator + "Roaming" + File.separator + "SettlerDevs" + File.separator + "Catan";

                        //ensure the directory is there
                        Files.createDirectories(Paths.get(ONLINE_SAVE_LOCATION));

                        //Create and output stream at the directory
                        FileOutputStream fos = new FileOutputStream(ONLINE_SAVE_LOCATION + ONLINE_SAVE_NAME + clientID + ONLINE_SAVE_TYPE);

                        //write the file
                        fos.write(fileTypeRecieve.getFile(), 0, fileTypeRecieve.getFile().length);
                        
                        fos.flush();

                        //close it
                        fos.close();

                        //setup the game that will be played
                        theGameFrame.resetGamePanel();
                        //load the save
                        theGameFrame.getGamePanel().load(ONLINE_SAVE_LOCATION + ONLINE_SAVE_NAME + clientID + ONLINE_SAVE_TYPE);
                        //make it visible
                        theGameFrame.setVisible(true);
                        theGameFrame.getMainMenu().getJoinOnlineGameMenu().setVisible(false);

                    } catch (FileNotFoundException exception) {
                        JOptionPane.showMessageDialog(null, "There was an error loading the save file:\n" + exception, "Loading Error", JOptionPane.ERROR_MESSAGE);
                    } catch (IOException exception) {
                        JOptionPane.showMessageDialog(null, "There was an IOException loading the save file:\n" + exception, "Loading Error", JOptionPane.ERROR_MESSAGE);
                    }   //System.out.println("Chat is : \n" + fileTypeRecieve.getChat());
                    buttonEnabled = true;

                }
                break;
            default:
                buttonEnabled = false;
                break;
        }

        //if there was a special case for when a client sent the mesage just recived its not needed anymore
        if (justPressedSend) {
            justPressedSend = false;
        }

        updateButtons();
    }

    private class FileTypeRecieve {

        private byte[] file;
        private String chat;
        private String fileName;

        public FileTypeRecieve() {
            this.file = new byte[1];
            this.chat = "";
        }

        public FileTypeRecieve(byte[] file, String chat, String fileName) {
            this.file = file;
            this.chat = chat;
            this.fileName = fileName;
        }

        public String getChat() {
            return chat;
        }

        public byte[] getFile() {
            return file;
        }

        public String getFileName() {
            return fileName;
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
                socket = new Socket(ip, 25570);
                dataIn = new DataInputStream(socket.getInputStream());
                dataOut = new DataOutputStream(socket.getOutputStream());
                //now that a connection has been establichsed get the number for this client
                clientID = dataIn.readInt();
                //the the totalClientNum
                totalClientNum = dataIn.readInt();
                //get the starting chat
                chat = dataIn.readUTF();
                System.out.println("[Client " + clientID + "] " + "Connected to a server as Client #" + clientID);
                messageRecived.setText(chat);

                //if everything else was able to be done save the success
                successfulConnect = true;

            } catch (IOException e) {
                System.out.println("[Client " + clientID + "] " + "IOException from CSC contructor ");

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
                System.out.println("[Client " + clientID + "] " + "IOException from CSC sendNewString()");
            }
        }

        public void sendFileStream(byte[] fileStream, String fileName) {
            try {
                dataOut.writeInt(2); //tell the server that is it recieving a file
                dataOut.writeInt(fileStream.length); //send the length of the file
                dataOut.writeUTF(fileName); //send the name of the file including the extension
                dataOut.write(fileStream, 0, fileStream.length);
                dataOut.flush();
            } catch (IOException e) {
                System.out.println("[Client " + clientID + "] " + "IOException from CSC sendFileStream()");
            }
        }

        public FileTypeRecieve recieveFile() {
            String msg = "";
            byte[] file = new byte[1];
            String fileName = "";

            try {
                msg = dataIn.readUTF();
                //get the file length
                file = new byte[dataIn.readInt()];
                //get the fileName
                fileName = dataIn.readUTF();

                int count = 0;
                while (count < file.length) {
                    int bytesRead = dataIn.read(file, count, file.length - count);
                    System.out.println("[Client " + clientID + "] " + "bytesRead: " + bytesRead);
                    if (bytesRead == -1) {
                        System.out.println("[Client " + clientID + "] " + "didn't get a complete file");
                    }
                    count += bytesRead;
                }

            } catch (IOException ex) {
                System.out.println("[Client " + clientID + "] " + "IOException from CSC reciveNewString()");
            }

            return new FileTypeRecieve(file, msg, fileName);
        }

        public String reciveNewString() {
            String msg = "";

            try {
                msg = dataIn.readUTF();
            } catch (IOException ex) {
                System.out.println("[Client " + clientID + "] " + "IOException from CSC reciveNewString()");
            }

            return msg;
        }

        public int reciveType() {
            int msg = 0;

            try {
                msg = dataIn.readInt();
            } catch (IOException ex) {
                System.out.println("[Client " + clientID + "] " + "IOException from CSC reciveType():\n" + ex);
            }

            return msg;
        }

        public boolean reciveBoolean() {
            boolean bool = false;

            try {
                bool = dataIn.readBoolean();
            } catch (IOException ex) {
                System.out.println("[Client " + clientID + "] " + "IOException from CSC reciveBoolean()");
            }

            return bool;
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        System.out.println("[Client] " + "Hello World: Client");
        //CatanClient client = new CatanClient(700, 200, "localhost");
        //client.connectToServer();
        //client.setUpGUI();
        //client.setUpButton();
    }

}
