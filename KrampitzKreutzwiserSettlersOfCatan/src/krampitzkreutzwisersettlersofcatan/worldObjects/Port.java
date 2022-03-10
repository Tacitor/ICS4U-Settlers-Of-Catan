/*
 * Lukas Krampitz
 * Feb 22, 2021
 * A class representing the trading ports on the outside of the island
 */
package krampitzkreutzwisersettlersofcatan.worldObjects;

import animation.PortAnimationData;
import java.awt.Image;
import javax.swing.ImageIcon;
import static krampitzkreutzwisersettlersofcatan.gui.GamePanel.*;
import textures.ImageRef;

/**
 *
 * @author Tacitor
 */
public class Port extends WorldObject {

    //World Port attributes
    private Tile linkedTile; //the the tile the port find itself on
    private int orientation; //the rotation of the port. 0 for top, up to 5 going clockwise around the tile.
    private int type; //the type of the port. 0 for 3:1, 1-5 for a 2:1 where the specific 2:1 is the card type of this int.
    private Image image; //the main image of the port (the ship and the piers)
    private Image typeImage; //the resource image. Get drawn ontop of the main image
    private int typePosX; //the x position of the type image
    private int typePosY; //the y position of the type image
    private int shipPosX; //the x position of the ship image (Will move around as part of animation)
    private int shipPosY; //The y position of the ship image
    private Image shipImage; //the image var for the ship
    private PortAnimationData portAnimationData; //the object that contains the variables for animation

    //image files for base port
    private final static Image TOP_PORT = new ImageIcon(ImageRef.class.getResource("port/peirGroups1.png")).getImage();
    private final static Image TOP_RIGHT_PORT = new ImageIcon(ImageRef.class.getResource("port/peirGroups2.png")).getImage();
    private final static Image BOTTOM_RIGHT_PORT = new ImageIcon(ImageRef.class.getResource("port/peirGroups3.png")).getImage();
    private final static Image BOTTOM_PORT = new ImageIcon(ImageRef.class.getResource("port/peirGroups4.png")).getImage();
    private final static Image BOTTOM_LEFT_PORT = new ImageIcon(ImageRef.class.getResource("port/peirGroups5.png")).getImage();
    private final static Image TOP_LEFT_PORT = new ImageIcon(ImageRef.class.getResource("port/peirGroups6.png")).getImage();
    //image files for type
    private final static Image CLAY_PORT = new ImageIcon(ImageRef.class.getResource("port/clayPort.png")).getImage();
    private final static Image WOOD_PORT = new ImageIcon(ImageRef.class.getResource("port/woodPort.png")).getImage();
    private final static Image WHEAT_PORT = new ImageIcon(ImageRef.class.getResource("port/wheatPort.png")).getImage();
    private final static Image SHEEP_PORT = new ImageIcon(ImageRef.class.getResource("port/sheepPort.png")).getImage();
    private final static Image ORE_PORT = new ImageIcon(ImageRef.class.getResource("port/orePort.png")).getImage();
    private final static Image WILD_CARD_PORT = new ImageIcon(ImageRef.class.getResource("port/wildcard.png")).getImage();
    //ship file
    private final static Image SHIP = new ImageIcon(ImageRef.class.getResource("port/ship.png")).getImage();

    /**
     * Constructor for a blank Port
     */
    public Port() {
        //init the pos
        xPos = 0;
        yPos = 0;

        typePosX = 0;
        typePosY = 0;
        shipPosX = 0;
        shipPosY = 0;

        //init the port attributes
        linkedTile = null;
        orientation = 0;
        type = 0;
        image = applyImage();
        typeImage = applyTypeImage();

        //set the ship to the ship image
        shipImage = SHIP;

        //set the animation data object
        portAnimationData = new PortAnimationData();
    }

    /**
     * Constructor for a full port
     *
     * @param linkedTile
     * @param orientation
     * @param type
     */
    public Port(Tile linkedTile, int orientation, int type) {
        this();

        //apply the specifics for this tile
        this.linkedTile = linkedTile;
        this.orientation = orientation;
        this.type = type;

        //update the images and coordinates based upon this new data
        image = applyImage();
        typeImage = applyTypeImage();
        applyCoordinates();
        applyTypeImageCoordinates();
        applyShipStarterCoordinates();
    }

    /**
     * Using the orientation return where the type & ship images should be
     * drawn. Update the correct attributes.
     */
    public void applyTypeImageCoordinates() {
        //get the orientation
        switch (orientation) {
            case 0:
                //if above
                typePosX = (int) (linkedTile.getXPos() + (getImgWidth(linkedTile.getImage()) / 2.0) - (getImgWidth(typeImage) / 2.0));
                typePosY = (int) (yPos + getImgHeight(image) - (getImgHeight(image) / 5.0) - getImgHeight(typeImage));
                break;
            case 1:
                //if top right
                typePosX = (int) (xPos + (25 / scaleFactor) + getImgWidth(typeImage));
                typePosY = (int) (yPos + getImgHeight(typeImage) / 2.0);
                break;
            case 2:
                //if bottom right
                typePosX = (int) (xPos + (25 / scaleFactor) + getImgWidth(typeImage));
                typePosY = (yPos + getImgHeight(image) - getImgHeight(typeImage));
                break;
            case 3:
                //if below
                typePosX = linkedTile.getXPos() + (getImgWidth(linkedTile.getImage()) / 2) - (getImgWidth(typeImage) / 2);
                typePosY = (int) (yPos + (30 / scaleFactor));
                break;
            case 4:
                //if bottom left
                typePosX = (int) (xPos + getImgWidth(image) - (50 / scaleFactor) - getImgWidth(typeImage));
                typePosY = (yPos + getImgHeight(image) - getImgHeight(typeImage));
                break;
            case 5:
                //if top left
                typePosX = (int) (xPos + getImgWidth(image) - (50 / scaleFactor) - getImgWidth(typeImage));
                typePosY = (int) (yPos + getImgHeight(typeImage) / 2.0);
                break;
            default:
                break;
        }
    }

    public void applyShipStarterCoordinates() {
        //get the orientation
        switch (orientation) {
            case 0:
                //if above
                shipPosX = xPos + getImgWidth(image) - (2 * getImgWidth(shipImage)) - scaleInt(10); //set the position to the bottom right corner of the pier image. Then move it over by twice the size of the ship image
                shipPosY = yPos + getImgHeight(image) - (2 * getImgHeight(shipImage));
                break;
            case 1:
                //if top right
                shipPosX = xPos + getImgWidth(shipImage); //move it diagonal by the size of the ship image
                shipPosY = yPos + getImgHeight(shipImage) - scaleInt(10);
                break;
            case 2:
                //if bottom right
                shipPosX = xPos + (getImgWidth(image) / 2) - scaleInt(20);
                shipPosY = yPos + (getImgWidth(image) / 2);
                break;
            case 3:
                //if below
                shipPosX = xPos + scaleInt(35); //leave the ship in the top left corner and move it down 35px and right 15px
                shipPosY = yPos + scaleInt(15);
                break;
            case 4:
                //if bottom left
                shipPosX = xPos + (getImgWidth(image) / 2) + scaleInt(7); //centre the ship in the middle of the port image. Then move it 7px down and right.
                shipPosY = yPos + (getImgHeight(image) / 2) + scaleInt(7);
                break;
            case 5:
                //if top left
                shipPosX = xPos + (getImgWidth(image) / 2) + scaleInt(6); //move it over by half and add 15 px
                shipPosY = yPos; //leave the y pos where it is
                break;
            default:
                break;
        }
    }

    /**
     * using the linked Tile and orientation calculate the x and y positions of
     * the Port
     */
    public void applyCoordinates() {
        //get the orientation
        switch (orientation) {
            case 0:
                //if above
                xPos = linkedTile.getXPos();
                yPos = (int) (linkedTile.getYPos() - getImgWidth(linkedTile.getImage()) + (5 / scaleFactor));
                break;
            case 1:
                //if top right
                xPos = (int) (linkedTile.getXPos() + (getImgWidth(linkedTile.getImage()) / 4.0) * 3);
                yPos = (int) (linkedTile.getYPos() - (15 / scaleFactor));
                break;
            case 2:
                //if bottom right
                xPos = (int) (linkedTile.getXPos() + (getImgWidth(linkedTile.getImage()) / 4.0) * 3);
                yPos = (int) (linkedTile.getYPos() - (20 / scaleFactor));
                break;
            case 3:
                //if below
                xPos = linkedTile.getXPos();
                yPos = (int) (linkedTile.getYPos() + getImgHeight(linkedTile.getImage()) - (25 / scaleFactor)); //set it below the tile but subtract the 3d space for the Alex tile art
                break;
            case 4:
                //if bottom left
                xPos = (int) (linkedTile.getXPos() - (getImgWidth(linkedTile.getImage()) / 4.0) * 3);
                yPos = (int) (linkedTile.getYPos() - (20 / scaleFactor));
                break;
            case 5:
                //if top left
                xPos = (int) (linkedTile.getXPos() - (getImgWidth(linkedTile.getImage()) / 4.0) * 3);
                yPos = (int) ((linkedTile.getYPos()) - (15 / scaleFactor));
                break;
            default:
                break;
        }
    }

    /**
     * Calculates the new scaled width of an image with a locked aspect ratio.
     *
     * @param image
     * @return
     */
    public final int getImgWidth(Image image) {

        if (getPanelWidth() > getPanelHeight()) {
            return (int) (getImgHeight(image) * ((float) image.getWidth(null) / image.getHeight(null)));
        } else {
            return (int) (image.getWidth(null) / 1920.0 * getPanelWidth());
        }

    }

    /**
     * Calculates the new scaled height of an image with a locked aspect ratio.
     *
     * @param image
     * @return
     */
    public final int getImgHeight(Image image) {

        if (getPanelWidth() > getPanelHeight()) {
            return (int) (image.getHeight(null) / 1080.0 * getPanelHeight());
        } else {
            return (int) (getImgWidth(image) / ((float) image.getWidth(null) / image.getHeight(null)));
        }
    }

    /**
     * Get the X position of the type image
     *
     * @return The object's current X position
     */
    public final int getTypePosX() {
        return typePosX;
    }

    /**
     * Set the X position of the type image
     *
     * @param typePosX
     */
    public final void setTypePosX(int typePosX) {
        this.typePosX = typePosX;
    }

    /**
     * Get the Y position of the type image
     *
     * @return The type image current Y position
     */
    public final int getTypePosY() {
        return typePosY;
    }

    /**
     * Set the Y position of the type image
     *
     * @param typePosY
     */
    public final void setTypePosY(int typePosY) {
        this.typePosY = typePosY;
    }

    /**
     * Set the Tile the Port is linked to
     *
     * @param linkedTile
     */
    public void setLinkedTile(Tile linkedTile) {
        this.linkedTile = linkedTile;
    }

    /**
     * Get the Tile the Port is linked to
     *
     * @return
     */
    public Tile getLinkedTile() {
        return linkedTile;
    }

    /**
     * Set the orientation of the Port
     *
     * @param orientation
     */
    public void setOrientation(int orientation) {
        this.orientation = orientation;
        image = applyImage();
    }

    /**
     * Get the orientation of the Port
     *
     * @return
     */
    public int getOrientation() {
        return orientation;
    }

    /**
     * Set the type of the Port
     *
     * @param type
     */
    public void setType(int type) {
        this.type = type;
        typeImage = applyTypeImage();
    }

    /**
     * Get the type of the Port
     *
     * @return
     */
    public int getType() {
        return type;
    }

    /**
     * Set the base Image of the Port
     *
     * @param image
     */
    public void setImage(Image image) {
        this.image = image;
    }

    /**
     * Get the base Image of the Port with correct orientation
     *
     * @return
     */
    public Image getImage() {
        return image;
    }

    /**
     * Set the type Image of the Port
     *
     * @param typeImage
     */
    public void setTypeImage(Image typeImage) {
        this.typeImage = typeImage;
    }

    /**
     * Get the type Image of the Port
     *
     * @return
     */
    public Image getTypeImage() {
        return typeImage;
    }

    /**
     * Get the X position of the top left corner of the ship for this port
     *
     * @return
     */
    public int getShipPosX() {
        return shipPosX;
    }

    /**
     * Set the X position of the top left corner of the ship for this port
     *
     * @param shipPosX
     */
    public void setShipPosX(int shipPosX) {
        this.shipPosX = shipPosX;
    }

    /**
     * Get the Y position of the top left corner of the ship for this port
     *
     * @return
     */
    public int getShipPosY() {
        return shipPosY;
    }

    /**
     * Set the Y position of the top left corner of the ship for this port
     *
     * @param shipPosY
     */
    public void setShipPosY(int shipPosY) {
        this.shipPosY = shipPosY;
    }

    /**
     * Set the Image of the Ship
     *
     * @param shipImage
     */
    public void setShipImage(Image shipImage) {
        this.shipImage = shipImage;
    }

    /**
     * Get the Image of the Ship
     *
     * @return
     */
    public Image getShipImage() {
        return shipImage;
    }

    /**
     * Dynamically set the image based on the orientation
     *
     * @return
     */
    public Image applyImage() {
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
     *
     * @return
     */
    public Image applyTypeImage() {
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

    /**
     *
     * @return
     */
    public int[] getShipPos() {
        int posTime; //the time in milis that this positon will be held for
        int[] positions = new int[2]; //and array to store the x and y coordinates of the ship

        posTime = portAnimationData.getPosTimeShip();

        long time = System.currentTimeMillis();

        //decide if a new position needs to be displayed of if the current positon is still the one that it should be
        if ((time - portAnimationData.getLastPosStart()) > posTime) {
            //yes it is time for the new positions
            
            System.out.println("Time: " + (time - portAnimationData.getLastPosStart()));

            //update the time
            portAnimationData.setLastPosStart(time);

            //increment the counter
            portAnimationData.setIncrementCyclesPassed(portAnimationData.getIncrementCyclesPassed() + 1);
            
            //update the offset to the new position
            portAnimationData.setShipAnimationX(portAnimationData.getShipAnimationX() - portAnimationData.getMovePosIncrement());

        }

        //increment the x pos of the ship (just the x for now while developing the feature)
        shipPosX = (int)(shipPosX + portAnimationData.getShipAnimationX());

        //save the new positions
        positions[0] = shipPosX; //save the x
        positions[1] = shipPosY; //save the y

        return positions;
    }

    /**
     * Create a string representations of the port
     *
     * @return The ports's attributes as a string
     */
    @Override
    public String toString() {
        // Create a String out of the tile's attributes
        return "Port:\n"
                + super.toString() + "\n"
                + "Type: " + type + "\n"
                + "linkedTile: " + linkedTile.getRefNum() + "\n"
                + "Orientation: " + orientation + "\n";
    }

    /**
     * Create an identical copy of the port
     *
     * @return the new port instance
     */
    @Override
    public Port clone() {
        // Create a new instance with the same properties
        Port copy = new Port(linkedTile, orientation, type);
        //apply the dynamic attibutes
        copy.applyImage();
        copy.applyTypeImage();
        copy.applyImage();
        copy.applyTypeImage();
        // Return the copy
        return copy;
    }

    /**
     * Compare this tile to another to check if they are the same
     *
     * @param other The tile to compare to
     * @return If the tiles are equal or not
     */
    public boolean equals(Port other) {
        // Compare all of the properties of the tiles
        return super.equals(other)
                && type == other.type
                && orientation == other.orientation
                && linkedTile.equals(other.linkedTile);
    }

}
