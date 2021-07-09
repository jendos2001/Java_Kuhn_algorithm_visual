package mkn.controller;

import mkn.model.algorithm.GraphAlgo;
import mkn.model.algorithm.Snapshot;
import mkn.model.command.NextStep;
import mkn.model.command.PrevStep;
import mkn.view.View;

import java.util.ArrayList;

/**
 * This class controls interaction between View (GUI and CLI) and Model (Kuhn's algorithm)
 * Terms of use: <code>algo</code> and <code>view</code> must be initialized with setters.
 */
public class AlgoViewController implements Controller {
    private GraphAlgo<?> algo = null;
    private View view = null;
    private ArrayList<Snapshot> states = new ArrayList<>();

    /**
     * Index of the previous state in <code>states</code>
     * <code>-1</code> means that <code>states</code> is empty
     */
    private int prevStateIndex = -1;

    @Override
    public void setView(View view) {
        this.view = view;
    }

    @Override
    public void setAlgo(GraphAlgo<?> algo) {
        this.algo = algo;
    }

    @Override
    public void saveState() {
        states.add(algo.save());
        prevStateIndex++;
    }

    @Override
    public void toStart() {
        if (prevStateIndex > -1) {
            algo.setCommand(new PrevStep(algo, states.get(0)));
            algo.executeCmd();
            view.update();
            prevStateIndex = -1; // Start from the beginning with same data
        }
    }

    @Override
    public void toFinish() {
        while (!algo.isEndReached()) {
            nextStep();
        }
    }

    @Override
    public boolean nextStep() {
        saveState();
        algo.setCommand(new NextStep(algo));
        algo.executeCmd();
        view.update();
        return algo.isEndReached();
    }

    @Override
    public boolean prevStep() {
        if (prevStateIndex > -1) {
            algo.setCommand(new PrevStep(algo, states.get(prevStateIndex)));
            algo.executeCmd();
            prevStateIndex--;
            view.update();
        }
        return prevStateIndex == -1;
    }

    @Override
    public boolean getNewData(String path) {
        return false;
    }

    @Override
    public void deleteOld() {

    }
}
