package Attacks.MeleeAttacks.Enemies;

import Attacks.MeleeAttacks.MeleeAttack;
import Entitys.Enemies.Ghoul;
import Entitys.Enemies.Summoner.SkeletonSummoner;
import Handlers.Sound.EnemySoundHandler;
import Main.Panels.GamePanel;

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

        spriteCounter++;
        if (spriteCounter > 5) {
            spriteCounter = 0;
            frame++;
        }
    }
}
