import kotlin.math.abs
import java.util.LinkedList

fun gcd(a: Int, b: Int): Int {
    if (a == 0) return abs(b)
    if (b == 0) return abs(a)
    var x = abs(a)
    var y = abs(b)
    while (x != y) {
        if (x > y) {
            x -= y
        } else {
            y -= x
        }
    }
    return x
}

fun gcd(a: Long, b: Long): Long {
    if (a == 0L) return abs(b)
    if (b == 0L) return abs(a)
    var x = abs(a)
    var y = abs(b)
    while (x != y) {
        if (x > y) {
            x -= y
        } else {
            y -= x
        }
    }
    return x
}

fun lcm(vararg arr: Long): Long {
    if (arr.isEmpty()) return 0
    var y = arr[0]
    for (x in arr.slice(1..arr.count() - 1)) {
        y *= x / gcd(y, x)
    }
    return y
}

fun <T> getPermutations(a: List<T>): List<List<T>> {
    if (a.count() == 1) {
        return listOf(listOf(a[0]))
    }

    val permutations = mutableListOf<List<T>>()
    val s = mutableSetOf<T>()
    for (i in 0..a.count() - 1) {
        if (s.contains(a[i])) {
            continue
        }
        s.add(a[i])

        val tail = mutableListOf<T>()
        tail.addAll(a.subList(0, i))
        tail.addAll(a.subList(i + 1, a.count()))
        val tailPermutations = getPermutations(tail)
        for (tailPermutation in tailPermutations) {
            val permutation = mutableListOf(a[i])
            permutation.addAll(tailPermutation)
            permutations.add(permutation)
        }
    }
    return permutations
}

data class Point(val y: Int, val x: Int) {

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is Point) {
            return false
        }
        return other.y == y && other.x == x
    }

    override fun hashCode(): Int {
        var result = y
        result = 31 * result + x
        return result
    }

    override fun toString(): String {
        return "(${y}, ${x})"
    }
}

fun dijkstraGridPath(pointToC: Map<Point, Char>, start: Point, end: Point, spaces: Set<Char>): List<Point> {
    if (start.equals(end)) return listOf()

    val deltas = arrayOf(arrayOf(-1, 0), arrayOf(0, 1), arrayOf(1, 0), arrayOf(0, -1)) // N E S W
    val q = LinkedList<Point>() // "Grid" assumes that adjacent distances are 1; thus, queue == min heap.
    q.add(start)
    val backtrace = mutableMapOf(start to start) // All keys have been queued but not necessarily explored.
    while (q.isNotEmpty()) {
        val p = q.removeFirst()

        for (delta in deltas) {
            val o = Point(p.y + delta[0], p.x + delta[1])
            if (o.equals(end)) {
                backtrace[o] = p
                val path = mutableListOf<Point>()
                var tail = end
                while (!tail.equals(start)) {
                    path.add(0, tail)
                    tail = backtrace[tail]!!
                }
                return path
            }
            if (pointToC.containsKey(o) && spaces.contains(pointToC[o]!!) && !backtrace.containsKey(o)) {
                q.add(o)
                backtrace[o] = p
            }
        }
    }
    throw RuntimeException("Unable to find path from ${start} to ${end}.")
}

fun dijkstraGridDistance(pointToC: Map<Point, Char>, start: Point, spaces: Set<Char>): Map<Point, Int> {
    val deltas = arrayOf(arrayOf(-1, 0), arrayOf(0, 1), arrayOf(1, 0), arrayOf(0, -1)) // N E S W
    val q = LinkedList<Point>() // "Grid" assumes that adjacent distances are 1; thus, queue == min heap.
    q.add(start)
    val pointToD = mutableMapOf(start to 0)
    while (q.isNotEmpty()) {
        val p = q.removeFirst()
        for (delta in deltas) {
            val o = Point(p.y + delta[0], p.x + delta[1])
            if (pointToC.containsKey(o) && spaces.contains(pointToC[o]!!) && !pointToD.containsKey(o)) {
                q.add(o)
                pointToD[o] = pointToD[p]!! + 1
            }
        }
    }
    return pointToD
}
