/*
 * Lukas Krampitz
 * Mar 20, 2021
 * Hold the main method and the clock for animation frames
 */
package krampitzkreutzwisersettlersofcatan;

import krampitzkreutzwisersettlersofcatan.gui.GamePanel;
import krampitzkreutzwisersettlersofcatan.gui.MainMenu;

/**
 *
 * @author Tacitor
 */
public class Catan {

    public static int clock = 0;
    public static GamePanel gamePanel;
    public static MainMenu menu;
    public static final String SAVE_FILE_VER = "V14"; //the save file version needed    
    public static final String GAME_VER = "prev5.1.3 - V5 Jason Patch"; //the version of the game/program

    //fast pulse vars
    private static long prevTime;
    private static int fastPulseTime; //the number of miliseconds between fast pulses

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Catan startup");
        //record the time
        prevTime = System.currentTimeMillis();

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
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        menu = new MainMenu();
        menu.setVisible(true);
        updateGamePanel();

        //set up the fast game pulse
        FastGamePulseRunnable fastGamePulseRunnable = new FastGamePulseRunnable();
        fastGamePulseRunnable.setDaemon(true);
        fastGamePulseRunnable.start();

        //run the slow game pulse
        while (true) {
            clock++;
            gamePanel.catanTickUpdate();
            Thread.sleep(1000); //time a spent sleeping is subject to change
            //the way sleep is envoked is also subject to change.
        }
    }

    public static void updateGamePanel() {
        gamePanel = menu.getGameFrame().getGamePanel();
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
                    //wait 10ms to apply it again
                    Thread.sleep(15l);
                } catch (InterruptedException ex) {
                    System.out.println("ERROR: " + ex);
                }
            }
        }
    }

}
