#load "../AocUtil.csx"

using System;
using System.Collections.Immutable;

class Day10_AdapterArray : AocUtil.Solver {

    public long Solve(ImmutableList<string> lines, int part) {
        var outputs = new List<int>();
        foreach (string line in lines) {
            outputs.Add(Int32.Parse(line));
        }

        outputs.Add(0); // Outlet at seat.
        outputs.Sort();
        outputs.Add(outputs[outputs.Count - 1] + 3); // Adapter of device.

        if (part == 1) {
            int differenceCount1 = 0;
            int differenceCount3 = 0;
            for (int i = 1; i < outputs.Count; i++) {
                int difference = outputs[i] - outputs[i - 1];
                if (difference == 1) {
                    differenceCount1++;
                } else if (difference == 3) {
                    differenceCount3++;
                } else if (difference > 3) {
                    throw new SystemException("Found a jolt difference greater than 3 at index " + i + ": " + outputs[i - 1] + " " + outputs[i]);
                }
            }
            return differenceCount1 * differenceCount3;
        }

        var outputWays = new long[outputs.Count];
        outputWays[outputWays.Length - 1] = 1L;
        for (int i = outputWays.Length - 2; i >= 0; i--) {
            int output = outputs[i];
            long ways = 0L;
            for (int j = 1; j <= 3; j++) {
                if (i + j >= outputs.Count || outputs[i + j] - output > 3) {
                    break;
                }
                ways += outputWays[i + j];
            }
            outputWays[i] = ways;
        }
        return outputWays[0];
    }
}

AocUtil.RunSolver(10, Args, new Day10_AdapterArray());
