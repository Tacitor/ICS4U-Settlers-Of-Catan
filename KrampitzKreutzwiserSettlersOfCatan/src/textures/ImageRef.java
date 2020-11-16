/*
 * Lukas Krampitz
 * Nov 8, 2020
 * This is a blank class simply used to locate the textures package for when it is complied to a jar. Yes there is a better way, no I did not have time to find it.
 */
package textures;

import java.awt.Image;
import javax.swing.ImageIcon;
import krampitzkreutzwisersettlersofcatan.GameFrame;

/**
 *
 * @author Tacitor
 */
public class ImageRef {
    
    //the image for the water ring
    public final static Image WATER_RING = new ImageIcon(ImageRef.class.getResource("waterRing.png")).getImage();

    public ImageRef() {
    }
    
    public static int getImgWidth(GameFrame frame, Image image) {
        return (int) (image.getWidth(null) / 1920.0 * frame.getWidth());
    }
    
    public static int getImgHeight(GameFrame frame, Image image) {
        return (int) (image.getHeight(null) / 1080.0 * frame.getHeight());
    }
    
}
