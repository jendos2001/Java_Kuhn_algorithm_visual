package mkn.view;

import mkn.controller.*;

public interface View {

    void update();

    void setController(Controller controller);

    void exec();
}