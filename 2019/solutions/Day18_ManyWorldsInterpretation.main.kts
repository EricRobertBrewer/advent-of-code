@file:Import(
    "../AocUtil.kts",
    "../Cs.kts",
)


solve(18, fun(lines: List<String>, part: Int): Long {
//    lines.forEach { println(it) }
    val pointToC = mutableMapOf<Point, Char>()
    val cToPoint = mutableMapOf<Char, Point>()
    lines.forEachIndexed { i, line ->
        line.forEachIndexed { j, c ->
            val point = Point(i, j)
            pointToC[point] = c
            if (c != '#' && c != '.') {
                if (cToPoint.containsKey(c)) {
                    throw RuntimeException("Character `${c}` is found twice in grid: ${cToPoint[c]} ${point}")
                }
                cToPoint[c] = point
            }
        }
    }
//    println(pointToC.count())
//    println(cToPoint.count())
//    cToPoint.keys.forEach { print(it); print(' ') }; println()

    val startPoint = cToPoint['@']!!
    val startPoints = if (part == 1) {
        listOf(startPoint)
    } else {
        listOf(
            Point(startPoint.y - 1, startPoint.x - 1),
            Point(startPoint.y - 1, startPoint.x + 1),
            Point(startPoint.y + 1, startPoint.x - 1),
            Point(startPoint.y + 1, startPoint.x + 1),
        )
    }
    if (part != 1) {
        for (point in startPoints) {
            pointToC[point] = '@'
        }
        pointToC[Point(startPoint.y - 1, startPoint.x)] = '#'
        pointToC[Point(startPoint.y, startPoint.x - 1)] = '#'
        pointToC[Point(startPoint.y, startPoint.x)] = '#'
        pointToC[Point(startPoint.y, startPoint.x + 1)] = '#'
        pointToC[Point(startPoint.y + 1, startPoint.x)] = '#'
    }

    val keysAll = cToPoint.keys.filter { it.isLowerCase() }.toSet()
//    keysAll.forEach { print(it); print(' ') }; println()

    var states = mutableListOf(State(startPoints, listOf(), 0))
    while (states.isNotEmpty() && states[0].keys.count() < keysAll.count()) {
        println("states: ${states.count()}; d[0]: ${states[0].distance}; d[${states.lastIndex}]: ${states.last().distance}")
        val statesNext = mutableListOf<State>()
        for (state in states) {
            // Determine set of passable and terminal tiles.
            val spaces = mutableSetOf('.', '@')
            val terminals = keysAll.map { cToPoint[it]!! }.toMutableSet()
            for (c in state.keys) {
                spaces.add(c) // Move past any collected keys, e.g., 'a'
                spaces.add(c.uppercaseChar()) // ... and their respective doors, e.g., 'A'.
                terminals.remove(cToPoint[c]!!) // Stop exploring from a tile after finding any key.
            }
            // Collect distances to all reachable keys.
            val pointKeyToDistance = List(state.points.count()) { mutableMapOf<Char, Int>() }
            for (i in state.points.indices) {
                val point = state.points[i]
                val pointToD = dijkstraGridDistance(pointToC, point, spaces, terminals)
                for (terminal in terminals) {
                    if (pointToD.containsKey(terminal)) {
                        pointKeyToDistance[i][pointToC[terminal]!!] = pointToD[terminal]!!
                    }
                }
            }
            // Branch out from the current state.
            for (i in pointKeyToDistance.indices) {
                val keyToDistance = pointKeyToDistance[i]
                for (key in keyToDistance.keys) {
                    val pointsNext = state.points.mapIndexed { j, point -> if (i == j) cToPoint[key]!! else point }
                    val keysNext = state.keys.toMutableList()
                    keysNext.add(key)
                    val distanceNext = state.distance + keyToDistance[key]!!
                    statesNext.add(State(pointsNext, keysNext, distanceNext))
                }
            }
        }

        // Group by location and collected key set.
        val pointsToKeySetToStates = mutableMapOf<List<Point>, MutableMap<Set<Char>, MutableList<State>>>()
        for (state in statesNext) {
            if (!pointsToKeySetToStates.containsKey(state.points)) {
                pointsToKeySetToStates[state.points] = mutableMapOf()
            }
            val keySetToStates = pointsToKeySetToStates[state.points]!!
            val keySet = state.keys.toSet()
            if (!keySetToStates.containsKey(keySet)) {
                keySetToStates[keySet] = mutableListOf()
            }
            keySetToStates[keySet]!!.add(state)
        }
        // Eliminate strictly worse paths, e.g., ([(3, 4)], {a, b, c}, 15) < ([(3, 4)], {b, a, c}, 20).
        states = mutableListOf()
        for (points in pointsToKeySetToStates.keys) {
            for (keySet in pointsToKeySetToStates[points]!!.keys) {
                states.add(pointsToKeySetToStates[points]!![keySet]!!.minBy { it.distance })
            }
        }
    }
    return states.minOf { it.distance }.toLong()
})

data class State(val points: List<Point>, val keys: List<Char>, val distance: Int)
