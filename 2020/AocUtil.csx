using System;
using System.Collections.Immutable;
using System.IO;
using System.Net.Http;

class AocUtil {

    public interface Solver {

        public long Solve(ImmutableList<string> lines, int part);
    }

    const int YEAR = 2020;
    const string PATH_INPUT = "./input";
    const string ENV_VAR = "AOC_SESSION";

    static readonly HttpClient client = new HttpClient();

    public static void RunSolver(int day, IList<string> args, Solver solver) {
        if (args.Count < 1 || args.Count > 2) {
            throw new SystemException("Usage: dotnet script ./solutions/Day##_XXXX.csx <part> [--example|-x]");
        }
        int part = Int32.Parse(args[0]);
        bool example = false;
        if (args.Count > 1) {
            example = args[1] == "--example" || args[1] == "-x";
        }

        ImmutableList<string> lines;
        if (example) {
            lines = GetExampleInput(YEAR, day);
        } else {
            lines = GetOrDownloadInput(YEAR, day);
        }

        var watch = Stopwatch.StartNew();
        long answer = solver.Solve(lines, part);
        watch.Stop();
        Console.WriteLine(answer);
        Console.WriteLine("Time: {0}ms", watch.Elapsed.TotalMilliseconds);
    }

    private static ImmutableList<string> GetOrDownloadInput(int year, int day) {
        string yearDir = GetYearDir(year);
        Directory.CreateDirectory(yearDir);
        string pathInput = Path.Combine(yearDir, GetDayName(day) + ".txt");
        if (File.Exists(pathInput)) {
            return File.ReadLines(pathInput).ToImmutableList();
        }

        string token = Environment.GetEnvironmentVariable(ENV_VAR);
        if (token == null) {
            Console.Error.WriteLine(
                "Set the environment variable `" + ENV_VAR + "` as your AoC session token " +
                "(found in Dev Tools > Application > Cookies > 'session'): " +
                "$ export " + ENV_VAR + "=<my_token>");
            throw new SystemException("Unable to find session token.");
        }

        var uri = new Uri(String.Format("https://adventofcode.com/{0}/day/{1}/input", year, day));
        var request = new HttpRequestMessage(new HttpMethod("GET"), uri);
        request.Headers.Add("cookie", "session=" + token);
        HttpResponseMessage response = client.Send(request);
        string body = response.Content.ReadAsStringAsync().Result;

        using (var streamWriter = new StreamWriter(pathInput)) {
            streamWriter.Write(body);
        }
        return GetOrDownloadInput(year, day);
    }

    static ImmutableList<string> GetExampleInput(int year, int day) {
        string yearDir = GetYearDir(year);
        string pathInput = Path.Combine(yearDir, GetDayName(day) + "-example.txt");
        return File.ReadLines(pathInput).ToImmutableList();
    }

    static string GetYearDir(int year) {
        return Path.Combine(PATH_INPUT, year.ToString());
    }

    static string GetDayName(int day) {
        return day.ToString().PadLeft(2, '0');
    }
}
