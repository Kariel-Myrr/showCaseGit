import java.io.*;
import java.util.*;

/*
 * Дана строка, составленная из круглых, квадратных и фигурных скобок. 
 * Определите, какое наименьшее количество символов необходимо удалить из этой строки, 
 * чтобы оставшиеся символы образовывали правильную скобочную последовательность
 */
 
public class Brackets{

    static public void main (String[] args) {
        try{
            Scanner in;
            PrintWriter out;
            if(true){
                in = new Scanner(System.in, "utf8");
                out = new PrintWriter(new OutputStreamWriter(System.out));
            } else {
               in = new Scanner(new File(args[0]), "utf8");
                out = new PrintWriter(new FileWriter(new File(args[1])));
            }
            
            StringBuilder siq = new StringBuilder(in.nextLine());
            
            int n = siq.length();
            
            int[][] dp = new int[n][n];
            int[][] path = new int[n][n];
            
            for(int i = 0; i < n; i++){
                dp[i][i] = 1;
                path[i][i] = -2;
            }
            
            for(int i = 1; i < n; i++){
                for(int j = 0; j < n-i; j++){
                    int l = j;
                    int r = i+j;
                    int min = Integer.MAX_VALUE;
                    if(r-l > 1){
                        for(int k = l+1; k < r-1; k++){
                            if(min > dp[l][k] + dp[k+1][r]){
                                min = dp[l][k] + dp[k+1][r];
                                path[l][r] = k;
                            }                            
                        }
                    }
                    
                    if(r-1 > -1 && l+1 < n && siq.charAt(l) == '(' && siq.charAt(r) == ')' && min > dp[l+1][r-1]){
                        min = dp[l+1][r-1];
                        path[l][r] = -1;
                    }
                    if(r-1 > -1 && l+1 < n && siq.charAt(l) == '[' && siq.charAt(r) == ']' && min > dp[l+1][r-1]){
                        min = dp[l+1][r-1];
                        path[l][r] = -1;
                    }
                    if(r-1 > -1 && l+1 < n && siq.charAt(l) == '{' && siq.charAt(r) == '}' && min > dp[l+1][r-1]){
                        min = dp[l+1][r-1];
                        path[l][r] = -1;
                    }
                    
                    if(l+1 < n && min > dp[l+1][r] + 1){
                        min = dp[l+1][r] + 1;
                        path[l][r] = -2;
                    }
                    if(r-1 > -1 && min > dp[l][r-1] + 1){
                        min = dp[l][r-1] + 1;
                        path[l][r] = -3;
                    }
                    dp[l][r] = min;
                }
            }
            
            
            
            /*for(int[] i : dp){
                for(int j : i){
                    System.out.print(j + " ");
                }
                System.out.println();
            }
            System.out.println();
            for(int[] i : path){
                for(int j : i){
                    System.out.print(j + " ");
                }
                System.out.println();
            }
            out.println(dp[0][n-1]);*/
            
            create(dp, path, 0, n-1, siq);
            
            for(int i = 0; i < n; i++){
                if(siq.charAt(i) != '0'){
                    out.print(siq.charAt(i));
                }
            }
            
            out.flush();
            out.close();
            in.close();
        } catch ( IOException e ){
            System.out.println("ex");
        }
    }
    
    private static void create(int[][] dp, int[][] path, int i, int j, StringBuilder sb){
        //System.out.println(sb);
        //System.out.println(i + " " + j + " " + path[i][j]);
        if(path[i][j] == 0){
            return;
        }
        if(i == j){
            sb.setCharAt(i, '0');
            //System.out.println(".");
            return;
        } else {
            if(path[i][j] == -1){
                if(i != j+1){
                    create(dp, path, i+1, j-1, sb);
                } else {
                    return;
                }
            }
            else if(path[i][j] == -2){
                sb.setCharAt(i, '0');
                create(dp, path, i+1, j, sb);
            }
            else if(path[i][j] == -3){
                sb.setCharAt(j, '0');
                create(dp, path, i, j-1, sb);
            }
            else if(path[i][j] != 0){
                create(dp, path, i, path[i][j], sb);
                create(dp, path, path[i][j]+1, j, sb);
            }
        }
    }
}
