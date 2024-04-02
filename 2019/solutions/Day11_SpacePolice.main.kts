@file:Import(
        "../AocUtil.kts",
        "../Cs.kts",
        "../Intcode.kts",
)


val deltas = arrayOf(intArrayOf(-1, 0), intArrayOf(0, 1), intArrayOf(1, 0), intArrayOf(0, -1))


solve(11, fun(lines: List<String>, part: Int): Long {
    val program = lines[0].split(",").map { it.toLong() }

    val intcode = Intcode(program)
    val panelToColor = mutableMapOf<Point, Int>()
    var panel = Point(0, 0)
    var direction = 0

    if (part != 1) {
        panelToColor[panel] = 1
    }

    try {
        while (true) {
            intcode.pushInput(panelToColor[panel]?.toLong() ?: 0L)
            val color = intcode.run()
            val turn = intcode.run()
            panelToColor[panel] = color.toInt()
            direction = when (turn) {
                0L -> (direction + 3) % 4
                1L -> (direction + 1) % 4
                else -> throw RuntimeException("Unexpected turn: $turn")
            }
            val delta = deltas[direction]
            panel = Point(panel.y + delta[0], panel.x + delta[1])
        }
    } catch (_: IntcodeHalt) {
    }

    if (part == 1) {
        return panelToColor.count().toLong()
    }

    val yMin = panelToColor.keys.minOf{ it.y }
    val yMax = panelToColor.keys.maxOf{ it.y }
    val xMin = panelToColor.keys.minOf{ it.x }
    val xMax = panelToColor.keys.maxOf{ it.x }
    for (y in yMin..yMax) {
        for (x in xMin..xMax) {
            val color = panelToColor[Point(y, x)] ?: 0
            print(if (color == 0) '.' else '#')
        }
        println()
    }
    return 0
})
