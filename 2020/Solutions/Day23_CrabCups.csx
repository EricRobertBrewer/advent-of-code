#load "../AocUtil.csx"

using System;
using System.Collections.Immutable;

class Day23_CrabCups : AocUtil.Solver {

    public long Solve(ImmutableList<string> lines, int part) {
        var labelToCup = new Dictionary<int, Cup>();
        Cup cupCurrent = null;
        Cup cupPrev = null;
        var labelsInput = new HashSet<int>(); // Verify labels are {1, 2, ..., n}.
        for (int i = 0; i < lines[0].Length; i++) {
            int label = Int32.Parse(lines[0].Substring(i, 1));
            Cup cup = new Cup(label);
            labelToCup[label] = cup;
            if (cupCurrent == null) {
                cupCurrent = cup;
            }
            if (cupPrev != null) {
                cupPrev.Next = cup;
            }
            cupPrev = cup;
            labelsInput.Add(label);
        }
        for (int label = 1; label <= lines[0].Length; label++) {
            if (!labelsInput.Contains(label)) {
                throw new SystemException("Label " + label + " not found.");
            }
        }
        if (part != 1) {
            for (int label = labelToCup.Keys.Count + 1; label <= 1000000; label++) {
                Cup cup = new Cup(label);
                labelToCup[label] = cup;
                cupPrev.Next = cup;
                cupPrev = cup;
            }
        }
        cupPrev.Next = cupCurrent;

        int n = labelToCup.Keys.Count;
        int moves = (part == 1) ? 100 : 10000000;
        for (int move = 0; move < moves; move++) {
            // Pick up 3 cups and shift.
            Cup cupPick = cupCurrent.Next;
            cupCurrent.Next = cupPick.Next.Next.Next;
            // Choose destination label.
            int label = (cupCurrent.Label == 1) ? n : cupCurrent.Label - 1;
            while (label == cupPick.Label || label == cupPick.Next.Label || label == cupPick.Next.Next.Label) {
                label = (label == 1) ? n : label - 1;
            }
            Cup cupDest = labelToCup[label];
            // Insert picked cups to right of destination cup.
            Cup cupDestNext = cupDest.Next;
            cupDest.Next = cupPick;
            cupPick.Next.Next.Next = cupDestNext;
            // Move clockwise.
            cupCurrent = cupCurrent.Next;
        }

        if (part == 1) {
            String answer = "";
            Cup cup = labelToCup[1].Next;
            while (cup.Label != 1) {
                answer += "" + cup.Label;
                cup = cup.Next;
            }
            Console.WriteLine(answer);
            return 0;
        }

        return ((long)labelToCup[1].Next.Label) * ((long)labelToCup[1].Next.Next.Label);
    }
}

private class Cup {

    public int Label;
    public Cup Next;

    public Cup(int label) {
        Label = label;
    }
}

AocUtil.RunSolver(23, Args, new Day23_CrabCups());
