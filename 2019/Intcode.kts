fun interface IntcodeInputProvider {

    fun getInput(): Long
}

class IntcodeQueue: IntcodeInputProvider {

    private val inputs = mutableListOf<Long>()

    fun pushInput(input: Long) {
        inputs.add(input)
    }

    override fun getInput(): Long {
        val input = inputs[0]
        inputs.removeAt(0)
        return input
    }
}

class Intcode(program: List<Long>, private val provider: IntcodeInputProvider) {

    companion object {

        private const val MODE_POSITION = 0L
        private const val MODE_IMMEDIATE = 1L
        private const val MODE_RELATIVE = 2L
    }

    private val memory = mutableMapOf<Int, Long>()
    private var i = 0 // Instruction pointer.
    private var r = 0 // Relative base.

    init {
        program.indices.forEach { memory[it] = program[it] }
    }

    fun run(): Long {
        while (true) {
            if (!memory.containsKey(i)) {
                throw IndexOutOfBoundsException("Unable to find opcode in memory at address: $i")
            }
            val opcode = memory[i]!! % 100 // DE
            if (opcode == 99L) {
                throw IntcodeHalt()
            }
            val mode0 = (memory[i]!! / 100) % 10 // C
            val mode1 = (memory[i]!! / 1000) % 10 // B
            val mode2 = (memory[i]!! / 10000) % 10 // A
            val a = if (opcode != 3L) getParameterValue(i + 1, mode0) else -1L
            val b = if (opcode != 3L && opcode != 4L && opcode != 9L) getParameterValue(i + 2, mode1) else -1L
            val address = when (opcode) {
                3L -> getParameterAddress(i + 1, mode0)
                1L, 2L, 7L, 8L -> getParameterAddress(i + 3, mode2)
                else -> -1
            }
            when (opcode) {
                1L -> { // Add
                    memory[address] = a + b
                    i += 4
                }
                2L -> { // Multiply
                    memory[address] = a * b
                    i += 4
                }
                3L -> { // Input
                    val input = provider.getInput()
                    memory[address] = input
                    i += 2
                }
                4L -> { // Output
                    i += 2
                    return a
                }
                5L -> { // Jump-if-true
                    i = if (a != 0L) b.toInt() else i + 3
                }
                6L -> { // Jump-if-false
                    i = if (a == 0L) b.toInt() else i + 3
                }
                7L -> { // Less-than
                    memory[address] = if (a < b) 1 else 0
                    i += 4
                }
                8L -> { // Equal
                    memory[address] = if (a == b) 1 else 0
                    i += 4
                }
                9L -> {
                    r += a.toInt()
                    i += 2
                }
                else -> throw IllegalArgumentException("Unexpected opcode for instruction at `${i}`: $opcode")
            }
        }
    }

    private fun getParameterValue(index: Int, mode: Long): Long {
        val value = memory[index] ?: 0L
        return when (mode) {
            MODE_POSITION -> memory[value.toInt()] ?: 0L
            MODE_IMMEDIATE -> value
            MODE_RELATIVE -> memory[r + value.toInt()] ?: 0L
            else -> {
                throw IllegalArgumentException("Unexpected parameter mode: $mode")
            }
        }
    }

    private fun getParameterAddress(index: Int, mode: Long): Int {
        val value = memory[index]!!.toInt()
        return when (mode) {
            MODE_POSITION -> value
            MODE_IMMEDIATE -> {
                throw RuntimeException("Unexpected immediate parameter mode for address.")
            }
            MODE_RELATIVE -> r + value
            else -> {
                throw IllegalArgumentException("Unexpected parameter mode: $mode")
            }
        }
    }
}

class IntcodeHalt: RuntimeException()
