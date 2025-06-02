/*
 * SummonerAttack.java
 * Leo Bogaert
 * May 28, 2025,
 * Extends MeleeAttack, used for the summoner attack
 */
package Attacks.MeleeAttacks.Enemies.BloodKing;

import Attacks.MeleeAttacks.MeleeAttack;
import Entitys.Enemies.BloodKing;
import Main.Panels.GamePanel;
import Map.TiledMap;

import java.awt.*;

public class HeartSlam extends MeleeAttack {

    public static final int COOLDOWN = 10000;

    private final BloodKing bloodKing;

    public HeartSlam (BloodKing bloodKing) {
        super(4);
        this.bloodKing = bloodKing;
        GamePanel.enemyAttacks.add(this);
    }

    /**
     * Updates the attack's hitbox based on the current frame.
     */
    @Override
    public void update() {

        if (frame == 15) {
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

        if (frame == 10 || frame == 11){
            TiledMap.cameraShake(6, 1);
        }

        spriteCounter++;
        if (spriteCounter > 9) {
            spriteCounter = 0;
            frame++;
        }
    }
}
