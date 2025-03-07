#load "../AocUtil.csx"

using System;
using System.Collections.Immutable;

class Day20_JurassicJigsaw : AocUtil.Solver {

    private const int TILE_SIZE = 10;

    public long Solve(ImmutableList<string> lines, int part) {
        // Read tiles.
        var tileIds = new List<int>();
        var tiles = new List<string[]>();
        int iLine = 0;
        while (iLine < lines.Count) {
            string lineId = lines[iLine];
            if (!lineId.StartsWith("Tile ") || !lineId.EndsWith(":")) {
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

        // Index tile borders.
        var borderToTileIndexToSides = new Dictionary<string, Dictionary<int, List<int>>>();
        for (int t = 0; t < tiles.Count; t++) {
            string[] tile = tiles[t];
            string[] borders = new string[] {
                Border(tile,  0), Border(tile,  1), Border(tile,  2), Border(tile,  3)
            };
            for (int side = 0; side < borders.Length; side++) {
                string border = borders[side];
                if (!borderToTileIndexToSides.ContainsKey(border)) {
                    borderToTileIndexToSides[border] = new Dictionary<int, List<int>>();
                }
                if (!borderToTileIndexToSides[border].ContainsKey(t)) {
                    borderToTileIndexToSides[border][t] = new List<int>();
                }
                borderToTileIndexToSides[border][t].Add(side);
            }
        }

        // Find borders with no complement, i.e., guaranteed edges (and consequently, corners).
        var tileIndexToEdgeSides = new Dictionary<int, List<int>>();
        foreach (string border in borderToTileIndexToSides.Keys) {
            Dictionary<int, List<int>> tileIndexToSides = borderToTileIndexToSides[border];
            if (tileIndexToSides.Keys.Count > 1) {
                continue; // Tile can be flipped to match side of other - not necessarily an edge.
            }
            int t = SomeKey(tileIndexToSides);
            string reverse = Reverse(border);
            if (!borderToTileIndexToSides.ContainsKey(reverse) || // No complement at all.
                (borderToTileIndexToSides[reverse].Keys.Count == 1 &&
                    SomeKey(borderToTileIndexToSides[reverse]) == t)) { // Only complement is itself.
                if (!tileIndexToEdgeSides.ContainsKey(t)) {
                    tileIndexToEdgeSides[t] = new List<int>();
                }
                foreach (int side in tileIndexToSides[t]) {
                    tileIndexToEdgeSides[t].Add(side);
                }
            }
        }

        // Find four corners (if possible).
        var cornerIndices = new List<int>();
        foreach (int t in tileIndexToEdgeSides.Keys) {
            if (tileIndexToEdgeSides[t].Count > 2) {
                throw new SystemException("Found tile with " + tileIndexToEdgeSides[t].Count + " edges: " + t);
            }
            if (tileIndexToEdgeSides[t].Count == 2) {
                if (tileIndexToEdgeSides[t][0] % 2 == tileIndexToEdgeSides[t][1] % 2) {
                    throw new SystemException("Found tile whose opposite sides are edges: Tile " + t + "(" + tileIds[t] + ")");
                }
                cornerIndices.Add(t);
            }
        }

        // Skip assembly, if possible.
        if (part == 1 && cornerIndices.Count == 4) {
            long product = 1L;
            foreach (int t in cornerIndices) {
                product *= tileIds[t];
            }
            return product;
        }

        // Assemble puzzle. In general, this may be necessary to complete part 1.
        if (cornerIndices.Count == 0) {
            throw new SystemException("Unable to easily find any corners...");
        }
        int[,] puzzleIndices = new int[puzzleSize,puzzleSize];
        string[,][] puzzleTiles = new string[puzzleSize,puzzleSize][];
        bool[] isTileUsed = new bool[tiles.Count];
        for (int t = 0; t < isTileUsed.Length; t++) {
            isTileUsed[t] = false;
        }
        // Choose any corner to be the top left corner of the entire puzzle.
        puzzleIndices[0,0] = cornerIndices[0];
        int sideUpStartingCorner;
        if ((tileIndexToEdgeSides[cornerIndices[0]][0] + 3) % 4 == tileIndexToEdgeSides[cornerIndices[0]][1]) {
            sideUpStartingCorner = tileIndexToEdgeSides[cornerIndices[0]][0]; // [0] is the top; [1] is the left edge.
        } else {
            sideUpStartingCorner = tileIndexToEdgeSides[cornerIndices[0]][1]; // [1] is the top; [0] is the left edge.
        }
        puzzleTiles[0,0] = RotateAndFlip(tiles[cornerIndices[0]], sideUpStartingCorner);
        isTileUsed[cornerIndices[0]] = true;
        // Row by row, fill in the rest of the puzzle.
        for (int i = 0; i < puzzleSize; i++) {
            for (int j = 0; j < puzzleSize; j++) {
                if (i == 0 && j == 0) {
                    continue;
                }

                // Find matches based on bottom edge of tile above.
                var tileIndexSideUpMatchesDown = new List<List<int>>();
                if (i > 0) {
                    string[] tileAbove = puzzleTiles[i - 1,j];
                    // Match on exactly the bottom border of tile above; tile needs to be flipped.
                    string borderDown = Border(tileAbove, 2);
                    if (borderToTileIndexToSides.ContainsKey(borderDown)) {
                        Dictionary<int, List<int>> tileIndexToSides = borderToTileIndexToSides[borderDown];
                        foreach (int t in tileIndexToSides.Keys) {
                            if (!isTileUsed[t]) {
                                foreach (int side in tileIndexToSides[t]) {
                                    tileIndexSideUpMatchesDown.Add(new List<int> {t, side - 4}); // Flip the tile.
                                }
                            }
                        }
                    }
                    // Match on the reverse of the bottom border; tile does not need to be flipped.
                    string borderDownReverse = Reverse(borderDown);
                    if (borderToTileIndexToSides.ContainsKey(borderDownReverse)) {
                        Dictionary<int, List<int>> tileIndexToSides = borderToTileIndexToSides[borderDownReverse];
                        foreach (int t in tileIndexToSides.Keys) {
                            if (!isTileUsed[t]) {
                                foreach (int side in tileIndexToSides[t]) {
                                    tileIndexSideUpMatchesDown.Add(new List<int> {t, side}); // Do not flip the tile.
                                }
                            }
                        }
                    }
                }
                // Find matches based on right edge of tile to the left.
                var tileIndexSideUpMatchesRight = new List<List<int>>();
                if (j > 0) {
                    string[] tileLeft = puzzleTiles[i,j - 1];
                    // Match on exactly the right border of left tile; tile needs to be flipped.
                    string borderRight = Border(tileLeft, 1);
                    if (borderToTileIndexToSides.ContainsKey(borderRight)) {
                        Dictionary<int, List<int>> tileIndexToSides = borderToTileIndexToSides[borderRight];
                        foreach (int t in tileIndexToSides.Keys) {
                            if (!isTileUsed[t]) {
                                foreach (int side in tileIndexToSides[t]) {
                                    tileIndexSideUpMatchesRight.Add(new List<int> {t, (side + 3) % 4 - 4}); // Left face is up after flipping.
                                }
                            }
                        }
                    }
                    // Match on the reverse of the right border; no flipping required.
                    string borderRightReverse = Reverse(borderRight);
                    if (borderToTileIndexToSides.ContainsKey(borderRightReverse)) {
                        Dictionary<int, List<int>> tileIndexToSides = borderToTileIndexToSides[borderRightReverse];
                        foreach (int t in tileIndexToSides.Keys) {
                            if (!isTileUsed[t]) {
                                foreach (int side in tileIndexToSides[t]) {
                                    tileIndexSideUpMatchesRight.Add(new List<int> {t, (side + 1) % 4}); // Right face up; no flip.
                                }
                            }
                        }
                    }
                }

                // Find matches.
                List<List<int>> matches;
                if (i == 0) {
                    // Top edge; base only on right matches.
                    matches = tileIndexSideUpMatchesRight;
                } else if (j == 0) {
                    // Left edge; base only on downward matches.
                    matches = tileIndexSideUpMatchesDown;
                } else {
                    // Find intersection of down and right matches.
                    matches = new List<List<int>>();
                    foreach (List<int> matchDown in tileIndexSideUpMatchesDown) {
                        foreach (List<int> matchRight in tileIndexSideUpMatchesRight) {
                            if (matchDown[0] == matchRight[0] && matchDown[1] == matchRight[1]) {
                                matches.Add(matchDown);
                                break;
                            }
                        }
                    }
                }
                if (matches.Count != 1) {
                    throw new SystemException("Unable to find tile at i=" + i + " j=" + j);
                }
                int tMatch = matches[0][0];
                puzzleIndices[i,j] = tMatch;
                puzzleTiles[i,j] = RotateAndFlip(tiles[tMatch], matches[0][1]);
                isTileUsed[tMatch] = true;
            }
        }

        if (part == 1) {
            long product = 1L;
            var indices = new List<int> {
                puzzleIndices[0,0], puzzleIndices[0,puzzleSize - 1],
                puzzleIndices[puzzleSize - 1,0], puzzleIndices[puzzleSize - 1,puzzleSize - 1]
            };
            foreach (int t in indices) {
                product *= tileIds[t];
            }
            return product;
        }

        // Create image; remove borders from tiles.
        int imageSize = puzzleSize * (TILE_SIZE - 2);
        char[,] image = new char[imageSize,imageSize];
        for (int pi = 0; pi < puzzleSize; pi++) {
            for (int pj = 0; pj < puzzleSize; pj++) {
                string[] tile = puzzleTiles[pi,pj];
                for (int i = 1; i < TILE_SIZE - 1; i++) {
                    for (int j = 1; j < TILE_SIZE - 1; j++) {
                        image[pi * (TILE_SIZE - 2) + i - 1,pj * (TILE_SIZE - 2) + j - 1] = tile[i][j];
                    }
                }
            }
        }

        // Search for monsters.
        string[] monster = new string[] {
            "                  # ",
            "#    ##    ##    ###",
            " #  #  #  #  #  #   "
        };
        for (int sideUp = -4; sideUp < 4; sideUp++) {
            string[] monsterPrime = RotateAndFlip(monster, sideUp);
            int h = monsterPrime.Length, w = monsterPrime[0].Length;

            for (int iStart = 0; iStart < imageSize - h; iStart++) {
                for (int jStart = 0; jStart < imageSize - w; jStart++) {
                    bool found = true;
                    for (int i = 0; i < h && found; i++) {
                        for (int j = 0; j < w; j++) {
                            if (monsterPrime[i][j] == '#' && image[iStart + i,jStart + j] == '.') {
                                found = false;
                                break;
                            }
                        }
                    }
                    if (found) {
                        for (int i = 0; i < h; i++) {
                            for (int j = 0; j < w; j++) {
                                if (monsterPrime[i][j] == '#') {
                                    image[iStart + i,jStart + j] = 'O';
                                }
                            }
                        }
                    }
                }
            }
        }

        long answer = 0L;
        for (int i = 0; i < imageSize; i++) {
            for (int j = 0; j < imageSize; j++) {
                if (image[i,j] == '#') {
                    answer++;
                }
            }
        }
        return answer;
    }

    private static string Border(string[] tile, int side) {
        int n = tile.Length, m = tile[0].Length;
        char[] cs;
        if (side == 0) {
            return tile[0];
        } else if (side == 1) {
            cs = new char[n];
            for (int i = 0; i < n; i++) {
                cs[i] = tile[i][m - 1];
            }
        } else if (side == 2) {
            cs = new char[m];
            for (int j = m - 1; j >= 0; j--) {
                cs[m - 1 - j] = tile[n - 1][j];
            }
        } else if (side == 3) {
            cs = new char[n];
            for (int i = n - 1; i >= 0; i--) {
                cs[n - 1 - i] = tile[i][0];
            }
        } else {
            throw new SystemException("Invalid side: " + side);
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

    private static string[] RotateAndFlip(string[] tile, int sideUp) {
        int n = tile.Length, m = tile[0].Length;
        int tiStart, di;
        int tjStart, dj;
        if (sideUp == -4 || sideUp == 0) {
            tiStart = 0;     di = 1;
            tjStart = 0;     dj = 1;
        } else if (sideUp == -3 || sideUp == 1) {
            tiStart = 0;     di = 1;
            tjStart = m - 1; dj = -1;
        } else if (sideUp == -2 || sideUp == 2) {
            tiStart = n - 1; di = -1;
            tjStart = m - 1; dj = -1;
        } else if (sideUp == -1 || sideUp == 3) {
            tiStart = n - 1; di = -1;
            tjStart = 0;     dj = 1;
        } else {
            throw new SystemException("Invalid `sideUp`: " + sideUp);
        }

        char[][] grid;
        int ti, tj;
        if (sideUp == -4 || sideUp == 0 || sideUp == -2 || sideUp == 2) {
            grid = new char[n][];
            ti = tiStart;
            for (int i = 0; i < n; i++) {
                grid[i] = new char[m];
                tj = tjStart;
                for (int j = 0; j < m; j++) {
                    grid[i][j] = tile[ti][tj];
                    tj += dj;
                }
                ti += di;
            }
        } else {
            grid = new char[m][];
            tj = tjStart;
            for (int i = 0; i < m; i++) {
                grid[i] = new char[n];
                ti = tiStart;
                for (int j = 0; j < n; j++) {
                    grid[i][j] = tile[ti][tj];
                    ti += di;
                }
                tj += dj;
            }
        }

        string[] lines = new string[grid.Length];
        for (int i = 0; i < lines.Length; i++) {
            string s = String.Join("", grid[i]);
            if (sideUp >= 0) {
                lines[i] = s;
            } else {
                lines[i] = Reverse(s);
            }
        }
        return lines;
    }

    private static K SomeKey<K, V>(Dictionary<K, V> d) {
        foreach (K key in d.Keys) {
            return key;
        }
        throw new SystemException("Empty dictionary.");
    }
}

AocUtil.RunSolver(20, Args, new Day20_JurassicJigsaw());
