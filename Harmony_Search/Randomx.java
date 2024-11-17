package org.cloudbus.cloudsim.examples;

import java.util.Random;

import SJF.HarmonySearch;
/**
 *
 * @author Sandeep Patel
 */
public class Randomx {

    protected static Random random= new Random();

    public static double randomInRange(double min, double max) {
        double range = max - min;
        double scaled = random.nextDouble()* range;
        double value = scaled + min;
        return value;
    }
    
    public static double randomprob() {
        double max=1,min=0;
        double range = max - min;
        double scaled = random.nextDouble()* range;
        double value = scaled + min;
        return value;
    }
    
    public static int randomindex(){
        int s=Math.toIntExact(Math.round(random.nextDouble()*HarmonySearch.HMS));
        return s;
    }
}

