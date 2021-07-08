package mkn.model;

import java.util.ArrayList;
import java.util.Arrays;


public class AlgoKuhn {
    private final Graph gr;
    private final ArrayList<String> max_matching = new ArrayList<>();
    int[] mt;//array of index for max matching (default = [-1,...,-1])
    public AlgoKuhn(Graph g){
        gr = g;
        mt = new int[gr.getSecond_share().length];
        Arrays.fill(mt, -1);
    }

    public void Kuhn(){// Kuhn's algorithm
        int [][] matrix = gr.getBipartition_matrix();
        boolean[] used = new boolean[gr.getFirst_share().length];//visit list
        for (int i = 0; i < gr.getFirst_share().length; i++){
            Arrays.fill(used, false);
            tryKuhn(i, matrix, used);
        }
    }

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
}
