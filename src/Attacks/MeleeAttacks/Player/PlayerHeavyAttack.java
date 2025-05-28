package Attacks.MeleeAttacks.Player;

import Attacks.MeleeAttacks.MeleeAttack;
import Entitys.Player;
import Handlers.CollisionHandler;
import Handlers.Sound.MusicHandler;
import Main.Panels.GamePanel;
import Map.TiledMap;

import java.awt.*;

public class PlayerHeavyAttack extends MeleeAttack {

    public static final int COOLDOWN = 1350;
    private final Player player;

    /**
     * Constructor for the PlayerHeavyAttack class.
     * @param player The player object that is performing the attack.
     */
    public PlayerHeavyAttack(Player player) {
        super(6);

        this.player = player;

        player.setAttacking(true);
        GamePanel.playerAttacks.add(this);
    }

    /**
     * Updates the attack's hitbox based on the current frame.
     */
    @Override
    public void update() {

        if (frame == 4 || (player.getState() == Player.PlayerState.DEAD || player.getState() == Player.PlayerState.HIT)) {
            GamePanel.playerAttacks.remove(this);
        } else if (frame == 0) {
            hitBox = new Rectangle( (player.getDirection().contains("right")) ? (int) player.getPosition().x : (int) player.getPosition().x + 12, (int) (player.getPosition().y), 30, 40);
        } else if (frame == 1){
            hitBox = new Rectangle((player.getDirection().contains("right")) ? (int) player.getPosition().x + 7: (int) player.getPosition().x - 90, (int) (player.getPosition().y - 10), 128, 63);
            if (spriteCounter == 2 && frame == 1 && (player.isOnGround() || CollisionHandler.checkAttackTileCollision(hitBox, player))) {
                MusicHandler.hitTile();
                MusicHandler.hitColladable();
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
