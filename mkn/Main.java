package mkn;

import mkn.controller.*;
import mkn.view.*;

public class Main {
    static class Test<T> {
        private T t;

        public Test(T t) { this.t = t; }

        public void print() { System.out.println(t); }
    }

    public static void main(String[] args) {
        Test<Integer> test = new Test<>(10);
        Test<?> t;
        t = test;
        t.print();

        // Initialization
//        Controller controller = new AlgoViewController();
//        View view = new GUI();
//        view.setController(controller);
//        controller.setView(view);
//
//        view.exec();
    }
}
