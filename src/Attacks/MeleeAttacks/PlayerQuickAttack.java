package Attacks.MeleeAttacks;

import Entitys.Player;
import Handlers.Sound.MusicHandler;
import Main.Panels.GamePanel;
import Map.TiledMap;

import java.awt.*;

import static Handlers.CollisionHandler.checkAttackTileCollision;

public class PlayerQuickAttack extends MeleeAttack{

    private final Player player;
    private final boolean chain;

    /**
     * Constructor for the PlayerQuickAttack class.
     * @param player The player object that is performing the attack.
     * @param chain Whether the attack is a chain attack or not.
     */
    public PlayerQuickAttack(Player player, boolean chain) {
        super(1, 650);

        this.chain = chain;
        this.player = player;

        player.setAttacking(true);
        GamePanel.meleeAttacks.add(this);
    }

    /**
     * Updates the attack's hitbox based on the current frame.
     */
    @Override
    public void update() {
        if (chain) {
            if (frame == 6) {
                GamePanel.meleeAttacks.remove(this);
                player.setAttacking(false);
            } else if (frame == 1 || frame == 2) {
                hitBox = new Rectangle((player.getDirection().contains("right")) ? (int) (player.getPosition().x + 3) : (int) (player.getPosition().x - 95) , (int) (player.getPosition().y + 21), 137, 30);
                if (spriteCounter == 1 && frame == 1 && checkAttackTileCollision(hitBox, player)) {
                    TiledMap.cameraShake(4, 6);
                    MusicHandler.hitColladable();
                } else {
                    TiledMap.cameraShake(2,6);
                }
            }
        } else if (frame == 4) {
                GamePanel.meleeAttacks.remove(this);
                player.setAttacking(false);
        } else if (frame == 1 || frame == 2) {
            hitBox = new Rectangle((player.getDirection().contains("right")) ? (int) (player.getPosition().x + 3) : (int) (player.getPosition().x - 105), (int) (player.getPosition().y + 17), 147, 30);
            if (spriteCounter == 1 && frame == 1 && checkAttackTileCollision(hitBox, player)) {
                TiledMap.cameraShake(4,6);
                MusicHandler.hitColladable();
            } else {
                TiledMap.cameraShake(2,6);
            }

        } else
            hitBox = null;

        spriteCounter++;
        if (spriteCounter > 3) {
            spriteCounter = 0;
            frame++;
        }
    }
}
