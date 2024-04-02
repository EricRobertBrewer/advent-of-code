@file:Import("../AocUtil.kts")


solve(4, fun(lines: List<String>, part: Int): Long {
    val rangeStartEndS = lines[0].split("-")
    val rangeStart = rangeStartEndS[0].toInt()
    val rangeEnd = rangeStartEndS[1].toInt()

    val digits = getDigits(rangeStart)
    val digitsEnd = getDigits(rangeEnd)
    var answer = 0L
    val pairPredicate = if (part == 1) ::pairPredicate1 else ::pairPredicate2
    while (digits.indices.any { digits[it] != digitsEnd[it] }) {
        if (pairPredicate.invoke(digits) &&
                digits.indices.all { it == 0 || digits[it] >= digits[it - 1] }) {
            answer++
        }
        incrementDigits(digits)
    }
    return answer
})

fun getDigits(x: Int): IntArray {
    var r = x
    var digits = intArrayOf()
    while (r > 0) {
        val digitsNext = IntArray(digits.count() + 1)
        digits.copyInto(digitsNext, 1)
        digitsNext[0] = r % 10
        r /= 10
        digits = digitsNext
    }
    return digits
}

fun incrementDigits(digits: IntArray) {
    var carry = digits.count() - 1
    while (carry != -1) {
        if (digits[carry] == 9) {
            digits[carry] = 0
            carry--
        } else {
            digits[carry]++
            carry = -1
        }
    }
}

fun pairPredicate1(digits: IntArray): Boolean {
    return digits.indices.any { it > 0 && digits[it] == digits[it - 1] }
}

fun pairPredicate2(digits: IntArray): Boolean {
    return digits.indices.any {
        if (it == 0) {
            false
        } else if (digits[it] != digits[it - 1]) {
            false // Not a pair.
        } else if (it > 1 && digits[it - 1] == digits[it - 2]) {
            false // First of pair is repeated.
        } else if (it < digits.count() - 1 && digits[it] == digits[it + 1]) {
            false // Second of pair is repeated.
        } else true
    }
}
