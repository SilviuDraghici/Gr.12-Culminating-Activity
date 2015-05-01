/**
 *
 * @author Silviu
 */
package game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.LinkedList;
import javax.swing.JPanel;

public class Screen extends JPanel implements Runnable{
    
    private LinkedList<Bullet> bullet = new LinkedList();
    private final LinkedList<Splosion> splosion = new LinkedList();
    private final LinkedList<Bomb> bomb = new LinkedList();
    private final LinkedList<Enemy> enemy= new LinkedList();
    private LinkedList<PowerUp> power= new LinkedList();
    private Sound bkrnd;
    
    long frameRate;
    
    private final int period = 25;
    
    // |width of player   |thickness of border,game field size
    private final int pSize=50,brdr=7,fieldx=5000,fieldy=5000,boomSize=35;
    
    //distance between grid lines
    private final int gridNum=70,gridDist=fieldx/gridNum;
    private final GridLine[] xLines=new GridLine[gridNum];
    private final GridLine[] yLines=new GridLine[gridNum];
    //player speed
    private double increment;
    private final int ogincrement = 12;
    private final Color grid = Color.GRAY;
    private final Color border = Color.LIGHT_GRAY;
    //screen location
    private int screenx,screeny;
    // how many bullets to shoot at once,shots per second,shoot counter,when to fire
    private int spread=2,sps=3,shootCount=0,shootWhen=1000/period/sps;
    //player location
    private int posx=fieldx/2-pSize/2,posy=fieldy/2-pSize/2,score=0,multiplyer=1;
    private boolean gameOver=false,alive=true,visible=true,paused=false;
    private final double modify = 0.12;
    private Graphics2D g2;
    private Image dbImage = null;
    private int width, height;
    private Thread gameThread;
    private Keypad keys;
    private Mouse mouse;
    private final int highScore=439953;
    private final String highScoreS=intToString(highScore,8);
    private String scoreS="00000000";
    private int numLives=3;
    private final UnPassable[] unp = new UnPassable[15];
    private DirectionField x;
    private Rectangle screenRect= new Rectangle(0, 0, width, height);
    private final int gSize = pSize;
    private long ticker = 0;
    
    private final boolean playMusic = false;
    

    Screen(int width, int height) {
        //plays random song
        if(playMusic){
            double music=Math.random();
            String location="/music/";
            if(music>=0.80)
                this.bkrnd = new Sound(location+"01 Kinetic Harvest.wav");
            else if(music>=0.70)
                this.bkrnd = new Sound(location+"02 Aurora.wav");
            else if(music>=0.66)
                this.bkrnd = new Sound(location+"04 The Krypton Garden.wav");
            else if(music>=0.50)
                this.bkrnd = new Sound(location+"05 Freon World.wav");
            else if(music>=0.40)
                this.bkrnd = new Sound(location+"06 The Amethyst Caverns.wav");
            else if(music>=0.25)
                this.bkrnd = new Sound(location+"08 The Argon Refinery.wav");
            else if(music>=0.15)
                this.bkrnd = new Sound(location+"09 Xenon Home World.wav");
            else if(music>=0.13)
                this.bkrnd = new Sound(location+"11 Great Grey Wolf Sif.wav");
            else
                this.bkrnd = new Sound(location+"10 End Of The World.wav");
        }
        this.width = width;
        this.height = height;
        screenx=fieldx/2-width/2;
        screeny=fieldy/2-height/2;
        this.setLayout(null);
        for(int i=0;i<xLines.length;i++){
            xLines[i]= new GridLine(gridDist*(i+1),0,gridDist*(i+1),height);
        }
        for(int i=0;i<yLines.length;i++){
            yLines[i]= new GridLine(0,gridDist*(i+1),width,gridDist*(i+1));
        }
    }
    
    @Override
    public void addNotify(){
		super.addNotify();	//creates the peer
		startGame();		//start the thread
    }	
    //initialise and start the thread
    private void startGame(){
        if(gameThread == null){
            gameThread = new Thread((Runnable) this);
            gameThread.start();
        }
    }
    
    
    public void run(){
        Enemy.speed=ogincrement;
//        enemy.add(new Enemy(randomEx(screenx,width,fieldx),randomEx(screeny,height,fieldy)));
        long beforeTime, timeDiff,sleepTime;
        beforeTime = System.currentTimeMillis();
        unp[0] = new UnPassable(100, 100, 400, 100);
        unp[1] = new UnPassable(0, 300, 400, 100);
        unp[2] = new UnPassable(500, 0, 100, 500);
        unp[3] = new UnPassable(0, 720, 1000, 10);
        unp[4] = new UnPassable(900, 500, 200, 100);
        unp[5] = new UnPassable(1020, 600, 10, 100);
        unp[6] = new UnPassable(100, 500, 700, 100);
        unp[7] = new UnPassable(1100, 100, 100, 500);
        unp[8] = new UnPassable(900, 100, 200, 100);
        unp[9] = new UnPassable(600, 100, 200, 100);
        unp[10] = new UnPassable(700, 250, 300, 100);
        unp[11] = new UnPassable(2000, 2000, 350, 350);
        x = new DirectionField(fieldx/gSize,fieldy/gSize, 
                                              gSize, width, height);
        x.add_obstacles(unp);
        while(!gameOver){
            ticker++;
            if(ticker % 10 ==1)
                toggle();
            if(numLives<0){
                gameOver=true;
            }
            if(!paused){
                if(alive&&visible){
                    gameUpdate();
                }
                else if(!alive&&visible){
                    explode();
                }
                else{
                    moveToCentre();
                }
            }
            renderGame();
            paintScreen();
            
            timeDiff = System.currentTimeMillis() - beforeTime;
            if(timeDiff !=0)
                frameRate = 1000/timeDiff;
            sleepTime = period - timeDiff;

            if(sleepTime < 0){
                sleepTime = 0;
            }

            try{
                Thread.sleep(sleepTime);
            }
            catch(InterruptedException e){}
            frameRate = 1000/(System.currentTimeMillis() - beforeTime);
            beforeTime = System.currentTimeMillis();
            if(keys.escKey()){
                System.exit(0);
                if(!paused){
                    paused=true;
                    try {Thread.sleep(200);} catch (InterruptedException ex) {}
                }
                else{
                    paused=false;
                    try {Thread.sleep(200);} catch (InterruptedException ex) {}
                }
            }
        }
        renderEnd();
        paintScreen();
        try {Thread.sleep(5000);} catch (InterruptedException ex) {}
        System.exit(0);		//makes enclosing applet/JFrame exit
    }
    
    
    private void gameUpdate(){
        posUpdate();
        bulletUpdate();
        bombUpdate();
        enemiesOnScreen();
        x.copy_obst_field(Math.round(screenx/gSize), Math.round(screeny/gSize));
        //x.updateDistField(EOnScreen, screenx, screeny);
        x.create_dist_field(posx + pSize/2,posy + pSize/2, screenx, screeny);
        x.createVectorField();
        enemyUpdate2();
        enemy_unpassable_collisions();
        resolve_enemy_collisions();
        powerUpdate();
        amIAlive();
    }
    //moves screen when appropriate
    private void posUpdate(){
        if((keys.keyDown()&&(keys.keyLeft()||keys.keyRight()))||
           (keys.keyUp()&&(keys.keyLeft()||keys.keyRight()))){
            increment = ogincrement*0.707067811865475;
        }
        else{
            increment = ogincrement;
        }
        //moves player
        if (keys.keyLeft()){
            posx -= increment;
        }
        if (keys.keyRight()){
            posx += increment;
        } 
        if (keys.keyUp()){
            posy -= increment;
        }
        if (keys.keyDown()){
            posy += increment;
        }
        //keeps player from going through obstacles
        resolve_unpassable_player_collision(posx, posy, pSize/2);
        //handles screen position
        screenx = posx - width/2;
        screeny = posy - height/2;
        if(screenx < 0)
            screenx = 0;
        else if(screenx + width > fieldx)
            screenx = fieldx - width;
        if (screeny < 0)
            screeny = 0;
        else if(screeny + height > fieldy)
            screeny = fieldy - height;
        //keeps player in bounds
        if (posx<=brdr)
        posx=brdr+1;
        else if(posx+pSize>=fieldx-brdr)
            posx=fieldx-pSize-brdr-1;
        if(posy<=brdr)
            posy=brdr+1;
        else if(posy+pSize>=fieldy-brdr)
            posy=fieldy-pSize-brdr-1;
    }
    //creates and moves bullets
    private void bulletUpdate(){
        if(mouse.reset()){
            shootCount=shootWhen;
            mouse.resetFalse();
        }
        if(mouse.down()){
            if(shootCount>=shootWhen){
                shoot();
                shootCount=0;
            }
            shootCount++;
        }
        for(int i=0;i<bullet.size();i++){
            if(bullet.get(i).x()>=brdr&&bullet.get(i).x2()<=fieldx-brdr&&bullet.get(i).y()>=brdr&&bullet.get(i).y2()<=fieldy-brdr){
                bullet.get(i).update();
                if(hitObstacle(new Rectangle((int)(bullet.get(i).x()),(int)(bullet.get(i).y()),Bullet.bSize,Bullet.bSize))){
                splosion.add(new Splosion(bullet.get(i).x(),bullet.get(i).y(),Bullet.color[(int)(Math.random()*Bullet.color.length)],Bullet.color[(int)(Math.random()*Bullet.color.length)],boomSize,0.5));
                //spawnmorefuckingbullets(bullet.get(i).x(),bullet.get(i).y());
                bullet.remove(i);
            }
            }
            else{
                splosion.add(new Splosion(bullet.get(i).x(),bullet.get(i).y(),Bullet.color[(int)(Math.random()*Bullet.color.length)],Bullet.color[(int)(Math.random()*Bullet.color.length)],boomSize,0.5));
                //spawnmorefuckingbullets(bullet.get(i).x(),bullet.get(i).y());
                bullet.remove(i);
            }
        }
    }
    private void spawnmorebullets(double x, double y){
        bullet.add(new Bullet(x + 10,y - 7,x,y,Math.toRadians(0)));
        bullet.getLast().update();
        bullet.getLast().update();
        bullet.add(new Bullet(x - 10,y - 7,x,y,Math.toRadians(0)));
        bullet.getLast().update();
        bullet.getLast().update();
        bullet.add(new Bullet(x - 7,y - 10,x,y,Math.toRadians(0)));
        bullet.getLast().update();
        bullet.getLast().update();
        bullet.add(new Bullet(x - 7,y + 7,x,y,Math.toRadians(0)));
        bullet.getLast().update();
        bullet.getLast().update();
    }
    private boolean hitObstacle(Rectangle r){
        boolean ret = false;
        for(int i = 0; i < unp.length; i++){
            if(unp[i] != null && unp[i].intersects(r))
                ret = true;
        }
        return ret;
    }
    private int[] intObstacle(Rectangle r){
        int[] ret = {0,0};
        for (UnPassable unp1 : unp) {
            if (unp1 != null && unp1.intersects(r)) {
                increment = ogincrement;
                int x11 = posx;
                int y11 = posy;
                int x12 = posx + pSize;
                int y12 = posy + pSize;
                int x21 = unp1.x;
                int y21 = unp1.y;
                int x22 = unp1.x + unp1.width;
                int y22 = unp1.y + unp1.height;
                if(y11 >= y21 && y12 <= y22){
                    if (x11 <= x22 && x12 >= x22)
                        ret[0] = x11 - x22;
                    else if(x12 >= x21)
                       ret[0] = x12 - x21;
                }
                else if(x11 >= x21 && x12 <= x22){
                    if (y11 <= y22 && y12 >= y22)
                        ret[1] = y11 - y22;
                    else if(y12 >= y21)
                        ret[1] = y12 - y21;            
                }
            }
        }
        return ret;
    }
    private void bombUpdate(){
        for(int i=0; i < bomb.size(); i++){
            if(bomb.get(i).is_active()){
                bomb.get(i).update();
            }
            else{
                bomb.remove(i);
            }
        }
    }
    private void shoot(){
//       splosion.add(new Splosion(fieldx/2,fieldy/2,new Color((int)(Math.random()*256),(int)(Math.random()*256),(int)(Math.random()*256)),new Color((int)(Math.random()*256),(int)(Math.random()*256),(int)(Math.random()*256)),45,0.8));
//       bomb.add(new Bomb((int)(fieldx/2), (int)(fieldy/2), Color.CYAN));
       double degree=3;
       if(spread%2!=0){
           bullet.add(new Bullet(mouse.x()+screenx,mouse.y()+screeny,(double)posx+pSize/2,(double)posy+pSize/2,Math.toRadians(0)));
       }
       else
           degree=1.5;
       for(int i=1;i<=(int)spread/2;i++){  
           bullet.add(new Bullet(mouse.x()+screenx,mouse.y()+screeny,
                      (double)posx+pSize/2,(double)posy+pSize/2,
                      Math.toRadians(degree)));
           bullet.add(new Bullet(mouse.x()+screenx,
                      mouse.y()+screeny,(double)posx+pSize/2,
                     (double)posy+pSize/2,Math.toRadians(-1*degree)));
           degree+=3;
       }
    }
    //variables used to seperate collision detection between 6 frames
    int counter=0;
    int num;
    int sum=0;
    int senda;
    int sendq;
    LinkedList<Enemy> EOnScreen;
    private void enemyUpdate(){
        //Enemy.mod();
        spawn();
        num = enemy.size()/6;
        if(sum<=enemy.size()){
            senda=sum;
        }
        if(sum+num<=enemy.size()){
            sendq=sum+num;
        }
        if(enemy.size()>0)
            collisions(senda,sendq);
        sum+=num;
        counter++;
        if(counter>=6){
            if(sum < enemy.size()){
                collisions(sum,enemy.size());
            }
            sum=0;
            counter=0;
        }
        for(int i = 0;i < enemy.size(); i++){
            enemy.get(i).update();
        }
        shot();
        blownUp();
    }
    private void enemyUpdate2(){
        spawn();
        for(int i = 0; i < enemy.size(); i++){
            Rectangle enmy = new Rectangle((int)enemy.get(i).posx - screenx,
                                           (int)enemy.get(i).posy - screeny,
                                           Enemy.eSize,Enemy.eSize);
            if(screenRect.contains(enmy)){
            int xcord = (int) Math.floor((enemy.get(i).posx + Enemy.eSize/2 - screenx) / gSize);
            int ycord = (int) Math.floor((enemy.get(i).posy + Enemy.eSize/2 - screeny) / gSize);
            SVector direction = x.dict_vector_field[xcord][ycord];
            enemy.get(i).x = direction.normx * Enemy.speed*0.8;
            enemy.get(i).y = direction.normy * Enemy.speed*0.8;
            }
            else
                enemy.get(i).follow(posx, posy);
            enemy.get(i).update();
        }
        shot();
        blownUp();
    }
    private void enemiesOnScreen(){
        EOnScreen = new LinkedList<Enemy>();
        screenRect = new Rectangle(0, 0, width, height);
        for(int i=0;i<enemy.size();i++){
            Rectangle enmy = new Rectangle((int)enemy.get(i).posx - screenx,
                                           (int)enemy.get(i).posy - screeny,
                                           Enemy.eSize,Enemy.eSize);
            if(screenRect.contains(enmy))
                EOnScreen.add(enemy.get(i));
        }
    }
    private void collisions(int a,int q){
        for(int i=a;i<q;i++){
            if(enemy.get(i).isMoving){
                enemy.get(i).follow(posx+pSize/2,posy+pSize/2);
            }
            for(int j=i+1;j<enemy.size();j++){
                if(j>=enemy.size())
                    j--;
                if(enemy.get(i).isIntersecting(enemy.get(j))){
                    if(enemy.get(i).isCloser(enemy.get(j))){
                        enemy.get(j).stop();
                    }
                    else{
                        enemy.get(i).stop();
                    }
                }
                else if(enemy.get(i).isNear(enemy.get(j))){
                    if(enemy.get(i).isCloser(enemy.get(j))){
                        enemy.get(j).changeCourse(enemy.get(i));
                    }
                    else{
                        enemy.get(i).changeCourse(enemy.get(j));
                    }
                }
                    
            }
            enemy.get(i).isMoving=true;
        }
    }
    //detects collisons between bullets and enemies,seperates into two frames
    boolean second=false;
    // determines how to spread out the bullets
    private void shot(){
        if(!second){
            for(int i=0;i<enemy.size()/2;i++){
                for(int j=0;j<bullet.size();j++){
                    if(enemy.get(i).isShot(bullet.get(j))){
                        splosion.add(new Splosion(enemy.get(i).x()+25,enemy.get(i).y()+25,Bullet.color[(int)(Math.random()*Bullet.color.length)],Bullet.color[(int)(Math.random()*Bullet.color.length)],boomSize,0.8));
                        if(Math.random()>=0.9&&power.size()<=20){
                            power.add(new PowerUp((int)enemy.get(i).x()+20,(int)enemy.get(i).y()+20));
                        }
                        enemy.remove(i);
                        //spawnmorefuckingbullets(bullet.get(j).x(),bullet.get(j).y());
                        bullet.remove(j);
                        spawnTick++;
                        score+=1*multiplyer;
                        scoreS=intToString(score,8);
                        i--;
                        break;
                    }
                }
            }
            second=true;
        }
        else{
            for(int i=enemy.size()/2;i<enemy.size();i++){
                a:for(int j=0;j<bullet.size();j++){
                    if(enemy.get(i).isShot(bullet.get(j))){
                        splosion.add(new Splosion(enemy.get(i).x()+25,enemy.get(i).y()+25,Bullet.color[(int)(Math.random()*Bullet.color.length)],Bullet.color[(int)(Math.random()*Bullet.color.length)],boomSize,0.8));
                        if(Math.random()>=0.9&&power.size()<=20){
                            power.add(new PowerUp((int)enemy.get(i).x()+20,(int)enemy.get(i).y()+20));
                        }
                        enemy.remove(i);
                        //spawnmorefuckingbullets(bullet.get(j).x(),bullet.get(j).y());
                        bullet.remove(j);
                        spawnTick++;
                        score+=1*multiplyer;
                        scoreS=intToString(score,8);
                        i--;
                        break a;
                    }
                }
            }
            second=false;
        }
            
    }
    private void blownUp(){
        for(int i = 0; i < bomb.size();i++){
            for(int j = 0; j < enemy.size(); j++){
                if(enemy.get(j).isBombed(bomb.get(i))){
                    splosion.add(new Splosion(enemy.get(j).x()+25,enemy.get(j).y()+25,enemy.get(j).clr,Bomb.clr,boomSize,0.8));
                    enemy.remove(j);
                }
            }
        }
    }
    int counterS=0;
    int spawnWhen=120;
    int spawnTick=20;
    int spawnNum=25;
    int spawnMax=50;
    private void spawn(){
        if(spawnTick>=30){
            spawnNum++;
            if(spawnMax<250)
                spawnMax+=5;
            spawnTick=0; 
        }
        if(counterS==spawnWhen){
            for(int i=0;i<spawnNum;i++){
                if(enemy.size()<spawnMax){
                    enemy.add(new Enemy(randomEx(screenx,width,fieldx),randomEx(screeny,height,fieldy)));
                }
                counterS=0;
            }
        }
        counterS++;
    }
    private void powerUpdate(){
        double distx;
        double disty;
        for(int i=0;i<power.size();i++){
           distx=(posx+pSize/2)-(power.get(i).x()+power.get(i).radius());
           disty=(posy+pSize/2)-(power.get(i).y()+power.get(i).radius());
           if(((distx*distx)+(disty*disty))<=Math.pow((pSize/2+power.get(i).radius()),2)){
               if(power.get(i).power()==PowerUp.spreadUp){
                   if(spread<5){
                       spread++;
                   }
                   else{
                       score+=6*multiplyer;
                       scoreS=intToString(score,8);
                   }
               }
               else if(power.get(i).power()==PowerUp.spsUp){
                   if(sps<10){
                           sps++;
                           shootWhen=1000/period/sps;
                   }
                   else{
                       score+=4*multiplyer;
                       scoreS=intToString(score,8);
                   }
               }
               else if(power.get(i).power()==PowerUp.lifeUp){
                   if(numLives<5){
                       numLives++;
                   }
                   else{
                       score+=15*multiplyer;
                       scoreS=intToString(score,8);
                   }
               }
               else if(power.get(i).power()==PowerUp.MultiUp){
                   if(multiplyer<150){
                       multiplyer++;
                   }
                   else{
                       score+=2*multiplyer;
                       scoreS=intToString(score,8);
                   }
               }
               power.remove(i);
               i--;
           }
        }
    }
    boolean second2;
    private Color temp;
    private void amIAlive(){
        if(!second2){
            for(int i=0;i<enemy.size()/2;i++){
                    double distx =(posx+pSize/2)-(enemy.get(i).x()+pSize/2);
                    double disty =(posy+pSize/2)-(enemy.get(i).y()+pSize/2);
                    if(Math.pow(distx,2)+Math.pow(disty,2)<Math.pow(pSize,2)){
                        temp=enemy.get(i).clr;
                        NO();
                    } 
            }        
            second2=true;
        }
        else{
            for(int i=enemy.size()/2;i<enemy.size();i++){
                double distx =(posx+pSize/2)-(enemy.get(i).x()+pSize/2);
                    double disty =(posy+pSize/2)-(enemy.get(i).y()+pSize/2);
                    if(Math.pow(distx,2)+Math.pow(disty,2)<Math.pow(pSize,2)){
                        temp=enemy.get(i).clr;
                        NO();
                    } 
            }
            second2=false;
        }
    }
    private void NO(){
        for(int i=enemy.size()-1;i>=0;i--){
            splosion.add(new Splosion(enemy.get(i).x()+25,enemy.get(i).y()+25,enemy.get(i).clr,enemy.get(i).clr, (int) (boomSize*(Math.random()+0.8)),0.7));
            enemy.remove(i);
        }
        splosion.add(new Splosion(posx+pSize/2,posy+pSize/2,temp,Color.CYAN,200,0.5));
        power= new LinkedList();
        bullet = new LinkedList();
        spawnMax=Math.round((float)(spawnMax*0.8));
        spread=Math.round((float)(spread*0.7));
        if(sps>3){
            spawnNum=Math.round((float)(spawnNum*0.8));
            sps=Math.round((float)(sps*0.7));
            shootWhen=1000/period/sps;
        }
        if(multiplyer>15)
            multiplyer=Math.round((float)(multiplyer*0.5));
        alive=false;
    }
    int cntr=0;
    private void explode(){
        cntr++;
        if(cntr<15)
            splosion.add(new Splosion(posx+pSize/2,posy+pSize/2,temp,Color.CYAN,200,0.5));
        else{
            visible=false;
            cntr=0;
        }
    }
    //determines how to move the screen back to the centre
    int cntrM=0;
    private void moveToCentre(){
        //delay to allow time for explosion animation to finish
        cntrM++;
        
        if(cntrM == 213){
            numLives--;
        }
        else if(cntrM>=215){
            if(screenx>fieldx/2-width/2-30&&screenx<fieldx/2-width/2)
                screenx+=fieldx/2-width/2-30-screenx;
            else if(screenx<fieldx/2-width/2)
                screenx+=30;
            else if(screenx<fieldx/2-width/2+30&&screenx>fieldx/2-width/2)
                screenx+=fieldx/2-width/2-30-screenx;
            else if(screenx>fieldx/2-width/2)
                screenx-=30;

            if(screeny>fieldy/2-height/2-30&&screeny<fieldy/2-height/2)
                screeny+=fieldy/2-height/2-30-screeny;
            else if(screeny<fieldy/2-height/2)
                screeny+=30;
            else if(screeny<fieldy/2-height/2+30&&screeny>fieldy/2-height/2)
                screeny+=fieldy/2-height/2-30-screeny;
            else if(screeny>fieldy/2-height/2)
                screeny-=30;
            if(screenx==fieldx/2-width/2&&screeny==fieldy/2-height/2){
                visible=true;
                alive=true;
                posx=fieldx/2-pSize/2;
                posy=fieldy/2-pSize/2;
                cntrM=0;
            }
        }
    }
    
    private int[] prevent_collision(int a0,int a1,int b0,int b1){
        int[] rtrn = {0,0};
        b0 = a0 + b0;
        b1 = a1 + b1;
        return rtrn;
    }
    private void collisions_line(){
        int a0 = (posx + pSize/2);
        int a1 = (posy + pSize/2);
        int b0 = (int)mouse.x() + screenx;
        int b1 = (int)mouse.y() + screeny;
        float[] length = {0,0};
        g2.setColor(Color.blue);
        g2.drawLine(a0 - screenx, a1 - screeny, b0 - screenx, b1 - screeny);
        for(UnPassable i : unp){
            if(i != null){
                length = AABBTest.lineBoxTest(i, a0, a1, b0, b1);
                
                b0 = (int) (b0 - length[0]);
                b1 = (int) (b1 - length[1]);
            }
        }
        g2.setColor(Color.white);
        g2.drawLine(a0 - screenx, a1 - screeny,
                    b0 - screenx, b1 - screeny);
    }
    private void enemy_unpassable_collisions(){
        for(int j = 0; j < enemy.size(); j++){
            int rad = Enemy.eSize/2;
            int x = (int) enemy.get(j).posx;
            int y = (int) enemy.get(j).posy;
            resolve_unpassable_collision(enemy.get(j),rad);
        }
    }
    private void resolve_unpassable_collision(Enemy en, int rad){
        if (en.posx<=brdr){
            en.posx=brdr+1;
        }
        else if(en.posx+Enemy.eSize>=fieldx-brdr)
            en.posx=fieldx-Enemy.eSize-brdr-1;
        if(en.posy<=brdr)
            en.posy=brdr+1;
        else if(en.posy+Enemy.eSize>=fieldy-brdr)
            en.posy=fieldy-Enemy.eSize-brdr-1;
        for(UnPassable i : unp){
            if(i != null){
                int px = (int) en.posx + rad;
                int py = (int) en.posy + rad;
                int psx = px;
                int psy = py;
                int a0 = i.x;
                int a1 = i.y;
                int b0 = a0 + i.width;
                int b1 = a1 + i.height;
                if(px > b0) px = b0;
                if(px < a0) px = a0;
                if(py > b1) py = b1;
                if(py < a1) py = a1;
                int distx = psx - px;
                int disty = psy - py;
                if((distx*distx + disty* disty) < rad*rad){
                    double length = Math.sqrt(distx*distx + disty* disty);
                    double ovr = rad - length;
                    double prct = ovr/length;
                    en.posx += distx * prct;
                    en.posy += disty * prct;
                }
            }
        }
    }
    private void resolve_unpassable_player_collision(int x, int y, int rad){
        for(UnPassable i : unp){
            if(i != null){
                int px = (int) x + rad;
                int py = (int) y + rad;
                int psx = px;
                int psy = py;
                int a0 = i.x;
                int a1 = i.y;
                int b0 = a0 + i.width;
                int b1 = a1 + i.height;
                if(px > b0) px = b0;
                if(px < a0) px = a0;
                if(py > b1) py = b1;
                if(py < a1) py = a1;
                int distx = psx - px;
                int disty = psy - py;
                if((distx*distx + disty* disty) < rad*rad){
                    double length = Math.sqrt(distx*distx + disty* disty);
                    double ovr = rad - length;
                    double prct = ovr/length;
                    posx += distx * prct;
                    posy += disty * prct;
                }
            }
        }
    }
    private void resolve_enemy_collisions(){
        for(int i = 0; i < enemy.size(); i++){
            for(int j = 0; j < enemy.size(); j++){
                if(i != j){
                    resolve_2circle_collision(enemy.get(i), enemy.get(j));
                }
            }
        }
    }
    private void resolve_2circle_collision(Enemy a, Enemy b){
        double dist = a.getDistance(b);
        if(dist < Enemy.eSize){
            double distx = a.posx - b.posx;
            double disty = a.posy - b.posy;
            double ovr = Enemy.eSize - dist;
            double prct = ovr/dist;
            distx = distx * (prct / 2);
            disty = disty * (prct / 2);
            a.posx += distx;
            a.posy += disty;
            b.posx -= distx;
            b.posy -= disty;
            int rad = Enemy.eSize/2;
            resolve_unpassable_collision(a, rad);
            resolve_unpassable_collision(b, rad);
        }
    }
    
    
    private void renderGame(){
        if(dbImage == null){
                dbImage = createImage(width,height);
                if(dbImage == null){
                        System.out.println("dbImage is null");
                        return;
                }
                else{
                        g2 = (Graphics2D) dbImage.getGraphics();
                }
        }

        //clears the background
        g2.setColor(new Color(0,0,0,255));
        g2.fillRect(0,0,width,height);
        Color[] clrs = new Color[]{Color.cyan,Color.black};
        RadialGradientPaint grad = new RadialGradientPaint((float) (posx - screenx + pSize/2), (float) (posy - screeny+ pSize/2),500,new float[]{0.0f,0.8f},clrs);
        g2.setPaint(grad);
        //g2.fillOval(posx - screenx + pSize/2 - 500, posy - screeny - 500 + pSize/2, 1000, 1000);
        if(distclr)
            renderDistField();
        if(!distclr)
            renderGrid();
        renderObstacles();
        if(distnums)
            renderDistFieldNums();
        if(vectfld)
            renderVectorField();
        RenderingHints rh = new RenderingHints(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHints(rh);
        renderPower();
        renderBullets();
        renderEnemies();
        //renderDirectionVectors();
        renderBomb();
        renderSplosion();
        renderBorder();
        if(visible){
            g2.setColor(Color.CYAN);
            g2.fillOval(posx-screenx,posy-screeny,pSize,pSize);
            g2.setColor(Color.BLACK);
            g2.fillOval((int)(posx-screenx+pSize*modify),(int)(posy-screeny+pSize*modify),(int)(pSize-pSize*modify*2),(int)(pSize-pSize*modify*2));
            g2.setColor(Color.CYAN);
            g2.fillRect(posx - screenx + 10, posy - screeny + 10, 30, 30);
        }
        //collisions_line();
        renderHUD();
        //g2.scale(1.001, 1.001);
    }
    //draws grid
    private void renderGrid(){
        g2.setColor(grid);
        for(int i=0;i<xLines.length;i++){
            if(xLines[i].x1>=screenx&&xLines[i].x1<=screenx+width)
                 g2.drawLine(xLines[i].x1-screenx,xLines[i].y1,xLines[i].x2-screenx,xLines[i].y2);
            if(yLines[i].y1>=screeny&&yLines[i].y1<=screeny+height)
                g2.drawLine(yLines[i].x1,yLines[i].y1-screeny,yLines[i].x2,yLines[i].y2-screeny);
        }
    }
    private void renderDistField(){
        int m = 3;
        for (int i = 0; i < x.dist_field.length; i++) {
            for (int j = 0; j < x.dist_field[0].length; j++) {
                if(m*x.dist_field[i][j] <= 255)
                    g2.setColor(new Color(0,255 - m*x.dist_field[i][j],
                                          m*x.dist_field[i][j]));
                else
                    g2.setColor(Color.black);
                g2.fillRect(i*gSize,j*gSize,gSize,gSize);
                g2.setColor(Color.white);
                g2.drawRect(i*gSize,j*gSize,gSize,gSize);
            }
            
        }
    }
    private void renderDistFieldNums(){
        g2.setColor(Color.white);
        g2.setFont(new Font("Rockwell Condensed",Font.BOLD,20));
        for (int i = 0; i < DirectionField.dist_field.length; i++) {
            for (int j = 0; j < DirectionField.dist_field[0].length; j++) {
                g2.drawString(Integer.toString(x.dist_field[i][j]),i*gSize + 2,j*gSize + 18);
            }
        }
    }
    private void renderVectorField(){
        g2.setColor(Color.black);
        for (int i = 0; i < x.dist_field.length; i++) {
            for (int j = 0; j < x.dist_field[0].length; j++) {
                SVector sv = x.dict_vector_field[i][j];
                g2.drawLine(i*gSize + gSize/2, j*gSize + gSize/2, 
                            i*gSize + gSize/2 + (int)(sv.normx*10),
                            j*gSize + gSize/2 + (int)(sv.normy*10));
            }
        }
    }
    int blue=249;
    boolean up=false;
    Color powerC;
    private void renderPower(){
        if(blue<110)
            up=true;
        else if(blue>249)
            up=false;
        if(up==false)
            blue-=2;
        else if(up=true)
            blue+=2;
        powerC = new Color(0,200,blue,255);
        for (int i=0;i<power.size();i++){
            if(power.get(i).x2()>=screenx&&power.get(i).x()<=screenx+width&&power.get(i).y2()>=screeny&&power.get(i).y()<=screeny+height){
                power.get(i).draw(g2,powerC,screenx,screeny);
            }
        }
    }
    private void renderBomb(){
        for(int i=0; i < bomb.size(); i++){
            bomb.get(i).draw(g2, Color.GREEN, screenx, screeny);
        }
    }
    //draws bullets
    private void renderBullets(){
        for(int i=0;i<bullet.size();i++){
           if(bullet.get(i).x()>=screenx&&bullet.get(i).x2()<=screenx+width&&bullet.get(i).y()>=screeny&&bullet.get(i).y2()<=screeny+height){
                bullet.get(i).draw(g2,screenx,screeny);
           }
        }
    }
    //draws border
    private void renderEnemies(){
        for (int i=0;i<enemy.size();i++){
            if(enemy.get(i).x2()>=screenx&&enemy.get(i).x()<=screenx+width&&enemy.get(i).y2()>=screeny&&enemy.get(i).y()<=screeny+height){
                  enemy.get(i).draw(g2,screenx,screeny);
            }
        }
    }
    private void renderDirectionVectors(){
        for (int i=0;i<enemy.size();i++){
            if(enemy.get(i).x2()>=screenx&&enemy.get(i).x()<=screenx+width&&enemy.get(i).y2()>=screeny&&enemy.get(i).y()<=screeny+height){
                  enemy.get(i).drawDirectionVector(g2,screenx,screeny);
            }
        }
    }
    //draw explosions
    private void renderSplosion(){
        for(int i=0;i<splosion.size();i++){
            if(splosion.get(i).isVisible())
                splosion.get(i).draw(g2,screenx,screeny);
            else
                splosion.remove(i);
        }
    }
    private void renderBorder() {
       g2.setColor(border);
       if(screenx<=brdr){
           g2.fillRect(0,0,brdr,height);
           if (screenx<=0)
                screenx=0;
       }
       else if(screenx+width>=fieldx-brdr){
           g2.fillRect(width-brdr,0,brdr,height);
           if(screenx+width>=fieldx)
               screenx=fieldx-width;
       }
       if (screeny<=brdr){
           g2.fillRect(0,0,width,brdr);
           if(screeny<=0)
               screeny=0;
       }
       else if(screeny+height>=fieldy-brdr){
           g2.fillRect(0,height-brdr,width,brdr);
           if(screeny+height>=fieldy)
               screeny=fieldy-height;
       }
    }
    private void renderHUD(){
        if(numLives>=5)
            g2.setColor(Color.BLUE);
        else if(numLives==4)
            g2.setColor(Color.CYAN);
        else if(numLives==3)
            g2.setColor(Color.GREEN);
        else if(numLives==2)
            g2.setColor(Color.YELLOW);
        else if(numLives==1)
            g2.setColor(Color.ORANGE);
        else
            g2.setColor(Color.RED);
        g2.setFont(new Font("Rockwell Condensed",Font.BOLD,30));
        g2.drawString(""+numLives,width/2-5,50+brdr);
        g2.setColor(Color.GREEN);
        g2.setFont(new Font("Rockwell Condensed",Font.BOLD,24));
        g2.drawString("Lives Remaining",width/2-85,20+brdr);
        g2.drawString("Score:"+scoreS,brdr+2,20+brdr);
        g2.drawString("HighScore:"+highScoreS,width-186-brdr,20+brdr);
        g2.setColor(powerC);
        g2.drawString("x"+multiplyer,brdr+142,20+brdr);
        g2.setColor(Color.white);
        g2.drawString(frameRate+"fps",width/2-400,20 + brdr);
    }
    private void renderObstacles(){
        for(UnPassable i : unp)
            if(i != null)
                i.draw(g2, screenx, screeny);
    }
    
    private void renderEnd(){
        if(dbImage == null){
                dbImage = createImage(width,height);
                if(dbImage == null){
                        System.out.println("dbImage is null");
                        return;
                }
                else{
                        g2 = (Graphics2D) dbImage.getGraphics();
                }
        }

        //clears the background
        g2.setColor(new Color(0,0,0,255));
        g2.fillRect(0,0,width,height);
        renderGrid();
        renderBorder();
        g2.setFont(new Font("Rockwell Condensed",Font.BOLD,50));
        g2.setColor(Color.RED);
        g2.drawString("GAME OVER",width/2-145,height/2-45);
        g2.setColor(Color.GREEN);
        g2.drawString("Score:"+scoreS+"  HighScore:"+highScoreS,width/2-325,height/2+15);
    }
    
    private void paintScreen(){
            Graphics g;
            try
            {
                    g = this.getGraphics();
                    if((g != null) && (dbImage != null)){
                            g.drawImage(dbImage, 0, 0,null);
                    }
                    Toolkit.getDefaultToolkit().sync();
                    g.dispose();
            }
            catch(Exception e){
                    System.out.println("Grapics context error");
            }
    }
    
    
    //gives screen acess to keypad variables
    public void addKeys(Keypad keys){this.keys = keys;}
    private boolean togglev = false,toggleb = false, togglec = false;
    private boolean distnums = false,vectfld = false,distclr = false;
    public void toggle(){
        if (keys.V() && !togglev) {
            vectfld = !vectfld;
            togglev = true;
        } else if (keys.V() && togglev) {
            togglev = false;
        }
        
        if (keys.B() && !toggleb) {
            distnums = !distnums;
            toggleb = true;
        } else if (keys.B() && toggleb) {
            toggleb = false;
        }
        
        if (keys.C() && !togglec) {
            distclr = !distclr;
            togglec = true;
        } else if (keys.C() && togglec) {
            togglec = false;
        }
    }
    //gives screen acess to mouse variables
    public void addMouse(Mouse mouse){this.mouse = mouse;}
    
    //random number with break in between
    private int randomEx(double screen,int length,int field){
        double num=Math.abs(screen/(field-length));
        if(Math.random()<=num){
            return (int)(Math.random()*(screen-40)+1);
        }
        else
            return (int)(Math.random()*(field-(length+screen+40))+(screen+length));
    }
    //converts int to string with leading zeros
    static String intToString(int num, int digits) {
        assert digits > 0 : "Invalid number of digits";

        // create variable length array of zeros
        char[] zeros = new char[digits];
        Arrays.fill(zeros, '0');
        // format number as String
        DecimalFormat df = new DecimalFormat(String.valueOf(zeros));

        return df.format(num);
    }
}