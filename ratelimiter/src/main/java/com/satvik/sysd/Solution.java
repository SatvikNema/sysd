package com.satvik.sysd;

import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

class Solution {

    public static void main(String[] args) {
        System.out.println(System.nanoTime()+" "+System.currentTimeMillis());
        long x = 1288834974657L;
        String bin = Long.toBinaryString(x);
        System.out.println(bin.length());
    }


}

// 1, 2, 3, 4, 5, 6, 7

// 1,3,-1,-3,5,3,6,7
// 0,1, 2, 3,4,5,6,7

//2-3+1

