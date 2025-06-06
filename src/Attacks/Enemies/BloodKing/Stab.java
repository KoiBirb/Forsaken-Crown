/*
 * SummonerAttack.java
 * Leo Bogaert
 * May 28, 2025,
 * Extends MeleeAttack, used for the summoner attack
 */
package Attacks.Enemies.BloodKing;

import Attacks.MeleeAttack;
import Entitys.Enemies.BloodKing;
import Handlers.Sound.SoundHandlers.EnemySoundHandler;
import Main.Panels.GamePanel;
import Map.TiledMap;

import java.awt.*;

public class Stab extends MeleeAttack {

    public static final int COOLDOWN = 5000;

    private final BloodKing bloodKing;

    public Stab(BloodKing bloodKing) {
        super(3);

        this.bloodKing = bloodKing;
        EnemySoundHandler.stabWarn();
        GamePanel.enemyAttacks.add(this);
    }

    /**
     * Updates the attack's hitbox based on the current frame.
     */
    @Override
    public void update() {

        if (frame == 12 || (bloodKing.getState() == BloodKing.State.DEAD || bloodKing.getState() == BloodKing.State.DAMAGED)) {
            GamePanel.enemyAttacks.remove(this);
        } else if (frame == 5) {
            hitBox = new Rectangle((bloodKing.getDirection().contains("right")) ? (int) bloodKing.getPosition().x - 140 : (int) bloodKing.getPosition().x - 50, (int) (bloodKing.getPosition().y + 35), 250, 30);
        } else if (frame == 9) {
            hitBox = new Rectangle((bloodKing.getDirection().contains("right")) ? (int) bloodKing.getPosition().x - 42 : (int) bloodKing.getPosition().x, (int) (bloodKing.getPosition().y + 20), 95, 50);
        } else {
            hitBox = null;
        }

        if (frame == 5 || frame == 10){
            TiledMap.cameraShake(4, 1);
        }

        if (spriteCounter == 0){
            if (frame == 5) {
                EnemySoundHandler.stabStab();
            } else if (frame == 9) {
                EnemySoundHandler.stabSlice();
            }
        }

        spriteCounter++;
        if (spriteCounter > 9) {
            spriteCounter = 0;
            frame++;
        }
    }
}
