/**
 *
 * @author Silviu
 */
package game;

import java.awt.Rectangle;
import java.util.Arrays;
import java.util.LinkedList;

public class DirectionField {
    private static int[][] obst_dist_field;
    public static int[][] dist_field;
    private static boolean[][] obst_accesed_field;
    private static boolean[][] accesed_field;
    public static SVector[][] dict_vector_field;
    private static int el;//length of each field location
    private static int width, height;
    
    
    public DirectionField(int lenX, int lenY, int px_length, int w, int h){
        width = Math.round(w / px_length) + 1;
        height = Math.round(h / px_length) + 1;
        obst_accesed_field = new boolean[lenX][lenY];
        for (int i = 0; i < obst_accesed_field.length; i++)
            Arrays.fill (obst_accesed_field[i],false);
        obst_dist_field = new int[lenX][lenY];
        el = px_length;
    }
    public void add_obstacles(UnPassable[] unp){
        for (UnPassable i : unp) {
            if (i != null){
                for(int j = 0; j < obst_dist_field.length; j++){
                    for(int k = 0; k < obst_dist_field[j].length; k++){
                        if(i.intersects(new Rectangle(j * el ,k * el,el,el))){
                               obst_dist_field[j][k] = 1000;
                               obst_accesed_field[j][k] = true;
                        }       
                    }
                }
            } 
        }
    }
    public void copy_obst_field(int x, int y){
        accesed_field = new boolean[width][];
        for(int i = x; i < width + x; i++)
            accesed_field[i - x] = bpartArray(obst_accesed_field[i],
                                          height, y);
        
        dist_field = new int[width][];
        for(int i = x; i < width + x; i++)
            dist_field[i - x] = partArray(obst_dist_field[i],
                                          height, y); 
    }
    public void create_dist_field(int x,int y, int screenx, int screeny){
        int ax = (int)(x/el - screenx/el);
        int ay = (int)(y/el - screeny/el);
        int ax1 = ax + 1;
        int ay1 = ay + 1;
        
        dist_field[ax][ay] = 0;
        accesed_field[ax][ay] = true;        
        LinkedList<SVector> wave1 = new LinkedList<SVector>();
        wave1.add(new SVector(ax,ay));
        create_dist_field_recursor(wave1, 1);
    }
    public void updateDistField(LinkedList<Enemy> enemy, int screenx, int screeny){
        for(int i=0;i<enemy.size();i++){
            int xcord = (int) Math.round((enemy.get(i).posx - screenx) / el);
            int ycord = (int)Math.round((enemy.get(i).posy - screeny) / el);
            accesed_field[xcord][ycord] = true;
            dist_field[xcord][ycord] += 1;
        }
    }
    public void createVectorField(){
        dict_vector_field = new SVector[width][height];
        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                
                dict_vector_field[i][j] = return_vector(i, j);
            }
        }
    }
    public SVector return_vector(int i, int j){
        int top, bottom, left, right;
        
        if((j - 1) >= 0 && dist_field[i][j - 1] != 1000)
            top = dist_field[i][j - 1];
        else
            top = dist_field[i][j];
        
        if((j + 1) < height && dist_field[i][j + 1] != 1000)
            bottom = dist_field[i][j + 1];
        else
            bottom = dist_field[i][j];
                
        if((i - 1) >= 0 && dist_field[i - 1][j] != 1000)
            left = dist_field[i - 1][j];
        else
            left = dist_field[i][j];
                
        if((i + 1) < width && dist_field[i + 1][j] != 1000)
            right = dist_field[i + 1][j];
        else
            right = dist_field[i][j];
        
        int dirx = left - right;
        int diry = top - bottom;
        return new SVector(dirx, diry);
    }
    private void create_dist_field_recursor(LinkedList<SVector> wave, int dist){
        for(int i = 0; i < wave.size(); i++)
            dist_field[wave.get(i).x][wave.get(i).y] = dist;
        LinkedList<SVector> next_wave = next_wave(wave, dist_field);
        if (next_wave.size() > 0) 
            create_dist_field_recursor(next_wave, dist + 1);
    }
    private LinkedList<SVector> next_wave(LinkedList<SVector> list, int[][] field){
        LinkedList<SVector> x = new LinkedList<SVector>();
            for(int i = 0; i < list.size(); i++){
                LinkedList<SVector> temp = 
                        surrounding_list(list.get(i).x,list.get(i).y, accesed_field);
                for(int j = 0; j < temp.size(); j++){
                        x.add(temp.get(j));
                }
            }
        return x;
    }
    private LinkedList surrounding_list(int x, int y, boolean[][] df){
        LinkedList<SVector> list = new LinkedList<SVector>();
        if(x > 0 && df[x - 1][y] == false){
            list.add(new SVector(x - 1, y));
            df[x - 1][y] = true;
        }
        if(x < df.length - 1 && df[x + 1][y] == false){
            list.add(new SVector(x + 1, y));
            df[x + 1][y] = true;
        }
        if(y > 0 && df[x][y - 1] == false){
            list.add(new SVector(x, y - 1));
            df[x][y - 1] = true;
        }
        if(y < df[0].length - 1 && df[x][y + 1] == false){
            list.add(new SVector(x, y + 1));
            df[x][y + 1] = true;
        }
        return list;
    }
    private static boolean[] bpartArray(boolean[] array, int size, int start) {
    boolean[] part = new boolean[size];
    try{
    System.arraycopy(array, start, part, 0, size);
    }catch(Exception e){}
    return part;
    }
    private static int[] partArray(int[] array, int size, int start) {
    int[] part = new int[size];
    try{
    System.arraycopy(array, start, part, 0, size);
    }catch(Exception e){}
    return part;
    }
    public static void printField(){
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < dist_field[0].length; i++) {
            for (int j = 0; j < dist_field.length; j++) {
                str.append(String.valueOf(dist_field[j][i]) + "\t");
            }
            str.append("\n");
        }
        System.out.println(str);
    }
    public void printVectorField(){
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < dict_vector_field[0].length; i++) {
            for (int j = 0; j < dict_vector_field.length; j++) {
                str.append(dict_vector_field[j][i] + "\t");
            }
            str.append("\n");
        }
        System.out.println(str);
    }
    public void printObstField(){
        StringBuilder str = new StringBuilder();
        for (int i = 0; i< obst_dist_field.length; i++) {
            for (int j = 0; j < obst_dist_field[0].length; j++) {
                str.append(String.valueOf(obst_dist_field[j][i]) + "\t");
            }
            str.append("\n");
        }
        System.out.println(str);
    }
    public void printBoolField(){
        StringBuilder str = new StringBuilder();
        for (int i = 0; i< accesed_field[0].length; i++) {
            for (int j = 0; j < accesed_field.length; j++) {
                str.append(String.valueOf(accesed_field[j][i]) + "\t");
            }
            str.append("\n");
        }
        System.out.println(str);
    }
}
