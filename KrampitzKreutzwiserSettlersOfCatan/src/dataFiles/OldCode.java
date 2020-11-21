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
    /**
     * Search for cards of a certain type in the current player's inventory and
     * return if they are present Uses a linear search to find the type of card,
     * and how many copies must be found.
     *
     * @param type What resource type to look for
     * @param count How many cards of the type must be found to return true
     * @return If the user has the given number of the type of cards
     /
    private boolean findCards(int type, int count) {

        int amountFound = 0; // How many cards of the target type have been found

        for (int i = 0; i < cards[currentPlayer].size(); i++) {
            // If the card type matches
            if (cards[currentPlayer].get(i) == type) {
                // Increment the counter
                amountFound++;
                // If the target number of cards have been found
                if (amountFound == count) {
                    return true; // The user has the cards
                }
            } // The list is sorted by type, so if the type ID is greater than the target, stop searching
            else if (cards[currentPlayer].get(i) == type) {
                return false; // The user does not have the cards
            }
        }

        // If the user does not have the cards
        return false;
    }
    */
    
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
            /* //Temp tile scaling code
            //draw the tiles
            //choose a drawing type. One corrects for width and one corrects for height
            if (superFrame.getWidth() <= superFrame.getHeight()) { //for any aspect ratio where the height is the longer side
                g2d.drawImage(tiles.get(i).getImage(),
                        (int) (tiles.get(i).getXPos() / 1920.0 * superFrame.getWidth()), //x pos can stay the same because the width dictates the size

                        //the y pos however needs to be adjusted because in the mode the size is controled 
                        //by the width (the smaller one). Just means if a taller image is expected that gets corrected for.
                        getTileYPos(tiles.get(i).getYPos()) + tileYOffset,
                        getImgWidth(tiles.get(i).getImage()),
                        getImgHeight(tiles.get(i).getImage()), null);
            } else {

                g2d.drawImage(tiles.get(i).getImage(),
                        getTileXPos(tiles.get(i).getXPos()) + tileXOffset, //same thing as above just the opposite
                        (int) (tiles.get(i).getYPos() / 1080.0 * superFrame.getHeight()),
                        getImgWidth(tiles.get(i).getImage()),
                        getImgHeight(tiles.get(i).getImage()), null);
            }
            */
}
