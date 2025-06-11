/*
 * WaspAttack.java
 * Leo Bogaert
 * May 28, 2025,
 * Extends MeleeAttack, used for the wasp attack
 */

package Attacks.Enemies;

import Attacks.MeleeAttack;
import Entitys.Enemies.TheHive.Wasp;
import Handlers.Sound.SoundHandlers.EnemySoundHandler;
import Main.Panels.GamePanel;

import java.awt.*;

public class WaspAttack extends MeleeAttack {

    public static final int COOLDOWN = 4000;
    private final Wasp wasp;

    /**
     * Constructor for the WaspAttack class.
     * @param wasp The wasp object that is performing the attack.
     */
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
            hitBox = new Rectangle((wasp.getDirection().contains("right")) ? (int) wasp.getPosition().x + 15 : (int) wasp.getPosition().x - 5, (int) (wasp.getPosition().y + 20), 25, 20);
        } else {
            hitBox = null;
        }

        if (frame == 3 && spriteCounter == 0){
            EnemySoundHandler.waspSting();
        }

        spriteCounter++;
        if (spriteCounter >= 5) {
            spriteCounter = 0;
            frame++;
        }
    }
}
