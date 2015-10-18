package mchi112;


import mchi112.hga.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * We assume that nodes are labelled by ints starting from 1
 */
public class Program {

    private static final int POPULATION_SIZE = 500;
    private static final String FILE = "rat99.tsp";

    public static void main(String[] args) {
        try {

            // Read file and save coordinates
            BufferedReader br = new BufferedReader(new FileReader(new File(FILE)));
            String line;
            List<Point> coords = new ArrayList<>();
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


//            Test cost matrix 999.0
//            float[][] matrix =
//                    {{999, 77, 99, 9, 35, 63, 8},
//                     {75, 999, 86, 46, 88, 29, 20},
//                     {99, 86, 999, 16, 28, 35, 28},
//                     {9, 46, 16, 999, 59, 53, 49},
//                     {35, 88, 28, 59, 999, 76, 72},
//                     {63, 29, 35, 53, 76, 999, 52},
//                     {8, 20, 28, 49, 72, 52, 999}};



            SCS scs = new SCS(CostMatrix.getInstance());
            SCX scx = new SCX(CostMatrix.getInstance());
            SRS srs = new SRS();

            // Generate initial population
            List<Tour> population = scs.generatePopulation(POPULATION_SIZE);

            // Determine best value
            Tour bestTour = null;
            float bestValue = Float.MAX_VALUE;
            for (Tour t : population) {
                System.out.println(t + " - longest edge: " + CostMatrix.getInstance().longestEdgeOf(t));
                if (bestTour == null || CostMatrix.getInstance().longestEdgeOf(t) < CostMatrix.getInstance().longestEdgeOf(bestTour)) {
                    bestTour = t;
                }
            }
            bestValue = CostMatrix.getInstance().longestEdgeOf(bestTour);

            System.out.println("Program complete, best tour is " + bestTour + " with longest edge " + CostMatrix.getInstance().longestEdgeOf(bestTour));
            Tour localSearched = bestTour.localSearch(CostMatrix.getInstance());
            System.out.println("Local search yields " + localSearched + " with longest edge " + CostMatrix.getInstance().longestEdgeOf(localSearched));
            System.out.println("Population size: " + population.size());
            System.out.println();


            // Generate new population
            List<Tour> newPop = srs.reproduce(population);
            for (Tour t : newPop) {
                System.out.println(t + " - longest edge: " + CostMatrix.getInstance().longestEdgeOf(t));
                if (bestTour == null || CostMatrix.getInstance().longestEdgeOf(t) < CostMatrix.getInstance().longestEdgeOf(bestTour)) {
                    bestTour = t;
                }
            }

            System.out.println("Program complete, best tour is " + bestTour + " with longest edge " + CostMatrix.getInstance().longestEdgeOf(bestTour));
            localSearched = bestTour.localSearch(CostMatrix.getInstance());
            System.out.println("Local search yields " + localSearched + " with longest edge " + CostMatrix.getInstance().longestEdgeOf(localSearched));
            System.out.println("Population size: " + newPop.size());

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}