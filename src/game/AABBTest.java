/**
 *
 * @author Silviu
 */
package game;

import java.awt.Rectangle;
import static java.lang.Math.max;
import static java.lang.Math.min;

public class AABBTest {
    static float low_r = 0;
    static float high_r = 1;
    public static float[] lineBoxTest(Rectangle box,float linx0, float liny0,
                                          float linx1, float liny1){
        float[] rtrn = {0,0};
        int boxx0 = box.x;
        int boxy0 = box.y;
        int boxx1 = boxx0 + box.width;
        int boxy1 = boxy0 + box.height;
        low_r = 0;
        high_r = 1;
        if(!check_dimension(boxx0, boxx1, linx0, linx1))
            return rtrn;
        if(!check_dimension(boxy0, boxy1, liny0, liny1))
            return rtrn;
        rtrn[0] = (linx1 - linx0) * (1 - low_r);
        rtrn[1] = (liny1 - liny0) * (1 - low_r);
        return rtrn;
    }
    
    private static boolean check_dimension(float box_low, float box_high,
                                float line_low, float line_high){
        float dim_low,dim_high;
        
            dim_low = (box_low - line_low)/(line_high - line_low);
            dim_high = (box_high - line_low)/(line_high - line_low);
        
        if (dim_high < dim_low){
            float temp = dim_high;
            dim_high = dim_low;
            dim_low = temp;
        }
        if(dim_high < low_r)
            return false;
        if(dim_low > high_r)
            return false;
        low_r = max(low_r, dim_low);
        high_r = min(high_r, dim_high);
        if(low_r > high_r)
            return false;
        return true;
    }
}
