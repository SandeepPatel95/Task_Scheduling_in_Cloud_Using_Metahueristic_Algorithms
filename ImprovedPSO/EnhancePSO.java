package org.cloudbus.cloudsim.examples;

import net.sourceforge.jswarm_pso.Swarm;
import utils.Constants;

public class EnhancePSO {
    private static Swarm swarm;
    private static EnhancePSOSchedulerParticle particles[];
    private static EnhancePSOSchedulerFitnessFunction ff = new EnhancePSOSchedulerFitnessFunction();

    public EnhancePSO() {
        initParticles();
    }

    public double[] run() {
        swarm = new Swarm(Constants.POPULATION_SIZE, new EnhancePSOSchedulerParticle(), ff);
        swarm.setMinPosition(0);
        swarm.setMaxPosition(Constants.NO_OF_DATA_CENTERS - 1);
        swarm.setMaxMinVelocity(0.5);
        swarm.setParticles(particles);
        swarm.setParticleUpdate(new EnhancePSOSchedulerUpdate(new EnhancePSOSchedulerParticle()));

        for (int i = 0; i < 500; i++) {
            swarm.evolve();
            if (i % 10 == 0) {
                System.out.printf("Global best at iteration (%d): %f\n", i, swarm.getBestFitness());
            }
        }

        System.out.println("\nThe best fitness value: " + swarm.getBestFitness());
        System.out.println("Best makespan: " + ff.calcMakespan(swarm.getBestParticle().getBestPosition()));
        System.out.println("The best solution is: ");
        EnhancePSOSchedulerParticle bestParticle = (EnhancePSOSchedulerParticle) swarm.getBestParticle();
        System.out.println(bestParticle.toString());

        return swarm.getBestPosition();
    }

    private static void initParticles() {
        particles = new EnhancePSOSchedulerParticle[Constants.POPULATION_SIZE];
        for (int i = 0; i < Constants.POPULATION_SIZE; ++i)
            particles[i] = new EnhancePSOSchedulerParticle();
    }

    public void printBestFitness() {
        System.out.println("\nBest fitness value: " + swarm.getBestFitness() +
                "\nBest makespan: " + ff.calcMakespan(swarm.getBestParticle().getBestPosition()));
    }
}
