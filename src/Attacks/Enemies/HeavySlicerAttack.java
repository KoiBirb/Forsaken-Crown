/*
 * HeavySlicerAttack.java
 * Leo Bogaert
 * Jun 7, 2025,
 * Extends MeleeAttack, used for the Heavy Slicer attack
 */

package Attacks.Enemies;

import Attacks.MeleeAttack;
import Entitys.Enemies.HeavySlicer;
import Handlers.Sound.SoundHandlers.EnemySoundHandler;
import Main.Panels.GamePanel;
import Main.UI.VFX.SlicerSlice;

import java.awt.*;

public class HeavySlicerAttack extends MeleeAttack {

    public static final int COOLDOWN = 2500;
    private final HeavySlicer slicer;

    /**
     * Constructor for the HeavySlicerAttack class.
     * @param slicer The HeavySlicer object that is performing the attack.
     */
    public HeavySlicerAttack(HeavySlicer slicer) {
        super(4);

        this.slicer = slicer;
        GamePanel.enemyAttacks.add(this);
    }

    /**
     * Updates the attack's hitbox based on the current frame.
     */
    @Override
    public void update() {

        if (frame == 8 || slicer.getState() != HeavySlicer.State.ATTACKING) {
            GamePanel.enemyAttacks.remove(this);
        } else if (frame == 5) {
            hitBox = new Rectangle((slicer.getDirection().contains("right")) ? (int) slicer.getPosition().x - 10 : (int) slicer.getPosition().x - 35, (int) (slicer.getPosition().y - 10), 100, 80);
            if (spriteCounter == 0) {
                EnemySoundHandler.slicerSwing();
                new SlicerSlice((int) slicer.getPosition().x, (int) slicer.getPosition().y, (slicer.getDirection().equals("left")));
            }
        } else {
            hitBox = null;
        }

        spriteCounter++;
        if (spriteCounter > 11) {
            spriteCounter = 0;
            frame++;
        }
    }
}
