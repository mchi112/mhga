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

    private Random random;

    public SCS(int count, CostMatrix costMatrix) {
        this.count = count;
        this.costMatrix = costMatrix;
    }

    public List<Tour> generatePopulation() throws Exception {
        // Step 0
        init();

        for (int attempt = 0; attempt < count; attempt++) {
            // Step 1
            Tour tour = new Tour(costMatrix.getSize());
            tour.add(1);

            for (int i = 0; i < this.count-1; i++) {
                // Step 2
                Integer next = getProbabilisticNextNode(tour);
                // Step 3
                float blockLeaderBound = calculateBlockLeaderBound(tour, next);

                // Step 4
                if (blockLeaderBound >= bestSolution) {
                    // Step 5
                    if (i == this.count - 2){
                        // Ran out of attempts. Generate another tour from scratch.
                        break;
                    }
                    continue;
                } else {
                    // Step 4: part B
                    tour.add(next);
                    i = -1; // Reset loop for searching next node in the tour
                    // Step 6
                    if (!tour.isPartial()) {
                        population.add(tour);
                        float longestEdge = costMatrix.longestEdgeOf(tour);
                        // Step 7
                        if (longestEdge < bestSolution) {
                            bestSolution = longestEdge;
                            // In the SCS paper, we normally stop and return the population here, but
                            // we modify this for the full blown HGA as the SCS paper only uses this algorithm
                            // to generate solutions immediately without using GA
                        }
                        else {
                            // Step 9
                            if (population.size() == this.count) {
                                return population;
                            }
                        }
                        break;
                    }
                    else {
                        // Step 8: do nothing
                    }
                }
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
        for (int i = 0; i < alphabetTable.getSize(); i++) {
            float prob = (float)(2 * (alphabetTable.getSize() - (i + 1.0) + 1)) / (alphabetTable.getSize() * (alphabetTable.getSize() + 1));
            probSelection[i] = prob;
        }

        this.random = new Random();
    }

    /**
     * A slight modification to Ahmed's algorithm that achieves the same effect with far less redundant checks
     */
    private Integer getProbabilisticNextNode(Tour tour) throws Exception {
        if (!tour.isPartial()) {
            throw new Exception("This should not be called! Tour " + tour + " is already complete!");
        }
        List<NodeValuePair> legitNodes = new LinkedList<NodeValuePair>();
        List<Float> cumulativeProb = new LinkedList<Float>();

        for (int i = 0; i < alphabetTable.getSize(); i++) {
            if (!tour.isVisitedAlready(alphabetTable.getNodeValuePair(tour.getLatestNode(), i).getNode())) {
                legitNodes.add(alphabetTable.getNodeValuePair(tour.getLatestNode(), i));
                cumulativeProb.add(cumulativeProb.size() == 0 ?
                        probSelection[i] :
                        probSelection[i] + cumulativeProb.get(cumulativeProb.size() - 1));
            }
        }
        float randomVal = (float)(random.nextDouble() * cumulativeProb.get(cumulativeProb.size()-1));

        int i = 0;
        while (randomVal > cumulativeProb.get(i)) {
            if (i < cumulativeProb.size() - 1) {
                i++;
            }
            else {
                break;
            }
        }
        return legitNodes.get(i).getNode();
    }

    private float calculateBlockLeaderBound(Tour partial, int node) throws Exception {
        float maxVal = -1;
        for (int i = 0; i < alphabetTable.getSize(); i++) {
            if (!partial.isVisitedAlready(i+1)) {
                maxVal = alphabetTable.getNodeValuePair(i+1, 0).getValue() > maxVal ?
                        alphabetTable.getNodeValuePair(i+1, 0).getValue() :
                        maxVal;
            }
        }

        if (maxVal == -1) {
            throw new Exception("maxVal should not be -1! This was called when the tour was already complete");
        }
        return maxVal;
    }
}
