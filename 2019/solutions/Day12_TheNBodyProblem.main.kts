@file:Import(
        "../AocUtil.kts",
        "../Cs.kts",
)

import kotlin.math.abs


solve(12, fun(lines: List<String>, part: Int): Long {
    val moons = 4
    val axes = 3
    assert(lines.count() == moons)
    val moonAxisPositions = lines.map { itLine ->
        itLine.substring(1, itLine.lastIndex) // Remove <>
                .split(", ").map {
            it.split("=")
        }
    }
    assert(moonAxisPositions.all { it.count() == axes && it[0][0] == "x" && it[1][0] == "y" && it[2][0] == "z" })
    val moonP = moonAxisPositions.map { IntArray(axes) { i -> it[i][1].toInt() } }
    val moonV = List(moons) { IntArray(axes) { 0 } }
    val moonPVToStep = Array(moons) { mutableMapOf(makePv(moonP[it], moonV[it]) to 0) }
    val moonPVCycle = Array<List<Int>?>(moons) { null }
    val moonCycleStepEnd = Array<Int?>(moons) { null }

    var step = 0
    while (part != 1 || step < 1000) {
        // Gravity.
        for (i in 0..moonP.count() - 2) {
            val pi = moonP[i]
            val vi = moonV[i]
            for (j in i + 1..moonP.count() - 1) {
                if (i == j) continue
                val pj = moonP[j]
                val vj = moonV[j]
                for (x in pi.indices) {
                    if (pi[x] < pj[x]) {
                        vi[x]++
                        vj[x]--
                    } else if (pi[x] > pj[x]) {
                        vi[x]--
                        vj[x]++
                    }
                }
            }
        }
        // Velocity.
        for (i in moonP.indices) {
            val pi = moonP[i]
            val vi = moonV[i]
            for (x in pi.indices) {
                pi[x] += vi[x]
            }
        }
        step++
        if (part != 1) {
            for (i in moonPVCycle.indices) {
                if (moonPVCycle[i] != null) continue
                val p = moonP[i]
                val v = moonV[i]
                val pv = makePv(p, v)
                if (moonPVToStep[i].containsKey(pv)) {
                    moonPVCycle[i] = pv
                    moonCycleStepEnd[i] = step
                } else {
                    moonPVToStep[i][pv] = step
                }
            }
            if (moonPVCycle.all { it != null }) {
//                val lengths = LongArray(moons) { moonCycleLengths[it]!!.toLong() }
//                return lcm(*lengths)
                moonPVCycle.indices.forEach { println("${moonPVToStep[it][moonPVCycle[it]]}, ${moonCycleStepEnd[it]}") }
                return 0
            }
        }
    }
    return moonP.indices.sumOf { i -> moonP[i].sumOf { abs(it) } * moonV[i].sumOf{ abs(it) } }.toLong()
})

fun makePv(p: IntArray, v: IntArray): List<Int> {
    val pv = p.toMutableList()
    pv.addAll(v.toList())
    return pv
}
