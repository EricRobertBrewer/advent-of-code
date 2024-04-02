@file:Import(
        "../AocUtil.kts",
        "../Cs.kts",
        "../Intcode.kts",
)


solve(13, fun(lines: List<String>, part: Int): Long {
    val program = lines[0].split(",").map { it.toLong() }
    val programQuarters = program.indices.map { if (part == 1 || it != 0) program[it] else 2 }

    val pointToTileId = mutableMapOf<Point, Int>()
    var score = -1L
    var yMin: Int? = null // Screen bounds
    var yMax: Int? = null
    var xMin: Int? = null
    var xMax: Int? = null
    var xPaddle = -1
    var xBall = -1
    val provider = if (part == 2) object: IntcodeInputProvider { // Human input
        override fun getInput(): Long {
            printScreen(pointToTileId, yMin!!..yMax!!, xMin!!..xMax!!, score)
            val inputToTilt = mapOf("f" to -1L, "" to 0L, "j" to 1L)
            var input = "F_OR_J_OR_NOTHING"
            while (!inputToTilt.containsKey(input)) {
                input = readLine()!!
            }
            return inputToTilt[input]!!
        }
    } else IntcodeInputProvider { // Automated input
        if (xPaddle > xBall) {
            -1
        } else if (xPaddle < xBall) {
            1
        } else 0
    }
    val intcode = Intcode(programQuarters, provider)

    while (true) {
        try {
            val x = intcode.run().toInt()
            val y = intcode.run().toInt()
            val z = intcode.run()

            if (x == -1 && y == 0) {
                score = z
            } else {
                val point = Point(y, x)
                val tileId = z.toInt()
                pointToTileId[point] = tileId.toInt()

                if (tileId == 3) {
                    xPaddle = x
                } else if (tileId == 4) {
                    xBall = x
                }
            }

            if (yMin == null || y < yMin) {
                yMin = y
            }
            if (yMax == null || y > yMax) {
                yMax = y
            }
            if (xMin == null || x < xMin) {
                xMin = x
            }
            if (xMax == null || x > xMax) {
                xMax = x
            }
        } catch (_: IntcodeHalt) {
            break
        }
    }

    if (part == 1) {
        var blockCount = 0L
        for (point in pointToTileId.keys) {
            val tileId = pointToTileId[point]!!
            if (tileId == 2) {
                blockCount++
            }
        }
        return blockCount
    }

    return score
})

fun printScreen(pointToTileId: Map<Point, Int>, yRange: IntRange, xRange: IntRange, score: Long) {
    val screenChars = " #o-@"

    println("  SCORE: ${score}")
    println()
    for (y in yRange) {
        for (x in xRange) {
            val point = Point(y, x)
            val tileId = pointToTileId[point] ?: 0
            print(screenChars[tileId])
        }
        println()
    }
    println()
    println("+++")
}
