/*
 * Lukas Krampitz
 * Nov 8, 2020
 * This is a blank class simply used to locate the textures package for when it is complied to a jar. Yes there is a better way, no I did not have time to find it.
 */
package textures;

import java.awt.Image;
import javax.swing.ImageIcon;

/**
 *
 * @author Tacitor
 */
public class ImageRef {
    
    //images for the cards
    public final static Image CARD_CLAY = new ImageIcon(ImageRef.class.getResource("cardClay.png")).getImage();
    public final static Image CARD_WHEAT = new ImageIcon(ImageRef.class.getResource("cardWheat.png")).getImage();
    public final static Image CARD_ORE = new ImageIcon(ImageRef.class.getResource("cardOre.png")).getImage();
    public final static Image CARD_SHEEP = new ImageIcon(ImageRef.class.getResource("cardSheep.png")).getImage();
    public final static Image CARD_WOOD = new ImageIcon(ImageRef.class.getResource("cardWood.png")).getImage();

    //images for the roads
    public final static Image RED_ROAD_H = new ImageIcon(ImageRef.class.getResource("redRoadH.png")).getImage(); //horizontal road
    public final static Image BLUE_ROAD_H = new ImageIcon(ImageRef.class.getResource("blueRoadH.png")).getImage();
    public final static Image RED_ROAD_R = new ImageIcon(ImageRef.class.getResource("redRoadR.png")).getImage(); //diagonal to the right (refernce point is the top of the road)
    public final static Image BLUE_ROAD_R = new ImageIcon(ImageRef.class.getResource("blueRoadR.png")).getImage();
    public final static Image RED_ROAD_L = new ImageIcon(ImageRef.class.getResource("redRoadL.png")).getImage(); //diagonal to the left
    public final static Image BLUE_ROAD_L = new ImageIcon(ImageRef.class.getResource("blueRoadL.png")).getImage();
    public final static Image BLANK_ROAD_H = new ImageIcon(ImageRef.class.getResource("blankRoadH.png")).getImage(); // Blank images for horizontal
    public final static Image BLANK_ROAD_V = new ImageIcon(ImageRef.class.getResource("blankRoadV.png")).getImage(); // and non horizontal roads
    
    public final static Image WHITE_ROAD_H = new ImageIcon(ImageRef.class.getResource("whiteRoadH.png")).getImage(); //white images 
    public final static Image WHITE_ROAD_R = new ImageIcon(ImageRef.class.getResource("whiteRoadR.png")).getImage();
    public final static Image WHITE_ROAD_L = new ImageIcon(ImageRef.class.getResource("whiteRoadL.png")).getImage();
    
    public final static Image ORANGE_ROAD_H = new ImageIcon(ImageRef.class.getResource("orangeRoadH.png")).getImage(); //orange images for horizontal
    public final static Image ORANGE_ROAD_R = new ImageIcon(ImageRef.class.getResource("orangeRoadR.png")).getImage(); 
    public final static Image ORANGE_ROAD_L = new ImageIcon(ImageRef.class.getResource("orangeRoadL.png")).getImage(); 

    //images for the settlements
    public final static Image BLUE_HOUSE_L = new ImageIcon(ImageRef.class.getResource("blueHouseL.png")).getImage();
    public final static Image RED_HOUSE_L = new ImageIcon(ImageRef.class.getResource("redHouseL.png")).getImage();
    public final static Image BLUE_HOUSE_S = new ImageIcon(ImageRef.class.getResource("blueHouseS.png")).getImage();
    public final static Image RED_HOUSE_S = new ImageIcon(ImageRef.class.getResource("redHouseS.png")).getImage();
    public final static Image BLANK_HOUSE = new ImageIcon(ImageRef.class.getResource("blankHouse.png")).getImage(); // Blank image for unowned settlement nodes 
    public final static Image WHITE_HOUSE_L = new ImageIcon(ImageRef.class.getResource("whiteHouseL.png")).getImage();
    public final static Image ORANGE_HOUSE_L = new ImageIcon(ImageRef.class.getResource("orangeHouseL.png")).getImage();
    public final static Image WHITE_HOUSE_S = new ImageIcon(ImageRef.class.getResource("whiteHouseS.png")).getImage();
    public final static Image ORANGE_HOUSE_S = new ImageIcon(ImageRef.class.getResource("orangeHouseS.png")).getImage();

    //image for the thief
    public final static Image THIEF = new ImageIcon(ImageRef.class.getResource("thief.png")).getImage();

    //the image for the water ring
    public final static Image WATER_RING = new ImageIcon(ImageRef.class.getResource("waterRing.png")).getImage();
    public final static Image WATER_RING_OVERLAY = new ImageIcon(ImageRef.class.getResource("waterRingOverlay.png")).getImage();
    
    //image for the background
    public final static Image WOOD_BACKGROUND = new ImageIcon(ImageRef.class.getResource("background.png")).getImage();

    //the image for the building materials
    public final static Image MATERIAL_KEY = new ImageIcon(ImageRef.class.getResource("buildKey.png")).getImage();
    
    //current player indecator
    public final static Image PLAYER_RED = new ImageIcon(ImageRef.class.getResource("playerRed.png")).getImage();
    public final static Image PLAYER_BLUE = new ImageIcon(ImageRef.class.getResource("playerBlue.png")).getImage();
    public final static Image PLAYER_NONE = new ImageIcon(ImageRef.class.getResource("playerNone.png")).getImage();
    public final static Image PLAYER_ORANGE = new ImageIcon(ImageRef.class.getResource("playerOrange.png")).getImage();
    public final static Image PLAYER_WHITE = new ImageIcon(ImageRef.class.getResource("playerWhite.png")).getImage();
    
    //smaller player indecator
    public final static Image SMALL_PLAYER_RED = new ImageIcon(ImageRef.class.getResource("smallPlayerRed.png")).getImage();
    public final static Image SMALL_PLAYER_BLUE = new ImageIcon(ImageRef.class.getResource("smallPlayerBlue.png")).getImage();
    public final static Image SMALL_PLAYER_NONE = new ImageIcon(ImageRef.class.getResource("smallPlayerNone.png")).getImage();
    public final static Image SMALL_PLAYER_ORANGE = new ImageIcon(ImageRef.class.getResource("smallPlayerOrange.png")).getImage();
    public final static Image SMALL_PLAYER_WHITE = new ImageIcon(ImageRef.class.getResource("smallPlayerWhite.png")).getImage();
    
    //dice images
    public final static Image DIE_IMAGE_1 = new ImageIcon(ImageRef.class.getResource("die1.png")).getImage();
    public final static Image DIE_IMAGE_2 = new ImageIcon(ImageRef.class.getResource("die2.png")).getImage();
    public final static Image DIE_IMAGE_3 = new ImageIcon(ImageRef.class.getResource("die3.png")).getImage();
    public final static Image DIE_IMAGE_4 = new ImageIcon(ImageRef.class.getResource("die4.png")).getImage();
    public final static Image DIE_IMAGE_5 = new ImageIcon(ImageRef.class.getResource("die5.png")).getImage();
    public final static Image DIE_IMAGE_6 = new ImageIcon(ImageRef.class.getResource("die6.png")).getImage();
    public final static Image DICE_GRAY = new ImageIcon(ImageRef.class.getResource("diceGray.png")).getImage();
    
    public final static Image[] DICE_IMAGES = new Image[]{
        DICE_GRAY, DIE_IMAGE_1, DIE_IMAGE_2, DIE_IMAGE_3, DIE_IMAGE_4, DIE_IMAGE_5, DIE_IMAGE_6};
    
    //store most of the images to reduce varable clutter in the main class (gamePanel)

    public ImageRef() {
    }
    
}
