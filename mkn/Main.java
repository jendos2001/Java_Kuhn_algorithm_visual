package mkn;

import mkn.controller.*;
import mkn.model.algorithm.AlgoKuhn;
import mkn.model.algorithm.GraphAlgo;
import mkn.view.*;

import java.util.Locale;

public class Main {
    public static void main(String[] args) {
        Controller controller = new AlgoViewController();

        View view;
        if(args.length > 0) {
            switch (args[0].toLowerCase(Locale.ROOT)) {
                case "--cli":
                case "-c":
                    view = new CLI();
                    break;

                case "--gui":
                case "-g":
                    view = new GUI();
                    break;

                default:
                    view = new GUI();
            }
        }
        else
            view = new GUI();

        GraphAlgo<?> algo = new AlgoKuhn<Character>();

        controller.setView(view);
        controller.setAlgo(algo);

        view.setController(controller);
        algo.setController(controller);

        view.exec();
    }
}
