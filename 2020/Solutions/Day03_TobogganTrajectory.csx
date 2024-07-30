#load "../AocUtil.csx"

using System;
using System.Collections.Immutable;

class Day03_TobogganTrajectory : AocUtil.Solver {

    public long Solve(ImmutableList<string> lines, int part) {
        int[][] slopes = new int[][] {
            new int[] {1, 1}, new int[] {3, 1}, new int[] {5, 1}, new int[] {7, 1}, new int[] {1, 2}
        };
        if (part == 1) {
            slopes = new int[][] { new int[] {3, 1} };
        }

        long hitsProduct = 1L;
        foreach (int[] slope in slopes) {
            int hits = 0;
            int i = 0;
            int j = 0;
            while (i < lines.Count()) {
                if (lines[i][j] == '#') {
                    hits++;
                }
                i += slope[1];
                j = (j + slope[0]) % lines[0].Count();
            }
            hitsProduct *= hits;
        }
        return hitsProduct;
    }
}

AocUtil.RunSolver(3, Args, new Day03_TobogganTrajectory());
