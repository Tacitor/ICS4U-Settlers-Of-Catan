/*
 * Lukas Krampitz
 * Aug 27, 2023
 * An interface the disctates global methods for Jcomponents that are resizeable
 */
package krampitzkreutzwisersettlersofcatan.gui;

import java.awt.Image;

/**
 *
 * @author Tacitor
 */
public interface SDScaleImageResizeable {
    
    public int getLocalImgWidth(Image image);
    public int getLocalImgHeight(Image image);    
    
}
