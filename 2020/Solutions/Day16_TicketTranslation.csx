#load "../AocUtil.csx"

using System;
using System.Collections.Immutable;

class Day16_TicketTranslation : AocUtil.Solver {

    public long Solve(ImmutableList<string> lines, int part) {
        int indexYourTicket = -1;
        int indexNearbyTickets = -1;
        for (int i = 0; i < lines.Count; i++) {
            string line = lines[i];
            if (line == "your ticket:") {
                if (indexYourTicket != -1) {
                    throw new SystemException("Found multiple `your ticket:` lines: " + indexYourTicket + " and " + i);
                }
                indexYourTicket = i;
            } else if (line == "nearby tickets:") {
                if (indexNearbyTickets != -1) {
                    throw new SystemException("Found multiple `nearby tickets:` lines: " + indexNearbyTickets + " and " + i);
                }
                indexNearbyTickets = i;
            }
        }

        var fieldToRanges = new Dictionary<string, TwoRanges>();
        for (int i = 0; i < indexYourTicket - 1; i++) {
            string[] fieldRangeStrs = lines[i].Split(": ");
            string field = fieldRangeStrs[0];
            string[] rangeStrs = fieldRangeStrs[1].Split(" or ");
            string[] range0 = rangeStrs[0].Split("-");
            int start0 = Int32.Parse(range0[0]);
            int end0 = Int32.Parse(range0[1]);
            string[] range1 = rangeStrs[1].Split("-");
            int start1 = Int32.Parse(range1[0]);
            int end1 = Int32.Parse(range1[1]);
            fieldToRanges[field] = new TwoRanges(start0, end0, start1, end1);
        }

        var yourTicket = new List<int>();
        foreach (string s in lines[indexYourTicket + 1].Split(",")) {
            yourTicket.Add(Int32.Parse(s));
        }

        var nearbyTickets = new List<List<int>>();
        for (int i = indexNearbyTickets + 1; i < lines.Count; i++) {
            var ticket = new List<int>();
            foreach (string s in lines[i].Split(",")) {
                ticket.Add(Int32.Parse(s));
            }
            nearbyTickets.Add(ticket);
        }

        // Check ticket values one by one.
        int sum = 0; // Part 1 only.
        var ticketsValid = new List<List<int>>(); // Part 2 only.
        ticketsValid.Add(yourTicket);
        foreach (List<int> ticket in nearbyTickets) {
            bool hasInvalidValue = false;
            foreach (int value in ticket) {
                bool isValid = false; // Falls into ANY field's range.
                foreach (string field in fieldToRanges.Keys) {
                    TwoRanges ranges = fieldToRanges[field];
                    if ((ranges.Start0 <= value && value <= ranges.End0) ||
                            (ranges.Start1 <= value && value <= ranges.End1)) {
                        isValid = true;
                        break;
                    }
                }
                if (!isValid) {
                    sum += value;
                    hasInvalidValue = true;
                }
            }
            if (!hasInvalidValue) {
                ticketsValid.Add(ticket);
            }
        }

        if (part == 1) {
            return sum;
        }

        // Instantiate all columns as initially possible.
        var columnToFields = new Dictionary<int, HashSet<string>>();
        for (int col = 0; col < yourTicket.Count; col++) {
            var fields = new HashSet<string>();
            foreach (string field in fieldToRanges.Keys) {
                fields.Add(field);
            }
            columnToFields[col] = fields;
        }

        // Use process of elimination.
        for (int col = 0; col < yourTicket.Count; col++) {
            foreach (string field in fieldToRanges.Keys) {
                TwoRanges ranges = fieldToRanges[field];
                bool isFieldValid = true;
                foreach (List<int> ticket in ticketsValid) {
                    int value = ticket[col];
                    if (value < ranges.Start0 ||
                            (ranges.End0 < value && value < ranges.Start1) ||
                            ranges.End1 < value) {
                        isFieldValid = false;
                        break;
                    }
                }
                if (!isFieldValid) {
                    Eliminate(col, field, columnToFields);
                }
            }
        }

        long product = 1L;
        foreach (int col in columnToFields.Keys) {
            if (columnToFields[col].Count != 1) {
                throw new SystemException("Elimination didn't work.");
            }
            string field = GetAnything(columnToFields[col]);
            if (field.StartsWith("departure")) {
                product *= yourTicket[col];
            }
        }
        return product;
    }

    private static void Eliminate(int col, string field, Dictionary<int, HashSet<string>> columnToFields) {
        if (columnToFields[col].Count == 1) {
            return; // Don't loop infinitely.
        }

        columnToFields[col].Remove(field);
        if (columnToFields[col].Count == 1) {
            string fieldChosen = GetAnything(columnToFields[col]);
            foreach (int colOther in columnToFields.Keys) {
                if (col == colOther) {
                    continue;
                }
                Eliminate(colOther, fieldChosen, columnToFields);
            }
        }
    }

    private static T GetAnything<T>(HashSet<T> items) {
        foreach (T item in items) {
            return item;
        }
        throw new SystemException("Set is empty.");
    }
}

// Abuse this specific assumption to make breaking from loops a little cleaner.
private class TwoRanges {

    public int Start0;
    public int End0;
    public int Start1;
    public int End1;

    public TwoRanges(int start0, int end0, int start1, int end1) {
        Start0 = start0;
        End0 = end0;
        Start1 = start1;
        End1 = end1;
    }
}

AocUtil.RunSolver(16, Args, new Day16_TicketTranslation());
