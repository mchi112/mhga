package mchi112.hga;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * TODO: add boolean 'isPartial', and add lazy+memoised fitness calculation for non-partial tours
 */
public class Tour implements Iterable<Integer> {
    private int maxLength;
    private List<Integer> tour;
    private boolean[] visited;
    private float expectedCount;

    public Tour(int maxLength) {
        this.maxLength = maxLength;
        this.tour = new ArrayList<Integer>();
        this.visited = new boolean[maxLength];
    }

    public Tour(Iterable<Integer> nodes) {
        tour = new ArrayList<Integer>();
        for (Integer node : nodes) {
            tour.add(node);
        }
        this.maxLength = tour.size();
        this.visited = new boolean[this.maxLength];
        for (int i = 0; i < this.visited.length; i++) {
            this.visited[i] = true;
        }
    }

    public void add(Integer node) throws Exception {
        if (tour.size() == maxLength) {
            throw new Exception("Cannot add node: size is already at maximum value of " + maxLength);
        }
        if (visited[node-1]) {
            throw new Exception("Node " + node + " has already been visited in tour " + this.tour);
        }
        tour.add(node);
        visited[node-1] = true;
    }

    // This method is probably better off in CostMatrix but w/e
    public Tour localSearch(CostMatrix costMatrix) throws Exception {
        Tour best = makeCopy();

        List<Integer> mutatedList;
        Tour mutated;
        for (int i = 1; i < maxLength-1; i++) {
            for (int j = i+1; j < maxLength; j++) {
                // Check insert
                mutatedList = best.getTourListCopy();
                mutatedList.add(j, mutatedList.get(i));
                mutatedList.remove(i);
                mutated = new Tour(mutatedList);
                if (costMatrix.longestEdgeOf(mutated) < costMatrix.longestEdgeOf(best)) {
                    best = mutated;
                }

                // Invert sublist
                mutatedList = best.getTourListCopy();
                int iterCount = (i-j) % 2 == 0 ? (i-j)/2 : ((i-j)/2)+1;
                for (int a = 0; a < iterCount; a++) {
                    Integer tmp = mutatedList.get(i+a);
                    mutatedList.set(i+a, mutatedList.get(j-a));
                    mutatedList.set(j-a, tmp);
                }
                mutated = new Tour(mutatedList);
                if (costMatrix.longestEdgeOf(mutated) < costMatrix.longestEdgeOf(best)) {
                    best = mutated;
                }

                // Swap mutation
                mutatedList = best.getTourListCopy();
                mutated = new Tour(mutatedList);
                mutated.swap(i, j);
                if (costMatrix.longestEdgeOf(mutated) < costMatrix.longestEdgeOf(best)) {
                    best = mutated;
                }
            }
        }

        return best;
    }

    public List<Integer> getTourListCopy() {
        List<Integer> copy = new ArrayList<Integer>();
        for (Integer node : tour) {
            copy.add(node);
        }
        return copy;
    }

    public Tour makeCopy() throws Exception {
        Tour copy = new Tour(maxLength);
        for (Integer node : tour) {
            copy.add(node);
        }
        return copy;
    }

    public Tour makeCopy(Tour other) throws Exception {
        Tour copy = new Tour(maxLength);
        for (Integer node : other) {
            copy.add(node);
        }
        return copy;
    }

    // Naive implementation, for now
    public void mutate() throws Exception {
        if (this.isPartial()) {
            throw new Exception("Cannot mutate a partial tour: " + this);
        }
        Random random = new Random();
        int first = random.nextInt((maxLength));
        int second;
        do {
            // Yeah, this looks bad
            second = random.nextInt((maxLength));
        } while (second == first);

        swap(first, second);
    }

    private void swap(Integer first, Integer second) throws Exception {
        if (first == second) {
            throw new Exception("Cannot swap the same node. This does nothing");
        }
        Integer tmp = tour.get(first);
        tour.set(first, tour.get(second));
        tour.set(second, tmp);
    }

    public boolean isVisitedAlready(Integer node) {
        return visited[node-1];
    }

    public Integer get(int index) {
        return tour.get(index);
    }

    public int indexOf(Integer o) {
        return tour.indexOf(o);
    }

    public Integer getLatestNode() {
        if (tour.size() == 0) {
            return null;
        }
        return tour.get(tour.size() - 1);
    }

    public int getLength() {
        return this.tour.size();
    }

    public boolean isPartial() {
        return this.tour.size() != maxLength;
    }

    public float getFitness() throws Exception {
        return 1/CostMatrix.getInstance().longestEdgeOf(this);
    }

    public float getExpectedCount() {
        return expectedCount;
    }

    public void setExpectedCount(float expectedCount) {
        this.expectedCount = expectedCount;
    }

    @Override
    public int hashCode() {
        return tour.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Tour)) {
            return false;
        }
        Tour t = (Tour)obj;

        return this.tour.equals(t.tour);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("(");
        for (int i = 0; i < tour.size(); i++) {
            sb.append(tour.get(i).toString());
            if (i < tour.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append(")");
        return sb.toString();
    }

    @Override
    public Iterator<Integer> iterator() {
        return tour.iterator();
    }

}
