/*
 * SlicerAttack.java
 * Leo Bogaert
 * Jun 7, 2025,
 * Extends MeleeAttack, used for the slicer attack
 */

package Attacks.Enemies;

import Attacks.MeleeAttack;
import Entitys.Enemies.SlicerBot;
import Handlers.Sound.SoundHandlers.EnemySoundHandler;
import Main.Panels.GamePanel;

import java.awt.*;

public class SlicerAttack extends MeleeAttack {

    public static final int COOLDOWN = 1500;
    private final SlicerBot bot;

    /**
     * Constructor for the SlicerAttack class.
     * @param bot The SlicerBot object that is performing the attack.
     */
    public SlicerAttack(SlicerBot bot) {
        super(4);

        this.bot = bot;
        GamePanel.enemyAttacks.add(this);
        EnemySoundHandler.botStab();
    }

    /**
     * Updates the attack's hitbox based on the current frame.
     */
    @Override
    public void update() {

        if (frame == 2) {
            GamePanel.enemyAttacks.remove(this);
        } else if (frame == 1 && bot.getState() != SlicerBot.State.DEAD) {
            hitBox = new Rectangle((int) bot.getPosition().x - 4, (int) (bot.getPosition().y - 7), 25, 45);
        } else {
            hitBox = null;
        }

        spriteCounter++;
        if (spriteCounter > 6) {
            spriteCounter = 0;
            frame++;
        }
    }
}
