package Attacks.MeleeAttacks.Enemies;

import Attacks.MeleeAttacks.MeleeAttack;
import Entitys.Enemies.Ghoul;
import Main.Panels.GamePanel;

import java.awt.*;

public class GhoulAttack extends MeleeAttack {

    public static final int COOLDOWN = 3500;

    private final Ghoul ghoul;

    public GhoulAttack(Ghoul ghoul) {
        super(1);

        this.ghoul = ghoul;
        GamePanel.enemyAttacks.add(this);
    }

    /**
     * Updates the attack's hitbox based on the current frame.
     */
    @Override
    public void update() {

        if (frame == 7) {
            GamePanel.enemyAttacks.remove(this);
        } else if (frame == 2 && ghoul.getState() != Ghoul.State.DEAD && ghoul.getState() != Ghoul.State.DAMAGED) {
            hitBox = new Rectangle((ghoul.getDirection().contains("right")) ? (int) ghoul.getPosition().x - 4 : (int) ghoul.getPosition().x - 50, (int) (ghoul.getPosition().y - 7), 80, 45);
        } else {
            hitBox = null;
        }

        spriteCounter++;
        if (spriteCounter > 12) {
            spriteCounter = 0;
            frame++;
        }
    }
}
