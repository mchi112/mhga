package mchi112.hga;

public class NodeValuePair {
    private int node;
    private float value;

    public NodeValuePair(int node, float value) {
        this.node = node;
        this.value = value;
    }

    public int getNode() {
        return this.node;
    }

    public float getValue() {
        return this.value;
    }

    public String toString() {
        return "(N: " + this.node + ", V: " + this.value + ")";
    }
}
