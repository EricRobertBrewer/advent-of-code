@file:Import(
    "../AocUtil.kts",
    "../Intcode.kts",
)

import java.io.File


val deltas = arrayOf(intArrayOf(-1, 0), intArrayOf(0, 1), intArrayOf(1, 0), intArrayOf(0, -1))
val cToDirection = mapOf('^' to 0, '>' to 1, 'v' to 2, '<' to 3)

solve(17, fun(lines: List<String>, part: Int): Long {
    val program = lines[0].split(",").map { it.toLong() }
    val programMovement = program.mapIndexed { i, v -> if (i != 0 || part == 1) v else 2L }
    val queue = IntcodeQueue()
    val intcode = Intcode(programMovement, queue)

    val view = getScaffoldingView(intcode) // Part 2 depends on cached file from part 1.

    if (part == 1) {
        val intersections = mutableListOf<IntArray>()
        for (i in view.indices) {
            val row = view[i]
            for (j in row.indices) {
                val c = row[j]
                if (c != '#') continue
                if (deltas.map { intArrayOf(i + it[0], j + it[1]) } // (i', j')
                        .all { 0 <= it[0] && it[0] < view.count() && // 0 <= i' < n
                                0 <= it[1] && it[1] < view[it[0]].count() && // 0 <= j' < m
                                view[it[0]][it[1]] == '#' }) {
                    intersections.add(intArrayOf(i, j))
                }
            }
        }
        return intersections.sumOf { it[0] * it[1] }.toLong()
    }

    // Find robot.
    var robot: IntArray? = null // Location (i, j) & direction.
    for (i in view.indices) {
        val row = view[i]
        for (j in row.indices) {
            val c = row[j]
            if (!cToDirection.containsKey(c)) continue
            if (robot != null) {
                throw RuntimeException("Multiple robot locations found: (${robot[0]}, ${robot[1]}) (${i}, ${j})")
            }
            robot = intArrayOf(i, j, cToDirection[c]!!)
        }
    }
    if (robot == null) {
        throw RuntimeException("No robot location found in scaffolding view.")
    }

    // Run solution, if it exists.
    val solutionFile = File(getYearDir(year), "17-solution.txt")
    if (solutionFile.exists()) {
        val solutionLines = solutionFile.readLines()
        for (line in solutionLines) {
            for (c in line) {
                queue.pushInput(c.code.toLong())
            }
            queue.pushInput('\n'.code.toLong())
        }
        var output: Long? = null // Last output will be dust collected.
        while (true) {
            try {
                output = intcode.run()
            } catch (_: IntcodeHalt) {
                break
            }
        }
        return output!!
    }

    // Traverse scaffolding.
    val path = mutableListOf<String>()
    while (true) {
        // Turn.
        val tileLeft = getTile(view, robot[0], robot[1], (robot[2] + 3) % deltas.count())
        val tileRight = getTile(view, robot[0], robot[1], (robot[2] + 1) % deltas.count())
        if ((tileLeft == null || tileLeft != '#') && (tileRight == null || tileRight != '#')) break
        if (tileLeft != null && tileLeft == '#' && tileRight != null && tileRight == '#') {
            throw RuntimeException("Fork found in path at (${robot[0]}, ${robot[1]})")
        }
        val turn = if (tileLeft != null && tileLeft == '#') 3 else 1
        robot[2] = (robot[2] + turn) % deltas.count()

        // Move forward.
        val delta = deltas[robot[2]]
        var steps = 0
        while (true) {
            val i_ = robot[0] + delta[0]
            val j_ = robot[1] + delta[1]
            if (0 <= i_ && i_ < view.count() && 0 <= j_ && j_ < view[i_].count() && view[i_][j_] == '#') {
                robot[0] = i_
                robot[1] = j_
                steps++
            } else {
                break
            }
        }

        path.add(if (turn == 3) "L" else "R")
        path.add("$steps")
    }

    var idx = 0
    while (idx < path.count()) {
        println("${path[idx]} ${path[idx + 1]}")
        idx += 2
    }
    println("Provide solution at: ${solutionFile.path}")
    return -1
})

fun getScaffoldingView(intcode: Intcode): List<String> {
    val yearDir = getYearDir(year) // `year` and `getYearDir` come from AocUtil.
    val viewFile = File(yearDir, "17-scaffolding.txt")
    if (viewFile.exists()) {
        return viewFile.readLines()
    }

    val view = StringBuilder()
    while (true) {
        try {
            val code = intcode.run()
            when (val c = Char(code.toUShort())) {
                '#', '.', '^', 'v', '<', '>', '\n' -> view.append(c)
                else -> throw RuntimeException("Unexpected character in view: $c")
            }
        } catch (_: IntcodeHalt) {
            break
        }
    }

    val writer = viewFile.bufferedWriter()
    writer.write(view.toString())
    writer.close()
    return getScaffoldingView(intcode)
}

fun getTile(view: List<String>, i: Int, j: Int, direction: Int): Char? {
    val delta = deltas[direction]
    val i_ = i + delta[0]
    val j_ = j + delta[1]
    if (0 <= i_ && i_ < view.count() && 0 <= j_ && j_ < view[i_].count()) {
        return view[i_][j_]
    }
    return null
}
