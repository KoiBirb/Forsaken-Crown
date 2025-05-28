package Attacks.MeleeAttacks.Player;

import Attacks.MeleeAttacks.MeleeAttack;
import Entitys.Player;
import Handlers.Sound.MusicHandler;
import Main.Panels.GamePanel;
import Map.TiledMap;

import java.awt.*;

import static Handlers.CollisionHandler.checkAttackTileCollision;

public class PlayerDashSwingAttack extends MeleeAttack {

    public static final int COOLDOWN = 1000;
    private final Player player;

    /**
     * Constructor for the Dash Swing Attack
     * @param player The player object that is performing the attack.
     */
    public PlayerDashSwingAttack(Player player) {
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

        if (frame == 5 || (player.getState() == Player.PlayerState.DEAD || player.getState() == Player.PlayerState.HIT)) {
            GamePanel.playerAttacks.remove(this);
        } else if (frame == 0){
            hitBox = new Rectangle((player.getDirection().contains("right")) ? (int) (player.getPosition().x - 36) : (int) (player.getPosition().x - 105) , (int) (player.getPosition().y + 20), 187, 31);
            if (spriteCounter == 1 && frame == 1 && checkAttackTileCollision(hitBox, player)) {
                TiledMap.cameraShake(5, 6);
                MusicHandler.hitColladable();
            } else {
                TiledMap.cameraShake(2,6);
            }
        } else {
            hitBox = null;
        }

        spriteCounter++;
        if (spriteCounter > 5) {
            spriteCounter = 0;
            frame++;
        }
    }
}
