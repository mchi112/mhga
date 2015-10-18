package mchi112.hga;

public class Point {

    private int x;
    private int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int X() {
        return x;
    }

    public int Y() {
        return y;
    }

    public float getDistance(Point p) {
        int xDistance = Math.abs(X() - p.X());
        int yDistance = Math.abs(Y() - p.Y());
        double distance = Math.sqrt( (xDistance*xDistance) + (yDistance*yDistance) );

        return (float)distance;
    }
}
