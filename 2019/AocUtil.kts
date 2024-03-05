import java.io.File
import java.net.HttpURLConnection
import java.net.URL

class AocUtil {

    companion object {
        private const val INPUT_DIR = "./input"
        private const val ENV_VAR = "AOC_SESSION"

        fun getOrDownloadInput(year: Int, day: Int): List<String> {
            val yearDir = getYearDir(year)
            val inputFile = File(yearDir, "${getDayName(day)}.txt")
            if (inputFile.exists()) {
                return inputFile.readLines()
            }

            val token = System.getenv(ENV_VAR)
            if (token == null) {
                System.err.println("Set the environment variable `${ENV_VAR}` as your AoC session token " +
                        "(found in Dev Tools > Application > Cookies > 'session'): " +
                        "$ export ${ENV_VAR}=<my_token>")
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

        private fun getYearDir(year: Int): File {
            val inputDir = File(INPUT_DIR)
            val yearDir = File(inputDir, "$year")
            if (!yearDir.exists() && !yearDir.mkdirs()) {
                throw RuntimeException("Unable to create year directory: ${yearDir.path}")
            }
            return yearDir
        }

        private fun getDayName(day: Int): String {
            return day.toString().padStart(2, '0')
        }
    }
}
