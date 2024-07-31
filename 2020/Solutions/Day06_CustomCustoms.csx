#load "../AocUtil.csx"

using System;
using System.Collections.Immutable;

class Day06_CustomCustoms : AocUtil.Solver {

    public long Solve(ImmutableList<string> lines, int part) {
        var groupAnswers = new List<List<string>>();
        var personAnswersCurrent = new List<string>();
        foreach (string line in lines) {
            if (line.Count() == 0) {
                groupAnswers.Add(personAnswersCurrent);
                personAnswersCurrent = new List<string>();
            } else {
                personAnswersCurrent.Add(line);
            }
        }
        groupAnswers.Add(personAnswersCurrent);

        int yesCount = 0;
        foreach (List<string> personAnswers in groupAnswers) {
            if (part == 1) {
                // Count "anyone" yes answers.
                var answerSet = new HashSet<char>();
                foreach (string answers in personAnswers) {
                    foreach (char answer in answers) {
                        answerSet.Add(answer);
                    }
                }
                yesCount += answerSet.Count();
            } else {
                // Count "everyone" yes answers.
                var otherAnswerSets = new List<HashSet<char>>();
                for (int i = 1; i < personAnswers.Count(); i++) {
                    var answerSet = new HashSet<char>();
                    foreach (char answer in personAnswers[i]) {
                        answerSet.Add(answer);
                    }
                    otherAnswerSets.Add(answerSet);
                }
                foreach (char answer in personAnswers[0]) {
                    bool allYes = true;
                    foreach (HashSet<char> answerSet in otherAnswerSets) {
                        if (!answerSet.Contains(answer)) {
                            allYes = false;
                            break;
                        }
                    }
                    if (allYes) {
                        yesCount++;
                    }
                }
            }
        }
        return yesCount;
    }
}

AocUtil.RunSolver(6, Args, new Day06_CustomCustoms());
