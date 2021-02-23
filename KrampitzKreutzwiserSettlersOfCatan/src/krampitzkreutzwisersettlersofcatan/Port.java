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
    private Image typeImage;
    private int typePosX; //the x position of the type image
    private int typePosY;
    
    //image files for base port
    private final static Image TOP_PORT = new ImageIcon(ImageRef.class.getResource("peirGroups1.png")).getImage(); 
    private final static Image TOP_RIGHT_PORT = new ImageIcon(ImageRef.class.getResource("peirGroups2.png")).getImage(); 
    private final static Image BOTTOM_RIGHT_PORT = new ImageIcon(ImageRef.class.getResource("peirGroups3.png")).getImage(); 
    private final static Image BOTTOM_PORT = new ImageIcon(ImageRef.class.getResource("peirGroups4.png")).getImage(); 
    private final static Image BOTTOM_LEFT_PORT = new ImageIcon(ImageRef.class.getResource("peirGroups5.png")).getImage(); 
    private final static Image TOP_LEFT_PORT = new ImageIcon(ImageRef.class.getResource("peirGroups6.png")).getImage(); 
    //image files for type
    private final static Image CLAY_PORT = new ImageIcon(ImageRef.class.getResource("clayPort.png")).getImage(); 
    private final static Image WOOD_PORT = new ImageIcon(ImageRef.class.getResource("woodPort.png")).getImage(); 
    private final static Image WHEAT_PORT = new ImageIcon(ImageRef.class.getResource("wheatPort.png")).getImage(); 
    private final static Image SHEEP_PORT = new ImageIcon(ImageRef.class.getResource("sheepPort.png")).getImage(); 
    private final static Image ORE_PORT = new ImageIcon(ImageRef.class.getResource("orePort.png")).getImage(); 
    private final static Image WILD_CARD_PORT = new ImageIcon(ImageRef.class.getResource("wildcard.png")).getImage();
    
    /**
     * Constructor for a blank Port
     */
    public Port() {
        //init the pos
        xPos = 0;
        yPos = 0;
        
        typePosX = 0;
        typePosY = 0;
        
        //init the port attributes
        linkedTile = null;
        orientation = 0;
        type = 0;
        image = applyImage();
        typeImage = applyTypeImage();
        
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
        typeImage = applyTypeImage();
        applyCoordinates();
        applyTypeImageCoordinates();
    }
    
    /**
     * Using the orientation return where the type image should be drawn. Update the correct attributes.
     */
    private void applyTypeImageCoordinates() {
        //get the orientation
        switch (orientation) {
            case 0:
                //if above
                typePosX = linkedTile.getXPos() + (linkedTile.getImage().getWidth(null) / 2) - (typeImage.getWidth(null) / 2);
                typePosY = (int) (yPos + image.getHeight(null) - (30 / scaleFactor) - typeImage.getHeight(null));
                break;
            case 1:
                //if top right
                typePosX = (int) (xPos + (25 / scaleFactor) + typeImage.getWidth(null));
                typePosY = (int) (yPos + typeImage.getHeight(null) / 2.0);
                break;
            case 2:
                //if bottom right
                typePosX = (int) (xPos + (25 / scaleFactor) + typeImage.getWidth(null));
                typePosY = (yPos + image.getHeight(null) - typeImage.getHeight(null));
                break;
            case 3:
                //if below
                typePosX = linkedTile.getXPos() + (linkedTile.getImage().getWidth(null) / 2) - (typeImage.getWidth(null) / 2);
                typePosY = (int) (yPos + (30 / scaleFactor));
                break;
            case 4:
                //if bottom left
                typePosX = (int) (xPos + image.getWidth(null) - (50 / scaleFactor) - typeImage.getWidth(null));
                typePosY = (yPos + image.getHeight(null) - typeImage.getHeight(null));
                break;
            case 5:
                //if top left
                typePosX = (int) (xPos + image.getWidth(null) - (50 / scaleFactor) - typeImage.getWidth(null));
                typePosY = (int) (yPos + typeImage.getHeight(null) / 2.0);
                break;
            default:
                break;
        }
    }
    
    /**
     * using the linked Tile and orientation calculate the x and y positions of the Port
     */
    private void applyCoordinates() {
        //get the orientation
        switch (orientation) {
            case 0:
                //if above
                xPos = linkedTile.getXPos();
                yPos = linkedTile.getYPos() - linkedTile.getImage().getHeight(null);
                break;
            case 1:
                //if top right
                xPos = (int) (linkedTile.getXPos() + (linkedTile.getImage().getWidth(null) / 4.0) * 3);
                yPos = (int) (linkedTile.getYPos() - (20 / scaleFactor));
                break;
            case 2:
                //if bottom right
                xPos = (int) (linkedTile.getXPos() + (linkedTile.getImage().getWidth(null) / 4.0) * 3);
                yPos = (int) (linkedTile.getYPos() - (20 / scaleFactor));
                break;
            case 3:
                //if below
                xPos = linkedTile.getXPos();
                yPos = (int) (linkedTile.getYPos() + linkedTile.getImage().getHeight(null) - (20 / scaleFactor)); //set it below the tile but subtract the 3d space for the Alex tile art
                break;
            case 4:
                //if bottom left
                xPos = (int) (linkedTile.getXPos() - (linkedTile.getImage().getWidth(null) / 4.0) * 3);
                yPos = (int) (linkedTile.getYPos() - (20 / scaleFactor));
                break;
            case 5:
                //if top left
                xPos = (int) (linkedTile.getXPos() - (linkedTile.getImage().getWidth(null) / 4.0) * 3);
                yPos = (int) ((linkedTile.getYPos()) - (20 / scaleFactor));
                break;
            default:
                break;
        }
    }
    
    /**
     * Get the X position of the type image
     * @return The object's current X position
     */
    public final int getTypePosX() {
        return typePosX;
    }

    /**
     * Set the X position of the type image
     * @param typePosX
     */
    public final void setTypePosX(int typePosX) {
        this.typePosX = typePosX;
    }

    /**
     * Get the Y position of the type image
     * @return The type image current Y position
     */
    public final int getTypePosY() {
        return typePosY;
    }

    /**
     * Set the Y position of the type image
     * @param typePosY
     */
    public final void setTypePosY(int typePosY) {
        this.typePosY = typePosY;
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
    
    /**
     * Set the type Image of the Port 
     * @param typeImage
     */
    public void setTypeImage(Image typeImage) {
        this.typeImage = typeImage;
    }
    
    /**
     * Get the type Image of the Port
     * @return 
     */
    public Image getTypeImage() {
        return typeImage;
    }
    
    @Override
    public WorldObject clone() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Dynamically set the image based on the orientation 
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
    
    /**
     * Dynamically set the type image based on the type 
     * @return 
     */
    private Image applyTypeImage() {
        switch (type) {
            case 0:
                return WILD_CARD_PORT;
            case 1:
                return CLAY_PORT;
            case 2:
                return WOOD_PORT;
            case 3:
                return WHEAT_PORT;
            case 4:
                return SHEEP_PORT;
            case 5:
                return ORE_PORT;
            default:
                return WILD_CARD_PORT;
        }
    }
    
}
