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

    private static final int REPEATS = 0;

    private static final int CONCURRENT_POPULATION_COUNT = 4;
    private static final int POPULATION_SIZE = 500;
    private static final int MIGRATION_THRESHOLD = 2000;
    private static final int MIGRATION_MULTIPLER = 1;

    private static final int MAX_GENERATION = 5 * MIGRATION_THRESHOLD;

    private static final String FILE = "rat99.tsp";

    public static void main(String[] args) {
        try {

            // Read file and save coordinates
            BufferedReader br = new BufferedReader(new FileReader(new File(FILE)));
            String line;
            List<Point> coords = new ArrayList<Point>();
            boolean start = false;
            while((line = br.readLine()) != null) {

                if(line.equals("NODE_COORD_SECTION")) {
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
            for(int i = 0; i < coords.size(); i++) {
                for(int j = 0; j < coords.size(); j++) {
                    matrix[i][j] = coords.get(i).getDistance(coords.get(j));
                }
            }
            CostMatrix.init(matrix);
            System.out.println("Cost matrix complete");

            // IF DOING MULTIPLE RUNS...
            int multirun = 0;
            do {
                // Create executor
                ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
                List<Future<MultiProcessResult>> concurrentPopulations = new ArrayList<>();

                long t1 = System.nanoTime();

                // Generate initial populations
                List<Future<MultiProcessResult>> generations = new ArrayList<>();
                for(int i = 0; i < CONCURRENT_POPULATION_COUNT; i++) {
                    generations.add(executor.submit(new PopulationGenerationTask(POPULATION_SIZE)));
                }
                waitTillComplete(generations); // wait for them to finish..


                // Kick off initial iteration
                for(int i = 0; i < CONCURRENT_POPULATION_COUNT; i++) {
                    concurrentPopulations.add(executor.submit(
                            new ModifiedGeneticTask(
                                    generations.get(i).get().getPopulation(),
                                    generations.get(i).get().getBestSolutionValue(),
                                    MIGRATION_THRESHOLD)
                    ));
                }
                int generationCounter = 0;
                while (true){

                    // Wait till they all finished
                    waitTillComplete(concurrentPopulations);
                    generationCounter += MIGRATION_THRESHOLD;

                    // If still not at max generation and all finished, then migrate
                    if(generationCounter < MAX_GENERATION) {

                        // Extract population from MultiProcessResult
                        List<List<Tour>> populationPool = new ArrayList<>();
                        for(Future<MultiProcessResult> f : concurrentPopulations) {
                            populationPool.add(f.get().getPopulation());
                        }
                        // Migrate
                        Migration.mechanism1(MIGRATION_MULTIPLER, populationPool);

                        // Kick off new iteration
                        List<Future<MultiProcessResult>> newConcurrentPopulations = new ArrayList<>();
                        for(int i = 0; i < concurrentPopulations.size(); i++) {
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
                CostMatrix costMatrix = CostMatrix.getInstance();
                for (Future<MultiProcessResult> f : concurrentPopulations) {
                    if (bestSolutionValue == null ||
                            costMatrix.longestEdgeOf(f.get().getBestSolutionValue()) < costMatrix.longestEdgeOf(bestSolutionValue)) {
                        bestSolutionValue = f.get().getBestSolutionValue();
                    }
                }

                long t2 = System.nanoTime();

                System.out.println("MPHGA complete");
                System.out.println("Best tour: " + bestSolutionValue);
                System.out.println("Longest edge: " + costMatrix.longestEdgeOf(bestSolutionValue));
                System.out.println("Time taken(s): " + (t2-t1)/1000000000.0);

            } while(++multirun < REPEATS);

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void waitTillComplete(List<Future<MultiProcessResult>> tasks)
            throws InterruptedException, ExecutionException {
        boolean isDone = false;
        while(!isDone) {
            Thread.sleep(100);

            isDone = true;
            for(Future<MultiProcessResult> f : tasks) {
                isDone &= f.isDone();
            }
        }
    }
}

