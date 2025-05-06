package Handlers.Sound;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.util.Objects;

public class Sound {

    private Clip clip;

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
