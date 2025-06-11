/*
 * Slash.java
 * Leo Bogaert
 * Jun 7, 2025,
 * Extends MeleeAttack, used for the king's slash attack
 */

package Attacks.Enemies.BloodKing;

import Attacks.MeleeAttack;
import Entitys.Enemies.BloodKing;
import Handlers.Sound.SoundHandlers.EnemySoundHandler;
import Main.Panels.GamePanel;
import Map.TiledMap;

import java.awt.*;

public class Slash extends MeleeAttack {

    public static final int COOLDOWN = 1000;
    private final BloodKing bloodKing;

    /**
     * Constructor for the Slash class.
     * @param bloodKing The BloodKing object that is performing the attack.
     */
    public Slash(BloodKing bloodKing) {
        super(1);

        this.bloodKing = bloodKing;
        GamePanel.enemyAttacks.add(this);
    }

    /**
     * Updates the attack's hitbox based on the current frame.
     */
    @Override
    public void update() {

        if (frame == 13 || (bloodKing.getState() == BloodKing.State.DEAD || bloodKing.getState() == BloodKing.State.DAMAGED)) {
            GamePanel.enemyAttacks.remove(this);
        } else if (frame == 2 || frame == 6) {
            hitBox = new Rectangle((bloodKing.getDirection().contains("right")) ? (int) bloodKing.getPosition().x + 20 : (int) bloodKing.getPosition().x - 35, (int) (bloodKing.getPosition().y - 10), 70, 80);
        } else {
            hitBox = null;
        }

        if (frame == 3 || frame == 7 && spriteCounter == 0){
            TiledMap.cameraShake(2, 7);
        }

        if (spriteCounter == 0){
            if (frame == 2) {
                EnemySoundHandler.slashSwing1();
            } else if (frame == 6) {
                EnemySoundHandler.slashSwing2();
            }
        }

        spriteCounter++;
        if (spriteCounter > 9) {
            spriteCounter = 0;
            frame++;
        }
    }
}
