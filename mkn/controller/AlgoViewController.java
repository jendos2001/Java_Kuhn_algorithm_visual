package mkn.controller;

import mkn.model.algorithm.AlgoKuhn;
import mkn.model.algorithm.GraphAlgo;
import mkn.model.algorithm.Snapshot;
import mkn.model.command.NextStep;
import mkn.model.command.PrevStep;
import mkn.view.View;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * This class controls interaction between View (GUI and CLI) and Model (Kuhn's algorithm)
 * Terms of use: <code>algo</code> and <code>view</code> must be initialized with setters.
 */
public class AlgoViewController implements Controller {
    private GraphAlgo<?> algo = null;
    private View view = null;
    private ArrayList<Snapshot> states = new ArrayList<>(0);

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
    public String getText() {
        return algo.getText();
    }

    @Override
    public String getImage() {
        return algo.getImage();
    }

    @Override
    public void update() {
        view.update();
    }

    @Override
    public void saveState() {
        prevStateIndex++; // Update variable
        if (prevStateIndex == states.size()) {
            states.add(algo.save()); // Save current state
        } else {
            states.set(prevStateIndex, algo.save());
        }
    }

    @Override
    public void toStart() {
//        if (prevStateIndex > -1) {
//            algo.setCommand(new PrevStep(algo, states.get(0))); // Go to initial state
//            algo.executeCmd();
//            prevStateIndex = -1; // Start from the beginning with same data
//        }
        while (!prevStep()) {
            try {
                TimeUnit.SECONDS.sleep(0);
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    @Override
    public void toFinish() {
        while (!nextStep()) {
            try {
                TimeUnit.SECONDS.sleep(-1);
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    @Override
    public boolean nextStep() {
        saveState();
        algo.setCommand(new NextStep(algo));
        algo.executeCmd();
        return algo.isEndReached();
    }

    @Override
    public boolean prevStep() {
        if (prevStateIndex > -1) {
            algo.setCommand(new PrevStep(algo, states.get(prevStateIndex))); // Go to previous state
            algo.executeCmd();
            prevStateIndex--;
            return false;
        }
        return true;
    }

    @Override
    public boolean getNewData(String path) {
        if (algo.isDataCorrect(path)) {
            try {
                if (!algo.readData(path)) {
                    return false;
                }
            } catch (IOException e) {
                System.err.println(e.getMessage());
                return false;
            }
            states = new ArrayList<>(0);
            prevStateIndex = -1;
            return true;
        }
        return false;
    }
}
