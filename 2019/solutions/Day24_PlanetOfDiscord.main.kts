@file:Import("../AocUtil.kts")


solve(24, fun(lines: List<String>, part: Int): Long {
    val n = 5
    assert(lines.count() == n)
    assert(lines.all { it.count() == n && it.all { c -> c == '#' || c == '.' } })

    val deltas = arrayOf(intArrayOf(-1, 0), intArrayOf(0, 1), intArrayOf(1, 0), intArrayOf(0, -1))
    var depthToGrid = mutableMapOf(0 to List(n) { i -> CharArray(n) { j -> lines[i][j] } })
    val layoutToMinute = mutableMapOf<String, Int>()
    var minute = 0
    while (part == 1 || minute < 200) {
        // Part 1: Check for repeated grid.
        if (part == 1) {
            val layout = toLayout(depthToGrid[0]!!)
            if (layoutToMinute.containsKey(layout)) {
                println(layout)
                return layout.mapIndexed { i, c -> if (c == '#') twoPower(i) else 0 }.sum()
            }
            layoutToMinute[layout] = minute
        }

        val depthToBugsAdjacent = mutableMapOf<Int, List<IntArray>>()
        depthToGrid.keys.forEach { depthToBugsAdjacent[it] = List(n) { IntArray(n) { 0 } } }
        for (depth in depthToGrid.keys) {
            val grid = depthToGrid[depth]!!
            for (i in 0..n - 1) {
                for (j in 0..n - 1) {
                    if (grid[i][j] != '#') continue
                    if (part == 1) {
                        for (delta in deltas) {
                            val iAdj = i + delta[0]
                            val jAdj = j + delta[1]
                            if (iAdj < 0 || iAdj >= n || jAdj < 0 || jAdj >= n) continue
                            depthToBugsAdjacent[depth]!![iAdj][jAdj]++
                        }
                    } else if (i == 2 && j == 2) {
                        continue // Part 2: skip center tile.
                    } else {
                        for (delta in deltas) {
                            val iAdj = i + delta[0]
                            val jAdj = j + delta[1]
                            if (iAdj == 2 && jAdj == 2) {
                                // Increase depth.
                                if (!depthToBugsAdjacent.containsKey(depth + 1)) {
                                    depthToBugsAdjacent[depth + 1] = List(n) { IntArray(n) { 0 } }
                                }
                                val bugsAdjacent = depthToBugsAdjacent[depth + 1]!!
                                if (i == 1 && j == 2) {
                                    IntRange(0, n - 1).forEach { bugsAdjacent[0][it]++ }
                                } else if (i == 2 && j == 3) {
                                    IntRange(0, n - 1).forEach { bugsAdjacent[it][n - 1]++ }
                                } else if (i == 3 && j == 2) {
                                    IntRange(0, n - 1).forEach { bugsAdjacent[n - 1][it]++ }
                                } else {
                                    IntRange(0, n - 1).forEach { bugsAdjacent[it][0]++ }
                                }
                            } else if (iAdj < 0 || iAdj >= n || jAdj < 0 || jAdj >= n) {
                                // Decrease depth.
                                if (!depthToBugsAdjacent.containsKey(depth - 1)) {
                                    depthToBugsAdjacent[depth - 1] = List(n) { IntArray(n) { 0 } }
                                }
                                val bugsAdjacent = depthToBugsAdjacent[depth - 1]!!
                                if (iAdj < 0) {
                                    bugsAdjacent[1][2]++
                                } else if (iAdj >= n) {
                                    bugsAdjacent[3][2]++
                                } else if (jAdj < 0) {
                                    bugsAdjacent[2][1]++
                                } else {
                                    bugsAdjacent[2][3]++
                                }
                            } else {
                                // Default; same as part 1.
                                depthToBugsAdjacent[depth]!![iAdj][jAdj]++
                            }
                        }
                    }
                }
            }
        }

        val depthToGridNext = mutableMapOf<Int, List<CharArray>>()
        depthToGrid.keys.forEach { depthToGridNext[it] = List(n) { i -> CharArray(n) { j -> depthToGrid[it]!![i][j] } } }
        for (depth in depthToBugsAdjacent.keys) {
            val bugsAdjacent = depthToBugsAdjacent[depth]!!
            if (!depthToGridNext.containsKey(depth)) {
                depthToGridNext[depth] = List(n) { CharArray(n) { '.' } }
            }
            val gridNext = depthToGridNext[depth]!!
            for (i in 0..n - 1) {
                for (j in 0..n - 1) {
                    if (gridNext[i][j] == '#') {
                        if (bugsAdjacent[i][j] != 1) {
                            gridNext[i][j] = '.'
                        }
                    } else {
                        if (bugsAdjacent[i][j] == 1 || bugsAdjacent[i][j] == 2) {
                            gridNext[i][j] = '#'
                        }
                    }
                }
            }
        }
        depthToGrid = depthToGridNext

        minute++
    }

    var bugs = 0L
    for (grid in depthToGrid.values) {
        for (i in 0..n - 1) {
            for (j in 0..n - 1) {
                if (grid[i][j] == '#') {
                    bugs++
                }
            }
        }
    }
    return bugs
})

fun toLayout(grid: List<CharArray>): String {
    return grid.joinToString("") { it.joinToString("") }
}

fun twoPower(i: Int): Long {
    var x = 1L
    for (j in 0..i - 1) {
        x *= 2
    }
    return x
}
