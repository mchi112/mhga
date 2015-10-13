package mchi112.hga;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: add boolean 'isPartial', and add lazy+memoised fitness calculation for non-partial tours
 */
public class Tour {
    private int maxLength;
    private List<Integer> tour;
    private boolean[] visited;

    public Tour(int maxLength) {
        this.maxLength = maxLength;
        this.tour = new ArrayList<Integer>();
        this.visited = new boolean[maxLength];
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

    public boolean isVisitedAlready(Integer node) {
        return visited[node-1];
    }

    public Integer get(int index) {
        return tour.get(index);
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
}
