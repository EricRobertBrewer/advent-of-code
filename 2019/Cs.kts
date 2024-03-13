class Cs {

    companion object {

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
