package com.satvik.dsa;

import java.util.Arrays;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Solves the gravity simulation problem where boxes fall and hit obstacles,
 * causing simultaneous 3x3 explosions centered on the obstacle.
 */
public class GravitySimulation {
    static int[][] dirs = {
            {-1, -1}, {-1, 0}, {-1, 1},
            { 0, -1},{ 0, 1},
            { 1, -1}, {1, 0}, {1, 1}
    };
    public char[][] solution(char[][] board) {
        if (board == null || board.length == 0) {
            return new char[0][0];
        }
        int r = board.length;
        int c = board[0].length;
        int outterLoop = r;
        while(outterLoop-- > 0){
            gravityOneTick(board, r, c);
        }
        return board;
    }

    private static void gravityOneTick(char[][] board, int r, int c) {
        boolean explosion = false;
        for(int i=r-1;i>0;i--) {
            char[] aboveRow = board[i - 1];
            char[] currentRow = board[i];
            for (int j = 0; j < c; j++) {
                char current = currentRow[j];
                char above = aboveRow[j];
                if (above == '#') {
                    if (current == '*') {
                        board[i][j] = 'B';
                        explosion = true;
                        board[i - 1][j] = '-';
                    } else if (current == '-') {
                        board[i][j] = '#';
                        board[i - 1][j] = '-';
                    }
                }
            }
        }
        if (explosion) {
            takeCareOfExplosions(board, r, c, dirs);
        }
    }

    private static void takeCareOfExplosions(char[][] board, int r, int c, int[][] dirs) {
        for(int a = 0; a< r; a++){
            for(int b = 0; b< c; b++){
                if(board[a][b] == 'B'){
                    board[a][b] = '*';
                    for(int[] dir: dirs){
                        int x = a+dir[0];
                        int y = b+dir[1];
                        if(inRange(r, c, x, y)){
                            if(board[x][y] != '*' && board[x][y] != 'B'){
                                board[x][y] = '-';
                            }
                        }
                    }
                }
            }
        }
    }

    public static boolean inRange(int r, int c, int i, int j){
        return i>=0 && j>=0 && i<r && j<c;
    }

    private static void printBoard(char[][] board) {
        System.out.println("{");
        for (int i = 0; i < board.length; i++) {
            System.out.print("  [");
            for (int j = 0; j < board[0].length; j++) {
                // Defensive check (though array should be rectangular)
                if (board[i] == null || board[i].length == 0) continue;

                System.out.print("'" + board[i][j] + "'" + (j == board[0].length - 1 ? "" : ", "));
            }
            System.out.println("]" + (i == board.length - 1 ? "" : ","));
        }
        System.out.println("}");
    }
    public static void main(String[] args) {
        GravitySimulation gs = new GravitySimulation();
        char[][] board3 = {
                {'#', '-', '#', '#', '*'},
                {'#', '-', '-', '#', '#'},
                {'-', '#', '-', '#', '-'},
                {'-', '-', '#', '-', '#'},
                {'#', '*', '-', '-', '-'},
                {'-', '-', '*', '#', '-'}
        };

        char[][] clonedBoard3 = Arrays.stream(board3).map(char[]::clone).toArray(char[][]::new);
        char[][] result3 = gs.solution(clonedBoard3);

        System.out.println("\nOutput Board 3:");
        printBoard(result3);

        char[][] board4 = {
                {'#', '#', '*'},
                {'#', '-', '*'},
                {'#', '-', '*'},
                {'-', '#', '#'},
                {'*', '-', '#'},
                {'*', '-', '-'},
                {'*', '-', '-'}
        };

        char[][] clonedBoard4 = Arrays.stream(board4).map(char[]::clone).toArray(char[][]::new);
        char[][] result4 = gs.solution(clonedBoard4);

        System.out.println("\nOutput Board 4:");
        printBoard(result4);
    }
}
