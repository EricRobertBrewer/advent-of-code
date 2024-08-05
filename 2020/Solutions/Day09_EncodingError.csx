#load "../AocUtil.csx"

using System;
using System.Collections.Immutable;

class Day09_EncodingError : AocUtil.Solver {

    public long Solve(ImmutableList<string> lines, int part) {
        var series = new List<long>();
        foreach (string line in lines) {
            series.Add(Int64.Parse(line));
        }

        int preambleCount = 25;

        var sumToCount = new Dictionary<long, int>();
        int iInvalid = -1;
        for (int i = 0; i < series.Count; i++) {
            long x = series[i];
            if (i >= preambleCount) {
                if (!sumToCount.ContainsKey(x)) {
                    iInvalid = i;
                    break;
                }
                long xRemove = series[i - preambleCount];
                for (int j = i - preambleCount + 1; j < i; j++) {
                    long sumRemove = xRemove + series[j];
                    if (sumToCount[sumRemove] == 1) {
                        sumToCount.Remove(sumRemove);
                    } else {
                        sumToCount[sumRemove] = sumToCount[sumRemove] - 1;
                    }
                }
            }
            for (int j = Math.Max(0, i - preambleCount + 1); j < i; j++) {
                long sum = x + series[j];
                if (!sumToCount.ContainsKey(sum)) {
                    sumToCount.Add(sum, 1);
                } else {
                    sumToCount[sum] = sumToCount[sum] + 1;
                }
            }
        }
        if (iInvalid == -1) {
            throw new SystemException("Unable to find misplaced number in series.");
        }
        Console.WriteLine(iInvalid);

        if (part == 1) {
            return series[iInvalid];
        }

        for (int window = 2; window < iInvalid - 1; window++) {
            long sum = 0L;
            for (int i = 0; i < iInvalid - 1; i++) {
                long x = series[i];
                if (i >= window) {
                    sum -= series[i - window];
                }
                sum += x;
                if (sum == series[iInvalid]) {
                    Console.WriteLine(window);
                    Console.WriteLine(i);
                    long min = series[i - window + 1];
                    long max = series[i - window + 1];
                    for (int j = i - window + 2; j <= i; j++) {
                        if (series[j] < min) {
                            min = series[j];
                        }
                        if (series[j] > max) {
                            max = series[j];
                        }
                    }
                    return min + max;
                }
            }
        }
        return -1L;
    }
}

AocUtil.RunSolver(9, Args, new Day09_EncodingError());
