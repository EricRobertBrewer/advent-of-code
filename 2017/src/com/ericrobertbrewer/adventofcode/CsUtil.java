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

    private CsUtil() {
    }
}