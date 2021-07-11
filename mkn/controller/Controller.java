package mkn.controller;

import mkn.model.algorithm.GraphAlgo;
import mkn.view.View;

public interface Controller {
    void setView(View view);
    void setAlgo(GraphAlgo<?> algo);
    void setStartVertex(String sv);

    String getText();
    String getImage();

    void update();

    /**
     * Save current state of the algorithm
     */
    void saveState();

    void toStart();
    void toFinish();
    boolean nextStep();
    boolean prevStep();
    boolean getNewData(String path);
}