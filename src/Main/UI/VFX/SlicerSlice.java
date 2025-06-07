/*
 * SlicerSlice.java
 * Leo Bogaert
 * Jun 7, 2025,
 * Renders a slice effect for the Slicer enemy
 */

package Main.UI.VFX;

import Handlers.ImageHandler;
import Handlers.Vector2;
import Main.Panels.GamePanel;

import java.awt.*;
import java.awt.image.VolatileImage;

public class SlicerSlice extends Effect{

    boolean flip;

    private static final VolatileImage imageVFX = ImageHandler.loadImage("Assets/Images/Enemies/Heavy Slicer/VFX Sprite Sheet 60x41.png");

    /**
     * Constructor for SlicerSlice
     * @param x int x position of the slice
     * @param y int y position of the slice
     * @param flip boolean whether the slice should be flipped horizontally
     */
    public SlicerSlice(int x, int y, boolean flip) {
        super();
        position = new Vector2(x, y);
        spriteRow = 1;
        maxSpriteCol = 2;

        this.image = imageVFX;

        this.flip = flip;

        GamePanel.effects.add(this);
    }

    /**
     * Update the slice effect
     */
    public void update(){
        spriteCounter++;
        if (spriteCounter > 12) {
            spriteCounter = 0;
            spriteCol++;
        }

        if (spriteCol > maxSpriteCol)
            GamePanel.effects.remove(this);
    }

    /**
     * Draw the slice effect
     * @param g2 Graphics2D object to draw on
     */
    public void draw(Graphics2D g2) {

//        super.draw(g2); // Debug

        Vector2 cameraPos = GamePanel.tileMap.returnCameraPos();

        double screenX = position.x - 41 - cameraPos.x;
        double screenY = position.y - 31 - cameraPos.y;

        double sizeX = 60 * 2;
        double sizeY = 41 * 2;

        if (flip) {
            g2.drawImage(
                image,
                (int) screenX - 9, (int) screenY + 15,
                (int) (screenX + sizeX) - 9, (int) (screenY + sizeY) + 15,
                spriteCol * 60 + 60, spriteRow * 41, spriteCol * 60, spriteRow * 41 + 41, null);
        } else {
            g2.drawImage(
                image,
                (int) screenX + 26, (int) screenY + 15,
                (int) (screenX + sizeX) + 26, (int) (screenY + sizeY) + 15,
                spriteCol * 60, spriteRow * 41, spriteCol * 60 + 60, spriteRow * 41 + 41, null);
        }
    }

}
