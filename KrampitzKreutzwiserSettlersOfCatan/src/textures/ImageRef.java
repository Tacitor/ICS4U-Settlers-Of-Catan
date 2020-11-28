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

    //images for the settlements
    public final static Image BLUE_HOUSE_L = new ImageIcon(ImageRef.class.getResource("blueHouseL.png")).getImage();
    public final static Image RED_HOUSE_L = new ImageIcon(ImageRef.class.getResource("redHouseL.png")).getImage();
    public final static Image BLUE_HOUSE_S = new ImageIcon(ImageRef.class.getResource("blueHouseS.png")).getImage();
    public final static Image RED_HOUSE_S = new ImageIcon(ImageRef.class.getResource("redHouseS.png")).getImage();
    public final static Image BLANK_HOUSE = new ImageIcon(ImageRef.class.getResource("blankHouse.png")).getImage(); // Blank image for unowned settlement nodes 

    //image for the thief
    public final static Image THIEF = new ImageIcon(ImageRef.class.getResource("thief.png")).getImage();

    //the image for the water ring
    public final static Image WATER_RING = new ImageIcon(ImageRef.class.getResource("waterRing.png")).getImage();

    //the image for the building materials
    public final static Image MATERIAL_KEY = new ImageIcon(ImageRef.class.getResource("buildKey.png")).getImage();
    
    //store most of the images to reduce varable clutter in the main class (gamePanel)

    public ImageRef() {
    }
    
}
