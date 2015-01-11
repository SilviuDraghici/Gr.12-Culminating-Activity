/**
 *
 * @author Silviu
 */
package game;

import java.awt.Color;
import java.awt.Graphics2D;

public class Enemy {
    public double posx,posy,x,y,incy,incx,angle;
    public static final int eSize = 50;
    private double modify = 0.12;
    public static double speed;
    public boolean isMoving=true;
    public static final Color color=Color.RED;
    public Color clr= new Color((int)(Math.random()*256),(int)(Math.random()*256),(int)(Math.random()*256));
    private static int mod=0;
    private static boolean up=true;
    Enemy(double posx,double posy){
        this.posx=posx;
        this.posy=posy;
    }
    public void follow(double playerx,double playery){
            incx=playerx-(posx+eSize/2)+mod;
            incy=playery-(posy+eSize/2)+mod;
            angle=Math.atan((incy/incx));
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
    public void stop(){
        x=0;
        y=0;
        isMoving=false;
    }
    public void changeCourse(Enemy other){
        x=other.x;
        y=other.y;
        isMoving=false;
    }
    public void update(){
        posx+=x;
        posy+=y;
    }
     public boolean isIntersecting(Enemy other){
        double distx =(posx+eSize)-(other.posx+eSize);
        double disty =(posy+eSize)-(other.posy+eSize);
        if(Math.pow(distx,2)+Math.pow(disty,2)<=Math.pow(eSize,2)){
            return true;
        }
        else
            return false;
    }
    public double getDistance(Enemy other){
        double distx =(posx+eSize)-(other.posx+eSize);
        double disty =(posy+eSize)-(other.posy+eSize);
        return Math.sqrt(distx*distx + disty*disty);
    }
     
    public boolean isNear(Enemy other){
        double distx =posx-other.posx;
        double disty =posy-other.posy;
        if((Math.pow(distx,2)+Math.pow(disty,2))<=Math.pow(eSize+speed,2)){
            return true;
        }
        else
            return false;
    }
         
    public boolean isCloser(Enemy other){
        double dist=Math.pow(incx,2)+Math.pow(incy,2);
        double otherDist=Math.pow(other.incx,2)+Math.pow(other.incy,2);
        if(dist<otherDist)
            return true;
        else
            return false;
    }
    
    public boolean isShot(Bullet shot){
        double distx =(posx+eSize/2)-(shot.x()+shot.radius());
        double disty =(posy+eSize/2)-(shot.y()+shot.radius());
        if(Math.pow(distx,2)+Math.pow(disty,2)<Math.pow(eSize/2+shot.radius(),2)){
            return true;
        }
        else
            return false;
    }
    public boolean isBombed(Bomb bomb){
        double distx =(posx+eSize/2)-(bomb.x()+bomb.rad()/2);
        double disty =(posy+eSize/2)-(bomb.y()+bomb.rad()/2);
        if(Math.pow(distx,2)+Math.pow(disty,2)<Math.pow(eSize/2+bomb.rad(),2)){
            return true;
        }
        else
            return false;
    }
    
    public void draw(Graphics2D g2,double screenx,double screeny){				
        g2.setColor(clr);
        g2.fillOval((int)(posx-screenx),
                    (int)(posy-screeny),
                    (int)eSize,(int)eSize);
        g2.setColor(Color.BLACK);
        g2.fillOval((int)(posx-screenx+eSize*modify),(int)
                    (posy-screeny+eSize*modify),
                    (int)(eSize-(eSize*modify*2)),
                    (int)(eSize-eSize*modify*2));
    }
    public void drawDirectionVector(Graphics2D g2,double screenx,double screeny){				
        g2.setColor(Color.white);
        int x1 = (int) ((posx + eSize/2) -screenx);
        int y1 = (int) ((posy + eSize/2) -screeny);
        int x2 = (int) (x1 + x*4.125);
        int y2 = (int) (y1 + y*4.125);
        g2.drawLine(x1,y1,x2,y2);
    }
    public static void mod(){
        if(mod<=-500)
            up=true;
        else if(mod>=500)
            up=false;
        if(up==true)
            mod+=10;
        else
            mod-=10;
    }
    
    public double x(){return posx;}
    public double y(){return posy;}
    public double x2() {return (posx+eSize);}
    public double y2() {return (posy+eSize);}
}