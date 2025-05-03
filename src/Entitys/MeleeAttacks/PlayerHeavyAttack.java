package Entitys.MeleeAttacks;

import Entitys.Player;
import Main.Panels.GamePanel;
import Map.TiledMap;

import java.awt.*;

public class PlayerHeavyAttack extends MeleeAttack{

    private final Player player;
    private static final int cooldown = 1350;

    public PlayerHeavyAttack(Player player) {
        super(1);

        this.player = player;

        player.setAttacking(true);
        GamePanel.meleeAttacks.add(this);
    }

    @Override
    public void update() {

        if (frame == 4) {
            GamePanel.meleeAttacks.remove(this);
            player.setAttacking(false);
        } else if (frame == 0) {
            hitBox = new Rectangle( (player.getDirection().equals("right")) ? (int) player.getPosition().x : (int) player.getPosition().x + 12, (int) (player.getPosition().y), 30, 40);
        } else if (frame == 1 || frame == 2){
            hitBox = new Rectangle((player.getDirection().equals("right")) ? (int) player.getPosition().x + 7: (int) player.getPosition().x - 90, (int) (player.getPosition().y - 10), 128, 63);
            TiledMap.cameraShake(5,1);
        } else
            hitBox = null;

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
