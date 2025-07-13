/*
 * Lukas Krampitz
 * Mar 20, 2021
 * Hold the main method and the clock for animation frames
 */
package krampitzkreutzwisersettlersofcatan;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import krampitzkreutzwisersettlersofcatan.gui.GamePanel;
import krampitzkreutzwisersettlersofcatan.gui.SDMenuFrame;

/**
 *
 * @author Tacitor
 */
public class Catan {

    public static GamePanel gamePanel;
    public static SDMenuFrame sDMenuFrame;
    public static final String SAVE_FILE_VER = "V15"; //the save file version needed
    public static final String GAME_VER = "pre-v6.2.0 - Native Neatened Networking"; //the version of the game/program

    public static final boolean DEBUG_ONLINE_MODE = true; //if this is true then the game will lauch in decorated windowed 720p
    public static final boolean DEBUG_SETTLER_SERVER = true; //if this is true then the game will connect to localhost, and not the specified URL/IP

    //fast pulse vars
    private static long prevTime;
    private static int fastPulseTime; //the number of miliseconds between fast pulses

    public static void main(String[] args) throws InterruptedException {
        //TODO: Try out migrating to latest Apache NetBean with a JDK SE 21 (latest LTS)

        System.out.println("Catan startup");
        //record the time
        prevTime = System.currentTimeMillis();

        sDMenuFrame = new SDMenuFrame();
        sDMenuFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                sDMenuFrame.getActiveJPanel().exitBtnActionPerformed();
            }
        });

        sDMenuFrame.setVisible(true);
        updateGamePanel();

        //Ensure that when the GameFrame is closed it will have any networking and sockets closed.
        sDMenuFrame.getSDMainMenuPanel().getGameFrame().addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                boolean doClose = gamePanel.backNoSaveBtnClicked();
                if (doClose) {
                    sDMenuFrame.getSDMainMenuPanel().getGameFrame().dispose();
                }
            }
        });

        //set up the fast game pulse
        FastGamePulseRunnable fastGamePulseRunnable = new FastGamePulseRunnable();
        fastGamePulseRunnable.setDaemon(true);
        fastGamePulseRunnable.start();

    }

    public static void updateGamePanel() {
        gamePanel = sDMenuFrame.getSDMainMenuPanel().getGameFrame().getGamePanel();
    }

    /**
     * A 10ms pulse clock for the game Faster than the 1 second animation
     * pulse/Tick update
     */
    private static void fastGamePulse() {
        //test the pulse regularity
        fastPulseTime = (int) (System.currentTimeMillis() - prevTime);
        //record new time
        prevTime = System.currentTimeMillis();

        //now call the game panel
        gamePanel.catanFastTickUpdate();
        gamePanel.catanAnimationTickUpdate(); //for animation
        sDMenuFrame.repaint();

    }

    private static class FastGamePulseRunnable extends Thread implements Runnable {

        private boolean stopRequested = false;

        public synchronized void requestStop() {
            stopRequested = true;
        }

        @Override
        public void run() {
            this.setName("FastGamePulseRunnable");

            //check if this thread should stop
            while (!stopRequested) {

                //apply the pulse
                fastGamePulse();

                try {
                    //wait 15ms to apply it again
                    Thread.sleep(15l);
                } catch (InterruptedException ex) {
                    System.out.println("ERROR: " + ex);
                }
            }
        }
    }

}
