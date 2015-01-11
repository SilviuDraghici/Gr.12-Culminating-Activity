package game;

/**
 *
 * @author Silviu
 */
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.LinkedList;
        
public class Bomb {
    private int numCircles=10;
    private LinkedList<Circle> circles = new LinkedList();
    public static Color clr;
    private int radius = 30;
    private static final int max_radius = 750;
    private boolean active = true;
    private int start_x;
    private int start_y;
    
    Bomb(int x, int y, Color clr){
        this.start_x = x;
        this.start_y = y;
        this.clr = clr;
        add_circle();
    }
    
    public void update(){
        radius += 15;
        numCircles += 5;
        if(radius <= max_radius){
            add_circle();
        }
        if(circles.size() <= 0){
            active = false;
        }
    }
    
    public void draw(Graphics2D g2,Color clr, int scrnx, int scrny){
        for(int i=0;i < circles.size(); i++){
            if (circles.get(i).is_visible()){
                circles.get(i).draw(g2, clr, scrnx, scrny);   
            }
            else{
                circles.remove(i);
                i--;
            }
        }
    }
    private void add_circle(){
        for(int i = 0; i< numCircles; i++ ){
            int angle=(int)(Math.random()*360+1);
            int temp_x=(int)((Math.cos(angle))*radius + Math.random()*10);
            int temp_y=(int)((Math.sin(angle))*radius + Math.random()*10);
            circles.add(new Circle(start_x + temp_x, start_y + temp_y));   
        }
    }
    public int x(){return start_x;}
    public int y(){return start_y;}
    public int rad(){return radius;}
    public boolean is_active(){return active;}
}