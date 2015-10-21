package mchi112;


import mchi112.hga.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * We assume that nodes are labelled by ints starting from 1
 */
public class HGA {

    private static final int POPULATION_SIZE = 500;
    private static final int MAX_GENERATION = 10000;
    private static final int IMMIGRATION_TIMER = 2000;
    private static final float IMMIGRATION_PROPORTION = 0.1f;
    private static final int IMMIGRATION_NUMBER = (int)(POPULATION_SIZE * IMMIGRATION_PROPORTION);
    private static final int NUM_TESTS = 3;

    private static final String SYS_OUT_DIVIDER_HEAVY = "\n\n===========================\n";
    private static final String SYS_OUT_DIVIDER_LIGHT = "---------------------------";

    private static String[] instancesToEvaluate = {
            "rat30"
//            ,"kroA40"
//            ,"kroD50"
//            "rd60"
//            ,"pr70"
//            ,"ch80"
//            ,"kroA90"
//            ,"kroD100"
//            ,"lin110"
//            ,"kroA120"
    };

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

                SCS scs = new SCS(costMatrix);
                SCX scx = new SCX(costMatrix);
                SRS srs = new SRS();
                Random random = new Random();

                double[] performanceResultsInSeconds = new double[NUM_TESTS];
                Tour[] tourResults = new Tour[NUM_TESTS];

                for (int testNum = 0; testNum < NUM_TESTS + 1; testNum++) {

                    long startTime = System.nanoTime();

                    // Generate initial population
                    List<Tour> population = scs.generatePopulation(POPULATION_SIZE);

                    // Determine best solution
                    Tour bestSolutionValue = null;
                    for (Tour t : population) {
                        if (bestSolutionValue == null ||
                                costMatrix.longestEdgeOf(t) < costMatrix.longestEdgeOf(bestSolutionValue)) {
                            bestSolutionValue = t;
                        }
                    }

                    // System.out.println("Initial population and best solution found");
                    // System.out.println("Initial best solution: " + bestSolutionValue);
                    // System.out.println("Longest edge: " + costMatrix.longestEdgeOf(bestSolutionValue));

                    int generation = 0;
                    int immigrationTimer = IMMIGRATION_TIMER;
                    while (generation < MAX_GENERATION) {

                        // Reproduce
                        population = srs.reproduce(population);

                        // Crossover
                        for (int i = 0; i < population.size(); i++) {
                            int j = (i + 1) % population.size();

                            Tour parent1 = population.get(i);
                            Tour parent2 = population.get(j);

                            if (parent1.equals(parent2)) {
                                continue;
                            }

                            Tour child = scx.crossover(parent1, parent2);
                            if (child.getFitness() > parent1.getFitness()) {
                                population.set(i, child);
                            }
                        }

                        // Mutation
                        for (Tour t : population) {
                            t.mutate();
                        }

                        // Evaluate
                        Tour bestPopulationValue = null;
                        for (Tour t : population) {
                            if (bestPopulationValue == null ||
                                    costMatrix.longestEdgeOf(t) < costMatrix.longestEdgeOf(bestPopulationValue)) {
                                bestPopulationValue = t;
                            }
                        }

                        if (costMatrix.longestEdgeOf(bestPopulationValue) < costMatrix.longestEdgeOf(bestSolutionValue)) {
                            bestPopulationValue = bestPopulationValue.localSearch(CostMatrix.getInstance());
                            bestSolutionValue = bestPopulationValue;
                            immigrationTimer = IMMIGRATION_TIMER;
                        } else {
                            if (immigrationTimer == 0) {
                                // Immigration
                                // System.out.println("Performing immigration for generation " + generation);

                                for (int i = 0; i < IMMIGRATION_NUMBER; i++) {
                                    int removedIndex = random.nextInt(population.size());
                                    population.remove(removedIndex);
                                }

                                List<Tour> immigrated = scs.generatePopulation(IMMIGRATION_NUMBER);
                                for (Tour tour : immigrated) {
                                    population.add(tour);
                                }

                                immigrationTimer = IMMIGRATION_TIMER;
                            }
                        }

                        generation++;
                        immigrationTimer--;
                    }
                    if (testNum == 0) {
                        System.out.println("Warm up complete");
                    } else {
                        // We subtract testNum by 1 because we ignore results of iter 0
                        performanceResultsInSeconds[testNum - 1] = (System.nanoTime() - startTime) / 1000000000.0;
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
                System.out.println("**********ERROR***** Instance: " + instance);
                e.printStackTrace();
            }
        }
    }
}