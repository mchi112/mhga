package mchi112.hga;

import java.util.*;

public class Migration {

    public static void mechanism1(float multiplier, List<List<Tour>> subpopulation) throws Exception {

        if (!(subpopulation.size() > 1)) {
            throw new Exception("Migration only possible between two or more subpopulations");
        }

        if (multiplier > 1 || multiplier < 0) {
            throw new Exception("Probability multiplier must be between 0 and 1 inclusive");
        }

        final int POPULATION_SIZE = subpopulation.get(0).size();
        
        float[] fitness = new float[subpopulation.size()];
        for (int i = 0; i < subpopulation.size(); i++) {
            float totalFitness = 0;
            for (int j = 0; j < subpopulation.get(i).size(); j++) {
                totalFitness += subpopulation.get(i).get(j).getFitness();
            }
            fitness[i] = totalFitness / subpopulation.get(i).size();
        }

        float maxFitness = max(fitness);
        float minFitness = min(fitness);

        if (max(fitness) == min(fitness)) {
            return;
        }

        float[] proportionTaken = new float[fitness.length];
        for (int i = 0; i < fitness.length; i++) {
            proportionTaken[i] = ((maxFitness - fitness[i]) / (maxFitness - minFitness)) * multiplier;
        }

        Random random = new Random();
        List<Tour> migrationPool = new LinkedList<Tour>();
        for (int i = 0; i < subpopulation.size(); i++) {
            int subpopulationSize = subpopulation.get(i).size();
            for (int j = 0; j < proportionTaken[i] * subpopulationSize; j++) {
                int removedIndex = random.nextInt(subpopulation.get(i).size());
                migrationPool.add(subpopulation.get(i).remove(removedIndex));
            }
        }

//        List<List<Tour>> subpopulationList = Arrays.asList(subpopulation);
        while (migrationPool.size() > 0) {
            int pickedIndex = random.nextInt(subpopulation.size());
            subpopulation.get(pickedIndex).add(migrationPool.remove(0));
            if (subpopulation.get(pickedIndex).size() == POPULATION_SIZE) {
                subpopulation.remove(pickedIndex);
            }
        }
    }

    private static float max(float[] array) {
        float max = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] > max) {
                max = array[i];
            }
        }
        return max;
    }

    private static float min(float[] array) {
        float min = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] < min) {
                min = array[i];
            }
        }
        return min;
    }
}
