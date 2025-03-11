#load "../AocUtil.csx"

using System;
using System.Collections.Immutable;

class Day25_ComboBreaker : AocUtil.Solver {

    public long Solve(ImmutableList<string> lines, int part) {
        long publicCard = Int64.Parse(lines[0]);
        long publicDoor = Int64.Parse(lines[1]);

        int loopSizeCard = 0;
        long valueCard = 1L;
        while (valueCard != publicCard) {
            valueCard = (valueCard * 7L) % 20201227;
            loopSizeCard++;
        }
        int loopSizeDoor = 0;
        long valueDoor = 1L;
        while (valueDoor != publicDoor) {
            valueDoor = (valueDoor * 7L) % 20201227;
            loopSizeDoor++;
        }
        Console.WriteLine("" + loopSizeCard + " " + loopSizeDoor);

        long key1 = 1L;
        for (long i = 0L; i < loopSizeCard; i++) {
            key1 = (key1 * publicDoor) % 20201227;
        }
        long key2 = 1L;
        for (long i = 0L; i < loopSizeDoor; i++) {
            key2 = (key2 * publicCard) % 20201227;
        }
        if (key1 != key2) {
            throw new SystemException("Keys don't match: " + key1 + " " + key2);
        }
        return key1;
    }
}

AocUtil.RunSolver(25, Args, new Day25_ComboBreaker());
