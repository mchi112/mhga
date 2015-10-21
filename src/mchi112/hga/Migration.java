package mchi112.hga;

import java.util.*;

public class Migration {

    public static void mechanism1(float multiplier, List<List<Tour>> subpopulations) throws Exception {

        if (!(subpopulations.size() > 1)) {
            throw new Exception("Migration only possible between two or more subpopulations");
        }

        if (multiplier > 1 || multiplier < 0) {
            throw new Exception("Probability multiplier must be between 0 and 1 inclusive");
        }

        List<List<Tour>> subpopulationsCopy = new ArrayList<List<Tour>>();

        for (List<Tour> subpopulation : subpopulations) {
            subpopulationsCopy.add(subpopulation);
        }

        final int POPULATION_SIZE = subpopulationsCopy.get(0).size();

        for (int i = 1; i < subpopulationsCopy.size(); i++) {
            if (subpopulationsCopy.get(i).size() != POPULATION_SIZE) {
                throw new Exception("Subpopulations do not have matching sizes: subpop 0 has " + POPULATION_SIZE + ", subpop " + i + " has " + subpopulationsCopy.get(i).size());
            }
        }
        
        float[] fitness = new float[subpopulationsCopy.size()];
        for (int i = 0; i < subpopulationsCopy.size(); i++) {
            float totalFitness = 0;
            for (int j = 0; j < subpopulationsCopy.get(i).size(); j++) {
                totalFitness += subpopulationsCopy.get(i).get(j).getFitness();
            }
            fitness[i] = totalFitness / subpopulationsCopy.get(i).size();
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
        for (int i = 0; i < subpopulationsCopy.size(); i++) {
            int subpopulationSize = subpopulationsCopy.get(i).size();
            for (int j = 0; j < proportionTaken[i] * subpopulationSize; j++) {
                int removedIndex = random.nextInt(subpopulationsCopy.get(i).size());
                migrationPool.add(subpopulationsCopy.get(i).remove(removedIndex));
            }
        }

//        List<List<Tour>> subpopulationList = Arrays.asList(subpopulation);
        while (migrationPool.size() > 0) {
            int pickedIndex = random.nextInt(subpopulationsCopy.size());
            subpopulationsCopy.get(pickedIndex).add(migrationPool.remove(0));
            if (subpopulationsCopy.get(pickedIndex).size() == POPULATION_SIZE) {
                subpopulationsCopy.remove(pickedIndex);
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
