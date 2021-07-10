package mkn;

import mkn.controller.*;
import mkn.model.algorithm.GraphAlgo;
import mkn.view.*;

public class Main {
    public static void main(String[] args) {
        Controller controller = new AlgoViewController();
        View view = new GUI();
        // GraphAlgo algo = new AlgoKuhn();

        view.setController(controller);
        controller.setView(view);

        view.exec();
    }
}
