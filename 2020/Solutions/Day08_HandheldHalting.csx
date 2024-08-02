#load "../AocUtil.csx"

using System;
using System.Collections.Immutable;

class Day08_HandheldHalting : AocUtil.Solver {

    public long Solve(ImmutableList<string> lines, int part) {
        var operations = new List<string>();
        var arguments = new List<int>();
        var ipHasExecuted = new List<bool>();
        foreach (string line in lines) {
            operations.Add(line.Substring(0, 3));
            arguments.Add(Int32.Parse(line.Substring(4)));
            ipHasExecuted.Add(false);
        }

        int ipFlip = 0;
        while (true) {
            if (part == 2) {
                while (ipFlip < operations.Count && operations[ipFlip] == "acc") {
                    ipFlip++;
                }
                if (ipFlip == operations.Count) {
                    throw new SystemException("Unable to find operation to flip.");
                }
            }
            int acc = 0;
            int ip = 0;
            while (ip >= 0 && ip < operations.Count) {
                if (ipHasExecuted[ip]) {
                    if (part == 1) {
                        return acc;
                    } else {
                        break;
                    }
                }
                ipHasExecuted[ip] = true;
                string operation = operations[ip];
                if (part == 2 && ipFlip == ip) {
                    operation = (operation == "jmp") ? "nop" : "jmp";
                }
                if (operation == "acc") {
                    acc += arguments[ip];
                    ip++;
                } else if (operation == "jmp") {
                    ip += arguments[ip];
                } else if (operation == "nop") {
                    ip++;
                }
            }
            if (part == 1) {
                throw new SystemException("Instruction pointer is out of range [0-" + operations.Count + "]: " + ip);
            }
            if (ip == operations.Count) {
                Console.WriteLine("ipFlip: " + ipFlip);
                return acc;
            }
            ipFlip++;
            for (int i = 0; i < ipHasExecuted.Count; i++) {
                ipHasExecuted[i] = false;
            }
        }
    }
}

AocUtil.RunSolver(8, Args, new Day08_HandheldHalting());
