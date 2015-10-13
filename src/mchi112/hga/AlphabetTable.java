package mchi112.hga;

import java.util.Arrays;
import java.util.Comparator;

public class AlphabetTable {
    private NodeValuePair[][] alphabetTable;
    private int size;
    private float overallLowerBound = -1;

    public AlphabetTable(CostMatrix costMatrix) {
        this.size = costMatrix.getSize();
        this.alphabetTable = new NodeValuePair[this.size][this.size];

        for (int i = 0; i < this.size; i++) {
            for (int j = 0; j < this.size; j++) {
                // +1, as nodes start from 1, not 0
                this.alphabetTable[i][j] = new NodeValuePair(j+1, costMatrix.getDistance(i+1, j+1));
            }
            Arrays.sort(this.alphabetTable[i], new Comparator<NodeValuePair>() {
                public int compare(NodeValuePair nvp1, NodeValuePair nvp2) {
                    return (int)(nvp1.getValue() - nvp2.getValue());
                }
            });
        }
    }

    public NodeValuePair getNodeValuePair(int source, int index) throws IndexOutOfBoundsException {
        if (source < 1 || source > this.size) {
            throw new IndexOutOfBoundsException("Source " + source + " is out of bounds for cost matrix of size " + this.size);
        }
        if (index < 0 || index >= this.size) {
            throw new IndexOutOfBoundsException("Destination " + index + " is out of bounds for cost matrix of size " + this.size);
        }
        return alphabetTable[source-1][index];
    }

    public int getSize() {
        return this.size;
    }

    public float getOverallLowerBound() {
        if (overallLowerBound == -1) {
            this.overallLowerBound = alphabetTable[0][1].getValue();
            for (int i = 1; i < this.size; i++) {
                this.overallLowerBound = Math.max(this.overallLowerBound, alphabetTable[i][1].getValue());
            }
        }
        return this.overallLowerBound;
    }

}
