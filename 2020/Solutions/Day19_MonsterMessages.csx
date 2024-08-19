#load "../AocUtil.csx"

using System;
using System.Collections.Immutable;

class Day19_MonsterMessages : AocUtil.Solver {

    public long Solve(ImmutableList<string> lines, int part) {
        int indexEmpty = -1;
        for (int i = 0; i < lines.Count; i++) {
            if (lines[i] == "") {
                if (indexEmpty != -1) {
                    throw new SystemException("Found two empty lines: " + indexEmpty + " and " + i);
                } else {
                    indexEmpty = i;
                }
            }
        }

        var ruleToChar = new Dictionary<int, char>();
        var ruleToPatterns = new Dictionary<int, List<List<int>>>();
        for (int i = 0; i < indexEmpty; i++) {
            string[] rulePatterns = lines[i].Split(": ");
            int rule = Int32.Parse(rulePatterns[0]);
            if (part == 2) {
                if (rule == 8) {
                    rulePatterns[1] = "42 | 42 8";
                } else if (rule == 11) {
                    rulePatterns[1] = "42 31 | 42 11 31";
                }
            }
            if (rulePatterns[1].StartsWith("\"")) {
                ruleToChar[rule] = rulePatterns[1][1];
            } else {
                string[] patternStrs = rulePatterns[1].Split(" | ");
                var patterns = new List<List<int>>();
                foreach (string patternStr in patternStrs) {
                    var rules = new List<int>();
                    string[] ruleStrs = patternStr.Split(" ");
                    foreach (string ruleStr in ruleStrs) {
                        rules.Add(Int32.Parse(ruleStr));
                    }
                    patterns.Add(rules);
                }
                ruleToPatterns[rule] = patterns;
            }
        }

        int answer = 0;
        for (int i = indexEmpty + 1; i < lines.Count; i++) {
            string message = lines[i];
//             Console.WriteLine("" + (i - indexEmpty - 1) + ": " + message);
            string match = Match(message, 0, ruleToChar, ruleToPatterns);
            if (match == message) {
                Console.WriteLine(message);
                answer++;
            }
        }
        return answer;
    }

    static string Match(string message, int rule, Dictionary<int, char> ruleToChar, Dictionary<int, List<List<int>>> ruleToPatterns) {
//         Console.WriteLine("  Match(" + message + ", " + rule + ")");
        if (ruleToChar.ContainsKey(rule)) {
            char c = ruleToChar[rule];
            if (message.Length > 0 && message[0] == c) {
                return "" + c;
            } else {
                return "!";
            }
        }
        if (!ruleToPatterns.ContainsKey(rule)) {
            throw new SystemException("Unknown rule: " + rule);
        }
        List<List<int>> patterns = ruleToPatterns[rule];
        foreach (List<int> pattern in patterns) {
            string match = "";
            foreach (int patternRule in pattern) {
                match += Match(message.Substring(match.Length), patternRule, ruleToChar, ruleToPatterns);
                if (!message.StartsWith(match)) {
                    break;
                }
            }
            if (message.StartsWith(match)) {
                return match;
            }
        }
        return "!";
    }
}

AocUtil.RunSolver(19, Args, new Day19_MonsterMessages());
