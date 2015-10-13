package mchi112.hga;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class SCS {

    // Initialised by constructor
    private int count;
    private CostMatrix costMatrix;

    // Initialised when population is generated
    private List<Tour> population;
    private float bestSolution;
    private AlphabetTable alphabetTable;
    private float overallLowerBound;
    private float[] probSelection;
    private float probSelectionTotal;

    private Random random;
    private int p;

    public SCS(int count, CostMatrix costMatrix) {
        this.count = count;
        this.costMatrix = costMatrix;
    }

    /**
     * NOTE: this will have to be redone so we can easily break up the problem. This means the class will probably have to maintain state.
     * For each partial tour, maintain a list of nodes that are
     */
    public List<Tour> generatePopulation() throws Exception {
        // Step 0
        init();

        for (int i = 0; i < this.count; i++) {
            // Step 1
            Tour tour = new Tour(costMatrix.getSize());
            p = 1;
            tour.add(p);

            // Step 2
            Integer next = getProbabilisticNextNode(tour);

            // Step 3
            float blockLeaderBound = calculateBlockLeaderBound(tour, next);

            // Step 4
            if (blockLeaderBound >= bestSolution) {

            }
            else {
                
            }
        }

        return null;
    }

    private void init() {
        this.population = new LinkedList<Tour>();
        this.bestSolution = Float.MAX_VALUE;
        this.alphabetTable = new AlphabetTable(costMatrix);
        this.overallLowerBound = alphabetTable.getOverallLowerBound();

        // Generate probability of selection from each column
        this.probSelection = new float[costMatrix.getSize()];
        this.probSelectionTotal = 0;
        for (int i = 0; i < alphabetTable.getSize(); i++) {
            float prob = (float)(2 * (alphabetTable.getSize() - (i + 1.0) + 1)) / (alphabetTable.getSize() * (alphabetTable.getSize() + 1));
            probSelection[i] = prob;
            probSelectionTotal += prob;
        }

        this.random = new Random();
    }

    /**
     * TODO: Very unoptimised at the moment. We could optimise this by recalculating the probabilities for remaining nodes only
     * TODO: so we don't have to keep rolling the dice until we get a legit node.
     */
    private Integer getProbabilisticNextNode(Tour tour) throws Exception {
        float randomVal = (float)(random.nextDouble() * probSelectionTotal);
        int i = 0;

        while (randomVal >= 0) {
            NodeValuePair nvp = alphabetTable.getNodeValuePair(tour.getLatestNode(), i);
            randomVal -= probSelection[i];
            if (randomVal < 0) {
                if (tour.isVisitedAlready(nvp.getNode())) {
                    // Reroll
                    randomVal = (float)(random.nextDouble() * probSelectionTotal);
                    i = 0;
                }
                else {
                    return nvp.getNode();
                }
            }
            else {
                i++;
            }
        }

        throw new Exception("This should not be reached.");
    }

    private float calculateBlockLeaderBound(Tour partial, int node) {
        return 50; // TODO: placeholder
    }
}
