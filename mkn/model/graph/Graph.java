package mkn.model.graph;

import java.util.Arrays;

public class Graph<T>{
    private final int[][] state; //adjacency matrix
    private final int num_V; //number of V
    private T[] V = null; // V in alphabet order
    private T[] first_share = null;
    private T[] second_share = null;
    private int[][] bipartition_matrix;
    private boolean isBipart;

    public Graph(int[][] gr, int n, T[] ver, boolean bipart){
        state = new int[n][n];
        for (int i = 0; i < n; i++)
            System.arraycopy(gr[i], 0, state[i], 0, n);
        num_V = n;
        V = Arrays.copyOf(V, n);
        System.arraycopy(ver, 0, V, 0, num_V);
        first_share = Arrays.copyOf(first_share, num_V);
        second_share = Arrays.copyOf(second_share, num_V);
        bipartition_matrix = new int[first_share.length][second_share.length];
        isBipart = bipart;
        if(isBipartition()){
//            System.out.print(first_share);
//            System.out.println(" - first share");
//            System.out.print(second_share);
//            System.out.println(" - second share");
            isBipart = true;
        }
        else{
            System.out.println("The graph is not bipartite!");
            return;
        }
        bipartition_matrix = Arrays.copyOf(bipartition_matrix, first_share.length);
        for (int i = 0; i < first_share.length; i++)
            bipartition_matrix[i] = Arrays.copyOf(bipartition_matrix[i], second_share.length);
        makeBipartitionMatrix();
    }

    /*public void printMatrix(){
        System.out.print("  ");
        for (T c : first_share) {
            System.out.print(c);
            System.out.print(' ');
        }
        System.out.println();
        for (int i = 0; i < second_share.length; i++){
            System.out.print(second_share[i]);
            System.out.print(' ');
            for (int j = 0; j < first_share.length; j++){
                System.out.print(bipartition_matrix[j][i]);
                System.out.print(' ');
            }
            System.out.println();
        }

    }*/

    public boolean isBipart() {
        return isBipart;
    }

    public int[][] getBipartition_matrix(){
        return bipartition_matrix;
    }

    public char[] getFirst_share() {
        return first_share;
    }

    public char[] getSecond_share() {
        return second_share;
    }

    public boolean isBipartition(){ // use DFS & coloring in red or blue
        int[] tmp = new int[num_V]; // 0 - not visited, 1 - red, -1 - blue
        int[] chain = new int[num_V];// current way
        for (int i = 0; i < num_V; i++)
            chain[i] = -1;
        int len_chain = 0;
        boolean flag = false; // find unvisited neighbor flag
        int cur_color = 1;
        int not_look = num_V - 1; //number of unvisited neighbors
        int cur_index_V = 0;
        chain[len_chain] = cur_index_V;
        len_chain++;
        while (not_look != 0){
            if(cur_index_V == -1){//if graph is disconnected
                for(int i = 0; i < tmp.length; i++){
                    if (tmp[i] == 0){
                        cur_index_V = i;
                        not_look--;
                        chain[len_chain] = cur_index_V;
                        len_chain++;
                        break;
                    }
                }
            }
            tmp[cur_index_V] = cur_color;
            for(int i = 0; i < num_V; i++){
                if(i != cur_index_V)
                    if(state[cur_index_V][i] == 1 && tmp[i] == cur_color) // check neighbors
                        return false;
                if(state[cur_index_V][i] == 1 && tmp[i] == 0){ // check unvisited neighbors
                    not_look--;
                    cur_index_V = i;
                    cur_color = -cur_color;
                    chain[len_chain] = cur_index_V;
                    len_chain++;
                    flag = true;
                }
                if(flag)
                    break;
            }
            if(!flag){// rollback
                cur_color = -cur_color;
                if(len_chain > 1)
                    cur_index_V = chain[len_chain - 2];
                else
                    cur_index_V = -1;
                chain[len_chain - 1] = -1;
                len_chain--;
            }
            else
                flag = false;
        }
        tmp[cur_index_V] = cur_color;
        int size1 = 0;
        int size2 = 0;
        for(int i = 0; i < tmp.length; i++){
            if(tmp[i] == 1){
                first_share[size1] = V[i];
                size1++;
            }
            else{
                second_share[size2] = V[i];
                size2++;
            }
        }
        first_share = Arrays.copyOf(first_share, size1);
        second_share = Arrays.copyOf(second_share, size2);
        return true;
    }

    public void makeBipartitionMatrix(){
        for(int i = 0; i < first_share.length; i++){
            int coordX = Arrays.binarySearch(V, first_share[i]);
            for(int j = 0; j < second_share.length; j++){
                int coordY = Arrays.binarySearch(V, second_share[j]);
                bipartition_matrix[i][j] = state[coordX][coordY];
            }
        }
    }
}