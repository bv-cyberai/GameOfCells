package cellcorp.gameofcells;

/**
 * Various static utilities.
 */
public class Util {
    /**
     * Adapted from Wikipedia:
     * <a href="https://en.wikipedia.org/wiki/Smoothstep">smoothstep</a>
     */
    public static float smoothStep(float min, float max, float x) {
        // Scale, and clamp x to 0..1 range
        x = Math.min(Math.max((x - min) / (max - min), 0f), 1f);

        return x * x * (3.0f - 2.0f * x);
    }
}
