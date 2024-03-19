import kotlin.math.abs

class Cs {

    companion object {

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
    }
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
}
