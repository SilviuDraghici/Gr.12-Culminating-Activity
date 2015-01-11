/**
 *
 * @author Silviu
 */
package game;

import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.KeyListener;
import javax.swing.JFrame;
import javax.swing.JMenuBar;

public class Main {
    static Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
    static Screen screen;
    static Keypad keys = new Keypad();
    static Mouse mouse = new Mouse();
    static GraphicsDevice myDevice;
    static JFrame frame = new JFrame();
    public static void main(String[] args) {
        GraphicsEnvironment env = 
                GraphicsEnvironment.getLocalGraphicsEnvironment();
        myDevice = env.getDefaultScreenDevice();
        frame.setSize((int)dim.getWidth(),(int)dim.getHeight());
        screen = new Screen((int)dim.getWidth(),(int)dim.getHeight());
        
        frame.setContentPane(screen);
        frame.addKeyListener((KeyListener) keys);
        frame.addMouseListener(mouse);
        frame.addMouseMotionListener(mouse);
        screen.addKeys(keys);
        screen.addMouse(mouse);
        if(myDevice.isFullScreenSupported()){
            frame.setResizable(false);
            frame.setUndecorated(true);
            myDevice.setFullScreenWindow(frame);
        }
        else{
            frame.setResizable(true);
            frame.setUndecorated(false);
            frame.setVisible(true);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }
    }
}