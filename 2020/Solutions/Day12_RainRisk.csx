#load "../AocUtil.csx"

using System;
using System.Collections.Immutable;

class Day12_RainRisk : AocUtil.Solver {

    public long Solve(ImmutableList<string> lines, int part) {
        int n = lines.Count;
        var actions = new char[n];
        var values = new int[n];
        for (int i = 0; i < n; i++) {
            actions[i] = lines[i][0];
            values[i] = Int32.Parse(lines[i].Substring(1));
        }

        int[][] deltas = new int[][] { new int[] {-1, 0}, new int[] {0, 1}, new int[] {1, 0}, new int[] {0, -1} };

        int y = 0;
        int x = 0;
        int yWaypoint = -1;
        int xWaypoint = 10;
        int direction = 1;
        for (int i = 0; i < n; i++) {
            char action = actions[i];
            int value = values[i];
            if (part == 1) {
                if (action == 'N') {
                    y -= value;
                } else if (action == 'S') {
                    y += value;
                } else if (action == 'E') {
                    x += value;
                } else if (action == 'W') {
                    x -= value;
                } else if (action == 'L') {
                    direction = (direction - (value / 90) + 4) % 4;
                } else if (action == 'R') {
                    direction = (direction + (value / 90)) % 4;
                } else if (action == 'F') {
                    int[] delta = deltas[direction];
                    y += value * delta[0];
                    x += value * delta[1];
                }
            } else {
                if (action == 'N') {
                    yWaypoint -= value;
                } else if (action == 'S') {
                    yWaypoint += value;
                } else if (action == 'E') {
                    xWaypoint += value;
                } else if (action == 'W') {
                    xWaypoint -= value;
                } else if (action == 'L' || action == 'R') {
                    if ((action == 'L' && value == 90) || (action == 'R' && value == 270)) {
                        int t = yWaypoint; // (10, 4) -> (-4, 10)
                        yWaypoint = -xWaypoint;
                        xWaypoint = t;
                    } else if (value == 180) {
                        yWaypoint = -yWaypoint;
                        xWaypoint = -xWaypoint;
                    } else if ((action == 'L' && value == 270) || (action == 'R' && value == 90)) {
                        int t = yWaypoint; // (-4, 10) -> (10, 4)
                        yWaypoint = xWaypoint;
                        xWaypoint = -t;
                    }
                } else if (action == 'F') {
                    y += value * yWaypoint;
                    x += value * xWaypoint;
                }
            }
        }
        return Math.Abs(y) + Math.Abs(x);
    }
}

AocUtil.RunSolver(12, Args, new Day12_RainRisk());
