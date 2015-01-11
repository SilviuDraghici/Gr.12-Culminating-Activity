package game;

/**
 *
 * @author Silviu
 */

import java.awt.Color;
import java.awt.Graphics2D;
        
public class Circle {
    private int x;
    private int y;
    private int radius = (int)(Math.random()*13 + 10);
    private boolean visible = true;
    
    Circle(int x, int y){
        this.x = x;
        this.y = y;
    }
    
    public void draw(Graphics2D g2,Color clr, int scrnx, int scrny){
        if (radius <= 0)
            visible = false;
        g2.setColor(clr);
        radius --;
        g2.drawOval(x - scrnx , y - scrny, radius, radius);
    }
    public boolean is_visible(){return visible;}
    
}