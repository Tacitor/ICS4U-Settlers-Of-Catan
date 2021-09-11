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
    private final static String KNIGHT_TIP = "Knight";
    private final static String ROAD_TIP = "Road Building: Use this card to build two new roads for free. Regular rules still apply, only build cost is ommited.";
    private final static String MONOPOLY_TIP = "Monopoly";
    private final static String YOP_TIP = "Year of Pleanty";
    private final static String MARKET_TIP = "Market";
    private final static String UNI_TIP = "University";
    private final static String HALL_TIP = "Great Hall";
    private final static String CHALEL_TIP = "Chapel";
    private final static String LIBRARY_TIP = "Library";
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
