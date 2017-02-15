public class Slimes {

    interface Handler {
        void handle(int val, int x, int z);
    }

    public static void main(String... args) {
        long start = System.currentTimeMillis();
        int oldmax = 0;
        int range = 300;
        for (long i = 0; i < Long.MAX_VALUE; i++) {
            int minX = -range;
            int minZ = -range;
            int width = 2 * range;
            int height = 2 * range;
            final int[] max = new int[3];
            Handler maxHandler = (val, x, z) -> {
                if (val > max[0]) {
                    max[0] = val;
                    max[1] = x;
                    max[2] = z;
                }
            };
            maxRect(i, minX, minZ, width, height, 10, maxHandler);
            boolean update = oldmax < max[0];
            if (update) {
                oldmax = max[0];
                System.out.printf("%sseed: %d   max: %d   at (%d, %d)\n", update ? "\t" : "", i, max[0], max[1] * 16, max[2] * 16);
            }
        }
        System.out.println(System.currentTimeMillis() - start);
    }

    public static void maxRect(long seed, int minX, int minZ, int width, int height, int threshold, Handler handler) {
        for (int z = 0; z < height; z++) {
            for (int x = 0; x < width; x++) {
                if (!(isSC(seed, minX + x, minZ + z)))
                    continue;
                int xsize = 1;
                //determining the width of the histogram
                while (isSC(seed, minX + x + xsize, minZ + z))
                    xsize++;
                //maximum rect in histogram
                int best = 0;
                int currentZ = Integer.MAX_VALUE;
                for (int i = 0; i < xsize; i++) {
                    int zSize = 1;
                    while (zSize < currentZ && isSC(seed, minX + x + i, minZ + z + zSize))
                        zSize++;
                    currentZ = zSize;
                    int area = (i + 1) * currentZ;
                    if (area > best)
                        best = area;
                }
                if (best >= threshold)
                    handler.handle(best, minX + x, minZ + z);
            }
        }
    }


    // RNG hereon
    private static int RNGnextInt(long seed, int n) {
        long RNGSEED = (seed ^ 0x5DEECE66DL) & ((1L << 48) - 1);
        int bits, val;
        do {
            RNGSEED = (RNGSEED * 0x5DEECE66DL + 0xBL) & ((1L << 48) - 1);
            bits = (int) (RNGSEED >>> 17);
            val = bits % n;
        }
        while (bits - val + (n - 1) < 0);
        return val;
    }

    public static boolean isSC(long seed, int x, int z) {
        long RNGSEED = (seed +
                (long) (x * x * 0x4c1906) +
                (long) (x * 0x5ac0db) +
                (long) (z * z) * 0x4307a7L +
                (long) (z * 0x5f24f) ^ 0x3ad8025f ^ 0x5DEECE66DL) & ((1L << 48) - 1);
        int bits, val;
        do {
            RNGSEED = (RNGSEED * 0x5DEECE66DL + 0xBL) & ((1L << 48) - 1);
            bits = (int) (RNGSEED >>> 17);
            val = bits % 10;
        }
        while (bits - val + (10 - 1) < 0);
        return val == 0;
    }

    public static boolean isSCold(long seed, int x, int z) {
        return RNGnextInt (seed +
                (long) (x * x * 0x4c1906) +
                (long) (x * 0x5ac0db) +
                (long) (z * z) * 0x4307a7L +
                (long) (z * 0x5f24f) ^ 0x3ad8025f, 10) == 0;
    }
}