package mkn.model.command;

import mkn.model.algorithm.GraphAlgo;

public class NextStep<T> implements Command {
    private GraphAlgo<T> algo;

    public NextStep(GraphAlgo<T> algo) { this.algo = algo; }

    @Override
    public void execute() {
        algo.nextStep();
        return;
    }
}
