#load "../AocUtil.csx"

using System;
using System.Collections.Immutable;

class Day15_RambunctiousRecitation : AocUtil.Solver {

    public long Solve(ImmutableList<string> lines, int part) {
        var starting = new List<int>();
        foreach (string s in lines[0].Split(",")) {
            starting.Add(Int32.Parse(s));
        }

        var numberToTurn = new Dictionary<int, int>();
        var numberToTurnLast = new Dictionary<int, int>();
        int numberLast = -1;
        int turn = 1;
        while (true) {
            int number;
            if (turn <= starting.Count) {
                number = starting[turn - 1];
            } else if (!numberToTurnLast.ContainsKey(numberLast)) {
                number = 0;
            } else {
                number = numberToTurn[numberLast] - numberToTurnLast[numberLast];
            }
            if ((part == 1 && turn == 2020) || (part == 2 && turn == 30000000)) {
                return number;
            }

            if (numberToTurn.ContainsKey(number)) {
                numberToTurnLast[number] = numberToTurn[number];
            }
            numberToTurn[number] = turn;
            numberLast = number;
            turn++;
        }
    }
}

AocUtil.RunSolver(15, Args, new Day15_RambunctiousRecitation());
