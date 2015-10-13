package mchi112;

import mchi112.hga.AlphabetTable;
import mchi112.hga.CostMatrix;
import mchi112.hga.SCS;
import mchi112.hga.Tour;

/**
 * We assume that nodes are labelled by ints starting from 1
 */
public class Program {
    private final int POPULATION_SIZE = 100;

    public static void main(String[] args) {
        try {
            // Test tour (1, 2, 3, 4, 5)
            Tour tour = new Tour(5);
            for (int i = 0; i < 5; i++) {
                tour.add(i + 1);
            }
            System.out.println(tour);

            // Test cost matrix 999.0
            int[][] matrix =
                    {{999, 77, 99, 9, 35, 63, 8},
                            {75, 999, 86, 46, 88, 29, 20},
                            {99, 86, 999, 16, 28, 35, 28},
                            {9, 46, 16, 999, 59, 53, 49},
                            {35, 88, 28, 59, 999, 76, 72},
                            {63, 29, 35, 53, 76, 999, 52},
                            {8, 20, 28, 49, 72, 52, 999}};
            CostMatrix costMatrix = new CostMatrix(matrix);
            System.out.println(costMatrix.getDistance(1, 1));

            // Test alphabet table
            AlphabetTable alphabetTable = new AlphabetTable(costMatrix);
            System.out.println(alphabetTable.getNodeValuePair(1, 0));
            System.out.println(alphabetTable.getNodeValuePair(7, 0));
            System.out.println(alphabetTable.getNodeValuePair(4, 4));
            System.out.println(alphabetTable.getNodeValuePair(7, 6));
            System.out.println(alphabetTable.getOverallLowerBound());

            SCS scs = new SCS(100, costMatrix);
            scs.generatePopulation();

            System.out.println("Success");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}