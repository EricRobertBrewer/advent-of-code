@file:Import(
    "../AocUtil.kts",
    "../Intcode.kts",
)

import java.io.File


solve(21, fun(lines: List<String>, part: Int): Long {
    val program = lines[0].split(",").map { it.toLong() }
    val queue = IntcodeQueue()
    val intcode = Intcode(program, queue)

    // Show prompt.
    while (true) {
        val output = intcode.run()
        val c = Char(output.toInt())
        print(c)
        if (c == '\n') break
    }

    // Input instructions.
    // WALK: !(A & B & C) & D
    // RUN:
    val solutionFileName = if (part == 1) "21-walk.txt" else "21-run.txt"
    val solutionFile = File(getYearDir(year), solutionFileName)
    if (solutionFile.exists()) {
        val solutionLines = solutionFile.readLines()
        for (line in solutionLines) {
            for (c in line) {
                queue.pushInput(c.code.toLong())
            }
            queue.pushInput('\n'.code.toLong())
            println(line)
        }
    } else {
        throw RuntimeException("Provide input instructions at `${solutionFile.path}`.")
    }

    while (true) {
        try {
            val output = intcode.run()
            if (output > 255L) return output
            val c = Char(output.toInt())
            print(c)
        } catch (_: IntcodeHalt) {
            break
        }
    }
    return -1
})
