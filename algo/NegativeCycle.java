import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Scanner;
 
 /*
  * Дан ориентированный граф. 
  * Определите, есть ли в нем цикл отрицательного веса, 
  * и если да, то выведите его.
  */
 
public class NegativeCycle {
 
    static int INF = Integer.MAX_VALUE;
 
    static class Edge {
        int a, b, cost;
 
        public Edge(int a, int b, int cost) {
            this.a = a;
            this.b = b;
            this.cost = cost;
        }
    }
 
    public static void main(String[] args) throws IOException {
 
        Scanner in = new Scanner(System.in);
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(System.out));
 
        int n = in.nextInt();
        int m = 0;
        ArrayList<Edge> e = new ArrayList<>();
 
        for(int i = 0; i < n; i++){
            for(int j = 0; j < n; j++){
                int cost = in.nextInt();
                if(cost != 100_000) {
                    e.add(new Edge(i, j, cost));
                    m++;
                }
            }
        }
 
 
        int[] d = new int[100];
        int[] p = new int[100];
        int x = 0;
        for (int i = 0; i < n; ++i) {
            x = -1;
            for (int j = 0; j < m; j++) {
                Edge eV = e.get(j);
                if (d[eV.b] > d[eV.a] + eV.cost) {
                    d[eV.b] = Math.max(-INF, d[eV.a] + eV.cost);
                    p[eV.b] = eV.a;
                    x = eV.b;
                }
            }
        }
 
        if (x == -1)
            out.write("NO\n");
        else {
            out.write("YES\n");
            int y = x;
            for (int i = 0; i < n; ++i)
                y = p[y];
 
            ArrayList<Integer> path = new ArrayList<>();
            for (int cur = y; ; cur = p[cur]) {
                path.add(cur);
                if (cur == y && path.size() > 1) break;
            }
 
            out.write(path.size()-1 + "\n");
            for (int i = path.size() - 2; i >= 0; i--) {
                out.write((path.get(i)+1) + " ");
            }
        }
        out.write("\n");
        out.close();
 
    }
 
 
}
