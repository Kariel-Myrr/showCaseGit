import java.util.Scanner;

/*
 * Выведите последовательно результат выполнения всех операций sum. 
 * Следуйте формату выходного файла из примера.
 */

public class SumTree {

    private static int myLog(final int n){
        int result = 1, myN = n;
        while(myN > 1){
            myN /= 2;
            result *= 2;
        }
        if(result == n){
            return result;
        }
        return result*2;
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int n = in.nextInt();
        long[] a = new long[n];

        for(int i = 0; i < n; i++){
            a[i] = in.nextLong();
        }

        Tree tree = new Tree(a);

        /*tree.set(0, 10);
        tree.set(1, 3);
        tree.set(4, 2);*/

        /*for(long i : tree.arr){
            System.out.prlong(i + " ");
        }*/

        while(in.hasNext()) {
            String key = in.next();
            if(key.equals("set")){
                tree.set(in.nextInt() - 1, in.nextInt());
            } else {
                System.out.println(tree.sum(in.nextLong() - 1 , in.nextLong()));
            }
            /*for(long i : tree.arr){
                System.out.prlong(i + " ");
            }
            System.out.prlongln();*/
        }

    }

    public static class Tree {

        Leaf head;
        int size;
        long[] arr;

        private static class Leaf {
            Leaf lc;
            Leaf rc;
            long sum;

            public Leaf(Leaf l, Leaf r, long sum) {
                this.lc = l;
                this.rc = r;
                this.sum = sum;
            }
        }

        public Tree(long[] arr) {
            this.arr = arr;
            size = myLog(arr.length);
            //System.out.println(size);

            head = build(0, size);
        }

        private Leaf build(int l, int r) {
            if (r - l <= 1 && l < arr.length) {
                //System.out.prlong(arr[l] + " ");
                //System.out.println("from " + l + " to " + r + " sum : " + (arr[l]));
                return new Leaf(null, null, arr[l]);
            } else if(l >= arr.length){
                return new Leaf(null, null, 0);
            } else {
                int mid = (l+r)/2;
                //System.out.prlongln();
                Leaf lc = build(l, mid);
                Leaf rc = build(mid, r);
                //System.out.prlongln("from " + l + " to " + r + " sum : " + (lc.sum + rc.sum));
                return new Leaf(lc, rc, lc.sum + rc.sum);
            }
        }

        public void set(int i, long x) {
            set(head, 0, size, i, x);
        }

        private void set(Leaf cur, long l, long r, int i, long x) {
            //System.out.prlongln("set in [" + l + ", " + r + "] to ind: " + i + " val: " + x + " cur.sum = " + cur.sum);
            if (r - l == 1 && l == i) {
                arr[i] = x;
                cur.sum = x;
                return;
            } else {
                long mid = (r + l) / 2;
                if (l <= i && i < mid) {
                    //System.out.prlongln("went left");
                    cur.sum -= cur.lc.sum;
                    set(cur.lc, l, mid, i, x);
                    cur.sum += cur.lc.sum;
                } else if(mid <= i && i < r){
                    //System.out.prlongln("went right");
                    cur.sum -= cur.rc.sum;
                    set(cur.rc, mid, r, i, x);
                    cur.sum += cur.rc.sum;
                } else{
                    return;
                }
                //System.out.prlongln("sum changed on [" + l + ", " + r + "] to " + cur.sum);
            }
        }

        public long sum(long left, long right) {
            return sum(head, 0, size, left, right);
        }

        private long sum(Leaf cur, long l, long r, long left, long right) {
            //System.out.prlongln("sum searching in [" + l + ", " + r + "] for [" + left + ", " + right + "]");
            if (left <= l && r <= right) {
                return cur.sum;
            }
            long sum = 0;
            long mid = (l + r) / 2;
            if (left < mid && l < right) {
                sum += sum(cur.lc, l, mid, left, right);
            }
            if (mid < right && left < r) {
                sum += sum(cur.rc, mid, r, left, right);
            }
            return sum;
        }
    }

}
