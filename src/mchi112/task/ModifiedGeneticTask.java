package mchi112.task;

import mchi112.hga.CostMatrix;
import mchi112.hga.SCX;
import mchi112.hga.SRS;
import mchi112.hga.Tour;
import mchi112.model.MultiProcessResult;

import java.util.List;
import java.util.concurrent.Callable;

public class ModifiedGeneticTask implements Callable<MultiProcessResult> {

    private List<Tour> population;
    private Tour bestSolutionValue;

    private Integer maxGeneration;
    private Integer migrationThreshold;

    private CostMatrix costMatrix;
    private SCX scx;
    private SRS srs;



    public ModifiedGeneticTask(List<Tour> population, Tour bestSolutionValue, Integer migrationThreshold) {

        costMatrix = CostMatrix.getInstance();
        scx = new SCX(costMatrix);
        srs = new SRS();

        this.population = population;
        this.bestSolutionValue = bestSolutionValue;
        this.migrationThreshold = migrationThreshold;
    }

    @Override
    public MultiProcessResult call() throws Exception {
        int generation = 0;
        while(generation < migrationThreshold) {

            // Reproduce
            population = srs.reproduce(population);

            // Crossover
            for(int i = 0; i < population.size(); i++) {
                int j = (i+1)%population.size();

                Tour parent1 = population.get(i);
                Tour parent2 = population.get(j);

                if(parent1.equals(parent2)){
                    continue;
                }

                Tour child = scx.crossover(parent1, parent2);
                if(child.getFitness() > parent1.getFitness()) {
                    population.set(i, child);
                }
            }

            // Mutation
            for(Tour t : population) {
                t.mutate();
            }

            // Evaluate
            Tour bestPopulationValue = null;
            for (Tour t : population) {
                if (bestPopulationValue == null ||
                        costMatrix.longestEdgeOf(t) < costMatrix.longestEdgeOf(bestPopulationValue)) {
                    bestPopulationValue = t;
                }
            }

            // Check & perform local search if necessary
            if(costMatrix.longestEdgeOf(bestPopulationValue) < costMatrix.longestEdgeOf(bestSolutionValue)) {
                bestPopulationValue = bestPopulationValue.localSearch(CostMatrix.getInstance());
                bestSolutionValue = bestPopulationValue;
            }

            generation++;
        }

        return new MultiProcessResult(population, bestSolutionValue);
    }
}
