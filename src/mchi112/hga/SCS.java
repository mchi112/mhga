package mchi112.hga;

import java.util.LinkedList;
import java.util.List;

public class SCS {

    /**
     * NOTE: this will have to be redone so we can easily break up the problem. This means the class will probably have to maintain state.
     * For each partial tour, maintain a list of nodes that are
     */
    public static List<Tour> generatePopulation(int count, CostMatrix costMatrix) throws Exception {
        /*
        List tours = new LinkedList<Tour>();
        int p; // Current node

        // Step 0
        float bestSolution = 5000;
        AlphabetTable alphabetTable = new AlphabetTable(costMatrix);
        float overallLowerBound = alphabetTable.getOverallLowerBound();

        // Generate probability of selection from each column
        float[] probSelection = new float[];
        for (int i = 0; i < alphabetTable.getSize(); i++) {
            probSelection[i] =
                (2 * (alphabetTable.getSize() - (i+1) + 1)) / (alphabetTable.getSize() * (alphabetTable.getSize() + 1));
        }

        // Step 1
        Tour tour = new Tour(alphabetTable.getSize());
        p = 1;

        // LOOP STARTS HERE
        tour.add(p);

        // Step 2
        */
        return null;
    }
}
