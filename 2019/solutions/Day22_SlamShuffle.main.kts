@file:Import("../AocUtil.kts")


solve(22, fun(lines: List<String>, part: Int): Long {
    if (part == 1) {
        val n = 10007 // Size.
        var deck = IntArray(n) { it } // Position 0 is top.
        for (line in lines) {
            val deckNext = IntArray(n) { deck[it] }
            if (line == "deal into new stack") {
                for (i in deck.indices) {
                    deckNext[n - 1 - i] = deck[i]
                    // deckNext[i] = deck[n - 1 - i]
                }
            } else if (line.startsWith("cut ")) {
                val nSigned = line.substring(4).toInt()
                val t = if (nSigned > 0) nSigned else n + nSigned
                for (i in deck.indices) {
                    deckNext[(n + i - t) % n] = deck[i]
                    // deckNext[i] = deck[(i + t) % n]
                }
            } else if (line.startsWith("deal with increment ")) {
                val m = line.substring(20).toInt()
                for (i in deck.indices) {
                    deckNext[(m * i) % n] = deck[i]
                    // deckNext[i] =
                }
            }
            deck = deckNext
        }

        for (i in deck.indices) {
            if (deck[i] == 2019) {
                return i.toLong()
            }
        }
        throw RuntimeException("Unable to find element `2019`.")
    }

    val n = 119315717514047L // Size.
    val shuffles = 101741582076661L
    // Transformations:
    // Stack:       i' -> n - 1 - i
    //              i  -> n - 1 - i'
    // Cut t:       i' -> (i + t) % n
    //              i  -> (n + i' - t) % n
    // Increment m: i' ->
    //              i  -> (m * i') % n
    var iPrime = 2020L
    return -1
})
