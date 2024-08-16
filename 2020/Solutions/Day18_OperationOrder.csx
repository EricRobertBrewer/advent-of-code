#load "../AocUtil.csx"

using System;
using System.Collections.Immutable;

class Day18_OperationOrder : AocUtil.Solver {

    public long Solve(ImmutableList<string> lines, int part) {
        long sum = 0L;
        foreach (string line in lines) {
            int[] i = { 0 };
            long value = Evaluate(line.Replace(" ", string.Empty), i, part == 2); // Remove spaces.
            sum += value;
        }
        return sum;
    }

    static long Evaluate(string s, int[] i, bool isAdditionFirst) {
        long x;
        if (s[i[0]] == '(') {
            i[0]++;
            x = Evaluate(s, i, isAdditionFirst);
        } else {
            x = Int64.Parse(s.Substring(i[0], 1)); // All literals are single-digit.
        }
        i[0]++;

        var multStack = new List<long>();
        while (i[0] < s.Length) {
            if (s[i[0]] == ')') {
                break;
            }

            char op = s[i[0]];
            i[0]++;

            long y;
            if (s[i[0]] == '(') {
                i[0]++;
                y = Evaluate(s, i, isAdditionFirst);
            } else {
                y = Int64.Parse(s.Substring(i[0], 1));
            }
            i[0]++;

            if (op == '+') {
                x += y;
            } else if (op == '*') {
                if (isAdditionFirst) {
                    multStack.Add(x);
                    x = y;
                } else {
                    x *= y;
                }
            } else {
                throw new SystemException("Unrecognized operator: " + op);
            }
        }
        foreach (long m in multStack) {
            x *= m;
        }
        return x;
    }
}

AocUtil.RunSolver(18, Args, new Day18_OperationOrder());
