@file:Import(
        "../AocUtil.kts",
        "../Cs.kts",
        "../Intcode.kts",
)

solve(7, fun(lines: List<String>, part: Int): Long {
    val program = lines[0].split(",").map { it.toLong() }

    val phaseSettings = getPermutations(if (part == 1) listOf(0, 1, 2, 3, 4) else listOf(5, 6, 7, 8, 9))
    var phaseSettingMax: List<Int>? = null
    var signalMax: Long? = null
    for (phaseSetting in phaseSettings) {
        val intcodes = List(5) { Intcode(program) }
        for (index in phaseSetting.indices) {
            intcodes[index].pushInput(phaseSetting[index].toLong())
        }
        var output = 0L
        while (true) {
            var index = 0
            try {
                while (index < intcodes.count()) {
                    val intcode = intcodes[index]
                    intcode.pushInput(output)
                    output = intcode.run()
                    index++
                }
                if (part == 1) {
                    break
                }
            } catch (e: IntcodeHalt) {
                if (part == 1 || index != 0) {
                    // Part 1 shouldn't halt;
                    // Part 2 should halt only while running amplifier A, i.e., after E has produced output.
                    throw e
                }
                break
            }
        }
        if (signalMax == null || output > signalMax) {
            phaseSettingMax = phaseSetting
            signalMax = output
        }
    }
    println(phaseSettingMax)
    return signalMax!!
})
