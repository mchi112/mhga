package mchi112.task;

import mchi112.hga.CostMatrix;
import mchi112.hga.SCS;
import mchi112.hga.Tour;
import mchi112.model.MultiProcessResult;

import java.util.List;
import java.util.concurrent.Callable;

public class PopulationGenerationTask implements Callable<MultiProcessResult> {

    SCS scs;
    Integer populationSize;

    public PopulationGenerationTask(Integer populationSize) {

        this.scs = new SCS(CostMatrix.getInstance());
        this.populationSize = populationSize;
    }

    @Override
    public MultiProcessResult call() throws Exception {

        List<Tour> population = scs.generatePopulation(populationSize);

        Tour best = null;
        for (Tour t : population) {
            if (best == null ||
                    CostMatrix.getInstance().longestEdgeOf(t) <
                            CostMatrix.getInstance().longestEdgeOf(best)) {
                best = t;
            }
        }

        return new MultiProcessResult(population, best);
    }
}
