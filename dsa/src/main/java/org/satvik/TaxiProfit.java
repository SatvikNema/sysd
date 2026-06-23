package org.satvik;

import java.util.Arrays;

public class TaxiProfit {
    static long[] dp;
    public static void main() {
        long[] pickups = new long[]{1, 2, 3, 4, 5};
        long[] drops = new long[]{3, 4, 10, 12, 15};
        long[] tips = new long[]{4, 3, 1, 6, 2};
        // 1 => 6,14 => 20
        // 2 => 5,14 => 19
        dp = new long[pickups.length];
        Arrays.fill(dp, -1);
        long ans = maxTrip(pickups, drops, tips, 0);
        System.out.println(ans);
        // 20, starting from pickup[0] = 1
    }

    private static long maxTrip(long[] pickups, long[] drops, long[] tips, int index) {
        if(index >= pickups.length) return 0;
        if(dp[index] != -1) return dp[index];

        long profit = drops[index] - pickups[index] + tips[index];
        long a = 0, b = 0;
        for(int i=0;i<pickups.length;i++){
            if(pickups[i] >= drops[index]){
                a = Math.max(a, maxTrip(pickups, drops, tips, i));
            }
        }
        a = a + profit;

        b = maxTrip(pickups, drops, tips, index+1);
        long ans = Math.max(a, b);
        dp[index] = ans;
        return ans;
    }
}
