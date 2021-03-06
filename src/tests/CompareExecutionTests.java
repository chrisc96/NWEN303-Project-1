package tests;

import model.*;
import org.junit.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class CompareExecutionTests {

    // Constants used by timeOf method
    static final int WARMUPS = 100;
    static final int RUNS = 200;

    static final int maxParticleValue = 1024;

    /**
     * These two tests output the time taken for the step() method in the model
     * class to complete at incrementing simulation sizes
     *
     * Easy to edit if you want other ranges. Code is self documenting
     */
    @Test
    public void CompareStep_10_500() {
        int startSimulationSize = 10;
        int maxSimulationSize = 500;
        int incrementBy = 10;

        for (int simulationSize = startSimulationSize; simulationSize <= maxSimulationSize; simulationSize += incrementBy) {
            CompareMethod_AnySize(simulationSize);
        }
    }

    @Test
    public void CompareStep_500_1000() {
        int startSimulationSize = 500;
        int maxSimulationSize = 1000;
        int incrementBy = 10;

        for (int simulationSize = startSimulationSize; simulationSize <= maxSimulationSize; simulationSize += incrementBy) {
            CompareMethod_AnySize(simulationSize);
        }
    }

    /**
     * These two tests output the time taken for the mergeParticles() method
     * in the model class to complete at incrementing simulation sizes
     *
     * Easy to edit if you want other ranges. Code is self documenting
     */
    @Test
    public void CompareMergeParticles_500_1000() {
        int startSimulationSize = 500;
        int maxSimulationSize = 1000;
        int incrementBy = 10;

        for (int simulationSize = startSimulationSize; simulationSize <= maxSimulationSize; simulationSize += incrementBy) {
            CompareMergeParticles_AnySize(simulationSize);
        }
    }

    @Test
    public void CompareMergeParticles_5000_50000() {
        int startSimulationSize = 10000;
        int maxSimulationSize = 15000;
        int incrementBy = 100;

        for (int simulationSize = startSimulationSize; simulationSize <= maxSimulationSize; simulationSize += incrementBy) {
            CompareMergeParticles_AnySize(simulationSize);
        }
    }

    /**
     * These two tests output the time taken for the updateGraphicalRepresentation()
     * method in the model class to complete at incrementing simulation sizes
     *
     * Easy to edit if you want other ranges. Code is self documenting
     */
    @Test
    public void CompareUpdateGui_500_1000() {
        int startSimulationSize = 500;
        int maxSimulationSize = 1000;
        int incrementBy = 10;

        for (int simulationSize = startSimulationSize; simulationSize <= maxSimulationSize; simulationSize += incrementBy) {
            CompareUpdateGui_AnySize(simulationSize);
        }
    }

    @Test
    public void CompareUpdateGui_5000_50000() {
        int startSimulationSize = 10000;
        int maxSimulationSize = 15000;
        int incrementBy = 100;

        for (int simulationSize = startSimulationSize; simulationSize <= maxSimulationSize; simulationSize += incrementBy) {
            CompareUpdateGui_AnySize(simulationSize);
        }
    }

    // Helper Methods to assess efficiencies
    /**
     * This helper acts as an easier way to measure the efficiency of
     * parallel execution vs sequential on different simulation sizes.
     * This helper acts on the step() method in the Model class.
     *
     * The step() method does all of the computational work for the simulation
     * so is a good starting place to assess efficiency differences between
     * the two implementations.
     *
     * This test method creates a new sequential and parallel model
     * and tests how long the step() function takes to complete.
     * Once complete, this information is output to the console.
     *
     * To try make it consistent, we call @timeOf method which warms
     * up the JIT and then runs each step() function @global RUNS number of times
     * on both the parallel execution method of the step() and the sequential
     * version.
     * @param simulationSize
     */
    public void CompareMethod_AnySize(int simulationSize) {
        Model mPar = createModelFromDataSet(generateUniformDataSet(simulationSize, maxParticleValue), ModelParallel::new);
        Model mParOpt = createModelFromDataSet(generateUniformDataSet(simulationSize, maxParticleValue), ModelParallelOptimised::new);
        Model mSeq = createModelFromDataSet(generateUniformDataSet(simulationSize, maxParticleValue), ModelSequential::new);

        long parrallelTime = timeOf(mPar::step, WARMUPS, RUNS);
        long parrallelTimeOpt = timeOf(mParOpt::step, WARMUPS, RUNS);
        long sequentialTime = timeOf(mSeq::step, WARMUPS, RUNS);

        outputTimings(parrallelTime, parrallelTimeOpt, sequentialTime, mPar.p.size());
    }

    /**
     * Same as above but calling different method
     * @param simulationSize
     */
    public void CompareMergeParticles_AnySize(int simulationSize) {
        Model mPar = createModelFromDataSet(generateUniformDataSet(simulationSize, maxParticleValue), ModelParallel::new);
        Model mParOpt = createModelFromDataSet(generateUniformDataSet(simulationSize, maxParticleValue), ModelParallelOptimised::new);
        Model mSeq = createModelFromDataSet(generateUniformDataSet(simulationSize, maxParticleValue), ModelSequential::new);

        long parrallelTime = timeOf(mPar::mergeParticles, WARMUPS, RUNS);
        long parrallelTimeOpt = timeOf(mParOpt::mergeParticles, WARMUPS, RUNS);
        long sequentialTime = timeOf(mSeq::mergeParticles, WARMUPS, RUNS);

        outputTimings(parrallelTime, parrallelTimeOpt, sequentialTime, mPar.p.size());
    }

    /**
     * Same as above but calling different method
     * @param simulationSize
     */
    public void CompareUpdateGui_AnySize(int simulationSize) {
        Model mPar = createModelFromDataSet(generateUniformDataSet(simulationSize, maxParticleValue), ModelParallel::new);
        Model mParOpt = createModelFromDataSet(generateUniformDataSet(simulationSize, maxParticleValue), ModelParallelOptimised::new);
        Model mSeq = createModelFromDataSet(generateUniformDataSet(simulationSize, maxParticleValue), ModelSequential::new);

        long parrallelTime = timeOf(mPar::updateGraphicalRepresentation, WARMUPS, RUNS);
        long parrallelTimeOpt = timeOf(mParOpt::updateGraphicalRepresentation, WARMUPS, RUNS);
        long sequentialTime = timeOf(mSeq::updateGraphicalRepresentation, WARMUPS, RUNS);

        outputTimings(parrallelTime, parrallelTimeOpt, sequentialTime, mPar.p.size());
    }

    /**
     * Outputs results of test to console
     * @param parallelTime time the parallel execution took
     * @param sequentialTime time the sequential execution took
     * @param simulationSize size of the simulation at this test
     */
    private void outputTimings(long parallelTime, long parallelTimeOpt, long sequentialTime, int simulationSize) {
        long min = Math.min(Math.min(parallelTime, parallelTimeOpt), sequentialTime);
        long max = Math.max(Math.max(parallelTime, parallelTimeOpt), sequentialTime);
        long mid = parallelTime + parallelTimeOpt + sequentialTime - min - max;

        if (min == parallelTime) {
            System.out.printf("Particle size was %d.\t\tParallel was fastest!\t\t\t(par = %2d ms vs parOpt = %2d vs seq = %2d ms)\t\t\t%.2f times faster than slowest\t\tTested over %d runs\n",
                    simulationSize, parallelTime, parallelTimeOpt, sequentialTime, ((float) max/min), RUNS);
        }
        else if (min == parallelTimeOpt) {
            System.out.printf("Particle size was %d.\t\tParallelOpt was fastest!\t\t(parOpt = %2d ms vs par = %2d ms vs seq = %2d ms)\t\t%.2f times faster than slowest\t\tTested over %d runs\n",
                    simulationSize, parallelTimeOpt, parallelTime, sequentialTime, ((float) max/min), RUNS);
        }
        else {
            System.out.printf("Particle size was %d.\t\tSequential was fastest!\t\t\t(seq = %2d ms vs parOpt = %2d ms vs par = %2d)\t\t\t%.2f times faster than slowest\t\tTested over %d runs\n",
                    simulationSize, sequentialTime, parallelTimeOpt, parallelTime, ((float) max/min), RUNS);
        }
    }

    /**
     *
     * @param r
     * @param warmUp
     * @param runs
     * @return
     */
    long timeOf(Runnable r, int warmUp, int runs) {
        System.gc();
        for (int i = 0; i < warmUp; i++) {
            r.run();
        }
        long time0 = System.currentTimeMillis();
        for (int i = 0; i < runs; i++) {
            r.run();
        }
        long time1 = System.currentTimeMillis();
        return time1 - time0;
    }

    /**
     * @param setSize
     * @return
     */
    public static List<Particle> generateUniformDataSet(int setSize, int maxValue) {
        List<Particle> p = new ArrayList<>();
        Random r = new Random(0);
        for (int i = 0; i < setSize; i++) {
            // Uses the max value passed in to determine numBits to pass to BigInteger
            // I.E 900 = Log(900)/Log(2) = ~9.81 ~ 10.0 numBits ~ 1024 max value. Approx.
            BigInteger numBitsLog2 = new BigInteger((int) Math.ceil(Math.log(maxValue)/Math.log(2)), r);
            p.add(new Particle(0.5, 0, 0, numBitsLog2.doubleValue(), numBitsLog2.doubleValue()));
        }
        return p;
    }

    /**
     *
     * @param dataset
     * @param mSupplier
     * @return
     */
    public static Model createModelFromDataSet(List<Particle> dataset, Supplier<Model> mSupplier) {
        Model m = mSupplier.get();
        m.p = new ArrayList<>(dataset);
        return m;
    }
}