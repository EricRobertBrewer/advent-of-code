@file:Import("../AocUtil.kts")


solve(2, fun(lines: List<String>, part: Int): Long {
    val program = lines[0].split(",").map { it.toInt() }
    if (part == 1) {
        return intcode(program, 12, 2)
    }

    for (noun in 0..99) {
        for (verb in 0..99) {
            try {
                val output = intcode(program, noun, verb)
                if (output == 19690720L) {
                    return 100L * noun + verb
                }
            } catch (_: IllegalArgumentException) {
            }
        }
    }

    throw RuntimeException("No solution found.")
})

fun intcode(program: List<Int>, noun: Int, verb: Int): Long {
    val memory = program.toMutableList()
    memory[1] = noun
    memory[2] = verb

    var i = 0
    while (i < memory.count() && memory[i] != 99) {
        val a = memory[i + 1]
        val b = memory[i + 2]
        val c = memory[i + 3]
        if (memory[i] == 1) {
            memory[c] = memory[a] + memory[b]
        } else if (program[i] == 2) {
            memory[c] = memory[a] * memory[b]
        } else {
            throw IllegalArgumentException("Unexpected opcode at position ${i}: ${memory[i]}")
        }
        i += 4
    }
    return memory[0].toLong()
}
