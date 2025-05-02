package Entitys.MeleeAttacks;

import Handlers.Vector2;
import Main.Panels.GamePanel;

import java.awt.*;

public abstract class MeleeAttack {

    protected int damage;
    protected int spriteCounter, frame;
    protected boolean chain;

    protected Rectangle hitBox;

    public MeleeAttack(int damage) {
        this.damage = damage;

        frame = 0;
        spriteCounter = 0;
        this.hitBox = null;
    }

    public abstract void update();

    public void draw(Graphics g) {
        if (hitBox != null) {
            g.setColor(Color.RED);

            Vector2 cameraPos = GamePanel.tileMap.getCameraPos();

            double screenX = hitBox.x - cameraPos.x;
            double screenY = hitBox.y - cameraPos.y;

            g.drawRect((int) screenX, (int) screenY, hitBox.width, hitBox.height);
        }
    }

    public boolean getChain() {
        return chain;
    }
}


