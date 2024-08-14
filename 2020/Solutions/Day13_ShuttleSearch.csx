#load "../AocUtil.csx"

using System;
using System.Collections.Immutable;
using System.Numerics; // BigInteger.

class Day13_ShuttleSearch : AocUtil.Solver {

    public long Solve(ImmutableList<string> lines, int part) {
        int timeDepart = Int32.Parse(lines[0]);
        string[] buses = lines[1].Split(",");

        if (part == 1) {
            int idMin = -1;
            int waitMin = -1;
            foreach (string bus in buses) {
                if (bus == "x") {
                    continue;
                }
                int id = Int32.Parse(bus);
                int wait = ((timeDepart / id + 1) * id) - timeDepart;
                if (waitMin == -1 || wait < waitMin) {
                    waitMin = wait;
                    idMin = id;
                }
            }
            Console.WriteLine(idMin);
            Console.WriteLine(waitMin);
            return idMin * waitMin;
        }

        // https://en.wikipedia.org/wiki/Chinese_remainder_theorem#Using_the_existence_construction
        var a = new List<long>();
        var n = new List<long>();
        for (int i = 0; i < buses.Length; i++) {
            if (buses[i] == "x") {
                continue;
            }
            long id = Int64.Parse(buses[i]);
            long aI;
            if (i % id == 0) {
                aI = 0;
            } else {
                long q = i / id;
                aI = id * (q + 1) - i; // Calculate positive version of: -a (mod n).
            }
            a.Add(aI);
            n.Add(id);
        }

        BigInteger aCurrent = a[0];
        BigInteger nCurrent = n[0];
        for (int i = 1; i < a.Count; i++) {
            // https://en.wikipedia.org/wiki/Extended_Euclidean_algorithm#Description
            long rMinusOne = (long)nCurrent;
            long r = n[i];
            long sMinusOne = 1L;
            long s = 0L;
            long tMinusOne = 0L;
            long t = 1L;
            while (rMinusOne % r != 0) {
                long q = rMinusOne / r;
                long rNext = rMinusOne - q * r; // === rMinusOne % r
                long sNext = sMinusOne - q * s;
                long tNext = tMinusOne - q * t;
                rMinusOne = r;
                r = rNext;
                sMinusOne = s;
                s = sNext;
                tMinusOne = t;
                t = tNext;
            }

//             Console.WriteLine("aCurrent = " + aCurrent + " * " + t + " * " + n[i] + " + " + a[i] + " * " + s + " * " + nCurrent);
            aCurrent = aCurrent * t * n[i] + a[i] * s * nCurrent; // Overflows here if not `BigInteger`.
            nCurrent = nCurrent * n[i];
            aCurrent = aCurrent % nCurrent;
            if (aCurrent < 0L) {
                aCurrent += nCurrent;
            }
//             Console.WriteLine("         = " + aCurrent);
//             Console.WriteLine("nCurrent = " + nCurrent);
        }
        return (long)aCurrent;
    }
}

AocUtil.RunSolver(13, Args, new Day13_ShuttleSearch());
