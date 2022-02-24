import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Scanner;

/*
 * Даны строки p и t. Требуется найти все вхождения строки p в строку t в качестве подстроки.
 */
 
public class FastStringSearch {
 
 
    public static void main(String[] args) throws IOException {
 
        Scanner in = new Scanner(System.in);
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(System.out));
 
        String line = in.nextLine();
        int lenP = line.length();
 
        line += "#";
        line += in.nextLine();
 
        int l = line.length();
 
        int[] zf = new int[2000_000];
        int left = 0, right = 0;
        for(int i = 1; i < l; i++){
            zf[i] = Math.max(0, Math.min(right - i, zf[i - left]));
            while (i + zf[i] < l && line.charAt(zf[i]) == line.charAt(i + zf[i])) {
                zf[i]++;
            }
            if(i + zf[i] > right) {
                left = i;
                right = i + zf[i];
            }
        }
 
        int sum = 0;
        int[] a = new int[1000_000];
 
        for(int i = lenP; i < l; i++){
            if(zf[i] == lenP){
                a[sum] = i - lenP;
                sum++;
            }
        }
 
        out.write(sum + "\n");
 
        for (int i = 0; i < sum; i++){
            out.write(a[i] + " ");
        }
 
        out.write("\n");
        out.close();
 
 
    }
 
 
}
