package mchi112;

import mchi112.hga.CostMatrix;
import mchi112.hga.SCS;
import mchi112.hga.SRS;
import mchi112.hga.Tour;

import java.util.List;

/**
 * We assume that nodes are labelled by ints starting from 1
 */
public class Program {
    private static final int POPULATION_SIZE = 500;

    public static void main(String[] args) {
        try {
            // Test cost matrix 999.0
            int[][] matrix =
                    {{999, 77, 99, 9, 35, 63, 8},
                            {75, 999, 86, 46, 88, 29, 20},
                            {99, 86, 999, 16, 28, 35, 28},
                            {9, 46, 16, 999, 59, 53, 49},
                            {35, 88, 28, 59, 999, 76, 72},
                            {63, 29, 35, 53, 76, 999, 52},
                            {8, 20, 28, 49, 72, 52, 999}};
            CostMatrix.init(matrix);

            SCS scs = new SCS(CostMatrix.getInstance());
            SRS srs = new SRS();


            List<Tour> population = scs.generatePopulation(POPULATION_SIZE);

            Tour bestTour = null;
            for (Tour t : population) {
                System.out.println(t + " - longest edge: " + CostMatrix.getInstance().longestEdgeOf(t));
                if (bestTour == null || CostMatrix.getInstance().longestEdgeOf(t) < CostMatrix.getInstance().longestEdgeOf(bestTour)) {
                    bestTour = t;
                }
            }

            System.out.println("Program complete, best tour is " + bestTour + " with longest edge " + CostMatrix.getInstance().longestEdgeOf(bestTour));
            Tour localSearched = bestTour.localSearch(CostMatrix.getInstance());
            System.out.println("Local search yields " + localSearched + " with longest edge " + CostMatrix.getInstance().longestEdgeOf(localSearched));
            System.out.println("Population size: " + population.size());


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