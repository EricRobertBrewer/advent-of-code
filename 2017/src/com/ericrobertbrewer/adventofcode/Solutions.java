package com.ericrobertbrewer.adventofcode;

import com.ericrobertbrewer.adventofcode.AocUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class Solutions {

    private static final int YEAR = 2017;

    private interface Solution {
        long getAnswer(List<String> lines, int part);
    }

    private static Map<Integer, Solution> SOLUTIONS = new HashMap<>();
    static {
        SOLUTIONS.put(1, Solutions::d01_InverseCaptcha);
        SOLUTIONS.put(2, Solutions::d02_CorruptionChecksum);
        SOLUTIONS.put(3, Solutions::d03_SpiralMemory);
        SOLUTIONS.put(4, Solutions::d04_HighEntropyPassphrases);
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            throw new IllegalArgumentException("Usage: Solutions <day> <part> [--example|-x]");
        }
        final int day = Integer.parseInt(args[0]);
        final int part = Integer.parseInt(args[1]);
        final boolean example;
        if (args.length > 2) {
            if ("--example".equals(args[3]) || "-x".equals(args[2])) {
                example = true;
            } else {
                throw new IllegalArgumentException("Unexpected argument: " + args[2]);
            }
        } else {
            example = false;
        }

        final File inputFile;
        if (example) {
            inputFile = AocUtil.getExampleFile(YEAR, day);
        } else {
            inputFile = AocUtil.getOrDownloadInputFile(YEAR, day);
        }

        final long start = System.currentTimeMillis();
        final long answer = solve(day, part, inputFile);
        final long duration = System.currentTimeMillis() - start;

        System.out.println(answer);
        System.out.println("Time: " + duration + " ms");
    }

    public static long solve(int day, int part, File inputFile) throws IOException {
        final List<String> lines = Files.readAllLines(inputFile.toPath());

        if (!SOLUTIONS.containsKey(day)) {
            throw new IllegalArgumentException("No solution for day: " + day);
        }
        final Solution solution = SOLUTIONS.get(day);
        return solution.getAnswer(lines, part);
    }

    private static long d01_InverseCaptcha(List<String> lines, int part) {
        final String captcha = lines.get(0);
        final int n = captcha.length();
        long sum = 0;
        if (part == 1) {
            for (int i = 0; i < n - 1; i++) {
                if (captcha.charAt(i) == captcha.charAt(i + 1)) {
                    sum += Integer.parseInt("" + captcha.charAt(i));
                }
            }
            if (captcha.charAt(n - 1) == captcha.charAt(0)) {
                sum += Integer.parseInt("" + captcha.charAt(n - 1));
            }
        } else {
            for (int i = 0; i < n / 2; i++) {
                if (captcha.charAt(i) == captcha.charAt(i + (n / 2))) {
                    sum += 2 * Integer.parseInt("" + captcha.charAt(i));
                }
            }
        }
        return sum;
    }

    private static long d02_CorruptionChecksum(List<String> lines, int part) {
        long sum = 0;
        for (final String line : lines) {
            final String[] tokens = line.split("\t");
            if (part == 1) {
                int min = -1, max = -1;
                for (final String token : tokens) {
                    final int value = Integer.parseInt(token);
                    if (min == -1 || value < min) {
                        min = value;
                    }
                    if (max == -1 || value > max) {
                        max = value;
                    }
                }
                sum += max - min;
            } else {
                boolean found = false;
                for (int i = 0; i < tokens.length - 1 && !found; i++) {
                    final int iValue = Integer.parseInt(tokens[i]);
                    for (int j = i + 1; j < tokens.length && !found; j++) {
                        final int jValue = Integer.valueOf(tokens[j]);
                        if (iValue % jValue == 0) {
                            sum += iValue / jValue;
                            found = true;
                        } else if (jValue % iValue == 0) {
                            sum += jValue / iValue;
                            found = true;
                        }
                    }
                }
            }
        }
        return sum;
    }

    public static long d03_SpiralMemory(List<String> lines, int part) {
        final int input = Integer.parseInt(lines.get(0));
        if (part == 1) {
            int layer = 0;
            int area = 1;
            while (input > area) {
                layer++;
                area = (2 * layer + 1) * (2 * layer + 1);
            }
            final int circumference = 8 * layer;
            final int side = 2 * layer;
            final int innerArea = area - circumference;
            final int tick = input - innerArea;
            return Math.abs((tick % side) - (side / 2)) + layer;
        }

        final Map<IntPair, Integer> pointToValue = new HashMap<>();
        pointToValue.put(new IntPair(0, 0), 1);
        int layer = 1;
        final int[][] deltas = {{-1, 0}, {0, -1}, {1, 0}, {0, 1}};
        while (true) {
            int i = layer;
            int j = layer;
            final int side = 2 * layer;
            for (int[] delta : deltas) {
                final int di = delta[0];
                final int dj = delta[1];
                for (int k = 0; k < side; k++) {
                    i += di;
                    j += dj;
                    int value = 0;
                    for (int oi = -1; oi <= 1; oi++) {
                        for (int oj = -1; oj <= 1; oj++) {
                            final IntPair point = new IntPair(i + oi, j + oj);
                            if (pointToValue.containsKey(point)) {
                                value += pointToValue.get(point);
                            }
                        }
                    }
                    if (value > input) {
                        return value;
                    }
                    pointToValue.put(new IntPair(i, j), value);
                }
            }
            layer++;
        }
    }

    public static long d04_HighEntropyPassphrases(List<String> lines, int part) {
        int valid = 0;
        for (String line : lines) {
            final Set<String> words = new HashSet<>();
            boolean isValid = true;
            for (String word : line.split(" ")) {
                if (part == 2) {
                    char[] array = word.toCharArray();
                    Arrays.sort(array);
                    word = String.valueOf(array);
                }
                if (words.contains(word)) {
                    isValid = false;
                    break;
                }
                words.add(word);
            }
            if (isValid) {
                valid++;
            }
        }
        return valid;
    }

    private static final class IntPair {

        final int a;
        final int b;

        IntPair(int a, int b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public int hashCode() {
            int result = 17;
            result = 31 * result + a;
            result = 31 * result + b;
            return result;
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof IntPair)) {
                return false;
            }
            final IntPair otherPair = (IntPair) other;
            return otherPair.a == a && otherPair.b == b;
        }
    }
}
