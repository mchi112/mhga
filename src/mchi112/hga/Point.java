package mchi112.hga;

public class Point {

    private float x;
    private float y;

    public Point(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float X() {
        return x;
    }

    public float Y() {
        return y;
    }

    public float getDistance(Point p) {
        float xDistance = Math.abs(X() - p.X());
        float yDistance = Math.abs(Y() - p.Y());
        double distance = Math.sqrt( (xDistance*xDistance) + (yDistance*yDistance) );

        return (float)distance;
    }
}
