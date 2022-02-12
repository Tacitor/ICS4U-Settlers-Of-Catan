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
    
    //store most of the images to reduce varable clutter in the main class (gamePanel)
    
    //error image
    public final static Image ERROR_IMAGE = new ImageIcon(ImageRef.class.getResource("util/error.png")).getImage();
    
    //icon image for game
    public final static Image ICON = new ImageIcon(ImageRef.class.getResource("util/icon.png")).getImage();
    
    //images for the resource cards
    public final static Image CARD_CLAY = new ImageIcon(ImageRef.class.getResource("cards/cardClay.png")).getImage();
    public final static Image CARD_WHEAT = new ImageIcon(ImageRef.class.getResource("cards/cardWheat.png")).getImage();
    public final static Image CARD_ORE = new ImageIcon(ImageRef.class.getResource("cards/cardOre.png")).getImage();
    public final static Image CARD_SHEEP = new ImageIcon(ImageRef.class.getResource("cards/cardSheep.png")).getImage();
    public final static Image CARD_WOOD = new ImageIcon(ImageRef.class.getResource("cards/cardWood.png")).getImage();
    
    //images for the development cards
    //place holders
    public final static Image DEV_CARD_KNIGHT = new ImageIcon(ImageRef.class.getResource("cards/devCardKnight.png")).getImage();
    //dev card vp
    public final static Image DEV_CARD_VP_UNI = new ImageIcon(ImageRef.class.getResource("cards/devCardVPUni.png")).getImage();
    public final static Image DEV_CARD_VP_MARKET = new ImageIcon(ImageRef.class.getResource("cards/devCardVPMarket.png")).getImage();
    public final static Image DEV_CARD_VP_LIBRARY = new ImageIcon(ImageRef.class.getResource("cards/devCardVPLibrary.png")).getImage();
    public final static Image DEV_CARD_VP_HALL = new ImageIcon(ImageRef.class.getResource("cards/devCardVPHall.png")).getImage();
    public final static Image DEV_CARD_VP_CHAPEL = new ImageIcon(ImageRef.class.getResource("cards/devCardVPChapel.png")).getImage();
    //dev card progress
    public final static Image DEV_CARD_PROGRESS_ROAD = new ImageIcon(ImageRef.class.getResource("cards/devCardProgressRoad.png")).getImage();
    public final static Image DEV_CARD_PROGRESS_MONO = new ImageIcon(ImageRef.class.getResource("cards/devCardProgressMono.png")).getImage();
    public final static Image DEV_CARD_PROGRESS_YOP = new ImageIcon(ImageRef.class.getResource("cards/devCardProgressYOP.png")).getImage();

    //images for the roads
    public final static Image RED_ROAD_H = new ImageIcon(ImageRef.class.getResource("playerPieces/redRoadH.png")).getImage(); //horizontal road
    public final static Image BLUE_ROAD_H = new ImageIcon(ImageRef.class.getResource("playerPieces/blueRoadH.png")).getImage();
    public final static Image RED_ROAD_R = new ImageIcon(ImageRef.class.getResource("playerPieces/redRoadR.png")).getImage(); //diagonal to the right (refernce point is the top of the road)
    public final static Image BLUE_ROAD_R = new ImageIcon(ImageRef.class.getResource("playerPieces/blueRoadR.png")).getImage();
    public final static Image RED_ROAD_L = new ImageIcon(ImageRef.class.getResource("playerPieces/redRoadL.png")).getImage(); //diagonal to the left
    public final static Image BLUE_ROAD_L = new ImageIcon(ImageRef.class.getResource("playerPieces/blueRoadL.png")).getImage();
    public final static Image BLANK_ROAD_H = new ImageIcon(ImageRef.class.getResource("playerPieces/blankRoadH.png")).getImage(); // Blank images for horizontal
    public final static Image BLANK_ROAD_V = new ImageIcon(ImageRef.class.getResource("playerPieces/blankRoadV.png")).getImage(); // and non horizontal roads
    
    public final static Image WHITE_ROAD_H = new ImageIcon(ImageRef.class.getResource("playerPieces/whiteRoadH.png")).getImage(); //white images 
    public final static Image WHITE_ROAD_R = new ImageIcon(ImageRef.class.getResource("playerPieces/whiteRoadR.png")).getImage();
    public final static Image WHITE_ROAD_L = new ImageIcon(ImageRef.class.getResource("playerPieces/whiteRoadL.png")).getImage();
    
    public final static Image ORANGE_ROAD_H = new ImageIcon(ImageRef.class.getResource("playerPieces/orangeRoadH.png")).getImage(); //orange images for horizontal
    public final static Image ORANGE_ROAD_R = new ImageIcon(ImageRef.class.getResource("playerPieces/orangeRoadR.png")).getImage(); 
    public final static Image ORANGE_ROAD_L = new ImageIcon(ImageRef.class.getResource("playerPieces/orangeRoadL.png")).getImage(); 

    //images for the settlements
    public final static Image BLANK_HOUSE = new ImageIcon(ImageRef.class.getResource("playerPieces/blankHouse.png")).getImage(); // Blank image for unowned settlement nodes 
    //the red ones
    public final static Image RED_HOUSE_L = new ImageIcon(ImageRef.class.getResource("playerPieces/redHouseL.png")).getImage();
    public final static Image RED_HOUSE_S0 = new ImageIcon(ImageRef.class.getResource("playerPieces/redHouseSmoke0.png")).getImage();
    public final static Image RED_HOUSE_S1 = new ImageIcon(ImageRef.class.getResource("playerPieces/redHouseSmoke1.png")).getImage();
    public final static Image RED_HOUSE_S2 = new ImageIcon(ImageRef.class.getResource("playerPieces/redHouseSmoke2.png")).getImage();
    public final static Image[] RED_HOUSES_S = new Image[]{
        RED_HOUSE_S0, RED_HOUSE_S1, RED_HOUSE_S2, RED_HOUSE_S1};
    //blue
    public final static Image BLUE_HOUSE_L = new ImageIcon(ImageRef.class.getResource("playerPieces/blueHouseL.png")).getImage();
    public final static Image BLUE_HOUSE_S0 = new ImageIcon(ImageRef.class.getResource("playerPieces/blueHouseSmoke0.png")).getImage();
    public final static Image BLUE_HOUSE_S1 = new ImageIcon(ImageRef.class.getResource("playerPieces/blueHouseSmoke1.png")).getImage();
    public final static Image BLUE_HOUSE_S2 = new ImageIcon(ImageRef.class.getResource("playerPieces/blueHouseSmoke2.png")).getImage();
    public final static Image[] BLUE_HOUSES_S = new Image[]{
        BLUE_HOUSE_S0, BLUE_HOUSE_S1, BLUE_HOUSE_S2, BLUE_HOUSE_S1};
    //white
    public final static Image WHITE_HOUSE_L = new ImageIcon(ImageRef.class.getResource("playerPieces/whiteHouseL.png")).getImage();
    public final static Image WHITE_HOUSE_S0 = new ImageIcon(ImageRef.class.getResource("playerPieces/whiteHouseSmoke0.png")).getImage();
    public final static Image WHITE_HOUSE_S1 = new ImageIcon(ImageRef.class.getResource("playerPieces/whiteHouseSmoke1.png")).getImage();
    public final static Image WHITE_HOUSE_S2 = new ImageIcon(ImageRef.class.getResource("playerPieces/whiteHouseSmoke2.png")).getImage();
    public final static Image[] WHITE_HOUSES_S = new Image[]{
        WHITE_HOUSE_S0, WHITE_HOUSE_S1, WHITE_HOUSE_S2, WHITE_HOUSE_S1};
    //orange
    public final static Image ORANGE_HOUSE_L = new ImageIcon(ImageRef.class.getResource("playerPieces/orangeHouseL.png")).getImage();
    public final static Image ORANGE_HOUSE_S0 = new ImageIcon(ImageRef.class.getResource("playerPieces/orangeHouseSmoke0.png")).getImage();
    public final static Image ORANGE_HOUSE_S1 = new ImageIcon(ImageRef.class.getResource("playerPieces/orangeHouseSmoke1.png")).getImage();
    public final static Image ORANGE_HOUSE_S2 = new ImageIcon(ImageRef.class.getResource("playerPieces/orangeHouseSmoke2.png")).getImage();
    public final static Image[] ORANGE_HOUSES_S = new Image[]{
        ORANGE_HOUSE_S0, ORANGE_HOUSE_S1, ORANGE_HOUSE_S2, ORANGE_HOUSE_S1};

    //image for the thief
    public final static Image THIEF = new ImageIcon(ImageRef.class.getResource("util/thief.png")).getImage();

    //the image for the water ring
    public final static Image WATER_RING = new ImageIcon(ImageRef.class.getResource("water/waterRing.png")).getImage();
    public final static Image WATER_RING_OVERLAY = new ImageIcon(ImageRef.class.getResource("water/waterRingOverlay.png")).getImage();
    
    //image for the background
    public final static Image WOOD_BACKGROUND = new ImageIcon(ImageRef.class.getResource("util/background.png")).getImage();

    //the image for the building materials
    public final static Image MATERIAL_KEY = new ImageIcon(ImageRef.class.getResource("bigCard/buildKeySmoke.png")).getImage();
    
    //current player indecator
    public final static Image PLAYER_RED = new ImageIcon(ImageRef.class.getResource("playerIcons/playerRed.png")).getImage();
    public final static Image PLAYER_BLUE = new ImageIcon(ImageRef.class.getResource("playerIcons/playerBlue.png")).getImage();
    public final static Image PLAYER_NONE = new ImageIcon(ImageRef.class.getResource("playerIcons/playerNone.png")).getImage();
    public final static Image PLAYER_ORANGE = new ImageIcon(ImageRef.class.getResource("playerIcons/playerOrange.png")).getImage();
    public final static Image PLAYER_WHITE = new ImageIcon(ImageRef.class.getResource("playerIcons/playerWhite.png")).getImage();
    
    //smaller player indecator
    public final static Image SMALL_PLAYER_RED = new ImageIcon(ImageRef.class.getResource("playerIcons/smallPlayerRed.png")).getImage();
    public final static Image SMALL_PLAYER_BLUE = new ImageIcon(ImageRef.class.getResource("playerIcons/smallPlayerBlue.png")).getImage();
    public final static Image SMALL_PLAYER_NONE = new ImageIcon(ImageRef.class.getResource("playerIcons/smallPlayerNone.png")).getImage();
    public final static Image SMALL_PLAYER_ORANGE = new ImageIcon(ImageRef.class.getResource("playerIcons/smallPlayerOrange.png")).getImage();
    public final static Image SMALL_PLAYER_WHITE = new ImageIcon(ImageRef.class.getResource("playerIcons/smallPlayerWhite.png")).getImage();
    
    //dice images
    public final static Image DIE_IMAGE_1 = new ImageIcon(ImageRef.class.getResource("dice/die1.png")).getImage();
    public final static Image DIE_IMAGE_2 = new ImageIcon(ImageRef.class.getResource("dice/die2.png")).getImage();
    public final static Image DIE_IMAGE_3 = new ImageIcon(ImageRef.class.getResource("dice/die3.png")).getImage();
    public final static Image DIE_IMAGE_4 = new ImageIcon(ImageRef.class.getResource("dice/die4.png")).getImage();
    public final static Image DIE_IMAGE_5 = new ImageIcon(ImageRef.class.getResource("dice/die5.png")).getImage();
    public final static Image DIE_IMAGE_6 = new ImageIcon(ImageRef.class.getResource("dice/die6.png")).getImage();
    public final static Image DICE_GRAY = new ImageIcon(ImageRef.class.getResource("dice/diceGray.png")).getImage();
    
    public final static Image[] DICE_IMAGES = new Image[]{
        DICE_GRAY, DIE_IMAGE_1, DIE_IMAGE_2, DIE_IMAGE_3, DIE_IMAGE_4, DIE_IMAGE_5, DIE_IMAGE_6};
    
    
    //player coloured dots
    private final static Image DOT_RED = new ImageIcon(ImageRef.class.getResource("playerIcons/redDot.png")).getImage();
    private final static Image DOT_BLUE = new ImageIcon(ImageRef.class.getResource("playerIcons/blueDot.png")).getImage();
    private final static Image DOT_ORANGE = new ImageIcon(ImageRef.class.getResource("playerIcons/orangeDot.png")).getImage();
    private final static Image DOT_WHITE = new ImageIcon(ImageRef.class.getResource("playerIcons/whiteDot.png")).getImage();
    //array for the dots to be called on to get the correct colour for each player
    public final static Image[] PLAYER_DOTS = new Image[]{ERROR_IMAGE, DOT_RED, DOT_BLUE, DOT_ORANGE, DOT_WHITE};
    
    //longest road tile thing
    public final static Image LONGEST_ROAD = new ImageIcon(ImageRef.class.getResource("bigCard/longestRoad.png")).getImage();
    
    //largest army tile thing
    public final static Image LARGEST_ARMY = new ImageIcon(ImageRef.class.getResource("bigCard/largestArmy.png")).getImage();
    
    //image files for the resource stacks
    private final static Image CLAY_STACK = new ImageIcon(ImageRef.class.getResource("util/clayStack.png")).getImage();
    private final static Image WOOD_STACK = new ImageIcon(ImageRef.class.getResource("util/woodStack.png")).getImage();
    private final static Image WHEAT_STACK = new ImageIcon(ImageRef.class.getResource("util/wheatStack.png")).getImage();
    private final static Image SHEEP_STACK = new ImageIcon(ImageRef.class.getResource("util/sheepStack.png")).getImage();
    private final static Image ORE_STACK = new ImageIcon(ImageRef.class.getResource("util/oreStack.png")).getImage();
    //the public array to acces those images
    public final static Image[] RES_STACKS = new Image[]{ERROR_IMAGE, CLAY_STACK, WOOD_STACK, WHEAT_STACK, SHEEP_STACK, ORE_STACK};
    
    //the image for dev card too tips
    public final static Image TOOLTIP_DEV_CARD_BGD = new ImageIcon(ImageRef.class.getResource("util/tooltipBackground.png")).getImage();

    public ImageRef() {
    }
    
}
