/*
 * Sound.java
 * Leo Bogaert
 * May 6, 2025,
 * Handles all sound effects and music for the game
 */
package Handlers.Sound;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.util.Objects;

public class Sound {

    Clip clip;

    private float volume = 1.0f;

    public void setVolume(float volume) {
        this.volume = volume;
        if (clip != null) {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float min = gainControl.getMinimum();
            float max = gainControl.getMaximum();
            float gain = min + (max - min) * volume; // volume: 0.0 - 1.0
            gainControl.setValue(gain);
        }
    }

    /**
     * Sets the current clip to the given path
     * @param path String path to the sound file
     * @return boolean true if the file was set successfully, false otherwise
     */
    public boolean setFile(String path){
        try{
            AudioInputStream ais = AudioSystem.getAudioInputStream(Objects.requireNonNull(getClass().getResource(path)));
            clip = AudioSystem.getClip();
            clip.open(ais);
            return true;
        } catch (Exception e){
            System.out.println("Error with sound file. " + path);
            System.out.println(e.getMessage());
            return false;
        }
    }

    public float getVolume() {
        return volume;
    }

    public boolean isPlaying() {
        return clip != null && clip.isRunning();
    }

    /**
     * Plays the current clip
     */
    public void play(){
        clip.start();
    }

    public Clip getClip() {
        return clip;
    }

    /**
     * Loops the current clip
     */
    public void loop(){
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    /**
     * Stops the current clip
     */
    public void stop(){
        clip.stop();
    }
}
