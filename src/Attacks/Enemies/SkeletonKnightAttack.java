/*
 * SummonerAttack.java
 * Leo Bogaert
 * May 28, 2025,
 * Extends MeleeAttack, used for the summoner attack
 */
package Attacks.Enemies;

import Attacks.MeleeAttack;
import Entitys.Enemies.SkeletonKnight;
import Handlers.Sound.SoundHandlers.EnemySoundHandler;
import Main.Panels.GamePanel;

import java.awt.*;

public class SkeletonKnightAttack extends MeleeAttack {

    public static final int COOLDOWN = 1000;

    private final SkeletonKnight skeleton;

    public SkeletonKnightAttack(SkeletonKnight skeleton) {
        super(2);

        this.skeleton = skeleton;
        GamePanel.enemyAttacks.add(this);
    }

    /**
     * Updates the attack's hitbox based on the current frame.
     */
    @Override
    public void update() {

        if (frame == 8 || (skeleton.getState() == SkeletonKnight.State.DEAD || skeleton.getState() == SkeletonKnight.State.DAMAGED)) {
            GamePanel.enemyAttacks.remove(this);
        } else if (frame == 5) {
            hitBox = new Rectangle((skeleton.getDirection().contains("right")) ? (int) skeleton.getPosition().x + 20 : (int) skeleton.getPosition().x - 30, (int) (skeleton.getPosition().y - 7), 35, 40);
            if (spriteCounter == 0) {
                EnemySoundHandler.skeletonAttack();
            }
        } else {
            hitBox = null;
        }

        spriteCounter++;
        if (spriteCounter > 4) {
            spriteCounter = 0;
            frame++;
        }
    }
}
