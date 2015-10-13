package mchi112.hga;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: hashmap to determine if a node to be added is sitll legitimate (i.e. not currently in the partial)
 */
public class Tour {
    private int maxLength;
    private List<Integer> tour;

    public Tour(int maxLength) {
        this.maxLength = maxLength;
        this.tour = new ArrayList<Integer>();
    }

    public void add(Integer node) throws Exception {
        if (tour.size() == maxLength) {
            throw new Exception("Cannot add node: size is already at maximum value of " + maxLength);
        }
        tour.add(node);
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
