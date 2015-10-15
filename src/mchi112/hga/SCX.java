package mchi112.hga;


public class SCX {

    private CostMatrix costMatrix;

    public SCX(CostMatrix costMatrix) {
        this.costMatrix = costMatrix;
    }


    public Tour crossover(Tour parent1, Tour parent2) throws Exception {

        Tour child = new Tour(costMatrix.getSize());
        Tour candidateParent;
        int candidateNodeIndex;
        float distance1, distance2;

        // partially construct new tour based on the parent's first two node
        distance1 = costMatrix.getDistance(parent1.get(0), parent1.get(1));
        distance2 = costMatrix.getDistance(parent2.get(0), parent2.get(1));

        candidateParent = distance1 < distance2 ? parent1 : parent2;
        child.add(candidateParent.get(0));
        child.add(candidateParent.get(1));

        // For the rest of the nodes...
        int i = 1;
        while(child.isPartial()) {

            Integer city =  child.get(i);
            Integer nextCity;

            // find position of the city in parent1 and locate the next one
            int parent1NodeIndex = parent1.indexOf(city);
            int parent1NextIndex = (parent1NodeIndex+1) % parent1.getLength();
            nextCity = parent1.get(parent1NextIndex);

            // probe parent1 for legit node
            int loops = 0;
            while(child.isVisitedAlready(nextCity)) {
                parent1NextIndex = (parent1NextIndex+1) % parent1.getLength();
                nextCity = parent1.get(parent1NextIndex);
            }

            // find position of the city in parent2 and locate the next one
            int parent2NodeIndex = parent2.indexOf(city);
            int parent2NextIndex = (parent2NodeIndex+1) % parent2.getLength();
            nextCity = parent2.get(parent2NextIndex);

            // probe parent2 for legit node
            while(child.isVisitedAlready(nextCity)) {
                parent2NextIndex = (parent2NextIndex+1) % parent2.getLength();
                nextCity = parent2.get(parent2NextIndex);
            }

            distance1 = (int) costMatrix.getDistance(parent1.get(parent1NodeIndex), parent1.get(parent1NextIndex));
            distance2 = (int) costMatrix.getDistance(parent2.get(parent2NodeIndex), parent2.get(parent2NextIndex));

            candidateParent = distance1 < distance2 ? parent1 : parent2;
            candidateNodeIndex = distance1 < distance2 ? parent1NextIndex : parent2NextIndex;

            child.add(candidateParent.get(candidateNodeIndex));
            i++;
        }

        return child;
    }
}
