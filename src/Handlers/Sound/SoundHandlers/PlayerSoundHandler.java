
package Handlers.Sound.SoundHandlers;

import Handlers.Sound.Sound;
import java.util.Random;

public class PlayerSoundHandler {

    private static final Random rand = new Random();

    // One instance per unique sound
    public static final Sound[] swordHits = {
        new Sound(), new Sound(), new Sound(), new Sound()
    };
    public static final Sound[] jumps = {
        new Sound(), new Sound(), new Sound()
    };

    public static final Sound[] hitCollidable = {
            new Sound(), new Sound()
    };

    public static final Sound footsteps = new Sound();
    public static final Sound healCharge = new Sound();
    public static final Sound heal = new Sound();
    public static final Sound dashSwingAttack = new Sound();
    public static final Sound dashHeavyAttack = new Sound();
    public static final Sound heavyAttack = new Sound();
    public static final Sound playerDamaged = new Sound();
    public static final Sound falling = new Sound();
    public static final Sound spawn = new Sound();
    public static final Sound death = new Sound();
    public static final Sound land = new Sound();
    public static final Sound landHard = new Sound();
    public static final Sound dash = new Sound();
    public static final Sound hitTile = new Sound();
    public static final Sound checkPoint = new Sound();

    public static final Sound UIHover = new Sound();
    public static final Sound UIConfirm = new Sound();

    private static boolean footstepsPlaying, fallingPlaying, healPlaying, spawnPlaying, deathPlaying;

    static {
        // Preload and cache clips
        for (int i = 0; i < swordHits.length; i++) {
            swordHits[i].setFile("/Audio/Player/sword_" + (i + 1) + ".wav");
        }

        for (int i = 0; i < hitCollidable.length; i++) {
            hitCollidable[i].setFile("/Audio/Player/sword_hit_reject_" + (i + 1) + ".wav");
        }

        for (int i = 0; i < jumps.length; i++) {
            jumps[i].setFile("/Audio/Player/jump_" + (i + 1) + ".wav");
        }

        footsteps.setFile("/Audio/Player/footsteps.wav");
        healCharge.setFile("/Audio/Player/heal_charge.wav");
        heal.setFile("/Audio/Player/heal.wav");
        dashSwingAttack.setFile("/Audio/Player/sword_wide_swing.wav");
        dashHeavyAttack.setFile("/Audio/Player/sword_dash_heavy.wav");
        heavyAttack.setFile("/Audio/Player/heavy_sword.wav");
        playerDamaged.setFile("/Audio/Player/damage.wav");
        falling.setFile("/Audio/Player/falling.wav");
        spawn.setFile("/Audio/Player/spawn.wav");
        death.setFile("/Audio/Player/death.wav");
        land.setFile("/Audio/Player/land.wav");
        landHard.setFile("/Audio/Player/land_hard.wav");
        dash.setFile("/Audio/Player/dash.wav");
        hitTile.setFile("/Audio/Player/sword_hit_tile.wav");
        UIHover.setFile("/Audio/UI/ui_change_selection.wav");
        UIConfirm.setFile("/Audio/UI/ui_button_confirm.wav");
        checkPoint.setFile("/Audio/Checkpoints/Startup.wav");
    }

    public static void hit() {
        swordHits[rand.nextInt(swordHits.length)].play();
    }

    public static void hitColladable() {
        hitCollidable[rand.nextInt(hitCollidable.length)].play();
    }

    public static void checkpoint() {
        checkPoint.play();
    }

    public static void jump() {
        jumps[rand.nextInt(jumps.length)].play();
    }

    public static void footsteps() {
        if (!footstepsPlaying) {
            footstepsPlaying = true;
            footsteps.play();
            footsteps.loop();
        }
    }

    public static void stopFootsteps() {
        if (footstepsPlaying) {
            footstepsPlaying = false;
            footsteps.stop();
        }
    }

    public static void healCharge() {
        if (!healPlaying) {
            healPlaying = true;
            healCharge.play();
            healCharge.loop();
        }
    }

    public static void stopHealCharge() {
        if (healPlaying) {
            healPlaying = false;
            healCharge.stop();
        }
    }

    public static void heal() {
        heal.play();
    }

    public static void UIHover() {
        UIHover.play();
    }

    public static void UIConfirm() {
        UIConfirm.play();
    }

    public static void hitTile() {
        hitTile.play();
    }

    public static void dashSwingAttack() {
        dashSwingAttack.play();
    }

    public static void dash() {
        dash.play();
    }

    public static void dashHeavyAttack() {
        dashHeavyAttack.play();
    }

    public static void landHard() {
        landHard.play();
    }

    public static void land() {
        land.play();
    }

    public static void heavyAttack() {
        heavyAttack.play();
    }

    public static void playerDamaged() {
        playerDamaged.play();
    }

    public static void falling() {
        if (!fallingPlaying) {
            fallingPlaying = true;
            falling.play();
            falling.loop();
        }
    }

    public static void stopFalling() {
        if (fallingPlaying) {
            fallingPlaying = false;
            falling.stop();
        }
    }

    public static void spawn() {
        if (!spawnPlaying) {
            spawnPlaying = true;
            spawn.play();
        }
    }

    public static void stopSpawn() {
        if (spawnPlaying) {
            spawnPlaying = false;
            spawn.stop();
        }
    }

    public static void death() {
        if (!deathPlaying) {
            deathPlaying = true;
            death.play();
        }
    }

    public static void stopDeath() {
        if (deathPlaying) {
            deathPlaying = false;
            death.stop();
        }
    }

    public static void setDeathPlaying(boolean playing) {
        deathPlaying = playing;
    }

    public static void setSpawnPlaying(boolean spawning) {
        spawnPlaying = spawning;
    }
}