#load "../AoCUtil.csx"

using System;
using System.Collections.Immutable;

class Day01_ReportRepair : AocUtil.Solver {

    public long Solve(ImmutableList<string> lines, int part) {
        var values = new List<int>();
        foreach (string line in lines) {
            values.Add(Int32.Parse(line));
        }

        if (part == 1) {
            for (int i = 0; i < values.Count() - 1; i++) {
                int a = values[i];
                for (int j = i + 1; j < values.Count(); j++) {
                    int b = values[j];
                    if (a + b == 2020) {
                        return a * b;
                    }
                }
            }
        } else {
            for (int i = 0; i < values.Count() - 2; i++) {
                int a = values[i];
                for (int j = i + 1; j < values.Count() - 1; j++) {
                    int b = values[j];
                    for (int k = j + 1; k < values.Count(); k++) {
                        int c = values[k];
                        if (a + b + c == 2020) {
                            return a * b * c;
                        }
                    }
                }
            }
        }
        throw new SystemException("Unable to find sum of 2020.");
    }
}

AocUtil.RunSolver(1, Args, new Day01_ReportRepair());
