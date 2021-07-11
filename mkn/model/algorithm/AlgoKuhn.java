package mkn.model.algorithm;

import mkn.controller.Controller;
import mkn.model.command.Command;
import mkn.model.graph.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AlgoKuhn<T> implements GraphAlgo<T> {
    private Graph graph;
    private int [][] matrix;
    private boolean[] used; // Visit list
    private ArrayList<String> maxMatching = new ArrayList<>();
    private ArrayList<String> log = new ArrayList<>();
    int curStep = 0;
    int[] mt; // Array of index for max matching (default = [-1,...,-1])
    Command cmd;
    Controller controller;

    public AlgoKuhn() {}

    public void init(Graph g) {
        graph = g;
        mt = new int[graph.getSecond_share().length];
        matrix = graph.getBipartition_matrix();
        used = new boolean[graph.getFirst_share().length];
        Arrays.fill(mt, -1);
        maxMatching = new ArrayList<>();
        curStep = 0;
        log = new ArrayList<>();
        log.add("Algorithm is initialized");

        controller.update();
    }

    // Is it possible to find way?
    public boolean tryKuhn(int v, int[][] matrix, boolean[] used) {
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

    public void makePair() {
        char[] share1 = graph.getFirst_share();
        char[] share2 = graph.getSecond_share();
        for(int i = 0; i < mt.length; i++){
            char[] tmp = new char[]{share1[mt[i]], ' ', share2[i]};
            maxMatching.add(new String(tmp));
        }
    }

    public void printMaxMatching() {
        makePair();
        System.out.println("Max matching:");
        for (String s : maxMatching) {
            System.out.println(s);
        }
    }

    // Make adjacency matrix
    public static int[][] makeMatrix(ArrayList<String> s) {
        int[][] matrix = new int[s.size()][s.size()];
        String[] V = makeV(s);
        String tmp = "";
        for (int i = 0; i < s.size(); i++){
            int index = 0;
            for(int j = 0; j < s.get(i).length(); j++){
                if(s.get(i).charAt(j) != ' ')
                    tmp = tmp + s.get(i).charAt(j);
                else{
                    index = j + 1;
                    break;
                }
            }
            int coordX = Arrays.binarySearch(V, tmp);
            tmp = "";

            for(int j = index; j < s.get(i).length(); j++){
                if(s.get(i).charAt(j) != ' ')
                    tmp = tmp + s.get(i).charAt(j);
                else{
                    int coordY = Arrays.binarySearch(V, tmp);
                    matrix[coordX][coordY] = 1;
                    matrix[coordY][coordX] = 1;
                    tmp = "";
                }
            }
            int coordY = Arrays.binarySearch(V, tmp);
            matrix[coordX][coordY] = 1;
            matrix[coordY][coordX] = 1;
            tmp = "";
        }

        return matrix;
    }

    // Make V in alphabet order
    public static String[] makeV(ArrayList<String> s) {
        String[] V = new String[1];
        V = Arrays.copyOf(V, s.size());
        for (int i = 0; i < s.size(); i++){
            char[] tmp = new char[1];
            int tmpSize = 0;
            for(int j = 0; j < s.get(i).length(); j++){
                if(s.get(i).charAt(j) != ' '){
                    tmp = Arrays.copyOf(tmp, tmpSize + 1);
                    tmp[tmpSize] = s.get(i).charAt(j);
                    tmpSize++;
                }
                else
                    break;
            }
            V[i] = new String(tmp);
        }

        Arrays.sort(V);
        return V;
    }

    @Override
    public void nextStep() {
        curStep++;
        Arrays.fill(used, false);
        tryKuhn(curStep, matrix, used);
        char tmp = graph.getFirst_share()[curStep];
        log.add("Vertex " + tmp + " is processed");
        controller.update();
    }

    @Override
    public void executeCmd() {
        cmd.execute();
    }

    @Override
    public boolean isEndReached() {
        return curStep + 1 == graph.getFirst_share().length;
    }

    @Override
    public boolean isDataCorrect(String path) { //rewrite!
        try (FileReader file = new FileReader(path)) {
            Scanner scan = new Scanner(file);
            ArrayList<String> aa = new ArrayList<>();
            while (scan.hasNextLine()){//read graph from file
                String read_string = scan.nextLine();
                boolean check = isCorrect(read_string);
                if (!check){
                    System.out.println("The file contains invalid data!");
                    return false;
                }
                aa.add(read_string);
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return false;
        }
        return true;
    }

    public static boolean isCorrect(String str){ //correct string format: V:A B C...
        String pattern = "[A-Za-z0-9][\s[A-Za-z0-9]]*";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(str);
        boolean isMatch = m.find();
        int start = m.start();
        int finish = m.end();
        return isMatch && start == 0 && finish == str.length();
    }

    @Override
    public void readData(String filename) throws IOException {//check!
        try(FileReader file = new FileReader(filename)){
            Scanner scan = new Scanner(file);
            ArrayList<String> aa = new ArrayList<>();
            while (scan.hasNextLine()){//read graph from file
                String read_string = scan.nextLine();
                aa.add(read_string);
            }

            int[][] b = makeMatrix(aa);//make adjacency matrix
            String[] ver_2 = makeV(aa);//make V in alphabet order
            int size = aa.size();
            Graph gr = new Graph<String>(b, size, ver_2, false);
            log.add("Data is read");
            controller.update();
            init(gr);
        }
        catch (IOException e){
            System.out.println("IOException");
            e.printStackTrace();
        }
    }

    @Override
    public void reset() {
        graph = null;
        matrix = null;
        used = null;//visit list
        maxMatching = new ArrayList<>();
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
    public String getImage() {
        try (PrintWriter writer = new PrintWriter("./graph.gv")) {
            writer.println("graph G");

        } catch (IOException e) {
            System.out.println(e.getMessage());
            return "";
        }
        return "";
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
        return new AlgoSnapshot(graph, matrix, used, maxMatching, log, curStep, mt, cmd, controller);
    }

    @Override
    public void restore(Snapshot snap) {
        AlgoKuhn.AlgoSnapshot snapshot = (AlgoKuhn.AlgoSnapshot) snap;
        this.graph = snapshot.gr;
        this.matrix = snapshot.gr.getBipartition_matrix();
        this.used = Arrays.copyOf(snapshot.used, snapshot.used.length);
        this.maxMatching = new ArrayList<>(snapshot.max_matching);
        this.log = new ArrayList<>(snapshot.log);
        curStep = snapshot.curStep;
        this.mt = Arrays.copyOf(snapshot.mt, snapshot.mt.length);
        this.cmd = snapshot.cmd;
        // this.controller = snapshot.controller;

        controller.update();
    }

    public static class AlgoSnapshot implements Snapshot {
        private final Graph gr;
        private final int [][] matrix;
        private final boolean[] used;//visit list
        private final ArrayList<String> max_matching;
        private final ArrayList<String> log;
        private int curStep;
        int[] mt;//array of index for max matching (default = [-1,...,-1])
        Command cmd;
        // Controller controller;
        public AlgoSnapshot(Graph gr, int[][] matrix, boolean[] used, ArrayList<String> max_matching,
                            ArrayList<String> log, int curStep, int[] mt, Command cmd, Controller controller) {
            this.gr = gr;
            this.matrix = gr.getBipartition_matrix(); //Bipartition_matrix is const
            this.used = Arrays.copyOf(used, used.length);
            this.max_matching = new ArrayList<>(max_matching);
            this.log = new ArrayList<>(log);
            this.curStep = curStep;
            this.mt = Arrays.copyOf(mt, mt.length);
            this.cmd = cmd;
            // this.controller = controller;
        }
    }
}