@file:Import(
        "../AocUtil.kts",
        "../Intcode.kts",
)

solve(5, fun(lines: List<String>, part: Int): Long {
    val program = lines[0].split(",").map { it.toLong() }

    val input = if (part == 1) 1L else 5L

    val intcode = Intcode(program)
    intcode.pushInput(input)
    var output: Long? = null
    while (true) {
        try {
            val outputNext = intcode.run()
            if (output != null && output != 0L) {
                throw RuntimeException("Unexpected output for instruction: $output")
            }
            output = outputNext
        } catch (_: IntcodeHalt) {
            break
        }
    }
    return output!!
})
