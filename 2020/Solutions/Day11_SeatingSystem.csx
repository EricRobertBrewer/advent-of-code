#load "../AocUtil.csx"

using System;
using System.Collections.Immutable;

class Day11_SeatingSystem : AocUtil.Solver {

    public long Solve(ImmutableList<string> lines, int part) {
        // Verify input is a rectangle.
        for (int i = 1; i < lines.Count; i++) {
            if (lines[i].Length != lines[0].Length) {
                throw new SystemException("Length of rows 0 and " + i + " are different: " + lines[0].Length + " and " + lines[i].Length);
            }
        }
        int h = lines.Count;
        int w = lines[0].Length;

        int[,] deltas = { {-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}, {1, 1} };
        int occupiedThreshold = (part == 1) ? 4 : 5;

        var grid = new char[h,w];
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                grid[i,j] = lines[i][j];
            }
        }

        while (true) {
            var gridNext = new char[h,w];
            bool changed = false;
            for (int i = 0; i < h; i++) {
                for (int j = 0; j < w; j++) {
                    // Count adjacent/visible occupied seats.
                    int occupiedAdjacent = 0;
                    if (part == 1) {
                        for (int d = 0; d < deltas.GetLength(0); d++) {
                            int ia = i + deltas[d,0];
                            int ja = j + deltas[d,1];
                            if (ia < 0 || ia >= h || ja < 0 || ja >= w) {
                                continue;
                            }
                            if (grid[ia,ja] == '#') {
                                occupiedAdjacent++;
                            }
                        }
                    } else {
                        for (int d = 0; d < deltas.GetLength(0); d++) {
                            int ia = i + deltas[d,0];
                            int ja = j + deltas[d,1];
                            bool seesOccupied = false;
                            while (ia >= 0 && ia < h && ja >= 0 && ja < w) {
                                if (grid[ia,ja] == '#') {
                                    seesOccupied = true;
                                    break;
                                } else if (grid[ia,ja] == 'L') {
                                    break;
                                }
                                ia += deltas[d,0];
                                ja += deltas[d,1];
                            }
                            if (seesOccupied) {
                                occupiedAdjacent++;
                            }
                        }
                    }

                    // Update next grid.
                    char c = grid[i,j];
                    if (c == 'L' && occupiedAdjacent == 0) {
                        gridNext[i,j] = '#';
                        changed = true;
                    } else if (c == '#' && occupiedAdjacent >= occupiedThreshold) {
                        gridNext[i,j] = 'L';
                        changed = true;
                    } else {
                        gridNext[i,j] = grid[i,j];
                    }
                }
            }

            if (!changed) {
                break;
            }
            grid = gridNext;
        }

        int occupied = 0;
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                if (grid[i,j] == '#') {
                    occupied++;
                }
            }
        }
        return occupied;
    }
}

AocUtil.RunSolver(11, Args, new Day11_SeatingSystem());
