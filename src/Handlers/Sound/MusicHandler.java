package Handlers.Sound;

import java.util.Random;

public class MusicHandler {

    public static final Sound music = new Sound();
    public static final Sound attack = new Sound();
    public static final Sound footsteps = new Sound();
    public static final Sound effect = new Sound();
    public static final Sound falling = new Sound();
    private static final Random rand = new Random();

    private static boolean footstepsPlaying = false, fallingPlaying = false;

    public static void playBackgroundMusic() {
        playMusic(0);
    }

    public static void playMusic(int i) {
        music.setFile(i);
        music.play();
        music.loop();
    }

    public static void hit() {
        playSoundEffect(rand.nextInt(4), attack);
    }

    public static void jump() {
        playSoundEffect(5, effect);
    }

    public static void land() {
        playSoundEffect(7, effect);
    }

    public static void landHard() {
        playSoundEffect(6, effect);
    }

    public static void heavyAttack() {
        playSoundEffect(4, attack);
    }

    public static void hitTile() {
        playSoundEffect(9, attack);
    }

    public static void footsteps() {
        if (!footstepsPlaying) {
            footstepsPlaying = true;
            footsteps.setFile(8);
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

    public static void falling() {
        if (!fallingPlaying) {
            fallingPlaying = true;
            falling.setFile(10);
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

    private static void playSoundEffect(int i, Sound sound) {
        if (sound.setFile(i)) {
            sound.play();
        }
    }
}