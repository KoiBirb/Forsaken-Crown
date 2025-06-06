/*
 * SummonerAttack.java
 * Leo Bogaert
 * May 28, 2025,
 * Extends MeleeAttack, used for the summoner attack
 */
package Attacks.Enemies;

import Attacks.MeleeAttack;
import Entitys.Enemies.Summoner.SkeletonSummoner;
import Handlers.Sound.SoundHandlers.EnemySoundHandler;
import Main.Panels.GamePanel;
import Map.TiledMap;

import java.awt.*;

public class SummonerAttack extends MeleeAttack {

    public static final int COOLDOWN = 4000;

    private final SkeletonSummoner summoner;

    public SummonerAttack(SkeletonSummoner summoner) {
        super(4);

        this.summoner = summoner;
        GamePanel.enemyAttacks.add(this);
    }

    /**
     * Updates the attack's hitbox based on the current frame.
     */
    @Override
    public void update() {

        if (frame == 18 || (summoner.getState() == SkeletonSummoner.State.DEAD || summoner.getState() == SkeletonSummoner.State.DAMAGED)) {
            GamePanel.enemyAttacks.remove(this);
        } else if (frame == 6) {
            hitBox = new Rectangle((summoner.getDirection().contains("right")) ? (int) summoner.getPosition().x + 50 : (int) summoner.getPosition().x - 115, (int) (summoner.getPosition().y - 7), 120, 73);
            if (spriteCounter == 0) {
                EnemySoundHandler.summonerAttack();
            }
        } else if (spriteCounter == 0 && frame == 7) {
            EnemySoundHandler.summonerSlam();
            hitBox = null;
        }

        if (frame == 6 || frame == 7){
            TiledMap.cameraShake(6, 1);
        }

        spriteCounter++;
        if (spriteCounter > 5) {
            spriteCounter = 0;
            frame++;
        }
    }
}
