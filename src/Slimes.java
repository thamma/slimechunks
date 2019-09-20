import java.util.IntSummaryStatistics;

public class Slimes {

    interface Handler {
        void handle(int val, int x, int z);
    }

    public static void main(String... args) {
        long start = System.currentTimeMillis();
        int oldmax = 0;
        int range = (int) Math.sqrt(Integer.MAX_VALUE) / 4;
        int minX = -range;
        int minZ = -range;
        int width = 2 * range;
        int height = 2 * range;
        long myseed = -1231333916804148705L;
        final long[] lastdist = {Long.MAX_VALUE};
        Handler maxHandler = (val, x, z) -> {
            long dist = x * x + z * z;
            if (val != 6)
                return;
            if (dist < lastdist[0]) {
                lastdist[0] = dist;
                System.out.printf("seed: %d   max: %d   at (%d, %d)\n", myseed, val, x * 16, z * 16);
            }
        };
        maxRect(myseed, minX, minZ, width, height, 4, maxHandler);
        System.out.println(System.currentTimeMillis() - start);
    }

    public static void maxFlood(long seed, int minX, int minZ, int width, int height, int threshold, Handler handler) {
        int[] stack = new int[width * height];
        int stackptr = 0; //points to the next free index
        boolean[] visited = new boolean[width * height];
        for (int z = 0; z < height; z++) {
            for (int x = 0; x < width; x++) {
                //check if floodfill can start here
                if (visited[x + z * width])
                    continue;
                if (!(isSC(seed, minX + x, minZ + z))) {
                    visited[x + z * width] = true;
                    continue;
                }
                int size = 0;
                int pos = x + z * width;
                stack[stackptr++] = pos;
                while (stackptr != 0) {
                    int curr = stack[--stackptr];
                    int currX = curr % width;
                    int currZ = curr / width;
                    if (visited[curr])
                        continue;
                    if (!isSC(seed, minX + currX, minZ + currZ)) {
                        visited[curr] = true;
                        continue;
                    }
                    size++;
                    visited[curr] = true;
                    //check if in bounds
                    if (currX < width - 1)
                        stack[stackptr++] = curr + 1;
                    if (currX > 0)
                        stack[stackptr++] = curr - 1;
                    if (currZ < height - 1)
                        stack[stackptr++] = curr + width;
                    if (currZ > 0)
                        stack[stackptr++] = curr - width;
                }
                if (size >= threshold)
                    handler.handle(size, minX + x, minZ + z);
            }
        }
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
}
