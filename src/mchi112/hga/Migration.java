package mchi112.hga;

import java.util.*;

public class Migration {

    public static void mechanism1(float multiplier, List<Tour>... subpopulation) throws Exception {

        if (!(subpopulation.length > 1)) {
            throw new Exception("Migration only possible between two or more subpopulations");
        }

        if (multiplier > 1 || multiplier < 0) {
            throw new Exception("Probability multiplier must be between 0 and 1 inclusive");
        }

        final int POPULATION_SIZE = subpopulation[0].size();
        
        float[] fitness = new float[subpopulation.length];
        for (int i = 0; i < subpopulation.length; i++) {
            float totalFitness = 0;
            for (int j = 0; j < subpopulation[i].size(); j++) {
                totalFitness += subpopulation[i].get(j).getFitness();
            }
            fitness[i] = totalFitness / subpopulation[i].size();
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
        for (int i = 0; i < subpopulation.length; i++) {
            int subpopulationSize = subpopulation[i].size();
            for (int j = 0; j < proportionTaken[i] * subpopulationSize; j++) {
                int removedIndex = random.nextInt(subpopulation[i].size());
                migrationPool.add(subpopulation[i].remove(removedIndex));
            }
        }

        List<List<Tour>> subpopulationList = Arrays.asList(subpopulation);
        while (migrationPool.size() > 0) {
            int pickedIndex = random.nextInt(subpopulationList.size());
            subpopulationList.get(pickedIndex).add(migrationPool.remove(0));
            if (subpopulationList.get(pickedIndex).size() == POPULATION_SIZE) {
                subpopulationList.remove(pickedIndex);
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
