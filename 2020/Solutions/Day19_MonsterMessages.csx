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
        if (indexEmpty == -1) {
            throw new SystemException("Unable to find empty line.");
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
            List<string> matches = Matches(message, 0, ruleToChar, ruleToPatterns);
            foreach (string match in matches) {
                if (match == message) {
                    answer++;
                    break;
                }
            }
        }
        return answer;
    }

    private static List<string> Matches(
        string message,
        int rule,
        Dictionary<int, char> ruleToChar,
        Dictionary<int, List<List<int>>> ruleToPatterns
    ) {
        List<string> matches = new List<string>();
        if (ruleToChar.ContainsKey(rule)) {
            char c = ruleToChar[rule];
            if (message.Length > 0 && message[0] == c) {
                matches.Add("" + c);
            }
        } else if (ruleToPatterns.ContainsKey(rule)) {
            List<List<int>> patterns = ruleToPatterns[rule]; // Like: `9 14 | 10 1`
            foreach (List<int> pattern in patterns) { // Like: `9 14`
                List<string> patternMatches = new List<string> {""}; // At the currently processed rule.
                foreach (int patternRule in pattern) { // Like: `9`
                    List<string> patternMatchesNext = new List<string>(); // Hold for next rule to be processed.
                    foreach (string match in patternMatches) {
                        string tail = message.Substring(match.Length);
                        List<string> tailMatches = Matches(tail, patternRule, ruleToChar, ruleToPatterns);
                        foreach (string tailMatch in tailMatches) {
                            patternMatchesNext.Add(match + tailMatch);
                        }
                    }
                    patternMatches = patternMatchesNext;
                }
                foreach (string match in patternMatches) { // Survived through each rule in this pattern.
                    matches.Add(match);
                }
            }
        } else {
            throw new SystemException("Unknown rule: " + rule);
        }
        return matches;
    }
}

AocUtil.RunSolver(19, Args, new Day19_MonsterMessages());
