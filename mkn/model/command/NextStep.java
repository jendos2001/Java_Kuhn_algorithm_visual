package mkn.model.command;

import mkn.model.algorithm.GraphAlgo;

public class NextStep implements Command {
    private GraphAlgo<?> algo;

    public NextStep(GraphAlgo<?> algo) { this.algo = algo; }

    @Override
    public void execute() {
        algo.nextStep();
    }
}