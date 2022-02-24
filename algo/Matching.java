import java.util.*;
import java.io.*;
import java.lang.*;

/*
 * Ваша задача — найти максимальное паросочетание в двудольном графе,
 *  то есть паросочетание с максимально возможным числом рёбер.
 */

public class Matching {
    Scanner in;
    int[] level, deletedVertexes;
    boolean[] used;
    int n, m, t, s;
    long INFINITY = Long.MAX_VALUE;
    int[] matching;
    List<List<Integer>> edges = new ArrayList<>();

    public void solve() {
        int n = in.nextInt();
        int m = in.nextInt();
        for (int i = 0; i < n; ++i) {
            edges.add(new ArrayList<>());
        }
        for (int i = 0; i < n; ++i) {
            int num = in.nextInt();
            while (num != 0) {
                edges.get(i).add(num - 1);
                num = in.nextInt();
            }
        }

        matching = new int[m];
        Arrays.fill(matching, -1);
        for (int i = 0; i < n; ++i) {
            used = new boolean[n];
            kuhn(i);
        }
        long size = Arrays.stream(matching).parallel().filter(x -> x != -1).count();
        System.out.println(size);
        for (int i = 0; i < m; ++i) {
            if (matching[i] != -1) {
                System.out.println((matching[i] + 1) + " " + (i + 1));
            }
        }


    }

    boolean kuhn(int v) {
        if (used[v]) {
            return false;
        }
        used[v] = true;
        for (int to : edges.get(v)) {
            if (matching[to] == -1 || kuhn(matching[to])) {
                matching[to] = v;
                return true;
            }
        }
        return false;
    }

    public void run() {
        in = new Scanner(System.in);
        solve();
    }


    public static void main(String[] args) {
        new Matching().run();
    }
}

