package Handlers.Sound;

public class EnemySoundHandler {


    public final Sound footsteps = new Sound();
    public final Sound effect = new Sound();


    private boolean footstepsPlaying;

    public void ghoulDeath() {
        playSoundEffect("/Audio/Enemy/Ghoul/Ghoul_Death.wav", effect);
    }
    public void ghoulAttack() {
        playSoundEffect("/Audio/Enemy/Ghoul/Ghoul_Attack.wav", effect);
    }

    public void stopGhoulAttack() {
        if (effect.clip != null) {
            effect.stop();
        }
    }
    public void ghoulFootsteps() {
        if (!footstepsPlaying) {
            footstepsPlaying = true;
            footsteps.setFile("/Audio/Enemy/Ghoul/Ghoul_Footsteps.wav");
            footsteps.play();
            footsteps.loop();
        }
    }
    public void stopGhoulFootsteps() {
        if (footstepsPlaying) {
            footstepsPlaying = false;
            if (footsteps.clip != null) {
                footsteps.stop();
            }
        }
    }

    public void ghoulHit() {
        playSoundEffect("/Audio/Enemy/Ghoul/Ghoul_Hit.wav", effect);
    }

    /**
     * Plays a given sound effect on a sound object
     * @param sound Sound object used to play effect
     * @param path String file path from Assets
     */
    private void playSoundEffect(String path, Sound sound) {
        if (sound.setFile(path)) {
            sound.play();
        }
    }
}
