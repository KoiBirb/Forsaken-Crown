/*
 * MeleeAttack.java
 * Leo Bogaert
 * May 6, 2025,
 * Parent attack class for melee attacks
 */

package Attacks.MeleeAttacks;

import Handlers.Vector2;
import Main.Panels.GamePanel;

import java.awt.*;

public abstract class MeleeAttack {

    protected int damage;
    protected int spriteCounter;
    protected int frame;
    protected static int cooldown;
    protected boolean chain;

    protected Rectangle hitBox;

    /**
     * Constructor for MeleeAttack
     */
    public MeleeAttack(int damage, int cooldown) {
        this.damage = damage;
        this.cooldown = cooldown;

        frame = 0;
        spriteCounter = 0;
        this.hitBox = null;
    }

    /**
     * abstract method to update the attack based on the frame
     */
    public abstract void update();

    /**
     * Debug draw method, shows hitbox of attack
     * @param g Graphics object to draw on
     */
    public void draw(Graphics g) {
        if (hitBox != null) {
            g.setColor(Color.RED);

            Vector2 cameraPos = GamePanel.tileMap.getCameraPos();

            double screenX = hitBox.x - cameraPos.x;
            double screenY = hitBox.y - cameraPos.y;

            g.drawRect((int) screenX, (int) screenY, hitBox.width, hitBox.height);
        }
    }

    /**
     * Getter for chain variable
     * @return true if active, false otherwise
     */
    public boolean getChain() {
        return chain;
    }


    public static int getCooldown() {
        return cooldown;
    }
}


