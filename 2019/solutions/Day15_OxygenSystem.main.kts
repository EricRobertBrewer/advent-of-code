@file:Import(
    "../AocUtil.kts",
    "../Cs.kts",
    "../Intcode.kts",
)


private val deltas = arrayOf(arrayOf(-1, 0), arrayOf(1, 0), arrayOf(0, -1), arrayOf(0, 1)) // N S W E

solve(15, fun(lines: List<String>, part: Int): Long {
    val program = lines[0].split(",").map { it.toLong() }

    val queue = IntcodeQueue()
    val intcode = Intcode(program, queue)
    val locationStart = Point(0, 0)
    val pointToC = mutableMapOf(locationStart to '.')
    val frontier = mutableListOf<Point>() // Stack.
    deltas.forEach { frontier.add(Point(locationStart.y + it[0], locationStart.x + it[1])) }

    var location = locationStart
    var oxygenLocation: Point? = null
    while (frontier.isNotEmpty()) {
        val target = frontier.removeLast()
        if (pointToC.containsKey(target)) continue

        // Move through empty spaces next to target.
        val path = dijkstraGridPath(pointToC, location, target)
        for (i in 0..path.count() - 2) {
            val point = path[i]
            val direction = getDeltaIndex(location, point) + 1 // +1: Map to program input format.
            queue.pushInput(direction.toLong())
            val status = intcode.run()
            if (status != 1L) throw RuntimeException("Unexpected status when moving to edge of frontier: $status")
            location = point
        }

        // Possibly move into target.
        val direction = getDeltaIndex(location, target) + 1
        queue.pushInput(direction.toLong())
        val status = intcode.run()
        when (status) {
            0L -> pointToC[target] = '#'
            1L -> {
                pointToC[target] = '.'
                for (delta in deltas) {
                    val other = Point(target.y + delta[0], target.x + delta[1])
                    if (!pointToC.containsKey(other)) {
                        frontier.add(other)
                    }
                }
                location = target
            }
            2L -> {
                if (part == 1) {
                    val endPath = dijkstraGridPath(pointToC, locationStart, target)
                    return endPath.count().toLong()
                }
                if (oxygenLocation != null) throw RuntimeException("Multiple oxygen locations: $oxygenLocation $target")
                oxygenLocation = target
                location = target
            }
        }
    }

    if (oxygenLocation == null) throw RuntimeException("No oxygen location found.")
    val pointToD = dijkstraGridDistance(pointToC, oxygenLocation!!)
    return pointToD.values.max().toLong()
})

fun getDeltaIndex(point: Point, adjacent: Point): Int {
    for (j in deltas.indices) {
        val delta = deltas[j]
        if (adjacent.y == point.y + delta[0] && adjacent.x == point.x + delta[1]) {
            return j
        }
    }
    throw RuntimeException("Points not adjacent: $point $adjacent")
}