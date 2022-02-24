import java.util.ArrayList;
import java.util.Scanner;
 
import static java.lang.Integer.min;

/*
 * Дан неориентированный граф, не обязательно связный,
 *  но не содержащий петель и кратных рёбер. 
 * Требуется найти все мосты в нём.
 */
 
public class Bridges {
 
    static final int MAXN = 200_000;
 
    static private class R{
        public int number;
        public int to;
 
        public R(int n, int t){
            number = n;
            to = t;
        }
 
        @Override
        public String toString() {
            return "R{" +
                    "number=" + number +
                    ", to=" + to +
                    '}';
        }
    }
 
    static ArrayList<ArrayList<R>> g = new ArrayList<>();
    static boolean[] used = new boolean[MAXN];
    static int timer;
    static int[] tin = new int[MAXN], fup = new int[MAXN];
    static int n;
    static ArrayList<Integer> ans = new ArrayList<>();
 
    static int counter;
 
    static void IS_BRIDGE(int a, R b){
        counter++;
        ans.add(b.number);
    }
 
    static void dfs(int v, int p) {//p = -1
        //System.out.println(v + " " + p);
        used[v] = true;
        tin[v] = fup[v] = timer++;
        for (int i = 0; i < g.get(v).size(); i++) {
            R r = g.get(v).get(i);
            int to = r.to;
            if (to == p)  continue;
            if (used[to])
                fup[v] = min(fup[v], tin[to]);
            else {
                dfs (to, v);
                fup[v] = min(fup[v], fup[to]);
                if (fup[to] > tin[v])
                    IS_BRIDGE(v,r);
            }
        }
    }
 
    static void find_bridges() {
        timer = 0;
        for (int i=0; i < n; ++i)
            used[i] = false;
        for (int i=0; i < n; i++)
            if (!used[i]) {
                dfs(i, -1);
            }
    }
 
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
 
        n = in.nextInt();
        int m = in.nextInt();
 
        for(int i = 0; i < n; i++){
            g.add(new ArrayList<>());
        }
 
        for(int i = 0; i < m; i++){
            int a = in.nextInt() - 1, b = in.nextInt() - 1;
            g.get(a).add(new R(i+1, b));
            g.get(b).add(new R(i+1, a));
 
        }
 
        for (int i = 0; i < n; ++i) {
            used[i] = false;
        }
 
        find_bridges();
        System.out.println(counter);
        ans.sort(Integer::compareTo);
        for (int i : ans){
            System.out.print(i + " ");
        }
        System.out.println();
    }
 
}
