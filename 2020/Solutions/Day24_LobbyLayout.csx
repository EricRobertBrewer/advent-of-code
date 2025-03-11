#load "../AocUtil.csx"

using System;
using System.Collections.Immutable;

class Day24_LobbyLayout : AocUtil.Solver {

    public long Solve(ImmutableList<string> lines, int part) {
        var tileToBlack = new Dictionary<(int i, int j), bool>();
        foreach (string line in lines) {
            int i = 0, j = 0;
            int k = 0;
            while (k < line.Length) {
                if (line[k] == 'e') {
                    j += 1;
                    k += 1;
                } else if (line[k] == 'w') {
                    j -= 1;
                    k += 1;
                } else if (line[k] == 'n') {
                    i -= 1;
                    if (line[k + 1] == 'w') {
                        j -= 1;
                    } else if (line[k + 1] != 'e') {
                        throw new SystemException("Unexpected direction: n" + line[k + 1]);
                    }
                    k += 2;
                } else if (line[k] == 's') {
                    i += 1;
                    if (line[k + 1] == 'e') {
                        j += 1;
                    } else if (line[k + 1] != 'w') {
                        throw new SystemException("Unexpected direction: s" + line[k + 1]);
                    }
                    k += 2;
                } else {
                    throw new SystemException("Unexpected direction: " + line[k]);
                }
            }

            (int i, int j) tile = (i, j);
            if (!tileToBlack.ContainsKey(tile)) {
                tileToBlack[tile] = true;
            } else {
                tileToBlack[tile] = !tileToBlack[tile];
            }
        }

        if (part != 1) {
            var deltas = new (int i, int j)[] {(-1, -1), (-1, 0), (0, -1), (0, 1), (1, 0), (1, 1)};
            for (int day = 0; day < 100; day++) {
                var tileToBlackAdjacent = new Dictionary<(int i, int j), int>();
                foreach ((int i, int j) tile in tileToBlack.Keys) {
                    if (!tileToBlackAdjacent.ContainsKey(tile)) {
                        tileToBlackAdjacent[tile] = 0;
                    }
                    if (tileToBlack[tile]) {
                        foreach ((int i, int j) delta in deltas) {
                            (int i, int j) neighbor = (tile.i + delta.i, tile.j + delta.j);
                            if (!tileToBlackAdjacent.ContainsKey(neighbor)) {
                                tileToBlackAdjacent[neighbor] = 0;
                            }
                            tileToBlackAdjacent[neighbor]++;
                        }
                    }
                }

                var tileToBlackNext = new Dictionary<(int i, int j), bool>();
                foreach ((int i, int j) tile in tileToBlackAdjacent.Keys) {
                    int blackAdjacent = tileToBlackAdjacent[tile];
                    if (tileToBlack.ContainsKey(tile) && tileToBlack[tile]) {
                        tileToBlackNext[tile] = !(blackAdjacent == 0 || blackAdjacent > 2);
                    } else {
                        tileToBlackNext[tile] = blackAdjacent == 2;
                    }
                }
                tileToBlack = tileToBlackNext;
            }
        }

        long answer = 0L;
        foreach ((int i, int j) tile in tileToBlack.Keys) {
            if (tileToBlack[tile]) {
                answer++;
            }
        }
        return answer;
    }
}

AocUtil.RunSolver(24, Args, new Day24_LobbyLayout());
