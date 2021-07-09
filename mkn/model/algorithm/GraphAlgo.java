package mkn.model.algorithm;

import mkn.model.command.Command;

public interface GraphAlgo<T> {
    void setCommand(Command cmd);
    void executeCmd();

    boolean isEndReached();

    Snapshot save();

    /**
     * This method restore older state of the algorithm.
     * In the implementation you have to cast <code>Snapshot</code> to corresponding snapshot type of actual class
     * in order to have access to private fields.
     * @param state current state of algorithm
     */
    void restore(Snapshot state);
}
