/*
 * Hit.java
 * Leo Bogaert
 * Jun 7, 2025,
 * Renders a hit effect
 */

package Main.UI.VFX;

import Handlers.Vector2;
import Main.Panels.GamePanel;

import java.awt.*;

public class Hit extends Effect{

    boolean flip;

    /**
     * Constructor for Hit effect
     * @param x int x position of the hit effect
     * @param y int y position of the hit effect
     * @param flip boolean whether the hit effect should be flipped horizontally
     */
    public Hit(int x, int y, boolean flip) {
        super();
        position = new Vector2(x, y);
        spriteRow = 2;
        maxSpriteCol = 2;

        this.flip = flip;

        GamePanel.effects.add(this);
    }

    /**
     * Update the hit effect
     */
    public void update(){
        super.update();
        if (spriteCol > maxSpriteCol)
            GamePanel.effects.remove(this);
    }

    /**
     * Draw the hit effect
     * @param g2 Graphics2D object to draw on
     */
    public void draw(Graphics2D g2) {

//        super.draw(g2); // Debug

        Vector2 cameraPos = GamePanel.tileMap.returnCameraPos();

        double screenX = position.x - 41 - cameraPos.x;
        double screenY = position.y - 31 - cameraPos.y;

        if (flip) {
            g2.drawImage(
                    image,
                    (int) screenX, (int) screenY,
                    (int) (screenX + 82), (int) (screenY + 65),
                    spriteCol * 82 + 82, spriteRow * 65, spriteCol * 82, spriteRow * 65 + 65, null);
        } else {
            g2.drawImage(
                    image,
                    (int) screenX, (int) screenY,
                    (int) (screenX + 82), (int) (screenY + 65),
                    spriteCol * 82, spriteRow * 65, spriteCol * 82 + 82, spriteRow * 65 + 65, null);

        }
    }

}
