#load "../AocUtil.csx"

using System;
using System.Collections.Immutable;

class Day17_ConwayCubes : AocUtil.Solver {

    public long Solve(ImmutableList<string> lines, int part) {
        var cubesActive = new HashSet<(int x, int y, int z, int w)>();
        for (int y = 0; y < lines.Count; y++) {
            string line = lines[y];
            for (int x = 0; x < line.Length; x++) {
                if (line[x] == '#') {
                    cubesActive.Add((x, y, 0, 0));
                }
            }
        }

        int cycle = 0;
        int cyclesMax = 6;
        while (cycle < cyclesMax) {
            // Count neighbors.
            var cubeToNeighbors = new Dictionary<(int x, int y, int z, int w), int>();
            foreach ((int x, int y, int z, int w) cube in cubesActive) {
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        for (int dz = -1; dz <= 1; dz++) {
                            if (part == 1) {
                                if (dx == 0 && dy == 0 && dz == 0) {
                                    continue;
                                }
                                (int x, int y, int z, int w) other = (cube.x + dx, cube.y + dy, cube.z + dz, cube.w);
                                if (!cubeToNeighbors.ContainsKey(other)) {
                                    cubeToNeighbors[other] = 0;
                                }
                                cubeToNeighbors[other]++;
                            } else {
                                for (int dw = -1; dw <= 1; dw++) {
                                    if (dx == 0 && dy == 0 && dz == 0 && dw == 0) {
                                        continue;
                                    }
                                    (int x, int y, int z, int w) other = (cube.x + dx, cube.y + dy, cube.z + dz, cube.w + dw);
                                    if (!cubeToNeighbors.ContainsKey(other)) {
                                        cubeToNeighbors[other] = 0;
                                    }
                                    cubeToNeighbors[other]++;
                                }
                            }
                        }
                    }
                }
            }

            // Calculate next cycle.
            var cubesActiveNext = new HashSet<(int x, int y, int z, int w)>();
            foreach ((int x, int y, int z, int w) cube in cubeToNeighbors.Keys) {
                int neighbors = cubeToNeighbors[cube];
                if (cubesActive.Contains(cube) && (neighbors == 2 || neighbors == 3)) {
                    cubesActiveNext.Add(cube);
                } else if (!cubesActive.Contains(cube) && neighbors == 3) {
                    cubesActiveNext.Add(cube);
                }
            }
            cubesActive = cubesActiveNext;
            cycle++;
        }
        return cubesActive.Count;
    }
}

AocUtil.RunSolver(17, Args, new Day17_ConwayCubes());
