package org.satvik;

import java.util.HashSet;
import java.util.Set;

public class Solution {
    public static void main() {
        Solution solution = new Solution();
        Set<Character> set = new HashSet<>();
        set.add((char) (1 + 'a'));
        System.out.println(set);
    }

    Set<Integer> globalSet = new HashSet<>();

    public boolean hasCycle(int[] arr){
        int len = arr.length;
        for(int i=0;i<len;i++){
            if(!globalSet.contains(arr[i]) && checkLoop(arr, i, len)){
                return true;
            }
        }
        return false;
    }

    boolean checkLoop(int[] arr, int i, int len){
        if(i < 0){
            return checkLoopLeft(arr, i, len);
        } else {
            return checkLoopRight(arr, i, len);
        }
    }

    boolean checkLoopRight(int[] arr, int i, int len){
        Set<Integer> set = new HashSet<>();
        set.add(i);
        globalSet.add(i);
        while(i<len){
            if(arr[i] <= 0) return false;
            i += arr[i];
            if(i >= len){
                i -= len;
            }
            if(set.contains(i)){
                int lastIndex = i;
                i += arr[i];
                if(i >= len){
                    i -= len;
                }
                return lastIndex != i;
            }
            globalSet.add(i);
        }
        return false;
    }

    boolean checkLoopLeft(int[] arr, int i, int len){
        Set<Integer> set = new HashSet<>();
        set.add(i);
        globalSet.add(i);
        while(i>=0){
            if(arr[i] >= 0) return false;
            i += arr[i];
            if(i < 0){
                i += len;
            }
            if(set.contains(i)){
                int lastIndex = i;
                i += arr[i];
                if(i < 0){
                    i += len;
                }
                return lastIndex != i;
            }
            globalSet.add(i);
        }
        return false;
    }

    class TreeNode{
        int val;
        TreeNode left;
        TreeNode right;
    }


}


/*
You are given an array of non-zero integers nums.
Each element represents the number of steps to move from the current index:
A positive value means move forward.
A negative value means move backward.
The array is circular, so moving beyond either end wraps around.
Examples:
nums = [2, -1, 1, 2, 2]
nums = [0, 2, -1, 1, 2, 2]

0 -> 2 -> 3 -> 0
0 -> 1

From index 0:
0 -> 2 -> 3 -> 0
Your task is to determine whether the array contains a valid cycle.
A cycle is considered valid if:
Starting from some index, you can return to the same index by repeatedly following the jumps.
The cycle contains more than one element.
All movements within the cycle are in the same direction (either all positive or all negative).
Return true if such a cycle exists, otherwise return false.

-2, -3, -9

-2 > -3 > -3
 */