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

    private static final Map<Integer, Solution> SOLUTIONS = new HashMap<>();
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
        SOLUTIONS.put(15, Solutions::d15_DuelingGenerators);
        SOLUTIONS.put(16, Solutions::d16_PermutationPromenade);
        SOLUTIONS.put(17, Solutions::d17_Spinlock);
        SOLUTIONS.put(18, Solutions::d18_Duet);
        SOLUTIONS.put(19, Solutions::d19_ASeriesOfTubes);
        SOLUTIONS.put(20, Solutions::d20_ParticleSwarm);
        SOLUTIONS.put(21, Solutions::d21_FractalArt);
        SOLUTIONS.put(22, Solutions::d22_SporificaVirus);
        SOLUTIONS.put(23, Solutions::d23_CoprocessorConflagration);
        SOLUTIONS.put(24, Solutions::d24_ElectromagneticMoat);
        SOLUTIONS.put(25, Solutions::d25_TheHaltingProblem);
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

    public static long d01_InverseCaptcha(List<String> lines, int part) {
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

    public static long d02_CorruptionChecksum(List<String> lines, int part) {
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

    public static long d15_DuelingGenerators(List<String> lines, int part) {
        int a = -1;
        int b = -1;
        for (String line : lines) {
            final String[] tokens = line.split(" ");
            final int value = Integer.parseInt(tokens[4]);
            if ("A".equals(tokens[1])) {
                a = value;
            } else if ("B".equals(tokens[1])) {
                b = value;
            } else {
                throw new IllegalArgumentException("Unexpected generator name: " + tokens[1]);
            }
        }

        final long fa = 16807;
        final long fb = 48271;
        final int x = 2147483647;
        final int rounds = part == 1 ? 40000000 : 5000000;
        int round = 0;
        int count = 0;
        while (round < rounds) {
            do {
                a = (int) ((fa * (long) a) % x);
            } while (part == 2 && a % 4 != 0);
            do {
                b = (int) ((fb * (long) b) % x);
            } while (part == 2 && b % 8 != 0);
            final int a16 = a & 0xffff;
            final int b16 = b & 0xffff;
            if (a16 == b16) {
                count++;
            }
            round++;
        }
        return count;
    }

    public static long d16_PermutationPromenade(List<String> lines, int part) {
        final int n = 'p' - 'a' + 1;
        final char[] order = new char[n];
        final Map<Character, Integer> cToPosition = new HashMap<>();
        for (int i = 0; i < n; i++) {
            order[i] = (char) ('a' + i);
            cToPosition.put(order[i], i);
        }

        final Map<String, Integer> orderToDance = new HashMap<>();
        boolean skipped = false;
        final int dances = part == 1 ? 1 : 1000000000;
        int dance = 0;
        final String[] moves = lines.get(0).split(",");
        while (dance < dances) {
            if (!skipped) {
                final String s = new String(order);
                if (orderToDance.containsKey(s)) {
                    final int danceSince = orderToDance.get(s);
                    final int danceInterval = dance - danceSince;
                    final int repeat = (dances - danceSince) / danceInterval - 1;
                    dance += repeat * danceInterval;
                    skipped = true;
                } else {
                    orderToDance.put(s, dance);
                }
            }
            for (String move : moves) {
                final char c = move.charAt(0);
                if (c == 's') {
                    final int x = Integer.parseInt(move.substring(1)) % n;
                    final char[] orderNext = new char[n];
                    for (int i = 0; i < n; i++) {
                        orderNext[i] = order[(i + n - x) % n];
                    }
                    for (int i = 0; i < n; i++) {
                        order[i] = orderNext[i];
                        cToPosition.put(order[i], i);
                    }
                } else if (c == 'x' || c == 'p') {
                    final int a, b;
                    if (c == 'x') {
                        final int slash = move.indexOf('/');
                        a = Integer.parseInt(move.substring(1, slash));
                        b = Integer.parseInt(move.substring(slash + 1));
                    } else {
                        a = cToPosition.get(move.charAt(1));
                        b = cToPosition.get(move.charAt(3));
                    }
                    char t = order[a];
                    order[a] = order[b];
                    order[b] = t;
                    cToPosition.put(order[a], a);
                    cToPosition.put(order[b], b);
                } else {
                    throw new IllegalArgumentException("Unexpected move: " + move);
                }
            }
            dance++;
        }
        System.out.println(order);
        return 0;
    }

    public static long d17_Spinlock(List<String> lines, int part) {
        final int steps = Integer.parseInt(lines.get(0));

        if (part == 1) {
            final LinkedList<Integer> buffer = new LinkedList<>();
            ListIterator<Integer> iterator = buffer.listIterator();
            iterator.add(0);
            while (buffer.size() <= 2017) {
                for (int i = 0; i < steps % buffer.size(); i++) {
                    if (iterator.hasNext()) {
                        iterator.next();
                    } else {
                        iterator = buffer.listIterator();
                        iterator.next();
                    }
                }
                iterator.add(buffer.size());
            }
            return iterator.hasNext() ? iterator.next() : buffer.getFirst();
        }

        int position = 0;
        int size = 1;
        int after0 = -1;
        while (size <= 50000000) {
            position = (position + steps) % size;
            if (position == 0) {
                after0 = size;
            }
            position++;
            size++;
        }
        return after0;
    }

    public static long d18_Duet(List<String> lines, int part) {
        final List<String[]> instructions = new ArrayList<>();
        final List<Map<Character, Long>> registersToValue = new ArrayList<>();
        int[] i = new int[2];
        final List<Deque<Long>> queues = new ArrayList<>();
        boolean[] waiting = new boolean[2];
        for (int p = 0; p < 2; p++) {
            registersToValue.add(new HashMap<>());
            i[p] = 0;
            queues.add(new LinkedList<>());
            waiting[p] = false;
        }
        for (String line : lines) {
            final String[] instruction = line.split(" ");
            for (int j = 1; j < instruction.length; j++) {
                final String s = instruction[j];
                if (s.length() == 1 && s.charAt(0) >= 'a' && s.charAt(0) <= 'z') {
                    registersToValue.get(0).put(s.charAt(0), 0L);
                    registersToValue.get(1).put(s.charAt(0), 0L);
                }
            }
            instructions.add(instruction);
        }
        registersToValue.get(1).put('p', 1L); // Program ID.

        long answer = 0;
        int p = 0;
        while (part == 1 || !waiting[0] || !waiting[1]) {
            final Map<Character, Long> registerToValue = registersToValue.get(p);
            while (i[p] >= 0 && i[p] < instructions.size()) {
                final String[] instruction = instructions.get(i[p]);
                final String operation = instruction[0];
                final String xs = instruction[1];
                final char xc = xs.charAt(0);
                final long x = getRegisterOrLiteralValue(xs, registerToValue);
                final String ys = instruction.length > 2 ? instruction[2] : null;
                if ("snd".equals(operation)) {
                    if (part == 1) {
                        answer = x;
                    } else {
                        queues.get(p == 0 ? 1 : 0).offer(x);
                        waiting[p == 0 ? 1 : 0] = false;
                        if (p == 1) {
                            answer++;
                        }
                    }
                } else if ("set".equals(operation)) {
                    final long y = getRegisterOrLiteralValue(ys, registerToValue);
                    registerToValue.put(xc, y);
                } else if ("add".equals(operation)) {
                    final long y = getRegisterOrLiteralValue(ys, registerToValue);
                    registerToValue.put(xc, x + y);
                } else if ("mul".equals(operation)) {
                    final long y = getRegisterOrLiteralValue(ys, registerToValue);
                    registerToValue.put(xc, x * y);
                } else if ("mod".equals(operation)) {
                    final long y = getRegisterOrLiteralValue(ys, registerToValue);
                    registerToValue.put(xc, x % y);
                } else if ("rcv".equals(operation)) {
                    if (part == 1) {
                        if (x != 0 && answer != 0) {
                            break;
                        }
                    } else if (!queues.get(p).isEmpty()) {
                        registerToValue.put(xc, queues.get(p).poll());
                    } else {
                        waiting[p] = true;
                        break;
                    }
                } else if ("jgz".equals(operation)) {
                    if (x > 0) {
                        final long y = getRegisterOrLiteralValue(ys, registerToValue);
                        i[p] += y;
                        continue;
                    }
                } else {
                    throw new IllegalArgumentException("Unexpected operation: " + operation);
                }
                i[p]++;
            }
            if (part == 1) {
                break;
            }
            if (i[p] < 0 || i[p] >= instructions.size()) {
                break;
            }
            p = p == 0 ? 1 : 0;
        }
        return answer;
    }

    private static long getRegisterOrLiteralValue(String ys, Map<Character, Long> registerToValue) {
        if (ys.length() == 1 && ys.charAt(0) >= 'a' && ys.charAt(0) <= 'z') {
            return registerToValue.get(ys.charAt(0));
        }
        return Long.parseLong(ys);
    }

    public static long d19_ASeriesOfTubes(List<String> lines, int part) {
        int i = 0;
        int j = -1;
        for (int k = 0; k < lines.get(0).length(); k++) {
            final char c = lines.get(0).charAt(k);
            if (c != ' ') {
                if (c != '|') {
                    throw new IllegalArgumentException("Unexpected character in first line: " + c);
                }
                if (j != -1) {
                    throw new IllegalArgumentException("Multiple start characters found at indices: " + j + ", " + k);
                }
                j = k;
            }
        }
        final int[][] deltas = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};
        final char[] pathChars = {'|', '-'};
        int direction = 2; // Down.
        String answer = "";
        int steps = 1;
        while (true) {
            final int[] delta = deltas[direction];
            final int iNext = i + delta[0];
            final int jNext = j + delta[1];
            final char c = lines.get(iNext).charAt(jNext);
            if (c == '+') {
                int directionNext = -1;
                int axisOther = (direction + 1) % 2;
                for (int directionOther : new int[]{axisOther, 2 + axisOther}) {
                    final int[] deltaOther = deltas[directionOther];
                    int iNextNext = iNext + deltaOther[0];
                    int jNextNext = jNext + deltaOther[1];
                    if (iNextNext < 0 || iNextNext >= lines.size() || jNextNext < 0 || jNextNext >= lines.get(iNextNext).length()) {
                        continue;
                    }
                    final char cNextNext = lines.get(iNextNext).charAt(jNextNext);
                    if (cNextNext != ' ') {
                        if (directionNext != -1) {
                            throw new IllegalArgumentException("Unexpected fork: (" + iNext + ", " + jNext + ")");
                        }
                        i = iNextNext;
                        j = jNextNext;
                        directionNext = directionOther;
                        if (cNextNext != pathChars[directionNext % 2]) {
                            answer += cNextNext;
                        }
                    }
                }
                if (directionNext == -1) {
                    break;
                }
                direction = directionNext;
                steps += 2;
            } else if (c == pathChars[direction % 2]) {
                i = iNext;
                j = jNext;
                steps++;
            } else if (c == pathChars[(direction + 1) % 2]) {
                // Cross.
                i = iNext;
                j = jNext;
                steps++;
            } else if (c == ' ') {
                break;
            } else {
                i = iNext;
                j = jNext;
                answer += c;
                steps++;
            }
        }
        System.out.println(answer);
        return steps;
    }

    public static long d20_ParticleSwarm(List<String> lines, int part) {
        final int[][][] particles = new int[lines.size()][3][3];
        final String names = "pva";
        final int P = 0, V = 1, A = 2;
        final int X = 0, Y = 1, Z = 2;
        for (int i = 0; i < lines.size(); i++) {
            final String[] pva = lines.get(i).split(", ");
            if (pva.length != 3) {
                throw new IllegalArgumentException("Unexpected line: " + lines.get(i));
            }
            for (int j = 0; j < pva.length; j++) {
                final String xyzS = pva[j];
                if (!xyzS.startsWith("" + names.charAt(j) + "=<") || !xyzS.endsWith(">")) {
                    throw new IllegalArgumentException("Unexpected point description on line " + i + ": " + xyzS);
                }
                final String[] vector = xyzS.substring(3, xyzS.length() - 1).split(",");
                if (vector.length != 3) {
                    throw new IllegalArgumentException("Unexpected vector on line " + i + ": " + xyzS);
                }
                for (int k = 0; k < vector.length; k++) {
                    particles[i][j][k] = Integer.parseInt(vector[k]);
                }
            }
        }

        if (part == 1) {
            // Find the minimum Manhattan acceleration; minimum sum of zero-acceleration velocities break ties.
            int aMin = -1;
            int iMin = -1;
            for (int i = 0; i < particles.length; i++) {
                final int[][] particle = particles[i];
                final int ax = particle[A][X], ay = particle[A][Y], az = particle[A][Z];
                final int a = Math.abs(ax) + Math.abs(ay) + Math.abs(az);
                if (aMin == -1 || a < aMin) {
                    aMin = a;
                    iMin = i;
                } else if (a == aMin) {
                    final int[][] particleMin = particles[iMin];
                    int dMin = 0;
                    int d = 0;
                    for (int k = 0; k < 3; k++) {
                        if (particleMin[A][k] == 0) {
                            dMin += Math.abs(particleMin[V][k]);
                        }
                        if (particle[A][k] == 0) {
                            d += Math.abs(particle[V][k]);
                        }
                    }
                    if (d < dMin) {
                        aMin = a;
                        iMin = i;
                    }
                }
            }
            return iMin;
        }

        final Set<Integer> ids = new HashSet<>();
        for (int i = 0; i < particles.length; i++) {
            ids.add(i);
        }
        int patience = 0; // Not deterministic; this is only a heuristic.
        while (patience < 20) {
            final Map<CsUtil.IntTriple, Set<Integer>> pointToIds = new HashMap<>();
            for (int id : ids) {
                final int[][] particle = particles[id];
                for (int k = 0; k < 3; k++) {
                    particle[V][k] += particle[A][k];
                    particle[P][k] += particle[V][k];
                }
                final CsUtil.IntTriple point = new CsUtil.IntTriple(particle[P][X], particle[P][Y], particle[P][Z]);
                if (!pointToIds.containsKey(point)) {
                    pointToIds.put(point, new HashSet<Integer>());
                }
                pointToIds.get(point).add(id);
            }
            boolean patient = true;
            for (CsUtil.IntTriple point : pointToIds.keySet()) {
                if (pointToIds.get(point).size() > 1) {
                    for (int id : pointToIds.get(point)) {
                        ids.remove(id);
                    }
                    patient = false;
                }
            }
            if (patient) {
                patience++;
            } else {
                patience = 0;
            }
        }
        return ids.size();
    }

    public static long d21_FractalArt(List<String> lines, int part) {
        final Map<String, String[]> squareToResult = new HashMap<>();
        for (String line : lines) {
            final String[] squareResult = line.split(" => ");
            final String[] result = squareResult[1].split("/");
            final String[] square = squareResult[0].split("/");
            final int n = square.length;
            final int[][] loops = {
                    {    0,  n,  1,     0,  n,  1}, // No rotation.
                    {n - 1, -1, -1,     0,  n,  1}, // Rotate 90 degrees clockwise.
                    {n - 1, -1, -1, n - 1, -1, -1}, // Rotate 180 degrees.
                    {    0,  n,  1, n - 1, -1, -1}  // Rotate 270 degress.
            };
            for (int k = 0; k < loops.length; k++) {
                final int[] loop = loops[k];
                final StringBuilder rotation = new StringBuilder();
                for (int i = loop[0]; i != loop[1]; i += loop[2]) {
                    for (int j = loop[3]; j != loop[4]; j += loop[5]) {
                        rotation.append(square[i].charAt(j));
                    }
                    rotation.append('/');
                }
                final StringBuilder flip = new StringBuilder();
                for (int j = loop[3]; j != loop[4]; j += loop[5]) {
                    for (int i = loop[0]; i != loop[1]; i += loop[2]) {
                        flip.append(square[i].charAt(j));
                    }
                    flip.append('/');
                }
                squareToResult.put(rotation.toString(), result);
                squareToResult.put(flip.toString(), result);
            }
        }

        char[][] grid = {
                {'.' , '#', '.'},
                {'.' , '.', '#'},
                {'#' , '#', '#'}
        };
        for (int iterations = 0; iterations < (part == 1 ? 5 : 18); iterations++) {
            final int v;
            final int m;
            if (grid.length % 2 == 0) {
                v = 2;
                m = 3;
            } else {
                v = 3;
                m = 4;
            }
            final int n = grid.length / v * m;
            final char[][] gridNext = new char[n][n];
            for (int i = 0; i < grid.length; i += v) {
                for (int j = 0; j < grid[i].length; j += v) {
                    final StringBuilder s = new StringBuilder();
                    for (int di = i; di < i + v; di++) {
                        for (int dj = j; dj < j + v; dj++) {
                            s.append(grid[di][dj]);
                        }
                        s.append('/');
                    }
                    final String[] result = squareToResult.get(s.toString());
                    for (int ri = 0; ri < m; ri++) {
                        for (int rj = 0; rj < m; rj++) {
                            gridNext[i / v * m + ri][j / v * m + rj] = result[ri].charAt(rj);
                        }
                    }
                }
            }
            grid = gridNext;
        }

        int on = 0;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] == '#') {
                    on++;
                }
            }
        }
        return on;
    }

    public static long d22_SporificaVirus(List<String> lines, int part) {
        final int STATE_CLEAN = 0;
        final int STATE_WEAKENED = 1;
        final int STATE_INFECTED = 2;
        final int STATE_FLAGGED = 3;
        final Map<CsUtil.IntPair, Integer> pointToState = new HashMap<>();
        for (int i = 0; i < lines.size(); i++) {
            final String line = lines.get(i);
            for (int j = 0; j < line.length(); j++) {
                pointToState.put(new CsUtil.IntPair(i, j), line.charAt(j) == '#' ? STATE_INFECTED : STATE_CLEAN);
            }
        }
        final int[][] deltas = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};
        int direction = 0;
        int i = lines.size() / 2;
        int j = lines.get(i).length() / 2;
        int count = 0;
        for (int burst = 0; burst < (part == 1 ? 10000 : 10000000); burst++) {
            final CsUtil.IntPair point = new CsUtil.IntPair(i, j);
            if (!pointToState.containsKey(point) || pointToState.get(point) == STATE_CLEAN) {
                direction = (direction + 3) % 4; // Left.
                if (part == 1) {
                    pointToState.put(point, STATE_INFECTED);
                    count++;
                } else {
                    pointToState.put(point, STATE_WEAKENED);
                }
            } else if (pointToState.get(point) == STATE_WEAKENED) {
                // Direction stays straight.
                pointToState.put(point, STATE_INFECTED);
                count++;
            } else if (pointToState.get(point) == STATE_INFECTED) {
                direction = (direction + 1) % 4; // Right.
                if (part == 1) {
                    pointToState.put(point, STATE_CLEAN);
                } else {
                    pointToState.put(point, STATE_FLAGGED);
                }
            } else {
                direction = (direction + 2) % 4; // Backwards.
                pointToState.put(point, STATE_CLEAN);
            }
            final int[] delta = deltas[direction];
            i += delta[0];
            j += delta[1];
        }
        return count;
    }

    public static long d23_CoprocessorConflagration(List<String> lines, int part) {
        final List<String[]> instructions = new ArrayList<>();
        for (String line : lines) {
            instructions.add(line.split(" "));
        }

        // Initialize registers.
        final Map<Character, Long> r = new HashMap<>();
        for (String[] instruction : instructions) {
            for (int j = 1; j < 3; j++) {
                if (instruction[j].length() == 1) {
                    final char c = instruction[j].charAt(0);
                    if (c >= 'a' && c <= 'z') {
                        if (!r.containsKey(c)) {
                            r.put(c, 0L);
                        }
                    }
                }
            }
        }

        final int loopIpStart;
        final long loopStep;
        if (part == 1) {
            loopIpStart = -1;
            loopStep = 0L;
        } else {
            r.put('a', 1L);

            // For part 2, I just wrote out the program (input) as pseudocode,
            // then determined with my eyeballs that the program calculates the
            // number of composite numbers inclusively between `b` and `c` with
            // an interval given on the second-to-last line.
            final String[] instructionLoopIpStart = instructions.get(instructions.size() - 1);
            if (!"jnz".equals(instructionLoopIpStart[0]) || !"1".equals(instructionLoopIpStart[1])) {
                throw new RuntimeException("Unsupported program structure (loop ip start): " + lines.get(lines.size() - 1));
            }
            loopIpStart = instructions.size() - 1 + Integer.parseInt(instructionLoopIpStart[2]);
            final String[] instructionLoopStep = instructions.get(instructions.size() - 2);
            if (!"sub".equals(instructionLoopStep[0]) || !"b".equals(instructionLoopStep[1])) {
                throw new RuntimeException("Unsupported program structure (loop step): " + lines.get(lines.size() - 2));
            }
            loopStep = Long.parseLong(instructionLoopStep[2]);
        }

        // Run.
        int ip = 0;
        long muls = 0L;
        while (ip >= 0 && ip < instructions.size()) {
            final String[] instruction = instructions.get(ip);
            final String operation = instruction[0];
            final String xs = instruction[1];
            final char xc = xs.charAt(0);
            final long x = getRegisterOrLiteralValue(xs, r);
            final String ys = instruction[2];
            final long y = getRegisterOrLiteralValue(ys, r);
            boolean jump = false;
            if ("set".equals(operation)) {
                r.put(xc, y);
            } else if ("sub".equals(operation)) {
                r.put(xc, x - y);
            } else if ("mul".equals(operation)) {
                r.put(xc, x * y);
                muls++;
            } else if ("jnz".equals(operation)) {
                if (x != 0) {
                    ip += (int) y;
                    jump = true;
                }
            } else {
                throw new IllegalArgumentException("Unknown operation: " + operation);
            }
            if (!jump) {
                ip++;
            }

            // Return number of composite numbers in range (and interval) of main loop.
            if (part == 2 && ip == loopIpStart) {
                final boolean[] sieve = getPrimeSieve(r.get('c').intValue());
                int h = 0;
                for (long i = r.get('b'); i <= r.get('c'); i -= loopStep) {
                    if (sieve[(int) i]) {
                        h++;
                    }
                }
                return h;
            }
        }

        return muls;
    }

    private static boolean[] getPrimeSieve(int x) {
        final boolean[] sieve = new boolean[x + 1];
        for (int i = 0; i < sieve.length; i++) {
            sieve[i] = false;
        }
        int i = 2;
        while (i < Math.sqrt(sieve.length)) {
            for (int j = i * i; j < sieve.length; j += i) {
                sieve[j] = true;
            }
            i++;
            while (i < Math.sqrt(sieve.length) && sieve[i]) {
                i++;
            }
        }
        return sieve;
    }

    public static long d24_ElectromagneticMoat(List<String> lines, int part) {
        final Map<Integer, List<Integer>> pinToIndices = new HashMap<>();
        final int[][] components = new int[lines.size()][2];
        boolean[] visited = new boolean[components.length];
        for (int i = 0; i < lines.size(); i++) {
            final String line = lines.get(i);
            final String[] pins = line.split("/");
            for (int j = 0; j < 2; j++) {
                final int pin = Integer.parseInt(pins[j]);
                if (!pinToIndices.containsKey(pin)) {
                    pinToIndices.put(pin, new ArrayList<>());
                }
                pinToIndices.get(pin).add(i);
                components[i][j] = pin;
            }
            visited[i] = false;
        }

        int strengthMax = 0;
        int lengthLongest = 0;
        int strengthMaxLongest = 0;
        final List<List<Integer>> bridges = getBridges(0, pinToIndices, components, visited);
        for (List<Integer> bridge : bridges) {
            int strength = 0;
            for (int index : bridge) {
                strength += components[index][0] + components[index][1];
            }
            if (strength > strengthMax) {
                strengthMax = strength;
            }
            if (bridge.size() > lengthLongest || (bridge.size() == lengthLongest && strength > strengthMaxLongest)) {
                lengthLongest = bridge.size();
                strengthMaxLongest = strength;
            }
        }
        if (part == 1) {
            return strengthMax;
        }
        return strengthMaxLongest;
    }

    private static List<List<Integer>> getBridges(
            int pin,
            Map<Integer, List<Integer>> pinToIndices,
            int[][] components,
            boolean[] visited
    ) {
        final List<List<Integer>> bridges = new ArrayList<>();
        if (pinToIndices.containsKey(pin)) {
            for (int index : pinToIndices.get(pin)) {
                if (visited[index]) {
                    continue;
                }
                visited[index] = true;
                final int[] component = components[index];
                final int pinNext = component[0] == pin ? component[1] : component[0];
                final List<List<Integer>> subBridges = getBridges(pinNext, pinToIndices, components, visited);
                if (subBridges.size() == 0) {
                    final List<Integer> bridge = new ArrayList<>();
                    bridge.add(index);
                    bridges.add(bridge);
                } else {
                    for (List<Integer> subBridge : subBridges) {
                        final List<Integer> bridge = new ArrayList<>();
                        bridge.add(index);
                        for (int subIndex : subBridge) {
                            bridge.add(subIndex);
                        }
                        bridges.add(bridge);
                    }
                }
                visited[index] = false;
            }
        }
        return bridges;
    }

    public static long d25_TheHaltingProblem(List<String> lines, int part) {
        final Map<Character, Map<Integer, Rule>> stateToValueToRule = new HashMap<>();
        for (int stateIndex = 0; stateIndex < (lines.size() - 2) / 10; stateIndex++) {
            final char stateIn = lines.get(10 * stateIndex + 3).split(" ")[2].charAt(0);
            stateToValueToRule.put(stateIn, new HashMap<>());
            for (int i = 0; i < 2; i++) {
                final int valueIf = lines.get(10 * stateIndex + 4 * i + 4).trim().split(" ")[5].charAt(0) - '0';
                final int value = lines.get(10 * stateIndex + 4 * i + 5).trim().split(" ")[4].charAt(0) - '0';
                final String moveLine = lines.get(10 * stateIndex + 4 * i + 6).trim();
                final String move = moveLine.substring(moveLine.lastIndexOf(' ') + 1, moveLine.lastIndexOf('.'));
                if (!"right".equals(move) && !"left".equals(move)) {
                    throw new IllegalArgumentException("Unexpected move: " + move);
                }
                final char state = lines.get(10 * stateIndex + 4 * i + 7).trim().split(" ")[4].charAt(0);
                stateToValueToRule.get(stateIn).put(valueIf, new Rule(value, move, state));
            }
        }

        char state = lines.get(0).split(" ")[3].charAt(0);
        final long check = Long.parseLong(lines.get(1).split(" ")[5]);
        final Map<Integer, Integer> tape = new HashMap<>();
        int i = 0;
        int step = 0;
        while (step < check) {
            if (!tape.containsKey(i)) {
                tape.put(i, 0);
            }
            final Rule rule = stateToValueToRule.get(state).get(tape.get(i));
            tape.put(i, rule.value);
            if ("right".equals(rule.move)) {
                i++;
            } else {
                i--;
            }
            state = rule.state;
            step++;
        }
        int sum = 0;
        for (int j : tape.keySet()) {
            if (tape.get(j) == 1) {
                sum++;
            }
        }
        return sum;
    }

    private static final class Rule {

        final int value;
        final String move;
        final char state;

        Rule(int value, String move, char state) {
            this.value = value;
            this.move = move;
            this.state = state;
        }
    }
}
