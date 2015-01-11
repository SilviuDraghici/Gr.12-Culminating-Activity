/**
 *
 * @author Silviu
 */
package game;

public class SVector {
    public int x;
    public int y;
    public double lengthlength;
    public double normx,normy;
    public SVector(int x, int y){
        this.x = x;
        this.y = y;
        this.lengthlength = (x*x) + (y*y);
        double invsqrt = invSqrt(lengthlength);
        this.normx = x*invsqrt;
        this.normy = y*invsqrt;
    }
    public static double invSqrt(double x) {
        double xhalf = 0.5d*x;
        long i = Double.doubleToLongBits(x);
        i = 0x5fe6ec85e7de30daL - (i>>1);
        x = Double.longBitsToDouble(i);
        x = x*(1.5d - xhalf*x*x);
        return x;
    }
    public boolean equals(Object v){
        SVector FUCKJAVA = (SVector)v;
        return this.x == FUCKJAVA.x && this.y == FUCKJAVA.y;
    }
    public String toString(){
        return "x:" + this.normx + " y:" + this.normy;
    }
}
