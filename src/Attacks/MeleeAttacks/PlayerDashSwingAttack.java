package Attacks.MeleeAttacks;

import Entitys.Player;
import Handlers.CollisionHandler;
import Handlers.Sound.MusicHandler;
import Main.Panels.GamePanel;
import Map.TiledMap;

import java.awt.*;

import static Handlers.CollisionHandler.checkAttackTileCollision;
import static Handlers.Sound.MusicHandler.hitColladable;

public class PlayerDashSwingAttack extends MeleeAttack{

    private final Player player;
    private static final int cooldown = 1000;

    public PlayerDashSwingAttack(Player player) {
        super(6);

        this.player = player;

        player.setAttacking(true);
        GamePanel.meleeAttacks.add(this);
    }

    @Override
    public void update() {

        if (frame == 5) {
            GamePanel.meleeAttacks.remove(this);
            player.setAttacking(false);
        } else if (frame == 0 || frame == 1){
            hitBox = new Rectangle((player.getDirection().equals("right")) ? (int) (player.getPosition().x - 36) : (int) (player.getPosition().x - 105) , (int) (player.getPosition().y + 21), 187, 31);
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

    public static int getCooldown() {
        return cooldown;
    }
}
