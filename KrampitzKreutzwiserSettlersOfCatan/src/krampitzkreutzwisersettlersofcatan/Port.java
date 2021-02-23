/*
 * Lukas Krampitz
 * Feb 22, 2021
 * 
 */
package krampitzkreutzwisersettlersofcatan;

import java.awt.Image;
import javax.swing.ImageIcon;
import static krampitzkreutzwisersettlersofcatan.GamePanel.scaleFactor;
import textures.ImageRef;

/**
 *
 * @author Tacitor
 */
public class Port extends WorldObject{
    
    //World Port attributes
    private Tile linkedTile; //the the tile the port find itself on
    private int orientation; //the rotation of the port. 0 for top, up to 5 going clockwise around the tile.
    private int type; //the type of the port. 0 for 3:1, 1-5 for a 2:1 where the specific 2:1 is the card type of this int.
    private Image image;
    
    //image files
    private final static Image TOP_PORT = new ImageIcon(ImageRef.class.getResource("peirGroups1.png")).getImage(); 
    private final static Image TOP_RIGHT_PORT = new ImageIcon(ImageRef.class.getResource("peirGroups2.png")).getImage(); 
    private final static Image BOTTOM_RIGHT_PORT = new ImageIcon(ImageRef.class.getResource("peirGroups3.png")).getImage(); 
    private final static Image BOTTOM_PORT = new ImageIcon(ImageRef.class.getResource("peirGroups4.png")).getImage(); 
    private final static Image BOTTOM_LEFT_PORT = new ImageIcon(ImageRef.class.getResource("peirGroups5.png")).getImage(); 
    private final static Image TOP_LEFT_PORT = new ImageIcon(ImageRef.class.getResource("peirGroups6.png")).getImage(); 
    
    /**
     * Constructor for a blank Port
     */
    public Port() {
        //init the pos
        xPos = 0;
        yPos = 0;
        
        //init the port attributes
        linkedTile = null;
        orientation = 0;
        type = 0;
        image = applyImage();
        
    }
    
    /**
     * 
     * 
     * @param linkedTile
     * @param orientation
     * @param type 
     */
    public Port(Tile linkedTile, int orientation, int type) {
        this();
        
        this.linkedTile = linkedTile;
        this.orientation = orientation;
        this.type = type;
        
        image = applyImage();
        applyCoordinates();
    }
    
    /**
     * using the linked Tile and orientation calculate the x and y positions of the Port
     */
    private void applyCoordinates() {
        //get the orientation
        if (orientation == 0) { //if above
            xPos = linkedTile.getXPos();
            yPos = linkedTile.getYPos() - linkedTile.getImage().getHeight(null);
        } else if (orientation == 1) { //if top right
            xPos = (int) (linkedTile.getXPos() + (linkedTile.getImage().getWidth(null) / 4.0) * 3);
            yPos = (int) (linkedTile.getYPos() - (20 / scaleFactor));
        } else if (orientation == 2) { //if bottom right
            xPos = (int) (linkedTile.getXPos() + (linkedTile.getImage().getWidth(null) / 4.0) * 3);
            yPos = (int) (linkedTile.getYPos() - (20 / scaleFactor));
        } else if (orientation == 3) { //if below
            xPos = linkedTile.getXPos();
            yPos = (int) (linkedTile.getYPos() + linkedTile.getImage().getHeight(null) - (20 / scaleFactor)); //set it below the tile but subtract the 3d space for the Alex tile art
        } else if (orientation == 4) { //if bottom left
            xPos = (int) (linkedTile.getXPos() - (linkedTile.getImage().getWidth(null) / 4.0) * 3);
            yPos = (int) (linkedTile.getYPos() - (20 / scaleFactor));
        } else if (orientation == 5) { //if top left
            xPos = (int) (linkedTile.getXPos() - (linkedTile.getImage().getWidth(null) / 4.0) * 3);
            yPos = (int) ((linkedTile.getYPos()) - (20 / scaleFactor));
        }
    }
    
    /**
     * Set the Tile the Port is linked to
     * @param linkedTile 
     */
    public void setLinkedTile (Tile linkedTile) {
        this.linkedTile = linkedTile;
    }
    
    /**
     * Get the Tile the Port is linked to
     * @return 
     */
    public Tile getLinkedTile() {
        return linkedTile;
    }
    
    /**
     * Set the orientation of the Port
     * @param orientation 
     */
    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }
    
    /**
     * Get the orientation of the Port
     * @return 
     */
    public int getOrientation() {
        return orientation;
    }
    
    /**
     * Set the base Image of the Port
     * @param image 
     */
    public void setImage(Image image) {
        this.image = image;
    }
    
    /**
     * Get the base Image of the Port with correct orientation
     * @return 
     */
    public Image getImage() {
        return image;
    }
    
    @Override
    public WorldObject clone() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Dynamically set the image based on the type 
     * @return 
     */
    private Image applyImage() {
        switch (orientation) {
            case 0:
                return TOP_PORT;
            case 1:
                return TOP_RIGHT_PORT;
            case 2:
                return BOTTOM_RIGHT_PORT;
            case 3:
                return BOTTOM_PORT;
            case 4:
                return BOTTOM_LEFT_PORT;
            case 5:
                return TOP_LEFT_PORT;
            default:
                return TOP_PORT;
        }
    }
    
}
