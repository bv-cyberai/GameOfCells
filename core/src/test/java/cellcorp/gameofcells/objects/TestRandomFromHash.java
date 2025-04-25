package cellcorp.gameofcells.objects;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestRandomFromHash {

    @Test
    public void testSameHashSameSeedProducesSameValue() {
        long seed = 123456L;
        RandomFromHash rng1 = new RandomFromHash(seed);
        RandomFromHash rng2 = new RandomFromHash(seed);

        float result1 = rng1.floatFrom(42);
        float result2 = rng2.floatFrom(42);

        assertEquals(result1, result2, 1e-6, "Same hash and seed should produce the same float");
    }

    @Test
    public void testDifferentHashSameSeedProducesDifferentValues() {
        RandomFromHash rng = new RandomFromHash(789L);

        float result1 = rng.floatFrom(1);
        float result2 = rng.floatFrom(2);

        assertNotEquals(result1, result2, "Different hashes should produce different floats");
    }

    @Test
    public void testSameHashDifferentSeedsProducesDifferentValues() {
        RandomFromHash rng1 = new RandomFromHash(100L);
        RandomFromHash rng2 = new RandomFromHash(999L);

        float result1 = rng1.floatFrom(55);
        float result2 = rng2.floatFrom(55);

        assertNotEquals(result1, result2, "Same hash with different seeds should produce different floats");
    }

    @Test
    public void testFloatOutputIsBetweenZeroAndOne() {
        RandomFromHash rng = new RandomFromHash(12345L);

        for (int i = 0; i < 100; i++) {
            float value = rng.floatFrom(i);
            assertTrue(value >= 0.0f && value < 1.0f, "floatFrom should return values in [0.0, 1.0)");
        }
    }

    @Test
    public void testZeroHashStillProducesValidValue() {
        RandomFromHash rng = new RandomFromHash(999L);
        float value = rng.floatFrom(0);
        assertTrue(value >= 0.0f && value < 1.0f, "Hash 0 should still produce valid float");
    }

    @Test
    public void testNegativeHashIsHandledCorrectly() {
        RandomFromHash rng = new RandomFromHash(888L);
        float value = rng.floatFrom(-123);
        assertTrue(value >= 0.0f && value < 1.0f, "Negative hashes should not break float generation");
    }
}
