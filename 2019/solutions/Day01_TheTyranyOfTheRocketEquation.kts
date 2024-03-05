class Day01_TheTyranyOfTheRocketEquation {

    companion object {

        fun getAnswer(lines: List<String>, part: Int): Long {
            val modules = lines.map { it.toLong() }

            if (part == 1) {
                return modules.sumOf { it / 3 - 2 }
            }

            var sum = 0L
            for (module in modules) {
                var fuel = module / 3 - 2
                while (fuel > 0) {
                    sum += fuel
                    fuel = fuel / 3 - 2
                }
            }
            return sum
        }
    }
}
