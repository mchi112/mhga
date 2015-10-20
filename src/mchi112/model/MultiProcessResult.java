package mchi112.model;


import mchi112.hga.Tour;

import java.util.List;

public class MultiProcessResult {

    private List<Tour> population;
    private Tour bestSolutionValue;

    public MultiProcessResult(List<Tour> population, Tour bestSolutionValue) {
        this.population = population;
        this.bestSolutionValue = bestSolutionValue;
    }

    public List<Tour> getPopulation() {
        return population;
    }

    public Tour getBestSolutionValue() {
        return bestSolutionValue;
    }
}
