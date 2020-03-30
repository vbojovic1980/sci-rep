package hr.irb.helpers;

import java.util.List;

public class MathHelper {
    public static  Double sumList(List<Double> list){
        double sum = 0;
        for(Double d : list)
            sum += (d==null)?0:d;
        return sum;
    }
}
