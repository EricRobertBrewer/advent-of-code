@file:Import(
    "../AocUtil.kts",
    "../Intcode.kts",
)


solve(19, fun(lines: List<String>, part: Int): Long {
    val program = lines[0].split(",").map { it.toLong() }

    if (part == 1) {
        val yMax = 49L
        val xMax = 49L
        var answer = 0L
        for (y in 0L..yMax) {
            for (x in 0L..xMax) {
                val pulled = pulledFlag(program, y, x)
                if (pulled == 1L) {
                    answer++
                }
                print(if (pulled == 1L) '#' else '.')
            }
            println()
        }
        return answer
    }

    val beamTip = findBeamTip(program)
    var y = beamTip[0]
    var x = beamTip[1]
    while (true) {
        if (y >= 100) {
            val pulledDiagonal = pulledFlag(program, y - 99L, x + 99L)
            if (pulledDiagonal == 1L) {
                break
            }
        }
        y++
        val pulledDown = pulledFlag(program, y, x)
        if (pulledDown == 0L) {
            x++
            val pulledDownRight = pulledFlag(program, y, x)
            if (pulledDownRight == 0L) {
                throw RuntimeException("Bottom edge of beam doesn't continue down or down-right. $x $y")
            }
        } else if (x > 0L) {
            val pulledDownLeft = pulledFlag(program, y, x - 1)
            if (pulledDownLeft == 1L) {
                throw RuntimeException("Unexpected beam expanding to the left. $x $y")
            }
        }
    }
    println("(${x}, ${y})")
    return 10000 * x + y - 99
})

fun pulledFlag(program: List<Long>, y: Long, x: Long): Long {
    val queue = IntcodeQueue()
    val intcode = Intcode(program, queue)
    queue.pushInput(x)
    queue.pushInput(y)
    return intcode.run()
}

fun findBeamTip(program: List<Long>): LongArray {
    var yStart = 1L
    while (true) {
        var y = yStart
        var x = 0L
        while (y >= 0L) {
            val pulled = pulledFlag(program, y, x)
            if (pulled == 1L) {
                return longArrayOf(y, x)
            }
            y--
            x++
        }
        yStart++
    }
}
