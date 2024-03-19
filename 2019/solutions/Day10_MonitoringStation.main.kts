@file:Import("../AocUtil.kts", "../Cs.kts")

solve(10, fun(lines: List<String>, part: Int): Long {
    val points = mutableListOf<Point>()
    for (y in lines.indices) {
        val line = lines[y]
        for (x in line.indices) {
            val c = line[x]
            if (c == '#') {
                points.add(Point(y, x))
            }
        }
    }

    val pointToVectorToFactors = mutableMapOf<Point, MutableMap<Point, MutableList<Int>>>()
    points.forEach{ pointToVectorToFactors[it] = mutableMapOf<Point, MutableList<Int>>() }
    for (point in points) {
        val vectorToFactors = pointToVectorToFactors[point]!!
        for (other in points) {
            if (point == other) continue
            val yDelta = other.y - point.y
            val xDelta = other.x - point.x
            val d = gcd(yDelta, xDelta)
            val vector = Point(yDelta / d, xDelta / d)
            val factor = if (vector.y != 0) yDelta / vector.y else xDelta / vector.x
            if (!vectorToFactors.containsKey(vector)) {
                vectorToFactors[vector] = mutableListOf<Int>()
            }
            val factors = vectorToFactors[vector]!!
            var index = 0
            while (index < factors.count() && factor > factors[index]) {
                index++
            }
            factors.add(index, factor)
        }
    }
    val entryStation = pointToVectorToFactors.maxBy{ it.value.count() }
    if (part == 1) {
        return entryStation.value.count().toLong()
    }

    // Sort vectors.
    val pointStation = entryStation.key
    val vectorToFactors = entryStation.value
    val vectors = vectorToFactors.keys.toList().sortedWith(fun(p1: Point, p2: Point): Int {
        if (p1.x == 0) {
            if (p2.x == 0) {
                return p1.y - p2.y
            }
            if (p1.y < 0) {
                return -1
            }
            return p2.x
        }
        if (p2.x == 0) {
            if (p2.y < 0) {
                return 1
            }
            return -p1.x
        }
        if (p1.x > 0 && p2.x < 0) {
            return -1
        }
        if (p1.x < 0 && p2.x > 0) {
            return 1
        }
        val tan1 = p1.y.toFloat() / p1.x
        val tan2 = p2.y.toFloat() / p2.x
        return if (tan1 < tan2) -1 else if (tan1 > tan2) 1 else 0
    })
    // Vaporize.
    var vaporized = 0
    var pointVaporized: Point? = null
    var i = 0
    while (vaporized < 200) {
        val vector = vectors[i]
        if (vectorToFactors[vector]!!.count() > 0) {
            val factor = vectorToFactors[vector]!![0]
            vectorToFactors[vector]!!.removeAt(0)
            pointVaporized = Point(factor * vector.y + pointStation.y, factor * vector.x + pointStation.x)
            vaporized++
        }
        i = (i + 1) % vectors.count()
    }
    return 100L * pointVaporized!!.x + pointVaporized!!.y
})
