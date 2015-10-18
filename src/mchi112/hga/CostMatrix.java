package mchi112.hga;

public class CostMatrix {


    public static void init(float[][] matrix) throws Exception {
        instance = new CostMatrix(matrix);
    }

    public static CostMatrix getInstance() {
        return instance;
    }

    private static CostMatrix instance;
    private float[][] matrix;
    private int size;

    private CostMatrix(float[][] matrix) throws Exception {

        for (int i = 0; i < matrix.length; i++) {
            if (matrix.length != matrix[i].length) {
                throw new Exception("Cost matrix must be a square matrix");
            }
        }
        this.matrix = matrix;
        this.size = matrix.length;
    }

    public float getDistance(int source, int destination) throws IndexOutOfBoundsException {
        if (source < 1 || source > this.size) {
            throw new IndexOutOfBoundsException("Source " + source + " is out of bounds for cost matrix of size " + this.size);
        }
        if (destination < 1 || destination > this.size) {
            throw new IndexOutOfBoundsException("Destination " + destination + " is out of bounds for cost matrix of size " + this.size);
        }
        return matrix[source-1][destination-1];
    }

    public int getSize() {
        return this.size;
    }

    public float longestEdgeOf(Tour tour) throws Exception{
        if (tour.isPartial()) {
            throw new Exception("Cannot find edge of partial tour: " + tour);
        }
        float longest = -1;
        for (int i = 0; i < tour.getLength(); i++) {
            int source = tour.get(i);
            int dest = tour.get((i+1)%tour.getLength());
            longest = Math.max(longest, getDistance(source, dest));
        }
        return longest;
    }
}
