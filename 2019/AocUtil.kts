import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import kotlin.time.measureTime

val year = 2019

private val inputPath = "./input"
private val envVar = "AOC_SESSION"

@OptIn(kotlin.time.ExperimentalTime::class)
fun solve(day: Int, solution: (List<String>, Int) -> Long) {
    if (args.isEmpty() || args.count() > 2) {
        throw RuntimeException("Usage: kotlin ${year}/solutions/Day##_[NAME].main.kts <part>")
    }
    val part = args[0].toInt()
    var example = false
    if (args.count() > 1) {
        example = args[1] == "-x" || args[1] == "--example"
    }

    val lines = if (example) {
        getExampleInput(year, day)
    } else {
        getOrDownloadInput(year, day)
    }

    val duration = measureTime { println(solution.invoke(lines, part)) }
    println("Time: ${duration.toIsoString()}")
}

fun getOrDownloadInput(year: Int, day: Int): List<String> {
    val yearDir = getYearDir(year)
    val inputFile = File(yearDir, "${getDayName(day)}.txt")
    if (inputFile.exists()) {
        return inputFile.readLines()
    }

    val token = System.getenv(envVar)
    if (token == null) {
        System.err.println("Set the environment variable `${envVar}` as your AoC session token " +
                "(found in Dev Tools > Application > Cookies > 'session'): " +
                "$ export ${envVar}=<my_token>")
        throw RuntimeException("Unable to find session token.")
    }

    val url = URL("https://adventofcode.com/${year}/day/${day}/input")
    val connection = url.openConnection() as HttpURLConnection
    connection.requestMethod = "GET"
    connection.setRequestProperty("cookie", "session=${token}")
    connection.connect()
    val code = connection.responseCode
    if (code != 200) {
        throw RuntimeException("Unexpected response code to ${url.path}: ${code}; ${connection.responseMessage}")
    }
    val reader = connection.inputStream.bufferedReader()
    val text = reader.readText()
    reader.close()
    val writer = inputFile.bufferedWriter()
    writer.write(text)
    writer.close()
    return getOrDownloadInput(year, day)
}

fun getExampleInput(year: Int, day: Int): List<String> {
    val yearDir = getYearDir(year)
    val inputFile = File(yearDir, "${getDayName(day)}-example.txt")
    return inputFile.readLines()
}

fun getYearDir(year: Int): File {
    val inputDir = File(inputPath)
    val yearDir = File(inputDir, "$year")
    if (!yearDir.exists() && !yearDir.mkdirs()) {
        throw RuntimeException("Unable to create year directory: ${yearDir.path}")
    }
    return yearDir
}

fun getDayName(day: Int): String {
    return day.toString().padStart(2, '0')
}
