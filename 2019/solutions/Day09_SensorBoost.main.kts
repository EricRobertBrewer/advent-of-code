@file:Import("../Intcode.kts")

class Day09_SensorBoost {

    companion object {

        fun getAnswer(lines: List<String>, part: Int): Long {
            val program = lines[0].split(",").map { it.toLong() }
            val intcode = Intcode(program)
            intcode.pushInput(if (part == 1) 1L else 2L)
            return intcode.run()
            // To run Quine example:
//            while (true) {
//                try {
//                    print(intcode.run())
//                } catch (_: HaltException) {
//                    break
//                }
//                print(",")
//            }
//            println()
//            return -1
        }
    }
}
