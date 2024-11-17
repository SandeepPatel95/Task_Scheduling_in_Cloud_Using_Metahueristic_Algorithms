package org.cloudbus.cloudsim.examples;

import java.util.ArrayList;

/**
 *
 * @author Sandeep Patel
 */
public class Problemdef {

    public static double test_problem(ArrayList<Double> var_values) {
        Double dec_var_values[] = new Double[var_values.size()];
        var_values.toArray(dec_var_values);
        /*problem definition*/
        double obj=Math.pow((dec_var_values[0]-2),2)+Math.pow((dec_var_values[1]-4),2);
        return obj;
    }

}