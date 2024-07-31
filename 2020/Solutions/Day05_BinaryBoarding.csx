#load "../AocUtil.csx"

using System;
using System.Collections.Immutable;

class Day05_BinaryBoarding : AocUtil.Solver {

    public long Solve(ImmutableList<string> lines, int part) {
        var seatIds = new List<int>();
        foreach (string line in lines) {
            // Row.
            int low = 0, high = 128;
            for (int i = 0; i < 7; i++) {
                char c = line[i];
                int mid = (low + high) / 2;
                if (c == 'F') {
                    high = mid;
                } else if (c == 'B') {
                    low = mid;
                } else {
                    throw new SystemException("Unexpected char in `" + line + "`: " + c);
                }
            }
            int row = low;

            // Column.
            low = 0; high = 8;
            for (int i = 7; i < 10; i++) {
                char c = line[i];
                int mid = (low + high) / 2;
                if (c == 'L') {
                    high = mid;
                } else if (c == 'R') {
                    low = mid;
                } else {
                    throw new SystemException("Unexpected char in `" + line + "`: " + c);
                }
            }
            int column = low;

            seatIds.Add(row * 8 + column);
        }

        if (part == 1) {
            return seatIds.Max();
        }

        seatIds.Sort();
        for (int i = 1; i < seatIds.Count(); i++) {
            if (seatIds[i] - 1 != seatIds[i - 1]) {
                return seatIds[i] - 1;
            }
        }
        throw new SystemException("Unable to find missing seat ID.");
    }
}

AocUtil.RunSolver(5, Args, new Day05_BinaryBoarding());
