/**
 *
 * @author Silviu
 */
package game;
    
import java.awt.Color;
import java.awt.Graphics2D;

public class Splosion {
    private final int speed=10;
    private final int numLines=25;
    private double percentclr;
    private double counter=1;
    private double[] posx= new double[numLines];
    private double[] posy= new double[numLines];
    private double[] x=new double[numLines];
    private double[] y=new double[numLines];
    private int[] size = new int[numLines / 4];
    private double fadeWhen;
    private boolean visible=true;
    private Color color,color2;
    int othercounter = 0; 
    Splosion(double posx, double posy,Color color,Color color2,int duration,double prcclr){
        percentclr=prcclr;
        fadeWhen=1-0.01*duration;
        int angle;
        this.color=color;
        this.color2=color2;
        for(int i=0;i<numLines;i++){
            if(i < numLines/4)
                this.size[i] = (int)(Math.random() * 30 + 65);
            this.posx[i]=posx;
            this.posy[i]=posy;
            angle=(int)(Math.random()*360+1);
            x[i]=(Math.cos(angle))*speed;
            y[i]=(Math.sin(angle))*speed;
        }
    }
    
    public void draw(Graphics2D g2,double screenx, double screeny){
        g2.setColor(color);
        if(counter<=fadeWhen)
            visible=false;
        counter-=0.01;
        for(int i=0;i<numLines-1;i+=2){
            if(i==(int)(numLines*percentclr)||i==(int)(numLines*percentclr)+1)
                g2.setColor(color2);
            posx[i]+=x[i]*counter;
            posy[i]+=y[i]*counter;
            posx[i+1]+=(x[(i+1)]*Math.random());
            posy[i+1]+=(y[i+1]*Math.random());
            
            g2.drawLine((int)(posx[i]-screenx),(int)(posy[i]-screeny),
                        (int)(posx[i]-screenx+x[i]*3),
                        (int)(posy[i]-screeny+y[i]*3));
            
            g2.drawLine((int)(posx[i+1]-screenx),(int)(posy[i+1]-screeny),
                        (int)(posx[i+1]-screenx+x[i+1]*2),
                        (int)(posy[i+1]-screeny+y[i+1]*2));
            if(i < numLines/4){
                size[i]-= 2;
                g2.drawOval((int)(posx[i+1]-screenx - 40),
                            (int)(posy[i+1]-screeny - 40),size[i],size[i]);
            }
        }
    }
    public boolean isVisible(){return visible;}
}