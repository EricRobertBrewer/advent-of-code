#load "../AocUtil.csx"

using System;
using System.Collections.Immutable;

class Day02_PasswordPhilosophy : AocUtil.Solver {

    public long Solve(ImmutableList<string> lines, int part) {
        int nValid = 0;
        foreach (string line in lines) {
            string[] policyPassword = line.Split(": ");
            string[] abCheck = policyPassword[0].Split(" ");
            string[] ab = abCheck[0].Split("-");
            int a = Int32.Parse(ab[0]);
            int b = Int32.Parse(ab[1]);
            char check = abCheck[1][0];
            string password = policyPassword[1];

            if (part == 1) {
                int nCheck = 0;
                foreach (char c in password) {
                    if (c == check) {
                        nCheck++;
                    }
                }
                if (a <= nCheck && nCheck <= b) {
                    nValid++;
                }
            } else {
                if ((password[a - 1] == check && password[b - 1] != check) ||
                        password[a - 1] != check && password[b - 1] == check) {
                    nValid++;
                }
            }
        }
        return nValid;
    }
}

AocUtil.RunSolver(2, Args, new Day02_PasswordPhilosophy());
