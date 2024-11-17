package org.cloudbus.cloudsim.examples;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.List;


public class EnhanceHarmonySearch {

    // Algorithm parameters
    public static int HMS = 0;
    public static int IMP = 0;
    public static int nOBJ = 0;
    public static int nDEC_VARS = 0;
    public static int nCONS = 0;
    public static float HMCR;
    public static float PAR;
    public static float FW;
    
    // Data structures
    public static List<Double> Max_dv = new ArrayList<>();
    public static List<Double> Min_dv = new ArrayList<>();
    public static List<Double> improvised_harmony = new ArrayList<>();
    public static Map<Integer, List<Double>> H_Mem = new HashMap<>();
    public static List<Double> objective = new ArrayList<>();
    
    public static void main(String[] args) throws IOException {
        // Initialize file paths (use relative paths for better portability)
        File init_harmony = new File("initial_harmony.out");
        File fin_harmony = new File("final_harmony.out");
        File bst_harmony = new File("best_harmony.out");
        File params = new File("parameters.out");

        // Buffered writers for output files
        try (BufferedWriter i_h = new BufferedWriter(new FileWriter(init_harmony));
             BufferedWriter f_h = new BufferedWriter(new FileWriter(fin_harmony));
             BufferedWriter b_h = new BufferedWriter(new FileWriter(bst_harmony));
             BufferedWriter param = new BufferedWriter(new FileWriter(params))) {

            i_h.write("# This File Contains The Input Harmonies # \n");
            f_h.write("# This File Contains The Final Harmonies # \n");
            b_h.write("# This File Contains The Best Harmony # \n");
            param.write("# This File Contains Pre-Set Parameters # \n");

            Scanner user_input = new Scanner(System.in);

            // Input handling with validation
            System.out.print("\nEnter the size of the harmony memory (a multiple of 4): ");
            HMS = user_input.nextInt();
            if (HMS % 4 != 0) {
                System.out.println("Warning: HMS should ideally be a multiple of 4.");
            }

            System.out.print("\nEnter the number of improvisations: ");
            IMP = getIntInput(user_input, "Number of improvisations must be greater than 0.", x -> x > 0);

            System.out.print("\nEnter Harmony Memory Considering Rate (0 <= HMCR <= 1): ");
            HMCR = getFloatInput(user_input, "HMCR must be between 0 and 1.", x -> x >= 0 && x <= 1);

            System.out.print("\nEnter Pitch Adjusting Rate (0 <= PAR <= 1): ");
            PAR = getFloatInput(user_input, "PAR must be between 0 and 1.", x -> x >= 0 && x <= 1);

            System.out.print("\nEnter Fret Width (0.01 <= FW <= 0.1): ");
            FW = getFloatInput(user_input, "FW must be between 0.01 and 0.1.", x -> x >= 0.01 && x <= 0.1);

            System.out.print("\nEnter the number of Objective Functions: ");
            nOBJ = getIntInput(user_input, "Number of objectives must be greater than 0.", x -> x > 0);

            System.out.print("\nEnter the number of Decision Variables: ");
            nDEC_VARS = getIntInput(user_input, "Number of decision variables must be greater than 0.", x -> x > 0);

            System.out.print("\nEnter the number of Constraints: ");
            nCONS = getIntInput(user_input, "Number of constraints must be non-negative.", x -> x >= 0);

            // Collect decision variable bounds
            for (int i = 0; i < nDEC_VARS; i++) {
                System.out.print("Enter max value for decision variable " + (i + 1) + ": ");
                double maxVal = user_input.nextDouble();
                System.out.print("Enter min value for decision variable " + (i + 1) + ": ");
                double minVal = user_input.nextDouble();

                if (maxVal <= minVal) {
                    System.out.println("Invalid bounds: max must be greater than min. Exiting.");
                    System.exit(1);
                }

                Max_dv.add(maxVal);
                Min_dv.add(minVal);
            }

            System.out.println("\n##### Input data successfully entered, now performing initialization #####\n");

            // Log parameters
            param.write("\n## Size of the Harmony Memory= " + HMS);
            param.write("\n## Maximum number of improvisations= " + IMP);
            param.write("\n## Harmony Memory Considering Rate= " + HMCR);
            param.write("\n## Pitch Adjusting Rate= " + PAR);
            param.write("\n## Fret Width= " + FW);
            param.write("\n## Number of Objectives= " + nOBJ);
            param.write("\n## Number of Constraints= " + nCONS);
            param.write("\n## Number of Decision Variables= " + nDEC_VARS);

            for (int i = 0; i < nDEC_VARS; i++) {
                param.write("\n__ Max value for decision variable " + (i + 1) + "= " + Max_dv.get(i));
                param.write("\n__ Min value for decision variable " + (i + 1) + "= " + Min_dv.get(i));
            }

            // Initialize and update harmony memory
            harmony_memory_initialization();
            logInitialHarmonies(i_h);

            System.out.println("\n##### Successfully initialized Harmony Memory, now performing improvisation & Updation #####\n");

            for (int i = 0; i < IMP; i++) {
                harmony_improvisation();
                harmony_updation();
            }

            logFinalHarmonies(f_h);
            logBestHarmony(b_h);

            System.out.println("\n##### Successfully finished Searching & outputs are written #####\n");
        }
    }

    // Method to safely get an integer input with validation
    private static int getIntInput(Scanner scanner, String errorMessage, java.util.function.IntPredicate validator) {
        int input;
        while (true) {
            input = scanner.nextInt();
            if (validator.test(input)) {
                return input;
            }
            System.out.println(errorMessage);
        }
    }

    // Method to safely get a float input with validation
    private static float getFloatInput(Scanner scanner, String errorMessage, java.util.function.DoublePredicate validator) {
        float input;
        while (true) {
            input = scanner.nextFloat();
            if (validator.test(input)) {
                return input;
            }
            System.out.println(errorMessage);
        }
    }

    // Initialize the harmony memory with random values
    public static void harmony_memory_initialization() {
        int nRandom_H = 4 * HMS;
        Map<Double, List<Double>> random_hms = new TreeMap<>();

        for (int i = 0; i < nRandom_H; i++) {
            List<Double> rand_values = new ArrayList<>();
            for (int j = 0; j < nDEC_VARS; j++) {
                rand_values.add(EnhanceHarmonyRandomx.randomInRange(Min_dv.get(j), Max_dv.get(j)));
            }
            double obj = EnhanceHarmonyProblemdef.test_problem(rand_values);
            random_hms.put(obj, rand_values);
        }

        int index = 0;
        for (Map.Entry<Double, List<Double>> entry : random_hms.entrySet()) {
            if (index >= HMS) break;
            objective.add(entry.getKey());
            H_Mem.put(index++, entry.getValue());
        }
    }

    // Improvisation logic
    public static void harmony_improvisation() {
        List<Double> improvised_harmony_local = new ArrayList<>();
        double prob = EnhanceHarmonyRandomx.randomprob();

        if (prob > HMCR) {
            for (int j = 0; j < nDEC_VARS; j++) {
                improvised_harmony_local.add(EnhanceHarmonyRandomx.randomInRange(Min_dv.get(j), Max_dv.get(j)));
            }
        } else {
            int index = EnhanceHarmonyRandomx.randomindex();
            improvised_harmony_local = new ArrayList<>(H_Mem.get(index));
            double prob1 = EnhanceHarmonyRandomx.randomprob();

            if (prob1 <= PAR) {
                for (int i = 0; i < nDEC_VARS; i++) {
                    double pitch_adj = EnhanceHarmonyRandomx.randomprob() * (Max_dv.get(i) - Min_dv.get(i)) * FW;
                    double newValue = (improvised_harmony_local.get(i) + pitch_adj <= Max_dv.get(i))
                            ? improvised_harmony_local.get(i) + pitch_adj
                            : improvised_harmony_local.get(i) - pitch_adj;
                    improvised_harmony_local.set(i, newValue);
                }
            }
        }

        improvised_harmony = improvised_harmony_local;
    }

    // Update harmony memory if the new harmony is better
    public static void harmony_updation() {
        double obj = EnhanceHarmonyProblemdef.test_problem(improvised_harmony);
        double max = Collections.max(objective);
        int index_to_remove = objective.indexOf(max);

        if (obj < max) {
            objective.set(index_to_remove, obj);
            H_Mem.put(index_to_remove, new ArrayList<>(improvised_harmony));
        }
    }

    // Log initial harmonies to a file
    private static void logInitialHarmonies(BufferedWriter writer) throws IOException {
        for (Map.Entry<Integer, List<Double>> entry : H_Mem.entrySet()) {
            writer.write("Objective= " + objective.get(entry.getKey()) + "  Decision Variables: " + entry.getValue() + "\n");
        }
    }

    // Log final harmonies to a file
    private static void logFinalHarmonies(BufferedWriter writer) throws IOException {
        for (Map.Entry<Integer, List<Double>> entry : H_Mem.entrySet()) {
            writer.write("Objective= " + objective.get(entry.getKey()) + "  Decision Variables: " + entry.getValue() + "\n");
        }
    }

    // Log the best harmony to a file
    private static void logBestHarmony(BufferedWriter writer) throws IOException {
        double minObjective = Collections.min(objective);
        int bestIndex = objective.indexOf(minObjective);
        writer.write("Best Objective= " + minObjective + "  Decision Variables: " + H_Mem.get(bestIndex) + "\n");
    }
}
