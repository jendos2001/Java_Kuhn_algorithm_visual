package mkn.model.algorithm;

import mkn.controller.Controller;
import mkn.model.command.Command;
import mkn.model.graph.*;

import java.awt.*;
import java.awt.event.ContainerAdapter;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;


public class AlgoKuhn<T> implements GraphAlgo<T>{
    private Graph gr;
    private int [][] matrix;
    private boolean[] used;//visit list
    private ArrayList<String> max_matching = new ArrayList<>();
    private ArrayList<String> log = new ArrayList<>();
    static int curStep = 0;
    int[] mt;//array of index for max matching (default = [-1,...,-1])
    Command cmd;
    Controller controller;
    public AlgoKuhn(Graph g){
        gr = g;
        mt = new int[gr.getSecond_share().length];
        matrix = gr.getBipartition_matrix();
        used = new boolean[gr.getFirst_share().length];
        Arrays.fill(mt, -1);
        log.add("Алгоритм инициализирован!");
        controller.update();

    }

    /*public void Kuhn(){// Kuhn's algorithm
        while (curStep < gr.getFirst_share().length) {
            nextStep();
        }

    }*/

    public boolean tryKuhn(int v, int[][] matrix, boolean[] used){//is it possible to find way
        if(used[v])
            return false;
        used[v] = true;
        for(int i = 0; i < matrix[v].length; i++){
            if(matrix[v][i] != 0){
                if(mt[i] == -1 || tryKuhn(mt[i], matrix, used)){
                    mt[i] = v;
                    return true;
                }
            }
        }
        return false;
    }

    public void makePair(){
        char[] share1 = gr.getFirst_share();
        char[] share2 = gr.getSecond_share();
        for(int i = 0; i < mt.length; i++){
            char[] tmp = new char[]{share1[mt[i]], ' ', share2[i]};
            max_matching.add(new String(tmp));
        }
    }

    public void printMax_matching(){
        makePair();
        System.out.println("Max matching:");
        for (String s : max_matching) {
            System.out.println(s);
        }
    }

    public static int[][] makeMatrix(ArrayList<String> s){//make adjacency matrix
        int[][] matrix = new int[s.size()][s.size()];
        char[] V = makeV(s);
        for (String value : s) {
            int coordX = Arrays.binarySearch(V, value.charAt(0));
            for (int j = 2; j < value.length(); j += 2) {
                int coordY = Arrays.binarySearch(V, value.charAt(j));
                matrix[coordX][coordY] = 1;
                matrix[coordY][coordX] = 1;
            }
        }

        return matrix;
    }

    public static char[] makeV(ArrayList<String> s){//make V in alphabet order
        char[] V = new char[s.size()];
        for (int i = 0; i < s.size(); i++)
            V[i] = s.get(i).charAt(0);
        Arrays.sort(V);
        return V;
    }

    @Override
    public void nextStep(){
        curStep++;
        Arrays.fill(used, false);
        tryKuhn(curStep, matrix, used);
        char tmp = gr.getFirst_share()[curStep];
        log.add("Обработана вершина!" + tmp);
        controller.update();
    }

    @Override
    public void executeCmd() {
        cmd.execute();
    }

    @Override
    public boolean isEndReached() {
        return curStep == gr.getFirst_share().length;
    }

    @Override
    public boolean isDataCorrect(String str) {//rewrite!
        if (str.length() % 2 == 0)
            return false;
        if (str.charAt(0) > 90 || str.charAt(0) < 65)// A..Z
            return false;
        if (str.length() != 1){
            if (str.charAt(1) != 58)
                return false;
            for (int i = 2; i < str.length(); i++){
                if (i % 2 == 0) {
                    if (str.charAt(i) > 90 || str.charAt(i) < 65)
                        return false;
                }
                else {
                    if (str.charAt(i) != 32)
                        return false;
                }
            }
        }
        return true;
    }

    @Override
    public AlgoKuhn<T> readData(String filename) throws IOException{//check!
        try(FileReader file = new FileReader(filename)){
            Scanner scan = new Scanner(filename);
            ArrayList<String> aa = new ArrayList<>();
            while (scan.hasNextLine()){//read graph from file
                String read_string = scan.nextLine();
                aa.add(read_string);
            }
            file.close();
            int[][] b = makeMatrix(aa);//make adjacency matrix
            char[] ver_2 = makeV(aa);//make V in alphabet order
            int size = aa.size();
            Graph gr = new Graph(b, size, ver_2, false);
            log.add("Данные прочитаны!");
            controller.update();
            return new AlgoKuhn<>(gr);
        }
        catch (IOException e){
            System.out.println("IOException");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void reset() {
        gr = null;
        matrix = null;
        used = null;//visit list
        max_matching = new ArrayList<>();
        log = new ArrayList<>();
        curStep = 0;
        mt = null;//array of index for max matching (default = [-1,...,-1])
        cmd = null;
        controller = null;
    }

    @Override
    public String getText() {
        return log.get(log.size() - 1);
    }

    @Override
    public String getPathToImage() {
        return null;
    }

    @Override
    public void setCommand(Command cmd) {
        this.cmd = cmd;
    }

    @Override
    public void setController(Controller controller) {
        this.controller = controller;
    }

    @Override
    public Snapshot save(){
        return new AlgoSnapshot(gr, matrix, used, max_matching, log, curStep, mt, cmd, controller);
    }

    @Override
    public void restore(AlgoKuhn.AlgoSnapshot state) {
        this.gr = state.gr;
        this.matrix = state.gr.getBipartition_matrix();
        this.used = Arrays.copyOf(state.used, state.used.length);
        this.max_matching = new ArrayList<>(state.max_matching);
        this.log = new ArrayList<>(state.log);
        this.curStep = state.curStep;
        this.mt = Arrays.copyOf(state.mt, state.mt.length);
        this.cmd = state.cmd;
        this.controller = state.controller;

    }

    public class AlgoSnapshot implements Snapshot{
        private final Graph gr;
        private final int [][] matrix;
        private final boolean[] used;//visit list
        private final ArrayList<String> max_matching;
        private final ArrayList<String> log;
        private int curStep;
        int[] mt;//array of index for max matching (default = [-1,...,-1])
        Command cmd;
        Controller controller;
        public AlgoSnapshot(Graph gr, int[][] matrix, boolean[] used, ArrayList<String> max_matching, ArrayList<String> log,
                            int curStep, int[] mt, Command cmd, Controller controller){
            this.gr = gr;
            this.matrix = gr.getBipartition_matrix();//Bipartition_matrix is const
            this.used = Arrays.copyOf(used, used.length);
            this.max_matching = new ArrayList<>(max_matching);
            this.log = new ArrayList<>(log);
            this.curStep = curStep;
            this.mt = Arrays.copyOf(mt, mt.length);
            this.cmd = cmd;
            this.controller = controller;
        }
    }
}