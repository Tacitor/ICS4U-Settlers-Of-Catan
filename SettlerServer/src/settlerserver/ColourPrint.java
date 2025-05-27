/*
 * Lukas Krampitz
 * May 26, 2025
 * Utility class to assist with coloured printing. Supports both the
 * attached NetBeans IDE 8.2 Output, and Debian 12 bash.
 */
package settlerserver;

/**
 *
 * @author Tacitor
 */
public class ColourPrint {

    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";

    /**
     * Prints the provided String to the System.out PrintStream in ANSI green.
     * Ensures colour formatting is cleared afterwards.
     *
     * @param s
     */
    public static void printGreen(String s) {
        System.out.println(ANSI_GREEN + s + ANSI_RESET);
    }

    /**
     * Prints the provided String to the System.out PrintStream in ANSI red.
     * Ensures colour formatting is cleared afterwards.
     *
     * @param s
     */
    public static void printRed(String s) {
        System.out.println(ANSI_RED + s + ANSI_RESET);
    }

    /**
     * Prints the provided String to the System.out PrintStream in ANSI purple.
     * Ensures colour formatting is cleared afterwards.
     *
     * @param s
     */
    public static void printPurple(String s) {
        System.out.println(ANSI_PURPLE + s + ANSI_RESET);
    }
}
