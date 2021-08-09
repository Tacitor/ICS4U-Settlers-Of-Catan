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

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Catan startup");

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

        while (true) {
            clock++;
            gamePanel.fire();
            //gamePanel.fire();
            Thread.sleep(1000); //time a spent sleeping is subject to change
            //the way sleep is envoked is also subject to change.
        }
    }
    
    public static void updateGamePanel() {
        gamePanel = menu.getGameFrame().getGamePanel();
    }

}
