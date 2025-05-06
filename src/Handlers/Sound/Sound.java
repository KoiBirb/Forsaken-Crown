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
import java.util.Objects;

public class Sound {

    private Clip clip;

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
            System.out.println("Error with sound file.");
            return false;
        }
    }

    /**
     * Plays the current clip
     */
    public void play(){
        clip.start();
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
