/*
 * KeyInput.java
 * Leo Bogaert
 * May 7, 2025,
 * Detects key input
 */
package Main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyInput implements KeyListener {

    public boolean wPressed, sPressed, aPressed, dPressed, uPressed, jPressed, iPressed, kPressed, oPressed;

    /**
     * Find keys typed
     * @param e the event to be processed
     */
    @Override
    public void keyTyped(KeyEvent e) {
    }
    /**
     * Find keys pressed
     * @param e the event to be processed
     */
    @Override
    public void keyPressed(KeyEvent e) {

        int code = e.getKeyCode();

        if(code == KeyEvent.VK_W){
            wPressed = true;
        }
        if(code == KeyEvent.VK_S){
            sPressed = true;
        }
        if(code == KeyEvent.VK_A){
            aPressed = true;
        }
        if(code == KeyEvent.VK_D){
            dPressed = true;
        }
        if(code == KeyEvent.VK_U){
            uPressed = true;
        }
        if(code == KeyEvent.VK_J){
            jPressed = true;
        }
        if(code == KeyEvent.VK_I){
            iPressed = true;
        }
        if(code == KeyEvent.VK_K){
            kPressed = true;
        }
        if(code == KeyEvent.VK_O){
            oPressed = true;
        }
    }

    /**
     * Find keys released
     * @param e the event to be processed
     */
    @Override
    public void keyReleased(KeyEvent e) {

        int code = e.getKeyCode();

        if(code == KeyEvent.VK_W){
            wPressed = false;
        }
        if(code == KeyEvent.VK_S){
            sPressed = false;
        }
        if(code == KeyEvent.VK_A){
            aPressed = false;
        }
        if(code == KeyEvent.VK_D){
            dPressed = false;
        }
        if(code == KeyEvent.VK_U){
            uPressed = false;
        }
        if(code == KeyEvent.VK_J){
            jPressed = false;
        }
        if(code == KeyEvent.VK_I){
            iPressed = false;
        }
        if(code == KeyEvent.VK_K){
            kPressed = false;
        }
        if(code == KeyEvent.VK_O){
            oPressed = false;
        }
    }
}
