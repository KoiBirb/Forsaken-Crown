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

    private static boolean footstepsPlaying, fallingPlaying, healPlaying;

    public static void hit() {

        String path = "/Audio/Player/sword_" + (rand.nextInt(4) + 1) + ".wav";

        playSoundEffect(path, attack);
    }

    public static void jump() {
        playSoundEffect("/Audio/Player/jump.wav", effect);
    }

    public static void dash() {
        playSoundEffect("/Audio/Player/dash.wav", effect);
    }

        public static void land() {
        playSoundEffect("/Audio/Player/land.wav", effect);
    }

    public static void landHard() {
        playSoundEffect("/Audio/Player/land_hard.wav", effect);
    }

    public static void heavyAttack() {
        playSoundEffect("/Audio/Player/heavy_sword.wav", attack);
    }

    public static void dashSwingAttack() {
        playSoundEffect("/Audio/Player/sword_wide_swing.wav", attack);
    }

    public static void dashHeavyAttack() {
        playSoundEffect("/Audio/Player/sword_dash_heavy.wav", attack);
    }

    public static void hitTile() {
        playSoundEffect("/Audio/Player/sword_hit_tile.wav", attack);
    }

    public static void hitColladable() {

        String path = "/Audio/Player/sword_hit_reject_" + (rand.nextInt(2) + 1) + ".wav";

        playSoundEffect(path, attack);
    }

    public static void footsteps() {
        if (!footstepsPlaying) {
            footstepsPlaying = true;
            footsteps.setFile("/Audio/Player/footsteps.wav");
            footsteps.play();
            footsteps.loop();
        }
    }

    public static void healCharge() {
        if (!healPlaying) {
            healPlaying = true;
            heal.setFile("/Audio/Player/heal_charge.wav");
            heal.play();
            heal.loop();
        }
    }

    public static void stopHealCharge() {
        if (healPlaying) {
            healPlaying = false;
            heal.stop();
        }
    }

    public static void heal() {
        playSoundEffect("/Audio/Player/heal.wav", heal);
    }

    public static void stopFootsteps() {
        if (footstepsPlaying) {
            footstepsPlaying = false;
            footsteps.stop();
        }
    }

    public static void falling() {
        if (!fallingPlaying) {
            fallingPlaying = true;
            falling.setFile("/Audio/Player/falling.wav");
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

    private static void playSoundEffect(String path, Sound sound) {
        if (sound.setFile(path)) {
            sound.play();
        }
    }
}