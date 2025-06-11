/*
 * HeartSlam.java
 * Leo Bogaert
 * Jun 7, 2025,
 * Extends MeleeAttack, used for the king's heart attack
 */

package Attacks.Enemies.BloodKing;

import Attacks.MeleeAttack;
import Entitys.Enemies.BloodKing;
import Handlers.Sound.SoundHandlers.EnemySoundHandler;
import Main.Panels.GamePanel;
import Map.TiledMap;

import java.awt.*;

public class HeartSlam extends MeleeAttack {

    public static final int COOLDOWN = 10000;
    private final BloodKing bloodKing;

    /**
     * Constructor for the HeartSlam class.
     * @param bloodKing The BloodKing object that is performing the attack.
     */
    public HeartSlam (BloodKing bloodKing) {
        super(3);

        this.bloodKing = bloodKing;
        GamePanel.enemyAttacks.add(this);
    }

    /**
     * Updates the attack's hitbox based on the current frame.
     */
    @Override
    public void update() {

        if (frame == 8) {
            bloodKing.canMove(false);
        }

        if (frame == 15) {
            bloodKing.canMove(true);
            GamePanel.enemyAttacks.remove(this);
        } else if (frame == 10) {
            hitBox = new Rectangle((int) bloodKing.getPosition().x - 50, (int) (bloodKing.getPosition().y + 40), 150, 30);
        } else if (frame == 11){
            hitBox = new Rectangle((int) bloodKing.getPosition().x - 120, (int) (bloodKing.getPosition().y + 45), 250, 30);
        } else {
            hitBox = null;
        }

        if (frame == 4){
            TiledMap.cameraShake(3, 1);
        }

        if (spriteCounter == 0) {
            if (frame == 3) {
                EnemySoundHandler.heartAppear();
            } else if (frame == 10) {
                EnemySoundHandler.heartSplash();
            }
        }

        if (frame == 10 || frame == 11){
            TiledMap.cameraShake(5, 1);
        }

        spriteCounter++;
        if (spriteCounter > 9) {
            spriteCounter = 0;
            frame++;
        }
    }
}
