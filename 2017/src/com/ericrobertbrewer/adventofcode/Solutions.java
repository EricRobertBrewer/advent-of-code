package com.ericrobertbrewer.adventofcode;

import com.ericrobertbrewer.adventofcode.AocUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Solutions {

    private static final int YEAR = 2017;

    private interface Solution {
        long getAnswer(List<String> lines, int part);
    }

    private static Map<Integer, Solution> SOLUTIONS = new HashMap<>();
    static {
        SOLUTIONS.put(1, Solutions::d01_InverseCaptcha);
        SOLUTIONS.put(2, Solutions::d02_CorruptionChecksum);
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            throw new IllegalArgumentException("Usage: Solutions <day> <part> [--example|-x]");
        }
        final int day = Integer.valueOf(args[0]);
        final int part = Integer.valueOf(args[1]);
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
                    sum += Integer.valueOf("" + captcha.charAt(i));
                }
            }
            if (captcha.charAt(n - 1) == captcha.charAt(0)) {
                sum += Integer.valueOf("" + captcha.charAt(n - 1));
            }
        } else {
            for (int i = 0; i < n / 2; i++) {
                if (captcha.charAt(i) == captcha.charAt(i + (n / 2))) {
                    sum += 2 * Integer.valueOf("" + captcha.charAt(i));
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
                    final int value = Integer.valueOf(token);
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
                    final int iValue = Integer.valueOf(tokens[i]);
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
}
