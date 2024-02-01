package huige233.transcend;

public class ld {
    public int minDistance(String w1, String w2){
        int m = w1.length();
        int n = w2.length();
        if(m*n == 0) return Math.max(m,n);

        int[][] lev = new int[m+1][n+1];

        for(int j = 0; j<n+1; j++) lev[0][j] = j;

        for(int i = 0; i<m+1; i++) lev[i][0] = i;

        for(int i = 1; i<m+1; i++){
            for(int j = 1; j<n+1; j++){
                int countByInsert = lev[i][j-1] + 1;
                int countByDel = lev[i-1][j] + 1;
                int countByReplace =  w1.charAt(i-1)==w2.charAt(j-1) ? lev[i-1][j-1] : lev[i-1][j-1]+1;
                lev[i][j] = min( countByInsert, countByDel, countByReplace );
            }
        }
        return lev[m][n];
    }

    private int min(int a, int b, int c){
        int temp = Math.min(a,b);
        int res = Math.min(temp, c);
        return res;
    }

    public int ldy(String str,String target){
        int[][] d;
        int n = str.length();
        int m = target.length();
        int i,j,temp;
        String ch1,ch2;
        if(n == 0 || m == 0) return 0;
        d = new int[n+1][m+1];
        for(i = 0;i<=n;i++) d[i][0] = i;
        for(j = 0;j<=m;j++) d[0][j] = j;

        for(i = 1;i<=n;i++){
            ch1 = str.substring(i-1,i);
            for(j = 1;j<=m;j++){
                ch2 = target.substring(j-1,j);
                if(ch1.equals(ch2)){
                    temp = 0;
                }else{
                    temp = 1;
                }
                d[i][j] = min(d[i-1][j]+1,d[i][j-1]+1,d[i-1][j-1]+temp);
            }
        }
        return (int) ((1 - (float) getRowColumnMin(d) / Math.max(str.length(), target.length())) * 100F);
    }

    public int getRowColumnMin(int[][] d){
        int min = Integer.MAX_VALUE;
        if(d.length<=d[0].length){
            for(int i =0;i<d.length;i++){
                min = Math.min(d[i][d[0].length -1],min);
            }
        }
        if(d.length >= d[0].length){
            for(int i =0;i<d[0].length;i++){
                min = Math.min(d[d.length -1][i],min);
            }
        }
        return min;
    }
}
