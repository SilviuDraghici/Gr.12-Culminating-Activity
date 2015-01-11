/**
 *
 * @author Silviu
 */
package game;

import java.awt.Color;
import java.awt.Graphics2D;

public class Bullet {
    	private double incx,incy,speed=15,posx,posy,x,y;
        static int bSize=14;
        private final double sizemod = 0.182857142857;
        private double angle;
        public static final Color[] color= new Color[]{Color.ORANGE,
                                                       Color.GREEN,
                                                       Color.RED,
                                                       Color.MAGENTA,
                                                       Color.YELLOW,
                                                       Color.PINK,
                                                       Color.BLUE,
                                                       Color.CYAN};
	public Bullet(double mousex, double mousey,double playerx,double playery,double angleDif){     
		posx=playerx-bSize/2;
		posy=playery-bSize/2;
		incx=mousex-posx;
                incy=mousey-posy;
		angle=Math.atan((incy/incx))+angleDif;
                x=(Math.cos(angle))*speed;
                y=(Math.sin(angle))*speed;
                if(incx<0&&incy<=0){
                    y*=-1;
                    x*=-1;
                }
                else if(incx<0){
                    y*=-1;
                    x*=-1;
                }
	}
	public void update(){
                posx+=x;
                posy+=y;
	}
        public void draw(Graphics2D g2, double screenx,double screeny){
            g2.setColor(color[(int)(Math.random()*color.length)]);
            g2.fillOval((int)(posx-screenx),(int)(posy-screeny),bSize,bSize);
            g2.setColor(Color.BLACK);
            g2.fillOval((int)(posx-screenx+bSize*sizemod) + 1,(int)(posy-screeny+bSize*sizemod) + 1,(int)(bSize-(bSize*(sizemod*2))),(int)(bSize-(bSize*(sizemod*2))));
        }
        public void update_xy(int x, int y){this.x = x;this.y = y;}
	public double x(){return posx;}
	public double y(){return posy;}
        public double x2(){return posx+bSize;}
        public double y2(){return posy+bSize;}
        public double radius(){return bSize/2;}
}