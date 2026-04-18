package com.satvik.dsa;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UnionFind {

    public static void main(String[] args) {
        int[] arr = new int[]{1, 2, 3, 4, 5, 6, 8, 9, 10, 13};
        Map<Integer, Boolean> map = new HashMap<>();
        for(int i:arr){
            map.put(i, false);
        }
        int ans = 0;
        for(int i:arr){
            if(map.get(i)) continue;
            int look = i-1;
            int curr = 1;
            while(map.containsKey(look)){
                curr++;
                map.put(look,true);
                look--;
            }
            look = i+1;
            while(map.containsKey(look)){
                curr++;
                map.put(look,true);
                look++;
            }
            ans = Math.max(ans, curr);
            map.put(i, true);
        }
        System.out.println(ans);

    }

    public static class DSU{
        Map<Integer, Integer> parent;
        Map<Integer, Integer> size;

        public DSU(){
            parent = new HashMap<>();
            size = new HashMap<>();
        }
        public void add(int element){
            parent.put(element, element);
            size.put(element, 1);
        }

        public int findParent(int element){
            Integer currParent = parent.get(element);
            if(currParent == null){
                throw new IllegalArgumentException("Element not found");
            }
            if(currParent == element){
                return element;
            }
            return findParent(currParent);
        }

        public void merge(int a, int b){
            int p1 = findParent(a);
            int p2 = findParent(b);
            if(p1 != p2) {
                parent.put(p2, p1);
                size.put(p1, size.get(p2) + size.get(p1));
            }
        }

        public int getMaxSetSize(){
            return size.values().stream().reduce(1, Math::max);
        }
    }
}

