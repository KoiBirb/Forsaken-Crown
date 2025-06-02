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

public class Slam extends MeleeAttack {

    public static final int COOLDOWN = 2000;

    private final BloodKing bloodKing;

    public Slam (BloodKing bloodKing) {
        super(4);

        this.bloodKing = bloodKing;
        GamePanel.enemyAttacks.add(this);
    }

    /**
     * Updates the attack's hitbox based on the current frame.
     */
    @Override
    public void update() {

        if (frame == 5) {
            bloodKing.canMove(false);
        }

        if (frame == 11 || (bloodKing.getState() == BloodKing.State.DEAD || bloodKing.getState() == BloodKing.State.DAMAGED)) {
            bloodKing.canMove(true);
            GamePanel.enemyAttacks.remove(this);
        }else if (frame == 5) {
            hitBox = new Rectangle((bloodKing.getDirection().contains("right")) ? (int) bloodKing.getPosition().x + 30 : (int) bloodKing.getPosition().x - 35, (int) (bloodKing.getPosition().y + 10), 65, 120);
        } else if (frame == 6) {
            hitBox = new Rectangle((bloodKing.getDirection().contains("right")) ? (int) bloodKing.getPosition().x + 30 : (int) bloodKing.getPosition().x - 35, (int) (bloodKing.getPosition().y + 10), 65, 60);
        } else {
            hitBox = null;
        }

        if (frame == 6){
            TiledMap.cameraShake(5, 1);
        }

        spriteCounter++;
        if (spriteCounter > 9) {
            spriteCounter = 0;
            frame++;
        }
    }
}
