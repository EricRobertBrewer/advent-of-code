package com.ericrobertbrewer.adventofcode;

import com.ericrobertbrewer.adventofcode.AocUtil;
import com.ericrobertbrewer.adventofcode.CsUtil;

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

    private Solutions() {
    }

    private interface Solution {
        long getAnswer(List<String> lines, int part);
    }

    private static Map<Integer, Solution> SOLUTIONS = new HashMap<>();
    static {
        SOLUTIONS.put(1, Solutions::d01_InverseCaptcha);
        SOLUTIONS.put(2, Solutions::d02_CorruptionChecksum);
        SOLUTIONS.put(3, Solutions::d03_SpiralMemory);
        SOLUTIONS.put(4, Solutions::d04_HighEntropyPassphrases);
        SOLUTIONS.put(5, Solutions::d05_AMazeOfTwistyTrampolinesAllAlike);
        SOLUTIONS.put(6, Solutions::d06_MemoryReallocation);
        SOLUTIONS.put(7, Solutions::d07_RecursiveCircus);
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

        final Map<CsUtil.IntPair, Integer> pointToValue = new HashMap<>();
        pointToValue.put(new CsUtil.IntPair(0, 0), 1);
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
                            final CsUtil.IntPair point = new CsUtil.IntPair(i + oi, j + oj);
                            if (pointToValue.containsKey(point)) {
                                value += pointToValue.get(point);
                            }
                        }
                    }
                    if (value > input) {
                        return value;
                    }
                    pointToValue.put(new CsUtil.IntPair(i, j), value);
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

    public static long d05_AMazeOfTwistyTrampolinesAllAlike(List<String> lines, int part) {
        final int[] instructions = new int[lines.size()];
        for (int i = 0; i < lines.size(); i++) {
            instructions[i] = Integer.parseInt(lines.get(i));
        }
        int ip = 0;
        int steps = 0;
        while (ip >= 0 && ip < instructions.length) {
            int offset = instructions[ip];
            if (part == 1) {
                instructions[ip]++;
            } else {
                if (offset >= 3) {
                    instructions[ip]--;
                } else {
                    instructions[ip]++;
                }
            }
            ip += offset;
            steps++;
        }
        return steps;
    }

    public static long d06_MemoryReallocation(List<String> lines, int part) {
        final String[] tokens = lines.get(0).split("\t");
        final int[] banks = new int[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            banks[i] = Integer.parseInt(tokens[i]);
        }
        int cycle = 0;
        final Map<String, Integer> configurationToCycle = new HashMap<>();
        while (true) {
            final StringBuilder builder = new StringBuilder();
            builder.append(banks[0]);
            for (int i = 1; i < banks.length; i++) {
                builder.append(",");
                builder.append(banks[i]);
            }
            final String configuration = builder.toString();
            if (configurationToCycle.containsKey(configuration)) {
                if (part == 1) {
                    return cycle;
                } else {
                    return cycle - configurationToCycle.get(configuration);
                }
            }
            configurationToCycle.put(configuration, cycle);

            int iMax = 0;
            for (int i = 1; i < banks.length; i++) {
                if (banks[i] > banks[iMax]) {
                    iMax = i;
                }
            }
            int blocks = banks[iMax];
            banks[iMax] = 0;
            for (int i = 0; i < banks.length; i++) {
                banks[i] += blocks / banks.length; // Even distribution.
            }
            for (int di = 1; di <= blocks % banks.length; di++) {
                banks[(iMax + di) % banks.length]++; // Overflow.
            }
            cycle++;
        }
    }

    public static long d07_RecursiveCircus(List<String> lines, int part) {
        final Map<String, List<String>> programToPrograms = new HashMap<>();
        final Map<String, Integer> programToWeight = new HashMap<>();
        for (String line : lines) {
            final String[] programWeightPrograms = line.split(" -> ");
            final String programWeight = programWeightPrograms[0];
            final int space = programWeight.indexOf(' ');
            final String program = programWeight.substring(0, space);
            final int weight = Integer.parseInt(programWeight.substring(space + 2, programWeight.length() - 1));
            final List<String> programs = new ArrayList<>();
            if (programWeightPrograms.length > 1) {
                for (String other : programWeightPrograms[1].split(", ")) {
                    programs.add(other);
                }
            }
            programToPrograms.put(program, programs);
            programToWeight.put(program, weight);
        }

        if (part == 1) {
            final Set<String> programs = new HashSet<>();
            for (String program : programToPrograms.keySet()) {
                final List<String> others = programToPrograms.get(program);
                for (String other : others) {
                    programs.add(other);
                }
            }
            for (String program : programToPrograms.keySet()) {
                if (!programs.contains(program)) {
                    System.out.println(program);
                    return 0;
                }
            }
        }

        final Map<String, Integer> programToTotalWeight = new HashMap<>();
        while (programToTotalWeight.size() < programToWeight.size()) {
            for (String program : programToPrograms.keySet()) {
                final List<String> others = programToPrograms.get(program);
                int othersWeight = 0;
                for (String other : others) {
                    if (!programToTotalWeight.containsKey(other)) {
                        othersWeight = -1;
                        break;
                    }
                    final int weight = programToTotalWeight.get(other);
                    othersWeight += weight;
                }
                if (othersWeight != -1) {
                    if (others.size() > 0) {
                        final int weight0 = programToTotalWeight.get(others.get(0));
                        for (int i = 1; i < others.size(); i++) {
                            final int weight = programToTotalWeight.get(others.get(i));
                            if (weight != weight0) {
                                final int mediatorWeight = programToTotalWeight.get(others.get(i == 1 ? 2 : 1));
                                if (weight0 == mediatorWeight) {
                                    return programToWeight.get(others.get(i)) - weight + weight0;
                                } else {
                                    return programToWeight.get(others.get(0)) - weight0 + weight;
                                }
                            }
                        }
                    }
                    programToTotalWeight.put(program, programToWeight.get(program) + othersWeight);
                }
            }
        }
        throw new RuntimeException("Unable to find non-matching program weight.");
    }
}
