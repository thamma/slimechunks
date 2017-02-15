public class Random {

    private static long RNGSEED;

    private static int next(int bits) {
        RNGSEED = (RNGSEED * 0x5DEECE66DL + 0xBL) & ((1L << 48) - 1);
        return (int) (RNGSEED >>> (48 - bits));
    }

    private static  int nextInt(long seed, int n) {
        RNGSEED = (seed ^ 0x5DEECE66DL) & ((1L << 48) - 1);
        if (n <= 0)
            throw new IllegalArgumentException("n must be positive");
        if ((n & -n) == n) // i.e., n is a power of 2
            return (int) ((n * (long) next(31)) >> 31);
        int bits, val;
        do {
            bits = next(31);
            val = bits % n;
        }
        while (bits - val + (n - 1) < 0);
        return val;
    }

}
