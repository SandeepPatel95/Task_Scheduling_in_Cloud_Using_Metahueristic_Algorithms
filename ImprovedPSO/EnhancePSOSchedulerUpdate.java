package org.cloudbus.cloudsim.examples;
/****
 * @author Sandeep Patel 
 * 
 * */
import net.sourceforge.jswarm_pso.Particle;
import net.sourceforge.jswarm_pso.ParticleUpdate;
import net.sourceforge.jswarm_pso.Swarm;
import utils.Constants;

//public class SchedulerParticleUpdate extends ParticleUpdate {
public class EnhancePSOSchedulerUpdate extends ParticleUpdate {
    private double w;  // Dynamic inertia weight
    private static final double C1 = 2.0;  // Cognitive coefficient
    private static final double C2 = 2.0;  // Social coefficient
    private static final double MAX_VELOCITY = 4.0;  // Maximum velocity limit

    EnhancePSOSchedulerUpdate(Particle particle) {
        super(particle);
        this.w = 0.9;  // Initial inertia weight
    }

    @Override
    public void update(Swarm swarm, Particle particle) {
        double[] v = particle.getVelocity();
        double[] x = particle.getPosition();
        double[] pbest = particle.getBestPosition();
        double[] gbest = swarm.getBestPosition();

        for (int i = 0; i < Constants.NO_OF_TASKS; ++i) {
            // Update velocity with clamping
            v[i] = w * v[i] + C1 * Math.random() * (pbest[i] - x[i]) + C2 * Math.random() * (gbest[i] - x[i]);
            v[i] = Math.max(-MAX_VELOCITY, Math.min(MAX_VELOCITY, v[i]));  // Clamp velocity

            // Update position and ensure it's within valid range
            x[i] = (int) (x[i] + v[i]);
            if (x[i] < 0) {
                x[i] = 0;  // Wrap to minimum index if out of bounds
            } else if (x[i] >= Constants.NO_OF_DATA_CENTERS) {
                x[i] = Constants.NO_OF_DATA_CENTERS - 1;  // Wrap to max index if out of bounds
            }
        }

        // Reduce inertia weight linearly for better convergence
        w = Math.max(0.4, w - 0.001);
    }
}
