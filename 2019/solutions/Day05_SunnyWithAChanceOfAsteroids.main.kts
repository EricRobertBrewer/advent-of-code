@file:Import("../Intcode.kts")

class Day05_SunnyWithAChanceOfAsteroids {

    companion object {

        fun getAnswer(lines: List<String>, part: Int): Long {
            val program = lines[0].split(",").map { it.toInt() }

            val input = if (part == 1) 1 else 5

            val intcode = Intcode(program)
            intcode.pushInput(input)
            var output: Int? = null
            while (true) {
                try {
                    val outputNext = intcode.run()
                    if (output != null && output != 0) {
                        throw RuntimeException("Unexpected output for instruction: $output")
                    }
                    output = outputNext
                } catch (_: HaltException) {
                    break
                }
            }
            return output!!.toLong()
        }
    }
}
