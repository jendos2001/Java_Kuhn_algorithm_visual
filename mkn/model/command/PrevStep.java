package mkn.model.command;

import mkn.model.algorithm.AlgoKuhn;
import mkn.model.algorithm.GraphAlgo;
import mkn.model.algorithm.Snapshot;

public class PrevStep implements Command {
    private final GraphAlgo<?> algo;
    private final AlgoKuhn.AlgoSnapshot prevState;

    public PrevStep(GraphAlgo<?> algo, AlgoKuhn.AlgoSnapshot prevState) {
        this.algo = algo;
        this.prevState = prevState;
    }

    @Override
    public void execute() {
        algo.restore(prevState);
    }
}
