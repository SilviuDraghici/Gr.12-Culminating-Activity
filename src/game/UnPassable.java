/**
 *
 * @author Silviu
 */
package game;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;

public class UnPassable extends Rectangle{
    private final int bx;
    private final int by;
    private Color color = new Color(0,0,0);
    public UnPassable(int x, int y, int width, int height){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        bx = x + width - 10;
        by = y + height - 10;
    }
    public void setColor(Color color){
        this.color = color;
    }
    public void draw(Graphics2D g2, double screenx,double screeny){
        g2.setColor(color);
        Color[] fuckingColors = new Color[]{Color.blue,Color.red};
        RadialGradientPaint grad = new RadialGradientPaint((float) (x - screenx), (float) (y - screeny),500,new float[]{0.1f,0.9f},new Color[]{Color.blue,Color.red});
        g2.setPaint(grad);
        g2.fillRect((int)(x-screenx), (int)(y - screeny), width, height);
        g2.setColor(Color.LIGHT_GRAY);
        g2.fillRect((int)(x-screenx), (int)(y - screeny), 10, height);
        g2.fillRect((int)(bx - screenx), (int)(y - screeny), 10, height);
        g2.fillRect((int)(x-screenx), (int)(y - screeny), width, 10);
        g2.fillRect((int)(x-screenx), (int)(by - screeny), width, 10);
    }
    //2147483647
}
