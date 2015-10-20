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
public class Program {

    private static final int POPULATION_SIZE = 500;
    private static final int MAX_GENERATION = 10000;
    private static final int IMMIGRATION_TIMER = 2000;
    private static final float IMMIGRATION_PROPORTION = 0.1f;
    private static final int IMMIGRATION_NUMBER = (int)(POPULATION_SIZE * IMMIGRATION_PROPORTION);
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
            CostMatrix costMatrix = CostMatrix.getInstance();


//            Test cost matrix 999.0
//            float[][] matrix =
//                    {{999, 77, 99, 9, 35, 63, 8},
//                     {75, 999, 86, 46, 88, 29, 20},
//                     {99, 86, 999, 16, 28, 35, 28},
//                     {9, 46, 16, 999, 59, 53, 49},
//                     {35, 88, 28, 59, 999, 76, 72},
//                     {63, 29, 35, 53, 76, 999, 52},
//                     {8, 20, 28, 49, 72, 52, 999}};


            System.out.println("Cost matrix complete");

            SCS scs = new SCS(costMatrix);
            SCX scx = new SCX(costMatrix);
            SRS srs = new SRS();
            Random random = new Random();

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

            System.out.println("Initial population and best solution found");
            System.out.println("Initial best solution: " + bestSolutionValue);
            System.out.println("Longest edge: " + costMatrix.longestEdgeOf(bestSolutionValue));

            int generation = 0;
            int immigrationTimer = IMMIGRATION_TIMER;
            while(generation < MAX_GENERATION) {

                // Reproduce
                population = srs.reproduce(population);

                // Crossover
                for(int i = 0; i < population.size(); i++) {
                    int j = (i+1)%population.size();

                    Tour parent1 = population.get(i);
                    Tour parent2 = population.get(j);

                    if(parent1.equals(parent2)){
                        continue;
                    }

                    Tour child = scx.crossover(parent1, parent2);
                    if(child.getFitness() > parent1.getFitness()) {
                        population.set(i, child);
                    }
                }

                // Mutation
                for(Tour t : population) {
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

                if(costMatrix.longestEdgeOf(bestPopulationValue) < costMatrix.longestEdgeOf(bestSolutionValue)) {
                    bestPopulationValue = bestPopulationValue.localSearch(CostMatrix.getInstance());
                    bestSolutionValue = bestPopulationValue;
                    immigrationTimer = IMMIGRATION_TIMER;
                } else {
                    if(immigrationTimer == 0) {
                        // Immigration
                        System.out.println("Performing immigration for generation " + generation);

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
            System.out.println("Program complete");
            System.out.println("Best tour: " + bestSolutionValue);
            System.out.println("Longest edge: " + costMatrix.longestEdgeOf(bestSolutionValue));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}