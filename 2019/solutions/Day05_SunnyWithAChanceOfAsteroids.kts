class Day05_SunnyWithAChanceOfAsteroids {

    companion object {

        fun getAnswer(lines: List<String>, part: Int): Long {
            val program = lines[0].split(",").map { it.toInt() }
            val input = if (part == 1) 1 else 5
            return intcode(program, input).toLong()
        }

        private fun intcode(program: List<Int>, input: Int): Int {
            val memory = program.toMutableList()
            var i = 0
            var output = -1
            while (i < memory.count() && memory[i] != 99) {
                val opcode = memory[i] % 100 // DE
                val mode0 = (memory[i] / 100) % 10 // C
                val mode1 = (memory[i] / 1000) % 10 // B
                val mode2 = (memory[i] / 10000) % 10 // A
                val a = if (opcode != 3) getValue(memory, i + 1, mode0) else -1
                val b = if (opcode != 3 && opcode != 4) getValue(memory, i + 2, mode1) else -1
                when (opcode) {
                    1 -> { // Add
                        memory[memory[i + 3]] = getValue(memory, i + 1, mode0) + getValue(memory, i + 2, mode1)
                        i += 4
                    }
                    2 -> { // Multiply
                        memory[memory[i + 3]] = getValue(memory, i + 1, mode0) * getValue(memory, i + 2, mode1)
                        i += 4
                    }
                    3 -> { // Input
                        memory[memory[i + 1]] = input
                        i += 2
                    }
                    4 -> { // Output
                        output = getValue(memory, i + 1, mode0)
                        if ((i + 2 >= memory.count() || memory[i + 2] != 99) && // Not immediately followed by a halt.
                                output != 0) {
                            throw RuntimeException("Unexpected output for instruction at `${i}`: $output")
                        }
                        i += 2
                    }
                    5 -> { // Jump-if-true
                        i = if (a != 0) b else i + 3
                    }
                    6 -> { // Jump-if-false
                        i = if (a == 0) b else i + 3
                    }
                    7 -> { // Less-than
                        memory[memory[i + 3]] = if (a < b) 1 else 0
                        i += 4
                    }
                    8 -> { // Equal
                        memory[memory[i + 3]] = if (a == b) 1 else 0
                        i += 4
                    }
                    else -> throw IllegalArgumentException("Unexpected opcode for instruction at `${i}`: $opcode")
                }
            }
            return output
        }

        private fun getValue(memory: List<Int>, i: Int, parameterMode: Int): Int {
            return if (parameterMode == 0) memory[memory[i]] else memory[i]
        }
    }
}
