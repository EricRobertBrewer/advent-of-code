#load "../AocUtil.csx"

using System;
using System.Collections.Immutable;

class Day04_PassportProcessing : AocUtil.Solver {

    public long Solve(ImmutableList<string> lines, int part) {
        string[] keysRequired = { "byr", "iyr", "eyr", "hgt", "hcl", "ecl", "pid" };
        var eyeColorsValid = new HashSet<string> {"amb", "blu", "brn", "gry", "grn", "hzl", "oth"};

        // Find passport separations.
        var iEnds = new List<int>();
        for (int i = 0; i < lines.Count(); i++) {
            if (lines[i].Count() == 0) {
                iEnds.Add(i);
            }
        }
        iEnds.Add(lines.Count());

        // Collect passports over multiple lines.
        var passportFields = new List<Dictionary<string, string>>();
        for (int x = 0; x < iEnds.Count(); x++) {
            int iStart = (x == 0) ? 0 : iEnds[x - 1] + 1;
            int iEnd = iEnds[x];
            var fields = new Dictionary<string, string>();
            while (iStart < iEnd) {
                string[] keyColonValues = lines[iStart].Split(" ");
                foreach (string keyColonValue in keyColonValues) {
                    string[] keyValue = keyColonValue.Split(":");
                    fields.Add(keyValue[0], keyValue[1]);
                }
                iStart++;
            }
            passportFields.Add(fields);
        }

        // Validate passports.
        int nValid = 0;
        foreach (Dictionary<string, string> fields in passportFields) {
            bool isValid = true;
            foreach (string key in keysRequired) {
                if (!isValid) {
                    break;
                }
                if (!fields.ContainsKey(key)) {
                    isValid = false;
                    break;
                }
                if (part == 2) {
                    string value = fields[key];
                    if (key == "byr") {
                        isValid = IsNumberInBounds(value, 1920, 2002);
                    } else if (key == "iyr") {
                        isValid = IsNumberInBounds(value, 2010, 2020);
                    } else if (key == "eyr") {
                        isValid = IsNumberInBounds(value, 2020, 2030);
                    } else if (key == "hgt") {
                        if (value.Substring(value.Count() - 2) == "cm") {
                            isValid = IsNumberInBounds(value.Substring(0, value.Count() - 2), 150, 193);
                        } else if (value.Substring(value.Count() - 2) == "in") {
                            isValid = IsNumberInBounds(value.Substring(0, value.Count() - 2), 59, 76);
                        } else {
                            isValid = false;
                        }
                    } else if (key == "hcl") {
                        if (value.Count() != 7 || value[0] != '#') {
                            isValid = false;
                        } else {
                            foreach (char c in value.Substring(1)) {
                                if (!('0' <= c && c <= '9') && !('a' <= c && c <= 'f')) {
                                    isValid = false;
                                    break;
                                }
                            }
                        }
                    } else if (key == "ecl") {
                        isValid = eyeColorsValid.Contains(value);
                    } else if (key == "pid") {
                        if (value.Count() != 9) {
                            isValid = false;
                        } else {
                            try {
                                Int32.Parse(value);
                            } catch (Exception) {
                                isValid = false;
                            }
                        }
                    }
                }
            }
            if (isValid) {
                nValid++;
            }
        }
        return nValid;
    }

    static bool IsNumberInBounds(string s, int atLeast, int atMost) {
        try {
            int x = Int32.Parse(s);
            if (atLeast <= x && x <= atMost) {
                return true;
            }
        } catch (Exception) {
        }
        return false;
    }
}

AocUtil.RunSolver(4, Args, new Day04_PassportProcessing());
