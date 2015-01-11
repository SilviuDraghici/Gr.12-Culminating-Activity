/**
 *
 * @author Silviu
 */
package game;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Keypad implements KeyListener{
    private volatile boolean keyDown=false, keyUp=false, keyLeft=false,
                             keyRight=false, escKey=false,V = false,
                             C = false,B = false;

    public void keyPressed(KeyEvent e) {
        int key1 = e.getKeyCode();

        if (key1 == KeyEvent.VK_W){
            keyUp = true;
        }

        if (key1 == KeyEvent.VK_S){
            keyDown = true;
        }

        if (key1 == KeyEvent.VK_A){
            keyLeft = true;
        }

        if (key1 == KeyEvent.VK_D){
            keyRight = true;
        }

        if(key1 == KeyEvent.VK_ESCAPE){
            escKey = true;
        }
        if(key1 == KeyEvent.VK_V){
            V = true;
        }
        if(key1 == KeyEvent.VK_C){
            C = true;
        }
        if(key1 == KeyEvent.VK_B){
            B = true;
        }
    }

    public void keyReleased(KeyEvent e) {
        int key1 = e.getKeyCode();

        if (key1 == KeyEvent.VK_W){
            keyUp = false;
        }
        if (key1 == KeyEvent.VK_S){
            keyDown = false;
        }
        if (key1 == KeyEvent.VK_A){
            keyLeft = false;
        }
        if (key1 == KeyEvent.VK_D){
            keyRight = false;
        }
        if (key1 == KeyEvent.VK_ESCAPE){
            escKey = false;
        }
        if(key1 == KeyEvent.VK_V){
            V = false;
        }
        if(key1 == KeyEvent.VK_C){
            C = false;
        }
        if(key1 == KeyEvent.VK_B){
            B = false;
        }
    }
    public void keyTyped(KeyEvent e) {}
    
    public boolean keyUp(){return keyUp;}
    public boolean keyDown(){return keyDown;}
    public boolean keyLeft(){return keyLeft;}
    public boolean keyRight(){return keyRight;}
    public boolean escKey(){return escKey;}
    public boolean V(){return V;}
    public boolean B(){return B;}
    public boolean C(){return C;}
    
}