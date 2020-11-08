/*
 * Lukas Krampitz
 * Nov 8, 2020
 * The JPanel that is the main part of the game
 */
package krampitzkreutzwisersettlersofcatan;

import java.awt.event.*; 
import java.awt.*; 
import javax.swing.*; 

/**
 *
 * @author Tacitor
 */
public class GamePanel extends JPanel {
    
    private static JButton backBtn;
    private final GameFrame superFrame;
    
    
    public GamePanel(GameFrame frame) {
        superFrame = frame;
        backBtn = new JButton("Test");
        this.add(backBtn);
    }

    //overrides paintComponent in JPanel class
    //performs custom painting
    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);//does the necessary work to prepare the panel for drawing
        initComponents();
        draw(g); //add the custom drawing (the game)
    }

    //draw the game
    private void draw(Graphics g) {
        //the Graphics2D class is the class that handles all the drawing
        //must be casted from older Graphics class in order to have access to some newer methods
        Graphics2D g2d = (Graphics2D) g;
        //draw a string on the panel        
        g2d.drawString("Java 2D", 50, 50); //(text, x, y)        }
    }
    
    private void initComponents() {
        backBtn = new JButton("< Back");
        
        /*
        backBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backBtnActionPerformed(evt);
            }
        });
        */
        
        //this.setLayout(new GridLayout(500, 500));
        //backBtn.setPreferredSize(new Dimension(100, 100));
        this.add(backBtn);
        this.setBackground(Color.gray);
    }
    
    private void backBtnActionPerformed(java.awt.event.ActionEvent evt) {                                        
        // Hide this window and show the main menu
        superFrame.getMainMenu().setVisible(true);
        superFrame.setVisible(false);
        this.add(backBtn);
    }  

}
