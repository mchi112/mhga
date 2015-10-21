package mchi112;


import mchi112.hga.CostMatrix;
import mchi112.hga.Migration;
import mchi112.hga.Point;
import mchi112.hga.Tour;
import mchi112.model.MultiProcessResult;
import mchi112.task.ModifiedGeneticTask;
import mchi112.task.PopulationGenerationTask;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * We assume that nodes are labelled by ints starting from 1
 */
public class MPHGA {
    private static final int CONCURRENT_POPULATION_COUNT = 4;
    private static final int POPULATION_SIZE = 500;
    private static final int MIGRATION_THRESHOLD = 20;
    private static final float MIGRATION_MULTIPLIER = 1.0f;

    private static final int MAX_GENERATION = 5 * MIGRATION_THRESHOLD;

    private static final String SYS_OUT_DIVIDER_HEAVY = "\n\n===========================\n";
    private static final String SYS_OUT_DIVIDER_LIGHT = "---------------------------";

    private static String[] instancesToEvaluate = {
            "a280"
            ,"ch150"
//            ,"gil262"
//            ,"kroA100"
//            ,"kroA150"
//            ,"kroA200"
//            ,"kroD00"
//            ,"lin318"
//            ,"pr144"
//            ,"pr299"
//            ,"rat99"
//            ,"rat195"
//            ,"rd100"
//            ,"si175"
//            ,"tsp225"
//            ,"u159"
    };

    private static final int NUM_TESTS = 3;

    public static void main(String[] args) {

        for (String instance : instancesToEvaluate) {
            System.out.println(SYS_OUT_DIVIDER_HEAVY);
            System.out.println("Instance: " + instance);
            System.out.println(SYS_OUT_DIVIDER_LIGHT);

            try {

                // Read file and save coordinates
                BufferedReader br = new BufferedReader(new FileReader(new File(instance + ".tsp")));
                String line;
                List<Point> coords = new ArrayList<Point>();
                boolean start = false;
                while ((line = br.readLine()) != null) {

                    if (line.equals("NODE_COORD_SECTION")) {
                        start = true;
                        continue;
                    } else if (line.equals("EOF")) {
                        start = false;
                        continue;
                    } else if (start) {
                        String[] split = line.trim().split("[\\s]+");
                        int x = Integer.parseInt(split[1]);
                        int y = Integer.parseInt(split[2]);
                        coords.add(new Point(x, y));
                    }
                }

                // Create cost matrix
                float[][] matrix = new float[coords.size()][coords.size()];
                for (int i = 0; i < coords.size(); i++) {
                    for (int j = 0; j < coords.size(); j++) {
                        matrix[i][j] = coords.get(i).getDistance(coords.get(j));
                    }
                }
                CostMatrix.init(matrix);
                CostMatrix costMatrix = CostMatrix.getInstance();
                System.out.println("Cost matrix complete");

                double[] performanceResultsInSeconds = new double[NUM_TESTS];
                Tour[] tourResults = new Tour[NUM_TESTS];

                for (int testNum = 0; testNum < NUM_TESTS + 1; testNum++) {
                    // Create executor
                    ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
                    List<Future<MultiProcessResult>> concurrentPopulations = new ArrayList<>();

                    long startTime = System.nanoTime();

                    // Generate initial populations
                    List<Future<MultiProcessResult>> generations = new ArrayList<>();
                    for (int i = 0; i < CONCURRENT_POPULATION_COUNT; i++) {
                        generations.add(executor.submit(new PopulationGenerationTask(POPULATION_SIZE)));
                    }
                    join(generations); // wait for them to finish..

                    // Kick off initial iteration
                    for (int i = 0; i < CONCURRENT_POPULATION_COUNT; i++) {
                        concurrentPopulations.add(executor.submit(
                                new ModifiedGeneticTask(
                                        generations.get(i).get().getPopulation(),
                                        generations.get(i).get().getBestSolutionValue(),
                                        MIGRATION_THRESHOLD)
                        ));
                    }
                    int generationCounter = 0;
                    while (true) {

                        // Wait till they all finished
                        join(concurrentPopulations);
                        generationCounter += MIGRATION_THRESHOLD;

                        // If still not at max generation and all finished, then migrate
                        if (generationCounter < MAX_GENERATION) {

                            // Extract population from MultiProcessResult
                            List<List<Tour>> populationPool = new ArrayList<>();
                            for (Future<MultiProcessResult> f : concurrentPopulations) {
                                populationPool.add(f.get().getPopulation());
                            }
                            // Migrate
                            Migration.mechanism1(MIGRATION_MULTIPLIER, populationPool);

                            // Kick off new iteration
                            List<Future<MultiProcessResult>> newConcurrentPopulations = new ArrayList<>();
                            for (int i = 0; i < concurrentPopulations.size(); i++) {
                                newConcurrentPopulations.add(executor.submit(
                                        new ModifiedGeneticTask(
                                                populationPool.get(i),
                                                concurrentPopulations.get(i).get().getBestSolutionValue(),
                                                MIGRATION_THRESHOLD)
                                ));
                            }
                            concurrentPopulations = newConcurrentPopulations;

                            // If exceed max generation, then terminate
                        } else {
                            executor.shutdown();
                            break;
                        }
                    }

                    // Collate result
                    Tour bestSolutionValue = null;

                    for (Future<MultiProcessResult> f : concurrentPopulations) {
                        if (bestSolutionValue == null ||
                                costMatrix.longestEdgeOf(f.get().getBestSolutionValue()) < costMatrix.longestEdgeOf(bestSolutionValue)) {
                            bestSolutionValue = f.get().getBestSolutionValue();
                        }
                    }

                    long endTime = System.nanoTime();

                    if (testNum == 0) {
                        System.out.println("Warm up complete");
                    } else {
                        // We subtract testNum by 1 because we ignore results of iter 0
                        performanceResultsInSeconds[testNum - 1] = (endTime - startTime) / 1000000000.0;
                        tourResults[testNum - 1] = bestSolutionValue;

                        System.out.println("Test " + (testNum - 1) + " complete");
                    }
                }

                System.out.println("Tests complete");

                double avgTime = 0;
                float avgLongestEdge = 0;
                for (int testNum = 0; testNum < NUM_TESTS; testNum++) {
                    avgTime += performanceResultsInSeconds[testNum];
                    float longestEdge = costMatrix.longestEdgeOf(tourResults[testNum]);
                    avgLongestEdge += longestEdge;
                    System.out.println("Test " + testNum + ": longestEdge=" + longestEdge + ", time(s)=" + performanceResultsInSeconds[testNum]);
                }
                avgTime /= NUM_TESTS;
                avgLongestEdge /= NUM_TESTS;
                System.out.println("Final results: avgLongestEdge=" + avgLongestEdge + ", avgTime(s)=" + avgTime);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void join(List<Future<MultiProcessResult>> tasks)
            throws InterruptedException, ExecutionException {
        for (Future<MultiProcessResult> task : tasks) {
            task.get();
        }
    }
}

