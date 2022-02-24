import java.io.*;
import java.util.Scanner;
 
/*
 * Дано подвешенное дерево с корнем в первой вершине. Вам нужно ответить на m запросов вида "найти LCA двух вершин". 
 * LCA вершин u и v в подвешенном дереве — это наиболее удалённая от корня дерева вершина, 
 * лежащая на обоих путях от u и v до корня.
 */
public class LCA {
 
    static BufferedWriter out;
    static int f = 1;
 
    private static int myLog(final int n) {
        int result = 1, myN = n+1;
        while (myN > 1) {
            myN /= 2;
            result += 1;
        }
        return result;
    }
 
    static int[][] jump;
    static int[] height;
    static int n, log;
 
 
    static int getStartLca(int u, int v) throws IOException{
        if(height[u] < height[v]) {
            int buff = u;
            u = v;
            v = buff;
        }
        int d = height[u] - height[v];
        for(int k = log; k >= 0; k--){
            if(d >= (1 << k)){
                u = jump[u][k];
                d -= (1<< k);
            }
        }
        return getLca(u, v);
    }
 
    static int getLca(int u, int v){
        if(u == v){
            return v;
        }
        int h = myLog(height[v]) - 1;
        for(; jump[v][h] == jump[u][h] && h != 0; h /= 2) {
        }
        return getLca(jump[v][h], jump[u][h]);
    }
 
 
 
    public static void main(String[] args) throws IOException {
 
        Scanner in;
        if (f == 1) {
            in = new Scanner(System.in);
            out = new BufferedWriter(new OutputStreamWriter(System.out));
        } else if(f == 0){
            in = new Scanner(new File("fastminimization.in"));
            out = new BufferedWriter(new FileWriter(new File("fastminimization.out")));
        } else {
            in = new Scanner(new File("minimization.in"));
            out = new BufferedWriter(new FileWriter(new File("minimization.out")));
        }
 
        n = in.nextInt()+1;
        log = myLog(n);
 
        jump = new int[n][log];
        height = new int[n];
 
        jump[1][0] = 0;
        height[1] = 1;
 
        for(int i = 2; i < n; i++){
            jump[i][0] = in.nextInt();
            height[i] = height[jump[i][0]] + 1;
        }
 
 
        for(int i = 1; i < log; i++){
            for(int j = 1; j < n; j++){
                jump[j][i] = jump[jump[j][i-1]][i-1];
            }
        }
 
        int m = in.nextInt();
 
        for(int i = 0; i < m; i++){
            out.write(getStartLca(in.nextInt(), in.nextInt()) + "\n");
            out.flush();
        }
 
 
        out.close();
 
    }
}
