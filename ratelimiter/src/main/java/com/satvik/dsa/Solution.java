package com.satvik.dsa;

import java.util.HashSet;
import java.util.Set;
import java.util.Arrays;
import java.util.Objects;

class Solution {

    /**
     * A simple helper class (or 'record' in Java 16+) to store coordinates.
     * We must implement equals() and hashCode() so the HashSet works correctly.
     */
    private static class Coord {
        int r, c;

        Coord(int r, int c) {
            this.r = r;
            this.c = c;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Coord coord = (Coord) o;
            return r == coord.r && c == coord.c;
        }

        @Override
        public int hashCode() {
            // Objects.hash is a standard way to generate a good hash code.
            return Objects.hash(r, c);
        }
    }

    public char[][] solution(char[][] board) {
        if (board == null || board.length == 0) {
            return new char[0][0];
        }

        int Rows = board.length;
        int Cols = board[0].length;

        // Stores (row, col) of *where* all boxes will land, pre-explosion
        Set<Coord> landedBoxes = new HashSet<>();
        // Stores (row, col) of the *obstacles* that were hit and will explode
        Set<Coord> explodingObstacles = new HashSet<>();

        // --- Phase 1 & 2: Simulate Gravity and Find Explosion Triggers ---

        for (int c = 0; c < Cols; c++) { // Iterate through each column

            // This is the next available row for a box to land in this column.
            // We start at the bottom.
            int landRow = Rows - 1;

            // Scan this column from bottom-to-top
            for (int r = Rows - 1; r >= 0; r--) {
                char cell = board[r][c];

                if (cell == '*') {
                    // This obstacle is a new "floor".
                    // The next available spot is just above it.
                    landRow = r - 1;
                } else if (cell == '#') {
                    // This box will fall to the current 'landRow'

                    // Check if it lands on the board (it might be full)
                    if (landRow >= 0) {
                        Coord landingSpot = new Coord(landRow, c);
                        landedBoxes.add(landingSpot);

                        // Check what this box hit (using the *original* board)
                        int groundRow = landRow + 1;
                        if (groundRow < Rows && board[groundRow][c] == '*') {
                            // It hit an obstacle! Mark this obstacle as a detonator.
                            explodingObstacles.add(new Coord(groundRow, c));
                        }
                    }

                    // This spot is now "taken", so the next box *above* this one
                    // will land one row higher.
                    landRow--;
                }
            }
        }

        // --- Phase 3: Resolve Explosions ---

        // Start by assuming all landed boxes survive
        Set<Coord> survivingBoxes = new HashSet<>(landedBoxes);

        // Define the 8 directions for an explosion's blast radius
        int[][] deltas = {
                {-1, -1}, {-1, 0}, {-1, 1},
                { 0, -1},          { 0, 1},
                { 1, -1}, { 1, 0}, { 1, 1}
        };

        for (Coord obs : explodingObstacles) {
            // Check all 8 cells surrounding the exploding obstacle
            for (int[] delta : deltas) {
                int neighborR = obs.r + delta[0];
                int neighborC = obs.c + delta[1];
                Coord neighborPos = new Coord(neighborR, neighborC);

                // If a box *landed* in this neighboring cell,
                // it is destroyed and removed from the survivors.
                // This also handles the box that *caused* the explosion.
                survivingBoxes.remove(neighborPos);
            }
        }

        // --- Phase 4: Build the Final Board ---

        char[][] finalBoard = new char[Rows][Cols];

        // 1. Fill the board with empty cells
        for (char[] row : finalBoard) {
            Arrays.fill(row, '.');
        }

        // 2. Add back all the *original* obstacles (they don't get destroyed)
        for (int r = 0; r < Rows; r++) {
            for (int c = 0; c < Cols; c++) {
                if (board[r][c] == '*') {
                    finalBoard[r][c] = '*';
                }
            }
        }

        // 3. Add only the *surviving* boxes to their final landing spots
        for (Coord box : survivingBoxes) {
            finalBoard[box.r][box.c] = '#';
        }

        return finalBoard;
    }
}
