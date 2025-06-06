/*
 * GhoulAttack.java
 * Leo Bogaert
 * May 28, 2025,
 * Extends MeleeAttack, used for the ghoul attack
 */

package Attacks.MeleeAttacks.Enemies;

import Attacks.MeleeAttacks.MeleeAttack;
import Entitys.Enemies.CagedShocker;
import Entitys.Enemies.TheHive.Wasp;
import Handlers.Sound.SoundHandlers.EnemySoundHandler;
import Main.Panels.GamePanel;

import java.awt.*;

public class WaspAttack extends MeleeAttack {

    public static final int COOLDOWN = 500;

    private final Wasp wasp;

    public WaspAttack(Wasp wasp) {
        super(1);

        this.wasp = wasp;
        GamePanel.enemyAttacks.add(this);
    }

    /**
     * Updates the attack's hitbox based on the current frame.
     */
    @Override
    public void update() {
        if (frame == 5 || wasp.getState() != Wasp.State.ATTACKING) {
            GamePanel.enemyAttacks.remove(this);
        } else if (frame == 3) {
            hitBox = new Rectangle((wasp.getDirection().contains("right")) ? (int) wasp.getPosition().x + 15 : (int) wasp.getPosition().x - 5, (int) (wasp.getPosition().y + 20), 20, 20);
        } else {
            hitBox = null;
        }

        spriteCounter++;
        if (spriteCounter >= 5) {
            spriteCounter = 0;
            frame++;
        }
    }
}
