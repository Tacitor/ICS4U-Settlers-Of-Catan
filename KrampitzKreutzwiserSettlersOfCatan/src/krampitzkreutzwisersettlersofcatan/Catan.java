/*
 * Lukas Krampitz
 * Mar 20, 2021
 * Hold the main method and the clock for animation frames
 */
package krampitzkreutzwisersettlersofcatan;

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
    public static final String GAME_VER = "v6.1.0 - Main Menu Modification"; //the version of the game/program

    public static final boolean DEBUG_ONLINE_MODE = false; //if this is true then the game will lauch in decorated windowed 720p

    //fast pulse vars
    private static long prevTime;
    private static int fastPulseTime; //the number of miliseconds between fast pulses

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Catan startup");
        //record the time
        prevTime = System.currentTimeMillis();

        sDMenuFrame = new SDMenuFrame();
        sDMenuFrame.setVisible(true);
        updateGamePanel();

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
