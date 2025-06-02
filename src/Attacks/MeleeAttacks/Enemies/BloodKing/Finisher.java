/*
 * SummonerAttack.java
 * Leo Bogaert
 * May 28, 2025,
 * Extends MeleeAttack, used for the summoner attack
 */
package Attacks.MeleeAttacks.Enemies.BloodKing;

import Attacks.MeleeAttacks.MeleeAttack;
import Entitys.Enemies.BloodKing;
import Handlers.Sound.EnemySoundHandler;
import Main.Panels.GamePanel;
import Map.TiledMap;

import java.awt.*;

public class Finisher extends MeleeAttack {

    public static final int COOLDOWN = 1000;

    private final BloodKing bloodKing;

    public Finisher(BloodKing bloodKing) {
        super(6);

        this.bloodKing = bloodKing;
        GamePanel.enemyAttacks.add(this);
    }

    /**
     * Updates the attack's hitbox based on the current frame.
     */
    @Override
    public void update() {

        if (frame == 12 || (bloodKing.getState() == BloodKing.State.DEAD || bloodKing.getState() == BloodKing.State.DAMAGED)) {
            GamePanel.enemyAttacks.remove(this);
        } else if (frame == 6) {
            hitBox = new Rectangle((bloodKing.getDirection().contains("right")) ? (int) bloodKing.getPosition().x + 45 : (int) bloodKing.getPosition().x - 20, (int) (bloodKing.getPosition().y + 10), 35, 60);
            if (spriteCounter == 0) {
                EnemySoundHandler.skeletonAttack();
            }
        } else {
            hitBox = null;
        }

        if (frame == 4){
            TiledMap.cameraShake(3, 1);
        }

        spriteCounter++;
        if (spriteCounter > 9) {
            spriteCounter = 0;
            frame++;
        }
    }
}
