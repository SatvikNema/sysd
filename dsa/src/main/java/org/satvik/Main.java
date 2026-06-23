package org.satvik;

import com.sun.source.tree.Tree;
import lombok.EqualsAndHashCode;
import org.apache.tomcat.util.net.jsse.JSSEUtil;

import java.util.*;

public class Main {
    List<List<String>> ans;
    boolean[] done;
    static void main() {
        Main main = new Main();
        StringBuilder sb = new StringBuilder();
        sb.append('a');
        TreeSet<Integer> treeSet = new TreeSet<>();
        TreeMap<Integer, Integer> treeMap = new TreeMap<>();
        PriorityQueue<Integer> pq = new PriorityQueue<>(Collections.reverseOrder());
//        Set<Integer> set = new HashSet<>();
        Deque<Integer> deque = new LinkedList<>();

        Set<Integer>[] sets = (Set<Integer>[]) new HashSet<?>[10];

        System.out.println(canDecode('7'));
        System.out.println(canDecode("26"));

        String s = "Satvik";
        String t = "" + s.charAt(0) + s.charAt(2);
        System.out.println(t);
    }

    static boolean canDecode(char i){
        int intAscii =  i - '0' + 64;
        return intAscii <= 'Z' && intAscii >= 'A';
    }

    static boolean canDecode(String s){
        int intAscii = (Integer.parseInt(s)) + 64;
        return intAscii <= 'Z' && intAscii >= 'A';
    }

    Set<Point> pset = new HashSet<>();
    Set<Point> aset = new HashSet<>();

    private static final int[][] dirs = new int[][]{
            {0, 1},
            {1, 0},
            {0, -1},
            {-1, 0}
    };

    public List<List<Integer>> pacificAtlantic(int[][] arr) {
        int r = arr.length, c = arr[0].length;
        List<int[]> points = new ArrayList<>();
        for(int i=0;i<r;i++){
            points.add(new int[]{i, 0});
        }
        for(int i=1;i<c;i++){
            points.add(new int[]{0, i});
        }

        for(int[] p:points){
            dfs(arr, p[0], p[1], pset, Integer.MIN_VALUE);
        }

        points = new ArrayList<>();
        for(int i=0;i<r;i++){
            points.add(new int[]{i, c-1});
        }
        for(int i=0;i<c-1;i++){
            points.add(new int[]{r-1, i});
        }

        for(int[] p:points){
            dfs(arr, p[0], p[1], aset, Integer.MIN_VALUE);
        }
        System.out.println(pset);
        System.out.println(aset);
        List<List<Integer>> ans = new ArrayList<>();
        for(Point p:pset){
            if(aset.contains(p)){
                ans.add(List.of(p.x, p.y));
            }
        }
        System.out.println(ans);
        return ans;
    }

    void dfs(int[][] arr, int x, int y, Set<Point> set, int last){
        if(x<0 || y<0 || x>=arr.length || y>= arr[0].length || arr[x][y] == -1 || arr[x][y] < last){
            return;
        }
        last = arr[x][y];
        arr[x][y] = -1;
        set.add(new Point(x, y));
        //dfs
        for(int[] pos:dirs){
            dfs(arr, x+pos[0], y+pos[1], set, last);
        }
        arr[x][y] = last;
    }

    static class Point{
        int x, y;
        public Point(int x, int y){
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Point point = (Point) o;
            return x == point.x && y == point.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }

        @Override
        public String toString() {
            return "("+x + ", " + y+")";
        }
    }

    /*
    2,5,6,9
     */

}

/*
1,10,5,7,4,3,6

1, 3, 4, 5, 6, 7, 10
1, 5, 7, 10

5, 10
7, 1

10100

00101 -> 5


 */
