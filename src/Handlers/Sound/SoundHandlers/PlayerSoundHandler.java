/*
 * PlayerSoundHandler.java
 * Leo Bogaert
 * Jun 7, 2025,
 * Handles player sound effects
 */

package Handlers.Sound.SoundHandlers;

import Handlers.Sound.Sound;
import java.util.Random;

public class PlayerSoundHandler {

    private static final Random rand = new Random();

    public static final Sound[] swordHits = {
        new Sound(), new Sound(), new Sound(), new Sound()
    };
    public static final Sound[] jumps = {
        new Sound(), new Sound(), new Sound()
    };

    public static final Sound[] hitCollidable = {
            new Sound(), new Sound()
    };

    public static final Sound[] heal = {
            new Sound(), new Sound(), new Sound()
    };

    public static final Sound footsteps = new Sound();
    public static final Sound healCharge = new Sound();
    public static final Sound healEnd = new Sound();
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

    // Preload all sound files
    static {
        for (int i = 0; i < swordHits.length; i++) {
            swordHits[i].setFile("/Audio/Player/sword_" + (i + 1) + ".wav");
        }

        for (int i = 0; i < hitCollidable.length; i++) {
            hitCollidable[i].setFile("/Audio/Player/sword_hit_reject_" + (i + 1) + ".wav");
        }

        for (int i = 0; i < heal.length; i++) {
            heal[i].setFile("/Audio/Player/heal" + (i + 1) + ".wav");
        }

        for (int i = 0; i < jumps.length; i++) {
            jumps[i].setFile("/Audio/Player/jump_" + (i + 1) + ".wav");
        }

        footsteps.setFile("/Audio/Player/footsteps.wav");
        healCharge.setFile("/Audio/Player/heal_charge.wav");
        healEnd.setFile("/Audio/Player/endHeal.wav");
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

    /**
     * Plays hit sound
     */
    public static void hit() {
        swordHits[rand.nextInt(swordHits.length)].play();
    }

    /**
     * Plays heal sound
     */
    public static void heal() {
        heal[rand.nextInt(heal.length)].play();
    }

    /**
     * Plays hit wall sound
     */
    public static void hitColladable() {
        hitCollidable[rand.nextInt(hitCollidable.length)].play();
    }

    /**
     * Plays checkpoint activation sound
     */
    public static void checkpoint() {
        checkPoint.play();
    }

    /**
     * Plays jump sound
     */
    public static void jump() {
        jumps[rand.nextInt(jumps.length)].play();
    }

    /**
     * Plays footsteps
     */
    public static void footsteps() {
        if (!footstepsPlaying) {
            footstepsPlaying = true;
            footsteps.play();
            footsteps.loop();
        }
    }

    /**
     * Stops footsteps
     */
    public static void stopFootsteps() {
        if (footstepsPlaying) {
            footstepsPlaying = false;
            footsteps.stop();
        }
    }

    /**
     * Plays heal sound
     */
    public static void healCharge() {
        if (!healPlaying) {
            healPlaying = true;
            healCharge.play();
            healCharge.loop();
        }
    }

    /**
     * Stops heal sound
     */
    public static void stopHealCharge() {
        if (healPlaying) {
            healPlaying = false;
            healCharge.stop();
        }
    }

    /**
     * Plays final heal sound
     */
    public static void healEnd() {
        healEnd.play();
    }

    /**
     * Plays UI Hover sound
     */
    public static void UIHover() {
        UIHover.play();
    }

    /**
     * Plays UI confirm sound
     */
    public static void UIConfirm() {
        UIConfirm.play();
    }

    /**
     * Plays Tile hit sound
     */
    public static void hitTile() {
        hitTile.play();
    }

    /**
     * Plays dash quick attack sound
     */
    public static void dashSwingAttack() {
        dashSwingAttack.play();
    }

    /**
     * Plays dash sound
     */
    public static void dash() {
        dash.play();
    }

    /**
     * Plays dash heavy attack sound
     */
    public static void dashHeavyAttack() {
        dashHeavyAttack.play();
    }

    /**
     * Plays landing hard sound
     */
    public static void landHard() {
        landHard.play();
    }

    /**
     * Plays land sound
     */
    public static void land() {
        land.play();
    }

    /**
     * Plays heavy attack sound
     */
    public static void heavyAttack() {
        heavyAttack.play();
    }

    /**
     * Plays damaged sound
     */
    public static void playerDamaged() {
        playerDamaged.play();
    }

    /**
     * Plays falling sound
     */
    public static void falling() {
        if (!fallingPlaying) {
            fallingPlaying = true;
            falling.play();
            falling.loop();
        }
    }

    /**
     * Stops falling sound
     */
    public static void stopFalling() {
        if (fallingPlaying) {
            fallingPlaying = false;
            falling.stop();
        }
    }

    /**
     * Plays spawn sound
     */
    public static void spawn() {
        if (!spawnPlaying) {
            spawnPlaying = true;
            spawn.play();
        }
    }

    /**
     * Plays Death sound
     */
    public static void death() {
        if (!deathPlaying) {
            deathPlaying = true;
            death.play();
        }
    }

    /**
     * Sets death playing state
     * @param playing boolean true if death sound is playing, false otherwise
     */
    public static void setDeathPlaying(boolean playing) {
        deathPlaying = playing;
    }

    /**
     * Sets spawn playing state
     * @param spawning boolean true if spawn sound is playing, false otherwise
     */
    public static void setSpawnPlaying(boolean spawning) {
        spawnPlaying = spawning;
    }
}