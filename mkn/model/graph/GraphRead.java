package mkn.model.graph;

import mkn.model.algorithm.AlgoKuhn;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class GraphRead {

    public static void read(String filename) throws IOException {
        FileReader file = new FileReader(filename);
        Scanner scan = new Scanner(file);
        ArrayList<String> aa = new ArrayList<>();
        while (scan.hasNextLine()){//read graph from file
            String read_string = scan.nextLine();
            boolean check = isCorrect(read_string);
            if (!check){
                System.out.println("The file contains invalid data!");
                return;
            }
            aa.add(read_string);
        }
        file.close();
        int[][] b = makeMatrix(aa);//make adjacency matrix
        char[] ver_2 = makeV(aa);//make V in alphabet order
        int size = aa.size();
        /*Graph gr = new Graph(b, size, ver_2, false);
        AlgoKuhn alg = new AlgoKuhn(gr);
        if(gr.isBipart()){
            alg.Kuhn();
            alg.printMax_matching();
        }*/
    }

    public static boolean isCorrect(String str){//correct string format: V:A B C...
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
}