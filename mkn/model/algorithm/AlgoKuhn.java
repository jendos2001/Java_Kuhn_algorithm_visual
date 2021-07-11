package mkn.model.algorithm;

import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.model.MutableNode;
import mkn.controller.Controller;
import mkn.model.command.Command;
import mkn.model.graph.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import static guru.nidi.graphviz.model.Factory.*;
import guru.nidi.graphviz.attribute.*;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.engine.Graphviz;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AlgoKuhn<T> implements GraphAlgo<T> {
    private Graph graph;
    private int [][] matrix;
    private boolean[] used; // Visit list
    private ArrayList<String> maxMatching = new ArrayList<>();
    private ArrayList<String> log = new ArrayList<>();
    int curStep = -1;
    int[] mt; // Array of index for max matching (default = [-1,...,-1])
    Command cmd;
    Controller controller;

    public AlgoKuhn() {}

    public void init(Graph g) {
        graph = g;
        mt = new int[graph.getSecond_share().length];
        Arrays.fill(mt, -1);
        matrix = graph.getBipartition_matrix();
        used = new boolean[graph.getFirst_share().length];
        maxMatching = new ArrayList<>();
        curStep = -1;
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
        String[] share1 = graph.getFirst_share();
        String[] share2 = graph.getSecond_share();
        for(int i = 0; i < mt.length; i++){
            //String[] tmp = new String[]{};
            maxMatching.add(new String(share1[mt[i]] + " " + share2[i]));
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
        //if(!graph.isFlagCheckStart()){
        //if()
        curStep++;
        Arrays.fill(used, false);
        tryKuhn(curStep, matrix, used);
        String tmp = graph.getFirst_share()[curStep];
        log.add("Vertex " + tmp + " is processed");
        controller.update();
        //}

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

    public static boolean isCorrect(String str) { //correct string format: V:A B C...
        String pattern = "[A-Za-z0-9][\s[A-Za-z0-9]]*";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(str);
        boolean isMatch = m.find();
        int start = m.start();
        int finish = m.end();
        return isMatch && start == 0 && finish == str.length();
        return true;
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

//    private void printShareToGv(PrintWriter writer, String share) {
//        for (int i = 0; i < graph.getFirst_share().length; ++i) {
//            if (share.equals("fs"))
//                writer.println("\t" + share + (char) i + " [label = \"" + graph.getFirst_share()[i] + "\", fillcolor=white");
//        }
//        writer.print("\t" + share + graph.getFirst_share()[0]);
//        for (int i = 1; i < graph.getFirst_share().length; ++i) {
//            writer.print("--" + share + graph.getFirst_share()[i]);
//        }
//    }

    @Override
    public String getImage() {
//        try (PrintWriter writer = new PrintWriter("./graph.gv")) {
//            writer.println("graph G {");
//            writer.println("\tsplines=false");
//            writer.println("\tnode[shape=circle, style=filled]");
//            // First share
//            for (char v : graph.getFirst_share()) {
//                writer.println("\t" + v + " [label = \"" + v + "\", fillcolor=white");
//            }
//            writer.print("\t" + graph.getFirst_share()[0]);
//            for (int i = 1; i < graph.getFirst_share().length; ++i) {
//                writer.print("--" + graph.getFirst_share()[i]);
//            }
//            writer.println(" [style=invis]");
//            writer.println("\tsubgraph sg {");
//            writer.println("\t\tcolor=invis\n\t}");
//
//            // Second share
//            for (char v : graph.getSecond_share()) {
//                writer.println("\t" + v + " [label = \"" + graph.getSecond_share()[i] + "\", fillcolor=white");
//            }
//            writer.print("\t" + graph.getSecond_share()[0]);
//            for (int i = 1; i < graph.getSecond_share().length; ++i) {
//                writer.print("--" + graph.getSecond_share()[i]);
//            }
//            writer.println();
//
//            // Edges
//            for (int i = 0; i < mt.length; ++i) {
//                if (mt[i] != -1) {
//                    writer.println(graph.getFirst_share()[mt[i]] + "--" + graph.getSecond_share()[i] + "[constraint=false, color=red]");
//                }
//            }
//
//        } catch (IOException e) {
//            System.out.println(e.getMessage());
//            return "";
//        }
//        return "";
        ArrayList<MutableNode> firstShare = new ArrayList<>(0);
        ArrayList<MutableNode> secondShare = new ArrayList<>(0);

        MutableGraph fs = mutGraph("First share").setDirected(false).use((grfs, ctxfs) -> {
            // graphAttrs().add("constraint", "false");
            for (String s : graph.getFirst_share()) {
                firstShare.add(mutNode(s));
            }
            for (int i = 1; i < graph.getFirst_share().length; ++i) {
                linkAttrs().add(Style.INVIS);
                firstShare.get(i).addLink(firstShare.get(i - 1));
            }
        });

        MutableGraph ss = mutGraph("Second share").setDirected(false).use((grfs, ctxfs) -> {
            // graphAttrs().add("constraint", "false");
            for (String s : graph.getSecond_share()) {
                secondShare.add(mutNode(s));
            }
            for (int i = 1; i < graph.getSecond_share().length; ++i) {
                linkAttrs().add(Style.INVIS);
                secondShare.get(i).addLink(secondShare.get(i - 1));
            }
        });

        MutableGraph gap = mutGraph("Gap").setDirected(false).use((grfs, ctxfs) -> {
            // graphAttrs().add("constraint", "false");
            nodeAttrs().add(Style.INVIS);
            mutNode("gapNode");
        });

        MutableGraph g = mutGraph("Bipartite graph").setDirected(false).use((gr, ctx) -> {
            nodeAttrs().add(Shape.CIRCLE);
            graphAttrs().add("splines", "false");
            linkAttrs().add("constraint", "false");

            gr.add(fs).add(gap).add(ss);

            boolean inProcess = false;
            for (int i = 0; i < graph.getFirst_share().length; ++i) {
                for (int j = 0; j < graph.getSecond_share().length; ++j) {
                    if (graph.getBipartition_matrix()[i][j] == 1) {
                        // linkAttrs().add("constraint", "false");
                        if (mt[j] == i) {
                            inProcess = true;
                            linkAttrs().add(Color.RED);
                            firstShare.get(i).addLink(secondShare.get(j));
                            linkAttrs().add(Color.BLACK);
                        } else {
                            firstShare.get(i).addLink(secondShare.get(j));
                        }
                    }
                }
            }

            if (inProcess) {
                firstShare.get(curStep).add(Style.FILLED).add("fillcolor", "green");
            }

//            for (int i = 0; i < mt.length; ++i) {
//                if (mt[i] != -1) {
//                    linkAttrs().add("constraint", "false");
//                    firstShare.get(mt[i]).links().get(i).add(Color.RED);
//                    // firstShare.get(mt[i]).addLink(secondShare.get(i)).add(Color.RED);
//                }
//            }
        });
        try {
            Graphviz.fromGraph(g).render(Format.PNG).toFile(new File("example/ex1i.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "example/ex1i.png";
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
        private final int curStep;
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