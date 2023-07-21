package com.ericrobertbrewer.adventofcode;

import com.ericrobertbrewer.adventofcode.AocUtil;
import com.ericrobertbrewer.adventofcode.CsUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
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
        SOLUTIONS.put(8, Solutions::d08_IHeardYouLikeRegisters);
        SOLUTIONS.put(9, Solutions::d09_StreamProcessing);
        SOLUTIONS.put(10, Solutions::d10_KnotHash);
        SOLUTIONS.put(11, Solutions::d11_HexEd);
        SOLUTIONS.put(12, Solutions::d12_DigitalPlumber);
        SOLUTIONS.put(13, Solutions::d13_PacketScanners);
        SOLUTIONS.put(14, Solutions::d14_DiskDefragmentation);
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            throw new IllegalArgumentException("Usage: Solutions <day> <part> [--example|-x]");
        }
        final int day = Integer.parseInt(args[0]);
        final int part = Integer.parseInt(args[1]);
        final boolean example;
        if (args.length > 2) {
            if ("--example".equals(args[2]) || "-x".equals(args[2])) {
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

    public static long d08_IHeardYouLikeRegisters(List<String> lines, int part) {
        final Map<String, Integer> registerToValue = new HashMap<>();
        int valueMax = Integer.MIN_VALUE;
        for (String line : lines) {
            final String[] tokens = line.split(" ");
            if (!registerToValue.containsKey(tokens[4])) {
                registerToValue.put(tokens[4], 0);
            }
            final int conditionRegister = registerToValue.get(tokens[4]);
            final int conditionValue = Integer.parseInt(tokens[6]);
            if ((">".equals(tokens[5]) && conditionRegister <= conditionValue) ||
                    ("<".equals(tokens[5]) && conditionRegister >= conditionValue) ||
                    (">=".equals(tokens[5]) && conditionRegister < conditionValue) ||
                    ("<=".equals(tokens[5]) && conditionRegister > conditionValue) ||
                    ("==".equals(tokens[5]) && conditionRegister != conditionValue) ||
                    ("!=".equals(tokens[5]) && conditionRegister == conditionValue)) {
                continue;
            }
            if (!registerToValue.containsKey(tokens[0])) {
                registerToValue.put(tokens[0], 0);
            }
            final int operationValue = registerToValue.get(tokens[0]);
            final int offset = Integer.parseInt(tokens[2]);
            final int value;
            if ("inc".equals(tokens[1])) {
                value = operationValue + offset;
            } else if ("dec".equals(tokens[1])) {
                value = operationValue - offset;
            } else {
                throw new RuntimeException("Unknown operator: " + tokens[1]);
            }
            if (part == 2) {
                if (value > valueMax) {
                    valueMax = value;
                }
            }
            registerToValue.put(tokens[0], value);
        }
        if (part == 1) {
            for (String register : registerToValue.keySet()) {
                final int value = registerToValue.get(register);
                if (value > valueMax) {
                    valueMax = value;
                }
            }
        }
        return valueMax;
    }

    public static long d09_StreamProcessing(List<String> lines, int part) {
        final String input = lines.get(0);
        int score = 0;
        int cancelled = 0;
        int group = 0;
        boolean garbage = false;
        int i = 0;
        while (i < input.length()) {
            final char c = input.charAt(i);
            if (garbage) {
                if (c == '>') {
                    garbage = false;
                } else if (c == '!') {
                    i++;
                } else {
                    cancelled++;
                }
            } else {
                if (c == '{') {
                    group++;
                    score += group;
                } else if (c == '<') {
                    garbage = true;
                } else if (c == '}') {
                    group--;
                }
            }
            i++;
        }
        if (part == 1) {
            return score;
        }
        return cancelled;
    }

    public static long d10_KnotHash(List<String> lines, int part) {
        final String input = lines.get(0);
        if (part == 2) {
            System.out.println(CsUtil.knotHash(input.toCharArray()));
            return 0;
        }

        final int n = 256;
        final char[] hash = new char[n];
        for (int i = 0; i < n; i++) {
            hash[i] = (char) i;
        }
        final String[] tokens = input.split(",");
        final char[] message = new char[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            message[i] = (char) Integer.parseInt(tokens[i]);
        }

        int position = 0;
        int skip = 0;
        for (char length : message) {
            for (int d = 0; d < length / 2; d++) {
                final int i = (position + d) % n;
                final int j = (position + length - 1 - d + n) % n;
                final char t = hash[i];
                hash[i] = hash[j];
                hash[j] = t;
            }
            position = (position + length + skip) % n;
            skip++;
        }
        return (int) hash[0] * (int) hash[1];
    }

    private static int hexDistance(int i, int j) {
        if (j > 0) {
            if (i > 0) {
                return j + i;
            } else if (i < -j) {
                return j + (-j - i);
            }
            return j;
        } else if (j < 0) {
            if (i < 0) {
                return -j + (-i);
            } else if (i > -j) {
                return -j + (i - (-j));
            }
            return -j;
        }
        return Math.abs(i);
    }

    public static long d11_HexEd(List<String> lines, int part) {
        final String[] steps = lines.get(0).split(",");
        int i = 0;
        int j = 0;
        int distanceMax = 0;
        for (String step : steps) {
            if ("n".equals(step)) {
                i++;
            } else if ("s".equals(step)) {
                i--;
            } else if ("ne".equals(step)) {
                j++;
            } else if ("nw".equals(step)) {
                i++;
                j--;
            } else if ("se".equals(step)) {
                i--;
                j++;
            } else if ("sw".equals(step)) {
                j--;
            } else {
                throw new IllegalArgumentException("Unrecognized step: " + step);
            }
            final int distance = hexDistance(i, j);
            if (distance > distanceMax) {
                distanceMax = distance;
            }
        }
        if (part == 1) {
            return hexDistance(i, j);
        }
        return distanceMax;
    }

    public static long d12_DigitalPlumber(List<String> lines, int part) {
        final Map<String, Set<String>> programToOthers = new HashMap<>();
        for (String line : lines) {
            final String[] programOthers = line.split(" <-> ");
            final String program = programOthers[0];
            final Set<String> others = new HashSet<>();
            for (String other : programOthers[1].split(", ")) {
                others.add(other);
            }
            programToOthers.put(program, others);
        }

        final Deque<String> queue = new LinkedList<>();
        final Set<String> visited = new HashSet<>();
        int groups = 0;
        for (String program : programToOthers.keySet()) {
            if (visited.contains(program)) {
                continue;
            }
            queue.push(program);
            boolean hasZero = false;
            int size = 0;
            while (!queue.isEmpty()) {
                final String programQueue = queue.poll();
                visited.add(programQueue);
                if ("0".equals(programQueue)) {
                    hasZero = true;
                }
                for (String other : programToOthers.get(programQueue)) {
                    if (!visited.contains(other)) {
                        queue.push(other);
                    }
                }
                size++;
            }
            if (part == 1 && hasZero) {
                return size;
            }
            groups++;
        }
        return groups;
    }

    public static long d13_PacketScanners(List<String> lines, int part) {
        final Map<Integer, Integer> depthToRange = new HashMap<>();
        int depthMax = -1;
        for (String line : lines) {
            final String[] depthRange = line.split(": ");
            final int depth = Integer.parseInt(depthRange[0]);
            final int range = Integer.parseInt(depthRange[1]);
            depthToRange.put(depth, range);
            if (depthMax == -1 || depth > depthMax) {
                depthMax = depth;
            }
        }
        final Map<Integer, Integer> depthToPosition = new HashMap<>();
        final Map<Integer, Boolean> depthToDown = new HashMap<>();
        for (int depth : depthToRange.keySet()) {
            depthToPosition.put(depth, 0);
            depthToDown.put(depth, true);
        }

        int severity = 0;
        final LinkedList<Integer> packets = new LinkedList<>(); // `Deque` interface doesn't have `listIterator()`.
        int delay = 0;
        packets.offer(0);
        while (packets.getFirst() <= depthMax) {
            // Check if packets are caught.
            final ListIterator<Integer> iterator = packets.listIterator();
            while (iterator.hasNext()) {
                final int packet = iterator.next();
                if (depthToPosition.containsKey(packet) && depthToPosition.get(packet) == 0) {
                    if (part == 1) {
                        severity += packet * depthToRange.get(packet);
                        iterator.set(packet + 1);
                    } else {
                        iterator.remove();
                    }
                } else {
                    iterator.set(packet + 1);
                }
            }
            // Move scanners.
            for (int depth : depthToPosition.keySet()) {
                int position = depthToPosition.get(depth);
                if (depthToDown.get(depth)) {
                    position++;
                    if (position == depthToRange.get(depth) - 1) {
                        depthToDown.put(depth, false);
                    }
                } else {
                    position--;
                    if (position == 0) {
                        depthToDown.put(depth, true);
                    }
                }
                depthToPosition.put(depth, position);
            }
            if (part != 1) {
                packets.offer(0);
            }
            delay++;
        }
        if (part == 1) {
            return severity;
        }
        return delay - depthMax - 1;
    }

    public static long d14_DiskDefragmentation(List<String> lines, int part) {
        final String input = lines.get(0);
        final int m = 128;
        final int n = 128;
        final char[][] grid = new char[m][n];
        for (int i = 0; i < m; i++) {
            final char[] message = (input + "-" + i).toCharArray();
            final byte[] hash = CsUtil.knotHashBytes(message);
            for (int j = 0; j < 16; j++) {
                final byte b = hash[j];
                for (int k = 0; k < 8; k++) {
                    grid[i][8 * j + 7 - k] = ((b >> k) & 0x01) == 0x01 ? '#' : '.';
                }
            }
        }

        if (part == 1) {
            int used = 0;
            for (int i = 0; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    if (grid[i][j] == '#') {
                        used++;
                    }
                }
            }
            return used;
        }

        int regions = 0;
        final Set<CsUtil.IntPair> visited = new HashSet<>();
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] != '#') {
                    continue;
                }
                final CsUtil.IntPair square = new CsUtil.IntPair(i, j);
                if (visited.contains(square)) {
                    continue;
                }
                final Deque<CsUtil.IntPair> queue = new LinkedList<>();
                queue.offer(square);
                while (!queue.isEmpty()) {
                    final CsUtil.IntPair qSquare = queue.poll();
                    visited.add(qSquare);
                    final int qi = qSquare.a;
                    final int qj = qSquare.b;
                    final int[][] neighbors = {{qi - 1, qj}, {qi, qj + 1}, {qi + 1, qj}, {qi, qj - 1}};
                    for (int[] neighbor : neighbors) {
                        final int ni = neighbor[0];
                        final int nj = neighbor[1];
                        final CsUtil.IntPair nSquare = new CsUtil.IntPair(ni, nj);
                        if (ni >= 0 && ni < m && nj >= 0 && nj < n &&
                                grid[ni][nj] == '#' && !visited.contains(nSquare)) {
                            queue.offer(nSquare);
                        }
                    }
                }
                regions++;
            }
        }
        return regions;
    }
}
