package Main.UI.VFX;

import Handlers.Vector2;
import Main.Panels.GamePanel;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class Hit extends Effect{

    boolean flip;

    public Hit(int x, int y, boolean flip) {
        super();
        position = new Vector2(x, y);
        spriteRow = 2;
        maxSpriteCol = 2;

        this.flip = flip;

        GamePanel.effects.add(this);
    }

    public void update(){
        super.update();
        if (spriteCol > maxSpriteCol)
            GamePanel.effects.remove(this);
    }

    public void draw(Graphics2D g2) {
        // hitbox
//        super.draw(g2);

        Vector2 cameraPos = GamePanel.tileMap.returnCameraPos();

        double screenX = position.x - 41 - cameraPos.x;
        double screenY = position.y - 31 - cameraPos.y;

        // Flip image
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
