package org.cloudbus.cloudsim.examples;

import java.util.*;

/**
 *
 * @author Sandeep Patel
 */
public class EnhanceHarmonyProblemdef {

    /** public static double test_problem(ArrayList<Double> var_values) {
    	
        Double dec_var_values[] = new Double[var_values.size()];
        var_values.toArray(dec_var_values);
        **/
	public static double test_problem(List<Double> rand_values) {
		Double dec_var_values[] = new Double[rand_values.size()];
		rand_values.toArray(dec_var_values);
        /*problem definition*/
        double obj=Math.pow((dec_var_values[0]-2),2)+Math.pow((dec_var_values[1]-4),2);
        return obj;
    }

}