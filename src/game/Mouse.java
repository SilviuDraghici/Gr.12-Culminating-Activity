/**
 *
 * @author Silviu
 */
package game;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class Mouse implements MouseListener, MouseMotionListener{
    private double x,y;
    private boolean down=false;
    private boolean reset=false;
    public boolean clicked = false;
    public void mouseClicked(MouseEvent e) {
            x=e.getX();
            y=e.getY();
            clicked = true;
	}
        
	public void mouseReleased(MouseEvent e) {
            down = false;
            clicked = false;
	}
        
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	public void mousePressed(MouseEvent e){
            x=e.getX();
            y=e.getY();
            down = true;
            reset=true;
	}
        
	public void mouseDragged(MouseEvent e) {
            x=e.getX();
            y=e.getY();
	}
        
	public void mouseMoved(MouseEvent e){
            x=e.getX();
            y=e.getY();
        }
	
        public void mouseDown(MouseEvent e){}
        
        public void resetFalse(){reset=false;}
        
        public double x(){return x;}
        public double y(){return y;}
        public boolean down(){return down;}
        public boolean reset(){return reset;}
        public boolean clicked(){return clicked;}
}
