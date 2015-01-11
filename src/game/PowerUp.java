/**
 *
 * @author Silviu
 */
package game;

import java.awt.Color;
import java.awt.Graphics2D;
public class PowerUp {
    public static final int spreadUp=0,spsUp=1,MultiUp=3,lifeUp=4;
    private int posx,posy,length=20;
    int power;
    int degree=0;
    PowerUp(int posx, int posy){
        this.posx=posx;
        this.posy=posy;
        double chance=Math.random();
        if(chance>=0.90)
            power=spreadUp;
        else if(chance>=0.65)
            power=spsUp;
        else if(chance>=0.60)
            power=lifeUp;
        else
            power=MultiUp;
    }
    public void draw(Graphics2D g2,Color color,int screenx, int screeny){
        if(degree>360){
            degree=0;
        }
        g2.rotate(Math.toRadians(degree),posx-screenx+length/2,posy-screeny+length/2);
        g2.setColor(color);
        g2.fillRoundRect(posx-screenx,posy-screeny,length,length,15,15);
        g2.setColor(Color.BLACK);
        g2.fillRoundRect(posx-screenx+(int)(length*0.2),
                         posy-screeny+(int)(length*0.2),
                        (int)(length-(length*0.4)),
                        (int)(length-(length*0.4)),0,0);
        g2.rotate(-Math.toRadians(degree),posx-screenx+length/2,posy-screeny+length/2);
        degree+=4;
    }
    public int x(){return posx;}
    public int y(){return posy;}
    public int power(){return power;}
    public int x2(){return posx+length;}
    public int y2(){return posy+length;}
    public double radius(){return length/2;}
}