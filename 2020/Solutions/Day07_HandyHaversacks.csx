#load "../AocUtil.csx"

using System;
using System.Collections.Immutable;

class Day07_HandyHaversacks : AocUtil.Solver {

    public long Solve(ImmutableList<string> lines, int part) {
        // Collect forward and backward bag-to-bag mappings.
        var bagToInsideToCount = new Dictionary<string, Dictionary<string, int>>();
        var bagToOutsideToCount = new Dictionary<string, Dictionary<string, int>>();
        foreach (string line in lines) {
            string[] outsideInsides = line.Substring(0, line.Count() - 1).Split(" bags contain ");
            string outside = outsideInsides[0];
            bagToInsideToCount.Add(outside, new Dictionary<string, int>());
            if (!outsideInsides[1].StartsWith("no other")) {
                string[] sInsides = outsideInsides[1].Split(", ");
                foreach (string sInside in sInsides) {
                    int iSpaceFirst = sInside.IndexOf(' ');
                    int iSpaceLast = sInside.LastIndexOf(' ');
                    int count = Int32.Parse(sInside.Substring(0, iSpaceFirst));
                    string inside = sInside.Substring(iSpaceFirst + 1, iSpaceLast - iSpaceFirst - 1);
                    bagToInsideToCount[outside].Add(inside, count);
                    if (!bagToOutsideToCount.ContainsKey(inside)) {
                        bagToOutsideToCount.Add(inside, new Dictionary<string, int>());
                    }
                    bagToOutsideToCount[inside].Add(outside, count);
                }
            }
        }

        if (part == 1) {
            // Collect all possible holders of "shiny gold" bags.
            var bagColors = new HashSet<string>();
            var visited = new HashSet<string>();
            var frontier = new Queue<string>();
            frontier.Enqueue("shiny gold");
            while (frontier.Count > 0) {
                string bag = frontier.Dequeue();
                if (visited.Contains(bag)) {
                    continue;
                }
                visited.Add(bag);
                bagColors.Add(bag);
                if (bagToOutsideToCount.ContainsKey(bag)) {
                    foreach (string outside in bagToOutsideToCount[bag].Keys) {
                        frontier.Enqueue(outside);
                    }
                }
            }
            return bagColors.Count() - 1; // Subtract "shiny gold" itself.
        }

        return BagInsideCountNested("shiny gold", bagToInsideToCount);
    }

    static int BagInsideCountNested(string bag, Dictionary<string, Dictionary<string, int>> bagToInsideToCount) {
        if (!bagToInsideToCount.ContainsKey(bag)) {
            return 0;
        }

        Dictionary<string, int> insideToCount = bagToInsideToCount[bag];
        int countTotal = 0;
        foreach (string inside in insideToCount.Keys) {
            int count = insideToCount[inside];
            countTotal += count;
            countTotal += count * BagInsideCountNested(inside, bagToInsideToCount);
        }
        return countTotal;
    }
}

AocUtil.RunSolver(7, Args, new Day07_HandyHaversacks());
