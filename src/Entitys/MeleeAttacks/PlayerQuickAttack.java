package Entitys.MeleeAttacks;

import Entitys.Player;
import Main.Panels.GamePanel;

import java.awt.*;

public class PlayerQuickAttack extends MeleeAttack{

    private final Player player;
    private final boolean chain;
    private static final int cooldown = 650;

    public PlayerQuickAttack(Player player, boolean chain) {
        super(1);

        this.chain = chain;
        this.player = player;

        player.setAttacking(true);
        GamePanel.meleeAttacks.add(this);
    }

    @Override
    public void update() {
        if (chain) {
            if (frame == 6) {
                GamePanel.meleeAttacks.remove(this);
                player.setAttacking(false);

            } else if (frame == 1 || frame == 2) {
                hitBox = new Rectangle((player.getDirection().equals("right")) ? (int) (player.getPosition().x + 3) : (int) (player.getPosition().x - 95) , (int) (player.getPosition().y + 22), 137, 30);
            }
        } else if (frame == 4) {
                GamePanel.meleeAttacks.remove(this);
                player.setAttacking(false);
        } else if (frame == 1 || frame == 2) {
                hitBox = new Rectangle((player.getDirection().equals("right")) ? (int) (player.getPosition().x + 3) : (int) (player.getPosition().x - 105), (int) (player.getPosition().y + 17), 147, 30);
        } else
            hitBox = null;

        spriteCounter++;
        if (spriteCounter > 3) {
            spriteCounter = 0;
            frame++;
        }
    }

    public static int getCooldown() {
        return cooldown;
    }
}
