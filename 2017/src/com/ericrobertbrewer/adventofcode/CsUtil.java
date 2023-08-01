package com.ericrobertbrewer.adventofcode;

public final class CsUtil {

    public static class IntPair {

        public final int a;
        public final int b;

        public IntPair(int a, int b) {
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

    public static class IntTriple extends IntPair {

        public final int c;

        public IntTriple(int a, int b, int c) {
            super(a, b);
            this.c = c;
        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + c;
            return result;
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof IntTriple)) {
                return false;
            }
            final IntTriple otherTriple = (IntTriple) other;
            return otherTriple.a == a && otherTriple.b == b && otherTriple.c == c;
        }
    }

    public static byte[] knotHashBytes(char[] message) {
        final int z = message.length + 5;
        final char[] payload = new char[z];
        for (int i = 0; i < message.length; i++) {
            payload[i] = message[i];
        }
        payload[z - 5] = 17;
        payload[z - 4] = 31;
        payload[z - 3] = 73;
        payload[z - 2] = 47;
        payload[z - 1] = 23;

        final int n = 256;
        final char[] hash = new char[n];
        for (int i = 0; i < n; i++) {
            hash[i] = (char) i;
        }

        // Sparse hash.
        int position = 0;
        int skip = 0;
        for (int round = 0; round < 64; round++) {
            for (char length : payload) {
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
        }

        // Dense hash.
        final byte[] dense = new byte[16];
        for (int i = 0; i < dense.length; i++) {
            byte value = (byte) hash[16 * i];
            for (int j = 1; j < 16; j++) {
                value ^= (byte) hash[16 * i + j];
            }
            dense[i] = value;
        }
        return dense;
    }

    public static String knotHash(char[] message) {
        final byte[] dense = knotHashBytes(message);
        final StringBuilder knot = new StringBuilder();
        for (byte b : dense) {
            knot.append(String.format("%02x", b));
        }
        return knot.toString();
    }

    private CsUtil() {
    }
}