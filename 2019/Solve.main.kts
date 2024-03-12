#!usr/bin/env kotlin

@file:Import(
        "AocUtil.kts",
        "solutions/Day01_TheTyranyOfTheRocketEquation.kts",
        "solutions/Day02_1202ProgramAlarm.kts",
        "solutions/Day03_CrossedWires.kts",
        "solutions/Day04_SecureContainer.kts",
        "solutions/Day05_SunnyWithAChanceOfAsteroids.kts",
)

import kotlin.time.measureTime

val year = 2019

@OptIn(kotlin.time.ExperimentalTime::class)
fun main() {
    if (args.count() < 2 || args.count() > 3) {
        throw RuntimeException("Usage: kotlin ${year}/Solve.main.kts <day> <part>")
    }
    val day = args[0].toInt()
    val part = args[1].toInt()
    var example = false
    if (args.count() > 2) {
        example = args[2] == "-x" || args[2] == "--example"
    }

    val lines = if (example) {
        AocUtil.getExampleInput(year, day)
    } else {
        AocUtil.getOrDownloadInput(year, day)
    }

    val duration = measureTime { println(solve(day, part, lines)) }
    println("Time: ${duration.toIsoString()}")
}

val solvers = mapOf(
        1 to Day01_TheTyranyOfTheRocketEquation::getAnswer,
        2 to Day02_1202ProgramAlarm::getAnswer,
        3 to Day03_CrossedWires::getAnswer,
        4 to Day04_SecureContainer::getAnswer,
        5 to Day05_SunnyWithAChanceOfAsteroids::getAnswer,
)

fun solve(day: Int, part: Int, lines: List<String>): Long {
    if (!solvers.containsKey(day)) {
        throw RuntimeException("No solution found for day ${day}.")
    }
    return solvers[day]!!.invoke(lines, part)
}

main()
