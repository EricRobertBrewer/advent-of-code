@file:Import("../AocUtil.kts")

import kotlin.math.abs


private val pattern = intArrayOf(0, 1, 0, -1)

solve(16, fun(lines: List<String>, part: Int): Long {
    var digits = lines[0].map { it.digitToInt() }

    if (part != 1) {
        digits = List(digits.count() * 10000) { digits[it % digits.count()] }
    }
    val offset = if (part != 1) getValue(digits, 7) else 0
    val phases = 100
    var phase = 0
    while (phase < phases) {
        digits = fftValues(digits).map { abs(it) % 10 }
        phase++
    }

    return getValue(digits, 8, offset).toLong()
})

fun getValue(digits: List<Int>, length: Int, offset: Int = 0): Int {
    var value = 0
    for (digit in digits.slice(offset..offset + length - 1)) {
        value = 10 * value + digit
    }
    return value
}

fun fftValues(digits: List<Int>): List<Int> {
    return List(digits.count()) { i ->
        digits.indices.sumOf { j -> digits[j] * p(i, j) }
    }
    // TODO: Fast!
}

fun p(i: Int, j: Int): Int {
    //        Left-shift 1    Wrap after multiples of 4    Reduce to range [0, 4)
    //               _/\__   ____________/\_____________    __/\___
    //              /     \ /                           \  /       \
    return pattern[((j + 1) % (pattern.count() * (i + 1))) / (i + 1)]
}
