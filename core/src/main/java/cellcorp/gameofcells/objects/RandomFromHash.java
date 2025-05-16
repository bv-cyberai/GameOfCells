package cellcorp.gameofcells.objects;

import java.util.Random;

/**
 * RandomFromHash Class
 * <p>
 * Once constructed with an initial seed, produces a random but consistent
 * value for the given hash.
 * @author Brendon Vineyard / vineyabn207
 * @author Andrew Sennoga-Kimuli / sennogat106
 * @author Mark Murphy / murphyml207
 * @author Tim Davey / daveytj206
 * @date 03/05/2025
 * @course CIS 405
 * @assignment GameOfCells
 */

public class RandomFromHash {

    private final long initialSeed;
    private final Random random;

    /**
     * Create a random generator with the given seed.
     */
    public RandomFromHash(long seed) {
        this.initialSeed = seed;
        this.random = new Random();
    }

    /**
     * Gets the value for the given hash.
     * A given `RandomOfHash` will always produce the same `nextInt` for the same `hash`.
     */
    public float floatFrom(int hash) {
        long seed = (long) hash * initialSeed;
        random.setSeed(seed);
        return random.nextFloat();
    }
}
