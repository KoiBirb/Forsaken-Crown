/*
 * SummonerAttack.java
 * Leo Bogaert
 * May 28, 2025,
 * Extends MeleeAttack, used for the summoner attack
 */
package Attacks.MeleeAttacks.Enemies;

import Attacks.MeleeAttacks.MeleeAttack;
import Entitys.Enemies.Summoner.Skeleton;
import Handlers.Sound.EnemySoundHandler;
import Main.Panels.GamePanel;

import java.awt.*;

public class SkeletonAttack extends MeleeAttack {

    public static final int COOLDOWN = 750;

    private final Skeleton skeleton;

    public SkeletonAttack(Skeleton skeleton) {
        super(1);

        this.skeleton = skeleton;
        GamePanel.enemyAttacks.add(this);
    }

    /**
     * Updates the attack's hitbox based on the current frame.
     */
    @Override
    public void update() {

        if (frame == 8 || (skeleton.getState() == Skeleton.State.DEAD || skeleton.getState() == Skeleton.State.DAMAGED)) {
            GamePanel.enemyAttacks.remove(this);
        } else if (frame == 5) {
            hitBox = new Rectangle((skeleton.getDirection().contains("right")) ? (int) skeleton.getPosition().x + 15 : (int) skeleton.getPosition().x - 18, (int) (skeleton.getPosition().y - 7), 30, 40);
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
