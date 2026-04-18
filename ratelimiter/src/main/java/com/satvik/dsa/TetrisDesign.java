package com.satvik.dsa;

import java.util.List;

class TetrisDesign {

    private static final List<int[]> FIGURE_A = List.of(new int[]{0, 0}); // dot
    private static final List<int[]> FIGURE_B = List.of(new int[]{0, 0}, new int[]{0, 1}, new int[]{0, 2}); // line
    private static final List<int[]> FIGURE_C = List.of(new int[]{0, 0}, new int[]{0, 1}, new int[]{1, 0}, new int[]{1, 1}); // square
    private static final List<int[]> FIGURE_D = List.of(new int[]{0, 0}, new int[]{1, 0}, new int[]{2, 0}, new int[]{1, 1}); // one line E
    private static final List<int[]> FIGURE_E = List.of(new int[]{0, 1}, new int[]{1, 0}, new int[]{1, 1}, new int[]{1, 2}); // T-shape


    public static int[][] solution(int n, int m, List<String> figures) {
        int[][] grid = new int[n][m];
        for (int figureIndex = 0; figureIndex < figures.size(); figureIndex++) {
            String figureType = figures.get(figureIndex);
            int figureValue = figureIndex + 1;
            List<int[]> shape = getShape(figureType);
            if (shape == null) continue;
            outerLoop:
            for (int r = 0; r < n; r++) {
                for (int c = 0; c < m; c++) {
                    boolean canPlace = true;
                    for (int[] offset : shape) {
                        int nextR = r + offset[0];
                        int nextC = c + offset[1];
                        if (nextR < 0 || nextR >= n || nextC < 0 || nextC >= m || grid[nextR][nextC] != 0) {
                            canPlace = false;
                            break;
                        }
                    }
                    if (canPlace) {
                        for (int[] offset : shape) {
                            int nextR = r + offset[0];
                            int nextC = c + offset[1];
                            grid[nextR][nextC] = figureValue;
                        }
                        break outerLoop;
                    }
                }
            }
        }

        return grid;
    }

    /**
     * Helper method to map figure type to its shape definition.
     */
    private static List<int[]> getShape(String type) {
        return switch (type) {
            case "A" -> FIGURE_A;
            case "B" -> FIGURE_B;
            case "C" -> FIGURE_C;
            case "D" -> FIGURE_D;
            case "E" -> FIGURE_E;
            default -> null;
        };
    }

    // --- Optional: Main method for testing the example cases ---

    public static void main(String[] args) {
        // Example 1: n = 4, m = 4, figures = ["D", "B", "A", "C"]
        // Expected Output:
        // [[1, 2, 2, 2],
        //  [1, 1, 3, 0],
        //  [1, 4, 4, 0],
        //  [0, 4, 4, 0]]
//        List<String> figures1 = List.of("D", "B", "A", "C");
//        int[][] result1 = solution(4, 4, figures1);
//        System.out.println("Example 1 Result:");
//        printMatrix(result1);

        // Example 2: n = 3, m = 5, figures = ["A", "D", "E"]
        // Expected Output:
        // [[1, 2, 0, 0, 0],
        //  [0, 2, 2, 3, 0],
        //  [0, 2, 3, 3, 3]]
        List<String> figures2 = List.of("A", "D", "E");
        int[][] result2 = solution(3, 5, figures2);
        System.out.println("\nExample 2 Result:");
        printMatrix(result2);
    }

    private static void printMatrix(int[][] matrix) {
        for (int[] row : matrix) {
            System.out.print("[");
            for (int i = 0; i < row.length; i++) {
                System.out.print(row[i] + (i < row.length - 1 ? ", " : ""));
            }
            System.out.println("]");
        }
    }
}
