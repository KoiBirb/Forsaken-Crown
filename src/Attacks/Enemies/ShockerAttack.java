/*
 * ShockerAttack.java
 * Leo Bogaert
 * Jun 7, 2025,
 * Extends MeleeAttack, used for the Shocker attack
 */

package Attacks.Enemies;

import Attacks.MeleeAttack;
import Entitys.Enemies.CagedShocker;
import Handlers.Sound.SoundHandlers.EnemySoundHandler;
import Main.Panels.GamePanel;

import java.awt.*;

public class ShockerAttack extends MeleeAttack {

    public static final int COOLDOWN = 3500;
    private final CagedShocker shocker;

    /**
     * Constructor for the ShockerAttack class.
     * @param shocker The CagedShocker object that is performing the attack.
     */
    public ShockerAttack(CagedShocker shocker) {
        super(4);

        this.shocker = shocker;
        GamePanel.enemyAttacks.add(this);

        EnemySoundHandler.shockerCharge();
    }

    /**
     * Updates the attack's hitbox based on the current frame.
     */
    @Override
    public void update() {
        if (frame == 15 || shocker.getState() != CagedShocker.State.ATTACKING) {
            GamePanel.enemyAttacks.remove(this);
        } else if (frame == 6) {
            hitBox = new Rectangle((shocker.getDirection().contains("right")) ? (int) shocker.getPosition().x - 5 : (int) shocker.getPosition().x - 120, (int) (shocker.getPosition().y + 50), 165, 40);
        } else if (frame == 11) {
            hitBox = new Rectangle((shocker.getDirection().contains("right")) ? (int) shocker.getPosition().x - 15 : (int) shocker.getPosition().x - 110, (int) (shocker.getPosition().y + 50), 165, 40);
        } else {
            hitBox = null;
        }

        if (spriteCounter == 0) {
            if (frame == 6) {
                EnemySoundHandler.shockerSwing1();
            } else if (frame == 11) {
                EnemySoundHandler.shockerEnd();
                EnemySoundHandler.shockerSwing2();
            }
        }

        spriteCounter++;
        if (spriteCounter > 11) {
            spriteCounter = 0;
            frame++;
        }
    }
}
