package Attacks.MeleeAttacks;

import Entitys.Player;
import Handlers.CollisionHandler;
import Handlers.Sound.MusicHandler;
import Main.Panels.GamePanel;
import Map.TiledMap;

import java.awt.*;

public class PlayerDashHeavyAttack extends MeleeAttack{

    private final Player player;
    private static final int cooldown = 300;

    public PlayerDashHeavyAttack(Player player) {
        super(5);

        this.player = player;

        player.setAttacking(true);
        GamePanel.meleeAttacks.add(this);
    }

    @Override
    public void update() {

        if (frame == 5) {
            GamePanel.meleeAttacks.remove(this);
            player.setAttacking(false);
        } else if (frame == 0) {
            hitBox = new Rectangle( (player.getDirection().equals("right")) ? (int) player.getPosition().x + 7 : (int) player.getPosition().x + 18, (int) (player.getPosition().y + 20), 20, 35);
        } else if (frame == 1 || frame == 2){
            hitBox = new Rectangle((player.getDirection().equals("right")) ? (int) player.getPosition().x + 20: (int) player.getPosition().x - 50, (int) (player.getPosition().y), 80, 52);
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

    public static int getCooldown() {
        return cooldown;
    }
}
