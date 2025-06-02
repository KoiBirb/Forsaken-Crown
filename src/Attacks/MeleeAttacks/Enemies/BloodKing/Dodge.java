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

public class Dodge extends MeleeAttack {

    public static final int COOLDOWN = 6000;

    private final BloodKing bloodKing;

    public Dodge(BloodKing bloodKing) {
        super(2);

        this.bloodKing = bloodKing;
        GamePanel.enemyAttacks.add(this);
    }

    /**
     * Updates the attack's hitbox based on the current frame.
     */
    @Override
    public void update() {

        if (frame == 0) {
            bloodKing.setImmune(true);
        }

        if (frame == 6) {
            bloodKing.setImmune(false);
        }

        if (frame == 17 || (bloodKing.getState() == BloodKing.State.DEAD || bloodKing.getState() == BloodKing.State.DAMAGED)) {
            GamePanel.enemyAttacks.remove(this);
        } else if (frame == 8) {
            hitBox = new Rectangle((bloodKing.getDirection().contains("right")) ? (int) bloodKing.getPosition().x + 20 : (int) bloodKing.getPosition().x - 90, (int) (bloodKing.getPosition().y - 10), 120, 80);
        } else if (frame == 14) {
            hitBox = new Rectangle((bloodKing.getDirection().contains("right")) ? (int) bloodKing.getPosition().x + 20 : (int) bloodKing.getPosition().x - 40, (int) (bloodKing.getPosition().y - 10), 75, 80);
        } else {
            hitBox = null;
        }

        if (frame == 8 || frame == 13){
            TiledMap.cameraShake(4, 1);
        }

        spriteCounter++;
        if (spriteCounter > 9) {
            spriteCounter = 0;
            frame++;
        }
    }
}
