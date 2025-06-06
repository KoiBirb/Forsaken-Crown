/*
 * PlayerDashHeavyAttack.java
 * Leo Bogaert
 * May 6, 2025,
 * Extends MeleeAttack, used for the player dash heavy attack
 */
package Attacks.Player;

import Attacks.MeleeAttack;
import Entitys.Player;
import Handlers.CollisionHandler;
import Handlers.Sound.SoundHandlers.PlayerSoundHandler;
import Main.Panels.GamePanel;
import Map.TiledMap;

import java.awt.*;

public class PlayerDashHeavyAttack extends MeleeAttack {

    public static final int COOLDOWN = 1000;
    private final Player player;

    /**
     * Constructor for the PlayerDashHeavyAttack class.
     * @param player The player object that is performing the attack.
     */
    public PlayerDashHeavyAttack(Player player) {
        super(3);

        this.player = player;

        player.setAttacking(true);
        GamePanel.playerAttacks.add(this);
    }

    /**
     * Updates the attack's hitbox based on the current frame.
     */
    @Override
    public void update() {

        if (frame == 5 || player.getState() == Player.PlayerState.DEAD) {
            player.setCanMove(true);
            player.setDirectionLock(false);
            GamePanel.playerAttacks.remove(this);
        } else if (frame == 0) {
            hitBox = new Rectangle( (player.getDirection().contains("right")) ? (int) player.getPosition().x + 7 : (int) player.getPosition().x + 18, (int) (player.getPosition().y + 20), 20, 35);
        } else if (frame == 1){
            hitBox = new Rectangle((player.getDirection().contains("right")) ? (int) player.getPosition().x + 20: (int) player.getPosition().x - 50, (int) (player.getPosition().y), 80, 52);
            if (spriteCounter == 2 && frame == 1 && (player.isOnGround() || CollisionHandler.checkAttackTileCollision(hitBox, player))) {
                PlayerSoundHandler.hitTile();
                PlayerSoundHandler.hitColladable();
                player.setCanMove(false);
                player.setDirectionLock(true);
            }
        } else {
            hitBox = null;
        }

        if (frame == 2 || frame == 3){
            TiledMap.cameraShake(5, 1);
        }

        spriteCounter++;
        if (spriteCounter > 5) {
            spriteCounter = 0;
            frame++;
        }
    }
}
