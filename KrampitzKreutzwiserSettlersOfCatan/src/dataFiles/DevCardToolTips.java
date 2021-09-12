/*
 * Lukas Krampitz
 * Sep 10, 2021
 * This Class just contains the Strings for the dev card tooltips
 */
package dataFiles;

/**
 *
 * @author Tacitor
 */
public class DevCardToolTips {

    //sigle vars
    private final static String KNIGHT_TIP = "Knight Card: Use this card to move the robber to any Tile. Any player with a building on the tile can be stolen from.";
    private final static String ROAD_TIP = "Road Building Card: Use this card to build two new roads for free. Regular rules still apply, only build cost is omitted.";
    private final static String MONOPOLY_TIP = "Monopoly Card: Use this card to select one resource type. You will then receive all the cards of the type from all players.";
    private final static String YOP_TIP = "Year of Plenty Card: Use this card to select one resource type. You will then receive two of the resource type.";
    private final static String MARKET_TIP = "Market Card: This is a Victory Point card. You built a Market for 1 VP. Others cannot see this point publicly.";
    private final static String UNI_TIP = "University Card: This is a Victory Point card. You built a University for 1 VP. Others cannot see this point publicly.";
    private final static String HALL_TIP = "Great Hall Card: This is a Victory Point card. You built a Great Hall for 1 VP. Others cannot see this point publicly.";
    private final static String CHALEL_TIP = "Chapel Card: This is a Victory Point card. You built a Chapel for 1 VP. Others cannot see this point publicly.";
    private final static String LIBRARY_TIP = "Library Card: This is a Victory Point card. You built a Library for 1 VP. Others cannot see this point publicly.";
    //master array
    private final static String[] TOOLTIPS = new String[]{KNIGHT_TIP, ROAD_TIP, MONOPOLY_TIP, YOP_TIP, MARKET_TIP, UNI_TIP, HALL_TIP, CHALEL_TIP, LIBRARY_TIP};

    /**
     * Accessors for the development card tool tips
     *
     * @return
     */
    public static String[] getDevCardTooltips() {
        return TOOLTIPS;
    }

}
