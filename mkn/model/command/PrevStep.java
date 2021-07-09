package mkn.model.command;

import mkn.model.algorithm.GraphAlgo;
import mkn.model.algorithm.Snapshot;

public class PrevStep implements Command {
    private final GraphAlgo<?> algo;
    private final Snapshot prevState;

    public PrevStep(GraphAlgo<?> algo, Snapshot prevState) {
        this.algo = algo;
        this.prevState = prevState;
    }

    @Override
    public void execute() {
        algo.restore(prevState);
    }
}
