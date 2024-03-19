@file:Import(
        "../AocUtil.kts",
        "../Cs.kts",
)

import kotlin.math.abs

val deltas = arrayOf(intArrayOf(-1, 0), intArrayOf(0, 1), intArrayOf(1, 0), intArrayOf(0, -1))
val directionToIndex = mapOf('U' to 0, 'R' to 1, 'D' to 2, 'L' to 3)

solve(3, fun(lines: List<String>, part: Int): Long {
    val paths0 = lines[0].split(",")
    val paths1 = lines[1].split(",")
    val pointToStep0 = getPointToStep(paths0)
    val pointToStep1 = getPointToStep(paths1)
    val pointOrigin = Point(0, 0)

    var answer: Int? = null
    for (point0 in pointToStep0.keys) {
        if (point0 == pointOrigin) {
            continue
        }
        if (pointToStep1.containsKey(point0)) {
            if (part == 1) {
                val distance = abs(point0.y) + abs(point0.x)
                if (answer == null || distance < answer) {
                    answer = distance
                }
            } else {
                val sum = pointToStep0[point0]!! + pointToStep1[point0]!!
                if (answer == null || sum < answer) {
                    answer = sum
                }
            }
        }
    }
    return answer!!.toLong()
})

fun getPointToStep(paths: List<String>): Map<Point, Int> {
    val pointToStep = mutableMapOf<Point, Int>()
    var pointCurrent = Point(0, 0)
    pointToStep[pointCurrent] = 0
    var step = 0
    for (path in paths) {
        val direction = path[0]
        val delta = deltas[directionToIndex[direction]!!]
        val length = path.substring(1).toInt()
        for (i in 1..length) {
            pointCurrent = Point(pointCurrent.y + delta[0], pointCurrent.x + delta[1])
            step++
            if (!pointToStep.containsKey(pointCurrent)) {
                pointToStep[pointCurrent] = step
            }
        }
    }
    return pointToStep
}
