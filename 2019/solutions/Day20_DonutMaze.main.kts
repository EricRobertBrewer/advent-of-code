@file:Import("../AocUtil.kts")

import java.util.LinkedList
import kotlin.math.max
import kotlin.math.min


solve(20, fun(lines: List<String>, part: Int): Long {
    assert(lines.count() >= 7)
    assert(lines[0].count() >= 7)
    assert(lines.all { it.count() == lines[0].count() })

    // Scan for passable tiles '.' and the bounds of the inner space (hole) area.
    val pointToC = mutableMapOf<List<Int>, Char>()
    var yHoleMin = lines.lastIndex - 2
    var yHoleMax = 2
    var xHoleMin = lines[0].lastIndex - 2
    var xHoleMax = 2
    lines.forEachIndexed { y, line ->
        line.forEachIndexed { x, c ->
            if (c == '.') { // Ignore walls '#'.
                pointToC[listOf(y, x)] = c
            } else if (c == ' ' && 2 <= y && y <= lines.lastIndex - 2 && 2 <= x && x <= lines[0].lastIndex - 2) {
                yHoleMin = min(y, yHoleMin)
                yHoleMax = max(y, yHoleMax)
                xHoleMin = min(x, xHoleMin)
                xHoleMax = max(x, xHoleMax)
            }
        }
    }

    // Create forward and reverse mapping of portals on outside.
    val portalToPointOuter = mutableMapOf<String, List<Int>>()
    findPortalsHorizontal(lines, 0, 3..lines[0].lastIndex - 3, false, portalToPointOuter)
    findPortalsHorizontal(lines, lines.lastIndex - 1, 3..lines[0].lastIndex - 3, true, portalToPointOuter)
    findPortalsVertical(lines, 0, 3..lines.lastIndex - 3, false, portalToPointOuter)
    findPortalsVertical(lines, lines[0].lastIndex - 1, 3..lines.lastIndex - 3, true, portalToPointOuter)
    val pointToPortalOuter = mutableMapOf<List<Int>, String>()
    for (portal in portalToPointOuter.keys) {
        val point = portalToPointOuter[portal]!!
        pointToPortalOuter[point] = portal
    }
    // Create forward and reverse mapping of portals on inside.
    val portalToPointInner = mutableMapOf<String, List<Int>>()
    findPortalsHorizontal(lines, yHoleMin, xHoleMin + 2..xHoleMax - 2, true, portalToPointInner)
    findPortalsHorizontal(lines, yHoleMax - 1, xHoleMin  + 2..xHoleMax - 2, false, portalToPointInner)
    findPortalsVertical(lines, xHoleMin, yHoleMin + 2..yHoleMax - 2, true, portalToPointInner)
    findPortalsVertical(lines, xHoleMax - 1, yHoleMin + 2..yHoleMax - 2, false, portalToPointInner)
    val pointToPortalInner = mutableMapOf<List<Int>, String>()
    for (portal in portalToPointInner.keys) {
        val point = portalToPointInner[portal]!!
        pointToPortalInner[point] = portal
    }

    // Solve using BFS with portals.
    val pointAA = portalToPointOuter["AA"]!!
    val pointZZ = portalToPointOuter["ZZ"]!!
    val deltas = arrayOf(intArrayOf(-1, 0), intArrayOf(0, 1), intArrayOf(1, 0), intArrayOf(0, -1))
    if (part == 1) {
        val path = dijkstraGridPathPortal(pointAA, pointZZ, fun(point: List<Int>): Set<List<Int>> {
            val neighbors = mutableSetOf<List<Int>>()
            // Add adjacent tiles.
            for (delta in deltas) {
                val adjacent = listOf(point[0] + delta[0], point[1] + delta[1])
                if (pointToC.containsKey(adjacent) && pointToC[adjacent]!! == '.') {
                    neighbors.add(adjacent)
                }
            }
            // Add tiles joined by a portal on inside and outside.
            if (pointToPortalOuter.containsKey(point)) {
                val portal = pointToPortalOuter[point]!!
                if (portalToPointInner.containsKey(portal)) {
                    neighbors.add(portalToPointInner[portal]!!)
                }
            }
            if (pointToPortalInner.containsKey(point)) {
                val portal = pointToPortalInner[point]!!
                neighbors.add(portalToPointOuter[portal]!!)
            }
            return neighbors
        })
        return path.count().toLong()
    }

    val point3AA = listOf(pointAA[0], pointAA[1], 0)
    val point3ZZ = listOf(pointZZ[0], pointZZ[1], 0)
    val path = dijkstraGridPathPortal(point3AA, point3ZZ, fun(point3: List<Int>): Set<List<Int>> {
        val point = listOf(point3[0], point3[1])
        val level = point3[2]
        val neighbors = mutableSetOf<List<Int>>()
        // Add adjacent tiles.
        for (delta in deltas) {
            val adjacent = listOf(point[0] + delta[0], point[1] + delta[1])
            if (pointToC.containsKey(adjacent) && pointToC[adjacent]!! == '.') {
                neighbors.add(listOf(adjacent[0], adjacent[1], level))
            }
        }
        // Add tiles to portals at lower levels.
        if (level > 0 && pointToPortalOuter.containsKey(point)) {
            val portal = pointToPortalOuter[point]
            if (portalToPointInner.containsKey(portal)) {
                val pointInner = portalToPointInner[portal]!!
                neighbors.add(listOf(pointInner[0], pointInner[1], level - 1))
            }
        }
        // Add tiles to portals at higher levels.
        if (pointToPortalInner.containsKey(point)) {
            val portal = pointToPortalInner[point]!!
            val pointOuter = portalToPointOuter[portal]!!
            neighbors.add(listOf(pointOuter[0], pointOuter[1], level + 1))
        }
        return neighbors
    })
    return path.count().toLong()
})

fun findPortalsHorizontal(
    lines: List<String>,
    yLabelTop: Int,
    xRange: IntRange,
    isPointAbove: Boolean,
    portalToPoint: MutableMap<String, List<Int>>,
) {
    val yPoint = if (isPointAbove) yLabelTop - 1 else yLabelTop + 2
    for (x in xRange) {
        if (lines[yLabelTop][x] == ' ') continue
        if (lines[yLabelTop + 1][x] == ' ' || lines[yPoint][x] != '.') {
            throw RuntimeException("Expected label and passable tile at (${yLabelTop}, ${x}).")
        }
        val portal = "${lines[yLabelTop][x]}${lines[yLabelTop + 1][x]}"
        portalToPoint[portal] = listOf(yPoint, x)
    }
}

fun findPortalsVertical(
    lines: List<String>,
    xLabelLeft: Int,
    yRange: IntRange,
    isPointLeft: Boolean,
    portalToPoint: MutableMap<String, List<Int>>,
) {
    val xPoint = if (isPointLeft) xLabelLeft - 1 else xLabelLeft + 2
    for (y in yRange) {
        if (lines[y][xLabelLeft] == ' ') continue
        if (lines[y][xLabelLeft + 1] == ' ' || lines[y][xPoint] != '.') {
            throw RuntimeException("Expected label and passable tile at (${y}, ${xLabelLeft}).")
        }
        val portal = "${lines[y][xLabelLeft]}${lines[y][xLabelLeft + 1]}"
        portalToPoint[portal] = listOf(y, xPoint)
    }
}

fun <T> dijkstraGridPathPortal(
    start: T & Any,
    end: T & Any,
    getNeighbors: (T & Any) -> Set<T & Any>
): List<T & Any> {
    if (start.equals(end)) return listOf()

    val q = LinkedList<T & Any>()
    q.add(start)
    val backtrace = mutableMapOf(start to start)
    while (q.isNotEmpty()) {
        val item = q.removeFirst()
        val neighbors = getNeighbors(item)
        for (neighbor in neighbors) {
            if (backtrace.contains(neighbor)) continue
            backtrace[neighbor] = item
            if (neighbor.equals(end)) {
                val path = mutableListOf<T & Any>()
                var itemPath = neighbor
                while (!itemPath.equals(start)) {
                    path.add(0, itemPath)
                    itemPath = backtrace[itemPath]!!
                }
                return path
            }
            q.add(neighbor)
        }
    }
    throw RuntimeException("Unable to find end ${end}.")
}
