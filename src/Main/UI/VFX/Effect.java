/*
 * Effect.java
 * Leo Bogaert
 * Jun 7, 2025,
 * Parent class for effects
 */

package Main.UI.VFX;

import Handlers.ImageHandler;
import Handlers.Vector2;
import Main.Panels.GamePanel;

import java.awt.*;
import java.awt.image.VolatileImage;

public class Effect {

    protected VolatileImage image;
    protected Vector2 position;

    protected int spriteCounter, spriteCol, spriteRow, maxSpriteCol;
    protected double spriteScale;

    /**
     * Constructor for Effect
     */
    public Effect (){
        image = ImageHandler.loadImage("Images/VFX/All.png");

        spriteScale = GamePanel.scale;
    }

    /**
     * Updates effect's sprite animation.
     */
    public void update() {
        spriteCounter++;
        if (spriteCounter > 5) {
            spriteCounter = 0;
            spriteCol++;
        }
    }

    /**
     * Draws effect hitbox
     * @param g2 Graphics2D object to draw on
     */
    public void draw(Graphics2D g2) {

        Vector2 cameraPos = GamePanel.tileMap.returnCameraPos();

        double screenX = position.x - cameraPos.x;
        double screenY = position.y - cameraPos.y;

        g2.drawRect((int) screenX, (int) screenY, 82, 65);
    }

}
