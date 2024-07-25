@file:Import(
    "../AocUtil.kts",
    "../Intcode.kts",
)


solve(25, fun(lines: List<String>, part: Int): Long {
    val program = lines[0].split(",").map { it.toLong() }
    val queue = CommandIntcodeQueue()
    val intcode = Intcode(program, queue)

    while (true) {
        val output = intcode.run()
        val c = Char(output.toInt())
        print(c)
    }
    return -1L
})

class CommandIntcodeQueue: IntcodeQueue() {

    override fun getInput(): Long {
        try {
            return super.getInput()
        } catch (_: NoSuchElementException) {
            val input = readLine()!!
            for (c in input) {
                pushInput(c.code.toLong())
            }
            pushInput('\n'.code.toLong())
            return super.getInput()
        }
    }
}
