#load "../AocUtil.csx"

using System;
using System.Collections.Immutable;

class Day20_JurassicJigsaw : AocUtil.Solver {

    private const int TILE_SIZE = 10;
    private const int Z = TILE_SIZE - 1;

    public long Solve(ImmutableList<string> lines, int part) {
        // Read tiles.
        var tileIds = new List<int>();
        var tiles = new List<string[]>();
        int iLine = 0;
        while (iLine < lines.Count) {
            string lineId = lines[iLine];
            if (!lineId.StartsWith("Tile ")) {
                throw new SystemException("Unexpected line for tile ID: " + lineId);
            }
            tileIds.Add(Int32.Parse(lineId.Substring(5, lineId.Length - 6))); // Subtract "Tile " (5) and ":" (1).
            iLine++;
            string[] tile = new string[TILE_SIZE];
            for (int i = 0; i < TILE_SIZE; i++) {
                if (lines[iLine].Length != TILE_SIZE) {
                    throw new SystemException("Unexpected line for a tile: " + lines[iLine]);
                }
                tile[i] = lines[iLine];
                iLine++;
            }
            tiles.Add(tile);
            iLine++;
        }
        int puzzleSize = (int) Math.Sqrt(tiles.Count);
        if (puzzleSize * puzzleSize != tiles.Count) {
            throw new SystemException("Non-square number of tiles: " + tiles.Count);
        }

        // Index tile edges.
        var borderToTileIndexSides = new Dictionary<string, List<List<int>>>();
        for (int i = 0; i < tiles.Count; i++) {
            string[] tile = tiles[i];
            string[] borders = new string[] {
                Border(tile,  0,  0,  0,  1), // Top.
                Border(tile,  0,  Z,  1,  0), // Right.
                Border(tile,  Z,  Z,  0, -1), // Bottom.
                Border(tile,  Z,  0, -1,  0), // Left.
            };
            for (int side = 0; side < borders.Length; side++) {
                string border = borders[side];
                if (!borderToTileIndexSides.ContainsKey(border)) {
                    borderToTileIndexSides[border] = new List<List<int>>();
                }
                borderToTileIndexSides[border].Add(new List<int> {i, side});
            }
        }

        // Find unique borders, i.e., guaranteed edges (and consequently, corners).
        var tileIndexToEdgeSides = new Dictionary<int, List<int>>();
        foreach (string border in borderToTileIndexSides.Keys) {
            List<List<int>> tileIndexSides = borderToTileIndexSides[border];
            string reverse = Reverse(border);
            if (tileIndexSides.Count == 1 && !borderToTileIndexSides.ContainsKey(reverse)) {
                int tileIndex = tileIndexSides[0][0];
                if (!tileIndexToEdgeSides.ContainsKey(tileIndex)) {
                    tileIndexToEdgeSides[tileIndex] = new List<int>();
                }
                tileIndexToEdgeSides[tileIndex].Add(tileIndexSides[0][1]);
            }
        }

        // Find four corners (if possible).
        var cornerIndices = new List<int>();
        foreach (int tileIndex in tileIndexToEdgeSides.Keys) {
            if (tileIndexToEdgeSides[tileIndex].Count == 2) {
                if (tileIndexToEdgeSides[tileIndex][0] % 2 == tileIndexToEdgeSides[tileIndex][1] % 2) {
                    throw new SystemException("Found tile whose opposite sides are unique: Tile " + tileIds[tileIndex]);
                }
                cornerIndices.Add(tileIndex);
            }
        }

        if (part == 1) {
            if (cornerIndices.Count < 4) {
                throw new SystemException("Unable to find four corners...");
            }
            long product = 1L;
            foreach (int cornerIndex in cornerIndices) {
                product *= tileIds[cornerIndex];
            }
            return product;
        }

        // Assemble puzzle.
        if (cornerIndices.Count == 0) {
            throw new SystemException("Unable to find any corners...");
        }
        bool[] isTileUsed = new bool[tiles.Count];
        for (int i = 0; i < isTileUsed.Length; i++) {
            isTileUsed[i] = false;
        }
        int[,,] puzzle = new int[puzzleSize,puzzleSize,2]; // Last items are [tileIndex, topSide].
        // Top left corner of entire puzzle.
        puzzle[0,0,0] = cornerIndices[0];
        if ((tileIndexToEdgeSides[cornerIndices[0]][0] + 3) % 4 == tileIndexToEdgeSides[cornerIndices[0]][1]) {
            puzzle[0,0,1] = tileIndexToEdgeSides[cornerIndices[0]][0];
        } else {
            puzzle[0,0,1] = tileIndexToEdgeSides[cornerIndices[0]][1];
        }
        isTileUsed[cornerIndices[0]] = true;
        // All rows.
        for (int i = 0; i < puzzleSize; i++) {
            for (int j = 0; j < puzzleSize; j++) {
                if (i == 0 && j == 0) {
                    continue;
                }
            }
        }
        return -1L;
    }

    private static string Border(string[] tile, int iStart, int jStart, int di, int dj) {
        char[] cs = new char[TILE_SIZE];
        int i = iStart;
        int j = jStart;
        for (int t = 0; t < TILE_SIZE; t++) {
            cs[t] = tile[i][j];
            i += di;
            j += dj;
        }
        return String.Join("", cs);
    }

    private static string Reverse(string s) {
        if (s == null || s.Length < 2) {
            return s;
        }
        char[] cs = new char[s.Length];
        for (int i = 0; i < s.Length; i++) {
            cs[s.Length - 1 - i] = s[i];
        }
        return String.Join("", cs);
    }
}

AocUtil.RunSolver(20, Args, new Day20_JurassicJigsaw());
