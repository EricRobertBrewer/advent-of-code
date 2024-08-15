#load "../AocUtil.csx"

using System;
using System.Collections.Immutable;

class Day14_DockingData : AocUtil.Solver {

    public long Solve(ImmutableList<string> lines, int part) {
        string mask = null;
        var memory = new Dictionary<ulong, ulong>();
        foreach (string line in lines) {
            string[] leftRight = line.Split(" = ");
            string left = leftRight[0];
            string right = leftRight[1];
            if (left == "mask") {
                mask = right;
            } else if (mask == null) {
                throw new SystemException("Mask is not set.");
            } else {
                ulong address = UInt64.Parse(left.Substring(4, left.Length - 5));
                ulong value = UInt64.Parse(right);
                if (part == 1) {
                    string valueBits = ToBinaryString(value);
                    // Mask value.
                    ulong valueMasked = 0;
                    ulong digitValue = 1;
                    for (int i = 0; i < mask.Length; i++) {
                        if (mask[mask.Length - 1 - i] == '1') {
                            valueMasked += digitValue;
                        } else if (mask[mask.Length - 1 - i] == '0') {
                            // Do nothing.
                        } else if (valueBits.Length - 1 - i >= 0 && valueBits[valueBits.Length - 1 - i] == '1') {
                            valueMasked += digitValue;
                        }
                        digitValue *= 2;
                    }
                    memory[address] = valueMasked;
                } else {
                    string addressBits = ToBinaryString(address);
                    var addressesMasked = new List<ulong>();
                    addressesMasked.Add(0);
                    ulong digitValue = 1;
                    for (int i = 0; i < mask.Length; i++) {
                        var addressesMaskedNext = new List<ulong>();
                        if (mask[mask.Length - 1 - i] == '1') {
                            // Overwrite with `1`.
                            foreach (ulong addressMasked in addressesMasked) {
                                addressesMaskedNext.Add(addressMasked + digitValue);
                            }
                        } else if (mask[mask.Length - 1 - i] == '0') {
                            // Use original address bit, if it exists.
                            foreach (ulong addressMasked in addressesMasked) {
                                if (addressBits.Length - 1 - i >= 0 && addressBits[addressBits.Length - 1 - i] == '1') {
                                    addressesMaskedNext.Add(addressMasked + digitValue);
                                } else {
                                    addressesMaskedNext.Add(addressMasked);
                                }
                            }
                        } else if (mask[mask.Length - 1 - i] == 'X') {
                            // Floating.
                            foreach (ulong addressMasked in addressesMasked) {
                                addressesMaskedNext.Add(addressMasked);
                                addressesMaskedNext.Add(addressMasked + digitValue);
                            }
                        }
                        addressesMasked = addressesMaskedNext;
                        digitValue *= 2;
                    }
                    foreach (ulong addressMasked in addressesMasked) {
                        memory[addressMasked] = value;
                    }
                }
            }
        }

        ulong sum = 0;
        foreach (ulong address in memory.Keys) {
            ulong valueMasked = memory[address];
            sum += valueMasked;
        }
        Console.WriteLine(sum);
        return -1;
    }

    static string ToBinaryString(ulong x) {
        string bits = "";
        while (x > 0) {
            bits = "" + (x % 2) + bits;
            x /= 2;
        }
        return bits;
    }
}

AocUtil.RunSolver(14, Args, new Day14_DockingData());
