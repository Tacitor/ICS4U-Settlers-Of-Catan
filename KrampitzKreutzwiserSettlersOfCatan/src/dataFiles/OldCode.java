/*
 * Lukas Krampitz
 * Nov 8, 2020
 * Just a file with some old code that we should keep just in case. This will never run from here
 */
package dataFiles;

/**
 *
 * @author Tacitor
 */
public class OldCode {
    
    /*  
        //the old way to draw the board this is where the coordinates come from for the new way
        
        //draw the baord from left to right top to bottom. Find the position of where every tile must go
        
        //draw left most column
        //draw 2 left of center, 1 above
        g2d.drawImage(tiles.get(0).getImage(), 1920 / 2 - (150 / 2) - (112 * 2), 1080 / 2 - (130 / 2) - (130), null);
        //draw 2 left of center
        g2d.drawImage(tiles.get(1).getImage(), 1920 / 2 - (150 / 2) - (112 * 2), 1080 / 2 - (130 / 2), null);        
        //draw 2 left of center, 1 below
        g2d.drawImage(tiles.get(2).getImage(), 1920 / 2 - (150 / 2) - (112 * 2), 1080 / 2 - (130 / 2) + (130), null);
        
        //draw the left middle column
        //draw 2 above center, 1 left
        g2d.drawImage(tiles.get(3).getImage(), 1920 / 2 - (150 / 2) - (112), 1080 / 2 - (130 / 2) - (130 / 2) - (130), null); //for the y pos: first center, then align to the next row of hexes, then move it up by one full hight
        //draw 1 above center, 1 left
        g2d.drawImage(tiles.get(4).getImage(), 1920 / 2 - (150 / 2) - (112), 1080 / 2 - (130 / 2) - (130 / 2), null); //for x pos: center it and then shift by 112 with is the length requires to align properly
        //draw 1 below center, 1 left
        g2d.drawImage(tiles.get(5).getImage(), 1920 / 2 - (150 / 2) - (112), 1080 / 2 - (130 / 2) + (130 / 2), null);
        //draw 2 below center, 1 left
        g2d.drawImage(tiles.get(6).getImage(), 1920 / 2 - (150 / 2) - (112), 1080 / 2 - (130 / 2) + (130 / 2) + (130), null);
        
        //draw the 2 above center
        //draw 2 above center
        g2d.drawImage(tiles.get(7).getImage(), 1920 / 2 - (150 / 2), 1080 / 2 - (130 / 2) - (130 * 2), null); //(x,y) (cut the screen in half then subtract half the tile size to center then add the y offset, cut the screen in half then subtract half the tile size to center then add the x offset)
        //draw 1 above center
        g2d.drawImage(tiles.get(8).getImage(), 1920 / 2 - (150 / 2), 1080 / 2 - (130 / 2) - 130, null);
        
        //draw center tile
        g2d.drawImage(tiles.get(9).getImage(), 1920 / 2 - (150 / 2), 1080 / 2 - (130 / 2), null); //draw the image with the upper left corner on the x,y pos

        //draw the 2 below center
        //draw 1 below center
        g2d.drawImage(tiles.get(10).getImage(), 1920 / 2 - (150 / 2), 1080 / 2 - (130 / 2) + 130, null);
        //draw 2 below center
        g2d.drawImage(tiles.get(11).getImage(), 1920 / 2 - (150 / 2), 1080 / 2 - (130 / 2) + (130 * 2), null);

        
        //draw the right middle column
        //draw 2 above center, 1 right
        g2d.drawImage(tiles.get(12).getImage(), 1920 / 2 - (150 / 2) + (112), 1080 / 2 - (130 / 2) - (130 / 2) - (130), null); //for the y pos: first center, then align to the next row of hexes, then move it up by one full hight
        //draw 1 above center, 1 right
        g2d.drawImage(tiles.get(13).getImage(), 1920 / 2 - (150 / 2) + (112), 1080 / 2 - (130 / 2) - (130 / 2), null); //for x pos: center it and then shift by 112 with is the length requires to align properly
        //draw 1 below center, 1 right
        g2d.drawImage(tiles.get(14).getImage(), 1920 / 2 - (150 / 2) + (112), 1080 / 2 - (130 / 2) + (130 / 2), null);
        //draw 2 below center, 1 right
        g2d.drawImage(tiles.get(15).getImage(), 1920 / 2 - (150 / 2) + (112), 1080 / 2 - (130 / 2) + (130 / 2) + (130), null);

        

        //draw right most column
        //draw 2 right of center, 1 above
        g2d.drawImage(tiles.get(16).getImage(), 1920 / 2 - (150 / 2) + (112 * 2), 1080 / 2 - (130 / 2) - (130), null);
        //draw 2 right of center
        g2d.drawImage(tiles.get(17).getImage(), 1920 / 2 - (150 / 2) + (112 * 2), 1080 / 2 - (130 / 2), null);
        //draw 2 right of center, 1 below
        g2d.drawImage(tiles.get(18).getImage(), 1920 / 2 - (150 / 2) + (112 * 2), 1080 / 2 - (130 / 2) + (130), null);
        */
    
}
