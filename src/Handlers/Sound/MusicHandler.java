/*
 * MusicHandler.java
 * Leo Bogaert
 * May 6, 2025,
 * Handles all sound effects and music for the game
 */

package Handlers.Sound;

import java.util.Random;

public class MusicHandler {

    public static final Sound music = new Sound();
    public static final Sound attack = new Sound();
    public static final Sound footsteps = new Sound();
    public static final Sound heal = new Sound();
    public static final Sound effect = new Sound();
    public static final Sound falling = new Sound();
    private static final Random rand = new Random();

    private static boolean footstepsPlaying, fallingPlaying, healPlaying, spawnPlaying, deathPlaying;

    /**
     * Plays random hit sound effect
     */
    public static void hit() {

        String path = "/Audio/Player/sword_" + (rand.nextInt(4) + 1) + ".wav";

        playSoundEffect(path, attack);
    }

    /**
     * Plays jump sound effect
     */
    public static void jump() {
        playSoundEffect("/Audio/Player/jump.wav", effect);
    }

    /**
     * Plays dash sound effect
     */
    public static void dash() {
        playSoundEffect("/Audio/Player/dash.wav", effect);
    }

    /**
     * Plays land sound effect
     */
    public static void land() {
        playSoundEffect("/Audio/Player/land.wav", effect);
    }

    /**
     * Plays land hard sound effect
     */
    public static void landHard() {
        playSoundEffect("/Audio/Player/land_hard.wav", effect);
    }

    /**
     * Plays heavy attack sound effect
     */
    public static void heavyAttack() {
        playSoundEffect("/Audio/Player/heavy_sword.wav", attack);
    }

    /**
     * Plays swing attack sound effect
     */
    public static void dashSwingAttack() {
        playSoundEffect("/Audio/Player/sword_wide_swing.wav", attack);
    }

    /**
     * Plays dash heavy attack sound effect
     */
    public static void dashHeavyAttack() {
        playSoundEffect("/Audio/Player/sword_dash_heavy.wav", attack);
    }

    /**
     * Plays tile hit sound effect
     */
    public static void hitTile() {
        playSoundEffect("/Audio/Player/sword_hit_tile.wav", attack);
    }

    /**
     * Plays spawn sound effect
     */
    public static void spawn(){
        if (!spawnPlaying) {
            playSoundEffect("/Audio/Player/spawn.wav", effect);
            spawnPlaying = true;
        }
    }

    /**
     * Plays sword reject sound effect
     */
    public static void hitColladable() {

        String path = "/Audio/Player/sword_hit_reject_" + (rand.nextInt(2) + 1) + ".wav";

        playSoundEffect(path, attack);
    }

    /**
     * Plays footsteps
     */
    public static void footsteps() {
        if (!footstepsPlaying) {
            footstepsPlaying = true;
            footsteps.setFile("/Audio/Player/footsteps.wav");
            footsteps.play();
            footsteps.loop();
        }
    }

    /**
     * Plays heal charge effect
     */
    public static void healCharge() {
        if (!healPlaying) {
            healPlaying = true;
            heal.setFile("/Audio/Player/heal_charge.wav");
            heal.play();
            heal.loop();
        }
    }

    /**
     * Stops heal charge effect
     */
    public static void stopHealCharge() {
        if (healPlaying) {
            healPlaying = false;
            heal.stop();
        }
    }

    /**
     * Plays heal effect
     */
    public static void heal() {
        playSoundEffect("/Audio/Player/heal.wav", heal);
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
     * Plays falling sound effect
     */
    public static void falling() {
        if (!fallingPlaying) {
            fallingPlaying = true;
            falling.setFile("/Audio/Player/falling.wav");
            falling.play();
            falling.loop();
        }
    }

    /**
     * Stops falling sound effect
     */
    public static void stopFalling() {
        if (fallingPlaying) {
            fallingPlaying = false;
            falling.stop();
        }
    }

    /**
     * Plays player damaged sound effect
     */
    public static void playerDamaged(){
        playSoundEffect("/Audio/Player/damage.wav", effect);
    }

    /**
     * Plays player death sound effect
     */
    public static void playerDeath(){
        if (!deathPlaying) {
            playSoundEffect("/Audio/Player/death.wav", effect);
            deathPlaying = true;
        }
    }

    /**
     * Plays a given sound effect on a sound object
     * @param sound Sound object used to play effect
     * @param path String file path from Assets
     */
    private static void playSoundEffect(String path, Sound sound) {
        if (sound.setFile(path)) {
            sound.play();
        }
    }

    /**
     * Sets the spawn-playing state
     * @param spawnPlaying boolean is spawn playing
     */
    public static void setSpawnPlaying (boolean spawnPlaying) {
        MusicHandler.spawnPlaying = spawnPlaying;
    }

    /**
     * Sets the death-playing state
     * @param deathPlaying boolean is death playing
     */
    public static void setDeathPlaying (boolean deathPlaying) {
        MusicHandler.deathPlaying = deathPlaying;
    }
}