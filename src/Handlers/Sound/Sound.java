package Handlers.Sound;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.net.URL;
import java.util.ArrayList;

public class Sound {

    private Clip clip;
    private final ArrayList<URL> soundURL = new ArrayList<>();

    public Sound() {

        // player sounds
        soundURL.add(getClass().getResource("/Audio/Player/sword_1.wav"));
        soundURL.add(getClass().getResource("/Audio/Player/sword_2.wav"));
        soundURL.add(getClass().getResource("/Audio/Player/sword_3.wav"));
        soundURL.add(getClass().getResource("/Audio/Player/sword_4.wav"));
        soundURL.add(getClass().getResource("/Audio/Player/heavy_sword.wav"));
        soundURL.add(getClass().getResource("/Audio/Player/jump.wav"));
        soundURL.add(getClass().getResource("/Audio/Player/land_hard.wav"));
        soundURL.add(getClass().getResource("/Audio/Player/land.wav"));
        soundURL.add(getClass().getResource("/Audio/Player/footsteps.wav"));
        soundURL.add(getClass().getResource("/Audio/Player/sword_hit_tile.wav"));
        soundURL.add(getClass().getResource("/Audio/Player/falling.wav"));
        soundURL.add(getClass().getResource("/Audio/Player/sword_hit_reject_1.wav"));
        soundURL.add(getClass().getResource("/Audio/Player/sword_hit_reject_2.wav"));
    }
    public boolean setFile(int i){
        try{
            AudioInputStream ais = AudioSystem.getAudioInputStream(soundURL.get(i));
            clip = AudioSystem.getClip();
            clip.open(ais);
            return true;
        } catch (Exception e){
            System.out.println("Error with sound file.");
            return false;
        }
    }

    public boolean isPlaying() {
        return clip != null && clip.isRunning();
    }

    public void play(){
        clip.start();
    }

    public void loop(){
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void stop(){
        clip.stop();
    }


}
