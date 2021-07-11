package mkn.model.algorithm;

import mkn.controller.Controller;
import mkn.model.command.Command;

import java.io.IOException;

public interface GraphAlgo<T> {
    void executeCmd();
    void nextStep();
    boolean isEndReached();
    boolean isDataCorrect(String path);
    boolean readData(String path) throws IOException;
    void reset(); // Set to null all variables in the algorithm class

    String getText();
    String getImage();

    void setCommand(Command cmd);
    void setController(Controller controller);

    Snapshot save();

    /**
     * This method restore older state of the algorithm.
     * In the implementation you have to cast <code>Snapshot</code> to corresponding snapshot type of actual class
     * in order to have access to private fields.
     * @param state current state of algorithm
     */
    void restore(Snapshot state);
}