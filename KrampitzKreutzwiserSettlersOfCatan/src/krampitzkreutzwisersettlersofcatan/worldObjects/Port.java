/*
 * Lukas Krampitz
 * Feb 22, 2021
 * A class representing the trading ports on the outside of the island
 */
package krampitzkreutzwisersettlersofcatan.worldObjects;

import animation.PortAnimationData;
import java.awt.Image;
import javax.swing.ImageIcon;
import krampitzkreutzwisersettlersofcatan.gui.GamePanel;
import static krampitzkreutzwisersettlersofcatan.gui.GamePanel.*;
import textures.ImageRef;
import static textures.ImageRef.WATER_RING_BOARDER;

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
    private int shipPosX; //the x position of the ship image before animation was added. These are the coords of the docked ship.
    private int shipPosY; //The y position of the ship image
    private Image shipImage; //the image var for the ship
    private PortAnimationData portAnimationData; //the object that contains the variables for animation
    private GamePanel gamePanel;

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

    //final vars for the margins of the sea. And the farthest the ships should sail.
    private int leftHandShipMargin;
    private int rightHandShipMargin;
    private int topShipMargin;
    private int bottomShipMargin;

    /**
     * Constructor for a full port
     *
     * @param linkedTile
     * @param orientation
     * @param type
     * @param gamePanel
     */
    public Port(Tile linkedTile, int orientation, int type, GamePanel gamePanel) {
        //init the pos
        xPos = 0;
        yPos = 0;

        typePosX = 0;
        typePosY = 0;
        shipPosX = 0;
        shipPosY = 0;

        //apply the specifics for this tile
        this.linkedTile = linkedTile;
        this.orientation = orientation;
        this.type = type;
        this.gamePanel = gamePanel;

        //set the animation data object
        portAnimationData = new PortAnimationData();

        //update the images and coordinates based upon this new data
        image = applyImage();
        typeImage = applyTypeImage();
        shipImage = applyShipImage();
        applyCoordinates();
        applyTypeImageCoordinates();
        applyShipStarterCoordinates();

        generatePortMargins();

    }

    /**
     * Using the orientation return where the type image should be drawn. Update
     * the correct attributes.
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

    /**
     * Using the orientation return where the ship image should be drawn. Update
     * the correct attributes.
     */
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
     * Set the PortAnimationData of the Port
     *
     * @param portAnimationData
     */
    public void setPortAnimationData(PortAnimationData portAnimationData) {
        this.portAnimationData = portAnimationData;
    }

    /**
     * Get the PortAnimationData of the Port
     *
     * @return
     */
    public PortAnimationData getPortAnimationData() {
        return portAnimationData;
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
     * Set the image of the ship depending on which direction it is sailing
     *
     * @return
     */
    public Image applyShipImage() {
        switch (orientation) {
            case 0:
            case 3:
                return SHIP;
            case 1:
            case 2:
                return SHIP;
            case 4:
            case 5:
                return SHIP;
            default:
                return ImageRef.ERROR_IMAGE;
        }
    }

    /**
     * Get the X & Y coordinates of where the ship should be drawn. This is for
     * each port for animating it.
     *
     * @param gamePanel
     * @return
     */
    public int[] getShipPos(GamePanel gamePanel) {
        int posTime; //the time in milis that this positon will be held for
        int[] positions = new int[2]; //and array to store the x and y coordinates of the ship

        //update those port margins
        generatePortMargins();

        posTime = portAnimationData.getPosTimeShip();

        long time = System.currentTimeMillis();

        //decide if a new position needs to be displayed of if the current positon is still the one that it should be
        if ((time - portAnimationData.getLastPosStart()) > posTime) {
            //yes it is time for the new positions

            //debug the timing of the ship animation
            //System.out.println("Time: " + (time - portAnimationData.getLastPosStart()));
            //update the time
            portAnimationData.setLastPosStart(time);

            //increment the counter
            portAnimationData.setIncrementCyclesPassed(portAnimationData.getIncrementCyclesPassed() + 1);

            //check what orientation the port is to see how it moves
            if (orientation == 4 || orientation == 5) { //top left and bottom left

                //decide what way the ship is facing
                if (portAnimationData.isOutToSea()) { //is it facing to the left right now?

                    //see if it should continue to 
                    if ((int) (shipPosX + portAnimationData.getShipAnimationX()) > leftHandShipMargin) { //yes it should
                        //update the offset to the new position
                        portAnimationData.setShipAnimationX(portAnimationData.getShipAnimationX() - portAnimationData.getMovePosIncrement());
                    } else { //no it should not
                        portAnimationData.setOutToSea(false);
                    }

                } else { //is facing to the right right now

                    //see if it should continue to?
                    if ((int) (shipPosX + portAnimationData.getShipAnimationX()) < shipPosX) {
                        //update the offset to the new position
                        portAnimationData.setShipAnimationX(portAnimationData.getShipAnimationX() + portAnimationData.getMovePosIncrement());
                    } else {
                        portAnimationData.setOutToSea(true);
                    }

                }

            }

            //check what orientation the port is to see how it moves
            if (orientation == 1 || orientation == 2) { //top right and bottom right

                //decide what way the ship is facing
                if (portAnimationData.isOutToSea()) { //is it facing to the left right now?
                    //facing right

                    //see if it should continue to 
                    if ((int) (shipPosX + portAnimationData.getShipAnimationX()) < rightHandShipMargin) { //yes it should
                        //update the offset to the new position
                        portAnimationData.setShipAnimationX(portAnimationData.getShipAnimationX() + portAnimationData.getMovePosIncrement());
                    } else { //no it should not
                        portAnimationData.setOutToSea(false);
                    }

                } else { //is facing to the left right now

                    //see if it should continue to?
                    if ((int) (shipPosX + portAnimationData.getShipAnimationX()) > shipPosX) {
                        //update the offset to the new position
                        portAnimationData.setShipAnimationX(portAnimationData.getShipAnimationX() - portAnimationData.getMovePosIncrement());
                    } else {
                        portAnimationData.setOutToSea(true);
                    }

                }

            }

            //check what orientation the port is to see how it moves
            if (orientation == 0) { //port is on top

                //decide what way the ship is facing
                if (portAnimationData.isOutToSea()) { //facing to the left and whill move diagally up and left

                    //see if it should continue to by seeing if it is too high
                    if ((int) (shipPosY + portAnimationData.getShipAnimationY()) > topShipMargin) { //yes it should
                        //update the offset to the new position
                        portAnimationData.setShipAnimationY(portAnimationData.getShipAnimationY() - portAnimationData.getMovePosIncrement());
                        //update the x offset to the new position to get some horizontal movement in there too
                        portAnimationData.setShipAnimationX(portAnimationData.getShipAnimationX() - portAnimationData.getMovePosIncrement());
                    } else { //no it should not
                        portAnimationData.setOutToSea(false);
                    }

                } else { //is facing to the right right now

                    //see if it should continue to?
                    if ((int) (shipPosY + portAnimationData.getShipAnimationY()) < shipPosY) {
                        //update the offset to the new position
                        portAnimationData.setShipAnimationY(portAnimationData.getShipAnimationY() + portAnimationData.getMovePosIncrement());
                        //update the x offset to the new position to get some horizontal movement in there too
                        portAnimationData.setShipAnimationX(portAnimationData.getShipAnimationX() + portAnimationData.getMovePosIncrement());
                    } else {
                        portAnimationData.setOutToSea(true);
                    }

                }

            }

            //check what orientation the port is to see how it moves
            if (orientation == 3) { //port is on bottom

                //decide what way the ship is facing
                if (portAnimationData.isOutToSea()) { //facing to the left and whill move diagally up and left

                    //see if it should continue to by seeing if it is too high
                    if ((int) (shipPosY + portAnimationData.getShipAnimationY()) < bottomShipMargin) { //yes it should
                        //update the offset to the new position
                        portAnimationData.setShipAnimationY(portAnimationData.getShipAnimationY() + portAnimationData.getMovePosIncrement());
                        //update the x offset to the new position to get some horizontal movement in there too
                        portAnimationData.setShipAnimationX(portAnimationData.getShipAnimationX() + portAnimationData.getMovePosIncrement());
                    } else { //no it should not
                        portAnimationData.setOutToSea(false);
                    }

                } else { //is facing to the right right now

                    //see if it should continue to?
                    if ((int) (shipPosY + portAnimationData.getShipAnimationY()) > shipPosY) {
                        //update the offset to the new position
                        portAnimationData.setShipAnimationY(portAnimationData.getShipAnimationY() - portAnimationData.getMovePosIncrement());
                        //update the x offset to the new position to get some horizontal movement in there too
                        portAnimationData.setShipAnimationX(portAnimationData.getShipAnimationX() - portAnimationData.getMovePosIncrement());
                    } else {
                        portAnimationData.setOutToSea(true);
                    }

                }

            }
        }

        //increment the x pos of the ship
        shipPosX = (int) (shipPosX + portAnimationData.getShipAnimationX());
        //increment the y pos of the ship
        shipPosY = (int) (shipPosY + portAnimationData.getShipAnimationY());

        //save the new positions
        positions[0] = shipPosX; //save the x
        positions[1] = shipPosY; //save the y

        //update the ship image
        shipImage = applyShipImage();

        return positions;
    }

    /**
     * If the image of the ship needs to be flipped, calculate what offset it
     * needs to render correctly then.
     *
     * @return
     */
    public int getFlipOffset() {
        //get the orientation of the ship
        int outToSea = getOutToSeaMultip(); //whether or not the ship is facing the sea or not

        int flipOffset; //how much to offset the x pos by to account for flipping
        if (outToSea == 1) {
            flipOffset = 0;
        } else {
            flipOffset = getImgWidth(shipImage);
        }

        return flipOffset;
    }

    /**
     * Decide if to flip the image of the ship from the regular left facing way
     *
     * @return
     */
    public int getOutToSeaMultip() {
        //decide if to flip the image of the ship from the regular left facing way
        //var for storing the result
        int result = 1; //set this to 1 for no flip, and -1 for an axial flip. Default is no flip (face to left)

        //is it sailing out to sea?
        if (portAnimationData.isOutToSea()) {
            /**
             * Next get the orientation. The flipping is based off what way the
             * ship is sailing and what way the port itself is facing
             */
            if (orientation == 4 || orientation == 5 || orientation == 0) {
                result = 1; //no flip
            } else if (orientation == 1 || orientation == 2 || orientation == 3) {
                result = -1; //do flip the image
            }
        } else if (!portAnimationData.isOutToSea()) {
            //exact opposite of when it is outToSea
            if (orientation == 4 || orientation == 5 || orientation == 0) {
                result = -1; //flip
            } else if (orientation == 1 || orientation == 2 || orientation == 3) {
                result = 1; //do not flip the image
            }
        }

        return result;
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
        Port copy = new Port(linkedTile, orientation, type, gamePanel);
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

    /**
     * Assign new X & Y Co-Ords to the ships. These are based off of the
     * position when docked in port, and where the ship turns around when at
     * sea. Pick a random spot in between.
     */
    public void randomizeShipAnimation() {
        //see if it has been randomized yet
        if (!portAnimationData.getHasBeenRandomized()) {
            //make sure there are margins
            generatePortMargins();

            //save that it has been randomized
            portAnimationData.setHasBeenRandomized(true);

            int legLength; //How long is a leg of a right angle triangle when useing pythagorean theorm
            int randDiagOffset; //the number of pixels both the x & y of the ship should be offset by then the ship has diagonal movement
            //pick a margin for each port
            //pick an orientation
            //and randomly pick an x or y coord to put that ship on
            switch (orientation) {
                case 4:
                case 5:
                    //If left
                    /**
                     * Randomize X position. Do this by getting the negative
                     * number of pixels from the left hand margin to the starter
                     * position of the ship. Then multiply that by a random
                     * decimal number from 0-1. All this gives a random negative
                     * offset for the ship. This random negative offset will
                     * place the ship within the left margin (on the left) and
                     * the ships starter position (on the right).
                     */
                    portAnimationData.setShipAnimationX((int) (Math.random() * (leftHandShipMargin - shipPosX)));
                    break;
                case 1:
                case 2:
                    //If right 
                    //randomize position
                    portAnimationData.setShipAnimationX((int) (Math.random() * (rightHandShipMargin - shipPosX)));
                    break;
                case 0:
                    //If top 
                    //calc the length of the legs of the right angle triangle.
                    //This is the length from port to top margin (straight line)
                    legLength = shipPosY - topShipMargin;

                    //randomize position
                    randDiagOffset = (int) (Math.random() * (-legLength));
                    portAnimationData.setShipAnimationY(randDiagOffset);
                    portAnimationData.setShipAnimationX(randDiagOffset);
                    break;
                case 3:
                    //If bottom
                    //calc the length of the legs of the right angle triangle.
                    //This is the length from port to bottom margin (straight line)
                    legLength = bottomShipMargin - shipPosY;

                    //randomize position
                    randDiagOffset = (int) (Math.random() * (legLength));
                    portAnimationData.setShipAnimationY(randDiagOffset);
                    portAnimationData.setShipAnimationX(randDiagOffset);
                    break;
                default:
                    break;
            }

            //randomize direction. 50/50 of facing left or right
            portAnimationData.setOutToSea(Math.random() < 0.5);

        }

    }

    /**
     * Calculate the positions of the margins where the ships turn around at sea.
     */
    private void generatePortMargins() {
        leftHandShipMargin = ((gamePanel.getWidth() / 2 - getImgWidth(WATER_RING_BOARDER) / 2) - (getImgWidth(shipImage)));
        rightHandShipMargin = ((gamePanel.getWidth() / 2 + getImgWidth(WATER_RING_BOARDER) / 2));
        topShipMargin = ((gamePanel.getHeight() / 2 - getImgHeight(WATER_RING_BOARDER) / 2) - (getImgHeight(shipImage)));
        bottomShipMargin = ((gamePanel.getHeight() / 2 + getImgHeight(WATER_RING_BOARDER) / 2));
    }

}
